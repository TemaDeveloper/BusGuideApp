CREATE TABLE organizators (
    id SERIAL PRIMARY KEY,
    avatar_img BYTEA,
    name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    regular_number VARCHAR(255),
    email VARCHAR(255),
    whatsapp_number VARCHAR(255),
    tg_tag VARCHAR(255)
);
