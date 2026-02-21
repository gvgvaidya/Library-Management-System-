# Library Management System

A console-based Java application that demonstrates OOP principles, design patterns, and a layered architecture for managing books, patrons, lending, reservations, and branches.

## Features
- Book CRUD and multi-strategy search (title/author/ISBN)
- Patron management with validation
- Lending with limits, loan period, and fine calculation
- Reservation queue (FIFO) with observer notifications
- Inventory reports (availability, overdue, most borrowed)
- Multi-branch support
- Recommendations based on borrowing history
- Configurable settings via `library.properties`

## Architecture (4-Layer)
- Layer 1: `main/` console UI (`Main.java`)
- Layer 2: `services/` business logic
- Layer 3: `repositories/` in-memory data access
- Layer 4: `models/` domain entities

## Project Structure
```
library-management-system/
├── src/
│   └── com/library/
│       ├── main/
│       ├── models/
│       ├── repositories/
│       ├── services/
│       ├── search/
│       ├── observer/
│       ├── validator/
│       ├── exception/
│       ├── util/
│       ├── config/
│       ├── enums/
│       └── constants/
├── resources/
│   └── library.properties
├── library.log
├── .gitignore
└── README.md
```

## Quick Start
Compile:

```powershell
javac -d out -sourcepath src (Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName })
```

Run:

```powershell
java -cp out com.library.main.Main
```

## Configuration
Edit `resources/library.properties`:

```
max.books.per.patron=5
loan.period.days=14
fine.per.day=1.00
email.notifications=true
```

## Sample Data
Loaded at startup in `Main.java`:
- 15 books across genres (programming, fiction, science, business)
- 5 patrons (PAT001–PAT005)
- 3 branches (Main, North, South)

## Console Menu (High Level)
- Book Management
- Patron Management
- Lending Operations
- Search Books (Strategy Pattern)
- Inventory & Reports
- Reservations (Observer Pattern)
- Branch Management
- Book Recommendations
- System Statistics

## Class Diagram (ASCII)
```
+------------------+        +------------------+        +------------------+
|      Main        | -----> |   Services       | -----> |  Repositories    |
+------------------+        +------------------+        +------------------+
        |                          |                          |
        v                          v                          v
+------------------+        +------------------+        +------------------+
|     Models       | <----- |   Domain Logic   | <----- | In-memory Store |
+------------------+        +------------------+        +------------------+

Models:
+-----------+     +--------+     +------------------+     +--------------+
|  Book     |     | Patron |     | BorrowingRecord  |     | Reservation  |
+-----------+     +--------+     +------------------+     +--------------+
        \               |                 |                       |
         \              |                 |                       |
          \             |                 |                       |
           \            |                 |                       |
            \           |                 |                       |
             \          |                 |                       |
              \         |                 |                       |
               \        |                 |                       |
                \       |                 |                       |
                 \      |                 |                       |
                  \     |                 |                       |
                   \    |                 |                       |
                    \   |                 |                       |
                     \  |                 |                       |
                      \ |                 |                       |
                       \|                 |                       |
                   +-------------------------------+
                   |        LibraryBranch          |
                   +-------------------------------+

Design Patterns:
- Strategy: SearchStrategy -> SearchByTitle/Author/ISBN -> BookSearchContext
- Observer: Subject/Observer -> ReservationNotifier -> PatronObserver
- Singleton: ConfigurationManager
- Repository: *Repository classes abstract data access
```

## How It Works (Flow Summary)
1. `Main` wires repositories and services, then preloads sample data.
2. Users interact via menu options (add/search/checkout/return/reserve).
3. Services enforce rules (limits, availability, validation, fines).
4. Repositories persist data in-memory.
5. When a book is returned, reservations trigger notifications.
6. Logs are written to `library.log`.

## Notes
- Data is in-memory; restarting the app resets data.
- You can add or modify sample data in `Main.java` (`preloadData()`).
