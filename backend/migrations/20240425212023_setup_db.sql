CREATE TABLE users (
    id SERIAL PRIMARY KEY,

    avatar BYTEA,
    name   VARCHAR(255) NOT NULL,

    email    VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    is_admin BOOLEAN NOT NULL
);

CREATE TABLE trips (
    id SERIAL PRIMARY KEY,

    image BYTEA,
    title VARCHAR(255) NOT NULL,
    price INT NOT NULL,
    plan TEXT NOT NULL,
    category VARCHAR(255) NOT NULL,
    pick_up_points TEXT[] NOT NULL
);

CREATE TABLE reservations (
    id SERIAL PRIMARY KEY,

    price INT NOT NULL,
    num_people INT NOT NULL,
    date TEXT NOT NULL,

    user_id INT NOT NULL, 
    FOREIGN KEY (user_id) REFERENCES users(id),

    trip_id INT NOT NULL,
    FOREIGN KEY (trip_id) REFERENCES trips(id)
);

CREATE TABLE reviews (
    id SERIAL PRIMARY KEY,
    rating INT NOT NULL,
    description TEXT NOT NULL,

    trip_id INT NOT NULL,
    FOREIGN KEY (trip_id) REFERENCES trips(id),

    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE organizators (
    id SERIAL PRIMARY KEY,
    avatar_img BYTEA,
    name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    regular_number VARCHAR(255),
    email VARCHAR(255),
    whatsapp_number VARCHAR(255),
    tg_tag VARCHAR(255),
    viber_number VARCHAR(255),

    trip_id INT NOT NULL,
    FOREIGN KEY (trip_id) REFERENCES trips(id)
);
