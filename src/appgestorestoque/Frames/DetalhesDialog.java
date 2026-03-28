package appgestorestoque.Frames;

import AppGestorEstoques.Model.MovimentacaoDetalhesDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DetalhesDialog extends JDialog {

    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public DetalhesDialog(List<MovimentacaoDetalhesDTO> detalhes) {
        setTitle("Detalhes da Semana");
        setModal(true);
        setSize(760, 380);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        String[] colunas = {"ID", "Produto", "Tipo", "Quantidade", "Data"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (MovimentacaoDetalhesDTO detalhe : detalhes) {
            String data = detalhe.data() == null ? "" : detalhe.data().format(DATE_TIME_FMT);
            model.addRow(new Object[]{
                    detalhe.idMovimentacao(),
                    detalhe.produtoNome(),
                    detalhe.tipo(),
                    detalhe.qtd(),
                    data
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);

        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dispose());

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.add(btnFechar);

        setLayout(new BorderLayout(8, 8));
        add(scrollPane, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }
}

