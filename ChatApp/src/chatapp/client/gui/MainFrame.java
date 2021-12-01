package chatapp.client.gui;

import chatapp.client.Config;
import chatapp.client.ServerConnection;
import chatapp.client.enums.MessageListOrigin;
import chatapp.client.interfaces.*;
import chatapp.client.models.Group;
import chatapp.client.models.Message;
import chatapp.client.models.User;
import chatapp.shared.enums.ChatPackageType;
import chatapp.shared.models.chatpackages.BcstPackage;
import chatapp.shared.models.chatpackages.ChatPackage;
import chatapp.shared.models.chatpackages.ConnPackage;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame implements ServerConnectionListener, AddUserDialogListener, AddGroupDialogListener, UserListener, GroupListener {

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

    public MainFrame() {
        ServerConnection.listeners.add(this);
        AddUserDialog.listeners.add(this);
        AddGroupDialog.listeners.add(this);
        Group.listeners.add(this);

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

        createEventHandlers();
    }

    private void createEventHandlers() {
        addUserButton.addActionListener(e -> {
            new AddUserDialog();
        });

        addGroupButton.addActionListener(e -> {
            new AddGroupDialog();
        });

        logOutButton.addActionListener(e -> {
            new LogInDialog();
        });

        userList.addListSelectionListener(e -> {
            User user = (User) userList.getSelectedValue();
            messageListModel.clear();
            messageListModel.addAll(user.getPrivateMessages());
            messageListOrigin = MessageListOrigin.User;

            infoTextPane.setText("");
        });

        groupList.addListSelectionListener(e -> {
            Group group = (Group) groupList.getSelectedValue();
            messageListModel.clear();
            messageListModel.addAll(group.getMessages());
            messageListOrigin = MessageListOrigin.Group;

            String info = "Users in group:\n";
            for (User user : group.getUsers()) {
                info += user.getName() + "\n";
            }
            infoTextPane.setText(info);
        });

        messageSendButton.addActionListener(e -> {
            if (messageListOrigin == MessageListOrigin.None)
                return;

            Message message = new Message(messageTextField.getText(), Config.currentUser);
            if (messageListOrigin == MessageListOrigin.User) {
                User user = (User) userList.getSelectedValue();
                user.addPrivateMessage(message);
            } else {
                Group group = (Group) groupList.getSelectedValue();
                group.addMessage(message);
            }

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
        if (chatPackage.getType() == ChatPackageType.BCST) {
            BcstPackage bcstPackage = (BcstPackage) chatPackage;
            System.out.println(bcstPackage);
        } else if (chatPackage.getType() == ChatPackageType.CONN) {
            ConnPackage connPackage = (ConnPackage) chatPackage;
            System.out.println(connPackage);
        }
    }


    @Override
    public void userSelected(User user) {
        userListModel.addElement(user);
    }

    @Override
    public void groupSelected(Group group) {
        groupListModel.addElement(group);
    }

    @Override
    public void privateMessageAdded(User user, Message message) {
        if (messageListOrigin == MessageListOrigin.User &&
                user.equals(userList.getSelectedValue())) {
            messageListModel.addElement(message);
        }
    }

    @Override
    public void messageAdded(Group group, Message message) {
        if (messageListOrigin == MessageListOrigin.Group &&
        group.equals(groupList.getSelectedValue())) {
            messageListModel.addElement(message);
        }
    }

}
