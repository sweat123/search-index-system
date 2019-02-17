CREATE TABLE user_desc (
  id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  address VARCHAR(512),
  weight FLOAT
);

INSERT INTO user_desc (`name`, address, weight)
VALUES (
  "user1", "address1", 50.0
  "user2", "address2", 51.2
  "user3", "address3", 55.0
);
