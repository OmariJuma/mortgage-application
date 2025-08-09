DROP TABLE IF EXISTS documents;
CREATE TABLE documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    application_id UUID NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    document_type VARCHAR(100) NOT NULL,
    presigned_url TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_application FOREIGN KEY (application_id) REFERENCES applications (id) ON DELETE CASCADE
);

ALTER TABLE applications
DROP COLUMN IF EXISTS document_ids;
