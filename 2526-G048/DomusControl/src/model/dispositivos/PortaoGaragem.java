package model.dispositivos;

public class PortaoGaragem extends Dispositivo{
    public static final int ABERTURA_MIN = 0;
    public static final int ABERTURA_MAX = 100;

    private int abertura;  //percentagem

    public PortaoGaragem() {
        super();
        this.abertura = 0;
    }

    public PortaoGaragem(String marca, String modelo, double consumoWh, int abertura) {
        super(marca,modelo,consumoWh);
        this.abertura = validaAbertura(abertura);
    }

    public PortaoGaragem(PortaoGaragem pg) {
        super(pg);
        this.abertura = pg.getAbertura();
    }

    private int validaAbertura(int abertura) {
        if(abertura < ABERTURA_MIN) return ABERTURA_MIN;
        if(abertura > ABERTURA_MAX) return ABERTURA_MAX;
        return abertura;
    }

    //getters e setters
    public int getAbertura() {
        return this.abertura;
    }
    public void setAbertura(int abertura) {
        this.abertura = validaAbertura(abertura);
    }

    @Override
    public void ligar() {
        incrementarAtivacoes();
        setLigado(true);
        this.abertura = 100;
    }
    @Override
    public void desligar() {
        setLigado(false);
        this.abertura = 0;
    }

    public void abrir() {
        setLigado(true);
        this.abertura = 100;
    }
    public void fechar() {
        setLigado(false);
        this.abertura = 0;
    }
    public void abrirParcialmente(int percentagem) {
        this.abertura = validaAbertura(percentagem);
        setLigado(this.abertura > 0);
    }

    @Override
    public String getEstadoDetalhado() {
        StringBuilder sb = new StringBuilder();
        if(this.abertura == 0) {
            sb.append("Fechado");
        } else if (this.abertura == 100) {
            sb.append("Aberto");
        } else {
            sb.append("Aberto Parcialmente");
            sb.append(" | Abertura : ").append(this.abertura).append("%");
        }
        return sb.toString();
    }

    @Override
    public PortaoGaragem clone() {
        return new PortaoGaragem(this);
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
