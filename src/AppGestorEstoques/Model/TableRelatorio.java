package AppGestorEstoques.Model;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

public class TableRelatorio extends AbstractTableModel {
    private final List<RelatorioSemanalDTO> dados;
    private final String[] colunas = {"Semana", "Período", "Total Produtos", "Vendidos", "Faturamento", "Exportar"};
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final WeekFields WEEK_FIELDS = WeekFields.of(Locale.getDefault());

    public TableRelatorio(List<RelatorioSemanalDTO> dados) {
        this.dados = dados;
    }

    @Override
    public int getRowCount() { return dados.size(); }
    @Override
    public int getColumnCount() { return colunas.length; }
    @Override
    public String getColumnName(int column) { return colunas[column]; }

    @Override
    public Object getValueAt(int row, int col) {
        RelatorioSemanalDTO item = dados.get(row);
        return switch (col) {
            case 0 -> String.format("Semana %02d", item.periodoInicio().get(WEEK_FIELDS.weekOfWeekBasedYear()));
            case 1 -> item.periodoInicio().format(DATE_FORMAT) + " - " + item.periodoFim().format(DATE_FORMAT);
            case 2 -> item.totalProdutos();
            case 3 -> item.produtosVendidos();
            case 4 -> item.faturamento();
            case 5 -> "EXPORTAR";
            default -> null;
        };
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return column == 5 ? JButton.class : super.getColumnClass(column);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // Only the action column should be clickable/editable (button editor).
        return columnIndex == 5;
    }

    public RelatorioSemanalDTO getRow(int row) {
        return dados.get(row);
    }

    public void addRow(RelatorioSemanalDTO relatorio) {
        int rowIndex = dados.size();
        dados.add(relatorio);
        fireTableRowsInserted(rowIndex, rowIndex);
    }
}
