package Client;

import Client.Client;
import Message.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ClientGUI extends JFrame{
    public static final int chatPanelWidth = 500;
    public static final int sendPanelHeight = 200;
    public static final int listWidth = 200;
    public final Client loginUser;
    private JFrame mainFrame;
    private JPanel chatPanel;
    private JPanel listPanel;
    private JPanel sendPanel;
    private JTextArea textArea;

    public ClientGUI(Client user) {
        loginUser = user;
        mainFrame = this;
        createChatPanel();
        createSendPanel();
        createList();
        startGUI();
    }

    private void startGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        setTitle("ChatRoom -- "+loginUser.nickName);
        setSize(800, 600);
        setLocation(250, 0);
        setLayout(new BorderLayout());
        JScrollPane chat = new JScrollPane(chatPanel);
        JScrollPane list = new JScrollPane(listPanel);
        list.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(chat, BorderLayout.CENTER);
        add(list, BorderLayout.EAST);
        add(sendPanel, BorderLayout.SOUTH);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Message exitSignal = new Message();
                exitSignal.setType(Message.TYPE.EXIT);
                exitSignal.setFrom(loginUser.nickName);
                loginUser.sendMsg(exitSignal);
                System.exit(0);
            }
        });
        setResizable(false);
        setVisible(true);
    }

    private JPanel createPanel(int width, int height) {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        jPanel.setPreferredSize(new Dimension(width, height));
        return jPanel;
    }

    private void createChatPanel() {
        chatPanel = createPanel(chatPanelWidth, 0);
        chatPanel.setBackground(Color.white);
        chatPanel.setLayout(new BoxLayout(chatPanel,BoxLayout.Y_AXIS));
    }

    private void createList() {
        listPanel = createPanel(listWidth, 0);
        listPanel.setLayout(new BoxLayout(listPanel,BoxLayout.Y_AXIS));
    }

    private void createSendPanel() {
        sendPanel = createPanel(0, sendPanelHeight);
        JPanel container = new JPanel();
        textArea = new JTextArea(8, 80);
        Button btn = new Button("send");
        container.add(textArea);
        container.add(btn);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        sendPanel.add(new JLabel("message:"));
        sendPanel.add(container);
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String text = textArea.getText();
                if (text.length() == 0) {
                    new dialog();
                } else {
                    textArea.setText("");
                    sendMessage(text);
                }
            }
        });

    }

    private static int addJLabel(JPanel jPanel, String str, int width) {
        JLabel label = new JLabel();
        label.setSize(width, 0);
        JLabelSetText(label, str);
        if(str.contains("@")){
            label.setForeground(Color.magenta);
        }
        if(str.contains("Notify")){
            label.setForeground(Color.RED);
            label.setFont(new Font(Font.DIALOG, Font.BOLD, 18));
        }
        jPanel.add(label);
        return label.getPreferredSize().height;
    }

    public void addList(String str) {
        int h = addJLabel(listPanel, str, listWidth -40);
        int oldH = listPanel.getPreferredSize().height;
        listPanel.setPreferredSize(new Dimension(listWidth ,oldH+h));
    }

    public void addChat(String str) {
        int h = addJLabel(chatPanel, str, chatPanelWidth - 20);
        int old = chatPanel.getPreferredSize().height;
        chatPanel.setPreferredSize(new Dimension(chatPanelWidth,old+h));
    }

    private static void JLabelSetText(JLabel label, String lStr) {
        StringBuilder builder = new StringBuilder("<html><body>");
        FontMetrics fontMetrics = label.getFontMetrics(label.getFont());
        char[] array = lStr.toCharArray();
        int start = 0, len = 0;
        while (start + len < lStr.length()) {
            while (true) {
                len++;
                if (start + len > lStr.length()) break;
                if (fontMetrics.charsWidth(array, start, len) > label.getWidth()) break;
            }
            builder.append(array, start, len - 1).append("<br/>");
            start = start + len - 1;
            len = 0;
        }
        builder.append(array, start, lStr.length() - start).append("<br/></body></html>");
        label.setText(builder.toString());
    }

    class dialog {
        dialog() {
            JButton btn = new JButton("confirm");
            JDialog jDialog = new JDialog(mainFrame, "Dialog");
            JLabel label = new JLabel("message can't be null");
            jDialog.setModal(true);
            jDialog.setSize(300, 300);
            jDialog.setLocation(500, 100);
            Container container = new Container();
            container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
            container.add(Box.createVerticalGlue());
            container.add(label);
            container.add(Box.createVerticalStrut(20));
            container.add(btn);
            container.add(Box.createVerticalGlue());
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            jDialog.add(container);
            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    jDialog.dispose();
                }
            });
            jDialog.setVisible(true);
        }
    }


    public void sendMessage(String msg) {
        String to = null;
        msg = msg.replaceAll("\\s*","");
        if(msg.contains("@")){
            String[] split = msg.split("@");
            to = split[split.length-1];
            msg = split[0];
        }
        loginUser.sendMsg(to,msg);
    }

    public void reNewList(String str){
        listPanel.removeAll();
        String[] split = str.split(";");
        for (String s : split) {
            addList(s);
        }
    }
}
