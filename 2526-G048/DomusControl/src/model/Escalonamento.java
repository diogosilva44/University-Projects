package model;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import model.automacoes.AcaoDispositivo;


public class Escalonamento implements Serializable{
    private String nome;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private Set<DayOfWeek> diasSemana;
    private List<AcaoDispositivo> acoes;
    private boolean ativo;
    
    public Escalonamento() {
        this.nome = "";
        this.horaInicio = LocalTime.of(0,0);
        this.horaFim = LocalTime.of(23,59);
        this.diasSemana = new HashSet<>();
        this.acoes = new ArrayList<>();  
        this.ativo = false;
    }

    public Escalonamento(String nome, LocalTime horaInicio, LocalTime horaFim, Set<DayOfWeek> diasSemana) {
        this.nome = nome;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.diasSemana = new HashSet<>(diasSemana);
        this.acoes = new ArrayList<>();
        this.ativo = true;
    }

    public Escalonamento(Escalonamento esca) {
        this.nome = esca.getNome();
        this.horaInicio = esca.getHoraInicio();
        this.horaFim = esca.getHoraFim();
        this.diasSemana = new HashSet<>(esca.getDiasSemana());
        this.acoes = new ArrayList<>();
        this.ativo = esca.isAtivo();
        for(AcaoDispositivo ad : esca.getAcoes()) {
            this.acoes.add(ad.clone());
        }
    }

    public String getNome() {
        return this.nome;
    }
    public LocalTime getHoraInicio() {
        return this.horaInicio;
    }
    public LocalTime getHoraFim() {
        return this.horaFim;
    }
    public boolean isAtivo() {
        return this.ativo;
    }
    public Set<DayOfWeek> getDiasSemana() {
        return new HashSet<>(this.diasSemana);
    }
    public List<AcaoDispositivo> getAcoes() {
        List<AcaoDispositivo> copia = new ArrayList<>();
        for(AcaoDispositivo ad : this.acoes) {
            copia.add(ad.clone());
        }
        return copia;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }
    public void setHoraFim(LocalTime horaFim) {
        this.horaFim = horaFim;
    }
    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public void addAcao(AcaoDispositivo acao) {
        this.acoes.add(acao.clone());
    }
    public void removeAcao(int index) {
        if(index >= 0 && index < this.acoes.size()) {
            this.acoes.remove(index);
        }
    }
    public void addDia(DayOfWeek dia) {
        this.diasSemana.add(dia);
    }
    public void removeDia(DayOfWeek dia) {
        this.diasSemana.remove(dia);
    }

    public boolean estaAtivo(LocalDateTime dataHora) {
        if(!this.ativo) return false;
        DayOfWeek diaAtual = dataHora.getDayOfWeek();
        LocalTime horaAtual = dataHora.toLocalTime();
        return this.diasSemana.contains(diaAtual) &&
               !horaAtual.isBefore(this.horaInicio) &&
               !horaAtual.isAfter(this.horaFim);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || this.getClass() != o.getClass()) return false;
        Escalonamento esca = (Escalonamento) o;
        return this.nome.equals(esca.nome);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nome     : ").append(this.nome).append("\n");
        sb.append("Inicio   : ").append(this.horaInicio).append("\n");
        sb.append("Fim      : ").append(this.horaFim).append("\n");
        sb.append("Dias     : ").append(this.diasSemana).append("\n");
        sb.append("Ativo    : ").append(this.ativo ? "Sim" : "Não").append("\n");
        sb.append("Acoes    : ").append(this.acoes.size());
        return sb.toString();
    }

    @Override
    public Escalonamento clone() {
        return new Escalonamento(this);
    }
}
