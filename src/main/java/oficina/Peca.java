package oficina;

public class Peca extends Registro {
    private String codigo;
    private String nome;
    private String marca;
    private double preco;

    public Peca(int id, String codigo, String nome, String marca, double preco) {
        setId(id);
        setCodigo(codigo);
        this.nome = nome;
        this.marca = marca;
        this.preco = preco;
    }

    @Override
    public void validar() throws DadoInvalidoException {
        exigir(codigo, "código");
        exigir(nome, "nome");
        exigir(marca, "marca");
        if (preco <= 0) {
            throw new DadoInvalidoException("Preço inválido: o valor precisa ser maior que zero.");
        }
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo == null ? null : codigo.trim().toUpperCase();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    @Override
    public String toString() {
        return String.format("%-4d %-12s %-28s %-16s R$ %8.2f", getId(), codigo, nome, marca, preco);
    }
}
