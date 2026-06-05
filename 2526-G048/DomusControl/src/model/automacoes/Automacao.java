package model.automacoes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import controller.DomusControl;
import model.dispositivos.Dispositivo;

public abstract class Automacao implements Serializable {
    private String nome;
    private boolean ativa;
    private List <AcaoDispositivo> acoes;

    public Automacao() {
        this.nome = "";
        this.ativa = false;
        this.acoes = new ArrayList<>();
    }

    public Automacao(String nome) {
        this.nome = nome;
        this.ativa = true;
        this.acoes = new ArrayList<>();
    }

    public Automacao(Automacao auto) {
        this.nome = auto.getNome();
        this.ativa = auto.isAtiva();
        this.acoes = new ArrayList<>();
        for(AcaoDispositivo ad : auto.getAcoes()) {
            this.acoes.add(ad.clone());
        }
    }

    public String getNome() {
        return this.nome;
    }
    public boolean isAtiva() {
        return this.ativa;
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
    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }

    public void addAcao(AcaoDispositivo acao) {
        this.acoes.add(acao.clone());
    }
    public void removeAcao(int index) {
        if(index >= 0 && index <this.acoes.size()) {
            this.acoes.remove(index);
        }
    }

    public abstract boolean avaliar(DomusControl sistema);

    public void executar(DomusControl sistema) {
        if(!this.ativa) return;
        for(AcaoDispositivo acao : this.acoes) {
            Dispositivo d = sistema.getDispositivoById(acao.getIdDispositivo());
            if(d != null) {
                acao.executar(d);
            } 
        }
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(o == null || o.getClass() != this.getClass()) return false;
        Automacao a = (Automacao) o;
        return this.nome.equals(a.nome);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nome  : ").append(this.nome).append("\n");
        sb.append("Ativa : ").append(this.ativa ? "Sim" : "Não").append("\n");
        sb.append("Ações : ").append(this.acoes.size());
        return sb.toString();
    }

    @Override
    public abstract Automacao clone();


}
