DROP TABLE IF EXISTS MAIL_ORDER_SALES;

CREATE TABLE mail_order_sales
(
    mail_order_sales_number VARCHAR(255) PRIMARY KEY,
    name                    VARCHAR(255),
    tax_payer_number        VARCHAR(255),
    corporation_number      VARCHAR(255),
    district_code           VARCHAR(255)
);