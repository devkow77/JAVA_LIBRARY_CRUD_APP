import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.Objects;
import java.util.Vector;

public class ReaderWindow {
    private JTextField textName;
    private JTextField textSurname;
    private JTextField textPesel;
    private JTextField textStreet;
    private JTextField textZipCode;
    private JTextField textCity;
    private JComboBox textId;
    private JButton searchButton;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton exportButton;
    private JTable readersTable;
    private JPanel tablePanel;
    private JPanel buttonsPanel;
    private JPanel mainPanel;
    private JButton backToMenuButton;
    private JButton openBooksManagerButton;
    private JTable borrowedBooksTable;
    private JButton openBorrowBookButton;
    private JButton openRentalsManagerButton;
    private JPanel borrowedBooksPanel;

    Connection connection;
    PreparedStatement preparedStatment;
    ResultSet result;

    public ReaderWindow(){
        initializeTable();
        Connect();
        loadReadersId();
        loadReadersData();
        loadBorrowedBooksData();

        // SEARCH READER BY ID
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String productId = Objects.requireNonNull(textId.getSelectedItem()).toString();
                try {
                    preparedStatment = connection.prepareStatement("SELECT * FROM readers WHERE id = ?");
                    preparedStatment.setString(1, productId);
                    result = preparedStatment.executeQuery();

                    if (result.next()) {
                        textName.setText(result.getString(2));
                        textSurname.setText(result.getString(3));
                        textPesel.setText(result.getString(4));
                        textStreet.setText(result.getString(5));
                        textZipCode.setText(result.getString(6));
                        textCity.setText(result.getString(7));
                        loadBorrowedBooksData();
                    } else {
                        JOptionPane.showMessageDialog(null, "No record found!");
                    }

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // ADD READER
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = textName.getText();
                String surname = textSurname.getText();
                String pesel = textPesel.getText();
                String street = textStreet.getText();
                String zipCode = textZipCode.getText();
                String city = textCity.getText();

                try {
                    preparedStatment = connection.prepareStatement("INSERT INTO readers (name, surname, pesel, street, zipCode, city) VALUES (?,?,?,?,?,?)");
                    preparedStatment.setString(1, name);
                    preparedStatment.setString(2, surname);
                    preparedStatment.setString(3, pesel);
                    preparedStatment.setString(4, street);
                    preparedStatment.setString(5, zipCode);
                    preparedStatment.setString(6, city);

                    int state = preparedStatment.executeUpdate();

                    if (state == 1) {
                        JOptionPane.showMessageDialog(null, "Record has been successfully add!");
                        textName.setText("");
                        textSurname.setText("");
                        textPesel.setText("");
                        textStreet.setText("");
                        textZipCode.setText("");
                        textCity.setText("");
                        textStreet.setText("");
                        textName.requestFocus();
                        loadReadersData();
                        loadReadersId();
                    }else{
                        JOptionPane.showMessageDialog(null, "Cannot add record!");
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // UPDATE READER
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = Objects.requireNonNull(textId.getSelectedItem()).toString();
                String name = textName.getText();
                String surname = textSurname.getText();
                String pesel = textPesel.getText();
                String street = textStreet.getText();
                String zipCode = textZipCode.getText();
                String city = textCity.getText();

                try {
                    preparedStatment = connection.prepareStatement("UPDATE readers SET name=?,surname=?,pesel=?,street=?,zipCode=?,city=? WHERE id=?");
                    preparedStatment.setString(1, name);
                    preparedStatment.setString(2, surname);
                    preparedStatment.setString(3, pesel);
                    preparedStatment.setString(4, street);
                    preparedStatment.setString(5, zipCode);
                    preparedStatment.setString(6, city);
                    preparedStatment.setString(7, id);

                    int state = preparedStatment.executeUpdate();

                    if (state == 1) {
                        JOptionPane.showMessageDialog(null, "Record has been successfully updated!");
                        textName.setText("");
                        textSurname.setText("");
                        textPesel.setText("");
                        textStreet.setText("");
                        textZipCode.setText("");
                        textCity.setText("");
                        textName.requestFocus();
                        loadReadersData();
                        loadReadersId();
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
                String readerId = Objects.requireNonNull(textId.getSelectedItem()).toString();
                try {
                    preparedStatment = connection.prepareStatement("DELETE FROM readers WHERE id=?");
                    preparedStatment.setString(1, readerId);

                    int state = preparedStatment.executeUpdate();
                    if (state == 1) {
                        JOptionPane.showMessageDialog(null, "Record has been successfully removed!");
                        textName.setText("");
                        textSurname.setText("");
                        textPesel.setText("");
                        textStreet.setText("");
                        textZipCode.setText("");
                        textCity.setText("");
                        loadReadersData();
                        loadReadersId();
                    } else {
                        JOptionPane.showMessageDialog(null, "Record failed to be removed!");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Cannot delete reader if rentals is not zero!");
                    throw new RuntimeException(ex);
                }
            }
        });

        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filename = "C:/Users/kacpe/IdeaProjects/Library/readers_data_exported.csv";
                try {
                    FileWriter fw = new FileWriter(filename);
                    preparedStatment = connection.prepareStatement("SELECT * FROM readers");
                    result = preparedStatment.executeQuery();

                    while(result.next()){
                        String id = result.getString(1);
                        String name = result.getString(2);
                        String surname = result.getString(3);
                        String pesel = result.getString(4);
                        String street = result.getString(5);
                        String zipCode = result.getString(6);
                        String city = result.getString(7);

                        fw.append(id).append(";").append(name).append(";").append(surname).append(";").append(pesel).append(";").append(street).append(";").append(zipCode).append(";").append(city).append("\n");

                    }
                    JOptionPane.showMessageDialog(null, "CSV File is exported successfully!");
                    fw.flush();
                    fw.close();
                } catch (IOException | SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });


        // OPEN BOOKS MANAGER
        openBooksManagerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BooksWindow window = new BooksWindow();
                window.showWindow();
            }
        });

        // OPEN READERS MANAGER
        openBorrowBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        // OPEN RENTALS MANAGER
        openRentalsManagerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RentalsWindow window = new RentalsWindow();
                window.showWindow();
            }
        });

        // OPEN MENU
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
        DefaultTableModel readersModel = new DefaultTableModel();
        DefaultTableModel borrowedBooksModel = new DefaultTableModel();

        readersModel.addColumn("id");
        readersModel.addColumn("name");
        readersModel.addColumn("surname");
        readersModel.addColumn("pesel");
        readersModel.addColumn("street");
        readersModel.addColumn("zipCode");
        readersModel.addColumn("city");
        readersModel.addColumn("rentals");

        borrowedBooksModel.addColumn("id");
        borrowedBooksModel.addColumn("title");
        borrowedBooksModel.addColumn("author");
        borrowedBooksModel.addColumn("surname");
        borrowedBooksModel.addColumn("pesel");
        borrowedBooksModel.addColumn("deadline");
        borrowedBooksModel.addColumn("penalty");

        readersTable.setModel(readersModel);
        borrowedBooksTable.setModel(borrowedBooksModel);
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

    //LOAD BOOKS ID TO COMBOBOX
    public void loadReadersId(){
        try {
            preparedStatment = connection.prepareStatement("SELECT id FROM readers");
            result = preparedStatment.executeQuery();
            textId.removeAllItems();
            while(result.next()){
                textId.addItem(result.getString(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // FETCH ALL DATA FROM READERS
    public void loadReadersData(){
        try{
            preparedStatment = connection.prepareStatement("SELECT readers.id, name, surname, pesel, street, zipCode, city, COUNT(rentals.readerId) AS rentals FROM readers LEFT JOIN rentals ON readers.id = rentals.readerId GROUP BY readers.id, name, surname, zipCode, city;");
            result = preparedStatment.executeQuery();
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

    public void loadBorrowedBooksData(){
        String readerId = Objects.requireNonNull(textId.getSelectedItem()).toString();
        try {
            preparedStatment = connection.prepareStatement("SELECT r.id AS id, b.title, b.author, rd.surname, rd.pesel, r.date, r.deadline, CASE WHEN CURRENT_DATE > r.deadline THEN (CURRENT_DATE - r.deadline) * 5 ELSE 0 END AS penalty FROM rentals r JOIN books b ON r.bookId = b.id JOIN readers rd ON r.readerId = rd.id WHERE r.readerId = ? ORDER BY r.id");
            preparedStatment.setString(1, readerId);
            result = preparedStatment.executeQuery();
            DefaultTableModel tableModel = (DefaultTableModel) borrowedBooksTable.getModel();
            tableModel.setRowCount(0);
            while(result.next()){
                Vector<String> v2 = new Vector<>();
                v2.add(result.getString("id"));
                v2.add(result.getString("title"));
                v2.add(result.getString("author"));
                v2.add(result.getString("surname"));
                v2.add(result.getString("pesel"));
                v2.add(result.getString("deadline"));
                v2.add(result.getString("penalty"));
                tableModel.addRow(v2);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Cannot delete reader if rentals is not zero!");
            throw new RuntimeException(ex);
        }
    }

    // SHOW APP WINDOW
    public void showWindow(){
        JFrame frame = new JFrame("readerFrame");
        ReaderWindow window = new ReaderWindow();
        frame.setContentPane(window.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}

