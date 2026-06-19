package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import model.Book;

public class FileManager {
    private static final String FILE_PATH = "data/books.txt";

    public static void saveBooks(ArrayList<Book> books) {
        File file = getDataFile();

        if (!prepareDataFile(file)) {
            return;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (Book book : books) {
                writer.println(book.getId() + ","
                        + book.getTitle() + ","
                        + book.getAuthor() + ","
                        + book.isBorrowed());
            }
        } catch (IOException e) {
            System.out.println("Could not save books. Please check the data file.");
        }
    }

    public static ArrayList<Book> loadBooks() {
        ArrayList<Book> books = new ArrayList<>();
        ArrayList<Integer> loadedIds = new ArrayList<>();
        File file = getDataFile();

        if (!prepareDataFile(file)) {
            return books;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",", -1);

                if (parts.length != 4) {
                    System.out.println("Skipping invalid book record: " + line);
                    continue;
                }

                try {
                    int id = Integer.parseInt(parts[0].trim());
                    String title = parts[1].trim();
                    String author = parts[2].trim();
                    String borrowedText = parts[3].trim();

                    if (id <= 0 || title.isEmpty() || author.isEmpty()
                            || (!borrowedText.equalsIgnoreCase("true")
                            && !borrowedText.equalsIgnoreCase("false"))) {
                        System.out.println("Skipping invalid book record: " + line);
                        continue;
                    }

                    if (loadedIds.contains(id)) {
                        System.out.println("Skipping duplicate book ID: " + id);
                        continue;
                    }

                    boolean borrowed = Boolean.parseBoolean(borrowedText);
                    books.add(new Book(id, title, author, borrowed));
                    loadedIds.add(id);
                } catch (NumberFormatException e) {
                    System.out.println("Skipping invalid book record: " + line);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Books file was not found. Starting with an empty library.");
        } catch (IOException e) {
            System.out.println("Could not load books. Please check the data file.");
        }

        return books;
    }

    private static File getDataFile() {
        return new File(FILE_PATH);
    }

    private static boolean prepareDataFile(File file) {
        File folder = file.getParentFile();

        // Make sure the data folder and text file exist before reading or writing.
        try {
            if (folder != null && !folder.exists() && !folder.mkdirs()) {
                System.out.println("Could not create data folder.");
                return false;
            }

            if (!file.exists() && !file.createNewFile()) {
                System.out.println("Could not create books file.");
                return false;
            }
        } catch (IOException e) {
            System.out.println("Could not prepare books file.");
            return false;
        }

        return true;
    }
}
