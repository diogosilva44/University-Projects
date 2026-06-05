package view;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

import controller.DomusControl;
import exceptions.*;
import model.Casa;
import model.Cenario;
import model.Divisao;
import model.Escalonamento;
import model.automacoes.AcaoDispositivo;
import model.automacoes.Automacao;
import model.automacoes.AutomacaoSensor;
import model.dispositivos.ColunaSom;
import model.dispositivos.Cortina;
import model.dispositivos.Dispositivo;
import model.dispositivos.Lampada;
import model.dispositivos.PortaoGaragem;
import model.dispositivos.Rele;
import model.dispositivos.Sensor;
import model.dispositivos.Televisao;
import model.utilizadores.Administrador;
import model.utilizadores.Utilizador;
import model.utilizadores.UtilizadorComum;

public class TextUI {
    private DomusControl model;
    private Scanner sc;

    public TextUI() {
        this.model = new DomusControl();
        this.sc = new Scanner(System.in);

        boolean carregado = false;
        while(!carregado) {
            System.out.println("\n ***** DomusControl ***** \n");
            System.out.println("1 - Carregar estado de ficheiro");
            System.out.println("2 - Carregar estado de teste");
            System.out.print("OPÇÃO: ");
            String escolha = sc.nextLine();

            if(escolha.equals("1")) {
                try {
                    this.model = DomusControl.carregarEstado("DomusControl/data/estado.dat");
                    System.out.println("Estado carregado com sucesso.");
                    carregado = true;
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Erro ao carregar estado.");
                }
            } else if (escolha.equals("2")) {
                model.carregarEstadoTeste();
                carregado = true;
            } else {
                System.out.println("Opção inválida.");
            }    
        }
    }

    public void run() {
        NewMenu menu = new NewMenu(new String[] {
            "Login",
        });

        menu.setHandler(1, () -> login());
       
        menu.run();
    }

    // inicio -> login
    private void login() {
        System.out.print("ID: ");
        String id = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();

        try {
            if(!model.autenticar(id, password)) {
                System.out.println("Password incorreta !!!");
                return;
            }

            Utilizador u = model.getUtilizador(id);
            System.out.println("Bem-vind@, " + u.getNome());

            if(u.isAdministrador()) {
                menuAdministrador(u);
            } else {
                menuUtilizadorComum(u);
            }
        } catch (UtilizadorNotFoundException e) {
            System.out.println("ERRO: User com id '" + e.getMessage() + "' não encontrado");
        }
    }

    // inicio -> login -> admin
    private void menuAdministrador(Utilizador u) {
        NewMenu menu = new NewMenu(new String[] {
            "Gestão de Utilizadores",
            "Gestão de Casas",
            "Gestão de Dispositivos",
            "Estatisticas",
            "Cenários",
            "Automações",
            "Escalonamentos",
            "Avançar Tempo",
            "Guardar Estado",
        });

        menu.setHandler(1, () -> gestaoUtilizadores());
        menu.setHandler(2, () -> gestaoCasas(u));
        menu.setHandler(3, () -> gestaoDispositivos(u));
        menu.setHandler(4, () -> estatisticas());
        menu.setHandler(5, () -> cenarios(u));
        menu.setHandler(6, () -> automacoes());
        menu.setHandler(7, () -> escalonamentos());
        menu.setHandler(8, () -> avancarTempo());
        menu.setHandler(9, () -> gravarEstado());

        menu.run();
    }

    // inicio -> login -> userComum
    private void menuUtilizadorComum(Utilizador u) {
        NewMenu menu = new NewMenu(new String[] {
            "Ver as minhas casas",
            "Operar dispositivos",
            "Cenários",
            "Automações",
            "Escalonamentos",
            "Avançar Tempo",
            "Guardar Estado"
        });

        menu.setHandler(1, () -> verCasas(u));
        menu.setHandler(2, () -> operarDispositivos(u));
        menu.setHandler(3, () -> cenarios(u));
        menu.setHandler(4, () -> automacoes());
        menu.setHandler(5, () -> escalonamentos());
        menu.setHandler(6, () -> avancarTempo());
        menu.setHandler(7, () -> gravarEstado());

        menu.run();
    }

    // inicio -> login -> admin -> gestaoUsers
    private void gestaoUtilizadores() {
        NewMenu menu = new NewMenu(new String[] {
            "Adicionar Utilizador",
            "Remover Utilizador",
            "Listar Utilizadores",
        });

        menu.setHandler(1, () -> addUtilizador());
        menu.setHandler(2, () -> removeUtilizador());
        menu.setHandler(3, () -> listaUtilizadores());

        menu.run();
    }

    // inicio -> login -> admin -> gestaoUsers -> addUser
    private void addUtilizador() {
        System.out.print("Nome: ");
        String nome = sc.nextLine();
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();
        System.out.print("Tipo Adminstrador/User Comum (1/2): ");
        String tipo = sc.nextLine();

        if(tipo.equals("1")) {
            String id = model.criarAdministrador(nome, email, password);
            System.out.println("Admin '" + id + "' adicionado com sucesso");
        } else if(tipo.equals("2")) {
            String id = model.criarUtilizadorComum(nome, email, password);
            System.out.println("Utilizador '" + id + "' adicionado com sucesso");
        } else {
            System.out.println("Tipo inválido.");
        }
    }

    // inicio -> login -> admin -> gestaoUsers -> removeUser
    private void removeUtilizador() {
        System.out.print("ID: ");
        String id = sc.nextLine();

        System.out.print("TEM A CERTEZA? (s/n): ");
        String confirmacao = sc.nextLine();
        if (!confirmacao.equals("s")) {
            System.out.println("Operação cancelada.");
            return;
        }
        try {
            model.removeUtilizador(id);
            System.out.println("Utilizador removido!");
        } catch (UtilizadorNotFoundException e) {
            System.out.println("ERRO: User com id '" + e.getMessage() + "' não encontrado");
        }
    }

    // inicio -> login -> admin -> gestaoUsers -> listaUsers
    private void listaUtilizadores() {
        Map<String, Utilizador> utilizadores = model.getUtilizadores();
        if(utilizadores.isEmpty()) {
            System.out.println("Não existem utilizadores");
            return;
        }
        for(Utilizador u : utilizadores.values()) {
            System.out.println(u.toString());
            System.out.println("---------------------");
        }
    }

    // inicio -> login -> admin -> gestaoCasas
    private void gestaoCasas(Utilizador u) {
        NewMenu menu = new NewMenu(new String[] {
            "Adicionar Casa",
            "Remover Casa",
            "Listar Casas",
            "Ver Casa",
            "Adicionar Divisão",
            "Remover Divisão",
            "Adicionar utilizador a Casa"
        });

        menu.setHandler(1, () -> addCasa(u));
        menu.setHandler(2, () -> removeCasa());
        menu.setHandler(3, () -> listarCasas());
        menu.setHandler(4, () -> verCasa());
        menu.setHandler(5, () -> addDivisao());
        menu.setHandler(6, () -> removeDivisao());
        menu.setHandler(7, () -> addUtilizadorCasa());
        
        menu.run();
    }

    // inicio -> login -> admin -> gestaoCasas -> addCasa
    private void addCasa(Utilizador u) {
        System.out.print("Nome da casa: ");
        String nome = sc.nextLine();
        System.out.print("Morada: ");
        String morada = sc.nextLine();

        String idCasa = model.criarCasa(nome, morada, u.getId());

        System.out.println("Casa criada com sucesso");
        System.out.println("ID da casa adicionada: " + idCasa);
    }

    // inicio -> login -> admin -> gestaoCasas -> removeCasa
    private void removeCasa() {
        System.out.print("ID da casa a remover: ");
        String id = sc.nextLine();

        System.out.print("TEM A CERTEZA? (s/n): ");
        String confirmação = sc.nextLine();
        if(!confirmação.equals("s")) {
            System.out.println("Operação Cancelada");
            return;
        }
        try {
            model.removeCasa(id);
            System.out.println("Casa removida com sucesso");
        } catch (CasaNotFoundException e) {
            System.out.println("ERRO: Casa com id '" + e.getMessage() + "' não encontrada");
        }
    }

    // inicio -> login -> admin -> gestaoCasas -> listarCasas
    public void listarCasas() {
        Map<String, Casa> casas = model.getCasas();
        if(casas.isEmpty()) {
            System.out.println("Não existem casas");
            return;
        }
        for(Casa c : casas.values()) {
            System.out.println("ID       : " + c.getId());
            System.out.println("Nome     : " + c.getNome());
            System.out.println("Morada   : " + c.getMorada());
            System.out.println("Divisões : " + c.numDivisoes());
            System.out.println(" ------------------------ ");
        }
    }

    // inicio -> login -> admin -> gestaoCasas -> verCasa
    private void verCasa() {
        System.out.print("ID da casa: ");
        String id = sc.nextLine();

        try {
            Casa c = model.getCasa(id);
            System.out.println(c.toString());
        } catch(CasaNotFoundException e) {
            System.out.println("ERRO: Casa com id '" + e.getMessage() + "' não encontrada");
        }
    }

    // inicio -> login -> admin -> gestaoCasas -> addDivisao
    private void addDivisao() {
        System.out.print("ID da casa: ");
        String idCasa = sc.nextLine();
        System.out.print("Nome da divisão: ");
        String nomeDivisao = sc.nextLine();

        try {
            model.addDivisaoCasa(idCasa, new Divisao(nomeDivisao));
            System.out.println("Divisao adicionada com sucesso.");
        } catch (CasaNotFoundException e) {
            System.out.println("ERRO: Casa com id '" + e.getMessage() + "' não encontrada");
        } catch (DivisaoAlreadyExistsException e) {
            System.out.println("ERRO: Divisão '" + e.getMessage() + "' não encontrada");
        }
    }

    // inicio -> login -> admin -> gestaoCasas -> removeDivisao
    private void removeDivisao() {
        System.out.print("ID da casa: ");
        String idCasa = sc.nextLine();
        System.out.print("Nome da divisão: ");
        String nomeDivisao = sc.nextLine();

        System.out.print("TEM A CERTEZA? (s/n): ");
        String confirmacao = sc.nextLine();
        if (!confirmacao.equals("s")) {
            System.out.println("Operação cancelada.");
            return;
        }
        
        try {
            model.removeDivisaoCasa(idCasa, nomeDivisao);
            System.out.println("Divisão removida com sucesso");
        } catch (CasaNotFoundException e) {
            System.out.println("ERRO: Casa com id '" + e.getMessage() + "' não encontrada");
        } catch (DivisaoNotFoundException e) {
            System.out.println("ERRO: Divisão '" + e.getMessage() + "' não encontrada");
        }
    }

    // inicio -> login -> admin -> gestaoCasas -> addUtilizadorCasa
    private void addUtilizadorCasa() {
        System.out.print("ID do utilizador: ");
        String idUtilizador = sc.nextLine();
        System.out.print("ID da casa: ");
        String idCasa = sc.nextLine();

        if(!model.existeUtilizador(idUtilizador)) {
            System.out.println("ERRO: Utilizador não encontrado.");
            return;
        }
        if(!model.existeCasa(idCasa)) {
            System.out.println("ERRO: Casa não encontrada.");
            return;
        }

        model.addCasaUtilizador(idUtilizador, idCasa);
        System.out.println("Utilizador adicionado á casa com sucesso.");
    }

    // inicio -> login -> admin -> gestaoDispositivos
    private void gestaoDispositivos(Utilizador u) {
        NewMenu menu = new NewMenu(new String[] {
            "Adicionar Dispositivo",
            "Remover Dispositivo",
            "Ligar Dispositivo",
            "Desligar Dispositivo",
            "Listar Dispositivos",
            "Regular Dispositivo",
        });

        menu.setHandler(1,() -> addDispositivo(u));
        menu.setHandler(2,() -> removeDispositivo(u));
        menu.setHandler(3,() -> ligarDispositivo(u));
        menu.setHandler(4,() -> desligarDispositivo(u));
        menu.setHandler(5,() -> listarDispositivos(u));
        menu.setHandler(6, () -> regularDispositivo(u));
      
        menu.run();
    }

    // inicio -> login -> admin -> gestaoDispositivos -> addDispositivo
    private void addDispositivo(Utilizador u) {
        System.out.print("ID da casa: ");
        String idCasa = sc.nextLine();
        if(!u.isAdministrador() && !model.utilizadorTemAcessoCasa(u.getId(), idCasa)) {
            System.out.println("ERRO: Não tem permissão para aceder a esta casa.");
            return;
        }
        System.out.print("Nome da divisão: ");
        String nomeDivisao = sc.nextLine();
        try {
            Casa c = model.getCasa(idCasa);
            Divisao d = c.getDivisao(nomeDivisao);

            if(d == null) {
                System.out.println("ERRO: Divisão não encontrada");
                return;
            }
        } catch (CasaNotFoundException e) {
            System.out.println("ERRO: Casa com id '"+ idCasa + "' não encontrada");
            return;
        }

        System.out.println("Tipo de dispositivo:");
        System.out.println("1 - Lampada");
        System.out.println("2 - Coluna de Som");
        System.out.println("3 - Cortina");
        System.out.println("4 - Portão de Garagem");
        System.out.println("5 - Sensor");
        System.out.println("6 - Rele");
        System.out.println("7 - Televisão");
        System.out.print("Opção: ");
        String tipo = sc.nextLine();

        System.out.print("Marca: ");
        String marca = sc.nextLine();
        System.out.print("Modelo: ");
        String modelo = sc.nextLine();
        System.out.print("Consumo (Wh): ");
        double consumo = Double.parseDouble(sc.nextLine());

        try {
            Dispositivo d = criarDispositivo(tipo,marca,modelo,consumo);
            if(d == null) {
                System.out.println("Tipo inválido.");
                return;
            }
            model.addDispositivoCasa(idCasa, nomeDivisao, d);
            System.out.println("Dispositivo adicionado com sucesso ! ID atribuido: " + d.getId());
        } catch (CasaNotFoundException e) {
            System.out.println("ERRO: Casa com id '" + e.getMessage() + "' não encontrada");
        } catch (DivisaoNotFoundException e) {
            System.out.println("ERRO: Divisão '" + e.getMessage() + "' não encontrada");
        }
    }

    // inicio -> login -> admin -> gestaoDispositivos -> criarDispositivo
    private Dispositivo criarDispositivo(String tipo, String marca, String modelo, double consumo) {
        switch (tipo) {
            case "1":
                System.out.print("Intensidade (0-100): ");
                int intensidade = Integer.parseInt(sc.nextLine());
                System.out.print("Cor em Kelvin (2700-4000): ");
                int cor = Integer.parseInt(sc.nextLine());
                return model.criarLampada(marca, modelo, consumo, intensidade, cor);                                       
            case "2":
                System.out.print("Volume (0-100): ");
                int volume = Integer.parseInt(sc.nextLine());
                System.out.print("Fonte (ex:Bluetooth): ");
                String fonte = sc.nextLine();
                return model.criarColunaSom(marca,modelo,consumo,volume,fonte);
            case "3":
                return model.criarCortina(marca,modelo,consumo,0);
            case "4":
                return model.criarPortaoGaragem(marca,modelo,consumo,0);
            case "5":
                System.out.print("Tipo de sensor (ex:Temperatura): ");
                String tipoSensor = sc.nextLine();
                System.out.print("Unidade (ex: ºC): ");
                String unidade = sc.nextLine();
                return model.criarSensor(marca,modelo,consumo,tipoSensor,unidade);
            case "6":
                return model.criarRele(marca,modelo,consumo);
            case "7":
                System.out.print("Canal inicial: ");
                int canal = Integer.parseInt(sc.nextLine());
                System.out.print("Volume inicial (0-100): ");
                int volumeTV = Integer.parseInt(sc.nextLine());
                return model.criarTelevisao(marca,modelo,consumo,canal,volumeTV);
            default:
                return null;
        }   
    }

    // inicio -> login -> admin -> gestaoDispositivos -> removeDispositivo
    private void removeDispositivo(Utilizador u) {
        System.out.print("ID da casa: ");
        String idCasa = sc.nextLine();

        if(!u.isAdministrador() && !model.utilizadorTemAcessoCasa(u.getId(), idCasa)) {
            System.out.println("ERRO: Não tem permissão para aceder a esta casa.");
            return;
        }

        System.out.print("Nome da divisão: ");
        String nomeDivisao = sc.nextLine();
        System.out.print("ID do dispositivo: ");
        int idDispositivo = Integer.parseInt(sc.nextLine());

        System.out.print("TEM A CERTEZA? (s/n): ");
        String confirmação = sc.nextLine();
        if(!confirmação.equals("s")) {
            System.out.println("Operação cancelada.");
            return;
        }

        try {
            model.removeDispositivoCasa(idCasa, nomeDivisao, idDispositivo);
            System.out.println("Dispositivo removido com sucesso.");
        } catch (CasaNotFoundException e) {
            System.out.println("ERRO: Casa com id '" + e.getMessage() + "' não encontrada");
        } catch (DivisaoNotFoundException e) {
            System.out.println("ERRO: Divisão '" + e.getMessage() + "' não encontrada");
        } catch (DispositivoNotFoundException e) {
            System.out.println("ERRO: Dispositivo com id '" + e.getMessage() + "' não encontrado");
        }
    }

    // inicio -> login -> admin -> gestaoDispositivos -> ligarDispositivo
    private void ligarDispositivo(Utilizador u) {
        System.out.print("ID da casa: ");
        String idCasa = sc.nextLine();

        if(!u.isAdministrador() && !model.utilizadorTemAcessoCasa(u.getId(), idCasa)) {
            System.out.println("ERRO: Não tem permissão para aceder a esta casa.");
            return;
        }

        System.out.print("Nome da divisão: ");
        String nomeDivisao = sc.nextLine();
        System.out.print("ID do dispositivo: ");
        int idDispositivo = Integer.parseInt(sc.nextLine());

        try {
            model.ligarDispositivo(idCasa, nomeDivisao, idDispositivo);
            System.out.println("Dispositivo ligado com sucesso.");
        } catch (CasaNotFoundException e) {
            System.out.println("ERRO: Casa com id '" + e.getMessage() + "' não encontrada");
        } catch (DivisaoNotFoundException e) {
            System.out.println("ERRO: Divisão '" + e.getMessage() + "' não encontrada");
        } catch (DispositivoNotFoundException e) {
            System.out.println("ERRO: Dispositivo com id '" + e.getMessage() + "' não encontrado");
        }
    }

    // inicio -> login -> admin -> gestaoDispositivos -> desligarDispositivo
    private void desligarDispositivo(Utilizador u) {
        System.out.print("ID da casa: ");
        String idCasa = sc.nextLine();

        if(!u.isAdministrador() && !model.utilizadorTemAcessoCasa(u.getId(), idCasa)) {
            System.out.println("ERRO: Não tem permissão para aceder a esta casa.");
            return;
        }

        System.out.print("Nome da divisão: ");
        String nomeDivisao = sc.nextLine();
        System.out.print("ID do dispositivo: ");
        int idDispositivo = Integer.parseInt(sc.nextLine());

        try {
            model.desligarDispositivo(idCasa, nomeDivisao, idDispositivo);
            System.out.println("Dispositivo desligado com sucesso.");
        } catch (CasaNotFoundException e) {
            System.out.println("ERRO: Casa com id '" + e.getMessage() + "' não encontrada");
        } catch (DivisaoNotFoundException e) {
            System.out.println("ERRO: Divisão '" + e.getMessage() + "' não encontrada");
        } catch (DispositivoNotFoundException e) {
            System.out.println("ERRO: Dispositivo com id '" + e.getMessage() + "' não encontrado");
        }
    }

    // inicio -> login -> admin -> gestaoDispositivos -> listarDispositivo
    private void listarDispositivos(Utilizador u) {
        System.out.print("ID da casa: ");
        String idCasa = sc.nextLine();

        if(!u.isAdministrador() && !model.utilizadorTemAcessoCasa(u.getId(), idCasa)) {
            System.out.println("ERRO: Não tem permissão para aceder a esta casa.");
            return;
        }

        System.out.print("Nome da divisão: ");
        String nomeDivisao = sc.nextLine();

        try {
            Casa c = model.getCasa(idCasa);
            Divisao d = c.getDivisao(nomeDivisao);
            if(d == null) {
                System.out.println("ERRO: Divisão não encontrada");
                return;
            }
            if(d.numDispositivos() == 0) {
                System.out.println("ERRO: Não existem dispositivos nesta divisão");
                return;
            }
            for (Dispositivo disp : d.getDispositivos()) {
                System.out.println(disp.toString());
                System.out.println(" ----------------- ");
            }
        } catch (CasaNotFoundException e) {
            System.out.println("ERRO: Casa com id '" + e.getMessage() + "' não encontrada");
        }
    }

    // inicio -> login -> admin -> gestaoDispositivos -> regularDispositivo
    private void regularDispositivo(Utilizador u) {
        System.out.print("ID da casa: ");
        String idCasa = sc.nextLine();
        if(!u.isAdministrador() && !model.utilizadorTemAcessoCasa(u.getId(), idCasa)) {
            System.out.println("ERRO: Não tem permissão para aceder a esta casa.");
            return;
        }

        System.out.print("Nome da divisão: ");
        String nomeDivisao = sc.nextLine();
        try {
            Casa c = model.getCasa(idCasa);
            Divisao d = c.getDivisao(nomeDivisao);

            if(d == null) {
                System.out.println("ERRO: Divisão não encontrada.");
                return;
            }
            if(d.numDispositivos() == 0) {
                System.out.println("ERRO: Não existem dispositivos nesta divisão.");
                return;
            }
            System.out.println("\n--- Dispositivos disponiveis ---");
            for(Dispositivo disp : d.getDispositivos()) {
                System.out.println(disp.toString());
                System.out.println(" ------------ ");
            }
        } catch (CasaNotFoundException e) {
            System.out.println("ERRO: Casa com id '" + e.getMessage() + "' não encontrada");
        }
        
        System.out.print("ID do dispositivo: ");
        int id = Integer.parseInt(sc.nextLine());

        String tipo = model.getTipoDispositivo(id);
        if(tipo == null) {
            System.out.println("ERRO: Dispositivo não encontrado.");
            return;
        }

        switch(tipo) {
            case "Lampada":
                regularLampada(id);
                break;
            case "ColunaSom":
                regularColuna(id);
                break;
            case "Cortina":
                regularCortina(id);
                break;
            case "PortaoGaragem":
                regularPortao(id);
                break;
            case "Sensor":
                regularSensor(id);
                break;
            case "Televisao":
                regularTelevisao(id);
                break;
            case "Rele":
                System.out.println("Rele não tem parâmetros reguláveis.");
                break;
            default:
                System.out.println("Tipo de dispositivo desconhecido.");
        }
    }

    // metodos para regular cada dispositivo
    private void regularLampada(int id) {
        System.out.println("\n- Lampada -");
        System.out.println("1 - Alterar intensidade");
        System.out.println("2 - Alterar cor (Kelvin)");
        System.out.print("Opção: ");
        String opcao = sc.nextLine();
        try {
            if(opcao.equals("1")) {
                System.out.print("Nova intensidade (0-100): ");
                int intensidade = Integer.parseInt(sc.nextLine());
                model.setIntensidadeLampada(id, intensidade);
                System.out.println("Intensidade alterada.");
            } else if(opcao.equals("2")) {
                System.out.print("Nova cor (2700-4000): ");
                int cor = Integer.parseInt(sc.nextLine());
                model.setCorLampada(id, cor);;
                System.out.println("Cor alterada.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido.");
        }
    }

    private void regularColuna(int id) {
        System.out.println("\n- Coluna de som -");
        System.out.println("1 - Alterar volume");
        System.out.println("2 - Alterar fonte");
        System.out.print("Opção: ");
        String opcao = sc.nextLine();
        try {
            if(opcao.equals("1")) {
                System.out.print("Novo volume (0-100): ");
                int volume = Integer.parseInt(sc.nextLine());
                model.setVolumeColuna(id, volume);;
                System.out.println("Volume alterado.");
            } else if(opcao.equals("2")) {
                System.out.print("Nova fonte: ");
                String fonte = sc.nextLine();
                model.setFonteColuna(id, fonte);;
                System.out.println("Fonte alterada.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido");
        }   
    }

    private void regularCortina(int id) {
        System.out.println("\n- Cortina -");
        System.out.print("Nova abertura (0-100): ");
        try {
            int abertura = Integer.parseInt(sc.nextLine());
            model.setAberturaCortina(id, abertura);;
            System.out.println("Abertura alterada.");
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido");
        }
    }

    private void regularPortao(int id) {
        System.out.println("\n- Portão -");
        System.out.print("Nova abertura (0-100): ");
        try {
            int abertura = Integer.parseInt(sc.nextLine());
            model.setAberturaPortao(id, abertura);;
            System.out.println("Abertura alterada.");
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido.");
        }
    }

    private void regularSensor(int id) {
        System.out.println("\n- Sensor -");
        System.out.print("Novo valor: ");
        try {
            double valor = Double.parseDouble(sc.nextLine());
            model.setValorSensor(id, valor);
            System.out.println("Valor atualizado.");
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido.");
        }
    }

    private void regularTelevisao(int id) {
        System.out.println("\n- Televisão -");
        System.out.println("1 - Mudar canal");
        System.out.println("2 - Alterar volume");
        System.out.print("Opção: ");
        String opcao = sc.nextLine();
        try {
            if(opcao.equals("1")) {
                System.out.print("Novo canal: ");
                int canal = Integer.parseInt(sc.nextLine());
                model.setCanalTelevisao(id, canal);
                System.out.println("Canal alterado.");
            } else if (opcao.equals("2")) {
                System.out.print("Novo volume (0-100): ");
                int volume = Integer.parseInt(sc.nextLine());
                model.setVolumeTelevisao(id, volume);
                System.out.println("Volume alterado.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido.");
        }
    }

    // inicio -> login -> admin -> estatisticas
    private void estatisticas() {
        NewMenu menu = new NewMenu(new String[] {
            "Casa que mais consome",
            "Top 3 dispositivos",
            "Top 3 divisões com mais dispositivos",
            "Dispositivos ligados na Casa"
        });

        menu.setHandler(1, () -> casaQueMaisConsome());
        menu.setHandler(2, () -> top3Dispositivos());
        menu.setHandler(3, () -> top3Divisoes());
        menu.setHandler(4, () -> dispositivosLigados());

        menu.run();
    }

    // inicio -> login -> admin -> estatisticas -> casaQueMaisConsome
    private void casaQueMaisConsome() {
        Casa c = model.casaQueMaisConsome();
        if(c == null) {
            System.out.println("Não existem casas");
            return;
        }
        System.out.println("Casa que mais consome: ");
        System.out.println("ID     : " + c.getId());
        System.out.println("Nome   : " + c.getNome());
        System.out.println("Consumo: " + String.format("%.2f", c.consumoTotal()) + "W");
    }

    // inicio -> login -> admin -> estatisticas -> top3DispositivosMaisUsados
    private void top3Dispositivos() {
        System.out.print("ID da casa: ");
        String idCasa = sc.nextLine();

        try {
            System.out.println("--- Top 3 por tempo ligado ---\n");
            List<Dispositivo> top3Tempo = model.top3DispositivosMaisUsadosTempo(idCasa);
            if (top3Tempo.isEmpty()) {
                System.out.println("Não existem dispositivos.");
            } else {
                for (Dispositivo d : top3Tempo) {
                    System.out.println(d.getClass().getSimpleName() + "," + d.getMarca() +
                        " | ID: " + d.getId() +  
                        " | Horas: " + String.format("%.2f", d.getHorasLigado()));
                    System.out.println("\n");
                }
            }

            System.out.println("--- Top 3 por numero de ativações ---\n");
            List<Dispositivo> top3Act = model.top3DispositivosMaisUsadosAtivacoes(idCasa);
            if (top3Act.isEmpty()) {
                System.out.println("Não existem dispositivos.");
            } else {
                for (Dispositivo d : top3Act) {
                    System.out.println(d.getClass().getSimpleName() + "," + d.getMarca() +
                        " | ID: " + d.getId() +  
                        " | Ativações: " + d.getNumAtivacoes());
                    System.out.println("\n");
                }
            }
        } catch (CasaNotFoundException e) {
            System.out.println("ERRO: Casa com id '" + e.getMessage() + "' não encontrada");
        }
    }

    // inicio -> login -> admin -> estatisticas -> top3DivisoesComMaisDisp
    private void top3Divisoes() {
        List<String[]> top3 = model.top3DivisoesComMaisDispositivos();
        if(top3.isEmpty()) {
            System.out.println("Não existem divisões.");
            return;
        }
        System.out.println("Top 3 divisões com mais dispositivos:");
        for(String[] d : top3) {
            System.out.println("Casa         : " + d[0]);
            System.out.println("Divisão      : " + d[1]);
            System.out.println("Dispositivos : " + d[2]);
            System.out.println(" ----------------- ");
        }
    }

    // inicio -> login -> admin -> estatisticas -> dispositivosLigados
    private void dispositivosLigados() {
        System.out.print("ID da casa: ");
        String idCasa = sc.nextLine();
        try {
            List<Dispositivo> ligados = model.dispositivosLigados(idCasa);
            if(ligados.isEmpty()) {
                System.out.println("Nenhum dispositivo ligado.");
                return;
            }
            System.out.println("Dispositivos ligados:\n");
            for(Dispositivo d : ligados) {
                System.out.println(d.getClass().getSimpleName() +
                    " | ID: " + d.getId() + 
                    " | " + d.getMarca());
                System.out.println("------------------- ");
            }
        } catch (CasaNotFoundException e) {
            System.out.println("ERRO: Casa com id '" + e.getMessage() + "' não encontrada");
        }
    }

    // inicio -> login -> menuUtilizadorComum -> verCasas
    private void verCasas(Utilizador u) {
        Set<String> idsCasas = u.getCasas();
        if(idsCasas.isEmpty()) {
            System.out.println("Não tens casas associadas.");
            return;
        }
        for(String idCasa : idsCasas) {
            try {
                Casa c = model.getCasa(idCasa);
                System.out.println("ID       : " + c.getId());
                System.out.println("Nome     : " + c.getNome());
                System.out.println("Morada   : " + c.getMorada());
                System.out.println("Divisoes : " + c.numDivisoes());
                System.out.println(" -------- ");
            } catch (CasaNotFoundException e) {
                System.out.println("ERRO: Casa com id '" + e.getMessage() + "' não encontrada");
            }
        }
    }

    // inicio -> login -> menuUtilizadorComum -> operarDispositivos
    private void operarDispositivos(Utilizador u) {
        NewMenu menu = new NewMenu(new String[] {
            "Ligar Dispositivo",
            "Desligar Dispositivo",
            "Regular Dispositivo",
            "Listar Dispositivos"
        });

        menu.setHandler(1,() -> ligarDispositivo(u));
        menu.setHandler(2, () -> desligarDispositivo(u));
        menu.setHandler(3, () -> regularDispositivo(u));
        menu.setHandler(4, () -> listarDispositivos(u));

        menu.run();
    }

    // inicio -> login -> admin/UserComum -> cenarios
    private void cenarios(Utilizador u) {
        NewMenu menu = new NewMenu(new String[] {
            "Listar Cenários",
            "Ativar Cenário",
            "Criar Cenário",
            "Remover Cenário"
        });

        menu.setHandler(1, () -> listarCenarios());
        menu.setHandler(2, () -> ativarCenario());
        menu.setHandler(3, () -> criarCenario());
        menu.setHandler(4, () -> removerCenario());

        menu.setPreCondition(4, () -> u.isAdministrador());

        menu.run();
    }

    // inicio -> login -> admin/UserComum -> cenarios -> listarCenarios
    private void listarCenarios() {
        List<Cenario> cenarios = model.getCenarios();
        if(cenarios.isEmpty()) {
            System.out.println("Não existem cenários.");
            return;
        }
        for(int i = 0; i < cenarios.size(); i++) {
            System.out.println("[" + i + "] " + cenarios.get(i).toString());
            System.out.println(" -------------- ");
        }
    }

    // inicio -> login -> admin/UserComum -> cenarios -> ativarCenario
    private void ativarCenario() {
        List<Cenario> cenarios = model.getCenarios();
        if(cenarios.isEmpty()) {
            System.out.println("Não existem cenários.");
            return;
        }

        listarCenarios();  //para o user ver o indice do cenario que quer ativar

        System.out.print("Indice do cenário a ativar: ");
        int index = Integer.parseInt(sc.nextLine());
        if(index < 0 || index >= cenarios.size()) {
            System.out.println("Indice inválido.");
            return;
        }
        model.ativarCenario(index);
        System.out.println("Cenário '" + cenarios.get(index).getNome() + "' ativado com sucesso.");
    }

    // inicio -> login -> admin/UserComum -> cenarios -> criarCenario
    private void criarCenario() {
        verCasa();

        System.out.print("Nome do Cenário: ");
        String nome = sc.nextLine();
        System.out.print("Descrição: ");
        String descricao = sc.nextLine();

        Cenario cenario = new Cenario(nome,descricao);

        String resposta = "s";
        while(resposta.equals("s")) {
            System.out.print("ID do dispositivo: ");
            int idDisp = Integer.parseInt(sc.nextLine());
            System.out.println("Ação: ");
            System.out.println("1 - Ligar");
            System.out.println("2 - Desligar");
            System.out.print("OPÇÃO: ");
            String acao = sc.nextLine();

            if(acao.equals("1")) {
                cenario.addAcao(new AcaoDispositivo(idDisp, "Ligar", d -> d.ligar()));
            } else {
                cenario.addAcao(new AcaoDispositivo(idDisp, "Desligar", d -> d.desligar()));
            }

            System.out.print("Adicionar mais alguma ação? (s/n): ");
            resposta = sc.nextLine();
        }
        model.addCenario(cenario);
        System.out.println("Cenário '" + nome + "' criado com sucesso");
    }

    // inicio -> login -> admin -> cenarios -> removerCenario
    private void removerCenario() {
        List<Cenario> cenarios = model.getCenarios();
        if(cenarios.isEmpty()) {
            System.out.println("Não existem cenários.");
            return;
        }

        listarCenarios();

        System.out.print("Indice do cenário a remover: ");
        int index = Integer.parseInt(sc.nextLine());

        if(index < 0 || index >= cenarios.size()) {
            System.out.println("Indice inválido.");
            return;
        }

        System.out.print("TEM A CERTEZA? (s/n): ");
        String confirmacao = sc.nextLine();
        if(!confirmacao.equals("s")) {
            System.out.println("Operação Cancelada.");
            return;
        }
        model.removeCenario(index);
        System.out.println("Cenário removido com sucesso.");
    }

    // inicio -> login -> admin/UserComum -> automacoes
    private void automacoes() {
        NewMenu menu = new NewMenu(new String[] {
            "Listar Automações",
            "Criar Automação Sensor",
            "Ativar/Desativar Automação",
            "Remover Automação"
        });

        menu.setHandler(1, () -> listarAutomacoes());
        menu.setHandler(2, () -> criarAutomacaoSensor());
        menu.setHandler(3, () -> ativarDesativarAutomacao());
        menu.setHandler(4, () -> removerAutomacao());

        menu.run();
    }

    // inicio -> login -> admin/UserComum -> automacoes -> Listar Automações
    private void listarAutomacoes() {
        List<Automacao> automacoes = model.getAutomacoes();
        if(automacoes.isEmpty()) {
            System.out.println("Não existem automações.");
            return;
        }
        for(int i = 0; i< automacoes.size(); i++) {
            System.out.println("[" + i + "] " + automacoes.get(i).toString());
            System.out.println(" ---------------- ");
        }
    }

    // inicio -> login -> admin/UserComum -> automacoes -> criar AutomacaoSensor
    private void criarAutomacaoSensor() {
        System.out.print("Nome da automação: ");
        String nome = sc.nextLine();
        System.out.print("ID do sensor: ");
        int idSensor = Integer.parseInt(sc.nextLine());
        System.out.println("Operador:");
        System.out.println("1 - maior que (>)");
        System.out.println("2 - menor que (<)");
        System.out.println("3 - igual a (==)");
        System.out.print("OPÇÃO: ");
        String opcao = sc.nextLine();

        String operador;
        switch(opcao) {
            case "1" : operador = ">"; break;
            case "2" : operador = "<"; break;
            case "3" : operador = "=="; break;
            default: 
                System.out.println("Operador inválido.");
                return;
        }

        System.out.print("Valor Limite: ");
        double valorLimite = Double.parseDouble(sc.nextLine());

        AutomacaoSensor automacao = new AutomacaoSensor(nome, idSensor, operador,valorLimite);

        String resposta = "s";
        while (resposta.equals("s")) {
            System.out.print("ID do dispositivo: ");
            int idDisp = Integer.parseInt(sc.nextLine());
            System.out.println("Ação:");
            System.out.println("1 - Ligar");
            System.out.println("2 - Desligar");
            System.out.print("OPÇÃO: ");
            String acao = sc.nextLine();

            if(acao.equals("1")) {
                automacao.addAcao(new AcaoDispositivo(idDisp, "Ligar", d -> d.ligar()));
            } else {
                automacao.addAcao(new AcaoDispositivo(idDisp, "Desligar", d -> d.desligar()));
            }
            System.out.print("Adicionar mais alguma ação? (s/n): ");
            resposta = sc.nextLine();
        }

        model.addAutomacao(automacao);
        System.out.println("Automação '" + nome + "' criada com sucesso.");
    }

    // inicio -> login -> admin/UserComum -> automacoes -> ativar/desativar automação
    private void ativarDesativarAutomacao() {
        List<Automacao> automacoes = model.getAutomacoes();
        if(automacoes.isEmpty()) {
            System.out.println("Não existem automações.");
            return;
        }

        listarAutomacoes();

        System.out.print("Indice da automação: ");
        int index = Integer.parseInt(sc.nextLine());

        if(index < 0 || index >= automacoes.size()) {
            System.out.println("Indice inválido.");
            return;
        }

        model.toggleAutomacao(index);
        System.out.println("Estado da automação alterado com sucesso");
    }

    // inicio -> login -> admin/UserComum -> automacoes -> removerAutomacao
    private void removerAutomacao() {
        List<Automacao> automacoes = model.getAutomacoes();
        if(automacoes.isEmpty()) {
            System.out.println("Não existem automações.");
            return;
        }

        listarAutomacoes();

        System.out.print("Indice da automação a remover: ");
        int index = Integer.parseInt(sc.nextLine());

        if(index < 0 || index >= automacoes.size()) {
            System.out.println("Indice inválido");
            return;
        }

        System.out.print("TEM A CERTEZA QUE QUER REMOVER? (s/n): ");
        String confirmacao = sc.nextLine();
        if(!confirmacao.equals("s")) {
            System.out.println("Operação cancelada.");
            return;
        }

        model.removeAutomacao(index);
        System.out.println("Automação removida com sucesso.");
    }

    // inicio -> login -> admin/UserComum -> escalonamentos
    private void escalonamentos() {
        NewMenu menu = new NewMenu(new String[] {
            "Listar Escalonamentos",
            "Criar Escalonamento",
            "Ativar/Desativar Escalonamento",
            "Remover Escalonamento"
        });

        menu.setHandler(1, () -> listarEscalonamentos());
        menu.setHandler(2, () -> criarEscalonamento());
        menu.setHandler(3, () -> ativarDesativarEscalonamento());
        menu.setHandler(4, () -> removerEscalonamento());

        menu.run();
    }

    // inicio -> login -> admin/UserComum -> escalonamentos -> listar Escalonamentos
    private void listarEscalonamentos() {
        List<Escalonamento> escalonamentos = model.getEscalonamentos();
        if(escalonamentos.isEmpty()) {
            System.out.println("Não existem escalonamentos.");
            return;
        }
        for(int i = 0; i < escalonamentos.size(); i++) {
            System.out.println("[" + i + "] " + escalonamentos.get(i).toString());
            System.out.println(" ------------- ");
        }
    }

    // inicio -> login -> admin/UserComum -> escalonamentos -> criarEscalonamento
    private void criarEscalonamento() {
        verCasa();

        System.out.print("Nome do escalonamento: ");
        String nome = sc.nextLine();

        int horaInicio,minutoInicio,horaFim,minutoFim;
        try {
            System.out.print("Hora de inicio(HH): ");
            horaInicio = Integer.parseInt(sc.nextLine());
            System.out.print("Minuto de inicio(MM): ");
            minutoInicio = Integer.parseInt(sc.nextLine());

            System.out.print("Hora de fim(HH): ");
            horaFim = Integer.parseInt(sc.nextLine());
            System.out.print("Minuto de fim(MM): ");
            minutoFim = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido. Introduza apenas horas/minutos.");
            return;
        }

        System.out.println("Dias da semana (s/n para cada dia):");
        Set<DayOfWeek> dias = new HashSet<>();
        DayOfWeek[] todosDias = DayOfWeek.values();
        for(DayOfWeek dia : todosDias) {
            System.out.print(dia + ": ");
            if(sc.nextLine().equals("s")) {
                dias.add(dia);
            }
        }
        
        Escalonamento escalonamento = new Escalonamento(nome, LocalTime.of(horaInicio,minutoInicio),LocalTime.of(horaFim,minutoFim), dias);
        String resposta = "s";
        while(resposta.equals("s")) {
            System.out.print("ID do dispositivo: ");
            int idDisp = Integer.parseInt(sc.nextLine());
            System.out.println("Ação:");
            System.out.println("1 - Ligar");
            System.out.println("2 - Desligar");
            System.out.print("OPÇÃO: ");
            String acao = sc.nextLine();

            if(acao.equals("1")) {
                escalonamento.addAcao(new AcaoDispositivo(idDisp, "Ligar", d -> d.ligar()));
            } else {
                escalonamento.addAcao(new AcaoDispositivo(idDisp, "Desligar", d -> d.desligar()));
            }
            System.out.print("Adicionar mais alguma ação? (s/n): ");
            resposta = sc.nextLine();
        }
        model.addEscalonamento(escalonamento);
        System.out.println("Escalonamento '" + nome + "' criado com sucesso.");
    }

    // inicio -> login -> admin/UserComum -> escalonamentos -> ativar/desativar escalonamento
    private void ativarDesativarEscalonamento() {
        List<Escalonamento> escalonamentos = model.getEscalonamentos();
        if(escalonamentos.isEmpty()) {
            System.out.println("Não existem escalonamentos");
            return;
        }

        listarEscalonamentos();

        System.out.print("Indice do escalonamento: ");
        int index = Integer.parseInt(sc.nextLine());

        if(index < 0 || index >= escalonamentos.size()) {
            System.out.println("Indice inválido.");
            return;
        }

        model.toggleEscalonamento(index);
        System.out.println("Estado do escalonamento alterado com sucesso");
    } 

    // inicio -> login -> admin/UserComum -> escalonamentos -> remover escalonamento
    private void removerEscalonamento() {
        List<Escalonamento> escalonamentos = model.getEscalonamentos();
        if(escalonamentos.isEmpty()) {
            System.out.println("Não existem escalonamentos");
            return;
        }

        listarEscalonamentos();

        System.out.print("Indice do escalonamento a remover: ");
        int index = Integer.parseInt(sc.nextLine());

        if(index < 0 || index >= escalonamentos.size()) {
            System.out.println("Indice inválido.");
            return;
        }

        System.out.print("TEM A CERTEZA? (s/n): ");
        String confirmacao = sc.nextLine();
        if(!confirmacao.equals("s")) {
            System.out.print("Operação cancelada.");
            return;
        }
        model.removeEscalonamento(index);
        System.out.println("Escalonamento removido com sucesso");
    }

    // login -> menuAdmin/UComum -> guardar estado
    private void gravarEstado() {
        try {
            model.gravarEstado("DomusControl/data/estado.dat");
            System.out.println("Estado gravado com sucesso.");
        } catch (IOException e) {
            System.out.println("Erro ao gravar estado: " + e.getMessage());
        }
    }
    
    // inicio -> login -> admin/UserComum -> avancarTempo
    private void avancarTempo() {
        System.out.println("Data/Hora atual: " + model.getDataHoraFormatada());
        System.out.println("1 - Avançar horas e minutos");
        System.out.println("2 - Avançar dias");
        System.out.print("OPÇÃO: ");
        String opcao = sc.nextLine();

        try {
            if(opcao.equals("1")) {
                System.out.print("Horas: ");
                int horas = Integer.parseInt(sc.nextLine());
                System.out.print("Minutos: ");
                int minutos = Integer.parseInt(sc.nextLine());
                model.avancarTempo(horas, minutos);
            } else if (opcao.equals("2")) {
                System.out.print("Dias: ");
                int dias = Integer.parseInt(sc.nextLine());
                model.avancarDias(dias);
            } else {
                System.out.println("Opção inválida !!!");
                return;
            }
            System.out.println("Nova Data/Hora: " + model.getDataHoraFormatada());
            System.out.println("Automações e Escalonamentos verificados.");
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido. Introduza apenas números.");
        }
    }
}
