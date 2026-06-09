# Frontend for Health Insurance Claim Management

## Overview

This frontend is a static UI built with plain HTML, CSS, and JavaScript with **role-based access control**. Each user role (ADMIN, APPROVER, POLICYHOLDER) sees different tabs and functionality tailored to their permissions.

## Key Features

### Authentication
- Login with username and password
- Role-based dashboard display
- Session management
- Logout functionality

### Role-Based Dashboards

**ADMIN**: Full system access
- View all users
- View all policies and claims
- See admin-specific actions

**APPROVER**: Claim approval focus
- View pending claims only
- Approve/reject claims with comments
- See approver dashboard

**POLICYHOLDER**: Personal claim management
- File new claims
- View personal claims and policies
- Track claim status

## File Structure

- `src/main/resources/static/index.html` - Main HTML page
- `src/main/resources/static/style.css` - Styling with role-specific styles
- `src/main/resources/static/app.js` - Role-based logic and API integration

## Usage

1. Start Spring Boot: `mvn spring-boot:run`
2. Open browser: `http://localhost:8080/index.html`
3. Login with credentials from database
4. Dashboard displays with role-specific content

## Troubleshooting Login Issues

### Problem: Login Only Works for ADMIN

**Root Cause**: Other roles (APPROVER, POLICYHOLDER) don't exist or are INACTIVE in the database.

**Solution 1 - Add Missing Users**

Check existing users:
```sql
SELECT user_id, username, role, account_status FROM users;
```

Add test users if missing:
```sql
-- POLICYHOLDER
INSERT INTO users (username, password, full_name, email, phone_number, date_of_birth, address, role, account_status, created_at)
VALUES ('policyholder1', '$2a$10$YIjlrBKvVZVSryznB.Pb1.', 'John Doe', 'john@test.com', '9876543210', '1990-01-15', '123 Main St', 'POLICYHOLDER', 'ACTIVE', NOW());

-- APPROVER  
INSERT INTO users (username, password, full_name, email, phone_number, date_of_birth, address, role, account_status, created_at)
VALUES ('approver1', '$2a$10$YIjlrBKvVZVSryznB.Pb1.', 'Jane Smith', 'jane@test.com', '8765432109', '1985-05-20', '456 Oak Ave', 'APPROVER', 'ACTIVE', NOW());
```

**Solution 2 - Activate Inactive Users**

```sql
UPDATE users SET account_status = 'ACTIVE' 
WHERE role IN ('APPROVER', 'POLICYHOLDER');
```

**Solution 3 - Verify in Browser**

1. Open F12 (Developer Tools)
2. Go to Console tab
3. Login attempt - check error messages
4. Go to Network tab - inspect API responses

## Role-Based Navigation

### ADMIN Dashboard
Tabs: Dashboard | Policies | Claims | Users | Profile | Logout

Features:
- Browse all system data
- View all users
- View all policies
- View all claims
- Access full profile

### APPROVER Dashboard
Tabs: Dashboard | Pending Claims | Profile | Logout

Features:
- Review pending claims
- Approve/reject claims
- Add approver comments
- View claim details

### POLICYHOLDER Dashboard
Tabs: Dashboard | My Claims | My Policies | Profile | Logout

Features:
- File new claims
- View personal claims
- View assigned policies
- Track status
- Access profile

## API Endpoints

**Login**
- `POST /api/users/login` - Authenticate user
- Response returns `message`, `status`, and `role` only; it does not return the full user object.

**Users (ADMIN only)**
- `GET /api/users` - List all users

**Policies**
- `GET /api/policies` - All policies (ADMIN, APPROVER)
- `GET /api/user-policies/user/{userId}` - User's policies (POLICYHOLDER)

**Claims**
- `GET /api/claims` - All claims (ADMIN)
- `GET /api/claims/user/{userId}` - User's claims (POLICYHOLDER)
- `GET /api/claims/status/PENDING` - Pending claims (APPROVER)
- `POST /api/claims` - File claim (POLICYHOLDER)
- `PUT /api/claims/{id}/approve` - Approve (APPROVER)
- `PUT /api/claims/{id}/reject` - Reject (APPROVER)

## Technical Notes

- No external libraries (vanilla JavaScript)
- Stateless authentication
- CORS enabled on backend
- All REST calls use Content-Type: application/json
- Role config: `ROLE_CONFIG` in app.js

## Future Angular Upgrade

- Replace static files with Angular build
- Keep same API endpoints
- Serve from `src/main/resources/static`
- Add form validation
- Add charts/dashboards
- Real-time notifications

## Structure

- `src/main/resources/static/index.html` - Main frontend page
- `src/main/resources/static/style.css` - Styling for layout and components
- `src/main/resources/static/app.js` - JavaScript logic for calling backend APIs and rendering UI

## What the Frontend Supports

- Login using the backend endpoint: `POST /api/users/login`
- View insurance policies: `GET /api/policies`
- View claims:
  - `GET /api/claims` for admin/approver
  - `GET /api/claims/user/{userId}` for policyholder
- View users (requires ADMIN role): `GET /api/users`
- Approve or reject claims (requires APPROVER role)

## Usage

1. Start the Spring Boot application.
2. Open a browser and navigate to:
   - `http://localhost:8080/index.html`
3. Use an existing backend user account to login.
4. Use the navigation buttons to inspect policies, claims, and users.

## Notes

- This frontend is intentionally simple and does not include Angular or Bootstrap.
- It is intended as a basic demonstration and can be extended later with a richer Angular-based UI.
- For more advanced frontend work, replace this static UI with your Angular project and connect to the same backend API.

## Tips for Later Angular Upgrade

- Keep the API endpoints in mind as you build components.
- Use `fetch` or a library like `HttpClient` in Angular to call the backend.
- You can continue serving the Angular production files from `src/main/resources/static` if you want Spring Boot to host the frontend.
