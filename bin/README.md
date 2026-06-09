# Health Insurance Claim Management System

## Project Overview

The Health Insurance Claim Management System is a backend application built with **Spring Boot 4** and **Java 17** that manages insurance claims, policies, users, and payment processing. The system supports three user roles: **POLICYHOLDER**, **APPROVER**, and **ADMIN**, each with specific functionalities.

### Key Features

- **User Management**: Create and manage users with role-based access
- **Policy Management**: Manage insurance policies and assign them to users
- **Claim Management**: Raise and track insurance claims
- **Approval Workflow**: Approvers can verify and approve/reject claims
- **Payment Processing**: Process payments for approved claims
- **Document Management**: Upload and manage supporting documents
- **Hospital & Doctor Management**: Manage healthcare providers

## Technologies Used

- **Language**: Java 17
- **Framework**: Spring Boot 4.0.6
- **Database**: MySQL 8.0+
- **Build Tool**: Maven
- **ORM**: Jakarta Persistence (JPA/Hibernate)
- **Password Encoding**: BCrypt
- **API Format**: REST API with JSON responses
- **Authentication**: Simple username/password with BCrypt hashing

## Project Structure

```
health-insurance-claim-management/
├── src/main/java/com/insurance/claimmanagement/
│   ├── entity/              # JPA Entity classes
│   │   ├── User.java
│   │   ├── InsurancePolicy.java
│   │   ├── UserPolicy.java
│   │   ├── Hospital.java
│   │   ├── Doctor.java
│   │   ├── Treatment.java
│   │   ├── Claim.java
│   │   ├── Document.java
│   │   └── Payment.java
│   ├── repository/          # JPA Repository interfaces
│   ├── service/             # Business logic layer
│   │   ├── *Service.java (Interfaces)
│   │   └── *ServiceImpl.java (Implementations)
│   └── controller/          # REST API Controllers
├── src/main/resources/
│   └── application.properties
├── pom.xml
├── health_db_initialization.sql
└── README.md
```

## Database Schema

The system uses 9 main tables:

1. **users**: User accounts with roles (ADMIN, APPROVER, POLICYHOLDER)
2. **insurance_policies**: Insurance policy information
3. **user_policies**: Join table linking users to policies
4. **hospitals**: Hospital information
5. **doctors**: Doctor information linked to hospitals
6. **treatments**: Treatment records linked to users, doctors, and hospitals
7. **claims**: Insurance claims linked to users, treatments, and policies
8. **documents**: Supporting documents uploaded for claims
9. **payments**: Payment records for approved claims

## User Roles

### ADMIN
- Create, update, and delete users
- Manage insurance policies
- Manage hospitals and doctors
- View all claims
- Process reimbursement payments
- Activate/deactivate policies

### APPROVER
- Verify and validate claims
- Review treatment details
- Examine hospital details
- Verify uploaded documents
- Approve or reject claims
- Reduce approved amounts if necessary
- Add mandatory comments

### POLICYHOLDER
- Login and view personal details
- View assigned policies
- Raise insurance claims
- Upload supporting documents
- Track claim status
- View payment details

## API Endpoints

### Authentication
- `POST /api/users/login` - Login user
- Login response returns only `message`, `status`, and `role` fields (no full user object).

### User Management
- `GET /api/users` - Get all users (ADMIN)
- `GET /api/users/{userId}` - Get user by ID
- `GET /api/users/role/{role}` - Get users by role
- `POST /api/users` - Create new user (ADMIN)
- `PUT /api/users/{userId}` - Update user
- `DELETE /api/users/{userId}` - Delete user (ADMIN)

### Policy Management
- `GET /api/policies` - Get all policies
- `GET /api/policies/{policyId}` - Get policy by ID
- `POST /api/policies` - Create policy (ADMIN)
- `PUT /api/policies/{policyId}` - Update policy (ADMIN)
- `DELETE /api/policies/{policyId}` - Delete policy (ADMIN)

### User Policies
- `GET /api/user-policies` - Get all user policies
- `GET /api/user-policies/user/{userId}` - Get policies assigned to user
- `GET /api/user-policies/{userPolicyId}` - Get specific user policy
- `POST /api/user-policies` - Assign policy to user (ADMIN)
- `PUT /api/user-policies/{userPolicyId}` - Update user policy

### Hospital Management
- `GET /api/hospitals` - Get all hospitals
- `GET /api/hospitals/{hospitalId}` - Get hospital by ID
- `POST /api/hospitals` - Create hospital (ADMIN)
- `PUT /api/hospitals/{hospitalId}` - Update hospital (ADMIN)
- `DELETE /api/hospitals/{hospitalId}` - Delete hospital (ADMIN)

### Doctor Management
- `GET /api/doctors` - Get all doctors
- `GET /api/doctors/{doctorId}` - Get doctor by ID
- `POST /api/doctors` - Create doctor (ADMIN)
- `PUT /api/doctors/{doctorId}` - Update doctor (ADMIN)
- `DELETE /api/doctors/{doctorId}` - Delete doctor (ADMIN)

### Treatment Management
- `GET /api/treatments` - Get all treatments
- `GET /api/treatments/{treatmentId}` - Get treatment by ID
- `POST /api/treatments` - Create treatment record
- `PUT /api/treatments/{treatmentId}` - Update treatment
- `DELETE /api/treatments/{treatmentId}` - Delete treatment

### Claim Management
- `GET /api/claims` - Get all claims (ADMIN)
- `GET /api/claims/{claimId}` - Get claim by ID
- `GET /api/claims/user/{userId}` - Get user's claims
- `GET /api/claims/status/{status}` - Get claims by status
- `POST /api/claims` - Raise new claim (POLICYHOLDER)
- `PUT /api/claims/{claimId}/approve` - Approve claim (APPROVER)
- `PUT /api/claims/{claimId}/reject` - Reject claim (APPROVER)
- `DELETE /api/claims/{claimId}` - Delete claim

### Document Management
- `GET /api/documents` - Get all documents
- `GET /api/documents/{documentId}` - Get document by ID
- `POST /api/documents` - Upload document
- `PUT /api/documents/{documentId}` - Update document
- `DELETE /api/documents/{documentId}` - Delete document

### Payment Management
- `GET /api/payments` - Get all payments
- `GET /api/payments/{paymentId}` - Get payment by ID
- `GET /api/payments/status/{status}` - Get payments by status
- `POST /api/payments` - Create payment record
- `PUT /api/payments/{paymentId}/process` - Process payment
- `DELETE /api/payments/{paymentId}` - Delete payment

## Database Setup

### Step 1: Install MySQL (if not already installed)
```bash
# Windows - Use MySQL installer
# Linux - sudo apt-get install mysql-server
# macOS - brew install mysql@8.0
```

### Step 2: Start MySQL Service
```bash
# Windows - MySQL should auto-start or use MySQL Services
# Linux - sudo systemctl start mysql
# macOS - brew services start mysql@8.0
```

### Step 3: Open MySQL and Run SQL Script
```bash
mysql -u root -p < health_db_initialization.sql
```

Or use MySQL Workbench:
1. Open MySQL Workbench
2. Connect to your MySQL server
3. Create a new SQL Tab
4. Copy and paste contents of `health_db_initialization.sql`
5. Execute the script

### Database Details
- **Database Name**: `health_db` (auto-created if it doesn't exist)
- **Default User**: root
- **Default Password**: root

## How to Run the Project

### Prerequisites
- Java 17 or higher installed
- MySQL 8.0 or higher installed and running
- Maven 3.6 or higher
- VS Code or any IDE

### Steps to Run

1. **Clone or download the project**
   ```bash
   cd health-insurance-claim-management
   ```

2. **Configure Database Connection** (if needed)
   - Edit `src/main/resources/application.properties`
   - Update `spring.datasource.username` and `spring.datasource.password` if different from defaults

3. **Build the project**
   ```bash
   mvn clean build
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```
   
   Or run the main class:
   - `HealthInsuranceClaimManagementApplication.java`

5. **Verify Application Started**
   - Check console for: "Started HealthInsuranceClaimManagementApplication"
   - Application runs on: `http://localhost:8080`

## How to Test Using Postman

### Step 1: Import Postman Collection
1. Open Postman
2. Click "Import" button
3. Select `HICMS_Postman_Collection.json`
4. Collection will be imported with all API endpoints

### Step 2: Test Login First
1. Go to `Users > Login` request
2. Send request with:
   ```json
   {
      "username": "admin001",
      "password": "password"
   }
   ```
3. You should get a successful response

### Step 3: Create Users (Admin Only)
1. Use `Users > Create User` request
2. Provide user details in request body
3. Send the request

### Step 4: Create Policy (Admin Only)
1. Use `Policies > Create Policy` request
2. Provide policy details
3. Send the request

### Step 5: Test Other Endpoints
1. All endpoints follow the same structure
2. Check Postman collection for request/response examples
3. Pass appropriate parameters

### Available Test Users (Password: password)
- **Admin**: admin001 (ADMIN role)
- **Approver**: approver001, approver002 (APPROVER role)
 - **Policyholders**: rajesh17, priya92, amit88, neha91, vikrant85, divya93, karthik87, anjali89, harsh91, manisha86, sanjay90, ritika94 (POLICYHOLDER role)

## Project Workflow

### Claim Process Workflow

1. **Policyholder raises claim**
   - Login as POLICYHOLDER
   - Create a treatment record
   - Raise a claim linked to treatment and policy
   - Upload supporting documents

2. **Approver reviews claim**
   - Login as APPROVER
   - View pending claims
   - Verify treatment, hospital, doctor details
   - Review uploaded documents

3. **Approver makes decision**
   - Approve claim with approved amount
   - Reduced amount requires comment (mandatory)
   - Reject claim with rejection reason (mandatory)

4. **Admin processes payment**
   - Login as ADMIN
   - View approved claims
   - Create payment record
   - Process payment (updates claim to SETTLED)

## Role Explanation

### POLICYHOLDER (12 users in system)
- **Purpose**: Individual or family purchasing insurance
- **Responsibilities**: 
  - Pay premiums
  - Raise claims when needed
  - Provide supporting documents
  - Track claim status
- **Access**: Own claims and policies only
- **Cannot**: Approve claims, manage users, or manage policies

### APPROVER (2 users in system)
- **Purpose**: Validate and approve/reject claims
- **Responsibilities**:
  - Review claim details
  - Verify treatment and hospital information
  - Check supporting documents
  - Make approval/rejection decision
  - Request additional information if needed
- **Access**: All pending and assigned claims
- **Cannot**: Create users, manage policies, or process payments

### ADMIN (1 user in system)
- **Purpose**: System administrator
- **Responsibilities**:
  - Create and manage users
  - Create and manage policies
  - Manage hospitals and doctors
  - Assign claims to approvers
  - Process payments
  - Monitor system operations
- **Access**: All data in system
- **Cannot**: None - has full access

## Response Format

### Success Response
```json
{
  "message": "Operation successful",
  "status": true,
  "data": { }
}
```

### Error Response
```json
{
  "message": "Error description",
  "status": false
}
```

## Business Rules

1. **Policy Expiry**: Users cannot raise claims if policy is expired
2. **Reduced Amount**: If approved amount < claim amount, approver comment is mandatory
3. **Claim Rejection**: Rejection requires mandatory rejection reason
4. **Initial Status**: New claims start with status "PENDING"
5. **Payment Processing**: Payments allowed only for "APPROVED" claims
6. **Claim Settlement**: After successful payment, claim status updates to "SETTLED"
7. **Account Status**: Inactive accounts cannot login
8. **Password Security**: All passwords are BCrypt hashed
9. **Last Login**: Automatically updated on successful login

## Important Files

- `pom.xml` - Maven dependencies
- `application.properties` - Application configuration
- `health_db_initialization.sql` - Database initialization script
- `HealthInsuranceClaimManagementApplication.java` - Main Spring Boot application class
- `HICMS_Postman_Collection.json` - Postman API collection

## Dependencies Used

- **spring-boot-starter-data-jpa**: For database operations
- **spring-boot-starter-webmvc**: For REST API support
- **spring-boot-starter-validation**: For input validation
- **mysql-connector-j**: MySQL database driver
- **spring-security-crypto**: For BCrypt password encoding
- **jackson-databind**: Built-in for JSON serialization

## Common Issues and Solutions

### Database Connection Failed
- **Issue**: Cannot connect to MySQL
- **Solution**: 
  - Ensure MySQL is running: `mysql -u root -p`
  - Check credentials in `application.properties`
  - Run `health_db_initialization.sql` to create database

### Port Already In Use
- **Issue**: Port 8080 already in use
- **Solution**: 
  - Add to `application.properties`: `server.port=8081`
  - Or stop the service using port 8080

### Hibernate DDL-Auto Issues
- **Issue**: Tables not creating automatically
- **Solution**: 
  - Ensure `spring.jpa.hibernate.ddl-auto=update` in properties
  - Or run SQL script manually

## Deployment Notes

- Application is ready for deployment on any server running Java 17
- Configure database connection strings for production server
- Use environment variables for sensitive data
- Implement API rate limiting and security headers in production
- Set up proper logging and monitoring

## Testing Checklist

- [ ] Test login with valid credentials
- [ ] Test login with invalid credentials
- [ ] Create a new user (Admin)
- [ ] Create a new policy
- [ ] Assign policy to user
- [ ] Raise a claim
- [ ] Upload documents to claim
- [ ] Approve a claim with reduced amount
- [ ] Reject a claim with reason
- [ ] Process payment for approved claim
- [ ] Verify claim status updates to SETTLED after payment
- [ ] Test INACTIVE account (should fail login)
- [ ] Test expired policy (should restrict claims)

## Future Enhancements

- Implement Angular frontend
- Add email notifications
- Implement advanced reporting
- Add document scanning/OCR
- Implement two-factor authentication
- Add dashboard for analytics
- Implement audit logging
- Add claim appeals process

## Support and Contact

For issues or questions regarding the system:
1. Check the logs in console
2. Verify database connection
3. Review REST API documentation
4. Check Postman collection for API examples

---

**Version**: 1.0.0  
**Last Updated**: 2024  
**Authors**: Development Team  
**License**: Internal Use Only
