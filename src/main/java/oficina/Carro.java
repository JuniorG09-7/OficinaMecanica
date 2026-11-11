package oficina;

import java.time.Year;

public class Carro extends Registro {
    private static final int ANO_MINIMO = 1900;

    private String placa;
    private String modelo;
    private int ano;
    private String cliente;

    public Carro(int id, String placa, String modelo, int ano, String cliente) {
        setId(id);
        setPlaca(placa);
        this.modelo = modelo;
        this.ano = ano;
        this.cliente = cliente;
    }

    @Override
    public void validar() throws DadoInvalidoException {
        exigir(placa, "placa");
        exigir(modelo, "modelo");
        exigir(cliente, "cliente");
        if (!placa.matches("[A-Z]{3}[0-9][A-Z0-9][0-9]{2}")) {
            throw new DadoInvalidoException("Placa inválida: " + placa + ". Use o formato ABC1234 ou ABC1D23.");
        }
        int anoMaximo = Year.now().getValue() + 1;
        if (ano < ANO_MINIMO || ano > anoMaximo) {
            throw new DadoInvalidoException("Ano inválido: " + ano + ". Informe um ano entre " + ANO_MINIMO
                    + " e " + anoMaximo + ".");
        }
    }

    public double calcularRevisao() {
        return 250.0 + Math.max(0, Year.now().getValue() - ano) * 15.0;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa == null ? null : placa.replaceAll("[\\s-]", "").toUpperCase();
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    @Override
    public String toString() {
        return String.format("%-4d %-9s %-20s %-5d %-22s R$ %8.2f",
                getId(), placa, modelo, ano, cliente, calcularRevisao());
    }
}
