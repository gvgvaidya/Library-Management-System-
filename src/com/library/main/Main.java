package com.library.main;

import com.library.config.ConfigurationManager;
import com.library.constants.AppConstants;
import com.library.constants.ErrorMessages;
import com.library.enums.BookStatus;
import com.library.enums.Genre;
import com.library.exception.BookNotFoundException;
import com.library.exception.DuplicateRecordException;
import com.library.exception.InvalidEmailException;
import com.library.exception.InvalidISBNException;
import com.library.exception.InvalidPatronException;
import com.library.exception.PatronNotFoundException;
import com.library.models.Book;
import com.library.models.BorrowingRecord;
import com.library.models.LibraryBranch;
import com.library.models.Patron;
import com.library.models.Reservation;
import com.library.observer.ReservationNotifier;
import com.library.repositories.BookRepository;
import com.library.repositories.BorrowingRecordRepository;
import com.library.repositories.BranchRepository;
import com.library.repositories.PatronRepository;
import com.library.repositories.ReservationRepository;
import com.library.search.BookSearchContext;
import com.library.search.SearchByAuthor;
import com.library.search.SearchByISBN;
import com.library.search.SearchByTitle;
import com.library.services.BookService;
import com.library.services.BranchService;
import com.library.services.InventoryService;
import com.library.services.LendingService;
import com.library.services.PatronService;
import com.library.services.RecommendationService;
import com.library.services.ReservationService;
import com.library.util.InputReader;
import com.library.util.Logger;
import java.time.LocalDate;
import java.util.List;

public class Main {
    private final InputReader input = new InputReader();
    private final ConfigurationManager config = ConfigurationManager.getInstance();

    private final BookRepository bookRepository = new BookRepository();
    private final PatronRepository patronRepository = new PatronRepository();
    private final BorrowingRecordRepository recordRepository = new BorrowingRecordRepository();
    private final ReservationRepository reservationRepository = new ReservationRepository();
    private final BranchRepository branchRepository = new BranchRepository();
    private final ReservationNotifier reservationNotifier = new ReservationNotifier();

    private final BookService bookService = new BookService(bookRepository);
    private final PatronService patronService = new PatronService(patronRepository);
    private final ReservationService reservationService = new ReservationService(
        reservationRepository,
        bookRepository,
        reservationNotifier
    );
    private final LendingService lendingService = new LendingService(
        bookService,
        patronService,
        recordRepository,
        reservationService
    );
    private final InventoryService inventoryService = new InventoryService(bookService, recordRepository);
    private final RecommendationService recommendationService = new RecommendationService(recordRepository, bookService);
    private final BranchService branchService = new BranchService(branchRepository);

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        Logger.info("Library Management System starting...");
        Logger.info("Config loaded: " + config.getProperty("app.name", "Library Management System"));

        preloadData();
        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = input.readLine("Select option: ");
            if (choice == null) {
                break;
            }
            switch (choice.trim()) {
                case "1":
                    bookManagementMenu();
                    break;
                case "2":
                    patronManagementMenu();
                    break;
                case "3":
                    lendingOperationsMenu();
                    break;
                case "4":
                    searchMenu();
                    break;
                case "5":
                    inventoryMenu();
                    break;
                case "6":
                    reservationMenu();
                    break;
                case "7":
                    branchMenu();
                    break;
                case "8":
                    recommendationMenu();
                    break;
                case "9":
                    systemStats();
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println(ErrorMessages.INVALID_INPUT);
            }
        }

        input.close();
        Logger.info("Shutting down.");
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("Main Menu");
        System.out.println("1. Book Management");
        System.out.println("2. Patron Management");
        System.out.println("3. Lending Operations");
        System.out.println("4. Search Books");
        System.out.println("5. Inventory & Reports");
        System.out.println("6. Reservations");
        System.out.println("7. Branch Management");
        System.out.println("8. Book Recommendations");
        System.out.println("9. System Statistics");
        System.out.println("0. Exit");
    }

    private void bookManagementMenu() {
        System.out.println("\nBook Management");
        System.out.println("1. Add Book");
        System.out.println("2. Search Books");
        System.out.println("3. Update Book");
        System.out.println("4. Remove Book");
        String choice = input.readLine("Select option: ");
        if (choice == null) {
            return;
        }
        switch (choice.trim()) {
            case "1":
                addBook();
                break;
            case "2":
                searchMenu();
                break;
            case "3":
                updateBook();
                break;
            case "4":
                removeBook();
                break;
            default:
                System.out.println(ErrorMessages.INVALID_INPUT);
        }
    }

    private void addBook() {
        try {
            String isbn = input.readLine("ISBN: ");
            String title = input.readLine("Title: ");
            String author = input.readLine("Author: ");
            int year = readInt("Year: ");
            Genre genre = readGenre();
            Book book = new Book(isbn, title, author, year, genre, BookStatus.AVAILABLE, null);
            bookService.addBook(book);
            System.out.println("Book added.");
        } catch (InvalidISBNException | DuplicateRecordException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void updateBook() {
        try {
            String isbn = input.readLine("ISBN to update: ");
            Book existing = bookService.getBook(isbn);
            String title = input.readLine("Title (" + existing.getTitle() + "): ");
            String author = input.readLine("Author (" + existing.getAuthor() + "): ");
            int year = readInt("Year (" + existing.getYear() + "): ");
            Genre genre = readGenre();
            Book updated = new Book(
                existing.getIsbn(),
                title.isEmpty() ? existing.getTitle() : title,
                author.isEmpty() ? existing.getAuthor() : author,
                year == 0 ? existing.getYear() : year,
                genre == null ? existing.getGenre() : genre,
                existing.getStatus(),
                existing.getBorrowerId()
            );
            bookService.updateBook(updated);
            System.out.println("Book updated.");
        } catch (BookNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void removeBook() {
        try {
            String isbn = input.readLine("ISBN to remove: ");
            bookService.removeBook(isbn);
            System.out.println("Book removed.");
        } catch (BookNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void patronManagementMenu() {
        System.out.println("\nPatron Management");
        System.out.println("1. Register Patron");
        System.out.println("2. View Patron Details");
        System.out.println("3. Update Patron");
        String choice = input.readLine("Select option: ");
        if (choice == null) {
            return;
        }
        switch (choice.trim()) {
            case "1":
                registerPatron();
                break;
            case "2":
                viewPatron();
                break;
            case "3":
                updatePatron();
                break;
            default:
                System.out.println(ErrorMessages.INVALID_INPUT);
        }
    }

    private void registerPatron() {
        try {
            String id = input.readLine("Patron ID: ");
            String name = input.readLine("Name: ");
            String email = input.readLine("Email: ");
            String phone = input.readLine("Phone: ");
            Patron patron = new Patron(id, name, email, phone, true);
            patronService.registerPatron(patron);
            System.out.println("Patron registered.");
        } catch (DuplicateRecordException | InvalidEmailException | InvalidPatronException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void viewPatron() {
        try {
            String id = input.readLine("Patron ID: ");
            Patron patron = patronService.getPatron(id);
            System.out.println("Patron: " + patron.getName() + " (" + patron.getPatronId() + ")");
            System.out.println("Email: " + patron.getEmail());
            System.out.println("Phone: " + patron.getPhone());
            System.out.println("Active: " + patron.isActive());
            System.out.println("Borrowed count: " + patron.getBorrowedBooksCount());
        } catch (PatronNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void updatePatron() {
        try {
            String id = input.readLine("Patron ID to update: ");
            Patron existing = patronService.getPatron(id);
            String name = input.readLine("Name (" + existing.getName() + "): ");
            String email = input.readLine("Email (" + existing.getEmail() + "): ");
            String phone = input.readLine("Phone (" + existing.getPhone() + "): ");
            String activeInput = input.readLine("Active (true/false, " + existing.isActive() + "): ");
            boolean active = activeInput.isEmpty() ? existing.isActive() : Boolean.parseBoolean(activeInput);
            Patron updated = new Patron(
                existing.getPatronId(),
                name.isEmpty() ? existing.getName() : name,
                email.isEmpty() ? existing.getEmail() : email,
                phone.isEmpty() ? existing.getPhone() : phone,
                active
            );
            patronService.updatePatron(updated);
            System.out.println("Patron updated.");
        } catch (PatronNotFoundException | InvalidEmailException | InvalidPatronException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void lendingOperationsMenu() {
        System.out.println("\nLending Operations");
        System.out.println("1. Checkout Book");
        System.out.println("2. Return Book");
        System.out.println("3. View Active Borrowings");
        String choice = input.readLine("Select option: ");
        if (choice == null) {
            return;
        }
        switch (choice.trim()) {
            case "1":
                checkoutBook();
                break;
            case "2":
                returnBook();
                break;
            case "3":
                listActiveBorrowings();
                break;
            default:
                System.out.println(ErrorMessages.INVALID_INPUT);
        }
    }

    private void checkoutBook() {
        try {
            String patronId = input.readLine("Patron ID: ");
            String isbn = input.readLine("Book ISBN: ");
            BorrowingRecord record = lendingService.checkout(patronId, isbn);
            System.out.println("Checkout successful.");
            System.out.println("Due Date: " + record.getDueDate());
            System.out.println("Record ID: " + record.getRecordId());
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void returnBook() {
        try {
            String patronId = input.readLine("Patron ID: ");
            String isbn = input.readLine("Book ISBN: ");
            double fine = lendingService.returnBook(patronId, isbn);
            System.out.println("Book returned.");
            if (fine > 0) {
                System.out.println("Fine: $" + String.format("%.2f", fine));
            }
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void listActiveBorrowings() {
        List<BorrowingRecord> records = recordRepository.findActive();
        if (records.isEmpty()) {
            System.out.println("No active borrowings.");
            return;
        }
        for (BorrowingRecord record : records) {
            System.out.println(record.getRecordId() + " | " + record.getPatronId() + " | " + record.getBookIsbn()
                + " | Due: " + record.getDueDate());
        }
    }

    private void searchMenu() {
        System.out.println("\nSearch Books");
        System.out.println("1. By Title");
        System.out.println("2. By Author");
        System.out.println("3. By ISBN");
        String choice = input.readLine("Select option: ");
        if (choice == null) {
            return;
        }
        BookSearchContext context;
        switch (choice.trim()) {
            case "1":
                context = new BookSearchContext(new SearchByTitle());
                break;
            case "2":
                context = new BookSearchContext(new SearchByAuthor());
                break;
            case "3":
                context = new BookSearchContext(new SearchByISBN());
                break;
            default:
                System.out.println(ErrorMessages.INVALID_INPUT);
                return;
        }
        String query = input.readLine("Search query: ");
        List<Book> results = context.search(bookService.listBooks(), query == null ? "" : query.trim());
        if (results.isEmpty()) {
            System.out.println("No results.");
        } else {
            for (Book book : results) {
                System.out.println(book.getIsbn() + " | " + book.getTitle() + " | " + book.getAuthor());
            }
        }
    }

    private void inventoryMenu() {
        System.out.println("\nInventory & Reports");
        System.out.println("1. View Statistics");
        System.out.println("2. Most Borrowed Books");
        System.out.println("3. Overdue Books");
        String choice = input.readLine("Select option: ");
        if (choice == null) {
            return;
        }
        switch (choice.trim()) {
            case "1":
                systemStats();
                break;
            case "2":
                List<String> top = inventoryService.mostBorrowedIsbns(5);
                if (top.isEmpty()) {
                    System.out.println("No borrow data yet.");
                } else {
                    for (String isbn : top) {
                        Book book = bookService.getBook(isbn);
                        System.out.println(book.getTitle() + " (" + isbn + ")");
                    }
                }
                break;
            case "3":
                List<BorrowingRecord> overdue = inventoryService.overdueRecords();
                if (overdue.isEmpty()) {
                    System.out.println("No overdue books.");
                } else {
                    for (BorrowingRecord record : overdue) {
                        System.out.println(record.getBookIsbn() + " | Patron " + record.getPatronId()
                            + " | Due: " + record.getDueDate());
                    }
                }
                break;
            default:
                System.out.println(ErrorMessages.INVALID_INPUT);
        }
    }

    private void reservationMenu() {
        System.out.println("\nReservations");
        System.out.println("1. Reserve Book");
        System.out.println("2. Cancel Reservation");
        System.out.println("3. View Reservations By ISBN");
        String choice = input.readLine("Select option: ");
        if (choice == null) {
            return;
        }
        switch (choice.trim()) {
            case "1":
                String patronId = input.readLine("Patron ID: ");
                String isbn = input.readLine("Book ISBN: ");
                try {
                    Reservation reservation = reservationService.reserve(patronId, isbn);
                    System.out.println("Reservation created: " + reservation.getReservationId());
                } catch (RuntimeException ex) {
                    System.out.println(ex.getMessage());
                }
                break;
            case "2":
                String reservationId = input.readLine("Reservation ID: ");
                reservationService.cancel(reservationId);
                System.out.println("Reservation cancelled.");
                break;
            case "3":
                String isbnLookup = input.readLine("Book ISBN: ");
                List<Reservation> reservations = reservationService.listByBook(isbnLookup);
                if (reservations.isEmpty()) {
                    System.out.println("No reservations for this ISBN.");
                } else {
                    for (Reservation reservation : reservations) {
                        System.out.println(reservation.getReservationId() + " | " + reservation.getPatronId()
                            + " | " + reservation.getStatus());
                    }
                }
                break;
            default:
                System.out.println(ErrorMessages.INVALID_INPUT);
        }
    }

    private void branchMenu() {
        System.out.println("\nBranch Management");
        System.out.println("1. Add Branch");
        System.out.println("2. View All Branches");
        String choice = input.readLine("Select option: ");
        if (choice == null) {
            return;
        }
        switch (choice.trim()) {
            case "1":
                String id = input.readLine("Branch ID: ");
                String name = input.readLine("Name: ");
                String location = input.readLine("Location: ");
                LibraryBranch branch = new LibraryBranch(id, name, location, true);
                branchService.addBranch(branch);
                System.out.println("Branch added.");
                break;
            case "2":
                for (LibraryBranch branchItem : branchService.listBranches()) {
                    System.out.println(branchItem.getBranchId() + " | " + branchItem.getName()
                        + " | " + branchItem.getLocation());
                }
                break;
            default:
                System.out.println(ErrorMessages.INVALID_INPUT);
        }
    }

    private void recommendationMenu() {
        String patronId = input.readLine("Patron ID: ");
        List<Book> recs = recommendationService.recommendForPatron(patronId);
        if (recs.isEmpty()) {
            System.out.println("No recommendations yet.");
        } else {
            for (Book book : recs) {
                System.out.println(book.getTitle() + " | " + book.getGenre());
            }
        }
    }

    private void systemStats() {
        System.out.println("Total books: " + inventoryService.totalBooks());
        System.out.println("Available: " + inventoryService.availableBooks());
        System.out.println("Borrowed: " + inventoryService.borrowedBooks());
        System.out.println("Overdue: " + inventoryService.overdueRecords().size());
        System.out.println("Max books per patron: " + config.getInt(AppConstants.CONFIG_MAX_BOOKS, AppConstants.DEFAULT_BORROW_LIMIT));
        System.out.println("Loan period (days): " + config.getInt(AppConstants.CONFIG_LOAN_DAYS, AppConstants.DEFAULT_LOAN_DAYS));
        System.out.println("Fine per day: $" + config.getDouble(AppConstants.CONFIG_FINE_PER_DAY, AppConstants.DEFAULT_FINE_PER_DAY));
    }

    private int readInt(String prompt) {
        String value = input.readLine(prompt);
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            System.out.println(ErrorMessages.INVALID_INPUT);
            return 0;
        }
    }

    private Genre readGenre() {
        String value = input.readLine("Genre (FICTION, NON_FICTION, SCIENCE, HISTORY, FANTASY, BIOGRAPHY, OTHER): ");
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Genre.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            System.out.println(ErrorMessages.INVALID_INPUT);
            return null;
        }
    }

    private void preloadData() {
        bookService.addBook(new Book("9780134685991", "Effective Java", "Joshua Bloch", 2018, Genre.SCIENCE, BookStatus.AVAILABLE, null));
        bookService.addBook(new Book("9780132350884", "Clean Code", "Robert C. Martin", 2008, Genre.SCIENCE, BookStatus.AVAILABLE, null));
        bookService.addBook(new Book("9780201485677", "Refactoring", "Martin Fowler", 1999, Genre.SCIENCE, BookStatus.AVAILABLE, null));
        bookService.addBook(new Book("9780451524935", "1984", "George Orwell", 1949, Genre.FICTION, BookStatus.AVAILABLE, null));
        bookService.addBook(new Book("9780061120084", "To Kill a Mockingbird", "Harper Lee", 1960, Genre.FICTION, BookStatus.AVAILABLE, null));
        bookService.addBook(new Book("9780141439518", "Pride and Prejudice", "Jane Austen", 1813, Genre.FICTION, BookStatus.AVAILABLE, null));
        bookService.addBook(new Book("9780553380163", "A Brief History of Time", "Stephen Hawking", 1998, Genre.SCIENCE, BookStatus.AVAILABLE, null));
        bookService.addBook(new Book("9780062316110", "Sapiens", "Yuval Noah Harari", 2015, Genre.HISTORY, BookStatus.AVAILABLE, null));
        bookService.addBook(new Book("9781933988177", "Think and Grow Rich", "Napoleon Hill", 2011, Genre.NON_FICTION, BookStatus.AVAILABLE, null));
        bookService.addBook(new Book("9780066620992", "Good to Great", "Jim Collins", 2001, Genre.NON_FICTION, BookStatus.AVAILABLE, null));
        bookService.addBook(new Book("9780307277671", "The Road", "Cormac McCarthy", 2006, Genre.FICTION, BookStatus.AVAILABLE, null));
        bookService.addBook(new Book("9780439139601", "Harry Potter and the Goblet of Fire", "J.K. Rowling", 2000, Genre.FANTASY, BookStatus.AVAILABLE, null));
        bookService.addBook(new Book("9780307887443", "Ready Player One", "Ernest Cline", 2011, Genre.FICTION, BookStatus.AVAILABLE, null));
        bookService.addBook(new Book("9781451648539", "Steve Jobs", "Walter Isaacson", 2011, Genre.BIOGRAPHY, BookStatus.AVAILABLE, null));
        bookService.addBook(new Book("9781593279509", "Eloquent JavaScript", "Marijn Haverbeke", 2018, Genre.SCIENCE, BookStatus.AVAILABLE, null));

        patronService.registerPatron(new Patron("PAT001", "John Doe", "john@example.com", "123-456-7890", true));
        patronService.registerPatron(new Patron("PAT002", "Jane Smith", "jane@example.com", "123-456-7891", true));
        patronService.registerPatron(new Patron("PAT003", "Alice Johnson", "alice@example.com", "123-456-7892", true));
        patronService.registerPatron(new Patron("PAT004", "Bob Williams", "bob@example.com", "123-456-7893", true));
        patronService.registerPatron(new Patron("PAT005", "Charlie Brown", "charlie@example.com", "123-456-7894", true));

        branchService.addBranch(new LibraryBranch("BR001", "Main Branch", "Downtown", true));
        branchService.addBranch(new LibraryBranch("BR002", "North Branch", "North", true));
        branchService.addBranch(new LibraryBranch("BR003", "South Branch", "South", true));

        Logger.info("Sample data loaded on " + LocalDate.now());
    }
}
