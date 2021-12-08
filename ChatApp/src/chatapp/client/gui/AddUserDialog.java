package chatapp.client.gui;

import chatapp.client.ClientGlobals;
import chatapp.client.interfaces.AddUserDialogListener;
import chatapp.shared.models.User;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

public class AddUserDialog extends JDialog {

    public static ArrayList<AddUserDialogListener> listeners = new ArrayList<>();

    private final ClientGlobals globals;

    private final JDialog dialog;
    private JPanel panel;

    private JScrollPane usersScrollPane;
    private JList userList;
    private final DefaultListModel<User> userListModel = new DefaultListModel<>();
    private JButton addButton;
    private JTextField searchTextField;
    private JLabel searchLabel;


    public AddUserDialog(ClientGlobals globals) {
        this.globals = globals;
        dialog = new JDialog();
        dialog.setContentPane(panel);
        dialog.setModal(true);
        dialog.getRootPane().setDefaultButton(addButton);

        userListModel.addAll(globals.users.valuesByChatAdded(false));
        userList.setModel(userListModel);

        addButton.addActionListener(e -> close());


        usersScrollPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2){
                    close();
                }
            }
        });

        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                close();
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

    private void createUIComponents() {
        searchLabel = SwingBuilder.getBaseLabel();
        searchTextField = SwingBuilder.getBaseTextField();
        usersScrollPane = SwingBuilder.getBaseScrollPane();
        userList = SwingBuilder.getBaseList();
        addButton = SwingBuilder.getBaseButton();
    }

    private void close() {
        if (userList.getSelectedIndex() != -1) {
            User user = (User) userList.getSelectedValue();

            if (user.isChatAdded() == false) {
                user.setChatAdded(true);
            }
        }

        dialog.dispose();
    }

}
