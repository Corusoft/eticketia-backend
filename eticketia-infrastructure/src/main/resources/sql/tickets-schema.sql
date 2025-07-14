DROP SCHEMA IF EXISTS tickets CASCADE;
CREATE SCHEMA tickets;

CREATE TABLE IF NOT EXISTS tickets.MindeeReceiptRawData (
  ticket_id                     BIGSERIAL,
  category                      VARCHAR(50),
  subcategory                   VARCHAR(50),
  document_type                 VARCHAR(30),
  purchase_date                 DATE,
  purchase_time                 TIME,
  line_items                    JSONB,
  locale                        VARCHAR(5),
  receipt_number                VARCHAR(50),
  supplier_name                 VARCHAR(100),
  country                       VARCHAR(50),        -- ISO3166-1-alpha-3
  currency                      VARCHAR(10),        -- ISO4217
  total_net                     NUMERIC(8, 2),      -- Up to 999_999.99
  total_amount                  NUMERIC(8, 2),      -- Up to 999_999.99
  total_tax                     NUMERIC(8, 2),      -- Up to 999_999.99
  taxes                         JSONB,
  raw_ocr_response              JSONB NOT NULL,
  scan_timestamp                TIMESTAMPTZ NOT NULL,
  uploaded_image_url            VARCHAR(2048) NOT NULL,
  uploaded_image_timestamp      TIMESTAMPTZ,
  user_id                       VARCHAR NOT NULL,

  CONSTRAINT PK_MindeeReceiptRawData PRIMARY KEY (ticket_id)
);

ALTER TABLE tickets.MindeeReceiptRawData ADD CONSTRAINT FK_MindeeReceiptRawData_to_UserTable
    FOREIGN KEY (user_id) REFERENCES users.UserTable(uid);
