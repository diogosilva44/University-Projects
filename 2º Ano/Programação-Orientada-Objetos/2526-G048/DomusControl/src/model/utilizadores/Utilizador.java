package model.utilizadores;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public abstract class Utilizador implements Serializable {
    private static int contadorId = 1;

    private String id;
    private String nome;
    private String email;
    private String password;
    private Set<String> casas;
    
    public Utilizador() {
        this.id = "U" + contadorId++;
        this.nome = "Sem Nome";
        this.email = "Sem Email";
        this.password = "Sem password";
        this.casas = new HashSet<>();
    }

    public Utilizador(String nome, String email, String password) {
        this.id = "U" + contadorId++;
        this.nome = nome;
        this.email = email;
        this.password = password;
        this.casas = new HashSet<>();
    }

    public Utilizador(Utilizador user) {
        this.id = user.getId();
        this.nome = user.getNome();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.casas = new HashSet<>(user.getCasas());
    }

    //getters e setters
    public static int getContadorId() {
        return contadorId;
    }
    public static void setContadorId(int valor) {
        contadorId = valor;
    }
    
    public String getId() {
        return this.id;
    }
    
    public String getNome() {
        return this.nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return this.email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }


    
    public Set<String> getCasas() {
        return new HashSet<>(this.casas);
    }

    public void addCasa(String idCasa) {
        this.casas.add(idCasa);
    }

    public void removeCasa(String idCasa) {
        this.casas.remove(idCasa);
    }

    public boolean temAcessoCasa(String idCasa) {
        return this.casas.contains(idCasa);
    }

    public int numCasas() {
        return this.casas.size();
    }

    public abstract boolean isAdministrador();

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || o.getClass() != this.getClass()) return false;
        Utilizador u = (Utilizador) o;
        return this.id.equals(u.getId());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID        : ").append(this.id).append("\n");
        sb.append("Nome      : ").append(this.nome).append("\n");
        sb.append("Email     : ").append(this.email).append("\n");
        sb.append("Tipo      : ").append(isAdministrador() ? "Administrador" : "Utilizador comum").append("\n");
        sb.append("Casas     : ").append(this.casas.size());
        return sb.toString();
    }

    @Override
    public abstract Utilizador clone();
}
