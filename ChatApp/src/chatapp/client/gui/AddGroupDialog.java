package chatapp.client.gui;

import chatapp.client.data.Groups;
import chatapp.client.interfaces.AddGroupDialogListener;
import chatapp.shared.models.Group;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

public class AddGroupDialog extends JDialog {

    private JDialog dialog;

    public static ArrayList<AddGroupDialogListener> listeners = new ArrayList<>();

    private JPanel panel;

    private JLabel searchLabel;
    private JTextField searchTextField;
    private JScrollPane groupsScrollPane;
    private JList groupList;
    private JButton addButton;

    private JLabel nameLabel;
    private JTextField nameTextField;
    private JButton createButton;


    public AddGroupDialog() {
        dialog = new JDialog();
        dialog.setContentPane(panel);
        dialog.setModal(true);
        dialog.getRootPane().setDefaultButton(addButton);

        groupList.setListData(Groups.instance.getGroups().values().toArray());

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
        groupsScrollPane = SwingBuilder.getBaseScrollPane();
        groupList = SwingBuilder.getBaseList();
        addButton = SwingBuilder.getBaseButton();

        nameLabel = SwingBuilder.getBaseLabel();
        nameTextField = SwingBuilder.getBaseTextField();
        createButton = SwingBuilder.getBaseButton();
    }

    private void close() {
        listeners.forEach(l -> l.groupSelected((Group) groupList.getSelectedValue()));
        dialog.dispose();
    }

}
