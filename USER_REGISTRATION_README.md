# User Registration System

This document describes the user registration functionality that has been added to the mortgage application.

## Features

- User registration with username, password, and roles
- Password hashing using BCrypt (no additional dependencies required)
- Input validation for registration data
- Custom exception handling
- RESTful API endpoints

## API Endpoints

### Register a new user
```
POST /api/users/register
Content-Type: application/json

{
    "username": "john_doe",
    "password": "securePassword123",
    "roles": "USER"
}
```

**Response (201 Created):**
```json
{
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "username": "john_doe",
    "roles": "USER",
    "createdAt": "2024-01-15T10:30:00"
}
```

### Get user by ID
```
GET /api/users/{userId}
```

### Get user by username
```
GET /api/users/username/{username}
```

### Get all users
```
GET /api/users
```

## Database Changes

A new Flyway migration `V4__add_password_to_users.sql` has been added to include the password field in the users table.

## Security Features

- Passwords are hashed using BCrypt with Spring Security's built-in encoder
- No plain text passwords are stored in the database
- Input validation ensures data integrity
- Custom exceptions provide meaningful error messages

## Validation Rules

- Username: 3-50 characters, required, unique
- Password: minimum 6 characters, required
- Roles: required field

## Error Handling

The system provides proper HTTP status codes and error messages:

- `400 Bad Request`: Validation errors
- `409 Conflict`: Username already exists
- `404 Not Found`: User not found
- `500 Internal Server Error`: Unexpected errors

## Testing

Comprehensive unit tests have been created for the UserService class, covering:
- Successful user registration
- Duplicate username handling
- User retrieval by ID and username
- Error scenarios

## Dependencies Added

- `spring-boot-starter-validation`: For input validation

## Files Created/Modified

### New Files:
- `UserRepository.java` - Data access layer
- `UserService.java` - Business logic layer
- `UserRegistrationDTO.java` - Request DTO
- `UserResponseDTO.java` - Response DTO
- `PasswordConfig.java` - BCrypt configuration
- `UserAlreadyExistsException.java` - Custom exception
- `UserNotFoundException.java` - Custom exception
- `GlobalExceptionHandler.java` - Exception handling
- `V4__add_password_to_users.sql` - Database migration
- `UserServiceTest.java` - Unit tests

### Modified Files:
- `User.java` - Added password field
- `Usercontroller.java` - Added REST endpoints
- `pom.xml` - Added validation dependency

## Usage Example

```bash
# Register a new user
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "roles": "USER"
  }'

# Get user by username
curl http://localhost:8080/api/users/username/testuser
```
