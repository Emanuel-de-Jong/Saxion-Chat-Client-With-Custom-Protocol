package chatapp.client.gui;

import chatapp.client.ClientGlobals;
import chatapp.shared.models.User;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LogInDialog {

    private final ClientGlobals globals;
    private final String name;
    private String username;
    private String password;
    private boolean validated;

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
    private JLabel tempErrorLabel;
    private JLabel loginErrorLabel;
    private JLabel errorLabel;


    public LogInDialog(ClientGlobals globals, String name) {
        this.globals = globals;
        this.name = name;
        this.validated = false;

        dialog = new JDialog();
        dialog.setResizable(false);
        dialog.setContentPane(panel);
        dialog.setModal(false);
        dialog.getRootPane().setDefaultButton(tempButton);
        dialog.setLocationRelativeTo(null);

        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                globals.systemHelper.exit();
            }
        });

        tempButton.addActionListener(e -> {
            username = tempNameTextField.getText();
            globals.clientListeners.logInDialog.forEach(l -> l.logIn(username, null));
        });

        accountLogInButton.addActionListener(e -> {
            username = accountNameTextField.getText();
            password = String.valueOf(accountPasswordField.getPassword());
            validated = true;
            globals.clientListeners.logInDialog.forEach(l -> l.logIn(username, password));
        });

        tempNameTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                dialog.getRootPane().setDefaultButton(tempButton);
                errorLabel = tempErrorLabel;
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                dialog.getRootPane().setDefaultButton(accountLogInButton);
                errorLabel = loginErrorLabel;
            }
        });

        dialog.pack();
        dialog.setVisible(true);
    }

    public void close() {
        globals.currentUser = new User(username, validated, globals);
        dialog.dispose();
        globals.clientListeners.logInDialog.forEach(l -> l.logInDialogClosed(name, username, password));
    }

    private void createUIComponents() {
        tempNameLabel = SwingBuilder.getBaseLabel();
        tempNameTextField = SwingBuilder.getBaseTextField();
        tempButton = SwingBuilder.getBaseButton();
        tempErrorLabel = SwingBuilder.getBaseLabel();
        loginErrorLabel = SwingBuilder.getBaseLabel();
        errorLabel = loginErrorLabel;

        accountNameLabel = SwingBuilder.getBaseLabel();
        accountNameTextField = SwingBuilder.getBaseTextField();
        accountPasswordLabel = SwingBuilder.getBaseLabel();
        accountPasswordField = SwingBuilder.getBasePasswordField();
        accountLogInButton = SwingBuilder.getBaseButton();
        accountRegisterButton = SwingBuilder.getBaseButton();
    }

    public void showError(String errMessage) {
        errorLabel.setVisible(true);
        errorLabel.setText(errMessage);
    }
}
