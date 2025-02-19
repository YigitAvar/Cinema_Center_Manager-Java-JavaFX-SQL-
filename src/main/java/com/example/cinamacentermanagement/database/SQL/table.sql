CREATE DATABASE Cinema_Center_Management;

Use Cinema_Center_Management;

-- Create Movies Table
CREATE TABLE Movies (
    movie_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    poster VARCHAR(255),
    genre VARCHAR(100),
    summary TEXT,
    ticket_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00
);

-- Create Halls Table
CREATE TABLE Halls (
    hall_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    capacity INT
);

-- Create Sessions Table
CREATE TABLE Sessions (
    session_id INT PRIMARY KEY AUTO_INCREMENT,
    movie_id INT,
    hall_id INT,
    session_time DATETIME,
    available_seats INT,
    FOREIGN KEY (movie_id) REFERENCES Movies(movie_id),
    FOREIGN KEY (hall_id) REFERENCES Halls(hall_id)
);

-- Create Customers Table
CREATE TABLE Customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    age INT,
    email VARCHAR(100),
    phone_number VARCHAR(20)
);

-- Create Employees Table
CREATE TABLE Employees (
    employee_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('cashier', 'admin', 'manager') NOT NULL
);

CREATE TABLE Seats (
    seat_id INT PRIMARY KEY AUTO_INCREMENT,
    hall_id INT NOT NULL,
    row_number INT NOT NULL,
    seat_number INT NOT NULL,
    is_occupied BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (hall_id) REFERENCES Halls(hall_id)
);

CREATE TABLE Tickets (
    ticket_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT,
    session_id INT,
    total_price DECIMAL(10, 2), -- with discount and tax
    discount DECIMAL(5, 2),           -- Discount applied (e.g., age-based discount)
    tax DECIMAL(5, 2),                -- Tax amount (e.g., 20% on ticket price)
    date_of_purchase DATETIME,
    FOREIGN KEY (customer_id) REFERENCES Customers(customer_id),
    FOREIGN KEY (session_id) REFERENCES Sessions(session_id),
    FOREIGN KEY (discount_id) REFERENCES AgeBasedDiscounts(discount_id)
);


-- Create Products Table
CREATE TABLE Products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    category ENUM('Beverage', 'Snack', 'Toy') NOT NULL,
    description TEXT,
    image VARCHAR(255),
    stock INT NOT NULL DEFAULT 10,
    discount DECIMAL(5, 2),           -- Discount applied (e.g., age-based discount)
    tax DECIMAL(5, 2) DEFAULT 0.00,  -- Tax (10% on product price)
    FOREIGN KEY (discount_id) REFERENCES AgeBasedDiscounts(discount_id)
);

-- Create Revenue Table
CREATE TABLE Revenue (
    revenue_id INT PRIMARY KEY AUTO_INCREMENT,
    total_revenue DECIMAL(10, 2),
    total_tax DECIMAL(10, 2),
    date DATE
);

-- Create AgeBasedDiscounts Table
CREATE TABLE AgeBasedDiscounts (
    discount_id INT PRIMARY KEY AUTO_INCREMENT,
    age_min INT,
    age_max INT,
    discount_percentage DECIMAL(5, 2)
);

-- Insert sample data into Roles Table
CREATE TABLE Roles (
    role_id INT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) UNIQUE NOT NULL
);

INSERT INTO Roles (role_name) VALUES ('cashier'), ('admin'), ('manager');


CREATE TABLE ShoppingCart (
    cart_id INT PRIMARY KEY AUTO_INCREMENT,
    session_id INT,                  -- Nullable for non-seat items
    seat_id INT,                     -- Nullable for non-seat items
    product_id INT,                  -- Nullable for seats
    quantity INT NOT NULL CHECK (quantity > 0),
    customer_id INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (session_id) REFERENCES Sessions(session_id) ON DELETE CASCADE,
    FOREIGN KEY (seat_id) REFERENCES Seats(seat_id) ON DELETE SET NULL,
    FOREIGN KEY (product_id) REFERENCES Products(product_id) ON DELETE SET NULL,
    FOREIGN KEY (customer_id) REFERENCES Customers(customer_id) ON DELETE CASCADE
);


-- Create OrderItems Table
CREATE TABLE OrderItems (
    order_item_id INT PRIMARY KEY AUTO_INCREMENT,
    cart_id INT,
    product_id INT,
    quantity INT,
    total_price DECIMAL(10, 2),
    FOREIGN KEY (cart_id) REFERENCES ShoppingCart(cart_id),
    FOREIGN KEY (product_id) REFERENCES Products(product_id)
);

-- Create Invoices Table
CREATE TABLE Invoices (
    invoice_id INT PRIMARY KEY AUTO_INCREMENT,
    cart_id INT NOT NULL,
    invoice_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10, 2) NOT NULL,
    total_tax DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL DEFAULT 'cash',
    FOREIGN KEY (cart_id) REFERENCES ShoppingCart(cart_id),
    invoice_pdf_path VARCHAR(255),
    ticket_pdf_path VARCHAR(255)
);

