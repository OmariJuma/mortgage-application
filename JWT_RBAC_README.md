# JWT RBAC Implementation

This document describes the JWT (JSON Web Token) and Role-Based Access Control (RBAC) implementation for the mortgage application.

## 🔐 Security Overview

- **Authentication**: JWT Bearer tokens
- **Authorization**: Role-based access control (RBAC)
- **Roles**: APPLICANT and OFFICER
- **Token Format**: Standard JWT with ROLE_ claims

## 🏗️ Architecture

### Components
1. **JwtTokenProvider**: Generates and validates JWT tokens
2. **JwtAuthenticationFilter**: Processes JWT tokens in requests
3. **UserPrincipal**: Custom UserDetails implementation
4. **CustomUserDetailsService**: Loads users from database
5. **AuthService**: Handles login logic
6. **SecurityConfig**: Configures security rules and RBAC

## 👥 Roles and Permissions

### APPLICANT Role
- **Permissions**: Create/Read own applications
- **Endpoints**: `/api/v1/applications/**` (own applications only)
- **Description**: Users who submit mortgage applications

### OFFICER Role
- **Permissions**: Read all applications, make decisions
- **Endpoints**: 
  - `/api/v1/applications/**` (all applications)
  - `/api/v1/decisions/**` (create decisions)
  - `/api/users/**` (user management)
- **Description**: Loan officers who review and approve applications

## 🔑 API Endpoints

### Public Endpoints (No Authentication Required)
```
POST /api/auth/login          - Login and get JWT token
POST /api/users/register      - Register new user
```

### Protected Endpoints (Authentication Required)

#### Application Management
```
GET    /api/v1/applications/**     - APPLICANT (own), OFFICER (all)
POST   /api/v1/applications/**     - APPLICANT
PUT    /api/v1/applications/**     - APPLICANT (own), OFFICER (all)
DELETE /api/v1/applications/**     - APPLICANT (own), OFFICER (all)
```

#### Decision Management
```
GET    /api/v1/decisions/**        - OFFICER only
POST   /api/v1/decisions/**        - OFFICER only
PUT    /api/v1/decisions/**        - OFFICER only
DELETE /api/v1/decisions/**        - OFFICER only
```

#### User Management
```
GET    /api/users/**               - OFFICER only
POST   /api/users/**               - OFFICER only
PUT    /api/users/**               - OFFICER only
DELETE /api/users/**               - OFFICER only
```

## 🚀 Usage Examples

### 1. Register a New User
```bash
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_applicant",
    "password": "password123",
    "roles": "APPLICANT"
  }'
```

### 2. Login and Get JWT Token
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_applicant",
    "password": "password123"
  }'
```

**Response:**
```json
{
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "Bearer",
    "username": "john_applicant",
    "roles": "ROLE_APPLICANT"
}
```

### 3. Use JWT Token for Protected Endpoints
```bash
curl -X GET http://localhost:8081/api/v1/applications \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

## 🔧 Configuration

### JWT Settings (application.properties)
```properties
# JWT Configuration
jwt.secret=your-super-secret-jwt-key-that-should-be-at-least-256-bits-long-for-production
jwt.expiration=86400000  # 24 hours in milliseconds
```

### Security Configuration
- **CSRF**: Disabled (stateless API)
- **Session**: Stateless (JWT-based)
- **Authentication**: JWT Bearer tokens
- **Authorization**: Role-based with method-level security

## 🛡️ Security Features

### JWT Token Structure
```json
{
  "sub": "username",
  "userId": "uuid",
  "roles": "ROLE_APPLICANT,ROLE_OFFICER",
  "iat": 1640995200,
  "exp": 1641081600
}
```

### Security Headers
- **Authorization**: `Bearer <jwt_token>`
- **Content-Type**: `application/json`

### Token Validation
- Signature verification
- Expiration check
- User existence validation
- Role-based authorization

## 📝 Implementation Details

### Role Mapping
- Database roles: `"APPLICANT"`, `"OFFICER"`
- Spring Security roles: `"ROLE_APPLICANT"`, `"ROLE_OFFICER"`

### Method-Level Security
```java
@PreAuthorize("hasRole('OFFICER')")
public List<Application> getAllApplications() {
    // Only OFFICER can access
}

@PreAuthorize("hasRole('APPLICANT') and #application.applicantId == authentication.principal.id")
public Application updateApplication(Application application) {
    // APPLICANT can only update their own applications
}
```

### Current User Access
```java
@Service
public class ApplicationService {
    
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        return userRepository.findById(userPrincipal.getId()).orElseThrow();
    }
}
```

## 🔍 Testing

### Test User Creation
```bash
# Create APPLICANT user
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "applicant1",
    "password": "password123",
    "roles": "APPLICANT"
  }'

# Create OFFICER user
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "officer1",
    "password": "password123",
    "roles": "OFFICER"
  }'
```

### Test Authentication
```bash
# Login as APPLICANT
TOKEN=$(curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "applicant1", "password": "password123"}' | jq -r '.token')

# Use token for protected endpoint
curl -X GET http://localhost:8081/api/v1/applications \
  -H "Authorization: Bearer $TOKEN"
```

## ⚠️ Security Considerations

1. **JWT Secret**: Use a strong, unique secret in production
2. **Token Expiration**: Set appropriate expiration times
3. **HTTPS**: Always use HTTPS in production
4. **Token Storage**: Store tokens securely on client side
5. **Role Validation**: Always validate roles on both client and server
6. **Input Validation**: Validate all inputs to prevent injection attacks

## 🚨 Error Handling

### Authentication Errors
```json
{
    "error": "Invalid JWT token"
}
```

### Authorization Errors
```json
{
    "error": "Access denied"
}
```

### Validation Errors
```json
{
    "username": "Username is required",
    "password": "Password must be at least 6 characters long"
}
```

## 📚 Dependencies Added

- `jjwt-api`: JWT API
- `jjwt-impl`: JWT Implementation
- `jjwt-jackson`: JWT Jackson support

## 🔄 Migration from Basic Auth

The system now uses JWT tokens instead of Basic Authentication:
- **Before**: `Authorization: Basic <base64_credentials>`
- **After**: `Authorization: Bearer <jwt_token>`

All existing user registration functionality remains the same, but authentication now requires JWT tokens.
