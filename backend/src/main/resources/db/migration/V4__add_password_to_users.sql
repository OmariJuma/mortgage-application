-- V4__add_password_to_users.sql

-- Add password column to users table
ALTER TABLE users ADD COLUMN password VARCHAR(255) NOT NULL DEFAULT '';

-- Remove the default after adding the column to ensure all existing records have a password
ALTER TABLE users ALTER COLUMN password DROP DEFAULT;
