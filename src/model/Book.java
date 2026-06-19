package model;

public class Book {
    private int id;
    private String title;
    private String author;
    private boolean borrowed;

    // Constructor used when adding a new book from the console or GUI.
    public Book(int id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.borrowed = false;
    }

    // Constructor used when loading saved books from the text file.
    public Book(int id, String title, String author, boolean borrowed) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.borrowed = borrowed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isBorrowed() {
        return borrowed;
    }

    public void setBorrowed(boolean borrowed) {
        this.borrowed = borrowed;
    }

    // toString(): controls how a Book object is displayed in the console.
    @Override
    public String toString() {
        String status = borrowed ? "Borrowed" : "Available";

        return "Book ID: " + id
                + ", Title: " + title
                + ", Author: " + author
                + ", Status: " + status;
    }
}
