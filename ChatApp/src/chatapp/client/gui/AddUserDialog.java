package chatapp.client.gui;

import javax.swing.*;
import java.awt.event.*;

public class AddUserDialog extends JDialog {private JDialog dialog;

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

        userList.setListData(new String[] {
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
        usersScrollPane = SwingBuilder.getBaseScrollPane();
        userList = SwingBuilder.getBaseList();
        addButton = SwingBuilder.getBaseButton();
    }

}
