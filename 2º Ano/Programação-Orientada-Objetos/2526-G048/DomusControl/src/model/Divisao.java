package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import model.dispositivos.Dispositivo;

public class Divisao implements Serializable {
    private String nome;
    private List<Dispositivo> dispositivos;

    public Divisao() {
        this.nome = "Sem nome";
        this.dispositivos = new ArrayList<>();
    }

    public Divisao(String nome) {
        this.nome = nome;
        this.dispositivos = new ArrayList<>();
    }

    public Divisao(Divisao dv) {
        this.nome = dv.getNome();
        this.dispositivos = new ArrayList<>();
        for(Dispositivo d : dv.getDispositivos()) {
            this.dispositivos.add(d.clone());
        }
    }

    //getters e setters
    public String getNome() {
        return this.nome;
    }
    public void setNome(String nome) {
        this.nome = nome; 
    }

    public List<Dispositivo> getDispositivos() {
        List<Dispositivo> copia = new ArrayList<>();
        for(Dispositivo d : this.dispositivos) {
            copia.add(d.clone());
        }
        return copia;
    }

    public void addDispositivo(Dispositivo d) {
        this.dispositivos.add(d.clone());
    }

    public void removeDispositivo(int id) {
        this.dispositivos.removeIf(d -> d.getId() == id);
    }

    public Dispositivo getDispositivo(int id) {
        for(Dispositivo d : this.dispositivos) {
            if(d.getId() == id) {
                return d.clone();
            }
        }
        return null;
    }

    Dispositivo getDispositivoInterno(int id) {
        for(Dispositivo d : this.dispositivos) {
            if(d.getId() == id) {
                return d;
            }
        }
        return null;
    }

    public boolean existeDispositivo(int id) {
        for(Dispositivo d : this.dispositivos) {
            if(d.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public int numDispositivos() {
        return this.dispositivos.size();
    }

    public void acumularHoras(double horas) {
        for(Dispositivo d : this.dispositivos) {
            d.addHorasLigado(horas);
        }
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || o.getClass() != this.getClass()) return false;
        Divisao dv = (Divisao) o;
        return this.nome.equals(dv.nome);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Divisão       : ").append(this.nome).append("\n");
        sb.append("Dispositivos  : ").append(this.dispositivos.size()).append("\n");
        for(Dispositivo d : this.dispositivos) {
            sb.append(d.toString()).append("\n");
            sb.append(" ---------------------- ").append("\n");
        }
        return sb.toString();
    }

    @Override
    public Divisao clone() {
        return new Divisao(this);
    }

}
