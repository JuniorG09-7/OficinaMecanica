package oficina;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    private final Scanner scanner = new Scanner(System.in);
    private final CarroDAO carroDAO = new CarroDAO();
    private final PecaDAO pecaDAO = new PecaDAO();
    private final DiagnosticoDAO diagnosticoDAO = new DiagnosticoDAO();

    public static void main(String[] args) {
        try {
            Conexao.criarTabelas();
            new Main().executar();
        } catch (OficinaException e) {
            System.out.println("ERRO: " + e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println();
        }
        System.out.println("Sistema encerrado. Até logo!");
    }

    private void executar() {
        System.out.println("=========================================");
        System.out.println("   OFICINA MECÂNICA");
        System.out.println("=========================================");

        boolean rodando = true;
        while (rodando) {
            System.out.println();
            System.out.println("CARROS:       1 - Cadastrar | 2 - Listar | 3 - Atualizar | 4 - Excluir");
            System.out.println("PEÇAS:        5 - Cadastrar | 6 - Catálogo | 7 - Atualizar | 8 - Excluir");
            System.out.println("DIAGNÓSTICOS: 9 - Registrar | 10 - Listar | 11 - Histórico do carro | 12 - Atualizar | 13 - Excluir");
            System.out.println("0 - Sair");
            String opcao = ler("Escolha uma opção: ");

            try {
                switch (opcao) {
                    case "1": cadastrarCarro(); break;
                    case "2": mostrar("CARROS", carroDAO.listar()); break;
                    case "3": atualizarCarro(); break;
                    case "4": excluirCarro(); break;
                    case "5": cadastrarPeca(); break;
                    case "6": mostrar("CATÁLOGO DE PEÇAS", pecaDAO.listar()); break;
                    case "7": atualizarPeca(); break;
                    case "8": excluirPeca(); break;
                    case "9": registrarDiagnostico(); break;
                    case "10": mostrar("DIAGNÓSTICOS", diagnosticoDAO.listar()); break;
                    case "11": historicoDoCarro(); break;
                    case "12": atualizarDiagnostico(); break;
                    case "13": excluirDiagnostico(); break;
                    case "0": rodando = false; break;
                    default: System.out.println("Opção inválida. Digite um número de 0 a 13.");
                }
            } catch (OficinaException e) {
                System.out.println("ERRO: " + e.getMessage());
            } catch (NoSuchElementException e) {
                throw e;
            } catch (RuntimeException e) {
                System.out.println("ERRO inesperado: " + e.getMessage());
            }
        }
    }

    private void mostrar(String titulo, List<? extends Registro> registros) {
        System.out.println("\n--- " + titulo + " ---");
        if (registros.isEmpty()) {
            System.out.println("Nenhum registro encontrado.");
            return;
        }
        for (Registro r : registros) {
            System.out.println(r);
        }
    }

    private void cadastrarCarro() throws OficinaException {
        System.out.println("\n--- CADASTRAR CARRO ---");
        String placa = lerObrigatorio("Placa: ", "placa");
        String modelo = lerObrigatorio("Modelo: ", "modelo");
        int ano = lerInteiro(lerObrigatorio("Ano: ", "ano"), "ano");
        String cliente = lerObrigatorio("Nome do cliente: ", "cliente");

        Carro carro = new Carro(0, placa, modelo, ano, cliente);
        carroDAO.inserir(carro);
        System.out.printf("Carro cadastrado! (id %d, revisão estimada R$ %.2f)%n", carro.getId(), carro.calcularRevisao());
    }

    private void atualizarCarro() throws OficinaException {
        System.out.println("\n--- ATUALIZAR CARRO ---");
        Carro atual = carroDAO.buscarPorId(lerId("carro"));
        System.out.println("(Aperte ENTER para manter o valor atual)");
        atual.setPlaca(lerOuManter("Placa", atual.getPlaca()));
        atual.setModelo(lerOuManter("Modelo", atual.getModelo()));
        atual.setAno(lerInteiro(lerOuManter("Ano", String.valueOf(atual.getAno())), "ano"));
        atual.setCliente(lerOuManter("Cliente", atual.getCliente()));
        carroDAO.atualizar(atual);
        System.out.println("Carro atualizado!");
    }

    private void excluirCarro() throws OficinaException {
        System.out.println("\n--- EXCLUIR CARRO ---");
        Carro c = carroDAO.buscarPorId(lerId("carro"));
        if (confirmar("Excluir o carro " + c.getModelo() + " (" + c.getPlaca() + ")?")) {
            carroDAO.excluir(c.getId());
            System.out.println("Carro excluído!");
        }
    }

    private void cadastrarPeca() throws OficinaException {
        System.out.println("\n--- CADASTRAR PEÇA ---");
        String codigo = lerObrigatorio("Código: ", "código");
        String nome = lerObrigatorio("Nome: ", "nome");
        String marca = lerObrigatorio("Marca: ", "marca");
        double preco = lerPreco(lerObrigatorio("Preço: ", "preço"));

        Peca peca = new Peca(0, codigo, nome, marca, preco);
        pecaDAO.inserir(peca);
        System.out.println("Peça cadastrada! (id " + peca.getId() + ")");
    }

    private void atualizarPeca() throws OficinaException {
        System.out.println("\n--- ATUALIZAR PEÇA ---");
        Peca atual = pecaDAO.buscarPorId(lerId("peça"));
        System.out.println("(Aperte ENTER para manter o valor atual)");
        atual.setCodigo(lerOuManter("Código", atual.getCodigo()));
        atual.setNome(lerOuManter("Nome", atual.getNome()));
        atual.setMarca(lerOuManter("Marca", atual.getMarca()));
        atual.setPreco(lerPreco(lerOuManter("Preço", String.valueOf(atual.getPreco()))));
        pecaDAO.atualizar(atual);
        System.out.println("Peça atualizada!");
    }

    private void excluirPeca() throws OficinaException {
        System.out.println("\n--- EXCLUIR PEÇA ---");
        Peca p = pecaDAO.buscarPorId(lerId("peça"));
        if (confirmar("Excluir a peça " + p.getNome() + " (" + p.getCodigo() + ")?")) {
            pecaDAO.excluir(p.getId());
            System.out.println("Peça excluída!");
        }
    }

    private void registrarDiagnostico() throws OficinaException {
        System.out.println("\n--- REGISTRAR DIAGNÓSTICO ---");
        Carro carro = carroDAO.buscarPorId(lerId("carro"));
        System.out.println("Carro: " + carro.getModelo() + " (" + carro.getPlaca() + ")");
        String avaria = lerObrigatorio("Avaria encontrada: ", "avaria");
        String gravidade = lerOpcao("Gravidade", Diagnostico.GRAVIDADES, null);
        Integer pecaId = lerPeca(null);

        Diagnostico d = new Diagnostico(0, carro.getId(), avaria, gravidade, "ABERTO", pecaId, LocalDate.now());
        diagnosticoDAO.inserir(d);
        System.out.println("Diagnóstico registrado! (id " + d.getId() + ")");
    }

    private void historicoDoCarro() throws OficinaException {
        Carro carro = carroDAO.buscarPorId(lerId("carro"));
        mostrar("HISTÓRICO DE " + carro.getPlaca(), diagnosticoDAO.listarPorCarro(carro.getId()));
    }

    private void atualizarDiagnostico() throws OficinaException {
        System.out.println("\n--- ATUALIZAR DIAGNÓSTICO ---");
        Diagnostico atual = diagnosticoDAO.buscarPorId(lerId("diagnóstico"));
        System.out.println("(Aperte ENTER para manter o valor atual)");
        String avaria = lerOuManter("Avaria", atual.getAvaria());
        String gravidade = lerOpcao("Gravidade", Diagnostico.GRAVIDADES, atual.getGravidade());
        String situacao = lerOpcao("Situação", Diagnostico.SITUACOES, atual.getSituacao());
        Integer pecaId = lerPeca(atual.getPecaId());

        diagnosticoDAO.atualizar(new Diagnostico(atual.getId(), atual.getCarroId(), avaria, gravidade, situacao,
                pecaId, atual.getData()));
        System.out.println("Diagnóstico atualizado!");
    }

    private void excluirDiagnostico() throws OficinaException {
        System.out.println("\n--- EXCLUIR DIAGNÓSTICO ---");
        Diagnostico d = diagnosticoDAO.buscarPorId(lerId("diagnóstico"));
        if (confirmar("Excluir o diagnóstico \"" + d.getAvaria() + "\"?")) {
            diagnosticoDAO.excluir(d.getId());
            System.out.println("Diagnóstico excluído!");
        }
    }

    private String ler(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine().trim();
    }

    private String lerObrigatorio(String mensagem, String campo) throws DadoInvalidoException {
        String valor = ler(mensagem);
        if (valor.isEmpty()) {
            throw new DadoInvalidoException("O campo \"" + campo + "\" não pode ficar vazio.");
        }
        return valor;
    }

    private String lerOuManter(String campo, String valorAtual) {
        String valor = ler(campo + " [" + valorAtual + "]: ");
        return valor.isEmpty() ? valorAtual : valor;
    }

    private int lerInteiro(String texto, String campo) throws DadoInvalidoException {
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new DadoInvalidoException("Valor inválido para " + campo + ": \"" + texto + "\". Digite apenas números.");
        }
    }

    private int lerId(String registro) throws DadoInvalidoException {
        return lerInteiro(lerObrigatorio("Id (" + registro + "): ", "id"), "id");
    }

    private double lerPreco(String texto) throws DadoInvalidoException {
        try {
            return Double.parseDouble(texto.replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new DadoInvalidoException("Preço inválido: \"" + texto + "\". Use apenas números (ex: 89,90).");
        }
    }

    private String lerOpcao(String campo, List<String> opcoes, String valorAtual) throws DadoInvalidoException {
        StringBuilder texto = new StringBuilder(campo + " (");
        for (int i = 0; i < opcoes.size(); i++) {
            texto.append(i + 1).append(" - ").append(opcoes.get(i)).append(i < opcoes.size() - 1 ? " | " : ")");
        }
        String valor = ler(texto + (valorAtual == null ? ": " : " [" + valorAtual + "]: "));
        if (valor.isEmpty() && valorAtual != null) {
            return valorAtual;
        }
        if (valor.isEmpty()) {
            throw new DadoInvalidoException("O campo \"" + campo + "\" não pode ficar vazio.");
        }
        int numero = lerInteiro(valor, campo);
        if (numero < 1 || numero > opcoes.size()) {
            throw new DadoInvalidoException(campo + " inválida: escolha um número de 1 a " + opcoes.size() + ".");
        }
        return opcoes.get(numero - 1);
    }

    private Integer lerPeca(Integer pecaAtual) throws OficinaException {
        String texto = ler("Id da peça necessária (ENTER para " + (pecaAtual == null ? "nenhuma" : "manter " + pecaAtual) + "): ");
        if (texto.isEmpty()) {
            return pecaAtual;
        }
        return pecaDAO.buscarPorId(lerInteiro(texto, "peça")).getId();
    }

    private boolean confirmar(String pergunta) {
        boolean sim = ler(pergunta + " (S/N): ").equalsIgnoreCase("S");
        if (!sim) {
            System.out.println("Exclusão cancelada.");
        }
        return sim;
    }
}
