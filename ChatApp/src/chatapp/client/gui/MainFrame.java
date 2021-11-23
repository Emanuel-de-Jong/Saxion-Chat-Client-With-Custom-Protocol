package chatapp.client.gui;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame {

    private JFrame frame;
    private JPanel panel;

    private JPanel leftPanel;
    private JPanel usersPanel;
    private JList userList;
    private JPanel groupsPanel;
    private JList groupList;

    private JPanel rightPanel;
    private JButton logOutButton;

    private JPanel messagePanel;
    private JTextField messageTextField;
    private JButton messageSendButton;

    public MainFrame() {
        frame = new JFrame();
        frame.setContentPane(panel);

        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                frame.dispose();
            }
        });
    }

    private void createUIComponents() {
        leftPanel = new JPanel();
        leftPanel.setBorder(new MatteBorder(0, 0, 0, 1, SwingConfig.foregroundColor));

        rightPanel = new JPanel();
        rightPanel.setBorder(new MatteBorder(0, 1, 0, 0, SwingConfig.foregroundColor));
        logOutButton = SwingConfig.getBaseButton();

        messagePanel = new JPanel();
        messagePanel.setBorder(new MatteBorder(1, 0, 0, 0, SwingConfig.foregroundColor));
        messageTextField = SwingConfig.getBaseTextField();
        messageSendButton = SwingConfig.getBaseButton();
        messageSendButton.setBorder(new MatteBorder(1, 0, 1, 1, SwingConfig.foregroundColor));

    }

}
