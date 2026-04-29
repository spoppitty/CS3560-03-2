USE department_store_inventory;

INSERT INTO suppliers (supplier_id, address, email, name, contact_name)
VALUES
    ('SUP-201', '411 Summit Ave, Denver, CO', 'orders@northernwear.com', 'Northern Wear Co.', 'Taylor Brooks'),
    ('SUP-202', '88 Harbor Blvd, Long Beach, CA', 'sales@seasidefootwear.com', 'Seaside Footwear', 'Morgan Patel'),
    ('SUP-203', '102 Cedar St, Portland, OR', 'ops@evergreenacc.com', 'Evergreen Accessories', 'Riley Chen'),
    ('SUP-204', '74 Market St, Chicago, IL', 'wholesale@metrostyle.com', 'Metro Style Wholesale', 'Jordan Lee'),
    ('SUP-205', '219 Cotton Mill Rd, Atlanta, GA', 'fulfillment@cottonlane.com', 'Cotton Lane Basics', 'Avery Johnson'),
    ('SUP-206', '560 Rainier Ave, Seattle, WA', 'orders@trailheadouterwear.com', 'Trailhead Outerwear', 'Casey Nguyen'),
    ('SUP-207', '33 Meridian Pkwy, Columbus, OH', 'accounts@homelinenhub.com', 'Home Linen Hub', 'Sam Rivera')
ON DUPLICATE KEY UPDATE
    address = VALUES(address),
    email = VALUES(email),
    name = VALUES(name),
    contact_name = VALUES(contact_name);

INSERT INTO products (product_id, product_name, description, product_price, supplier_id)
VALUES
    ('PROD-1001', 'Alpine Parka', 'Insulated winter coat for front display', 89.99, 'SUP-201'),
    ('PROD-1002', 'Waterproof Hiking Boot', 'Water-resistant ankle boot with lug sole', 74.99, 'SUP-202'),
    ('PROD-1003', 'Silk Scarf', 'Giftable silk scarf with seasonal print', 29.99, 'SUP-203'),
    ('PROD-1004', 'Classic Denim Jacket', 'Medium-weight denim jacket for casual wear', 54.99, 'SUP-204'),
    ('PROD-1005', 'Cotton Crew Tee', 'Everyday cotton T-shirt in core colors', 14.99, 'SUP-205'),
    ('PROD-1006', 'Performance Rain Shell', 'Lightweight waterproof shell jacket', 64.99, 'SUP-206'),
    ('PROD-1007', 'Linen Sheet Set', 'Queen sheet set for home department', 69.99, 'SUP-207'),
    ('PROD-1008', 'Leather Crossbody Bag', 'Compact leather bag with adjustable strap', 79.99, 'SUP-203'),
    ('PROD-1009', 'Slim Chino Pant', 'Stretch chino pant for workwear display', 39.99, 'SUP-204'),
    ('PROD-1010', 'Fleece Hoodie', 'Soft pullover hoodie for activewear section', 34.99, 'SUP-205'),
    ('PROD-1011', 'Trail Running Sock 3-Pack', 'Moisture-wicking socks for footwear add-on sales', 12.99, 'SUP-202'),
    ('PROD-1012', 'Down Vest', 'Packable vest for transitional outerwear', 59.99, 'SUP-206')
ON DUPLICATE KEY UPDATE
    product_name = VALUES(product_name),
    description = VALUES(description),
    product_price = VALUES(product_price),
    supplier_id = VALUES(supplier_id);

INSERT INTO inventory_items (inventory_id, product_id, color, size, quantity_on_hand, reorder_level)
VALUES
    ('INV-1001', 'PROD-1001', 'Navy', 'L', 18, 8),
    ('INV-1002', 'PROD-1001', 'Black', 'M', 5, 8),
    ('INV-1003', 'PROD-1002', 'Brown', '9', 6, 10),
    ('INV-1004', 'PROD-1002', 'Black', '10', 22, 10),
    ('INV-1005', 'PROD-1003', 'Emerald', 'One Size', 3, 6),
    ('INV-1006', 'PROD-1004', 'Indigo', 'M', 14, 6),
    ('INV-1007', 'PROD-1004', 'Light Wash', 'S', 6, 6),
    ('INV-1008', 'PROD-1005', 'White', 'M', 72, 24),
    ('INV-1009', 'PROD-1005', 'Black', 'L', 19, 24),
    ('INV-1010', 'PROD-1006', 'Forest', 'M', 9, 12),
    ('INV-1011', 'PROD-1007', 'Ivory', 'Queen', 16, 8),
    ('INV-1012', 'PROD-1008', 'Tan', 'One Size', 4, 5),
    ('INV-1013', 'PROD-1009', 'Khaki', '32x32', 31, 12),
    ('INV-1014', 'PROD-1010', 'Heather Gray', 'XL', 8, 15),
    ('INV-1015', 'PROD-1011', 'Assorted', 'M', 44, 20),
    ('INV-1016', 'PROD-1012', 'Olive', 'L', 11, 10)
ON DUPLICATE KEY UPDATE
    product_id = VALUES(product_id),
    color = VALUES(color),
    size = VALUES(size),
    quantity_on_hand = VALUES(quantity_on_hand),
    reorder_level = VALUES(reorder_level);


INSERT INTO employees (employee_id, first_name, last_name, username, password, role, account_status)
VALUES
    ('MGR-1001', 'Admin', 'User', 'admin', '1234', 'MGR', 'ACTIVE'),
    ('EMP-1004', 'John', 'Doe', 'jdoe', '1234', 'EMP', 'ACTIVE'),
    ('EMP-1005', 'Jane', 'Smith', 'jsmith', '1234', 'EMP', 'ACTIVE'),
    ('EMP-1006', 'Alex', 'Brown', 'abrown', '1234', 'EMP', 'ACTIVE')
ON DUPLICATE KEY UPDATE
    first_name = VALUES(first_name),
    last_name = VALUES(last_name),
    password = VALUES(password),
    role = VALUES(role),
    account_status = VALUES(account_status);


INSERT INTO purchase_orders (purchase_order_id, employee_id, order_amount, order_status, order_date)
VALUES
    ('PO-3001', 'MGR-1001', 749.90, 'Shipped', DATE_SUB(CURRENT_DATE, INTERVAL 5 DAY)),
    ('PO-3002', 'MGR-1001', 299.90, 'Pending', DATE_SUB(CURRENT_DATE, INTERVAL 2 DAY)),
    ('PO-3003', 'EMP-1005', 204.93, 'Received', DATE_SUB(CURRENT_DATE, INTERVAL 12 DAY))
ON DUPLICATE KEY UPDATE
    employee_id = VALUES(employee_id),
    order_amount = VALUES(order_amount),
    order_status = VALUES(order_status),
    order_date = VALUES(order_date);

INSERT INTO purchase_order_items (order_item_id, purchase_order_id, inventory_id, cost_per_item, order_quantity)
VALUES
    ('POI-4001', 'PO-3001', 'INV-1003', 74.99, 10),
    ('POI-4002', 'PO-3002', 'INV-1005', 29.99, 10),
    ('POI-4003', 'PO-3003', 'INV-1011', 69.99, 2),
    ('POI-4004', 'PO-3003', 'INV-1015', 12.99, 5)
ON DUPLICATE KEY UPDATE
    purchase_order_id = VALUES(purchase_order_id),
    inventory_id = VALUES(inventory_id),
    cost_per_item = VALUES(cost_per_item),
    order_quantity = VALUES(order_quantity);

INSERT INTO shipments (shipment_id, purchase_order_id, supplier_id, shipment_date, delivery_date, shipment_status)
VALUES
    ('SHIP-5001', 'PO-3001', 'SUP-202', DATE_SUB(CURRENT_DATE, INTERVAL 3 DAY), NULL, 'Shipped'),
    ('SHIP-5002', 'PO-3002', 'SUP-203', CURRENT_DATE, NULL, 'Pending'),
    ('SHIP-5003', 'PO-3003', 'SUP-207', DATE_SUB(CURRENT_DATE, INTERVAL 10 DAY), DATE_SUB(CURRENT_DATE, INTERVAL 7 DAY), 'Received')
ON DUPLICATE KEY UPDATE
    purchase_order_id = VALUES(purchase_order_id),
    supplier_id = VALUES(supplier_id),
    shipment_date = VALUES(shipment_date),
    delivery_date = VALUES(delivery_date),
    shipment_status = VALUES(shipment_status);

INSERT INTO shipment_items (shipment_item_id, shipment_id, inventory_id, shipment_quantity)
VALUES
    ('SHI-6001', 'SHIP-5001', 'INV-1003', 10),
    ('SHI-6002', 'SHIP-5002', 'INV-1005', 10),
    ('SHI-6003', 'SHIP-5003', 'INV-1011', 2),
    ('SHI-6004', 'SHIP-5003', 'INV-1015', 5)
ON DUPLICATE KEY UPDATE
    shipment_id = VALUES(shipment_id),
    inventory_id = VALUES(inventory_id),
    shipment_quantity = VALUES(shipment_quantity);
