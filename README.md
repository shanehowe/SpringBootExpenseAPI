# Expense Tracker API

## Introduction

The Expense Tracker API is a robust application built with Spring Boot, designed to help users manage and track their expenses efficiently. Utilizing PostgreSQL as the database management system, this application ensures reliable data storage and retrieval. 

With the integration of Spring Data it allows for easy interaction with the database through a simplified repository interface. 

The API employs JWT (JSON Web Token) authentication, providing a secure method for user authentication and authorization.

# Authentication API

## Routes

### POST /auth/signup

- **Description**: Sign up a new user.
- **Request Body**: 
  - `AuthRequest` (JSON):
    - `username`: string
    - `password`: string
- **Responses**:
  - **201 Created**: Returns the signup response.
    - Example:
      ```json
      {
        "token": "jwt_token_here",
        "username": "new_user"
      }
      ```
  - **400 Bad Request**: Returns an error message if the request is invalid.
    - Example:
      ```json
      {
        "error": "Error message here"
      }
      ```

### POST /auth/login

- **Description**: Log in an existing user.
- **Request Body**:
  - `AuthRequest` (JSON):
    - `username`: string
    - `password`: string
- **Responses**:
  - **200 OK**: Returns the login response.
    - Example:
      ```json
      {
        "token": "jwt_token_here",
        "username": "existing_user"
      }
      ```
  - **400 Bad Request**: Returns an error if login fails.


  # Expense API

## Routes

### GET /expenses/{id}

- **Description**: Retrieve an expense by its ID for the authenticated user.
- **Path Parameters**:
  - `id`: The ID of the expense (long).
- **Responses**:
  - **200 OK**: Returns the expense details.
    - Example:
      ```json
      {
        "id": 1,
        "amount": 100.0,
        "description": "Groceries",
        "date": "2024-01-01"
      }
      ```
  - **404 Not Found**: If the expense does not exist.

### GET /expenses

- **Description**: Retrieve all expenses for the authenticated user, paginated.
- **Query Parameters**:
  - `page`: The page number (optional).
  - `size`: The number of expenses per page (default is 100).
- **Responses**:
  - **200 OK**: Returns a list of expenses.
    - Example:
      ```json
      [
        {
          "id": 1,
          "amount": 100.0,
          "description": "Groceries",
          "date": "2024-01-01"
        },
        {
          "id": 2,
          "amount": 50.0,
          "description": "Transport",
          "date": "2024-01-02"
        }
      ]
      ```

### POST /expenses

- **Description**: Create a new expense for the authenticated user.
- **Request Body**:
  - `ExpenseDto` (JSON):
    - `amount`: double
    - `description`: string
    - `date`: string (format: YYYY-MM-DD)
- **Responses**:
  - **201 Created**: Returns the created expense.
    - Example:
      ```json
      {
        "id": 1,
        "amount": 100.0,
        "description": "Groceries",
        "date": "2024-01-01"
      }
      ```
  - **400 Bad Request**: Returns an error message if the request is invalid.

### PUT /expenses/{id}

- **Description**: Update an existing expense for the authenticated user.
- **Path Parameters**:
  - `id`: The ID of the expense (long).
- **Request Body**:
  - `ExpenseDto` (JSON):
    - `amount`: double
    - `description`: string
    - `date`: string (format: YYYY-MM-DD)
- **Responses**:
  - **204 No Content**: Expense updated successfully.
  - **400 Bad Request**: Returns an error message if the request is invalid.

### DELETE /expenses/{id}

- **Description**: Delete an expense by its ID for the authenticated user.
- **Path Parameters**:
  - `id`: The ID of the expense (long).
- **Responses**:
  - **204 No Content**: Expense deleted successfully.
  - **404 Not Found**: If the expense does not exist.
