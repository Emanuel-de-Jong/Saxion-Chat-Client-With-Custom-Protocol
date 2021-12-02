package chatapp.client.gui;

import chatapp.client.data.Users;
import chatapp.client.interfaces.AddUserDialogListener;
import chatapp.shared.models.User;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

public class AddUserDialog extends JDialog {

    private JDialog dialog;

    public static ArrayList<AddUserDialogListener> listeners = new ArrayList<>();

    private JPanel panel;
    private JScrollPane usersScrollPane;
    private JList userList;
    private JButton addButton;
    private JTextField searchTextField;
    private JLabel searchLabel;


    public AddUserDialog() {
        dialog = new JDialog();
        dialog.setContentPane(panel);
        dialog.setModal(true);
        dialog.getRootPane().setDefaultButton(addButton);

        userList.setListData(Users.instance.getUsers().values().toArray());

        addButton.addActionListener(e -> {
            close();
        });

        dialog.setDefaultCloseOperation(dialog.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                close();
            }
        });

        panel.registerKeyboardAction(e -> {
            close();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        dialog.setLocationRelativeTo(null);
        dialog.pack();
        dialog.setVisible(true);
    }

    private void createUIComponents() {
        searchLabel = SwingBuilder.getBaseLabel();
        searchTextField = SwingBuilder.getBaseTextField();
        usersScrollPane = SwingBuilder.getBaseScrollPane();
        userList = SwingBuilder.getBaseList();
        addButton = SwingBuilder.getBaseButton();
    }

    private void close() {
        User user = (User) userList.getSelectedValue();
        if (user != null)
            listeners.forEach(l -> l.userSelected(user));
        dialog.dispose();
    }

}
