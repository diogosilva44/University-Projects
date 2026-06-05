package model.utilizadores;

public class Administrador extends Utilizador {

    public Administrador() {
        super();
    }

    public Administrador(String nome, String email, String password) {
        super(nome, email, password);
    }

    public Administrador(Administrador admin) {
        super(admin);
    }

    @Override
    public boolean isAdministrador() {
        return true;
    }

    @Override
    public Administrador clone() {
        return new Administrador(this);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
