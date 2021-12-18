package chatapp.client.gui;

import chatapp.client.ClientGlobals;
import chatapp.client.SystemHelper;
import chatapp.server.models.AuthUser;
import chatapp.shared.models.User;

import javax.swing.*;
import java.awt.event.*;

public class LogInDialog {

    private final ClientGlobals globals;
    private String name;
    private String username;
    private String password;

    private final JDialog dialog;
    private JPanel panel;

    private JLabel tempNameLabel;
    private JTextField tempNameTextField;
    private JButton tempButton;

    private JLabel accountNameLabel;
    private JTextField accountNameTextField;
    private JLabel accountPasswordLabel;
    private JPasswordField accountPasswordField;
    private JButton accountLogInButton;
    private JButton accountRegisterButton;


    public LogInDialog(ClientGlobals globals, String name) {
        this.globals = globals;
        this.name = name;

        dialog = new JDialog();
        dialog.setResizable(false);
        dialog.setContentPane(panel);
        dialog.setModal(true);
        dialog.getRootPane().setDefaultButton(tempButton);
        dialog.setLocationRelativeTo(null);

        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                SystemHelper.exit();
            }
        });

        tempButton.addActionListener(e -> {
            username = tempNameTextField.getText();
            close();
        });

        accountLogInButton.addActionListener(e -> {
            username = accountNameTextField.getText();
            password = String.valueOf(accountPasswordField.getPassword());
            close();
        });

        tempNameTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                dialog.getRootPane().setDefaultButton(tempButton);
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                dialog.getRootPane().setDefaultButton(accountLogInButton);
            }
        });

        dialog.pack();
        dialog.setVisible(true);
    }

    private void close() {
        globals.currentUser = new User(username, globals);
        dialog.dispose();
        globals.clientListeners.logInDialog.forEach(l -> l.logInDialogClosed(name, username, password));
    }

    private void createUIComponents() {
        tempNameLabel = SwingBuilder.getBaseLabel();
        tempNameTextField = SwingBuilder.getBaseTextField();
        tempButton = SwingBuilder.getBaseButton();

        accountNameLabel = SwingBuilder.getBaseLabel();
        accountNameTextField = SwingBuilder.getBaseTextField();
        accountPasswordLabel = SwingBuilder.getBaseLabel();
        accountPasswordField = SwingBuilder.getBasePasswordField();
        accountLogInButton = SwingBuilder.getBaseButton();
        accountRegisterButton = SwingBuilder.getBaseButton();
    }

}
