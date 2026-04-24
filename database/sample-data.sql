USE department_store_inventory;

INSERT INTO suppliers (supplier_id, address, email, name, contact_name)
VALUES
    ('SUP-201', '411 Summit Ave, Denver, CO', 'orders@northernwear.com', 'Northern Wear', 'Taylor Brooks'),
    ('SUP-202', '88 Harbor Blvd, Long Beach, CA', 'sales@seasidefoot.com', 'Seaside Footwear', 'Morgan Patel'),
    ('SUP-203', '102 Cedar St, Portland, OR', 'ops@evergreenacc.com', 'Evergreen Accessories', 'Riley Chen')
ON DUPLICATE KEY UPDATE
    address = VALUES(address),
    email = VALUES(email),
    name = VALUES(name),
    contact_name = VALUES(contact_name);

INSERT INTO products (product_id, product_name, description, supplier_id)
VALUES
    ('PROD-1001', 'Winter Parka', 'Insulated coat for seasonal display', 'SUP-201'),
    ('PROD-1002', 'Leather Boots', 'Water-resistant ankle boots', 'SUP-202'),
    ('PROD-1003', 'Silk Scarf', 'Luxury scarf for gift section', 'SUP-203')
ON DUPLICATE KEY UPDATE
    product_name = VALUES(product_name),
    description = VALUES(description),
    supplier_id = VALUES(supplier_id);

INSERT INTO inventory_items (inventory_id, product_id, color, size, quantity_on_hand, reorder_level)
VALUES
    ('INV-1001', 'PROD-1001', 'Navy', 'L', 12, 10),
    ('INV-1002', 'PROD-1002', 'Brown', '9', 6, 8),
    ('INV-1003', 'PROD-1003', 'Emerald', 'One Size', 18, 7)
ON DUPLICATE KEY UPDATE
    product_id = VALUES(product_id),
    color = VALUES(color),
    size = VALUES(size),
    quantity_on_hand = VALUES(quantity_on_hand),
    reorder_level = VALUES(reorder_level);

INSERT INTO employees (employee_id, first_name, last_name, username, password, role)
VALUES
    ('MGR-1001', 'Admin', 'User', 'admin', '1234', 'MGR'),
    ('EMP-1004', 'John', 'Doe', 'jdoe', '1234', 'EMP'),
    ('EMP-1005', 'Jane', 'Smith', 'jsmith', '1234', 'EMP'),
    ('EMP-1006', 'Alex', 'Brown', 'abrown', '1234', 'EMP')
ON DUPLICATE KEY UPDATE
    first_name = VALUES(first_name),
    last_name = VALUES(last_name),
    password = VALUES(password),
    role = VALUES(role);