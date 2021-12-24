package chatapp.client.gui.mainframe;

import chatapp.client.ClientGlobals;
import chatapp.client.enums.MessageListOrigin;
import chatapp.client.gui.AddGroupDialog;
import chatapp.client.gui.AddUserDialog;
import chatapp.client.gui.SwingBuilder;
import chatapp.client.interfaces.UsersListener;
import chatapp.shared.Globals;
import chatapp.shared.interfaces.GroupListener;
import chatapp.shared.interfaces.UserListener;
import chatapp.shared.models.Group;
import chatapp.shared.models.Message;
import chatapp.shared.models.User;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SelectPanel implements UserListener, GroupListener, UsersListener {

    private final MainFrame mainFrame;
    private ChatPanel chatPanel;
    private final ClientGlobals globals;

    private JPanel selectPanel;
    private JButton addUserButton;
    private JScrollPane usersScrollPane;
    private JList userList;
    private final DefaultListModel<User> userListModel = new DefaultListModel<>();
    private JButton addGroupButton;
    private JScrollPane groupsScrollPane;
    private JList groupList;
    private final DefaultListModel<Group> groupListModel = new DefaultListModel<>();

    public SelectPanel(MainFrame mainFrame, ClientGlobals globals) {
        this.mainFrame = mainFrame;
        this.globals = globals;

        globals.clientListeners.users.add(this);
        globals.listeners.user.add(this);
        globals.listeners.group.add(this);

        selectPanel.setBorder(new MatteBorder(0, 0, 0, 1, SwingBuilder.foregroundColor));

        userList.setModel(userListModel);
        groupList.setModel(groupListModel);

        createEventHandlers();
    }

    private void createUIComponents() {
        selectPanel = new JPanel();
        selectPanel.setBorder(new MatteBorder(0, 0, 0, 1, SwingBuilder.foregroundColor));
        addUserButton = SwingBuilder.getBaseButton();
        usersScrollPane = SwingBuilder.getBaseScrollPane();
        userList = SwingBuilder.getBaseList();
        addGroupButton = SwingBuilder.getBaseButton();
        groupsScrollPane = SwingBuilder.getBaseScrollPane();
        groupList = SwingBuilder.getBaseList();
    }

    private void createEventHandlers() {

        addUserButton.addActionListener(e ->
                new AddUserDialog(globals));

        addGroupButton.addActionListener(e ->
                new AddGroupDialog(globals));

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
    }


    public void setChatPanel(ChatPanel chatPanel) {
        this.chatPanel = chatPanel;
    }

    public JPanel getSelectPanel() {
        return selectPanel;
    }

    public JList getUserList() {
        return userList;
    }

    public JList getGroupList() {
        return groupList;
    }


    public void changeDM(ListSelectionEvent e) {
        User user = (User) userList.getSelectedValue();
        if (user == null)
            return;

        DefaultListModel<Message> messageListModel = chatPanel.getMessageListModel();
        messageListModel.clear();
        messageListModel.addAll(user.getPrivateMessages());
        mainFrame.setMessageListOrigin(MessageListOrigin.User);

        groupList.clearSelection();
        chatPanel.getChatNameLabel().setText(user.toString());
        chatPanel.getMessageUploadButton().setVisible(true);
        chatPanel.getChatLeaveButton().setVisible(false);
    }

    public void changeGroup(ListSelectionEvent e) {
        Group group = (Group) groupList.getSelectedValue();
        if (group == null)
            return;

        DefaultListModel<Message> messageListModel = chatPanel.getMessageListModel();
        messageListModel.clear();
        messageListModel.addAll(group.getMessages());
        mainFrame.setMessageListOrigin(MessageListOrigin.Group);

        userList.clearSelection();
        chatPanel.getChatNameLabel().setText(group.toString());
        chatPanel.getMessageUploadButton().setVisible(false);
        JButton chatLeaveButton = chatPanel.getChatLeaveButton();
        chatLeaveButton.setVisible(!group.getName().equals(Globals.PUBLIC_GROUP_NAME));
    }

    @Override
    public void privateMessageAdded(User user, Message message) {
    }

    @Override
    public void chatAddedSet(User user, boolean chatAdded) {
        if (chatAdded && !user.equals(globals.currentUser)) {
            System.out.println("C: MainFrame chatAddedSet " + user + " " + chatAdded);
            userListModel.addElement(user);
            userList.setSelectedValue(user, true);
        }
    }

    @Override
    public void messageAdded(Group group, Message message) {
    }

    @Override
    public void joinedSet(Group group, boolean joined) {
        System.out.println("C: MainFrame joinedSet " + group + " " + joined);
        if (joined) {
            groupListModel.addElement(group);
            groupList.setSelectedValue(group, true);
        } else {
            if (groupList.getSelectedValue().equals(group)) {
                groupList.setSelectedIndex(0);
            }

            groupListModel.removeElement(group);
        }
    }

    @Override
    public void userRemoved(User user) {
        System.out.println("C: MainFrame userRemoved " + user);

        if (userList.getSelectedValue() != null &&
                userList.getSelectedValue().equals(user)) {
            groupList.setSelectedIndex(0);
        }

        userListModel.removeElement(user);
    }

    @Override
    public void userAdded(User user) {
    }

}
