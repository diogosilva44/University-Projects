package model.dispositivos;

public class ColunaSom extends Dispositivo {
    public static final int VOLUME_MIN = 0;
    public static final int VOLUME_MAX = 100;

    private int volume;
    private String fonte; //Bluetooth , wifi , aux , etc 
    
    public ColunaSom() {
        super();
        this.volume = 44;
        this.fonte = "Bluetooth";
    }

    public ColunaSom(String marca, String modelo, double consumoWh, int volume, String fonte) {
        super(marca,modelo,consumoWh);
        this.volume = validaVolume(volume);
        this.fonte = fonte;
    }

    public ColunaSom(ColunaSom cs) {
        super(cs);
        this.volume = cs.getVolume();
        this.fonte = cs.getFonte();
    }

    private int validaVolume(int volume) {
        if(volume < VOLUME_MIN) return VOLUME_MIN;
        if(volume > VOLUME_MAX) return VOLUME_MAX;
        return volume;
    }

    //getters e setters
    public int getVolume() {
        return this.volume;
    }
    public void setVolume(int volume) {
        this.volume = validaVolume(volume);
    }

    public String getFonte() {
        return this.fonte;
    }
    public void setFonte(String fonte) {
        this.fonte = fonte;
    }

    @Override
    public void ligar() {
        incrementarAtivacoes();
        setLigado(true);
        if(this.volume == 0) this.volume = 44;
    }

    @Override
    public void desligar() {
        setLigado(false);
    }

    @Override
    public String getEstadoDetalhado() {
        StringBuilder sb = new StringBuilder();
        sb.append(" | Volume : ").append(this.volume).append("%");
        sb.append(" | Fonte : ").append(this.fonte);

        return sb.toString();
    }

    @Override
    public ColunaSom clone() {
        return new ColunaSom(this);
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
