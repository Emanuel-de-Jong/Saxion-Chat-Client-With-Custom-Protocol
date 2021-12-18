package chatapp.client.gui;

import chatapp.client.ClientGlobals;
import chatapp.shared.models.User;

import javax.swing.*;
import java.awt.event.*;

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


    public LogInDialog(ClientGlobals globals, String name) {
        this.globals = globals;
        this.name = name;
        this.validated = false;

        dialog = new JDialog();
        dialog.setResizable(false);
        dialog.setContentPane(panel);
        dialog.setModal(true);
        dialog.getRootPane().setDefaultButton(tempButton);

        accountLogInButton.addActionListener(e -> {
            username = accountNameTextField.getText();
            password = String.valueOf(accountPasswordField.getPassword());
            validated = true;
            close();
        });

        tempButton.addActionListener(e -> {
            username = tempNameTextField.getText();
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

        dialog.setDefaultCloseOperation(dialog.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dialog.dispose();
                super.windowClosing(e);
            }
        });

        panel.registerKeyboardAction(e -> close(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        dialog.setLocationRelativeTo(null);
        dialog.pack();
        dialog.setVisible(true);
    }

    private void close() {
        globals.currentUser = new User(username, validated, globals);
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
