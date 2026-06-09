POSTMAN MANUAL TESTING DOCUMENTATION
Health Insurance Claim Management System Backend

NOTE: Use http://localhost:8080 for all API endpoints.

SECURITY NOTES:
- Existing login remains `POST /api/users/login`.
- Protected endpoints use Spring Security with database-backed authentication.
- CSRF is disabled to allow easy Postman testing.
- Use `Basic Auth` in Postman for protected calls with the same username/password.
- Role-based access is enforced by the application for ADMIN, APPROVER, and POLICYHOLDER.

====================================================
API NAME:
Login

====================================================
API NAME:
Login

ROLE:
ADMIN / APPROVER / POLICYHOLDER

METHOD:
POST

URL:
http://localhost:8080/api/users/login

HEADERS:
Content-Type: application/json

# For login, no Authorization header is required.
# For protected API requests, add Basic Auth in Postman.
# Example: Authorization: Basic YWRtaW4wMDE6cGFzc3dvcmQ=

REQUEST:
{
  "username": "admin001",
  "password": "password"
}

SUCCESS RESPONSE:
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

PURPOSE:
Used for user login authentication and verifying credentials.

====================================================
API NAME:
Get All Users

ROLE:
ADMIN

METHOD:
GET

URL:
http://localhost:8080/api/users

HEADERS:
Content-Type: application/json

REQUEST:
No request body required.

SUCCESS RESPONSE:
{
  "message": "Users retrieved successfully",
  "status": true,
  "data": [
    {
      "userId": 1,
      "username": "admin001",
      "fullName": "Admin User",
      "email": "admin@example.com",
      "role": "ADMIN",
      "accountStatus": "ACTIVE"
    },
    {
      "userId": 2,
      "username": "approver001",
      "fullName": "Approver One",
      "email": "approver1@example.com",
      "role": "APPROVER",
      "accountStatus": "ACTIVE"
    }
  ]
}

PURPOSE:
Used by the admin to list all user accounts in the system.

====================================================
API NAME:
Get User By ID

ROLE:
ADMIN / POLICYHOLDER / APPROVER

METHOD:
GET

URL:
http://localhost:8080/api/users/{userId}

HEADERS:
Content-Type: application/json

REQUEST:
No request body required.

SUCCESS RESPONSE:
{
  "message": "User retrieved successfully",
  "status": true,
  "data": {
    "userId": 2,
    "username": "approver001",
    "fullName": "Approver One",
    "email": "approver1@example.com",
    "role": "APPROVER",
    "accountStatus": "ACTIVE"
  }
}

PURPOSE:
Used to retrieve the profile details for a specific user.

====================================================
API NAME:
Create User

ROLE:
ADMIN

METHOD:
POST

URL:
http://localhost:8080/api/users

HEADERS:
Content-Type: application/json

REQUEST:
{
  "username": "user013",
  "password": "password",
  "fullName": "Policyholder Thirteen",
  "email": "user013@example.com",
  "phoneNumber": "9988776655",
  "dateOfBirth": "1992-09-10",
  "address": "18 New Street, City",
  "role": "POLICYHOLDER",
  "accountStatus": "ACTIVE",
  "bankAccountNumber": "1234509876",
  "ifscCode": "HDFC0001234",
  "bankName": "HDFC Bank"
}

SUCCESS RESPONSE:
{
  "message": "User created successfully",
  "status": true,
  "data": {
    "userId": 13,
    "username": "user013",
    "role": "POLICYHOLDER"
  }
}

PURPOSE:
Used by the admin to add a new user to the system.

====================================================
API NAME:
Update User

ROLE:
ADMIN

METHOD:
PUT

URL:
http://localhost:8080/api/users/{userId}

HEADERS:
Content-Type: application/json

REQUEST:
{
  "fullName": "Policyholder Thirteen Updated",
  "email": "user013@example.com",
  "phoneNumber": "9988776655",
  "address": "18 New Street, Updated City",
  "accountStatus": "ACTIVE"
}

SUCCESS RESPONSE:
{
  "message": "User updated successfully",
  "status": true,
  "data": {
    "userId": 13,
    "username": "user013",
    "fullName": "Policyholder Thirteen Updated"
  }
}

PURPOSE:
Used to update user information and account status.

====================================================
API NAME:
Delete User

ROLE:
ADMIN

METHOD:
DELETE

URL:
http://localhost:8080/api/users/{userId}

HEADERS:
Content-Type: application/json

REQUEST:
No request body required.

SUCCESS RESPONSE:
{
  "message": "User deleted successfully",
  "status": true,
  "data": null
}

PURPOSE:
Used by the admin to remove a user account.

====================================================
API NAME:
Get Users By Role

ROLE:
ADMIN

METHOD:
GET

URL:
http://localhost:8080/api/users/role/{role}

HEADERS:
Content-Type: application/json

REQUEST:
No request body required.

SUCCESS RESPONSE:
{
  "message": "Users retrieved successfully",
  "status": true,
  "data": [
    {
      "userId": 2,
      "username": "approver001",
      "fullName": "Approver One",
      "role": "APPROVER"
    }
  ]
}

PURPOSE:
Used by the admin to filter users by role.

====================================================
API NAME:
Get All Policies

ROLE:
ADMIN / POLICYHOLDER / APPROVER

METHOD:
GET

URL:
http://localhost:8080/api/policies

HEADERS:
Content-Type: application/json

REQUEST:
No request body required.

SUCCESS RESPONSE:
{
  "message": "Policies retrieved successfully",
  "status": true,
  "data": [
    {
      "policyId": 1,
      "policyNumber": "POL001",
      "policyName": "Silver Health Plan",
      "policyStatus": "ACTIVE"
    },
    {
      "policyId": 2,
      "policyNumber": "POL002",
      "policyName": "Gold Health Plan",
      "policyStatus": "ACTIVE"
    }
  ]
}

PURPOSE:
Used to list all insurance policies available in the system.

====================================================
API NAME:
Get Policy By ID

ROLE:
ADMIN / POLICYHOLDER / APPROVER

METHOD:
GET

URL:
http://localhost:8080/api/policies/{policyId}

HEADERS:
Content-Type: application/json

REQUEST:
No request body required.

SUCCESS RESPONSE:
{
  "message": "Policy retrieved successfully",
  "status": true,
  "data": {
    "policyId": 2,
    "policyNumber": "POL002",
    "policyName": "Gold Health Plan",
    "policyType": "Family",
    "coverageAmount": 500000,
    "premiumAmount": 22000,
    "policyStatus": "ACTIVE"
  }
}

PURPOSE:
Used to view specific policy details by ID.

====================================================
API NAME:
Create Policy

ROLE:
ADMIN

METHOD:
POST

URL:
http://localhost:8080/api/policies

HEADERS:
Content-Type: application/json

REQUEST:
{
  "policyNumber": "POL006",
  "policyName": "Platinum Health Plan",
  "policyType": "Individual",
  "coverageAmount": 750000,
  "premiumAmount": 25000,
  "benefits": "Hospitalization, Surgery, Maternity",
  "policyStatus": "ACTIVE",
  "startDate": "2024-01-01",
  "endDate": "2025-12-31"
}

SUCCESS RESPONSE:
{
  "message": "Policy created successfully",
  "status": true,
  "data": {
    "policyId": 6,
    "policyNumber": "POL006",
    "policyName": "Platinum Health Plan"
  }
}

PURPOSE:
Used by the admin to add a new insurance policy.

====================================================
API NAME:
Update Policy

ROLE:
ADMIN

METHOD:
PUT

URL:
http://localhost:8080/api/policies/{policyId}

HEADERS:
Content-Type: application/json

REQUEST:
{
  "policyName": "Platinum Health Plan Updated",
  "policyType": "Individual",
  "coverageAmount": 800000,
  "premiumAmount": 26000,
  "benefits": "Hospitalization, Surgery, Maternity, Dental",
  "policyStatus": "ACTIVE",
  "startDate": "2024-01-01",
  "endDate": "2026-12-31"
}

SUCCESS RESPONSE:
{
  "message": "Policy updated successfully",
  "status": true,
  "data": {
    "policyId": 6,
    "policyNumber": "POL006",
    "policyName": "Platinum Health Plan Updated"
  }
}

PURPOSE:
Used to modify existing policy details.

====================================================
API NAME:
Delete Policy

ROLE:
ADMIN

METHOD:
DELETE

URL:
http://localhost:8080/api/policies/{policyId}

HEADERS:
Content-Type: application/json

REQUEST:
No request body required.

SUCCESS RESPONSE:
{
  "message": "Policy deleted successfully",
  "status": true,
  "data": null
}

PURPOSE:
Used by the admin to remove a policy from the system.

====================================================
API NAME:
Assign Policy To User

ROLE:
ADMIN

METHOD:
POST

URL:
http://localhost:8080/api/user-policies

HEADERS:
Content-Type: application/json

REQUEST:
{
  "user": { "userId": 5 },
  "insurancePolicy": { "policyId": 2 },
  "purchasedDate": "2024-05-12",
  "expiryDate": "2025-05-11",
  "policyActiveStatus": true
}

SUCCESS RESPONSE:
{
  "message": "User policy assigned successfully",
  "status": true,
  "data": {
    "userPolicyId": 21,
    "user": { "userId": 5 },
    "insurancePolicy": { "policyId": 2 }
  }
}

PURPOSE:
Used by the admin to assign an insurance policy to a user.

====================================================
API NAME:
Get Policies By User

ROLE:
ADMIN / POLICYHOLDER

METHOD:
GET

URL:
http://localhost:8080/api/user-policies/user/{userId}

HEADERS:
Content-Type: application/json

REQUEST:
No request body required.

SUCCESS RESPONSE:
{
  "message": "User policies retrieved successfully",
  "status": true,
  "data": [
    {
      "userPolicyId": 12,
      "insurancePolicy": {
        "policyId": 2,
        "policyNumber": "POL002",
        "policyName": "Gold Health Plan"
      },
      "policyActiveStatus": true
    }
  ]
}

PURPOSE:
Used to view insurance policies assigned to a specific user.

====================================================
API NAME:
Get All Hospitals

ROLE:
ADMIN / APPROVER / POLICYHOLDER

METHOD:
GET

URL:
http://localhost:8080/api/hospitals

HEADERS:
Content-Type: application/json

REQUEST:
No request body required.

SUCCESS RESPONSE:
{
  "message": "Hospitals retrieved successfully",
  "status": true,
  "data": [
    {
      "hospitalId": 1,
      "hospitalName": "City Health Hospital",
      "hospitalType": "Private"
    },
    {
      "hospitalId": 2,
      "hospitalName": "State General Hospital",
      "hospitalType": "Government"
    }
  ]
}

PURPOSE:
Used to list all hospitals stored in the system.

====================================================
API NAME:
Create Hospital

ROLE:
ADMIN

METHOD:
POST

URL:
http://localhost:8080/api/hospitals

HEADERS:
Content-Type: application/json

REQUEST:
{
  "hospitalName": "Central Care Hospital",
  "hospitalType": "Private",
  "address": "22 Central Avenue, City",
  "phoneNumber": "01122334455"
}

SUCCESS RESPONSE:
{
  "message": "Hospital created successfully",
  "status": true,
  "data": {
    "hospitalId": 6,
    "hospitalName": "Central Care Hospital"
  }
}

PURPOSE:
Used by the admin to add a new hospital.

====================================================
API NAME:
Get All Doctors

ROLE:
ADMIN / APPROVER / POLICYHOLDER

METHOD:
GET

URL:
http://localhost:8080/api/doctors

HEADERS:
Content-Type: application/json

REQUEST:
No request body required.

SUCCESS RESPONSE:
{
  "message": "Doctors retrieved successfully",
  "status": true,
  "data": [
    {
      "doctorId": 1,
      "doctorName": "Dr. Amit Sharma",
      "specialization": "Cardiology"
    },
    {
      "doctorId": 2,
      "doctorName": "Dr. Neha Patel",
      "specialization": "Neurology"
    }
  ]
}

PURPOSE:
Used to list all doctors in the system.

====================================================
API NAME:
Create Doctor

ROLE:
ADMIN

METHOD:
POST

URL:
http://localhost:8080/api/doctors

HEADERS:
Content-Type: application/json

REQUEST:
{
  "doctorName": "Dr. Ravi Verma",
  "specialization": "Orthopedics",
  "qualification": "MBBS, MS",
  "experienceYears": 12,
  "hospital": { "hospitalId": 1 }
}

SUCCESS RESPONSE:
{
  "message": "Doctor created successfully",
  "status": true,
  "data": {
    "doctorId": 11,
    "doctorName": "Dr. Ravi Verma"
  }
}

PURPOSE:
Used by the admin to add a new doctor to a hospital.

====================================================
API NAME:
Get All Treatments

ROLE:
ADMIN / APPROVER / POLICYHOLDER

METHOD:
GET

URL:
http://localhost:8080/api/treatments

HEADERS:
Content-Type: application/json

REQUEST:
No request body required.

SUCCESS RESPONSE:
{
  "message": "Treatments retrieved successfully",
  "status": true,
  "data": [
    {
      "treatmentId": 1,
      "diagnosis": "Fracture",
      "treatmentAmount": 45000,
      "treatmentDate": "2024-03-12"
    },
    {
      "treatmentId": 2,
      "diagnosis": "Appendicitis",
      "treatmentAmount": 30000,
      "treatmentDate": "2024-02-20"
    }
  ]
}

PURPOSE:
Used to list all treatment records in the system.

====================================================
API NAME:
Create Treatment

ROLE:
ADMIN

METHOD:
POST

URL:
http://localhost:8080/api/treatments

HEADERS:
Content-Type: application/json

REQUEST:
{
  "diagnosis": "Kidney Stone",
  "treatmentDescription": "Surgical removal of kidney stone",
  "treatmentAmount": 55000,
  "treatmentDate": "2024-06-10",
  "user": { "userId": 5 },
  "doctor": { "doctorId": 3 },
  "hospital": { "hospitalId": 2 }
}

SUCCESS RESPONSE:
{
  "message": "Treatment created successfully",
  "status": true,
  "data": {
    "treatmentId": 11,
    "diagnosis": "Kidney Stone"
  }
}

PURPOSE:
Used by admin or support to record a new treatment event.

====================================================
API NAME:
Raise Claim

ROLE:
POLICYHOLDER

METHOD:
POST

URL:
http://localhost:8080/api/claims

HEADERS:
Content-Type: application/json

REQUEST:
{
  "claimNumber": "CLM011",
  "claimAmount": 45000,
  "claimDate": "2024-06-15",
  "user": { "userId": 5 },
  "treatment": {
    "diagnosis": "Kidney Stone",
    "treatmentDescription": "Surgical removal of kidney stone",
    "treatmentAmount": 45000,
    "treatmentDate": "2024-06-10",
    "hospitalName": "Central Health Hospital",
    "hospitalAddress": "123 Main St, Springfield",
    "hospitalPhone": "555-0123",
    "doctorName": "Dr. Alice Brown",
    "doctorSpecialization": "Urology",
    "doctorQualification": "MD",
    "doctorExperienceYears": 12
  },
  "insurancePolicy": { "policyId": 2 }
}

SUCCESS RESPONSE:
{
  "message": "Claim saved successfully",
  "status": true,
  "data": {
    "claimId": 11,
    "claimNumber": "CLM011",
    "claimStatus": "PENDING"
  }
}

PURPOSE:
Used by policyholders to create a new claim for treatment.

====================================================
API NAME:
Get All Claims

ROLE:
ADMIN / APPROVER

METHOD:
GET

URL:
http://localhost:8080/api/claims

HEADERS:
Content-Type: application/json

REQUEST:
No request body required.

SUCCESS RESPONSE:
{
  "message": "Claims retrieved successfully",
  "status": true,
  "data": [
    {
      "claimId": 1,
      "claimNumber": "CLM001",
      "claimStatus": "PENDING",
      "claimAmount": 35000
    },
    {
      "claimId": 2,
      "claimNumber": "CLM002",
      "claimStatus": "APPROVED",
      "claimAmount": 50000
    }
  ]
}

PURPOSE:
Used by approvers and admin to view every claim in the system.

====================================================
API NAME:
Get Claims By User

ROLE:
POLICYHOLDER / ADMIN

METHOD:
GET

URL:
http://localhost:8080/api/claims/user/{userId}

HEADERS:
Content-Type: application/json

REQUEST:
No request body required.

SUCCESS RESPONSE:
{
  "message": "Claims retrieved successfully",
  "status": true,
  "data": [
    {
      "claimId": 5,
      "claimNumber": "CLM005",
      "claimStatus": "REJECTED",
      "claimAmount": 25000
    }
  ]
}

PURPOSE:
Used to display claims raised by a specific policyholder.

====================================================
API NAME:
Get Claims By Status

ROLE:
ADMIN / APPROVER

METHOD:
GET

URL:
http://localhost:8080/api/claims/status/{status}

HEADERS:
Content-Type: application/json

REQUEST:
No request body required.

SUCCESS RESPONSE:
{
  "message": "Claims retrieved successfully",
  "status": true,
  "data": [
    {
      "claimId": 3,
      "claimNumber": "CLM003",
      "claimStatus": "APPROVED",
      "claimAmount": 60000
    }
  ]
}

PURPOSE:
Used to filter claims by status such as PENDING, APPROVED, REJECTED, or SETTLED.

====================================================
API NAME:
Approve Claim

ROLE:
APPROVER

METHOD:
PUT

URL:
http://localhost:8080/api/claims/{claimId}/approve

HEADERS:
Content-Type: application/json

REQUEST:
{
  "approvedAmount": 42000,
  "approverComment": "Approved with partial coverage"
}

SUCCESS RESPONSE:
{
  "message": "Claim approved successfully",
  "status": true,
  "data": {
    "claimId": 3,
    "claimStatus": "APPROVED",
    "approvedAmount": 42000
  }
}

PURPOSE:
Used by approvers to approve a pending claim and optionally provide comments.

====================================================
API NAME:
Reject Claim

ROLE:
APPROVER

METHOD:
PUT

URL:
http://localhost:8080/api/claims/{claimId}/reject

HEADERS:
Content-Type: application/json

REQUEST:
{
  "rejectionReason": "Required documents missing"
}

SUCCESS RESPONSE:
{
  "message": "Claim rejected successfully",
  "status": true,
  "data": {
    "claimId": 4,
    "claimStatus": "REJECTED",
    "rejectionReason": "Required documents missing"
  }
}

PURPOSE:
Used by approvers to reject a claim with a reason.

====================================================
API NAME:
Upload Document

ROLE:
POLICYHOLDER / ADMIN

METHOD:
POST

URL:
http://localhost:8080/api/documents

HEADERS:
Content-Type: application/json

REQUEST:
{
  "documentName": "Hospital Invoice",
  "documentType": "Invoice",
  "documentPath": "/uploads/invoices/invoice_11.pdf",
  "uploadedDate": "2024-06-16",
  "claim": { "claimId": 11 }
}

SUCCESS RESPONSE:
{
  "message": "Document saved successfully",
  "status": true,
  "data": {
    "documentId": 11,
    "documentName": "Hospital Invoice"
  }
}

PURPOSE:
Used to save claim document details related to a claim.

====================================================
API NAME:
Get All Documents

ROLE:
ADMIN / APPROVER / POLICYHOLDER

METHOD:
GET

URL:
http://localhost:8080/api/documents

HEADERS:
Content-Type: application/json

REQUEST:
No request body required.

SUCCESS RESPONSE:
{
  "message": "Documents retrieved successfully",
  "status": true,
  "data": [
    {
      "documentId": 1,
      "documentName": "Medical Report",
      "documentType": "Report"
    },
    {
      "documentId": 2,
      "documentName": "Insurance Form",
      "documentType": "Form"
    }
  ]
}

PURPOSE:
Used to list all documents attached to claims.

====================================================
API NAME:
Create Payment

ROLE:
ADMIN

METHOD:
POST

URL:
http://localhost:8080/api/payments

HEADERS:
Content-Type: application/json

REQUEST:
{
  "paymentAmount": 42000,
  "paymentDate": "2024-06-18",
  "paymentMode": "NEFT",
  "transactionId": "TXN123456789",
  "paymentStatus": "PENDING",
  "companyAccountNumber": "5550012345",
  "companyBankName": "State Bank",
  "claim": { "claimId": 3 },
  "user": { "userId": 2 }
}

SUCCESS RESPONSE:
{
  "message": "Payment saved successfully",
  "status": true,
  "data": {
    "paymentId": 8,
    "paymentAmount": 42000,
    "paymentStatus": "PENDING"
  }
}

PURPOSE:
Used to record a payment entry for an approved claim.

====================================================
API NAME:
Process Payment

ROLE:
ADMIN

METHOD:
PUT

URL:
http://localhost:8080/api/payments/{paymentId}/process

HEADERS:
Content-Type: application/json

REQUEST:
No request body required.

SUCCESS RESPONSE:
{
  "message": "Payment processed successfully",
  "status": true,
  "data": {
    "paymentId": 8,
    "paymentStatus": "COMPLETED",
    "paymentAmount": 42000
  }
}

PURPOSE:
Used by the admin to process and complete a payment for an approved claim.

====================================================
API NAME:
Get All Payments

ROLE:
ADMIN / POLICYHOLDER / APPROVER

METHOD:
GET

URL:
http://localhost:8080/api/payments

HEADERS:
Content-Type: application/json

REQUEST:
No request body required.

SUCCESS RESPONSE:
{
  "message": "Payments retrieved successfully",
  "status": true,
  "data": [
    {
      "paymentId": 1,
      "paymentAmount": 35000,
      "paymentStatus": "COMPLETED"
    },
    {
      "paymentId": 2,
      "paymentAmount": 42000,
      "paymentStatus": "PENDING"
    }
  ]
}

PURPOSE:
Used to list all payment records available in the system.

====================================================
API NAME:
Get Payments By Status

ROLE:
ADMIN / POLICYHOLDER / APPROVER

METHOD:
GET

URL:
http://localhost:8080/api/payments/status/{status}

HEADERS:
Content-Type: application/json

REQUEST:
No request body required.

SUCCESS RESPONSE:
{
  "message": "Payments retrieved successfully",
  "status": true,
  "data": [
    {
      "paymentId": 2,
      "paymentAmount": 42000,
      "paymentStatus": "PENDING"
    }
  ]
}

PURPOSE:
Used to filter and view payments by status, such as PENDING or COMPLETED.

====================================================
END OF DOCUMENTATION
