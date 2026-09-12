DROP TABLE IF EXISTS TransactionHistory;
DROP TABLE IF EXISTS Account;
DROP TABLE IF EXISTS User;

CREATE TABLE User(
    username TEXT PRIMARY KEY NOT NULL
        CHECK(length(username) BETWEEN 5 AND 50)
        COLLATE BINARY,
    passwordHash TEXT NOT NULL
        CHECK(length(passwordHash) <= 255),
    firstName TEXT NOT NULL
        CHECK(length(firstName) <= 20),
    lastName TEXT NOT NULL
        CHECK(length(lastName) <= 20),
    email TEXT UNIQUE NOT NULL
        CHECK(email LIKE '%@%.%'),
    phoneNumber TEXT NOT NULL
        CHECK (phoneNumber NOT GLOB '*[^0-9+() -]*')
);

CREATE TABLE Account(
    accountNumber INTEGER PRIMARY KEY NOT NULL
        CHECK(length(accountNumber) = 12),
    pinHash TEXT NOT NULL
        CHECK(length(pinHash) <= 255),
    accountType TEXT NOT NULL
    	CHECK(accountType IN ('CHECKING', 'SAVINGS')),
    balance REAL DEFAULT(0.00)
        CHECK(balance >= 0.00),
    username TEXT REFERENCES User(username)
        COLLATE BINARY
);

CREATE TABLE TransactionHistory(
    transactionId INTEGER PRIMARY KEY NOT NULL,
    dateTime TEXT DEFAULT CURRENT_TIMESTAMP,
    type TEXT NOT NULL
        CHECK(type IN ('Deposit', 'Withdraw', 'Transfer')),
    amount REAL NOT NULL
        CHECK(amount >= 0.00),
    originAccountNumber INTEGER REFERENCES Account(accountNumber),
    destinationAccountNumber INTEGER REFERENCES Account(accountNumber)
);