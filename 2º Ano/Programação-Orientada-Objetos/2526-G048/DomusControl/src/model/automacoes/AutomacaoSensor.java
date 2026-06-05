package model.automacoes;

import controller.DomusControl;
import model.dispositivos.Sensor;

public class AutomacaoSensor extends Automacao {
    private int idSensor;
    private String operador;  //como < , > , ==
    private double valorLimite;
    
    public AutomacaoSensor() {
        super();
        this.idSensor = -1;
        this.operador = ">";
        this.valorLimite = 0.0;
    }

    public AutomacaoSensor(String nome, int idSensor, String operador, double valorLimite) {
        super(nome);
        this.idSensor = idSensor;
        this.operador = operador;
        this.valorLimite = valorLimite;
    }

    public AutomacaoSensor(AutomacaoSensor as) {
        super(as);
        this.idSensor = as.getIdSensor();
        this.operador = as.getOperador();
        this.valorLimite = as.getValorLimite();
    }

    public int getIdSensor() {
        return this.idSensor;
    }
    public String getOperador() {
        return this.operador;
    }
    public double getValorLimite() {
        return this.valorLimite;
    }

    public void setIdSensor(int idSensor) {
        this.idSensor = idSensor;
    }
    public void setOperador(String operador) {
        this.operador = operador;
    }
    public void setValorLimite(double valorLimite) {
        this.valorLimite = valorLimite;
    }

    @Override
    public boolean avaliar(DomusControl sistema) {
        if(!isAtiva()) return false;
        Sensor sensor = (Sensor) sistema.getDispositivoById(this.idSensor);
        if(sensor == null || !sensor.isLigado()) return false;

        double valorAtual = sensor.getValorAtual();

        switch(this.operador) { 
            case ">": return valorAtual > this.valorLimite;
            case "<": return valorAtual < this.valorLimite;
            case "==": return valorAtual == this.valorLimite;
            default: return false;
        }
    }

    @Override
    public AutomacaoSensor clone() {
        return new AutomacaoSensor(this);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append("\n");
        sb.append("Sensor      : ").append(this.idSensor).append("\n");
        sb.append("Operador    : ").append(this.operador).append("\n");
        sb.append("ValorLimite : ").append(this.valorLimite);
        return sb.toString();
    }
}
