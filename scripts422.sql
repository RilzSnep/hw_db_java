
CREATE TABLE car (
    id SERIAL PRIMARY KEY,
    brand VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    price DECIMAL(10, 2) NOT NULL
);


CREATE TABLE person (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    has_driver_license BOOLEAN NOT NULL,
    car_id INT NOT NULL,
    FOREIGN KEY (car_id) REFERENCES car(id)
);