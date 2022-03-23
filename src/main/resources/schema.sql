DROP TABLE IF EXISTS content;
DROP TABLE IF EXISTS publisher;
CREATE TABLE publisher (
    id VARCHAR(250) PRIMARY KEY,
    user_id INT NOT NULL UNIQUE
);
CREATE TABLE content (
    id SERIAL,
    publisher_id VARCHAR(250) NOT NULL,
    content VARCHAR(500) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (publisher_id) REFERENCES publisher(id)
);