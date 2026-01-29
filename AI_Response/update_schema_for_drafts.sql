-- SQL Script to update book_clubs table to allow NULL values for Draft support
-- Please run this in your Database Management Tool (e.g., SSMS, DBeaver)

-- 1. Allow NULL for category_id
ALTER TABLE book_clubs ALTER COLUMN category_id INT NULL;

-- 2. Allow NULL for event_date
ALTER TABLE book_clubs ALTER COLUMN event_date DATETIME2 NULL;

-- 3. Allow NULL for deadline
ALTER TABLE book_clubs ALTER COLUMN deadline DATETIME2 NULL;

-- 4. Allow NULL for location
ALTER TABLE book_clubs ALTER COLUMN location NVARCHAR(200) NULL;

-- Note: The data types (INT, DATETIME2, NVARCHAR) assume a standard SQL Server mapping.
-- If your database uses different types (e.g., MySQL), please adjust accordingly:
-- MySQL Example:
-- ALTER TABLE book_clubs MODIFY category_id INT NULL;
-- ALTER TABLE book_clubs MODIFY event_date DATETIME NULL;
-- ...
