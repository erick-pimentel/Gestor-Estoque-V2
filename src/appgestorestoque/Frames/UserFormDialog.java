package appgestorestoque.Frames;

import AppGestorEstoques.Model.UserProfile;

import javax.swing.*;
import java.awt.*;

public class UserFormDialog extends JDialog {
    private static final String[] PERFIS = {"Administrador", "Gestor", "Operador de Estoque"};

    private final JTextField txtNome = new JTextField();
    private final JTextField txtEmail = new JTextField();
    private final JPasswordField txtSenha = new JPasswordField();
    private final JComboBox<String> comboPerfil = new JComboBox<>(PERFIS);
    private final JCheckBox checkAtivo = new JCheckBox("Usuario ativo", true);
    private final JLabel lblSenhaHint = new JLabel();
    private boolean confirmado = false;

    public UserFormDialog(Frame owner, String titulo, UserProfile existente) {
        super(owner, titulo, true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(525, 320);
        setResizable(false);
        setLocationRelativeTo(owner);

        JPanel form = new JPanel(new GridLayout(6, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        form.add(new JLabel("Nome:"));
        form.add(txtNome);

        form.add(new JLabel("Email:"));
        form.add(txtEmail);

        form.add(new JLabel("Senha:"));
        form.add(txtSenha);

        form.add(new JLabel("Perfil:"));
        form.add(comboPerfil);

        form.add(new JLabel("Status:"));
        form.add(checkAtivo);

        form.add(new JLabel(""));
        form.add(lblSenhaHint);

        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.add(btnCancelar);
        footer.add(btnSalvar);

        btnCancelar.addActionListener(e -> dispose());
        btnSalvar.addActionListener(e -> {
            if (txtNome.getText() == null || txtNome.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome de usuario obrigatorio.");
                return;
            }
            if (txtEmail.getText() == null || txtEmail.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Email de usuario obrigatorio.");
                return;
            }
            confirmado = true;
            dispose();
        });

        if (existente != null) {
            txtNome.setText(existente.nome());
            txtEmail.setText(existente.email());
            comboPerfil.setSelectedItem(existente.perfil());
            checkAtivo.setSelected(existente.ativo());
            lblSenhaHint.setText("Deixe senha em branco para manter igual.");
        } else {
            lblSenhaHint.setText("Senha obrigatoria para novo usuario.");
        }

        JPanel root = new JPanel(new BorderLayout());
        root.add(form, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);
        setContentPane(root);
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public String getNome() {
        return txtNome.getText();
    }

    public String getEmail() {
        return txtEmail.getText();
    }


    public String getSenha() {
        return new String(txtSenha.getPassword());
    }

    public String getPerfil() {
        return (String) comboPerfil.getSelectedItem();
    }

    public boolean isAtivo() {
        return checkAtivo.isSelected();
    }
}

