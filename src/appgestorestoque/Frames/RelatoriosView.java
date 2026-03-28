/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package appgestorestoque.Frames;

import AppGestorEstoques.DAO.IRelatorioDAO;
import AppGestorEstoques.DAO.RelatorioDAO;
import AppGestorEstoques.Model.*;
import AppGestorEstoques.Service.RelatorioExporter;
import AppGestorEstoques.Service.RelatorioPdfExporter;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Ericz
 */
public class RelatoriosView extends javax.swing.JFrame {

    private User user;
    private JTable tabela;
    private TableRelatorio model;
    private TableRowSorter<TableRelatorio> sorter;
    private final IRelatorioDAO relatorioDAO;
    private final RelatorioExporter relatorioExporter;
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Creates new form RelatoriosView
     */
    public RelatoriosView() {
        this(new RelatorioDAO(), new RelatorioPdfExporter());
    }

    public RelatoriosView(IRelatorioDAO relatorioDAO, RelatorioExporter relatorioExporter) {
        this.relatorioDAO = relatorioDAO;
        this.relatorioExporter = relatorioExporter;
        initComponents();
        this.setTitle("Relatorios - Gestor Estoque");
        this.setSize(800, 360);
        this.setResizable(false);

        tabela = table_relatorios;
        carregarDados();
        initTable();
        initFiltros();
    }

    public void setUser(User user) {
        this.user = user;
    }

    private void initTable() {
        tabela.setRowHeight(35);
        sorter = new TableRowSorter<>(model);
        tabela.setRowSorter(sorter);
        tabela.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        tabela.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));
    }

    private void initFiltros() {
        combo_relatoriosFiltro.setModel(new DefaultComboBoxModel<>(new String[]{
                "Todos",
                "Semana",
                "Período",
                "Total Produtos",
                "Vendidos",
                "Faturamento"
        }));

        combo_relatoriosFiltro.addActionListener(e -> aplicarFiltroDoCombo());
    }

    private void aplicarFiltroDoCombo() {
        if (sorter == null) {
            return;
        }

        String filtro = (String) combo_relatoriosFiltro.getSelectedItem();
        if (filtro == null || "Todos".equals(filtro)) {
            sorter.setRowFilter(null);
            return;
        }

        String termo = JOptionPane.showInputDialog(this, "Digite o valor para filtrar por " + filtro + ":");
        if (termo == null) {
            return;
        }

        termo = termo.trim();
        if (termo.isEmpty()) {
            sorter.setRowFilter(null);
            return;
        }

        int coluna = switch (filtro) {
            case "Semana" -> 0;
            case "Período" -> 1;
            case "Total Produtos" -> 2;
            case "Vendidos" -> 3;
            case "Faturamento" -> 4;
            default -> -1;
        };

        if (coluna >= 0) {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(termo), coluna));
        }
    }

    private void exportarRelatorio(int row) {
        if (model == null || row < 0) {
            return;
        }

        int modelRow = tabela.convertRowIndexToModel(row);
        if (modelRow < 0 || modelRow >= model.getRowCount()) {
            return;
        }

        RelatorioSemanalDTO relatorio = model.getRow(modelRow);
        List<MovimentacaoDetalhesDTO> detalhes = relatorioDAO.listarDetalhesPorSemana(
                relatorio.periodoInicio(),
                relatorio.periodoFim()
        );

        String nomePadrao = "relatorio_" + relatorio.periodoInicio() + "_" + relatorio.periodoFim() + ".pdf";
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Salvar relatorio em PDF");
        chooser.setSelectedFile(new File(nomePadrao));

        int escolha = chooser.showSaveDialog(this);
        if (escolha != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File arquivo = chooser.getSelectedFile();
        if (!arquivo.getName().toLowerCase().endsWith(".pdf")) {
            arquivo = new File(arquivo.getAbsolutePath() + ".pdf");
        }

        try {
            relatorioExporter.exportar(relatorio, detalhes, arquivo);
            JOptionPane.showMessageDialog(this, "PDF exportado com sucesso em:\n" + arquivo.getAbsolutePath());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao exportar PDF: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarDados() {
        List<RelatorioSemanalDTO> dados = relatorioDAO.listarRelatorioSemanal();
        model = new TableRelatorio(dados);
        tabela.setModel(model);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        combo_relatoriosFiltro = new javax.swing.JComboBox<>();
        btn_relatoriosVoltar = new javax.swing.JButton();
        btn_relatorioGerar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_relatorios = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        combo_relatoriosFiltro.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btn_relatoriosVoltar.setBackground(new java.awt.Color(0, 255, 0));
        btn_relatoriosVoltar.setText("Voltar");
        btn_relatoriosVoltar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_relatoriosVoltarActionPerformed(evt);
            }
        });

        btn_relatorioGerar.setBackground(new java.awt.Color(0, 255, 255));
        btn_relatorioGerar.setText("GERAR RELATÓRIO");
        btn_relatorioGerar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_relatorioGerarActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("RELATÓRIOS");

        table_relatorios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {"Semana", "Período", "Total Produtos", "Produtos Vendidos", "Faturamento", "Exportar"}
        ));
        jScrollPane1.setViewportView(table_relatorios);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(combo_relatoriosFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_relatorioGerar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_relatoriosVoltar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(combo_relatoriosFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_relatorioGerar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_relatoriosVoltar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_relatoriosVoltarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_relatoriosVoltarActionPerformed
        MenuView menu = new MenuView();
        if (user != null) {
            menu.setUser(user);
        }
        menu.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btn_relatoriosVoltarActionPerformed

    private void btn_relatorioGerarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_relatorioGerarActionPerformed
        cadastrarRelatorioManual();
    }//GEN-LAST:event_btn_relatorioGerarActionPerformed

    private void cadastrarRelatorioManual() {
        LocalDate periodoInicio = solicitarData("Informe o início do período (dd/MM/yyyy):");
        if (periodoInicio == null) {
            return;
        }

        LocalDate periodoFim = solicitarData("Informe o fim do período (dd/MM/yyyy):");
        if (periodoFim == null) {
            return;
        }

        if (periodoFim.isBefore(periodoInicio)) {
            JOptionPane.showMessageDialog(this, "O fim do período não pode ser menor que o início.");
            return;
        }

        Integer totalProdutos = solicitarInteiro("Informe o total de produtos:");
        if (totalProdutos == null) {
            return;
        }

        Integer produtosVendidos = solicitarInteiro("Informe a quantidade vendida:");
        if (produtosVendidos == null) {
            return;
        }

        BigDecimal faturamento = solicitarDecimal("Informe o faturamento:");
        if (faturamento == null) {
            return;
        }

        RelatorioSemanalDTO novoRelatorio = new RelatorioSemanalDTO(
                periodoInicio,
                periodoFim,
                totalProdutos,
                produtosVendidos,
                faturamento,
                new ArrayList<>()
        );

        model.addRow(novoRelatorio);
        JOptionPane.showMessageDialog(this, "Relatório manual adicionado com sucesso.");
    }

    private LocalDate solicitarData(String mensagem) {
        while (true) {
            String valor = JOptionPane.showInputDialog(this, mensagem);
            if (valor == null) {
                return null;
            }

            try {
                return LocalDate.parse(valor.trim(), INPUT_DATE_FORMAT);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Data inválida. Use o formato dd/MM/yyyy.");
            }
        }
    }

    private Integer solicitarInteiro(String mensagem) {
        while (true) {
            String valor = JOptionPane.showInputDialog(this, mensagem);
            if (valor == null) {
                return null;
            }

            try {
                int numero = Integer.parseInt(valor.trim());
                if (numero < 0) {
                    JOptionPane.showMessageDialog(this, "O valor não pode ser negativo.");
                    continue;
                }
                return numero;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Valor inválido. Digite um número inteiro.");
            }
        }
    }

    private BigDecimal solicitarDecimal(String mensagem) {
        while (true) {
            String valor = JOptionPane.showInputDialog(this, mensagem);
            if (valor == null) {
                return null;
            }

            try {
                String normalizado = valor.trim().replace(",", ".");
                BigDecimal numero = new BigDecimal(normalizado);
                if (numero.compareTo(BigDecimal.ZERO) < 0) {
                    JOptionPane.showMessageDialog(this, "O valor não pode ser negativo.");
                    continue;
                }
                return numero;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Valor inválido. Digite um número decimal.");
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(RelatoriosView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RelatoriosView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RelatoriosView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RelatoriosView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_relatorioGerar;
    private javax.swing.JButton btn_relatoriosVoltar;
    private javax.swing.JComboBox<String> combo_relatoriosFiltro;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable table_relatorios;
    // End of variables declaration//GEN-END:variables
    
    // Custom Button Renderer for table cells
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setHorizontalAlignment(JButton.CENTER);
            setBackground(new java.awt.Color(0, 123, 255));
            setForeground(java.awt.Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "EXPORTAR" : value.toString());
            return this;
        }
    }
    
    // Custom Button Editor for table cells
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private int selectedRow = -1;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "EXPORTAR" : value.toString();
            button.setText(label);
            selectedRow = row;
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                exportarRelatorio(selectedRow);
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
}
