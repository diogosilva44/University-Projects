package controller;

import model.Casa;
import model.Cenario;
import model.Divisao;
import model.Escalonamento;
import model.automacoes.AcaoDispositivo;
import model.automacoes.Automacao;
import model.automacoes.AutomacaoSensor;
import model.dispositivos.*;

import model.utilizadores.Administrador;
import model.utilizadores.Utilizador;
import model.utilizadores.UtilizadorComum;
import model.automacoes.AcaoSerializavel;

import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import exceptions.*;

public class DomusControl implements Serializable {
    private int contadorDispositivos;
    private int contadorUtilizadores;
    private int contadorCasas;
    private Map<String, Utilizador> utilizadores;
    private Map<String, Casa> casas;
    private LocalDateTime dataHoraAtual;

    private List<Automacao> automacoes;
    private LocalDateTime ultimaVerificacao;
    private List<Escalonamento> escalonamentos;
    private List<Cenario> cenarios;

    public DomusControl() {
        this.utilizadores = new HashMap<>();
        this.casas = new HashMap<>();
        this.dataHoraAtual = LocalDateTime.now();
        this.automacoes = new ArrayList<>();
        this.ultimaVerificacao = this.dataHoraAtual;
        this.escalonamentos = new ArrayList<>();
        this.cenarios = new ArrayList<>();
        this.contadorDispositivos = 1;
        this.contadorUtilizadores = 1;
        this.contadorCasas = 1;
    }

    public DomusControl(DomusControl dc) {
        this.dataHoraAtual = dc.getDataHoraAtual();
        this.ultimaVerificacao = dc.getUltimaVerificacao();
        this.utilizadores = new HashMap<>();
        this.casas = new HashMap<>();
        this.automacoes = new ArrayList<>();
        this.escalonamentos = new ArrayList<>();
        this.cenarios = new ArrayList<>();

        for(Map.Entry<String, Utilizador> entry : dc.getUtilizadores().entrySet()) {
            this.utilizadores.put(entry.getKey(),entry.getValue().clone());
        }
        for(Map.Entry<String,Casa> entry : dc.getCasas().entrySet()) {
            this.casas.put(entry.getKey(), entry.getValue().clone());
        }
        for(Automacao a : dc.getAutomacoes()) {
            this.automacoes.add(a.clone());
        }
        for(Escalonamento e : dc.getEscalonamentos()) {
            this.escalonamentos.add(e.clone());
        }
        for(Cenario c : dc.getCenarios()) {
            this.cenarios.add(c.clone());
        }

        this.contadorDispositivos = dc.getContadorDispositivos();
        this.contadorUtilizadores = dc.getContadorUtilizadores();
        this.contadorCasas = dc.getContadorCasas();
    }
    
    // ----- Getters e Setters -----
    public LocalDateTime getDataHoraAtual() {
        return this.dataHoraAtual;
    }
    public void setDataHoraAtual(LocalDateTime dataHoraAtual) {
        this.dataHoraAtual = dataHoraAtual;
    }
    
    public Map<String,Utilizador> getUtilizadores() {
        Map<String,Utilizador> copia = new HashMap<>();
        for(Map.Entry<String,Utilizador> entry : this.utilizadores.entrySet()) {
            copia.put(entry.getKey(),entry.getValue().clone());
        }
        return copia;
    }

    public Map<String,Casa> getCasas() {
        Map<String,Casa> copia = new HashMap<>();
        for(Map.Entry<String, Casa> entry : this.casas.entrySet()) {
            copia.put(entry.getKey(),entry.getValue().clone());
        }
        return copia;
    }
    
    public List<Automacao> getAutomacoes() {
        List<Automacao> copia = new ArrayList<>();
        for(Automacao a : this.automacoes) {
            copia.add(a.clone());
        }
        return copia;
    }

    public LocalDateTime getUltimaVerificacao() {
        return this.ultimaVerificacao;
    }

    public List<Escalonamento> getEscalonamentos() {
        List<Escalonamento> copia = new ArrayList<>();
        for(Escalonamento e : this.escalonamentos) {
            copia.add(e.clone());
        }
        return copia;
    } 
    public List<Cenario> getCenarios() {
        List<Cenario> copia = new ArrayList<>();
        for(Cenario c : this.cenarios) {
            copia.add(c.clone());
        }
        return copia;
    }

    public int getContadorDispositivos() { return this.contadorDispositivos; }
    public int getContadorUtilizadores() { return this.contadorUtilizadores; }
    public int getContadorCasas()        { return this.contadorCasas; }

   // ----- METODOS DE GESTAO DE USERS -----
    public void addUtilizador(Utilizador u) {
        this.utilizadores.put(u.getId(),u.clone());
    }

    public String criarAdministrador(String nome, String email, String password) {
        Administrador admin = new Administrador(nome,email,password);
        addUtilizador(admin);
        return admin.getId();
    }
    public String criarUtilizadorComum(String nome, String email, String password) {
        UtilizadorComum user = new UtilizadorComum(nome, email, password);
        addUtilizador(user);
        return user.getId();
    }

    public void removeUtilizador(String id) throws UtilizadorNotFoundException {
        if(!this.utilizadores.containsKey(id)) {
            throw new UtilizadorNotFoundException("Utilizador com id '" + id + "' não encontrado");
        }
        this.utilizadores.remove(id);
    }

    public Utilizador getUtilizador(String id) throws UtilizadorNotFoundException {
        Utilizador u = this.utilizadores.get(id);
        if(u == null) throw new UtilizadorNotFoundException("Utilizador com id '" + id + "' não encontrado.");
        return u.clone();
    }

    public boolean existeUtilizador(String id) {
        return this.utilizadores.containsKey(id);
    }

    public int numUtilizadores() {
        return this.utilizadores.size();
    }

    public boolean autenticar(String idUtilizador ,String password) throws UtilizadorNotFoundException {
        Utilizador u = this.utilizadores.get(idUtilizador);
        if(u == null) throw new UtilizadorNotFoundException(idUtilizador);
        return u.getPassword().equals(password);
    }

    //----- METODOS DE GESTAO DE CASAS -----
    public void addCasa(Casa c) {
        this.casas.put(c.getId(),c.clone());
    }
    
    public String criarCasa(String nome, String morada, String idUtilizador) {
        Casa casa = new Casa(nome,morada);
        addCasa(casa);
        addCasaUtilizador(idUtilizador, casa.getId());
        return casa.getId();
    }

    public void removeCasa(String idCasa)  throws CasaNotFoundException {
        if(!this.casas.containsKey(idCasa)) {
            throw new CasaNotFoundException(idCasa);
        }        
        this.casas.remove(idCasa);
    }

    public Casa getCasa(String idCasa) throws CasaNotFoundException {
        Casa c = this.casas.get(idCasa);
        if(c == null) throw new CasaNotFoundException(idCasa);
        return c.clone();
    }

    public boolean existeCasa(String id) {
        return this.casas.containsKey(id);
    }

    public int numCasas() {
        return this.casas.size();
    }

    public void addCasaUtilizador(String idUser, String idCasa) {
        Utilizador u = this.utilizadores.get(idUser);
        if(u == null) return;
        if(!this.casas.containsKey(idCasa)) return;
        u.addCasa(idCasa);
    }

    public boolean utilizadorTemAcessoCasa(String idUtilizador, String idCasa) {
        try {
            Utilizador u = getUtilizador(idUtilizador);
            return u.getCasas().contains(idCasa);
        } catch (UtilizadorNotFoundException e) {
            return false;
        }
    }

    public void addDivisaoCasa(String idCasa, Divisao d) throws CasaNotFoundException, DivisaoAlreadyExistsException {
        Casa c = this.casas.get(idCasa);
        if(c == null) throw new CasaNotFoundException(idCasa);
        if(c.existeDivisao(d.getNome())) throw new DivisaoAlreadyExistsException(d.getNome());
        c.addDivisao(d);
    }

    public void removeDivisaoCasa(String idCasa, String nomeDivisao) throws CasaNotFoundException, DivisaoNotFoundException {
        Casa c = this.casas.get(idCasa);
        if (c == null) throw new CasaNotFoundException(idCasa);
        if (!c.existeDivisao(nomeDivisao)) throw new DivisaoNotFoundException(nomeDivisao);
        c.removeDivisao(nomeDivisao);
    }

    public void addDispositivoCasa(String idCasa, String nomeDivisao,Dispositivo d) throws CasaNotFoundException,DivisaoNotFoundException {
        Casa c = this.casas.get(idCasa);
        if(c == null) throw new CasaNotFoundException(idCasa);
        if(!c.existeDivisao(nomeDivisao)) throw new DivisaoNotFoundException(nomeDivisao);
        c.addDispositivoDivisao(nomeDivisao, d);
    }

    public void removeDispositivoCasa(String idCasa, String nomeDivisao, int idDispositivo) throws CasaNotFoundException, DivisaoNotFoundException, DispositivoNotFoundException {
        Casa c = this.casas.get(idCasa);
        if (c == null) {
            throw new CasaNotFoundException(idCasa);
        }
        if (!c.existeDivisao(nomeDivisao)) {
            throw new DivisaoNotFoundException(nomeDivisao);
        }
        Divisao d = c.getDivisao(nomeDivisao);
        if (!d.existeDispositivo(idDispositivo)) {
            throw new DispositivoNotFoundException(""+idDispositivo);
        }
        c.removeDispositivoDivisao(nomeDivisao, idDispositivo);
    }

    public void ligarDispositivo(String idCasa, String nomeDivisao, int idDispositivo) throws CasaNotFoundException,DivisaoNotFoundException,DispositivoNotFoundException {
        Casa c = this.casas.get(idCasa);
        if(c == null) {
            throw new CasaNotFoundException(idCasa);
        }
        c.ligarDispositivo(nomeDivisao, idDispositivo);
    }

        public void desligarDispositivo(String idCasa, String nomeDivisao, int idDispositivo) throws CasaNotFoundException,DivisaoNotFoundException,DispositivoNotFoundException {
        Casa c = this.casas.get(idCasa);
        if(c == null) {
            throw new CasaNotFoundException(idCasa);
        }
        c.desligarDispositivo(nomeDivisao, idDispositivo);
    }

    public Dispositivo getDispositivoById(int idDispositivo) {
        for(Casa c : this.casas.values()) {
            Dispositivo disp = c.getDispositivoById(idDispositivo);
            if(disp != null) {
                return disp;
            }
        }   
        return null;
    }

    public String getTipoDispositivo(int id) {
        Dispositivo d = getDispositivoById(id);
        if(d == null) {
            return null;
        }
        return d.getClass().getSimpleName();
    }

    //criar dispositivos
    public Dispositivo criarLampada(String marca, String modelo, double consumo, int intensidade, int corKelvin) {
        return new Lampada(marca,modelo,consumo,intensidade,corKelvin);
    }
    public Dispositivo criarColunaSom(String marca,String modelo, double consumo,int volume, String fonte) {
        return new ColunaSom(marca,modelo,consumo,volume,fonte);
    }
    public Dispositivo criarCortina(String marca, String modelo, double consumo,int abertura) {
        return new Cortina(marca,modelo,consumo,abertura);
    }
    public Dispositivo criarPortaoGaragem(String marca,String modelo, double consumo,int abertura) {
        return new PortaoGaragem(marca, modelo, consumo, abertura);
    }
    public Dispositivo criarSensor(String marca, String modelo, double consumo,String tipoSensor,String unidade) {
        return new Sensor(marca, modelo, consumo, tipoSensor, unidade);
    }
    public Dispositivo criarRele(String marca, String modelo, double consumo) {
        return new Rele(marca,modelo,consumo);
    }
    public Dispositivo criarTelevisao(String marca, String modelo, double consumo,int canal, int volume) {
        return new Televisao(marca, modelo, consumo, canal, volume);
    }

    //regular dispositivos
    public void regularDispositivo(int idDispositivo, AcaoSerializavel acao) {
        Dispositivo d = getDispositivoById(idDispositivo);
        if(d != null) acao.executar(d);
    }

    public void setIntensidadeLampada(int id, int intensidade) {
        regularDispositivo(id, d -> ((Lampada) d).setIntensidade(intensidade));
    }
    public void setCorLampada(int id, int cor) {
        regularDispositivo(id, d -> ((Lampada) d).setCorKelvin(cor));
    }

    public void setVolumeColuna(int id,int volume) {
        regularDispositivo(id, d -> ((ColunaSom) d).setVolume(volume));
    }
    public void setFonteColuna(int id, String fonte) {
        regularDispositivo(id, d -> ((ColunaSom) d).setFonte(fonte));
    }

    public void setAberturaCortina(int id, int abertura) {
        regularDispositivo(id, d -> ((Cortina) d).abrirParcialmente(abertura));
    }

    public void setAberturaPortao(int id,int abertura) {
        regularDispositivo(id, d -> ((PortaoGaragem) d).abrirParcialmente(abertura));
    }

    public void setValorSensor(int id, double valor) {
        regularDispositivo(id, d -> ((Sensor) d).setValorAtual(valor));
    }

    public void setCanalTelevisao(int id, int canal) {
        regularDispositivo(id, d -> ((Televisao) d).setCanal(canal));
    }
    public void setVolumeTelevisao(int id, int volume) {
        regularDispositivo(id, d -> ((Televisao) d).setVolume(volume));
    }

    // ------ GESTAO DE AUTOMACOES -----
    public void addAutomacao(Automacao a) {
        this.automacoes.add(a.clone());
    }

    public void removeAutomacao(int index) {
        if(index >= 0 && index < this.automacoes.size()) {
            this.automacoes.remove(index);
        }
    }

    public void verificarAutomacoes() {
        for(Automacao a : this.automacoes) {
            if(a.avaliar(this)) {
                a.executar(this);
            }
        }
    }

    public void toggleAutomacao(int index) {
        if(index >= 0 && index < this.automacoes.size()) {
            Automacao a = this.automacoes.get(index);
            a.setAtiva(!a.isAtiva());
        }
    }

    // ----- GESTAO DE ESCALONAMENTOS -----
    public void addEscalonamento(Escalonamento e) {
        this.escalonamentos.add(e.clone());
    }

    public void removeEscalonamento(int index) {
        if(index >= 0 && index < this.escalonamentos.size()) {
            this.escalonamentos.remove(index);
        }
    }

    public void verificarEscalonamentos() {
        for(Escalonamento e : this.escalonamentos) {
            if(e.estaAtivo(this.dataHoraAtual)) {
                for (AcaoDispositivo ad : e.getAcoes()) {
                    Dispositivo d = getDispositivoById(ad.getIdDispositivo());
                    if (d != null) {
                        ad.executar(d);
                    }
                }
            }
        }
    }

    public void toggleEscalonamento(int index) {   //ligar / desligar
        if(index >= 0 && index < this.escalonamentos.size()) {
            Escalonamento e = this.escalonamentos.get(index);
            e.setAtivo(!e.isAtivo());
        }
    }

    // ------ GESTAO DE CENARIOS -----
    public void addCenario(Cenario c) {
        this.cenarios.add(c.clone());
    }

    public void removeCenario(int index) {
        if(index >= 0 && index < this.cenarios.size()) {
            this.cenarios.remove(index);
        }
    }

    public void ativarCenario(int index) {
        if(index >= 0 && index < this.cenarios.size()) {
            this.cenarios.get(index).ativar(this);
        }
    }

    // ----- METODOS DE SIMULACAO DE TEMPO -----

    public void avancarTempo(int horas, int minutos) {
        double horasAvancadas = horas + minutos / 60.0;
        acumularHoras(horasAvancadas);
        this.ultimaVerificacao = this.dataHoraAtual;
        this.dataHoraAtual = this.dataHoraAtual.plusHours(horas).plusMinutes(minutos);
        verificarAutomacoes();
        verificarEscalonamentos();
    }

    public void avancarDias(int dias) {
        double horasAvancadas = dias * 24.0;
        acumularHoras(horasAvancadas);
        this.ultimaVerificacao = this.dataHoraAtual;
        this.dataHoraAtual = this.dataHoraAtual.plusDays(dias);
        verificarAutomacoes();
        verificarEscalonamentos();
    }

    public void setDataHora(int ano, int mes, int dia, int hora, int minuto) {
        this.dataHoraAtual = LocalDateTime.of(ano, mes, dia, hora, minuto);
    }

    public String getDataHoraFormatada() {
        return this.dataHoraAtual.getDayOfMonth() + "/" +
               this.dataHoraAtual.getMonthValue() + "/" +
               this.dataHoraAtual.getYear() + " " +
               this.dataHoraAtual.getHour() + ":" +
               String.format("%02d", this.dataHoraAtual.getMinute());
    }

    // ----- METODOS ESTATISTICOS -----
    private void acumularHoras(double horas) {
        for (Casa c : this.casas.values()) {
            c.acumularHoras(horas);
        }
    }

    public Casa casaQueMaisConsome() {
        Casa mConsome = null;
        double max = -1.0;
        for(Casa c : this.casas.values()) {
            if(c.consumoTotal() >= max) {
                max = c.consumoTotal();
                mConsome = c;
            }
        }
        if(mConsome == null) return null;
        return mConsome.clone();
    }

    // usamos um array de String para fornecer informação das 3 divisoes e da respetiva casa
    public List<String[]> top3DivisoesComMaisDispositivos() {
        List<String[]> todasDivisoes = new ArrayList<>();
        for(Casa c : this.casas.values()) {
            for(Divisao d : c.getDivisoes().values()) {
                todasDivisoes.add(new String[]{
                    c.getNome(),
                    d.getNome(),
                    String.valueOf(d.numDispositivos())
                });
            }
        }

        return todasDivisoes.stream()
                            .sorted((d1,d2) -> Integer.parseInt(d2[2]) - Integer.parseInt(d1[2]))
                            .limit(3)
                            .collect(Collectors.toList());
    }

    public List<Dispositivo> top3DispositivosMaisUsadosTempo(String idCasa) throws CasaNotFoundException {
        Casa c = this.casas.get(idCasa);
        if(c == null) throw new CasaNotFoundException(idCasa);
        List<Dispositivo> disp = new ArrayList<>();
        for (Divisao d : c.getDivisoes().values()) {
            disp.addAll(d.getDispositivos());
        }

        return disp.stream()
                   .sorted((d1,d2) -> Double.compare(d2.getHorasLigado(), d1.getHorasLigado()))
                   .limit(3)
                   .map(Dispositivo::clone)
                   .collect(Collectors.toList());
    }

    public List<Dispositivo> top3DispositivosMaisUsadosAtivacoes(String idCasa) throws CasaNotFoundException {
        Casa c = this.casas.get(idCasa);
        if(c == null) throw new CasaNotFoundException(idCasa);
        List<Dispositivo> disp = new ArrayList<>();
        for (Divisao d : c.getDivisoes().values()) {
            disp.addAll(d.getDispositivos());
        }

        return disp.stream()
                   .sorted((d1,d2) -> d2.getNumAtivacoes() - d1.getNumAtivacoes())
                   .limit(3)
                   .map(Dispositivo::clone)
                   .collect(Collectors.toList());
    }

    public List<Dispositivo> dispositivosLigados(String idCasa) throws CasaNotFoundException {
        Casa c = this.casas.get(idCasa);
        if(c == null) throw new CasaNotFoundException(idCasa);
        List<Dispositivo> ligados = new ArrayList<>();
        for(Divisao d : c.getDivisoes().values()) {
            for(Dispositivo disp : d.getDispositivos()) {
                if(disp.isLigado()) {
                    ligados.add(disp.clone());
                }
            }
        }
        return ligados;
    }

    // ----- Metodos de Serializacao -----

    public void gravarEstado(String nomeFicheiro) throws IOException {
        this.contadorDispositivos = Dispositivo.getContadorId();
        this.contadorUtilizadores = Utilizador.getContadorId();
        this.contadorCasas = Casa.getContadorId();

        FileOutputStream fos = new FileOutputStream(nomeFicheiro);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this);
        oos.flush();
        oos.close();
    }

    public static DomusControl carregarEstado(String nomeFicheiro) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(nomeFicheiro);
        ObjectInputStream ois = new ObjectInputStream(fis);
        DomusControl dc = (DomusControl) ois.readObject();
        ois.close();

        Dispositivo.setContadorId(dc.contadorDispositivos);
        Utilizador.setContadorId(dc.contadorUtilizadores);
        Casa.setContadorId(dc.contadorCasas);
        return dc;
    }

    public void carregarEstadoTeste() {
            Administrador admin = new Administrador("Miguel Barrocas", "barrocas@sapo.pt", "1234");
            this.addUtilizador(admin);

            System.out.println("(1234)Admin ID: " + admin.getId());
    }      
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || o.getClass() != this.getClass()) return false;
        DomusControl dc = (DomusControl) o;
        return this.utilizadores.equals(dc.utilizadores) && 
               this.casas.equals(dc.casas);
    }

    @Override
    public DomusControl clone() {
        return new DomusControl(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" --- DomusControl ---").append("\n");
        sb.append("Data/Hora      : ").append(getDataHoraFormatada()).append("\n");
        sb.append("Utilizadores   : ").append(this.utilizadores.size()).append("\n");
        sb.append("Casas          : ").append(this.casas.size()).append("\n");
        for(Casa c : this.casas.values()) {
            sb.append(c.toString()).append("\n");
        }
        return sb.toString();
    }
}
