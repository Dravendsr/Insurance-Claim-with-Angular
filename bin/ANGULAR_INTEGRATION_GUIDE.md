================================================================================
ANGULAR FRONTEND INTEGRATION GUIDE
Health Insurance Claim Management System
================================================================================

This guide explains how to build an Angular frontend that connects with the
Spring Boot backend REST API.


================================================================================
1. OVERVIEW
================================================================================

Backend API Details:
  - Base URL: http://localhost:8080/api
  - Response Format: JSON
  - Authentication: Username/Password with BCrypt
  - CORS Enabled: Yes (all origins allowed in dev)


================================================================================
2. SUGGESTED ANGULAR PAGES
================================================================================

2.1 LOGIN PAGE (Public)
------------------------
Route: /login
Components:
  - Username input field
  - Password input field
  - Login button
  - Error message display

Functionality:
  - Call POST /api/users/login
  - Store user details in browser storage (localStorage)
  - Redirect to dashboard based on role
  - Display error message if login fails

Backend Call Example:
  POST /api/users/login
  Body:
  {
    "username": "admin001",
    "password": "password"
  }
  
  Response:
  {
    "message": "Login successful",
    "status": true,
    "data": {
      "userId": 1,
      "username": "admin001",
      "fullName": "Admin User",
      "role": "ADMIN",
      "accountStatus": "ACTIVE"
    }
  }

Sample Angular Code:
  login(credentials: any) {
    return this.http.post('http://localhost:8080/api/users/login', credentials)
      .subscribe(response => {
        if (response.status) {
          localStorage.setItem('currentUser', JSON.stringify(response.data));
          this.router.navigate(['/dashboard']);
        }
      });
  }


2.2 ADMIN DASHBOARD
-------------------
Route: /admin-dashboard (Guard: Role = ADMIN)
Components:
  - Sidebar navigation
  - Quick statistics cards
  - Recent activities list
  - Main content area

Features:
  - Display total users, policies, claims, hospitals
  - Show recent claims
  - List of pending approvals
  - Quick action buttons

Backend Calls:
  GET /api/users (Total users count)
  GET /api/policies (Total policies count)
  GET /api/claims (Total claims count)
  GET /api/claims/status/PENDING (Pending claims)

Navigation Options:
  - User Management
  - Policy Management
  - Hospital Management
  - Doctor Management
  - View All Claims
  - Payment Processing
  - Settings


2.3 POLICYHOLDER DASHBOARD
--------------------------
Route: /user-dashboard (Guard: Role = POLICYHOLDER)
Components:
  - User profile section
  - Assigned policies list
  - Recent claims list
  - Payment history

Features:
  - Display user's personal information
  - Show all policies assigned to user
  - Show recent claims with status
  - Display payment records
  - Button to "Raise New Claim"

Backend Calls:
  GET /api/users/{userId} (User details)
  GET /api/user-policies/user/{userId} (User's policies)
  GET /api/claims/user/{userId} (User's claims)
  GET /api/payments (User's payments)

Navigation Options:
  - View Profile
  - My Policies
  - Claim History
  - Payment Status
  - Raise New Claim
  - Logout


2.4 APPROVER DASHBOARD
---------------------
Route: /approver-dashboard (Guard: Role = APPROVER)
Components:
  - Pending claims list
  - Claim detail view
  - Approve/Reject form
  - Claims statistics

Features:
  - Show only PENDING claims
  - Display claim amount and treatment details
  - Show treatment verification details
  - List documents
  - Form to approve/reject with comments

Backend Calls:
  GET /api/claims/status/PENDING (Pending claims)
  GET /api/claims/{claimId} (Claim details)
  GET /api/documents?claimId={claimId} (Document list)
  PUT /api/claims/{claimId}/approve (Approve claim)
  PUT /api/claims/{claimId}/reject (Reject claim)

Backend URLs to Call:
  PUT /api/claims/{claimId}/approve
  Body:
  {
    "approvedAmount": 45000,
    "approverComment": "Approved with deductible"
  }
  
  PUT /api/claims/{claimId}/reject
  Body:
  {
    "rejectionReason": "Treatment not covered"
  }


2.5 CLAIM REQUEST FORM
---------------------
Route: /raise-claim (Guard: Role = POLICYHOLDER)
Components:
  - Treatment selection dropdown
  - Claim amount input
  - Insurance policy selection
  - Submit button
  - Success/Error message

Features:
  - Let user select from their treatments
  - Auto-populate claim amount from treatment
  - Select from active policies
  - Validate policy is not expired
  - Submit claim form

Backend Calls:
  GET /api/treatments/user/{userId} (List user's treatments)
  GET /api/user-policies/user/{userId} (List user's policies)
  POST /api/claims (Create new claim)

Sample Request:
  POST /api/claims
  Body:
  {
    "claimNumber": "CLM011",
    "claimAmount": 50000,
    "claimStatus": "PENDING",
    "claimDate": "2024-10-20",
    "user": {"userId": 2},
    "treatment": {
      "diagnosis": "Knee Sprain",
      "treatmentDescription": "Outpatient consultation and brace fitting",
      "treatmentAmount": 50000,
      "treatmentDate": "2024-10-18",
      "hospitalName": "Eastside Rehab Center",
      "hospitalAddress": "101 Healing Plaza",
      "hospitalPhone": "555-3710",
      "doctorName": "Dr. Aaron Kim",
      "doctorSpecialization": "Orthopedics",
      "doctorQualification": "MD",
      "doctorExperienceYears": 9
    },
    "insurancePolicy": {"policyId": 1}
  }


2.6 CLAIM STATUS PAGE
--------------------
Route: /claim-status/{claimId}
Components:
  - Claim status timeline
  - Claim details panel
  - Treatment information
  - Documents section
  - Payment status

Features:
  - Show claim progress: PENDING → APPROVED/REJECTED → SETTLED
  - Display all claim details
  - Show uploaded documents
  - Show treatment specifications
  - Display payment info if applicable

Backend Calls:
  GET /api/claims/{claimId} (Claim details)
  GET /api/documents?claimId={claimId} (Documents)
  GET /api/payments/claim/{claimId} (Payment info)


2.7 USER MANAGEMENT PAGE (Admin Only)
------------------------------------
Route: /admin/users
Components:
  - User list table
  - Create user form
  - Edit user form
  - Delete confirmation dialog

Features:
  - Display all users in table format
  - Filter users by role
  - Search users by name/email
  - Create new user
  - Edit existing user
  - Deactivate/Delete user

Backend Calls:
  GET /api/users (Get all users)
  GET /api/users/role/{role} (Filter by role)
  POST /api/users (Create user)
  PUT /api/users/{userId} (Update user)
  DELETE /api/users/{userId} (Delete user)

Sample Create User:
  POST /api/users
  Body:
  {
    "username": "newuser",
    "password": "password123",
    "fullName": "New User",
    "email": "newuser@email.com",
    "phoneNumber": "9876543210",
    "dateOfBirth": "1995-05-15",
    "address": "123 Street",
    "role": "POLICYHOLDER",
    "accountStatus": "ACTIVE",
    "bankAccountNumber": "123456789",
    "ifscCode": "ICIC0000001",
    "bankName": "ICICI Bank"
  }


2.8 POLICY MANAGEMENT PAGE (Admin Only)
--------------------------------------
Route: /admin/policies
Components:
  - Policy list table
  - Create policy form
  - Edit policy form
  - Assign policy to user form

Features:
  - Display all policies
  - Create new policy
  - Update policy details
  - Activate/Deactivate policies
  - Assign policies to users
  - View policy holders

Backend Calls:
  GET /api/policies (Get all policies)
  POST /api/policies (Create policy)
  PUT /api/policies/{policyId} (Update policy)
  POST /api/user-policies (Assign policy to user)

Sample Create Policy:
  POST /api/policies
  Body:
  {
    "policyNumber": "POL006",
    "policyName": "New Health Plan",
    "policyType": "Individual",
    "coverageAmount": 600000,
    "premiumAmount": 20000,
    "benefits": "Full coverage",
    "policyStatus": "ACTIVE",
    "startDate": "2024-01-01",
    "endDate": "2025-12-31"
  }


================================================================================
3. SUGGESTED ANGULAR SERVICES AND API CALLS
================================================================================

3.1 Authentication Service
---------------------------
Class: AuthService
Methods:
  - login(username: string, password: string): Observable<any>
  - logout(): void
  - getCurrentUser(): any
  - isAuthenticated(): boolean
  - hasRole(role: string): boolean
  - isTokenExpired(): boolean

Implementation:
  export class AuthService {
    constructor(private http: HttpClient) {}
    
    login(username: string, password: string) {
      return this.http.post<any>('http://localhost:8080/api/users/login', {
        username: username,
        password: password
      });
    }
    
    logout() {
      localStorage.removeItem('currentUser');
    }
    
    getCurrentUser() {
      return JSON.parse(localStorage.getItem('currentUser') || '{}');
    }
    
    isAuthenticated(): boolean {
      return !!localStorage.getItem('currentUser');
    }
    
    hasRole(role: string): boolean {
      const user = this.getCurrentUser();
      return user.role === role;
    }
  }


3.2 User Service
-----------------
Class: UserService
Methods:
  - getAllUsers(): Observable<any>
  - getUserById(id: number): Observable<any>
  - getUsersByRole(role: string): Observable<any>
  - createUser(user: any): Observable<any>
  - updateUser(id: number, user: any): Observable<any>
  - deleteUser(id: number): Observable<any>

Implementation:
  export class UserService {
    private apiUrl = 'http://localhost:8080/api/users';
    
    constructor(private http: HttpClient) {}
    
    getAllUsers() {
      return this.http.get<any>(this.apiUrl);
    }
    
    createUser(user: any) {
      return this.http.post<any>(this.apiUrl, user);
    }
    
    updateUser(id: number, user: any) {
      return this.http.put<any>(`${this.apiUrl}/${id}`, user);
    }
    
    deleteUser(id: number) {
      return this.http.delete<any>(`${this.apiUrl}/${id}`);
    }
  }


3.3 Policy Service
-------------------
Class: PolicyService
Methods:
  - getAllPolicies(): Observable<any>
  - getPolicyById(id: number): Observable<any>
  - createPolicy(policy: any): Observable<any>
  - updatePolicy(id: number, policy: any): Observable<any>
  - deletePolicy(id: number): Observable<any>
  - assignPolicyToUser(userPolicy: any): Observable<any>

Implementation:
  export class PolicyService {
    private apiUrl = 'http://localhost:8080/api/policies';
    
    constructor(private http: HttpClient) {}
    
    getAllPolicies() {
      return this.http.get<any>(this.apiUrl);
    }
    
    createPolicy(policy: any) {
      return this.http.post<any>(this.apiUrl, policy);
    }
  }


3.4 Claim Service
------------------
Class: ClaimService
Methods:
  - getAllClaims(): Observable<any>
  - getClaimById(id: number): Observable<any>
  - getClaimsByUser(userId: number): Observable<any>
  - getClaimsByStatus(status: string): Observable<any>
  - createClaim(claim: any): Observable<any>
  - approveClaim(id: number, data: any): Observable<any>
  - rejectClaim(id: number, reason: string): Observable<any>

Implementation:
  export class ClaimService {
    private apiUrl = 'http://localhost:8080/api/claims';
    
    constructor(private http: HttpClient) {}
    
    getClaimsByUser(userId: number) {
      return this.http.get<any>(`${this.apiUrl}/user/${userId}`);
    }
    
    createClaim(claim: any) {
      return this.http.post<any>(this.apiUrl, claim);
    }
    
    approveClaim(id: number, data: any) {
      return this.http.put<any>(`${this.apiUrl}/${id}/approve`, data);
    }
    
    rejectClaim(id: number, reason: string) {
      return this.http.put<any>(`${this.apiUrl}/${id}/reject`, {
        rejectionReason: reason
      });
    }
  }


3.5 Payment Service
--------------------
Class: PaymentService
Methods:
  - getAllPayments(): Observable<any>
  - getPaymentById(id: number): Observable<any>
  - getPaymentsByStatus(status: string): Observable<any>
  - createPayment(payment: any): Observable<any>
  - processPayment(id: number): Observable<any>

Implementation:
  export class PaymentService {
    private apiUrl = 'http://localhost:8080/api/payments';
    
    constructor(private http: HttpClient) {}
    
    processPayment(id: number) {
      return this.http.put<any>(`${this.apiUrl}/${id}/process`, {});
    }
  }


3.6 Hospital Service
---------------------
Methods:
  - getAllHospitals(): Observable<any>
  - createHospital(hospital: any): Observable<any>


3.7 Doctor Service
-------------------
Methods:
  - getAllDoctors(): Observable<any>
  - createDoctor(doctor: any): Observable<any>


3.8 Treatment Service
----------------------
Methods:
  - getTreatmentsByUser(userId: number): Observable<any>
  - createTreatment(treatment: any): Observable<any>


3.9 Document Service
---------------------
Methods:
  - uploadDocument(document: any): Observable<any>
  - getDocuments(claimId: number): Observable<any>


================================================================================
4. ROUTE GUARDS
================================================================================

4.1 Authentication Guard
------------------------
Protects: All authenticated routes
Checks: User is logged in

export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}
  
  canActivate(route: ActivatedRouteSnapshot): boolean {
    if (this.authService.isAuthenticated()) {
      return true;
    } else {
      this.router.navigate(['/login']);
      return false;
    }
  }
}


4.2 Role Guard
---------------
Protects: Role-specific routes
Checks: User has required role

export class RoleGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}
  
  canActivate(route: ActivatedRouteSnapshot): boolean {
    const requiredRole = route.data['role'];
    const user = this.authService.getCurrentUser();
    
    if (user && user.role === requiredRole) {
      return true;
    } else {
      this.router.navigate(['/unauthorized']);
      return false;
    }
  }
}


4.3 Route Configuration
------------------------
const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { 
    path: 'admin-dashboard', 
    component: AdminDashboardComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'ADMIN' }
  },
  {
    path: 'user-dashboard',
    component: UserDashboardComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'POLICYHOLDER' }
  },
  {
    path: 'approver-dashboard',
    component: ApproverDashboardComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'APPROVER' }
  }
];


================================================================================
5. HTTP INTERCEPTOR FOR ERROR HANDLING
================================================================================

Create an HTTP Interceptor to handle errors globally:

export class HttpErrorInterceptor implements HttpInterceptor {
  constructor(private router: Router) {}
  
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          // Unauthorized - redirect to login
          this.router.navigate(['/login']);
        } else if (error.status === 403) {
          // Forbidden - no permission
          this.router.navigate(['/unauthorized']);
        } else if (error.status === 500) {
          // Server error
          console.error('Server error:', error.message);
        }
        return throwError(() => error);
      })
    );
  }
}


================================================================================
6. COMPONENTS STRUCTURE
================================================================================

Login Component:
  - Form with username and password
  - Login button
  - Error message display

Admin Dashboard:
  - Sidebar with navigation menu
  - Main content area
  - Statistics cards
  - Recent activities list

User Dashboard:
  - Profile section
  - My Policies section
  - My Claims section
  - Payment History section

Approver Dashboard:
  - Pending Claims list
  - Claim details view
  - Approve/Reject form

Claim Request Form:
  - Treatment selection
  - Claim amount input
  - Policy selection
  - Submit button

User Management:
  - User table with CRUD operations
  - Create user form
  - Edit user form

Policy Management:
  - Policy table with CRUD operations
  - Create policy form
  - Assign policy to user form


================================================================================
7. FORMS AND VALIDATION
================================================================================

Login Form:
  - Username: Required, min 3 characters
  - Password: Required, min 6 characters

Create User Form:
  - Username: Required, unique, min 3 characters
  - Password: Required, min 6 characters
  - Full Name: Required
  - Email: Required, valid email format
  - Phone Number: Required
  - Date of Birth: Required
  - Address: Required
  - Role: Required (dropdown)
  - Account Status: Required (dropdown)

Create Claim Form:
  - Treatment: Required (dropdown)
  - Insurance Policy: Required (dropdown, only active/non-expired)
  - Claim Amount: Auto-filled from treatment
  - Submit: Only after validation

Approve/Reject Claim:
  - Approved Amount: Required (for approve)
  - Approver Comment: Required (if amount reduced)
  - Rejection Reason: Required (for reject)


================================================================================
8. DATA MODELS (TypeScript Interfaces)
================================================================================

export interface User {
  userId: number;
  username: string;
  password?: string;
  fullName: string;
  email: string;
  phoneNumber: string;
  dateOfBirth: Date;
  address: string;
  role: 'ADMIN' | 'APPROVER' | 'POLICYHOLDER';
  accountStatus: 'ACTIVE' | 'INACTIVE';
  bankAccountNumber?: string;
  ifscCode?: string;
  bankName?: string;
}

export interface InsurancePolicy {
  policyId: number;
  policyNumber: string;
  policyName: string;
  policyType: string;
  coverageAmount: number;
  premiumAmount: number;
  benefits: string;
  policyStatus: 'ACTIVE' | 'INACTIVE';
  startDate: Date;
  endDate: Date;
}

export interface Claim {
  claimId: number;
  claimNumber: string;
  claimAmount: number;
  approvedAmount: number;
  claimStatus: 'PENDING' | 'APPROVED' | 'REJECTED' | 'SETTLED';
  approverComment?: string;
  rejectionReason?: string;
  claimDate: Date;
  approvedDate?: Date;
  user: User;
  treatment: any;
  insurancePolicy: InsurancePolicy;
  assignedApprover?: User;
}

export interface Treatment {
  treatmentId: number;
  diagnosis: string;
  treatmentDescription: string;
  treatmentAmount: number;
  treatmentDate: Date;
  user: User;
  doctor: any;
  hospital: any;
}

export interface Payment {
  paymentId: number;
  paymentAmount: number;
  paymentDate: Date;
  paymentMode: string;
  transactionId?: string;
  paymentStatus: 'PENDING' | 'COMPLETED' | 'FAILED';
  companyAccountNumber: string;
  companyBankName: string;
  claim: Claim;
  user: User;
}


================================================================================
9. ANGULAR HTTP CLIENT SETUP
================================================================================

In app.module.ts:
  import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
  import { HttpErrorInterceptor } from './services/http-error.interceptor';
  
  @NgModule({
    imports: [HttpClientModule],
    providers: [
      {
        provide: HTTP_INTERCEPTORS,
        useClass: HttpErrorInterceptor,
        multi: true
      }
    ]
  })
  export class AppModule { }

CORS Configuration:
  - Backend has @CrossOrigin(origins = "*")
  - No additional CORS headers needed in Angular for development
  - In production, verify backend CORS configuration


================================================================================
10. ERROR HANDLING AND USER FEEDBACK
================================================================================

Display Error Messages:
  - Login failure: Show "Invalid credentials"
  - Network error: Show "Unable to connect to server"
  - Validation error: Show specific field errors
  - Server error (500): Show "Server error, please try again"

Success Messages:
  - Claim created: "Claim raised successfully"
  - Claim approved: "Claim approved successfully"
  - Payment processed: "Payment processed successfully"

Loading States:
  - Show loading spinner during API calls
  - Disable buttons during submission
  - Show progress indicators


================================================================================
11. LOCAL STORAGE USAGE
================================================================================

Store:
  - Current user: localStorage.setItem('currentUser', JSON.stringify(user))
  - Authentication token: Not needed (no JWT)
  - User preferences: Optional

Retrieve:
  - Get current user: JSON.parse(localStorage.getItem('currentUser'))
  - Check if user logged in: !!localStorage.getItem('currentUser')

Clear:
  - On logout: localStorage.removeItem('currentUser')
  - On session timeout: localStorage.clear()


================================================================================
12. RESPONSIVE DESIGN RECOMMENDATIONS
================================================================================

Breakpoints:
  - Mobile: <576px
  - Tablet: 576px - 992px
  - Desktop: >992px

Use Bootstrap or Material Design:
  - Bootstrap 5 for responsive grid
  - Material Design Angular components
  - Responsive tables for mobile
  - Collapsible sidebar on mobile


================================================================================
13. TESTING RECOMMENDATIONS
================================================================================

Unit Tests:
  - Test services with mock HTTP
  - Test component logic
  - Test form validation

Integration Tests:
  - Test with actual backend API
  - Test complete user flows
  - Test error scenarios

E2E Tests:
  - Test login flow
  - Test claim creation flow
  - Test approval flow

Testing Tools:
  - Jasmine for unit testing
  - Karma for test runner
  - Protractor for E2E testing


================================================================================
14. DEPLOYMENT NOTES
================================================================================

Base URL Configuration:
  - Development: http://localhost:8080/api
  - Production: https://yourdomain.com/api
  - Use environment files for different configurations

environment.ts (Development):
  export const environment = {
    apiUrl: 'http://localhost:8080/api'
  };

environment.prod.ts (Production):
  export const environment = {
    apiUrl: 'https://yourdomain.com/api'
  };

Build and Deploy:
  npm run build --prod
  Deploy dist/ folder to web server


================================================================================
15. SUGGESTED TECH STACK
================================================================================

Frontend Framework: Angular 17+
UI Framework: Bootstrap 5 or Angular Material
HTTP Client: HttpClient (built-in)
State Management: NgRx (optional, for complex state)
Form Validation: Reactive Forms or Template-driven Forms
Routing: Angular Router with Guards
Testing: Jasmine & Karma

Node.js Version: 18 LTS or higher
npm: 9+
Angular CLI: 17+


================================================================================
16. COMPARISON: BACKEND TO FRONTEND MAPPING
================================================================================

Backend Endpoint | Frontend Service | Component | Data Flow
/api/users/login | AuthService.login() | LoginComponent | Credentials → Auth
/api/users | UserService.getAllUsers() | UserManagementComponent | List Users
/api/policies | PolicyService.getAllPolicies() | PolicyManagementComponent | List Policies
/api/claims | ClaimService.getClaimsByUser() | ClaimStatusComponent | Show Claims
/api/claims/{id}/approve | ClaimService.approveClaim() | ApproverComponent | Approve
/api/payments | PaymentService.getAllPayments() | PaymentComponent | Process Payment


================================================================================
17. QUICK START CHECKLIST FOR ANGULAR FRONTEND
================================================================================

□ Create new Angular project: ng new hicms-frontend
□ Install Bootstrap or Material: npm install bootstrap
□ Create interceptor for error handling
□ Create auth guard for route protection
□ Create all services (Auth, User, Policy, Claim, etc.)
□ Create login component
□ Create dashboard components (Admin, User, Approver)
□ Create forms for CRUD operations
□ Add table components for data display
□ Implement error handling and loading states
□ Add responsive design
□ Test with Postman collection
□ Test all user flows
□ Deploy to production


================================================================================
END OF ANGULAR INTEGRATION GUIDE
================================================================================

Questions or Issues?
- Verify backend is running on http://localhost:8080
- Check CORS configuration in application.properties
- Verify Angular is running on http://localhost:4200
- Check browser console for errors
- Test API endpoints with Postman first
