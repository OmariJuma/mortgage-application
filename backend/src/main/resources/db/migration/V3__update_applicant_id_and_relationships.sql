-- Change `applicant_id` type to UUID in `applications` table
ALTER TABLE applications DROP CONSTRAINT IF EXISTS fk_applicant_user;
ALTER TABLE applications ALTER COLUMN applicant_id TYPE UUID USING applicant_id::uuid;

ALTER TABLE applications ALTER COLUMN applicant_id SET NOT NULL;
