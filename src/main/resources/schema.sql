DROP TABLE IF EXISTS content;
DROP TABLE IF EXISTS publisher;
CREATE TABLE publisher (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL
);
CREATE TABLE content (
    id SERIAL,
    publisher_id INT NOT NULL,
    content VARCHAR(500) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (publisher_id) REFERENCES publisher(id)
);