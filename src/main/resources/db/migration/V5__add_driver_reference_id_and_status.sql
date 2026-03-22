ALTER TABLE driver
ADD COLUMN reference_id VARCHAR(255),
ADD COLUMN is_active BOOLEAN DEFAULT TRUE;

UPDATE driver SET reference_id = gen_random_uuid()::text WHERE reference_id IS NULL;

ALTER TABLE driver ALTER COLUMN reference_id SET NOT NULL;
ALTER TABLE driver ADD CONSTRAINT uk_driver_reference_id UNIQUE (reference_id);
