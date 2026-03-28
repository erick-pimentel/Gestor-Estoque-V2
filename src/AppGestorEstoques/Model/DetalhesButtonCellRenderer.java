package AppGestorEstoques.Model;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class DetalhesButtonCellRenderer extends JButton implements TableCellRenderer {

    public DetalhesButtonCellRenderer() {
        setOpaque(true);
        setBackground(new Color(0, 123, 255));
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setText("DETALHES");
        setFont(getFont().deriveFont(10f));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return this;
    }

}
