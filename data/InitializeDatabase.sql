DROP TABLE IF EXISTS TransactionHistory;
DROP TABLE IF EXISTS Account;
DROP TABLE IF EXISTS User;

CREATE TABLE User(
    username TEXT PRIMARY KEY NOT NULL
        CHECK(length(username) BETWEEN 5 AND 50)
        COLLATE BINARY,
    passwordHash TEXT NOT NULL
        CHECK(length(passwordHash) <= 255),
    fName TEXT NOT NULL
        CHECK(length(fName) <= 20),
    lName TEXT NOT NULL
        CHECK(length(lName) <= 20),
    email TEXT UNIQUE NOT NULL
        CHECK(email LIKE '%@%.%'),
    phone INTEGER NOT NULL
        CHECK(length(phone) = 10)
);

CREATE TABLE Account(
    accountId INTEGER PRIMARY KEY NOT NULL
        CHECK(length(accountId) = 12),
    pin INTEGER NOT NULL
        CHECK(length(pin) = 4),
    balance REAL DEFAULT(0.00)
        CHECK(balance >= 0.00),
    user TEXT REFERENCES User(username)
        COLLATE BINARY
);

CREATE TABLE TransactionHistory(
    transactionId INTEGER PRIMARY KEY NOT NULL,
    dateTime TEXT DEFAULT CURRENT_TIMESTAMP,
    type TEXT NOT NULL
        CHECK(type IN ('Deposit', 'Withdraw', 'Transfer')),
    amount REAL NOT NULL
        CHECK(amount >= 0.00),
    originAccountId INTEGER REFERENCES Account(accountId),
    destinationAccountId INTEGER REFERENCES Account(accountId)
);