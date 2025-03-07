-- Als Postgres User:
--create user xxx PASSWORD 'mtcgdb';
--create database xxx with owner xxx;

-- Connect to mtcgdb database as mtcgdb user before executing the following table creation commands

-- Create the "users" table
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(100),
    bio TEXT,
    image VARCHAR(255),
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
    spell_effect VARCHAR(50),            -- Specific to Spell cards
    owner_id INTEGER REFERENCES users(id) ON DELETE SET NULL,
    deck BOOLEAN DEFAULT FALSE
);

-- Tabelle für Benutzerkarten
CREATE TABLE user_cards (
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    card_id UUID REFERENCES cards(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, card_id)
);

-- Tabelle für Benutzerdecks
CREATE TABLE user_deck (
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    card_id UUID REFERENCES cards(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, card_id)
);

CREATE TABLE user_packages (
    user_id INT NOT NULL,
    package_id UUID NOT NULL,
    PRIMARY KEY (user_id, package_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (package_id) REFERENCES packages(id)
);

-- Tabelle für Pakete
CREATE TABLE packages (
    id UUID PRIMARY KEY,
    card1 UUID REFERENCES cards(id),
    card2 UUID REFERENCES cards(id),
    card3 UUID REFERENCES cards(id),
    card4 UUID REFERENCES cards(id),
    card5 UUID REFERENCES cards(id)
);

-- Verbindungstabelle für Karten und Pakete
CREATE TABLE package_cards (
    package_id UUID REFERENCES packages(id) ON DELETE CASCADE,
    card_id UUID REFERENCES cards(id) ON DELETE CASCADE,
    PRIMARY KEY (package_id, card_id)
);

-- Tabelle für Handelsangebote
CREATE TABLE trading_deals (
    id UUID PRIMARY KEY,
    card_to_trade UUID REFERENCES cards(id) ON DELETE CASCADE,
    type VARCHAR(20) CHECK (type IN ('monster', 'spell')),
    minimum_damage FLOAT NOT NULL,
    owner_id INTEGER REFERENCES users(id) ON DELETE CASCADE
);

-- Tabelle für Schlachtstatistiken
CREATE TABLE battles (
    id SERIAL PRIMARY KEY,
    player1_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    player2_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    winner_id INTEGER REFERENCES users(id) ON DELETE SET NULL,
    battle_log TEXT
);

ALTER TABLE user_cards DROP CONSTRAINT user_cards_pkey;
ALTER TABLE user_cards ADD COLUMN id SERIAL PRIMARY KEY;

ALTER TABLE user_packages DROP CONSTRAINT user_packages_pkey;
ALTER TABLE user_packages ADD COLUMN id SERIAL PRIMARY KEY;

-- Verify the setup by selecting all data
--SELECT * FROM users;
--SELECT * FROM cards;
--SELECT * FROM packages;