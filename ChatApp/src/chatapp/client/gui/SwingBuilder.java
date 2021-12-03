package chatapp.client.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class SwingBuilder {

    public static final Dimension elementHeight = new Dimension(-1, 28);

    public static final Color textColor = new Color(187, 187, 187);
    public static final Color textSelectColor = new Color(60, 60, 60);
    public static final Color foregroundColor = new Color(130, 130, 130);
    public static final Color backgroundColor1 = new Color(80, 60, 80);
    public static final Color backgroundColor2 = new Color(70, 50, 70);
    public static final Color backgroundColor3 = new Color(60, 40, 60);
    public static final Color transparentColor = new Color(0, 0, 0, 0);

    public static final Font baseFont = new Font("baseFont", Font.PLAIN, 14);

    public static final LineBorder baseBorder = new LineBorder(foregroundColor, 1);
    public static final EmptyBorder emptyBorder = new EmptyBorder(0, 0, 0, 0);


    public static JList getBaseList() {
        JList list = new JList();
        list.setBackground(backgroundColor1);
        list.setFocusable(false);
        list.setFont(baseFont);
        list.setForeground(textColor);
        list.setSelectionBackground(backgroundColor3);
        list.setSelectionForeground(textColor);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        return list;
    }

    public static JScrollPane getBaseScrollPane() {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(emptyBorder);

        scrollPane.setOpaque(false);
        scrollPane.getVerticalScrollBar().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            protected void configureScrollBarColors() {
                super.configureScrollBarColors();
                this.trackColor = transparentColor;
            }
        });
        scrollPane.getHorizontalScrollBar().setOpaque(false);
        scrollPane.getHorizontalScrollBar().setUI(new BasicScrollBarUI() {
            protected void configureScrollBarColors() {
                super.configureScrollBarColors();
                this.trackColor = transparentColor;
            }
        });

        return scrollPane;
    }

    public static JButton getBaseButton() {
        return getBaseButton(true);
    }
    public static JButton getBaseButton(boolean isColored) {
        JButton button = new BaseJButton(isColored);
        button.setMinimumSize(elementHeight);
        button.setForeground(textColor);
        button.setFont(baseFont);
        button.setFocusable(false);
        button.setOpaque(false);

        if (isColored) {
            button.setBackground(backgroundColor3);
            button.setBorder(baseBorder);
        } else {
            button.setBackground(transparentColor);
            button.setBorder(emptyBorder);
        }

        return button;
    }

    public static JTextField getBaseTextField() {
        JTextField textField = new JTextField();
        textField.setPreferredSize(elementHeight);
        textField.setBackground(backgroundColor1);
        textField.setForeground(textColor);
        textField.setSelectedTextColor(textSelectColor);
        textField.setFont(baseFont);
        textField.setBorder(BorderFactory.createCompoundBorder(
                baseBorder,
                BorderFactory.createEmptyBorder(0, 5, 0, 5)));
        textField.setCaretColor(foregroundColor);

        return textField;
    }

    public static JPasswordField getBasePasswordField() {
        JPasswordField passwordField = new JPasswordField();
        JTextField textField = getBaseTextField();
        passwordField.setPreferredSize(textField.getPreferredSize());
        passwordField.setBackground(textField.getBackground());
        passwordField.setForeground(textField.getForeground());
        passwordField.setSelectedTextColor(textField.getSelectedTextColor());
        passwordField.setFont(textField.getFont());
        passwordField.setBorder(textField.getBorder());
        passwordField.setCaretColor(textField.getCaretColor());

        return passwordField;
    }

    public static JLabel getBaseLabel() {
        JLabel label = new JLabel();
        label.setFont(baseFont);
        label.setForeground(textColor);

        return label;
    }


    private static class BaseJButton extends JButton {

        public BaseJButton(boolean isColored) {
            super("");
            super.setContentAreaFilled(false);

            if (isColored) {
                getModel().addChangeListener(e -> {
                    ButtonModel model = (ButtonModel) e.getSource();
                    if (model.isRollover()) {
                        setBackground(backgroundColor2);
                    } else {
                        setBackground(backgroundColor3);
                    }
                    if (model.isPressed()) {
                        setBackground(backgroundColor3);
                    }
                });
            } else {
                getModel().addChangeListener(e -> {
                    ButtonModel model = (ButtonModel) e.getSource();
                    if (model.isRollover()) {
                        setBackground(backgroundColor3);
                    } else {
                        setBackground(transparentColor);
                    }
                    if (model.isPressed()) {
                        setBackground(backgroundColor2);
                    }
                });
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
            super.paintComponent(g);
        }

    }

}
