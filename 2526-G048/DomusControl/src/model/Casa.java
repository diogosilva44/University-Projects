package model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import exceptions.DispositivoNotFoundException;
import exceptions.DivisaoNotFoundException;
import model.dispositivos.Dispositivo;

public class Casa implements Serializable {
    private static int contadorId = 1;

    private String id;  
    private String nome;
    private String morada;
    private Map<String, Divisao> divisoes;

    public Casa() {
        this.id = "C" + contadorId++;
        this.nome = "Sem Nome";
        this.morada = "Sem Morada";
        this.divisoes = new HashMap<>();
    }

    public Casa(String nome, String morada) {
        this.id = "C" + contadorId++;
        this.nome = nome;
        this.morada = morada;
        this.divisoes = new HashMap<>();
    }

    public Casa(Casa c) {
        this.id = c.getId();
        this.nome = c.getNome();
        this.morada = c.getMorada();
        this.divisoes = new HashMap<>();
        for(Map.Entry<String,Divisao> entry : c.getDivisoes().entrySet()) {
            this.divisoes.put(entry.getKey(), entry.getValue().clone());
        }
    }

    //getters e setters
    public static int getContadorId() {
        return contadorId;
    }
    public static void setContadorId(int valor) {
        contadorId = valor;
    }
    
    public String getId() {
        return this.id;
    }
    
    public String getNome() {
        return this.nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMorada() {
        return this.morada;
    }
    public void setMorada(String morada) {
        this.morada = morada;
    }

    public Map<String,Divisao> getDivisoes() {
        Map<String,Divisao> copia = new HashMap<>();
        for(Map.Entry<String,Divisao> entry : this.divisoes.entrySet()) {
            copia.put(entry.getKey(), entry.getValue().clone());
        }
        return copia;
    }

    // -- GESTAO DE DIVISOES --

    public void addDivisao(Divisao d) {
        this.divisoes.put(d.getNome(), d.clone());
    }

    public void removeDivisao(String nome) {
        this.divisoes.remove(nome);
    }

    public Divisao getDivisao(String nome) {
        Divisao d = this.divisoes.get(nome);
        if(d == null) {
            return null;
        }
        return d.clone();
    }

    public boolean existeDivisao(String nome) {
        return this.divisoes.containsKey(nome);
    }

    public int numDivisoes() {
        return this.divisoes.size();
    }

    // -- GESTAO DE DISPOSITIVOS DA CASA --

    public void addDispositivoDivisao(String nomeDivisao, Dispositivo d) {
        Divisao dv = this.divisoes.get(nomeDivisao);
        if(dv != null) {
            dv.addDispositivo(d);
        }
    }

    public void removeDispositivoDivisao(String nomeDivisao, int idDispositivo) {
        Divisao dv = this.divisoes.get(nomeDivisao);
        if(dv != null) {
            dv.removeDispositivo(idDispositivo);
        }
    }

    public Dispositivo getDispositivoDivisao(String nomeDivisao, int idDispositivo) {
        Divisao dv = this.divisoes.get(nomeDivisao);
        if(dv == null) {
            return null;
        }
        return dv.getDispositivo(idDispositivo);
    }

    public Dispositivo getDispositivoById(int idDispositivo) {
        for(Divisao d : this.divisoes.values()) {
            Dispositivo disp = d.getDispositivoInterno(idDispositivo);
            if(disp != null) return disp;
        }
        return null;
    }

    // -- Estatisticas --

    public void acumularHoras(double horas) {
            for (Divisao d : this.divisoes.values()) {
                d.acumularHoras(horas);
            }
    }

    public double consumoTotal() {
        double consumoT = 0.0;
        for(Divisao dv : this.divisoes.values()) {
            for(Dispositivo disp : dv.getDispositivos()) {
                consumoT += disp.getConsumoWh() * disp.getHorasLigado();
            }
        }
        return consumoT;
    }

    public int numTotalDispositivos() {
        int total = 0;
        for(Divisao d : this.divisoes.values()) {
            total += d.numDispositivos();
        }
        return total;
    }

    public Divisao divisaoComMaisDispositivos() {
        Divisao comMais = null;
        int max = 0;
        for(Divisao d : this.divisoes.values()) {
            if(d.numDispositivos() > max) {
                max = d.numDispositivos();
                comMais = d;
            }
        }
        if(comMais == null) {
            return null;
        } else {
            return comMais.clone();
        }
    }

    public void ligarDispositivo(String nomeDivisao,int idDispositivo) throws DivisaoNotFoundException, DispositivoNotFoundException {
        Divisao d = this.divisoes.get(nomeDivisao);
        if(d == null) throw new DivisaoNotFoundException(nomeDivisao);
        Dispositivo disp = d.getDispositivoInterno(idDispositivo);
        if(disp == null) throw new DispositivoNotFoundException(""+idDispositivo);
        disp.ligar(); 
    }

    public void desligarDispositivo(String nomeDivisao,int idDispositivo) throws DivisaoNotFoundException, DispositivoNotFoundException {
        Divisao d = this.divisoes.get(nomeDivisao);
        if(d == null){
            throw new DivisaoNotFoundException(nomeDivisao);
        }
        Dispositivo disp = d.getDispositivoInterno(idDispositivo);
        if(disp == null){
            throw new DispositivoNotFoundException(""+idDispositivo);
        }
        disp.desligar(); 
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || o.getClass() != this.getClass()) return false;
        Casa c = (Casa) o;
        return this.id.equals(c.getId());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID             : ").append(this.id).append("\n");
        sb.append("Nome           : ").append(this.nome).append("\n");
        sb.append("Morada         : ").append(this.morada).append("\n");
        sb.append("Divisões       : ").append(this.divisoes.size()).append("\n");
        for (Divisao d : this.divisoes.values()) {
            sb.append("\n").append(d.toString()).append("\n");
        }

        return sb.toString();
    }

    @Override
    public Casa clone() {
        return new Casa(this);
    }     
}
