package model.utilizadores;

public class UtilizadorComum extends Utilizador {

    public UtilizadorComum() {
        super();
    }

    public UtilizadorComum(String nome, String email, String password) {
        super(nome, email, password);
    }

    public UtilizadorComum(UtilizadorComum userC) {
        super(userC);
    }

    @Override
    public boolean isAdministrador() {
        return false;
    }

    @Override
    public UtilizadorComum clone() {
        return new UtilizadorComum(this);
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
