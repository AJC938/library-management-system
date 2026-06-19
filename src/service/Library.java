package service;

import java.util.ArrayList;
import model.Book;
import util.FileManager;

public class Library {
    private ArrayList<Book> books;

    // Constructor: initializes the ArrayList that stores all books in memory.
    public Library() {
        books = new ArrayList<>();
    }

    public boolean addBook(Book book) {
        if (book == null
                || book.getId() <= 0
                || isBlank(book.getTitle())
                || isBlank(book.getAuthor())
                || searchBookById(book.getId()) != null) {
            return false;
        }

        book.setTitle(book.getTitle().trim());
        book.setAuthor(book.getAuthor().trim());
        books.add(book);
        System.out.println("Book added successfully.");
        return true;
    }

    public void displayBooks() {
        if (books.isEmpty()) {
            System.out.println("No books available.");
            return;
        }

        System.out.println("\n----- Book List -----");
        for (Book book : books) {
            System.out.println(book);
        }
    }

    public ArrayList<Book> getBooks() {
        return new ArrayList<>(books);
    }

    public Book searchBookById(int id) {
        // Loop through each book and compare its ID with the ID entered by the user.
        for (Book book : books) {
            if (book.getId() == id) {
                return book;
            }
        }

        return null;
    }

    public boolean updateBook(int id, String newTitle, String newAuthor) {
        Book book = searchBookById(id);

        if (book == null || isBlank(newTitle) || isBlank(newAuthor)) {
            return false;
        }

        book.setTitle(newTitle.trim());
        book.setAuthor(newAuthor.trim());
        return true;
    }

    public boolean deleteBook(int id) {
        Book book = searchBookById(id);

        if (book == null) {
            return false;
        }

        books.remove(book);
        return true;
    }

    public boolean borrowBook(int id) {
        Book book = searchBookById(id);

        if (book == null || book.isBorrowed()) {
            return false;
        }

        book.setBorrowed(true);
        return true;
    }

    public boolean returnBook(int id) {
        Book book = searchBookById(id);

        if (book == null || !book.isBorrowed()) {
            return false;
        }

        book.setBorrowed(false);
        return true;
    }

    public void loadData() {
        books = FileManager.loadBooks();
        System.out.println("Data loaded successfully.");
    }

    public void saveData() {
        FileManager.saveBooks(books);
        System.out.println("Data saved successfully.");
    }

    private boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }
}
