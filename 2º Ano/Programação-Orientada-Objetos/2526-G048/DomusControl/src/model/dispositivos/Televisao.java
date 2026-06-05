package model.dispositivos;

public class Televisao extends Dispositivo {
    private int canal;
    private int volume;

    public Televisao() {
        super();
        this.canal = 1;
        this.volume = 20;
    }
    public Televisao(String marca, String modelo, double consumoWh, int canal, int volume) {
        super(marca,modelo,consumoWh);
        this.canal = canal;
        this.volume = volume;
    }

    public Televisao(Televisao tv) {
        super(tv);
        this.canal = tv.getCanal();
        this.volume = tv.getVolume();
    }

    //getters e setters
    public int getCanal() {
        return this.canal;
    }
    public int getVolume() {
        return this.volume;
    }

    public void setCanal(int canal) {
        this.canal = canal;
    }
    public void setVolume(int volume) {
        this.volume = volume;
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
        StringBuilder sb = new StringBuilder();
        sb.append(" | Canal : ").append(this.canal);
        sb.append(" | Volume : ").append(this.volume).append("%\n");
        return sb.toString();
    }

    @Override
    public Televisao clone() {
        return new Televisao(this);
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
