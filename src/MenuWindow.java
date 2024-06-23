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
        // EXIT APP
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // OPEN BOOKS WINDOW
        booksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BooksWindow window = new BooksWindow();
                window.showWindow();
            }
        });

        // OPEN READERS WINDOW
        readersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ReaderWindow window = new ReaderWindow();
                window.showWindow();
            }
        });
    }

    // SHOW APP WINDOW
    public void showWindow(){
        JFrame frame = new JFrame("menuFrame");
        MenuWindow window = new MenuWindow();
        frame.setContentPane(window.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        MenuWindow window = new MenuWindow();
        window.showWindow();
    }
}
