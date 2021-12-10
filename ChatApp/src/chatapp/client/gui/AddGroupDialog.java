package chatapp.client.gui;

import chatapp.client.ClientGlobals;
import chatapp.client.data.Groups;
import chatapp.client.interfaces.AddGroupDialogListener;
import chatapp.client.interfaces.GroupsListener;
import chatapp.shared.models.Group;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

public class AddGroupDialog implements GroupsListener {

    private final ClientGlobals globals;

    private final JDialog dialog;
    private JPanel panel;

    private JLabel searchLabel;
    private JTextField searchTextField;
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

        groupListModel.addAll(globals.groups.valuesByJoined(false));
        groupList.setModel(groupListModel);

        addButton.addActionListener(e -> close());

        groupList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2){
                    close();
                }
            }
        });

        createButton.addActionListener(e -> {
            globals.clientListeners.addGroupDialog.forEach(l -> l.createGroup(nameTextField.getText()));
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

            if (group.isJoined() == false) {
                group.setJoined(true);
            }
        }

        dialog.dispose();
    }

    @Override
    public void groupAdded(Group group) {
        groupListModel.addElement(group);
    }

}
