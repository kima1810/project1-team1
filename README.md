# Fifty/50 Bank

Fifty/50 Bank is a modern CLI-based banking application that lets clients of Fifty/50 Bank perform basic banking tasks — depositing, withdrawing, and transferring money — as well as registering and opening new accounts.

## Features

- Create and register user accounts
- Open and manage multiple bank accounts per user
- Deposit money
- Withdraw money
- Transfer money
- Support for checking and savings accounts
- PIN-based account access
- SQLite database for persistent data storage
- Command-line interface

## Prerequisites

- **Java Development Kit (JDK 21.0.12.1)** installed and on your `PATH`.
- **SQLite** and a SQL client of your choice for running the setup scripts.

> **Note:** Maven does **not** need to be installed separately. This project uses the Maven Wrapper (`mvnw` / `mvnw.cmd`), which is included in the repository.


## Getting Started

### 1. Initialize the database

The SQL scripts are located in the `/data/` folder.

Run `InitializeDatabase.sql` in the SQL program of your choice to initialize a SQLite database.

Optionally, run `SampleData.sql` to populate the database with sample records for testing/debugging.

#### Sample data

| Username    | Password   | Account(s)                      | PIN    |
|-------------|------------|---------------------------------|--------|
| `billy`     | `password` | CHECKING `5756`                 | `1234` |
| `johndoe`   | `password` | SAVINGS `8512`                  | `9876` |
| `sally`     | `password` | CHECKING `2739`                 | `4567` |
| `slagathor` | `password` | SAVINGS `5205`, CHECKING `0105` | `1111` |

> These credentials are intended for testing and debugging purposes only.

### 2. Set the database environment variable

Create an environment variable pointing to your database:

```
DATABASE_URL=jdbc:sqlite:<path_to_db>
```

**Windows (PowerShell)**

- Per session:
  ```powershell
  $env:DATABASE_URL="jdbc:sqlite:<path_to_db>"
  ```
- Permanent: add `DATABASE_URL` to your system environment variables.

**macOS / Linux**

- Per session:
  ```bash
  export DATABASE_URL=jdbc:sqlite:<path_to_db>
  ```
- Permanent: add the `export` line to your shell profile (e.g. `~/.bashrc` or `~/.zshrc`).

### 3. Compile the code

From a terminal, navigate to the root of the project directory.

**Windows (PowerShell)**

```powershell
.\mvnw.cmd clean compile
```

**macOS / Linux**

```bash
./mvnw clean compile
```

### 4. Run the Program

After compiling the project, run the application from the root directory.

#### Windows

```powershell
.\mvnw.cmd -q exec:java "-Dexec.mainClass=org.half.Main"
```

#### macOS / Linux

```bash
./mvnw -q exec:java "-Dexec.mainClass=org.half.Main"
```

The Fifty/50 Bank CLI application should then start in your terminal.

## Database

Fifty/50 Bank uses **SQLite** for data persistence.

The database location is configured through the `DATABASE_URL` environment variable:

```text
DATABASE_URL=jdbc:sqlite:<path_to_db>
```
This allows the application to connect to different database files without changing the source code.

## Team Half & Half
### Contributors

- [@AbishekSh](https://github.com/AbishekSh)
- [@Darsh-KP](https://github.com/Darsh-KP)
- [@gaving747](https://github.com/gaving747)
- [@jackhannan](https://github.com/jackhannan)
- [@kima1810](https://github.com/kima1810)
- [@TuanDinh1233](https://github.com/TuanDinh1233)
