package chatapp.client.gui.mainframe;

import chatapp.client.ClientGlobals;
import chatapp.client.enums.MessageListOrigin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame {

    private final ClientGlobals globals;
    private MessageListOrigin messageListOrigin = MessageListOrigin.None;

    private final JFrame frame;
    private final JPanel panel;

    private final SelectPanel selectPanel;
    private final ChatPanel chatPanel;
    private final ControlPanel controlPanel;

    public MainFrame(ClientGlobals globals) {
        this.globals = globals;

        panel = new JPanel();
        panel.setPreferredSize(new Dimension(800, 450));

        selectPanel = new SelectPanel(this, globals);
        chatPanel = new ChatPanel(this, globals);
        controlPanel = new ControlPanel(this, globals);

        selectPanel.setChatPanel(chatPanel);
        chatPanel.setSelectPanel(selectPanel);

        JPanel selectPanelPanel = selectPanel.getSelectPanel();
        JPanel chatPanelPanel = chatPanel.getChatPanel();
        JPanel controlPanelPanel = controlPanel.getControlPanel();

        selectPanelPanel.setPreferredSize(new Dimension(200, 0));
        selectPanelPanel.setMaximumSize(new Dimension(200, 0));
        controlPanelPanel.setPreferredSize(new Dimension(200, 0));
        controlPanelPanel.setMaximumSize(new Dimension(200, 0));

        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        panel.add(selectPanelPanel, c);
        c.gridx = 3;
        panel.add(controlPanelPanel, c);
        c.weightx = 1;
        c.gridx = 1;
        c.gridwidth = 2;
        panel.add(chatPanelPanel, c);

        frame = new JFrame();
        frame.setContentPane(panel);

        frame.setResizable(false);
        frame.setTitle(globals.currentUser.getName());
        frame.setLocationRelativeTo(null);
        frame.getRootPane().setDefaultButton(chatPanel.getMessageSendButton());

        createEventHandlers();

        frame.pack();
        frame.setVisible(true);
    }

    private void createEventHandlers() {
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                globals.systemHelper.exit();
            }
        });
    }


    public JFrame getFrame() {
        return frame;
    }

    public MessageListOrigin getMessageListOrigin() {
        return messageListOrigin;
    }

    public void setMessageListOrigin(MessageListOrigin messageListOrigin) {
        this.messageListOrigin = messageListOrigin;
    }

}
