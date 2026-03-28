/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AppGestorEstoques.Model;

/**
 *
 * @author Ericz
 */
public class User {
    private final int id;
    private final String nome;
    private final String perfil;
    private final boolean ativo;

    public User(int id, String nome, String perfil, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.perfil = perfil;
        this.ativo = ativo;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getPerfil() {
        return perfil;
    }

    public boolean isAtivo() {
        return ativo;
    }

}
