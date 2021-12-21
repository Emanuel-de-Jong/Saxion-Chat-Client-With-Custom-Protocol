package chatapp.client.gui;

import chatapp.client.ClientGlobals;
import chatapp.client.enums.LogLevel;
import chatapp.client.enums.MessageListOrigin;
import chatapp.client.interfaces.AddGroupDialogListener;
import chatapp.client.interfaces.ServerConnectionListener;
import chatapp.client.interfaces.UsersListener;
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

public class MainFrame implements ServerConnectionListener, AddGroupDialogListener,
        UserListener, GroupListener, UsersListener {

    private final ClientGlobals globals;

    private final JFrame frame;
    private JPanel panel;

    private JPanel leftPanel;
    private JButton addUserButton;
    private JScrollPane usersScrollPane;
    private JList userList;
    private final DefaultListModel<User> userListModel = new DefaultListModel<>();
    private JButton addGroupButton;
    private JScrollPane groupsScrollPane;
    private JList groupList;
    private final DefaultListModel<Group> groupListModel = new DefaultListModel<>();

    private JPanel rightPanel;
    private JScrollPane logScrollPane;
    private JList logList;
    private final DefaultListModel<Log> logListModel = new DefaultListModel<>();
    private JButton logOutButton;

    private JPanel chatPanel;
    private JLabel chatNameLabel;
    private JButton chatLeaveButton;

    private JScrollPane messagesScrollPane;
    private JList messageList;
    private final DefaultListModel<Message> messageListModel = new DefaultListModel<>();
    private MessageListOrigin messageListOrigin = MessageListOrigin.None;

    private JPanel messagePanel;
    private JButton messageUploadButton;
    private JTextField messageTextField;
    private JButton messageSendButton;

    public MainFrame(ClientGlobals globals) {
        this.globals = globals;

        globals.clientListeners.serverConnection.add(this);
        globals.clientListeners.addGroupDialog.add(this);
        globals.clientListeners.users.add(this);
        globals.listeners.user.add(this);
        globals.listeners.group.add(this);

        frame = new JFrame();
        frame.setResizable(false);
        frame.setContentPane(panel);
        frame.setTitle(globals.currentUser.getName());
        frame.setLocationRelativeTo(null);
        frame.getRootPane().setDefaultButton(messageSendButton);

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

        logList.setCellRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                String text = "<html><body style='width: 120px'>" + value + "</html>";
                setText(text);

                if (value instanceof Log) {
                    Log log = (Log) value;
                    if (log.getLevel() == LogLevel.Error) {
                        setForeground(SwingBuilder.redColor);
                    }
                }
                return c;
            }
        });

        userList.setModel(userListModel);
        groupList.setModel(groupListModel);
        logList.setModel(logListModel);
        messageList.setModel(messageListModel);

        createEventHandlers();

        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {
        leftPanel = new JPanel();
        leftPanel.setBorder(new MatteBorder(0, 0, 0, 1, SwingBuilder.foregroundColor));
        addUserButton = SwingBuilder.getBaseButton();
        usersScrollPane = SwingBuilder.getBaseScrollPane();
        userList = SwingBuilder.getBaseList();
        addGroupButton = SwingBuilder.getBaseButton();
        groupsScrollPane = SwingBuilder.getBaseScrollPane();
        groupList = SwingBuilder.getBaseList();

        rightPanel = new JPanel();
        rightPanel.setBorder(new MatteBorder(0, 1, 0, 0, SwingBuilder.foregroundColor));
        logScrollPane = SwingBuilder.getBaseScrollPane();
        logList = SwingBuilder.getBaseList();
        logOutButton = SwingBuilder.getBaseButton();

        chatPanel = new JPanel();
        chatPanel.setBorder(new MatteBorder(0, 0, 1, 0, SwingBuilder.foregroundColor));
        chatNameLabel = SwingBuilder.getBaseLabel();
        chatLeaveButton = SwingBuilder.getBaseButton();

        messagePanel = new JPanel();
        messagePanel.setBorder(new MatteBorder(1, 0, 0, 0, SwingBuilder.foregroundColor));
        messagesScrollPane = SwingBuilder.getBaseScrollPane();
        messageList = SwingBuilder.getBaseList();
        messageUploadButton = SwingBuilder.getBaseButton();
        messageTextField = SwingBuilder.getBaseTextField();
        messageSendButton = SwingBuilder.getBaseButton();
        messageSendButton.setBorder(new MatteBorder(1, 0, 1, 1, SwingBuilder.foregroundColor));
    }

    private void createEventHandlers() {
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                globals.systemHelper.exit();
            }
        });

        logOutButton.addActionListener(e -> globals.systemHelper.restart());

        addUserButton.addActionListener(e ->
                new AddUserDialog(globals));

        addGroupButton.addActionListener(e ->
                new AddGroupDialog(globals));

        chatLeaveButton.addActionListener(e -> {
            if (messageListOrigin == MessageListOrigin.Group) {
                Group group = (Group) groupList.getSelectedValue();
                if (!group.getName().equals(Globals.publicGroupName)) {
                    groupList.setSelectedIndex(0);
                    group.setJoined(false);
                }
            }
        });

        userList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                changeDM(null);
            }
        });

        userList.addListSelectionListener(this::changeDM);

        groupList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                changeGroup(null);
            }
        });

        groupList.addListSelectionListener(this::changeGroup);

        messageSendButton.addActionListener(this::sendMessage);
    }


    public JFrame getFrame() {
        return frame;
    }


    public void changeDM(ListSelectionEvent e) {
        User user = (User) userList.getSelectedValue();
        if (user == null)
            return;

        messageListModel.clear();
        messageListModel.addAll(user.getPrivateMessages());
        messageListOrigin = MessageListOrigin.User;

        groupList.clearSelection();
        chatNameLabel.setText(user.toString());
        chatLeaveButton.setVisible(false);
        messageUploadButton.setVisible(true);
    }

    public void changeGroup(ListSelectionEvent e) {
        Group group = (Group) groupList.getSelectedValue();
        if (group == null)
            return;

        messageListModel.clear();
        messageListModel.addAll(group.getMessages());
        messageListOrigin = MessageListOrigin.Group;

        userList.clearSelection();
        chatNameLabel.setText(group.toString());
        chatLeaveButton.setVisible(true);
        messageUploadButton.setVisible(false);
    }

    public void sendMessage(ActionEvent e) {
        if (messageListOrigin == MessageListOrigin.None)
            return;

        Message message = null;
        if (messageListOrigin == MessageListOrigin.User) {
            User user = (User) userList.getSelectedValue();
            message = new Message(messageTextField.getText(), globals.currentUser, user);
        } else if (messageListOrigin == MessageListOrigin.Group) {
            Group group = (Group) groupList.getSelectedValue();
            message = new Message(messageTextField.getText(), globals.currentUser, group);
        }

        Message finalMessage = message;
        globals.clientListeners.mainFrame.forEach(l -> l.sendMessage(finalMessage));

        messageTextField.setText("");
    }


    @Override
    public void chatPackageReceived(ChatPackage chatPackage) {
        switch (chatPackage.getType()) {
            case INFO:
                InfoPackage infoPackage = (InfoPackage) chatPackage;
                logListModel.addElement(new Log(
                        infoPackage.getMessage(),
                        "Server",
                        LogLevel.Info));
//            case ER:
//                ErPackage erPackage = (ErPackage) chatPackage;
//                logListModel.addElement(new Log(
//                        erPackage.getCode() + " " + erPackage.getMessage(),
//                        "Server",
//                        LogLevel.Error));
        }
    }

    @Override
    public void chatAddedSet(User user, boolean chatAdded) {
        if (chatAdded && !user.equals(globals.currentUser)) {
            System.out.println("MainFrame chatAddedSet " + user + " " + chatAdded);
            userListModel.addElement(user);
            userList.setSelectedValue(user, true);
        }
    }

    @Override
    public void joinedSet(Group group, boolean joined) {
        if (joined) {
            System.out.println("MainFrame joinedSet " + group + " " + joined);
            groupListModel.addElement(group);
            groupList.setSelectedValue(group, true);
        } else {
            groupListModel.removeElement(group);
        }
    }

    @Override
    public void createGroup(String name) {
    }

    @Override
    public void privateMessageAdded(User user, Message message) {
        if (messageListOrigin == MessageListOrigin.User &&
                user.equals(userList.getSelectedValue())) {
            System.out.println("MainFrame privateMessageAdded " + user + " " + message);
            messageListModel.addElement(message);
        }

        if (!message.getSender().equals(globals.currentUser)) {
            message.getSender().setChatAdded(true);
        }
    }

    @Override
    public void messageAdded(Group group, Message message) {
        if (messageListOrigin == MessageListOrigin.Group &&
                group.equals(groupList.getSelectedValue())) {
            System.out.println("MainFrame messageAdded " + group + " " + message);
            messageListModel.addElement(message);
        }
    }

    @Override
    public void userRemoved(User user) {
        userListModel.removeElement(user);
    }

    @Override
    public void userAdded(User user) {
    }

}
