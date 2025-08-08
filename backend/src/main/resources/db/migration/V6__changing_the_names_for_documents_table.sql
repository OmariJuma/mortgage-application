DROP TABLE IF EXISTS documents;

-- Recreate documents table to match the updated entity
CREATE TABLE documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    application_id UUID NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    size BIGINT NOT NULL,
    url TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_application FOREIGN KEY (application_id) REFERENCES applications (id) ON DELETE CASCADE
);

ALTER TABLE applications
DROP COLUMN IF EXISTS document_ids;

-- Drop UNIQUE constraint from national_id if it exists
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.constraint_column_usage
        WHERE table_name = 'applications'
        AND column_name = 'national_id'
    ) THEN
        ALTER TABLE applications
        DROP CONSTRAINT IF EXISTS applications_national_id_key;
    END IF;
END
$$;

-- Reapply national_id as NOT NULL, non-unique
ALTER TABLE applications
ALTER COLUMN national_id DROP NOT NULL,
ALTER COLUMN national_id SET DATA TYPE VARCHAR(20);
