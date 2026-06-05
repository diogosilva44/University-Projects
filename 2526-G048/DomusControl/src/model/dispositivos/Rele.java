package model.dispositivos;

public class Rele extends Dispositivo{
    public Rele() {
        super();
    }

    public Rele(String marca, String modelo, double consumoWh) {
        super(marca,modelo,consumoWh);
    }

    public Rele(Rele r) {
        super(r);
    }

    @Override
    public void ligar() {
        incrementarAtivacoes();
        setLigado(true);
    }

    @Override
    public void desligar() {
        setLigado(false);
    }

    @Override
    public String getEstadoDetalhado() {
        if(isLigado()) {
            return "Ligado";
        } else {
            return "Desligado";
        }
    }

    @Override
    public Rele clone() {
        return new Rele(this);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public String toString() {
        return super.toString() + "\n" + getEstadoDetalhado();
    }
}
