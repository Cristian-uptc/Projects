package co.edu.uptc.client.view;

import java.awt.GridLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public abstract class BaseFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final int MARGIN = 16;
    private static final int GAP = 8;

    protected BaseFrame(String title) {
        super(title);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Información",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public boolean confirm(String message) {
        return JOptionPane.showConfirmDialog(this, message, "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    protected static JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, GAP, GAP));
        panel.setBorder(new EmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN));
        return panel;
    }

    protected static void addRow(JPanel panel, String label, JComponent field) {
        panel.add(new JLabel(label));
        panel.add(field);
    }
}
