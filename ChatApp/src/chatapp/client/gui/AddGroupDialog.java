package chatapp.client.gui;

import chatapp.client.ClientGlobals;
import chatapp.client.interfaces.GroupsListener;
import chatapp.shared.models.Group;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AddGroupDialog implements GroupsListener {

    private final ClientGlobals globals;

    private final JDialog dialog;
    private JPanel panel;

    private JScrollPane groupsScrollPane;
    private JList groupList;
    private final DefaultListModel<Group> groupListModel = new DefaultListModel<>();
    private JButton addButton;

    private JLabel nameLabel;
    private JTextField nameTextField;
    private JButton createButton;


    public AddGroupDialog(ClientGlobals globals) {
        this.globals = globals;
        globals.clientListeners.groups.add(this);

        dialog = new JDialog();
        dialog.setResizable(false);
        dialog.setContentPane(panel);
        dialog.setModal(true);
        dialog.getRootPane().setDefaultButton(addButton);
        dialog.setLocationRelativeTo(null);

        groupListModel.addAll(globals.groups.valuesByJoined(false));
        groupList.setModel(groupListModel);

        addButton.addActionListener(e -> close());

        groupList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    close();
                }
            }
        });

        nameTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                dialog.getRootPane().setDefaultButton(createButton);
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                dialog.getRootPane().setDefaultButton(addButton);
            }
        });

        createButton.addActionListener(e -> {
            globals.clientListeners.addGroupDialog.forEach(l -> l.createGroup(nameTextField.getText()));
        });

        dialog.pack();
        dialog.setVisible(true);
    }

    private void createUIComponents() {
        groupsScrollPane = SwingBuilder.getBaseScrollPane();
        groupList = SwingBuilder.getBaseList();
        addButton = SwingBuilder.getBaseButton();

        nameLabel = SwingBuilder.getBaseLabel();
        nameTextField = SwingBuilder.getBaseTextField();
        createButton = SwingBuilder.getBaseButton();
    }

    private void close() {
        if (groupList.getSelectedIndex() != -1) {
            Group group = (Group) groupList.getSelectedValue();
            group.setJoined(true);
        }

        dialog.dispose();
    }

    @Override
    public void groupAdded(Group group) {
        System.out.println("C: AddGroupDialog groupAdded " + group);
        groupListModel.addElement(group);

        if (group.getName().equals(nameTextField.getText())) {
            groupList.setSelectedValue(group, true);
            close();
        }
    }

}
