CREATE DATABASE IF NOT EXISTS department_store_inventory;

USE department_store_inventory;

CREATE TABLE IF NOT EXISTS suppliers (
    supplier_id VARCHAR(50) PRIMARY KEY,
    address VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    contact_name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS products (
    product_id VARCHAR(50) PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    description TEXT,
    product_price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    supplier_id VARCHAR(50) NOT NULL,
    CONSTRAINT fk_products_supplier
        FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS inventory_items (
    inventory_id VARCHAR(50) PRIMARY KEY,
    product_id VARCHAR(50) NOT NULL,
    color VARCHAR(50) NOT NULL,
    size VARCHAR(50) NOT NULL,
    quantity_on_hand INT NOT NULL,
    reorder_level INT NOT NULL,
    CONSTRAINT fk_inventory_product
        FOREIGN KEY (product_id) REFERENCES products(product_id)
        ON UPDATE CASCADE,
    CONSTRAINT chk_inventory_quantity
        CHECK (quantity_on_hand >= 0),
    CONSTRAINT chk_inventory_reorder
        CHECK (reorder_level >= 0)
);

CREATE TABLE IF NOT EXISTS employees (
    employee_id VARCHAR(50) PRIMARY KEY,
    employee_name VARCHAR(100) NOT NULL,
    employee_email VARCHAR(100) NOT NULL UNIQUE,
    employee_phone VARCHAR(30),
    role VARCHAR(50) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    account_status VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS purchase_orders (
    purchase_order_id VARCHAR(50) PRIMARY KEY,
    employee_id VARCHAR(50),
    order_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    order_status VARCHAR(30) NOT NULL,
    order_date DATE NOT NULL,
    CONSTRAINT fk_purchase_orders_employee
        FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS purchase_order_items (
    order_item_id VARCHAR(50) PRIMARY KEY,
    purchase_order_id VARCHAR(50) NOT NULL,
    inventory_id VARCHAR(50) NOT NULL,
    cost_per_item DECIMAL(10,2) NOT NULL,
    order_quantity INT NOT NULL,
    CONSTRAINT fk_poi_purchase_order
        FOREIGN KEY (purchase_order_id) REFERENCES purchase_orders(purchase_order_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_poi_inventory
        FOREIGN KEY (inventory_id) REFERENCES inventory_items(inventory_id)
        ON UPDATE CASCADE,
    CONSTRAINT chk_poi_quantity
        CHECK (order_quantity > 0),
    CONSTRAINT chk_poi_cost
        CHECK (cost_per_item >= 0)
);

CREATE TABLE IF NOT EXISTS shipments (
    shipment_id VARCHAR(50) PRIMARY KEY,
    purchase_order_id VARCHAR(50) NOT NULL,
    supplier_id VARCHAR(50) NOT NULL,
    shipment_date DATE NOT NULL,
    delivery_date DATE,
    shipment_status VARCHAR(30) NOT NULL,
    CONSTRAINT fk_shipments_purchase_order
        FOREIGN KEY (purchase_order_id) REFERENCES purchase_orders(purchase_order_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_shipments_supplier
        FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS shipment_items (
    shipment_item_id VARCHAR(50) PRIMARY KEY,
    shipment_id VARCHAR(50) NOT NULL,
    inventory_id VARCHAR(50) NOT NULL,
    shipment_quantity INT NOT NULL,
    CONSTRAINT fk_shipment_items_shipment
        FOREIGN KEY (shipment_id) REFERENCES shipments(shipment_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_shipment_items_inventory
        FOREIGN KEY (inventory_id) REFERENCES inventory_items(inventory_id)
        ON UPDATE CASCADE,
    CONSTRAINT chk_shipment_quantity
        CHECK (shipment_quantity > 0)
);