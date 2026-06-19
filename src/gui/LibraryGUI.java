package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import model.Book;
import service.Library;

public class LibraryGUI extends JFrame {
    private static final Color BACKGROUND = new Color(245, 247, 250);
    private static final Color PANEL_COLOR = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    private static final Color TEXT_COLOR = new Color(30, 41, 59);
    private static final Color MUTED_TEXT_COLOR = new Color(71, 85, 105);
    private static final Color SIDEBAR = new Color(15, 23, 42);
    private static final Color SIDEBAR_BUTTON = new Color(51, 65, 85);
    private static final Color SIDEBAR_BUTTON_HOVER = new Color(71, 85, 105);
    private static final Color PRIMARY_COLOR = new Color(36, 99, 235);
    private static final Color SUCCESS_COLOR = new Color(22, 163, 74);
    private static final Color ERROR_COLOR = new Color(220, 38, 38);

    private final Library library;
    private final DefaultTableModel tableModel;
    private final JTable booksTable;
    private final JLabel statusLabel;
    private final JLabel totalBooksLabel;
    private final JLabel availableBooksLabel;
    private final JLabel borrowedBooksLabel;
    private final JPanel actionPanel;
    private final CardLayout cardLayout;

    private JTextField addIdField;
    private JTextField addTitleField;
    private JTextField addAuthorField;
    private JTextField searchIdField;
    private JTextField updateIdField;
    private JTextField updateTitleField;
    private JTextField updateAuthorField;
    private JTextField deleteIdField;
    private JTextField borrowIdField;
    private JTextField returnIdField;

    public LibraryGUI() {
        library = new Library();
        library.loadData();

        tableModel = new DefaultTableModel(new String[]{"ID", "Title", "Author", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        booksTable = new JTable(tableModel);
        statusLabel = new JLabel("Ready");
        totalBooksLabel = new JLabel("0");
        availableBooksLabel = new JLabel("0");
        borrowedBooksLabel = new JLabel("0");
        cardLayout = new CardLayout();
        actionPanel = new JPanel(cardLayout);

        setupWindow();
        buildLayout();
        refreshTable();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.out.println("Could not apply system look and feel.");
            }

            new LibraryGUI().setVisible(true);
        });
    }

    private void setupWindow() {
        setTitle("Library Management System");
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                library.saveData();
                dispose();
            }
        });
    }

    private void buildLayout() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(BACKGROUND);
        setContentPane(rootPanel);

        rootPanel.add(createHeader(), BorderLayout.NORTH);
        rootPanel.add(createSidebar(), BorderLayout.WEST);
        rootPanel.add(createMainContent(), BorderLayout.CENTER);
        rootPanel.add(createStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PANEL_COLOR);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(20, 26, 18, 26)));

        JLabel title = new JLabel("Library Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT_COLOR);

        JLabel subtitle = new JLabel("Manage books, borrowing, and saved library data");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(MUTED_TEXT_COLOR);
        subtitle.setHorizontalAlignment(SwingConstants.RIGHT);

        header.add(title, BorderLayout.WEST);
        header.add(subtitle, BorderLayout.EAST);

        return header;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new GridBagLayout());
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(SIDEBAR);
        sidebar.setBorder(BorderFactory.createEmptyBorder(24, 16, 24, 16));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 12, 0);
        gbc.weightx = 1;

        JLabel menuTitle = new JLabel("ACTIONS");
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        menuTitle.setForeground(new Color(203, 213, 225));
        gbc.gridy = 0;
        sidebar.add(menuTitle, gbc);

        addNavigationButton(sidebar, gbc, "Add Book", "ADD", 1);
        addNavigationButton(sidebar, gbc, "Search Book", "SEARCH", 2);
        addNavigationButton(sidebar, gbc, "Update Book", "UPDATE", 3);
        addNavigationButton(sidebar, gbc, "Delete Book", "DELETE", 4);
        addNavigationButton(sidebar, gbc, "Borrow Book", "BORROW", 5);
        addNavigationButton(sidebar, gbc, "Return Book", "RETURN", 6);

        gbc.gridy = 7;
        gbc.weighty = 1;
        sidebar.add(new JLabel(), gbc);

        JButton refreshButton = createSidebarButton("Refresh Table");
        refreshButton.addActionListener(e -> {
            refreshTable();
            showStatus("Table refreshed.", false);
        });

        gbc.gridy = 8;
        gbc.weighty = 0;
        sidebar.add(refreshButton, gbc);

        return sidebar;
    }

    private void addNavigationButton(JPanel sidebar, GridBagConstraints gbc,
            String text, String cardName, int row) {
        JButton button = createSidebarButton(text);
        button.addActionListener(e -> cardLayout.show(actionPanel, cardName));
        gbc.gridy = row;
        sidebar.add(button, gbc);
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBackground(SIDEBAR_BUTTON);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(13, 16, 13, 16));
        button.setPreferredSize(new Dimension(188, 46));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(SIDEBAR_BUTTON_HOVER);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(SIDEBAR_BUTTON);
            }
        });
        return button;
    }

    private JPanel createMainContent() {
        JPanel mainPanel = new JPanel(new BorderLayout(18, 18));
        mainPanel.setBackground(BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(22, 24, 24, 24));

        actionPanel.setBackground(BACKGROUND);
        actionPanel.add(createAddPanel(), "ADD");
        actionPanel.add(createSearchPanel(), "SEARCH");
        actionPanel.add(createUpdatePanel(), "UPDATE");
        actionPanel.add(createDeletePanel(), "DELETE");
        actionPanel.add(createBorrowPanel(), "BORROW");
        actionPanel.add(createReturnPanel(), "RETURN");

        JPanel lowerPanel = new JPanel(new BorderLayout(18, 18));
        lowerPanel.setBackground(BACKGROUND);
        lowerPanel.add(createStatsPanel(), BorderLayout.NORTH);
        lowerPanel.add(createTablePanel(), BorderLayout.CENTER);

        mainPanel.add(actionPanel, BorderLayout.NORTH);
        mainPanel.add(lowerPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createAddPanel() {
        addIdField = createTextField();
        addTitleField = createTextField();
        addAuthorField = createTextField();

        JPanel panel = createFormPanel("Add Book");
        addField(panel, "Book ID", addIdField, 0);
        addField(panel, "Title", addTitleField, 1);
        addField(panel, "Author", addAuthorField, 2);

        JButton addButton = createPrimaryButton("Add Book");
        addButton.addActionListener(e -> addBook());
        addButton(panel, addButton, 3);

        return panel;
    }

    private JPanel createSearchPanel() {
        searchIdField = createTextField();

        JPanel panel = createFormPanel("Search Book");
        addField(panel, "Book ID", searchIdField, 0);

        JButton searchButton = createPrimaryButton("Search");
        searchButton.addActionListener(e -> searchBook());
        addButton(panel, searchButton, 1);

        return panel;
    }

    private JPanel createUpdatePanel() {
        updateIdField = createTextField();
        updateTitleField = createTextField();
        updateAuthorField = createTextField();

        JPanel panel = createFormPanel("Update Book");
        addField(panel, "Book ID", updateIdField, 0);
        addField(panel, "New Title", updateTitleField, 1);
        addField(panel, "New Author", updateAuthorField, 2);

        JButton updateButton = createPrimaryButton("Update Book");
        updateButton.addActionListener(e -> updateBook());
        addButton(panel, updateButton, 3);

        return panel;
    }

    private JPanel createDeletePanel() {
        deleteIdField = createTextField();

        JPanel panel = createFormPanel("Delete Book");
        addField(panel, "Book ID", deleteIdField, 0);

        JButton deleteButton = createDangerButton("Delete Book");
        deleteButton.addActionListener(e -> deleteBook());
        addButton(panel, deleteButton, 1);

        return panel;
    }

    private JPanel createBorrowPanel() {
        borrowIdField = createTextField();

        JPanel panel = createFormPanel("Borrow Book");
        addField(panel, "Book ID", borrowIdField, 0);

        JButton borrowButton = createPrimaryButton("Borrow Book");
        borrowButton.addActionListener(e -> borrowBook());
        addButton(panel, borrowButton, 1);

        return panel;
    }

    private JPanel createReturnPanel() {
        returnIdField = createTextField();

        JPanel panel = createFormPanel("Return Book");
        addField(panel, "Book ID", returnIdField, 0);

        JButton returnButton = createPrimaryButton("Return Book");
        returnButton.addActionListener(e -> returnBook());
        addButton(panel, returnButton, 1);

        return panel;
    }

    private JPanel createFormPanel(String titleText) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(22, 24, 24, 24)));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 16, 0);
        panel.add(title, gbc);

        return panel;
    }

    private void addField(JPanel panel, String labelText, JTextField field, int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = row + 1;
        gbc.insets = new Insets(4, 0, 8, 12);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(MUTED_TEXT_COLOR);

        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private void addButton(JPanel panel, JButton button, int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = row + 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(10, 0, 0, 12);
        panel.add(button, gbc);
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(320, 34));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        return field;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(11, 20, 11, 20));
        return button;
    }

    private JButton createDangerButton(String text) {
        JButton button = createPrimaryButton(text);
        button.setBackground(ERROR_COLOR);
        return button;
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 14, 0));
        statsPanel.setBackground(BACKGROUND);

        statsPanel.add(createStatCard("Total Books", totalBooksLabel, PRIMARY_COLOR));
        statsPanel.add(createStatCard("Available Books", availableBooksLabel, SUCCESS_COLOR));
        statsPanel.add(createStatCard("Borrowed Books", borrowedBooksLabel, ERROR_COLOR));

        return statsPanel;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(PANEL_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(MUTED_TEXT_COLOR);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLabel.setForeground(accentColor);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(PANEL_COLOR);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JLabel tableTitle = new JLabel("Books");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        tableTitle.setForeground(TEXT_COLOR);
        tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        booksTable.setRowHeight(34);
        booksTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        booksTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        booksTable.getTableHeader().setBackground(new Color(241, 245, 249));
        booksTable.getTableHeader().setForeground(TEXT_COLOR);
        booksTable.setGridColor(new Color(241, 245, 249));
        booksTable.setShowVerticalLines(false);
        booksTable.setFillsViewportHeight(true);
        booksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        booksTable.setDefaultRenderer(Object.class, new StatusTableRenderer());
        booksTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        booksTable.getColumnModel().getColumn(1).setPreferredWidth(320);
        booksTable.getColumnModel().getColumn(2).setPreferredWidth(260);
        booksTable.getColumnModel().getColumn(3).setPreferredWidth(130);

        JScrollPane scrollPane = new JScrollPane(booksTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

        tablePanel.add(tableTitle, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 10));
        statusBar.setBackground(PANEL_COLOR);
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));

        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setForeground(MUTED_TEXT_COLOR);
        statusBar.add(statusLabel);

        return statusBar;
    }

    private void addBook() {
        Integer id = readPositiveId(addIdField.getText());

        if (id == null) {
            showStatus("Invalid ID.", true);
            return;
        }

        String title = addTitleField.getText().trim();
        String author = addAuthorField.getText().trim();

        if (title.isEmpty()) {
            showStatus("Title cannot be empty.", true);
            return;
        }

        if (author.isEmpty()) {
            showStatus("Author cannot be empty.", true);
            return;
        }

        if (library.searchBookById(id) != null) {
            showStatus("Book ID already exists.", true);
            return;
        }

        if (library.addBook(new Book(id, title, author))) {
            afterSuccessfulChange("Book added successfully.");
            clearFields(addIdField, addTitleField, addAuthorField);
        } else {
            showStatus("Book could not be added. Please check the book details.", true);
        }
    }

    private void searchBook() {
        Integer id = readPositiveId(searchIdField.getText());

        if (id == null) {
            showStatus("Invalid ID.", true);
            return;
        }

        Book book = library.searchBookById(id);

        if (book == null) {
            showStatus("Book not found.", true);
            clearTableSelection();
            return;
        }

        selectBookInTable(id);
        showStatus("Book found: " + book.getTitle(), false);
    }

    private void updateBook() {
        Integer id = readPositiveId(updateIdField.getText());

        if (id == null) {
            showStatus("Invalid ID.", true);
            return;
        }

        if (library.searchBookById(id) == null) {
            showStatus("Book not found.", true);
            return;
        }

        String title = updateTitleField.getText().trim();
        String author = updateAuthorField.getText().trim();

        if (title.isEmpty()) {
            showStatus("Title cannot be empty.", true);
            return;
        }

        if (author.isEmpty()) {
            showStatus("Author cannot be empty.", true);
            return;
        }

        library.updateBook(id, title, author);
        afterSuccessfulChange("Book updated successfully.");
        selectBookInTable(id);
        clearFields(updateIdField, updateTitleField, updateAuthorField);
    }

    private void deleteBook() {
        Integer id = readPositiveId(deleteIdField.getText());

        if (id == null) {
            showStatus("Invalid ID.", true);
            return;
        }

        if (library.deleteBook(id)) {
            afterSuccessfulChange("Book deleted successfully.");
            clearFields(deleteIdField);
        } else {
            showStatus("Book not found.", true);
        }
    }

    private void borrowBook() {
        Integer id = readPositiveId(borrowIdField.getText());

        if (id == null) {
            showStatus("Invalid ID.", true);
            return;
        }

        Book book = library.searchBookById(id);

        if (book == null) {
            showStatus("Book not found.", true);
        } else if (book.isBorrowed()) {
            showStatus("Book already borrowed.", true);
        } else {
            library.borrowBook(id);
            afterSuccessfulChange("Book borrowed successfully.");
            selectBookInTable(id);
            clearFields(borrowIdField);
        }
    }

    private void returnBook() {
        Integer id = readPositiveId(returnIdField.getText());

        if (id == null) {
            showStatus("Invalid ID.", true);
            return;
        }

        Book book = library.searchBookById(id);

        if (book == null) {
            showStatus("Book not found.", true);
        } else if (!book.isBorrowed()) {
            showStatus("Book was not borrowed.", true);
        } else {
            library.returnBook(id);
            afterSuccessfulChange("Book returned successfully.");
            selectBookInTable(id);
            clearFields(returnIdField);
        }
    }

    private Integer readPositiveId(String input) {
        try {
            int id = Integer.parseInt(input.trim());

            if (id <= 0) {
                return null;
            }

            return id;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void afterSuccessfulChange(String message) {
        refreshTable();
        library.saveData();
        showStatus(message, false);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);

        for (Book book : library.getBooks()) {
            tableModel.addRow(new Object[]{
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.isBorrowed() ? "Borrowed" : "Available"
            });
        }

        updateStatistics();
    }

    private void updateStatistics() {
        int total = 0;
        int borrowed = 0;

        for (Book book : library.getBooks()) {
            total++;

            if (book.isBorrowed()) {
                borrowed++;
            }
        }

        totalBooksLabel.setText(String.valueOf(total));
        borrowedBooksLabel.setText(String.valueOf(borrowed));
        availableBooksLabel.setText(String.valueOf(total - borrowed));
    }

    private void selectBookInTable(int id) {
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            int tableId = (int) tableModel.getValueAt(row, 0);

            if (tableId == id) {
                booksTable.setRowSelectionInterval(row, row);
                booksTable.scrollRectToVisible(booksTable.getCellRect(row, 0, true));
                return;
            }
        }
    }

    private void clearTableSelection() {
        booksTable.clearSelection();
    }

    private void clearFields(JTextField... fields) {
        for (JTextField field : fields) {
            field.setText("");
        }
    }

    private void showStatus(String message, boolean error) {
        statusLabel.setText(message);
        statusLabel.setForeground(error ? ERROR_COLOR : SUCCESS_COLOR);
    }

    private static class StatusTableRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            component.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            if (!isSelected) {
                component.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                component.setForeground(TEXT_COLOR);
            }

            setHorizontalAlignment(column == 0 || column == 3
                    ? SwingConstants.CENTER
                    : SwingConstants.LEFT);

            if (column == 0 || column == 3) {
                setFont(new Font("Segoe UI", Font.BOLD, 14));
            }

            if (column == 3 && !isSelected) {
                String status = String.valueOf(value);
                component.setForeground("Borrowed".equals(status) ? ERROR_COLOR : SUCCESS_COLOR);
            }

            return component;
        }
    }
}
