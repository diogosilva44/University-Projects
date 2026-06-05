package model.dispositivos;

public class Sensor extends Dispositivo {
    private double valorAtual;
    private String unidade;
    private String tipo;

    public Sensor() {
        super();
        this.valorAtual = 0.0;
        this.unidade = "Sem unidade";
        this.tipo = "Sem tipo";
    }

    public Sensor(String marca, String modelo, double consumoWh, String tipo, String unidade) {
        super(marca, modelo, consumoWh);
        this.valorAtual = 0.0;
        this.unidade = unidade;
        this.tipo = tipo;
    }

    public Sensor(Sensor s) {
        super(s);
        this.valorAtual = s.getValorAtual();
        this.unidade = s.getUnidade();
        this.tipo = s.getTipo();
    }

    //getters e setters
    public double getValorAtual() {
        return this.valorAtual;
    }
    public void setValorAtual(double valorAtual) {
        this.valorAtual = valorAtual;
    }

    public String getUnidade() {
        return this.unidade;
    }
    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getTipo() {
        return this.tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public void ligar() {
        incrementarAtivacoes();
        setLigado(true);
    }

    @Override
    public void desligar() {
        setLigado(false);
        this.valorAtual = 0.0;
    }

    @Override
    public String getEstadoDetalhado() {
        if(!isLigado()) {
            return "Sensor Desligado";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("A medir");
        sb.append(" | Tipo : ").append(this.tipo);
        sb.append(" | Valor : ").append(this.valorAtual);
        sb.append(" ").append(this.unidade);
        
        return sb.toString();
    }

    @Override
    public Sensor clone() {
        return new Sensor(this);
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
