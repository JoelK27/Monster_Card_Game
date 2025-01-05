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

-- Create the "cards" table
CREATE TABLE cards (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    damage INT NOT NULL,
    element_type VARCHAR(50) NOT NULL,
    card_type VARCHAR(50) NOT NULL CHECK (card_type IN ('Monster', 'Spell')),
    monster_type VARCHAR(50),           -- Specific to Monster cards
    spell_effect VARCHAR(50)            -- Specific to Spell cards
);

-- Create the "packages" table
CREATE TABLE packages (
    id SERIAL PRIMARY KEY,
    card1 UUID REFERENCES cards(id),
    card2 UUID REFERENCES cards(id),
    card3 UUID REFERENCES cards(id),
    card4 UUID REFERENCES cards(id),
    card5 UUID REFERENCES cards(id)
);

-- Insert example data into the users table
INSERT INTO users (username, password, coins, score, token) VALUES ('ben_dover', 'plsdo:)', 20, 100, NULL);
INSERT INTO users (username, password, coins, score, token) VALUES ('joe_mama', 'haagoteem', 20, 100, NULL);
INSERT INTO users (username, password, coins, score, token) VALUES ('kim_jong_dos', 'bitchasslingling', 20, 100, NULL);

-- Insert example data into the cards table
INSERT INTO cards (id, name, damage, element_type, card_type, monster_type) VALUES ('845f0dc7-37d0-426e-994e-43fc3ac83c08', 'WaterGoblin', 10, 'Water', 'Monster', 'Goblin');
INSERT INTO cards (id, name, damage, element_type, card_type, monster_type) VALUES ('99f8f8dc-e25e-4a95-aa2c-782823f36e2a', 'Dragon', 50, 'Fire', 'Monster', 'Dragon');
INSERT INTO cards (id, name, damage, element_type, card_type, spell_effect) VALUES ('e85e3976-7c86-4d06-9a80-641c2019a79f', 'WaterSpell', 20, 'Water', 'Spell', 'Splash');
INSERT INTO cards (id, name, damage, element_type, card_type, monster_type) VALUES ('1cb6ab86-bdb2-47e5-b6e4-68c5ab389334', 'Ork', 45, 'Earth', 'Monster', 'Ork');
INSERT INTO cards (id, name, damage, element_type, card_type, spell_effect) VALUES ('dfdd758f-649c-40f9-ba3a-8657f4b3439f', 'FireSpell', 25, 'Fire', 'Spell', 'Burn');
INSERT INTO cards (id, name, damage, element_type, card_type, monster_type) VALUES ('644808c2-f87a-4600-b313-122b02322fd5', 'WaterGoblin', 9, 'Water', 'Monster', 'Goblin');
INSERT INTO cards (id, name, damage, element_type, card_type, monster_type) VALUES ('4a2757d6-b1c3-47ac-b9a3-91deab093531', 'Dragon', 55, 'Fire', 'Monster', 'Dragon');
INSERT INTO cards (id, name, damage, element_type, card_type, spell_effect) VALUES ('91a6471b-1426-43f6-ad65-6fc473e16f9f', 'WaterSpell', 21, 'Water', 'Spell', 'Splash');
INSERT INTO cards (id, name, damage, element_type, card_type, monster_type) VALUES ('4ec8b269-0dfa-4f97-809a-2c63fe2a0025', 'Ork', 55, 'Earth', 'Monster', 'Ork');
INSERT INTO cards (id, name, damage, element_type, card_type, spell_effect) VALUES ('f8043c23-1534-4487-b66b-238e0c3c39b5', 'WaterSpell', 23, 'Water', 'Spell', 'Splash');
INSERT INTO cards (id, name, damage, element_type, card_type, spell_effect) VALUES ('b017ee50-1c14-44e2-bfd6-2c0c5653a37c', 'WaterGoblin', 23, 'Water', 'Spell', 'Splash');

-- Insert example data into the packages table
INSERT INTO packages (card1, card2, card3, card4, card5) VALUES 
('845f0dc7-37d0-426e-994e-43fc3ac83c08', '99f8f8dc-e25e-4a95-aa2c-782823f36e2a', 'e85e3976-7c86-4d06-9a80-641c2019a79f', '1cb6ab86-bdb2-47e5-b6e4-68c5ab389334', 'dfdd758f-649c-40f9-ba3a-8657f4b3439f');

-- Verify the setup by selecting all data
--SELECT * FROM users;
--SELECT * FROM cards;
--SELECT * FROM packages;