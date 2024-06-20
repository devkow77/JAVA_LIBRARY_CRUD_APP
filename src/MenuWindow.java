import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuWindow {
    private JPanel mainPanel;
    private JLabel header;
    private JLabel description;
    private JButton booksButton;
    private JButton readersButton;
    private JButton exitButton;


    public MenuWindow() {
        // HANDLE EXIT APP
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // HANDLE OPEN WINDOW WITH BOOKS
        booksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new BooksWindow();
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("menuFrame");
        MenuWindow window = new MenuWindow();
        frame.setContentPane(window.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
