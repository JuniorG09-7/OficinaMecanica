package oficina;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class Diagnostico extends Registro {
    public static final List<String> GRAVIDADES = Arrays.asList("BAIXA", "MEDIA", "ALTA");
    public static final List<String> SITUACOES = Arrays.asList("ABERTO", "EM REPARO", "CONCLUIDO");

    private int carroId;
    private String avaria;
    private String gravidade;
    private String situacao;
    private Integer pecaId;
    private LocalDate data;

    private String placa = "";
    private String nomePeca;

    public Diagnostico(int id, int carroId, String avaria, String gravidade, String situacao,
                       Integer pecaId, LocalDate data) {
        setId(id);
        this.carroId = carroId;
        this.avaria = avaria;
        this.gravidade = gravidade;
        this.situacao = situacao;
        this.pecaId = pecaId;
        this.data = data;
    }

    @Override
    public void validar() throws DadoInvalidoException {
        exigir(avaria, "avaria");
        if (!GRAVIDADES.contains(gravidade)) {
            throw new DadoInvalidoException("Gravidade inválida: " + gravidade + ".");
        }
        if (!SITUACOES.contains(situacao)) {
            throw new DadoInvalidoException("Situação inválida: " + situacao + ".");
        }
    }

    public int getCarroId() {
        return carroId;
    }

    public String getAvaria() {
        return avaria;
    }

    public String getGravidade() {
        return gravidade;
    }

    public String getSituacao() {
        return situacao;
    }

    public Integer getPecaId() {
        return pecaId;
    }

    public LocalDate getData() {
        return data;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public void setNomePeca(String nomePeca) {
        this.nomePeca = nomePeca;
    }

    @Override
    public String toString() {
        return String.format("%-4d %-9s %-10s %-30s %-7s %-10s %s",
                getId(), placa, data, avaria, gravidade, situacao, nomePeca == null ? "-" : nomePeca);
    }
}
