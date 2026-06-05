package model.dispositivos;

public class Lampada extends Dispositivo {
    //limites da temperatura de cor (enunciado)
    public static final int COR_MIN = 2700;
    public static final int COR_MAX = 4000;  
    
    private int intensidade;  //percentagem
    private int corKelvin;

    public Lampada() {
        super();
        this.intensidade = 100;
        this.corKelvin = 3000;
    }

    public Lampada(String marca, String modelo, double consumoWh, int intensidade, int corKelvin) {
        super(marca, modelo, consumoWh);
        this.intensidade = validaIntensidade(intensidade);
        this.corKelvin = validaCor(corKelvin);
    }

    public Lampada(Lampada l) {
        super(l);
        this.intensidade = l.getIntensidade();
        this.corKelvin = l.getCorKelvin();
    }

    private int validaIntensidade(int intensidade) {
        if(intensidade < 0) return 0;
        if(intensidade > 100) return 100;
        return intensidade;
    }
    private int validaCor(int cor) {
        if(cor < COR_MIN) return COR_MIN;
        if(cor > COR_MAX) return COR_MAX;
        return cor;
    }

    //getters e setters
    public int getIntensidade() {
        return this.intensidade;
    }
    public void setIntensidade(int intensidade) {
        this.intensidade = validaIntensidade(intensidade);
    }

    public int getCorKelvin() {
        return this.corKelvin;
    }
    public void setCorKelvin(int corKelvin) {
        this.corKelvin = validaCor(corKelvin);
    }
    
    @Override
    public void ligar() {
        incrementarAtivacoes();
        setLigado(true);
        if(this.intensidade == 0) {
            this.intensidade = 100;
        }
    }

    @Override
    public void desligar() {
        setLigado(false);
    }

    @Override
    public String getEstadoDetalhado() {
        StringBuilder sb = new StringBuilder();
        sb.append(" | Intensidade : ").append(this.intensidade).append("%");
        sb.append(" | Cor : ").append(this.corKelvin).append("K");
        return sb.toString();
    }

    @Override
    public Lampada clone() {
        return new Lampada(this);
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
