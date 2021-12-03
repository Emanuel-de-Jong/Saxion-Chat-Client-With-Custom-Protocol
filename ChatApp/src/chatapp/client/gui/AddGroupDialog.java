package chatapp.client.gui;

import chatapp.client.Globals;
import chatapp.client.ServerConnection;
import chatapp.client.data.Groups;
import chatapp.client.interfaces.AddGroupDialogListener;
import chatapp.client.interfaces.GroupsListener;
import chatapp.client.interfaces.ServerConnectionListener;
import chatapp.shared.enums.ChatPackageType;
import chatapp.shared.models.Group;
import chatapp.shared.models.chatpackages.ChatPackage;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

public class AddGroupDialog extends JDialog implements GroupsListener {

    public static ArrayList<AddGroupDialogListener> listeners = new ArrayList<>();

    private Globals globals;

    private JDialog dialog;
    private JPanel panel;

    private JLabel searchLabel;
    private JTextField searchTextField;
    private JScrollPane groupsScrollPane;
    private JList groupList;
    private DefaultListModel<Group> groupListModel = new DefaultListModel<>();
    private JButton addButton;

    private JLabel nameLabel;
    private JTextField nameTextField;
    private JButton createButton;


    public AddGroupDialog(Globals globals) {
        this.globals = globals;

        Groups.listeners.add(this);

        dialog = new JDialog();
        dialog.setContentPane(panel);
        dialog.setModal(true);
        dialog.getRootPane().setDefaultButton(addButton);

        groupListModel.addAll(globals.groups.getGroups().values());
        groupList.setModel(groupListModel);

        addButton.addActionListener(e -> {
            close();
        });

        createButton.addActionListener(e -> {
            listeners.forEach(l -> l.createGroup(nameTextField.getText()));
        });

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
        groupsScrollPane = SwingBuilder.getBaseScrollPane();
        groupList = SwingBuilder.getBaseList();
        addButton = SwingBuilder.getBaseButton();

        nameLabel = SwingBuilder.getBaseLabel();
        nameTextField = SwingBuilder.getBaseTextField();
        createButton = SwingBuilder.getBaseButton();
    }

    private void close() {
        Group group = (Group) groupList.getSelectedValue();
        group.setJoined(true);
        if (group != null)
            listeners.forEach(l -> l.groupSelected(group));
        dialog.dispose();
    }

    @Override
    public void groupAdded(Group group) {
        groupListModel.addElement(group);
    }

}
