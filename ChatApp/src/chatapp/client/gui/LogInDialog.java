package chatapp.client.gui;

import chatapp.client.Globals;
import chatapp.client.interfaces.LogInDialogListener;
import chatapp.shared.models.User;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

public class LogInDialog {

    public static ArrayList<LogInDialogListener> listeners = new ArrayList<>();

    private Globals globals;

    private JDialog dialog;
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


    public LogInDialog(Globals globals) {
        this.globals = globals;

        dialog = new JDialog();
        dialog.setContentPane(panel);
        dialog.setModal(true);
        dialog.getRootPane().setDefaultButton(tempButton);

        accountLogInButton.addActionListener(e -> {
            close();
        });

        tempButton.addActionListener(e -> {
            globals.currentUser = new User(tempNameTextField.getText());
            close();
        });

        dialog.setDefaultCloseOperation(dialog.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dialog.dispose();
            }
        });

        panel.registerKeyboardAction(e -> {
            close();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        dialog.setLocationRelativeTo(null);
        dialog.pack();
        dialog.setVisible(true);
    }

    private void close() {
        dialog.dispose();
        listeners.forEach(l -> l.logInDialogClosed());
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
