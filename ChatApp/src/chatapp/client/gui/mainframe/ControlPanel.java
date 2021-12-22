package chatapp.client.gui.mainframe;

import chatapp.client.ClientGlobals;
import chatapp.client.enums.LogLevel;
import chatapp.client.gui.SwingBuilder;
import chatapp.client.interfaces.ServerConnectionListener;
import chatapp.client.models.Log;
import chatapp.shared.models.chatpackages.ChatPackage;
import chatapp.shared.models.chatpackages.ErPackage;
import chatapp.shared.models.chatpackages.InfoPackage;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class ControlPanel implements ServerConnectionListener {

    private final MainFrame mainFrame;
    private final ClientGlobals globals;

    private JPanel controlPanel;
    private JScrollPane logScrollPane;
    private JList logList;
    private final DefaultListModel<Log> logListModel = new DefaultListModel<>();
    private JButton logOutButton;

    public ControlPanel(MainFrame mainFrame, ClientGlobals globals) {
        this.mainFrame = mainFrame;
        this.globals = globals;

        globals.clientListeners.serverConnection.add(this);

        controlPanel.setBorder(new MatteBorder(0, 1, 0, 0, SwingBuilder.foregroundColor));

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

        logList.setModel(logListModel);

        createEventHandlers();
    }

    private void createUIComponents() {
        controlPanel = new JPanel();
        controlPanel.setBorder(new MatteBorder(0, 1, 0, 0, SwingBuilder.foregroundColor));
        logScrollPane = SwingBuilder.getBaseScrollPane();
        logList = SwingBuilder.getBaseList();
        logOutButton = SwingBuilder.getBaseButton();
    }

    private void createEventHandlers() {
        logOutButton.addActionListener(e -> globals.systemHelper.restart());
    }


    public JPanel getControlPanel() {
        return controlPanel;
    }


    @Override
    public void chatPackageReceived(ChatPackage chatPackage) {
        switch (chatPackage.getType()) {
            case INFO:
                InfoPackage infoPackage = (InfoPackage) chatPackage;
                System.out.println("C: MainFrame chatPackageReceived " + infoPackage);
                logListModel.addElement(new Log(
                        infoPackage.getMessage(),
                        "Server",
                        LogLevel.Info));
                break;
            case ER:
                ErPackage erPackage = (ErPackage) chatPackage;
                System.out.println("C: MainFrame chatPackageReceived " + erPackage);
                logListModel.addElement(new Log(
                        erPackage.getCode() + " " + erPackage.getMessage(),
                        "Server",
                        LogLevel.Error));
                break;
        }
    }

}
