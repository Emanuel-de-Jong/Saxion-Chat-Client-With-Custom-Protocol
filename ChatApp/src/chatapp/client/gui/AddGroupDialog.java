package chatapp.client.gui;

import javax.swing.*;
import java.awt.event.*;

public class AddGroupDialog extends JDialog {private JDialog dialog;

    private JPanel panel;
    private JScrollPane groupsScrollPane;
    private JList groupList;
    private JButton addButton;


    public AddGroupDialog() {
        dialog = new JDialog();
        dialog.setContentPane(panel);
        dialog.setModal(true);
        dialog.getRootPane().setDefaultButton(addButton);

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

        dialog.pack();
        dialog.setVisible(true);

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
    }

    private void createUIComponents() {
        groupsScrollPane = SwingBuilder.getBaseScrollPane();
        groupList = SwingBuilder.getBaseList();
        addButton = SwingBuilder.getBaseButton();
    }

}
