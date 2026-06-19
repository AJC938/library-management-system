package app;

import java.util.Scanner;
import model.Book;
import service.Library;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Library library = new Library();
        boolean running = true;

        // Load saved books automatically when the program starts.
        library.loadData();

        // Menu loop: keeps showing the menu until the user chooses Exit.
        while (running) {
            System.out.println("\n===== Library Management System =====");
            System.out.println("1. Add Book");
            System.out.println("2. View Books");
            System.out.println("3. Search Book");
            System.out.println("4. Update Book");
            System.out.println("5. Delete Book");
            System.out.println("6. Borrow Book");
            System.out.println("7. Return Book");
            System.out.println("8. Exit");

            Integer choice = readNumber(scanner, "Enter your choice: ",
                    "Invalid choice. Please enter a valid number.");

            if (choice == null) {
                continue;
            }

            switch (choice) {
                case 1:
                    Integer id = readNumber(scanner, "Enter book ID: ", "Invalid ID.");

                    if (id == null) {
                        break;
                    }

                    if (id <= 0) {
                        System.out.println("ID must be positive.");
                        break;
                    }

                    if (library.searchBookById(id) != null) {
                        System.out.println("Book ID already exists.");
                        break;
                    }

                    String title = readRequiredText(scanner, "Enter book title: ",
                            "Title cannot be empty.");

                    if (title == null) {
                        break;
                    }

                    String author = readRequiredText(scanner, "Enter book author: ",
                            "Author cannot be empty.");

                    if (author == null) {
                        break;
                    }

                    Book book = new Book(id, title, author);

                    if (!library.addBook(book)) {
                        System.out.println("Book could not be added. Please check the book details.");
                    }
                    break;

                case 2:
                    library.displayBooks();
                    break;

                case 3:
                    Integer searchId = readBookId(scanner, "Enter book ID to search: ");

                    if (searchId == null) {
                        break;
                    }

                    Book foundBook = library.searchBookById(searchId);

                    if (foundBook == null) {
                        System.out.println("Book not found.");
                    } else {
                        System.out.println("Book found.");
                        System.out.println(foundBook);
                    }
                    break;

                case 4:
                    Integer updateId = readBookId(scanner, "Enter book ID to update: ");

                    if (updateId == null) {
                        break;
                    }

                    if (library.searchBookById(updateId) == null) {
                        System.out.println("Book not found.");
                        break;
                    }

                    String newTitle = readRequiredText(scanner, "Enter new book title: ",
                            "Title cannot be empty.");

                    if (newTitle == null) {
                        break;
                    }

                    String newAuthor = readRequiredText(scanner, "Enter new book author: ",
                            "Author cannot be empty.");

                    if (newAuthor == null) {
                        break;
                    }

                    boolean updated = library.updateBook(updateId, newTitle, newAuthor);

                    if (updated) {
                        System.out.println("Book updated successfully.");
                    } else {
                        System.out.println("Book not found.");
                    }
                    break;

                case 5:
                    Integer deleteId = readBookId(scanner, "Enter book ID to delete: ");

                    if (deleteId == null) {
                        break;
                    }

                    boolean deleted = library.deleteBook(deleteId);

                    if (deleted) {
                        System.out.println("Book deleted successfully.");
                    } else {
                        System.out.println("Book not found.");
                    }
                    break;

                case 6:
                    Integer borrowId = readBookId(scanner, "Enter book ID to borrow: ");

                    if (borrowId == null) {
                        break;
                    }

                    Book bookToBorrow = library.searchBookById(borrowId);

                    if (bookToBorrow == null) {
                        System.out.println("Book not found.");
                    } else if (bookToBorrow.isBorrowed()) {
                        System.out.println("Book already borrowed.");
                    } else {
                        library.borrowBook(borrowId);
                        System.out.println("Book borrowed successfully.");
                    }
                    break;

                case 7:
                    Integer returnId = readBookId(scanner, "Enter book ID to return: ");

                    if (returnId == null) {
                        break;
                    }

                    Book bookToReturn = library.searchBookById(returnId);

                    if (bookToReturn == null) {
                        System.out.println("Book not found.");
                    } else if (!bookToReturn.isBorrowed()) {
                        System.out.println("Book was not borrowed.");
                    } else {
                        library.returnBook(returnId);
                        System.out.println("Book returned successfully.");
                    }
                    break;

                case 8:
                    // Save all books automatically before closing the program.
                    library.saveData();
                    running = false;
                    System.out.println("Thank you for using the Library Management System.");
                    break;

                default:
                    System.out.println("Invalid choice. Please enter a valid number.");
            }
        }

        scanner.close();
    }

    private static Integer readBookId(Scanner scanner, String message) {
        Integer id = readNumber(scanner, message, "Invalid ID.");

        if (id == null) {
            return null;
        }

        if (id <= 0) {
            System.out.println("Invalid ID.");
            return null;
        }

        return id;
    }

    private static Integer readNumber(Scanner scanner, String message, String errorMessage) {
        System.out.print(message);
        String input = scanner.nextLine();

        // try-catch prevents non-numeric input from crashing the program.
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println(errorMessage);
            return null;
        }
    }

    private static String readRequiredText(Scanner scanner, String message, String errorMessage) {
        System.out.print(message);
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            System.out.println(errorMessage);
            return null;
        }

        return input;
    }
}
