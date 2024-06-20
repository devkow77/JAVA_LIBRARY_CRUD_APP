import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Objects;
import java.util.Vector;

public class BooksWindow {
    private JTextField textTitle;
    private JTextField textPublishedAt;
    private JTextField textQuantity;
    private JTextField textAvailable;
    private JTextField textAuthorId;
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
    private JButton openAuthorsWindowButton;

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
                            textPublishedAt.setText(result.getString(3));
                            textQuantity.setText(result.getString(4));
                            textAvailable.setText(result.getString(5));
                            textAuthorId.setText(result.getString(6));
                        } else {
                            JOptionPane.showMessageDialog(null, "No record found!");
                        }

                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = Objects.requireNonNull(textId.getSelectedItem()).toString();
                String title = textTitle.getText();
                String publishedAt = textPublishedAt.getText();
                String quantity = textQuantity.getText();
                String available = textAvailable.getText();
                String authorId = textAuthorId.getText();

                try {
                    preparedStatment = connection.prepareStatement("UPDATE books SET title=?,publishedAt=?,quantity=?,available=?,authorId=? WHERE id=?");
                    preparedStatment.setString(1, title);
                    preparedStatment.setString(2, publishedAt);
                    preparedStatment.setString(3, quantity);
                    preparedStatment.setString(4, available);
                    preparedStatment.setString(5, authorId);
                    preparedStatment.setString(6, id);

                    int state = preparedStatment.executeUpdate();

                    if (state == 1) {
                        JOptionPane.showMessageDialog(null, "Record has been successfully updated!");
                        textTitle.setText("");
                        textPublishedAt.setText("");
                        textQuantity.setText("");
                        textAvailable.setText("");
                        textAuthorId.setText("");
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
                    preparedStatment = connection.prepareStatement("DELETE FROM books WHERE id = ?");
                    preparedStatment.setString(1, bookId);

                    int state = preparedStatment.executeUpdate();
                    if (state == 1) {
                        JOptionPane.showMessageDialog(null, "Record has been successfully removed!");
                        textTitle.setText("");
                        textAuthorId.setText("");
                        textPublishedAt.setText("");
                        textQuantity.setText("");
                        textAvailable.setText("");

                        loadData();
                        loadProductsId();
                    } else {
                        JOptionPane.showMessageDialog(null, "Record failed to be removed!");
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    // INITIALIZE TABLE
    private void initializeTable() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("id");
        model.addColumn("title");
        model.addColumn("publishedAt");
        model.addColumn("quantity");
        model.addColumn("available");
        model.addColumn("authorId");
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
            preparedStatment = connection.prepareStatement("SELECT books.id as id, title, publishedAt, quantity, available, authors.name as authorName FROM books JOIN authors ON books.authorId = authors.id");
            result = preparedStatment.executeQuery();
            ResultSetMetaData resultSetData = result.getMetaData();
            quantity = resultSetData.getColumnCount();
            String[] columnNames = {"id", "title", "publishedAt", "quantity", "available", "authorId"};
            DefaultTableModel tableModel = (DefaultTableModel) booksTable.getModel();
            tableModel.setRowCount(0);
            while(result.next()){
                Vector<String> v2 = new Vector<>();
                v2.add(result.getString("id"));
                v2.add(result.getString("title"));
                v2.add(result.getString("authorName"));
                v2.add(result.getString("publishedAt"));
                v2.add(result.getString("quantity"));
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
