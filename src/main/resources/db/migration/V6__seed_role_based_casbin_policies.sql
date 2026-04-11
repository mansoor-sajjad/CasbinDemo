INSERT INTO users (username, password, tenant_id)
VALUES ('alice@example.com', '$2y$10$lFHUzb4epg/Ierh5Ncbo1.FRdvhuCHLGNs/uMJ/XScelR3iP802J6', 'DEFAULT')
ON CONFLICT (username) DO NOTHING;

INSERT INTO casbin_rule (ptype, v0, v1, v2, v3) VALUES
    ('p', 'ADMIN', 'DEFAULT', 'shipment', 'read'),
    ('p', 'ADMIN', 'DEFAULT', 'shipment', 'write'),
    ('p', 'ADMIN', 'DEFAULT', 'driver', 'read'),
    ('p', 'ADMIN', 'DEFAULT', 'driver', 'write'),
    ('p', 'OPERATOR', 'DEFAULT', 'shipment', 'read'),
    ('p', 'OPERATOR', 'DEFAULT', 'shipment', 'write'),
    ('p', 'OPERATOR', 'DEFAULT', 'driver', 'read'),
    ('p', 'OPERATOR', 'DEFAULT', 'driver', 'write'),
    ('p', 'DRIVER', 'DEFAULT', 'shipment', 'read'),
    ('p', 'DRIVER', 'DEFAULT', 'driver', 'read');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
VALUES ('g', 'alice@example.com', 'ADMIN', 'DEFAULT');
