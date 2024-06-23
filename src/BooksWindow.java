import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.Objects;
import java.util.Vector;

public class BooksWindow {
    private JTextField textTitle;
    private JTextField textPublishedAt;
    private JTextField textQuantity;
    private JTextField textAuthor;
    private JButton searchButton;
    private JComboBox textId;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton exportToCSVButton;
    private JPanel mainPanel;
    private JPanel buttonsPanel;
    private JTable booksTable;
    private JPanel tablePanel;
    private JButton borrowButton;
    private JButton addReaderButton;
    private JButton backToMenuButton;

    Connection connection;
    PreparedStatement preparedStatment;
    ResultSet result;

    public BooksWindow(){
        initializeTable();
        Connect();
        loadProductsId();
        loadData();

        // SEARCH BOOK BY ID
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    String productId = Objects.requireNonNull(textId.getSelectedItem()).toString();
                    try {
                        preparedStatment = connection.prepareStatement("SELECT * FROM books WHERE id = ?");
                        preparedStatment.setString(1, productId);
                        result = preparedStatment.executeQuery();

                        if (result.next()) {
                            textTitle.setText(result.getString(2));
                            textAuthor.setText(result.getString(3));
                            textPublishedAt.setText(result.getString(4));
                            textQuantity.setText(result.getString(5));
                        } else {
                            JOptionPane.showMessageDialog(null, "No record found!");
                        }

                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
        });

        // ADD BOOK
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = textTitle.getText();
                String author = textAuthor.getText();
                String publishedAt = textPublishedAt.getText();
                String quantity = textQuantity.getText();

                try {
                    preparedStatment = connection.prepareStatement("INSERT INTO books (title, author, publishedAt, quantity) VALUES (?,?,?,?)");
                    preparedStatment.setString(1, title);
                    preparedStatment.setString(2, author);
                    preparedStatment.setString(3, publishedAt);
                    preparedStatment.setString(4, quantity);

                    int state = preparedStatment.executeUpdate();

                    if (state == 1) {
                        JOptionPane.showMessageDialog(null, "Record has been successfully add!");
                        textTitle.setText("");
                        textPublishedAt.setText("");
                        textQuantity.setText("");
                        textAuthor.setText("");
                        textTitle.requestFocus();
                        loadData();
                        loadProductsId();
                    }else{
                        JOptionPane.showMessageDialog(null, "Cannot add record!");
                    }

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // UPDATE BOOK
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = Objects.requireNonNull(textId.getSelectedItem()).toString();
                String title = textTitle.getText();
                String publishedAt = textPublishedAt.getText();
                String quantity = textQuantity.getText();
                String author = textAuthor.getText();

                try {
                    preparedStatment = connection.prepareStatement("UPDATE books SET title=?,author=?,publishedAt=?,quantity=? WHERE id=?");
                    preparedStatment.setString(1, title);
                    preparedStatment.setString(2, author);
                    preparedStatment.setString(3, publishedAt);
                    preparedStatment.setString(4, quantity);
                    preparedStatment.setString(5, id);

                    int state = preparedStatment.executeUpdate();

                    if (state == 1) {
                        JOptionPane.showMessageDialog(null, "Record has been successfully updated!");
                        textTitle.setText("");
                        textPublishedAt.setText("");
                        textQuantity.setText("");
                        textAuthor.setText("");
                        textTitle.requestFocus();
                        loadData();
                        loadProductsId();
                    }else{
                        JOptionPane.showMessageDialog(null, "Cannot update record!");
                    }

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // DELETE BOOK BY ID
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String bookId = Objects.requireNonNull(textId.getSelectedItem()).toString();
                try {
                    preparedStatment = connection.prepareStatement("DELETE FROM books WHERE id=?");
                    preparedStatment.setString(1, bookId);

                    int state = preparedStatment.executeUpdate();
                    if (state == 1) {
                        JOptionPane.showMessageDialog(null, "Record has been successfully removed!");
                        textTitle.setText("");
                        textAuthor.setText("");
                        textPublishedAt.setText("");
                        textQuantity.setText("");

                        loadData();
                        loadProductsId();
                    } else {
                        JOptionPane.showMessageDialog(null, "Record failed to be removed!");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Cannot delete book if borrowers this book still exist!");
                    throw new RuntimeException(ex);
                }
            }
        });

        // EXPORT BOOKS DATA TO EXCEL
        exportToCSVButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filename = "C:/Users/kacpe/IdeaProjects/Library/data_exported.csv";
                try {
                    FileWriter fw = new FileWriter(filename);
                    preparedStatment = connection.prepareStatement("SELECT * FROM books");
                    result = preparedStatment.executeQuery();

                    while(result.next()){
                        String id = result.getString(1);
                        String name = result.getString(2);
                        String price = result.getString(3);
                        String quantity = result.getString(4);

                        fw.append(id).append(";").append(name).append(";").append(price).append(";").append(quantity).append("\n");

                    }
                    JOptionPane.showMessageDialog(null, "CSV File is exported successfully!");
                    fw.flush();
                    fw.close();
                } catch (IOException | SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // BORROW A BOOK
        borrowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        // OPEN READER WINDOW
        addReaderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ReaderWindow window = new ReaderWindow();
                window.showWindow();
            }
        });

        // BACK TO MENU
        backToMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MenuWindow window = new MenuWindow();
                window.showWindow();
            }
        });
    }

    // INITIALIZE TABLE
    private void initializeTable() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("id");
        model.addColumn("title");
        model.addColumn("author");
        model.addColumn("publishedAt");
        model.addColumn("quantity");
        model.addColumn("borrowed");
        model.addColumn("available");
        booksTable.setModel(model);
    }

    // CONNECT WITH DATABASE
    public void Connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/library", "root", "");
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // LOAD BOOKS ID TO COMBOBOX
    public void loadProductsId(){
        try {
            preparedStatment = connection.prepareStatement("SELECT id FROM books");
            result = preparedStatment.executeQuery();
            textId.removeAllItems();
            while(result.next()){
                textId.addItem(result.getString(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // FETCH ALL DATA FROM BOOKS
    public void loadData(){
        try{
            int quantity;
            preparedStatment = connection.prepareStatement("SELECT books.id, title, author, publishedAt, quantity, COUNT(rentals.bookId) AS borrowed, IF(COUNT(rentals.bookId) < quantity, 'true', 'false') AS available FROM books LEFT JOIN rentals ON books.id = rentals.bookId GROUP BY books.id, title, author, publishedAt, quantity");
            result = preparedStatment.executeQuery();
            ResultSetMetaData resultSetData = result.getMetaData();
            quantity = resultSetData.getColumnCount();
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
        JFrame frame = new JFrame("bookFrame");
        BooksWindow window = new BooksWindow();
        frame.setContentPane(window.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        BooksWindow window = new BooksWindow();
        window.showWindow();
    }
}
