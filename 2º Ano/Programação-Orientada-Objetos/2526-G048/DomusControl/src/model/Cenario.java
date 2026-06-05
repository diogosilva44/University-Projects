package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import controller.DomusControl;
import model.automacoes.AcaoDispositivo;
import model.dispositivos.Dispositivo;

public class
Cenario implements Serializable{
    private String nome;
    private String descricao;
    private List<AcaoDispositivo> acoes;

    public Cenario() {
        this.nome = "";
        this.descricao = "";
        this.acoes = new ArrayList<>();
    }

    public Cenario(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
        this.acoes = new ArrayList<>();
    }

    public Cenario(Cenario cena) {
        this.nome = cena.getNome();
        this.descricao = cena.getDescricao();
        this.acoes = new ArrayList<>();
        for(AcaoDispositivo ad : cena.getAcoes()) {
            this.acoes.add(ad.clone());
        }
    }

    public String getNome() {
        return this.nome;
    }
    public String getDescricao() {
        return this.descricao;
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
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void addAcao(AcaoDispositivo acao) {
        this.acoes.add(acao.clone());
    }
    public void removeAcao(int index) {
        if(index >= 0 && index < this.acoes.size()) {
            this.acoes.remove(index);
        }
    }

    public void ativar(DomusControl sistema) {
        for(AcaoDispositivo acao : this.acoes) {
            Dispositivo d = sistema.getDispositivoById((acao.getIdDispositivo()));
            if(d != null) {
                acao.executar(d);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || o.getClass() != this.getClass()) return false;
        Cenario c = (Cenario) o;
        return this.nome.equals(c.nome);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nome    : ").append(this.nome).append("\n");
        sb.append("Descrição : ").append(this.descricao).append("\n");
        sb.append("Ações    : ").append(this.acoes.size());
        return sb.toString();
    }

    @Override
    public Cenario clone() {
        return new Cenario(this);
    }
}
