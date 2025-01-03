-- Als Postgres User:
--create user xxx PASSWORD 'mtcgdb';
--create database xxx with owner xxx;

-- Connect to mtcgdb database as mtcgdb user before executing the following table creation commands

-- Create the "users" table
 CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(100) NOT NULL,
                       coins INT DEFAULT 20,               -- Starting value of 20 coins
                       score INT DEFAULT 100,              -- Starting score of 100
                       token VARCHAR(255)                  -- Token can be NULL
);

-- Insert example data into the users table
INSERT INTO users (username, password, coins, score, token) VALUES ('ben_dover', 'plsdo:)', 20, 100, NULL);
INSERT INTO users (username, password, coins, score, token) VALUES ('joe_mama', 'haagoteem', 20, 100, NULL);
INSERT INTO users (username, password, coins, score, token) VALUES ('kim_jong_dos', 'bitchasslingling', 20, 100, NULL);


-- Verify the setup by selecting all data
--SELECT * FROM users;
