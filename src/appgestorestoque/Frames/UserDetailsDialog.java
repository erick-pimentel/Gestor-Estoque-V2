package appgestorestoque.Frames;

import AppGestorEstoques.Model.UserProfile;

import javax.swing.*;
import java.awt.*;

public class UserDetailsDialog extends JDialog {
    public UserDetailsDialog(Frame owner, UserProfile userProfile) {
        super(owner, "Detalhes do Usuario", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(420, 250);
        setResizable(false);
        setLocationRelativeTo(owner);

        JPanel panel = new JPanel(new GridLayout(6, 1, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        panel.add(new JLabel("ID: " + userProfile.id()));
        panel.add(new JLabel("Nome: " + userProfile.nome()));
        panel.add(new JLabel("Email: " + userProfile.email()));
        panel.add(new JLabel("Perfil: " + userProfile.perfil()));
        panel.add(new JLabel("Status: " + (userProfile.ativo() ? "Ativo" : "Inativo")));

        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dispose());
        panel.add(btnFechar);

        setContentPane(panel);
    }
}

