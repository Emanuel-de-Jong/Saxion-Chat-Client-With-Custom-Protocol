package chatapp.client.gui;

import javax.swing.*;
import java.awt.event.*;

public class LogInDialog {

    private JDialog dialog;
    private JPanel panel;

    private JTextField tempNameTextField;
    private JButton tempButton;

    private JTextField accountNameTextField;
    private JPasswordField accountPasswordField;
    private JButton accountLogInButton;
    private JButton accountRegisterButton;


    public LogInDialog() {
        dialog = new JDialog();
        dialog.setContentPane(panel);
        dialog.setModal(true);
        dialog.getRootPane().setDefaultButton(accountLogInButton);

        accountLogInButton.addActionListener(e -> {
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
        tempNameTextField = SwingBuilder.getBaseTextField();
        tempButton = SwingBuilder.getBaseButton();

        accountNameTextField = SwingBuilder.getBaseTextField();
        accountPasswordField = SwingBuilder.getBasePasswordField();
        accountLogInButton = SwingBuilder.getBaseButton();
        accountRegisterButton = SwingBuilder.getBaseButton();
    }

}
