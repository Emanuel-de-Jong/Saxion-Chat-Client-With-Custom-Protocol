package chatapp.client.gui.mainframe;

import chatapp.client.ClientGlobals;
import chatapp.client.enums.MessageListOrigin;
import chatapp.client.filetransfer.UploadHandler;
import chatapp.client.gui.SwingBuilder;
import chatapp.shared.interfaces.GroupListener;
import chatapp.shared.interfaces.UserListener;
import chatapp.shared.models.Group;
import chatapp.shared.models.Message;
import chatapp.shared.models.User;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;

public class ChatPanel implements UserListener, GroupListener {

    private final MainFrame mainFrame;
    private SelectPanel selectPanel;
    private final ClientGlobals globals;

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
    private JScrollBar messagesScrollBar;

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
        messagesScrollBar = messagesScrollPane.getVerticalScrollBar();
    }

    private void createEventHandlers() {
        chatLeaveButton.addActionListener(e -> {
            if (mainFrame.getMessageListOrigin() == MessageListOrigin.Group) {
                Group group = (Group) selectPanel.getGroupList().getSelectedValue();
                group.setJoined(false);
            }
        });

        messageSendButton.addActionListener(this::sendMessage);

        messageUploadButton.addActionListener(this::uploadFile);

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
        globals.clientListeners.chatPanel.forEach(l -> l.sendMessage(finalMessage));

        messageTextField.setText("");
    }

    private void uploadFile(ActionEvent actionEvent) {
        if (mainFrame.getMessageListOrigin() != MessageListOrigin.User) return;

        final JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(messageUploadButton);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                byte[] file = Files.readAllBytes(fc.getSelectedFile().toPath());
                String fileName = fc.getSelectedFile().getName();
                User user = (User) selectPanel.getUserList().getSelectedValue();
                new UploadHandler(file, fileName, user, globals, "MD5");
            } catch (IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void privateMessageAdded(User user, Message message) {
        if (mainFrame.getMessageListOrigin() == MessageListOrigin.User &&
                user.equals(selectPanel.getUserList().getSelectedValue())) {
            globals.systemHelper.log("MainFrame privateMessageAdded " + user + " " + message);
            messageListModel.addElement(message);
            moveScrollBarToBottom();
        }
    }

    @Override
    public void chatAddedSet(User user, boolean chatAdded) {
    }

    @Override
    public void messageAdded(Group group, Message message) {
        if (mainFrame.getMessageListOrigin() == MessageListOrigin.Group &&
                group.equals(selectPanel.getGroupList().getSelectedValue())) {
            globals.systemHelper.log("MainFrame messageAdded " + group + " " + message);
            messageListModel.addElement(message);
            moveScrollBarToBottom();
        }
    }

    private void moveScrollBarToBottom() {
        if (messagesScrollBar.getMaximum() - messagesScrollBar.getValue() - messagesScrollBar.getSize().height < .05 * messagesScrollBar.getMaximum() + 20) {
            messagesScrollBar.setValue(messagesScrollBar.getMaximum());

            //requires a sleep because the scrollbar takes a few microseconds to update
            // and to make sure it doesn't block anything it's made in a new thread.
            new Thread(() -> {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                messagesScrollBar.setValue(messagesScrollBar.getMaximum());
            }).start();
        }
    }

    @Override
    public void joinedSet(Group group, boolean joined) {
    }

}
