package chatapp.client.gui;

import javax.swing.*;
import java.awt.event.*;

public class AddGroupDialog extends JDialog {private JDialog dialog;

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

        groupList.setListData(new String[] {
                "item1",
                "item2",
                "item1",
                "item2",
                "item1",
                "item2",
                "item1",
                "item2",
                "item1",
                "item2",
                "item1",
                "item2",
                "item1",
                "item2",
                "item1",
                "item2",
                "item1",
                "item2",
                "item1",
                "item2",
                "item1",
                "item2",
                "item1",
                "item2",
                "item1",
                "item2",
                "item1",
                "item2",
                "item1",
        });

        addButton.addActionListener(e -> {
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

}
