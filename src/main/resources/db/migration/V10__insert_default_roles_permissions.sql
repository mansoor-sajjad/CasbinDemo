-- Insert default roles for DEFAULT tenant
INSERT INTO roles (name, description, tenant_id) VALUES
    ('ADMIN', 'Administrator with full access', 'DEFAULT'),
    ('OPERATOR', 'Operator with shipment and driver management', 'DEFAULT'),
    ('DRIVER', 'Driver with read-only and limited access', 'DEFAULT')
ON CONFLICT (name, tenant_id) DO NOTHING;

-- Insert default permissions for DEFAULT tenant
INSERT INTO permissions (name, description, resource, action, tenant_id) VALUES
    -- Shipment permissions
    ('shipment:read', 'Read shipments', 'shipment', 'read', 'DEFAULT'),
    ('shipment:create', 'Create shipments', 'shipment', 'create', 'DEFAULT'),
    ('shipment:update', 'Update shipments', 'shipment', 'update', 'DEFAULT'),
    ('shipment:delete', 'Delete shipments', 'shipment', 'delete', 'DEFAULT'),
    
    -- Driver permissions
    ('driver:read', 'Read drivers', 'driver', 'read', 'DEFAULT'),
    ('driver:create', 'Create drivers', 'driver', 'create', 'DEFAULT'),
    ('driver:update', 'Update drivers', 'driver', 'update', 'DEFAULT'),
    ('driver:delete', 'Delete drivers', 'driver', 'delete', 'DEFAULT'),
    
    -- User permissions
    ('user:read', 'Read users', 'user', 'read', 'DEFAULT'),
    ('user:create', 'Create users', 'user', 'create', 'DEFAULT'),
    ('user:update', 'Update users', 'user', 'update', 'DEFAULT'),
    ('user:delete', 'Delete users', 'user', 'delete', 'DEFAULT'),
    
    -- Role permissions
    ('role:read', 'Read roles', 'role', 'read', 'DEFAULT'),
    ('role:create', 'Create roles', 'role', 'create', 'DEFAULT'),
    ('role:update', 'Update roles', 'role', 'update', 'DEFAULT'),
    ('role:delete', 'Delete roles', 'role', 'delete', 'DEFAULT'),
    
    -- Permission permissions
    ('permission:read', 'Read permissions', 'permission', 'read', 'DEFAULT'),
    ('permission:create', 'Create permissions', 'permission', 'create', 'DEFAULT'),
    ('permission:update', 'Update permissions', 'permission', 'update', 'DEFAULT'),
    ('permission:delete', 'Delete permissions', 'permission', 'delete', 'DEFAULT')
ON CONFLICT (name, tenant_id) DO NOTHING;

-- Assign all permissions to ADMIN role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ADMIN' AND r.tenant_id = 'DEFAULT' AND p.tenant_id = 'DEFAULT'
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- Assign specific permissions to OPERATOR role (shipment and driver management)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'OPERATOR' AND r.tenant_id = 'DEFAULT' AND p.tenant_id = 'DEFAULT'
      AND p.name IN ('shipment:read', 'shipment:create', 'shipment:update', 'shipment:delete',
                      'driver:read', 'driver:create', 'driver:update', 'driver:delete')
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- Assign read-only permissions to DRIVER role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'DRIVER' AND r.tenant_id = 'DEFAULT' AND p.tenant_id = 'DEFAULT'
      AND p.name IN ('shipment:read', 'driver:read')
ON CONFLICT (role_id, permission_id) DO NOTHING;
