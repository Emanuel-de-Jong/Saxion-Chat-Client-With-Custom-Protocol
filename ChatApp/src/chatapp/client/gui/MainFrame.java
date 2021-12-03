package chatapp.client.gui;

import chatapp.client.ClientGlobals;
import chatapp.client.ServerConnection;
import chatapp.client.data.Groups;
import chatapp.client.data.Users;
import chatapp.client.enums.MessageListOrigin;
import chatapp.client.interfaces.*;
import chatapp.shared.SharedConfig;
import chatapp.shared.interfaces.GroupListener;
import chatapp.shared.interfaces.UserListener;
import chatapp.shared.models.Group;
import chatapp.shared.models.Message;
import chatapp.shared.models.User;
import chatapp.shared.models.chatpackages.ChatPackage;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class MainFrame implements ServerConnectionListener, AddUserDialogListener, AddGroupDialogListener,
        UserListener, GroupListener, UsersListener, GroupsListener {

    public static ArrayList<MainFrameListener> listeners = new ArrayList<>();

    private ClientGlobals globals;
    private boolean autoListUsersAndGroups;

    private JFrame frame;
    private JPanel panel;

    private JPanel leftPanel;
    private JButton addUserButton;
    private JScrollPane usersScrollPane;
    private JList userList;
    private DefaultListModel<User> userListModel = new DefaultListModel<>();
    private JButton addGroupButton;
    private JScrollPane groupsScrollPane;
    private JList groupList;
    private DefaultListModel<Group> groupListModel = new DefaultListModel<>();

    private JPanel rightPanel;
    private JTextPane infoTextPane;
    private JButton logOutButton;

    private JPanel messagePanel;
    private JScrollPane messagesScrollPane;
    private JList messageList;
    private DefaultListModel<Message> messageListModel = new DefaultListModel<>();
    private MessageListOrigin messageListOrigin = MessageListOrigin.None;
    private JTextField messageTextField;
    private JButton messageSendButton;

    public MainFrame(ClientGlobals globals, boolean autoListUsersAndGroups) {
        this.globals = globals;
        this.autoListUsersAndGroups = autoListUsersAndGroups;

        ServerConnection.listeners.add(this);
        AddUserDialog.listeners.add(this);
        AddGroupDialog.listeners.add(this);
        User.listeners.add(this);
        Group.listeners.add(this);
        Users.listeners.add(this);
        Groups.listeners.add(this);

        frame = new JFrame();
        frame.setContentPane(panel);

        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
        frame.getRootPane().setDefaultButton(messageSendButton);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                frame.dispose();
            }
        });

        userList.setModel(userListModel);
        groupList.setModel(groupListModel);
        messageList.setModel(messageListModel);

        if (autoListUsersAndGroups) {
            userListModel.addAll(globals.users.getUsers().values());
            groupListModel.addAll(globals.groups.getGroups().values());
        }

        createEventHandlers();
    }

    public DefaultListModel<User> getUserListModel() {
        return userListModel;
    }

    public DefaultListModel<Group> getGroupListModel() {
        return groupListModel;
    }

    private void createEventHandlers() {
        addUserButton.addActionListener(e -> {
            new AddUserDialog(globals);
        });

        addGroupButton.addActionListener(e -> {
            new AddGroupDialog(globals);
        });

        logOutButton.addActionListener(e -> {
            new LogInDialog(globals, "LoggedOut");
        });

        userList.addListSelectionListener(e -> {
            User user = (User) userList.getSelectedValue();
            messageListModel.clear();
            messageListModel.addAll(user.getPrivateMessages());
            messageListOrigin = MessageListOrigin.User;

            infoTextPane.setText("Current user: " + user);
        });

        groupList.addListSelectionListener(e -> {
            Group group = (Group) groupList.getSelectedValue();
            messageListModel.clear();
            messageListModel.addAll(group.getMessages());
            messageListOrigin = MessageListOrigin.Group;

            infoTextPane.setText("Current group: " + group);
        });

        messageSendButton.addActionListener(e -> {
            if (messageListOrigin == MessageListOrigin.None)
                return;

            Message message = null;
            if (messageListOrigin == MessageListOrigin.User) {
                User user = (User) userList.getSelectedValue();
                message = new Message(messageTextField.getText(), globals.currentUser, user);
                user.addPrivateMessage(message);
            }
            else if (messageListOrigin == MessageListOrigin.Group) {
                Group group = (Group) groupList.getSelectedValue();
                message = new Message(messageTextField.getText(), globals.currentUser, group);
                group.addMessage(message);
            }

            Message finalMessage = message;
            listeners.forEach(l -> l.sendMessage(finalMessage));

            messageTextField.setText("");
        });
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
        logOutButton = SwingBuilder.getBaseButton();

        messagePanel = new JPanel();
        messagePanel.setBorder(new MatteBorder(1, 0, 0, 0, SwingBuilder.foregroundColor));
        messagesScrollPane = SwingBuilder.getBaseScrollPane();
        messageList = SwingBuilder.getBaseList();
        messageTextField = SwingBuilder.getBaseTextField();
        messageSendButton = SwingBuilder.getBaseButton();
        messageSendButton.setBorder(new MatteBorder(1, 0, 1, 1, SwingBuilder.foregroundColor));

    }

    @Override
    public void chatPackageReceived(ChatPackage chatPackage) {

    }


    @Override
    public void userSelected(User user) {
        System.out.println("MainFrame userSelected " + user);
        if (!userListModel.contains(user))
            userListModel.addElement(user);
    }

    @Override
    public void groupSelected(Group group) {
        System.out.println("MainFrame groupSelected " + group);
        if (!groupListModel.contains(group))
            groupListModel.addElement(group);
    }

    @Override
    public void createGroup(String name) {}

    @Override
    public void privateMessageAdded(User user, Message message) {
        System.out.println("MainFrame privateMessageAdded " + user + " " + message);
        if (messageListOrigin == MessageListOrigin.User &&
                user.equals(userList.getSelectedValue())) {
            messageListModel.addElement(message);
        }
    }

    @Override
    public void messageAdded(Group group, Message message) {
        System.out.println("MainFrame messageAdded " + group + " " + message);
        if (messageListOrigin == MessageListOrigin.Group &&
        group.equals(groupList.getSelectedValue())) {
            messageListModel.addElement(message);
        }
    }

    @Override
    public void userAdded(User user) {
        if (autoListUsersAndGroups && !userListModel.contains(user)) {
            System.out.println("MainFrame userAdded " + user);
            userListModel.addElement(user);
        }
    }

    @Override
    public void groupAdded(Group group) {
        if ((autoListUsersAndGroups && !groupListModel.contains(group)) ||
                group.getName().equals(SharedConfig.publicGroupName)) {
            System.out.println("MainFrame groupAdded " + group);
            groupListModel.addElement(group);
        }
    }

}
