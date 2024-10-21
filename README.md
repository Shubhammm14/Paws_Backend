# Paws_Backend

Paws_Backend is a Spring Boot application designed to manage users, pets, products, orders, and appointments for a pet-buying website. This application provides secure user authentication, CRUD operations for pets and products, order management, and appointment handling.

## Features

- **User Authentication & Authorization:**
  - Secure JWT-based authentication.
  - User sign-up and sign-in functionalities.
  - Role-based access control (e.g., seller, consumer, vet).

- **Pet Management:**
  - Create, update, delete, and retrieve pets.
  - Search pets by keyword or seller.

- **Product Management:**
  - Create, update, delete, and retrieve products.
  - Search products by keyword or seller.

- **Order Management:**
  - Create orders for pets or products.
  - Approve, confirm, and cancel orders.
  - Handle order delivery with OTP verification.
  - Manage order statuses (e.g., confirmed, canceled).

- **Appointment Management:**
  - Schedule, approve, and manage appointments with vets.
  - Handle appointment statuses (e.g., scheduled, completed, canceled).

## API Endpoints

### Authentication (`/auth`)
- **POST /auth/signup:** Create a new user account.
- **POST /auth/sign_in:** Sign in to the application.

### User Management (`/api/users`)
- **GET /api/users/{userId}:** Retrieve user details by ID.
- **PUT /api/users/{userId}:** Update user details.
- **GET /api/users/me:** Retrieve the authenticated user's details.
- **GET /api/users/{sellerId}/orders-needing-approval:** Get orders needing approval for a seller.

### Pet Management (`/api/pets`)
- **POST /api/pets:** Create a new pet.
- **PUT /api/pets/{id}:** Update an existing pet.
- **DELETE /api/pets/{id}:** Delete a pet.
- **GET /api/pets/{id}:** Retrieve a pet by ID.
- **GET /api/pets:** Retrieve all pets.
- **GET /api/pets/search:** Search pets by keyword.
- **GET /api/pets/seller/{sellerId}:** Retrieve pets by seller ID.

### Product Management (`/api/products`)
- **POST /api/products:** Create a new product.
- **PUT /api/products/{id}:** Update an existing product.
- **DELETE /api/products/{id}:** Delete a product.
- **GET /api/products/{id}:** Retrieve a product by ID.
- **GET /api/products:** Retrieve all products.
- **GET /api/products/search:** Search products by keyword.
- **GET /api/products/seller/name:** Search products by seller name.
- **GET /api/products/seller/{sellerId}:** Retrieve products by seller ID.

### Order Management (`/api/orders`)
- **POST /api/orders:** Create a new order.
- **PUT /api/orders/{orderId}/confirm:** Confirm an order.
- **GET /api/orders/user/{userId}/approved-orders:** Get all orders with approved items for a user.
- **PUT /api/orders/{orderId}/handleItem/{itemId}/user/{userId}:** Approve or reject an item in an order.
- **PUT /api/orders/{orderId}/cancel:** Cancel an order.
- **GET /api/orders/user/{userId}/confirmed-orders:** Get all confirmed orders for a user.
- **GET /api/orders/user/{userId}/canceled-orders:** Get all canceled orders for a user.
- **GET /api/orders/seller/{sellerId}/confirmed-orders:** Get all confirmed orders for a seller.
- **GET /api/orders/seller/{sellerId}/canceled-orders:** Get all canceled orders for a seller.
- **POST /api/orders/{orderId}/complete:** Complete an order using OTP verification.

### Appointment Management (`/api/appointments`)
- **POST /api/appointments:** Schedule a new appointment.
- **GET /api/appointments/{appointmentId}:** Get details of an appointment.
- **PUT /api/appointments/{appointmentId}/approve:** Approve an appointment.
- **PUT /api/appointments/{appointmentId}/complete:** Mark an appointment as completed.
- **PUT /api/appointments/{appointmentId}/cancel:** Cancel an appointment.
- **GET /api/appointments/user/{userId}:** Get all appointments for a user.
- **GET /api/appointments/vet/{vetId}:** Get all appointments for a vet.

## Installation

### Prerequisites

- Java 17 or later
- Maven
- MySQL

### Steps

1. Clone the repository:
    ```bash
    git clone https://github.com/Shubhammm14/Paws_Backend.git
    ```

2. Navigate to the project directory:
    ```bash
    cd Paws_Backend
    ```

3. Configure MySQL database:
   - Create a database named `paws_backend`.
   - Update `application.properties` with your database credentials.

4. Build the project:
    ```bash
    mvn clean install
    ```

5. Run the application:
    ```bash
    mvn spring-boot:run
    ```

6. The application will be available at `http://localhost:8080`.

## Contributing

Feel free to contribute to this project by creating a pull request or submitting an issue.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
