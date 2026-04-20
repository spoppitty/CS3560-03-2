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
