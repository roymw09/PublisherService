DROP TABLE IF EXISTS content;
DROP TABLE IF EXISTS publisher;
CREATE TABLE publisher (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL UNIQUE
);
CREATE TABLE content (
    id SERIAL PRIMARY KEY,
    publisher_id INT NOT NULL,
    content VARCHAR(500) NOT NULL,
    FOREIGN KEY (publisher_id) REFERENCES publisher(id)
);