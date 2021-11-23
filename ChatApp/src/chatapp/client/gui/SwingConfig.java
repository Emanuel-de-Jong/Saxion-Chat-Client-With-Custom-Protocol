package chatapp.client.gui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class SwingConfig {

    public static final Dimension elementHeight = new Dimension(-1, 28);

    public static final Color textColor = new Color(187, 187, 187);
    public static final Color textSelectColor = new Color(60, 60, 60);

    public static final Color foregroundColor = new Color(130, 130, 130);
    public static final Color backgroundColor1 = new Color(80, 60, 80);
    public static final Color backgroundColor2 = new Color(70, 50, 70);
    public static final Color backgroundColor3 = new Color(60, 40, 60);

    public static final Font baseFont = new Font("baseFont", Font.PLAIN, 14);

    public static final LineBorder baseBorder = new LineBorder(foregroundColor, 1);

    public static JButton getBaseButton() {
        JButton button = new BaseJButton();
        button.setMinimumSize(elementHeight);
        button.setBorder(baseBorder);
        button.setBackground(backgroundColor3);
        button.setForeground(textColor);
        button.setFont(baseFont);
        button.setFocusable(false);

        button.getModel().addChangeListener(e -> {
            ButtonModel model = (ButtonModel)e.getSource();
            if (model.isRollover()) {
                button.setBackground(backgroundColor2);
            } else {
                button.setBackground(backgroundColor3);
            }
            if (model.isPressed()) {
                button.setBackground(backgroundColor3);
            }
        });

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


    private static class BaseJButton extends JButton {

        public BaseJButton() {
            this(null);
        }

        public BaseJButton(String text) {
            super(text);
            super.setContentAreaFilled(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
            super.paintComponent(g);
        }

    }

}
