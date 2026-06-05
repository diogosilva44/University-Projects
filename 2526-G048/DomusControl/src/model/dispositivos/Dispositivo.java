package model.dispositivos;

import java.io.Serializable;

public abstract class Dispositivo implements Serializable {
    private static int contadorId = 1;

    private int id;
    private String marca;
    private String modelo;
    private double consumoWh;
    private boolean ligado;
    private int numAtivacoes;
    private double horasLigado;

    public Dispositivo(String marca, String modelo, double consumoWh) {
        this.id = contadorId++;
        this.marca = marca;
        this.modelo = modelo;
        this.consumoWh = consumoWh;
        this.ligado = false;
        this.numAtivacoes = 0;
        this.horasLigado = 0.0;
    }

    public Dispositivo() {
        this.id = contadorId++;
        this.marca = "";
        this.modelo = "";
        this.consumoWh = 0.0;
        this.ligado = false;
        this.numAtivacoes = 0;
        this.horasLigado = 0.0;
    }

    public Dispositivo(Dispositivo d) {
        this.id = d.getId();
        this.marca = d.getMarca();
        this.modelo = d.getModelo();
        this.consumoWh = d.getConsumoWh();
        this.ligado = d.isLigado();
        this.numAtivacoes = d.getNumAtivacoes();
        this.horasLigado = d.getHorasLigado();
    }

    //Getters - Setters
    public static int getContadorId() {
        return contadorId;
    }
    public static void setContadorId(int valor) {
        contadorId = valor;
    }
    
    public int getId() {
        return this.id;
    }

    public String getMarca() {
        return this.marca;
    }
    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return this.modelo;
    }
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public double getConsumoWh() {
        return this.consumoWh;
    }
    public void setConsumoWh(double consumo) {
        this.consumoWh = consumo;
    }

    public boolean isLigado() {
        return this.ligado;
    }
    protected void setLigado(boolean ligado) {
        this.ligado = ligado;
    }

    public static int getTotalDispositivos() {
        return contadorId - 1;
    }

    public int getNumAtivacoes() {
        return this.numAtivacoes;
    }

    public double getHorasLigado() {
        return this.horasLigado;
    }

    public void addHorasLigado(double horas) {
        if(isLigado()) {
            this.horasLigado += horas;
        }
    }

    protected void incrementarAtivacoes() {
    if (!isLigado()) {
        this.numAtivacoes++;
    }
}

    //métodos abstratos
    public abstract void ligar();
    public abstract void desligar();
    public abstract String getEstadoDetalhado();

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || this.getClass() != o.getClass()) return false;
        Dispositivo d = (Dispositivo) o;
        return this.id == d.id;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Tipo     : ").append(this.getClass().getSimpleName()).append("\n");
        sb.append("ID       : ").append(this.id).append("\n");
        sb.append("Marca    : ").append(this.marca).append("\n");
        sb.append("Modelo   : ").append(this.modelo).append("\n");
        sb.append("Consumo  : ").append(this.consumoWh).append(" Wh\n");
        sb.append("Estado   : ").append(this.ligado ? "LIGADO" : "DESLIGADO");

        return sb.toString();
    }

    @Override
    public abstract Dispositivo clone();




}
