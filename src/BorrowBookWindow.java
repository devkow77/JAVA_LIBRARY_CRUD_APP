import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.sql.*;
import java.util.Objects;
import java.util.Vector;

public class BorrowBookWindow {
    private JPanel TablePanel;
    private JTable readersTable;
    private JTable booksTable;
    private JPanel mainPanel;
    private JComboBox textReaderId;
    private JComboBox textBookId;
    private JButton openReaderManagerButton;
    private JButton openBookManagerButton;
    private JTextField textDeadline;
    private JButton addButton;
    private JButton openRentalsManagerButton;
    private JButton goToMenuButton;

    Connection connection;
    PreparedStatement preparedStatement;
    ResultSet result;

    public BorrowBookWindow(){
        initializeTable();
        Connect();
        loadReadersId();
        loadBooksId();
        loadReaderData();
        loadBooksData();

        // BORROW BOOK
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String bookId = Objects.requireNonNull(textBookId.getSelectedItem()).toString();
                String readerId = Objects.requireNonNull(textReaderId.getSelectedItem()).toString();
                String deadline = textDeadline.getText();

                try {
                    preparedStatement = connection.prepareStatement("INSERT INTO rentals (bookId, readerId, date, deadline) VALUES (?,?,CURRENT_DATE,?)");
                    preparedStatement.setString(1, bookId);
                    preparedStatement.setString(2, readerId);
                    preparedStatement.setString(3, deadline);

                    int state = preparedStatement.executeUpdate();

                    if (state == 1) {
                        JOptionPane.showMessageDialog(null, "Record has been successfully add!");
                        loadReadersId();
                        loadBooksId();
                        loadReaderData();
                        loadBooksData();
                    }else{
                        JOptionPane.showMessageDialog(null, "Cannot add record!");
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // OPEN READERS MANAGER
        openReaderManagerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                ReaderWindow window = new ReaderWindow();
//                window.showWindow();
            }
        });

        // OPEN BOOKS WINDOW
        openBookManagerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BooksWindow window = new BooksWindow();
                window.showWindow();
            }
        });

        // OPEN RENTALS WINDOW
        openRentalsManagerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RentalsWindow window = new RentalsWindow();
                window.showWindow();
            }
        });

        // GO TO MENU
        goToMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               MenuWindow window = new MenuWindow();
               window.showWindow();
            }
        });
    }

    // INITIALIZE TABLE
    private void initializeTable() {
        DefaultTableModel readerModel = new DefaultTableModel();
        DefaultTableModel bookModel = new DefaultTableModel();

        readerModel.addColumn("id");
        readerModel.addColumn("name");
        readerModel.addColumn("surname");
        readerModel.addColumn("pesel");
        readerModel.addColumn("street");
        readerModel.addColumn("zipCode");
        readerModel.addColumn("city");
        readerModel.addColumn("rentals");

        bookModel.addColumn("id");
        bookModel.addColumn("title");
        bookModel.addColumn("author");
        bookModel.addColumn("publishedAt");
        bookModel.addColumn("quantity");
        bookModel.addColumn("borrowed");
        bookModel.addColumn("available");

        readersTable.setModel(readerModel);
        booksTable.setModel(bookModel);
    }

    //CONNECT WITH DATABASE
    public void Connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/library", "root", "");
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadReadersId() {
        try {
            preparedStatement = connection.prepareStatement("SELECT id FROM readers");
            result = preparedStatement.executeQuery();
            textReaderId.removeAllItems();
            while (result.next()) {
                textReaderId.addItem(result.getString("id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadBooksId() {
        try {
            preparedStatement = connection.prepareStatement("SELECT id FROM books");
            result = preparedStatement.executeQuery();
            textBookId.removeAllItems();
            while (result.next()) {
                textBookId.addItem(result.getString("id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

  // FETCH ALL DATA FROM READERS
    public void loadReaderData(){
        try{
            preparedStatement = connection.prepareStatement("SELECT readers.id, name, surname, pesel, street, zipCode, city, COUNT(rentals.readerId) AS rentals FROM readers LEFT JOIN rentals ON readers.id = rentals.readerId GROUP BY readers.id, name, surname, zipCode, city ORDER BY surname;");
            result = preparedStatement.executeQuery();
            ResultSetMetaData resultSetData = result.getMetaData();
            DefaultTableModel tableModel = (DefaultTableModel) readersTable.getModel();
            tableModel.setRowCount(0);
            while(result.next()){
                Vector<String> v2 = new Vector<>();
                v2.add(result.getString("id"));
                v2.add(result.getString("name"));
                v2.add(result.getString("surname"));
                v2.add(result.getString("pesel"));
                v2.add(result.getString("street"));
                v2.add(result.getString("zipCode"));
                v2.add(result.getString("city"));
                v2.add(result.getString("rentals"));
                tableModel.addRow(v2);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // FETCH ALL DATA FROM BOOKS
    public void loadBooksData(){
        try{
            preparedStatement = connection.prepareStatement("SELECT books.id, title, author, publishedAt, quantity, COUNT(rentals.bookId) AS borrowed, IF(COUNT(rentals.bookId) < quantity, 'true', 'false') AS available FROM books LEFT JOIN rentals ON books.id = rentals.bookId GROUP BY books.id, title, author, publishedAt, quantity ORDER BY title");
            result = preparedStatement.executeQuery();
            ResultSetMetaData resultSetData = result.getMetaData();
            DefaultTableModel tableModel = (DefaultTableModel) booksTable.getModel();
            tableModel.setRowCount(0);
            while(result.next()){
                Vector<String> v2 = new Vector<>();
                v2.add(result.getString("id"));
                v2.add(result.getString("title"));
                v2.add(result.getString("author"));
                v2.add(result.getString("publishedAt"));
                v2.add(result.getString("quantity"));
                v2.add(result.getString("borrowed"));
                v2.add(result.getString("available"));
                tableModel.addRow(v2);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // SHOW APP WINDOW
    public void showWindow(){
        JFrame frame = new JFrame("borrowBookFrame");
        BorrowBookWindow window = new BorrowBookWindow();
        frame.setContentPane(window.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
