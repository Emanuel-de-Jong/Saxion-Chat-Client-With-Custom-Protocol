package chatapp.client.gui;

import javax.swing.*;
import java.awt.event.*;

public class LogInDialog {

    private JDialog dialog;
    private JPanel panel;

    private JTextField tempNameTextField;
    private JButton tempButton;

    private JTextField logInNameTextField;
    private JPasswordField logInPasswordField;
    private JButton logInButton;

    private JTextField registerNameTextField;
    private JPasswordField registerPasswordField;
    private JPasswordField registerConfirmPasswordField;
    private JButton registerButton;


    public LogInDialog() {
        dialog = new JDialog();
        dialog.setContentPane(panel);
        dialog.setModal(true);
        dialog.getRootPane().setDefaultButton(logInButton);

        logInButton.addActionListener(e -> {
            dialog.dispose();
        });

        dialog.setDefaultCloseOperation(dialog.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dialog.dispose();
            }
        });

        panel.registerKeyboardAction(e -> {
            dialog.dispose();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        dialog.pack();
        dialog.setVisible(true);
    }

    private void createUIComponents() {
        tempNameTextField = SwingConfig.getBaseTextField();
        tempButton = SwingConfig.getBaseButton();

        logInNameTextField = SwingConfig.getBaseTextField();
        logInPasswordField = SwingConfig.getBasePasswordField();
        logInButton = SwingConfig.getBaseButton();

        registerNameTextField = SwingConfig.getBaseTextField();
        registerPasswordField = SwingConfig.getBasePasswordField();
        registerConfirmPasswordField = SwingConfig.getBasePasswordField();
        registerButton = SwingConfig.getBaseButton();
    }
}
