package model.automacoes;

import java.io.Serializable;
import model.dispositivos.Dispositivo;

public class AcaoDispositivo implements Serializable {
    private int idDispositivo;
    private String descricao;
    private AcaoSerializavel acao;

    public AcaoDispositivo() {
        this.idDispositivo = -1;
        this.descricao = "";
        this.acao = d -> {};
    }

    public AcaoDispositivo(int idDispositivo, String descricao, AcaoSerializavel acao) {
        this.idDispositivo = idDispositivo;
        this.descricao = descricao;
        this.acao = acao;
    }

    public AcaoDispositivo(AcaoDispositivo ac) {
        this.idDispositivo = ac.getIdDispositivo();
        this.descricao = ac.getDescricao();
        this.acao = ac.getAcao();
    }

    public int getIdDispositivo() {
        return this.idDispositivo;
    }
    public String getDescricao() {
        return this.descricao;
    }
    public AcaoSerializavel getAcao() {
        return this.acao;
    }

    public void setIdDispositivo(int idDispositivo) {
        this.idDispositivo = idDispositivo;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public void setAcao(AcaoSerializavel acao) {
        this.acao = acao;
    }

    public void executar(Dispositivo d) {
        this.acao.executar(d);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || o.getClass() != this.getClass()) return false;

        AcaoDispositivo ad = (AcaoDispositivo) o;
        return this.idDispositivo == ad.idDispositivo &&
               this.descricao.equals(ad.descricao);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Dispositivo : ").append(this.idDispositivo).append("\n");
        sb.append("Descricao   : ").append(this.descricao);
        return sb.toString();
    }

    @Override
    public AcaoDispositivo clone() {
        return new AcaoDispositivo(this);
    }
}
