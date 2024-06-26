import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginWindow {
    private JTextField textUsername;
    private JTextField textPassword;
    private JButton logInButton;
    private JButton exitButton;
    private JPanel mainPanel;

    String login = "admin";
    String password = "admin12345";

    public LoginWindow() {
        logInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String providedLogin = textUsername.getText();
                String providedPassword = textPassword.getText();
               if(providedLogin.equals(login) && providedPassword.equals(password)){
                   MenuWindow window = new MenuWindow();
                   window.showWindow();
               }else{
                   JOptionPane.showMessageDialog(null, "Login or Password is incorrect!");
               }
            }
        });

        // EXIT APP
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
             System.exit(0);
            }
        });
    }

    public void showWindow(){
        JFrame frame = new JFrame("loginFrame");
        LoginWindow window = new LoginWindow();
        frame.setContentPane(window.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
