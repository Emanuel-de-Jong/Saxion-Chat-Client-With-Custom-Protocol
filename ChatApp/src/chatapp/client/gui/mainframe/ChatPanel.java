package chatapp.client.gui.mainframe;

import chatapp.client.ClientGlobals;
import chatapp.client.enums.LogLevel;
import chatapp.client.enums.MessageListOrigin;
import chatapp.client.gui.AddGroupDialog;
import chatapp.client.gui.AddUserDialog;
import chatapp.client.gui.SwingBuilder;
import chatapp.client.models.Log;
import chatapp.shared.Globals;
import chatapp.shared.interfaces.GroupListener;
import chatapp.shared.interfaces.UserListener;
import chatapp.shared.models.Group;
import chatapp.shared.models.Message;
import chatapp.shared.models.User;
import chatapp.shared.models.chatpackages.ChatPackage;
import chatapp.shared.models.chatpackages.ErPackage;
import chatapp.shared.models.chatpackages.InfoPackage;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.*;

public class ChatPanel implements UserListener, GroupListener {

    private MainFrame mainFrame;
    private SelectPanel selectPanel;
    private ClientGlobals globals;

    private JPanel chatPanel;
    private JPanel chatControlPanel;
    private JLabel chatNameLabel;
    private JButton chatLeaveButton;
    private JScrollPane messagesScrollPane;
    private JList messageList;
    private final DefaultListModel<Message> messageListModel = new DefaultListModel<>();
    private JPanel sendPanel;
    private JButton messageUploadButton;
    private JTextField messageTextField;
    private JButton messageSendButton;
    private JPanel messagesPanel;

    public ChatPanel(MainFrame mainFrame, ClientGlobals globals) {
        this.mainFrame = mainFrame;
        this.globals = globals;

        globals.listeners.user.add(this);
        globals.listeners.group.add(this);

        messageList.setCellRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value.toString());
                if (value instanceof Message) {
                    Message message = (Message) value;
                    if (message.getSender() == null) {
                        setForeground(SwingBuilder.yellowColor);
                    }
                }
                return c;
            }
        });

        messageList.setModel(messageListModel);

        createEventHandlers();
    }

    private void createUIComponents() {
        chatPanel = new JPanel();
        chatControlPanel = new JPanel();
        chatControlPanel.setBorder(new MatteBorder(0, 0, 1, 0, SwingBuilder.foregroundColor));
        chatNameLabel = SwingBuilder.getBaseLabel();
        chatLeaveButton = SwingBuilder.getBaseButton();
        sendPanel = new JPanel();
        sendPanel.setBorder(new MatteBorder(1, 0, 0, 0, SwingBuilder.foregroundColor));
        messagesScrollPane = SwingBuilder.getBaseScrollPane();
        messageList = SwingBuilder.getBaseList();
        messageUploadButton = SwingBuilder.getBaseButton();
        messageTextField = SwingBuilder.getBaseTextField();
        messageSendButton = SwingBuilder.getBaseButton();
        messageSendButton.setBorder(new MatteBorder(1, 0, 1, 1, SwingBuilder.foregroundColor));
    }

    private void createEventHandlers() {
        chatLeaveButton.addActionListener(e -> {
            if (mainFrame.getMessageListOrigin() == MessageListOrigin.Group) {
                Group group = (Group) selectPanel.getGroupList().getSelectedValue();
                if (!group.getName().equals(Globals.publicGroupName)) {
                    group.setJoined(false);
                }
            }
        });

        messageSendButton.addActionListener(this::sendMessage);
    }


    public void setSelectPanel(SelectPanel selectPanel) {
        this.selectPanel = selectPanel;
    }

    public JPanel getChatPanel() {
        return chatPanel;
    }

    public JButton getMessageSendButton() {
        return messageSendButton;
    }

    public DefaultListModel<Message> getMessageListModel() {
        return messageListModel;
    }

    public JLabel getChatNameLabel() {
        return chatNameLabel;
    }

    public JButton getChatLeaveButton() {
        return chatLeaveButton;
    }

    public JButton getMessageUploadButton() {
        return messageUploadButton;
    }


    public void sendMessage(ActionEvent e) {
        MessageListOrigin messageListOrigin = mainFrame.getMessageListOrigin();
        if (messageListOrigin == MessageListOrigin.None) return;

        String text = messageTextField.getText();
        if (text.equals("")) return;

        Message message = null;
        if (messageListOrigin == MessageListOrigin.User) {
            User user = (User) selectPanel.getUserList().getSelectedValue();
            message = new Message(text, globals.currentUser, user);
        } else if (messageListOrigin == MessageListOrigin.Group) {
            Group group = (Group) selectPanel.getGroupList().getSelectedValue();
            message = new Message(text, globals.currentUser, group);
        }

        Message finalMessage = message;
        globals.clientListeners.mainFrame.forEach(l -> l.sendMessage(finalMessage));

        messageTextField.setText("");
    }

    @Override
    public void privateMessageAdded(User user, Message message) {
        System.out.println("C: MainFrame privateMessageAdded " + user + " " + message);
        if (mainFrame.getMessageListOrigin() == MessageListOrigin.User &&
                user.equals(selectPanel.getUserList().getSelectedValue())) {
            messageListModel.addElement(message);
        } else if (!message.getSender().equals(globals.currentUser)) {
            message.getSender().setChatAdded(true);
        }
    }

    @Override
    public void chatAddedSet(User user, boolean chatAdded) {
    }

    @Override
    public void messageAdded(Group group, Message message) {
        if (mainFrame.getMessageListOrigin() == MessageListOrigin.Group &&
                group.equals(selectPanel.getGroupList().getSelectedValue())) {
            System.out.println("C: MainFrame chatPackageReceived " + group + " " + message);
            messageListModel.addElement(message);
        }
    }

    @Override
    public void joinedSet(Group group, boolean joined) {
    }

}
