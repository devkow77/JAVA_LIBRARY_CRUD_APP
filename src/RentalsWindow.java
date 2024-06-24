import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Objects;
import java.util.Vector;

public class RentalsWindow {
    private JPanel tablePanel;
    private JTable rentalsTable;
    private JComboBox textId;
    private JButton deleteButton;
    private JButton backToMenuButton;
    private JPanel mainPanel;
    private JButton openReadersManagerButton;
    private JButton openBooksManagerButton;
    private JButton borrowBookManagerButton;

    Connection connection;
    PreparedStatement preparedStatement;
    ResultSet result;

    public RentalsWindow(){
        initializeTable();
        Connect();
        loadRentalsId();
        loadData();

        // DELETE RENTAL BY ID
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String rentalId = Objects.requireNonNull(textId.getSelectedItem()).toString();
                try {
                    preparedStatement = connection.prepareStatement("DELETE FROM rentals WHERE id=?");
                    preparedStatement.setString(1, rentalId);

                    int state = preparedStatement.executeUpdate();
                    if (state == 1) {
                        JOptionPane.showMessageDialog(null, "Record has been successfully removed!");
                        loadData();
                        loadRentalsId();
                    } else {
                        JOptionPane.showMessageDialog(null, "Record failed to be removed!");
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // GO TO MENU
        backToMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MenuWindow window = new MenuWindow();
                window.showWindow();
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
        openReadersManagerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ReaderWindow window = new ReaderWindow();
                window.showWindow();
            }
        });

        // OPEN BORROW BOOK
        borrowBookManagerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BooksWindow window = new BooksWindow();
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
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("id");
        model.addColumn("title");
        model.addColumn("surname");
        model.addColumn("pesel");
        model.addColumn("borrowedAt");
        model.addColumn("deadline");
        model.addColumn("penalty");
        rentalsTable.setModel(model);
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
    public void loadRentalsId(){
        try {
            preparedStatement = connection.prepareStatement("SELECT id FROM rentals ORDER BY id");
            result = preparedStatement.executeQuery();
            textId.removeAllItems();
            while(result.next()){
                textId.addItem(result.getString(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // FETCH ALL DATA FROM RENTALS
    public void loadData(){
        try{
            preparedStatement = connection.prepareStatement("SELECT r.id, b.title AS title, rd.surname AS surname, rd.pesel AS pesel, r.date, r.deadline, CASE WHEN CURRENT_DATE > deadline THEN (CURRENT_DATE - deadline) * 5 ELSE 0 END AS penalty FROM rentals r JOIN books b ON r.bookId = b.id JOIN readers rd ON r.readerId = rd.id");
            result = preparedStatement.executeQuery();
            DefaultTableModel tableModel = (DefaultTableModel) rentalsTable.getModel();
            tableModel.setRowCount(0);
            while(result.next()){
                Vector<String> v2 = new Vector<>();
                v2.add(result.getString("id"));
                v2.add(result.getString("title"));
                v2.add(result.getString("surname"));
                v2.add(result.getString("pesel"));
                v2.add(result.getString("date"));
                v2.add(result.getString("deadline"));
                v2.add(result.getString("penalty"));
                tableModel.addRow(v2);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // SHOW APP WINDOW
    public void showWindow(){
        JFrame frame = new JFrame("rentalsFrame");
        RentalsWindow window = new RentalsWindow();
        frame.setContentPane(window.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        RentalsWindow window = new RentalsWindow();
        window.showWindow();
    }
}
