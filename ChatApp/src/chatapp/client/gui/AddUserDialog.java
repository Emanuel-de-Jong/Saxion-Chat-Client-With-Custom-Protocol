package chatapp.client.gui;

import chatapp.client.ClientGlobals;
import chatapp.client.interfaces.AddUserDialogListener;
import chatapp.shared.models.User;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

public class AddUserDialog {

    private final ClientGlobals globals;

    private final JDialog dialog;
    private JPanel panel;

    private JScrollPane usersScrollPane;
    private JList userList;
    private final DefaultListModel<User> userListModel = new DefaultListModel<>();
    private JButton addButton;


    public AddUserDialog(ClientGlobals globals) {
        this.globals = globals;

        dialog = new JDialog();
        dialog.setResizable(false);
        dialog.setContentPane(panel);
        dialog.setModal(true);
        dialog.getRootPane().setDefaultButton(addButton);
        dialog.setLocationRelativeTo(null);

        userListModel.addAll(globals.users.valuesByChatAdded(false));
        userList.setModel(userListModel);

        addButton.addActionListener(e -> close());

        userList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2){
                    close();
                }
            }
        });

        dialog.pack();
        dialog.setVisible(true);
    }

    private void createUIComponents() {
        usersScrollPane = SwingBuilder.getBaseScrollPane();
        userList = SwingBuilder.getBaseList();
        addButton = SwingBuilder.getBaseButton();
    }

    private void close() {
        if (userList.getSelectedIndex() != -1) {
            User user = (User) userList.getSelectedValue();
            user.setChatAdded(true);
        }

        dialog.dispose();
    }

}
