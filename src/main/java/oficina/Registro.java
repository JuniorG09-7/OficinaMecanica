package oficina;

public abstract class Registro {
    private int id;

    public abstract void validar() throws DadoInvalidoException;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    protected void exigir(String valor, String campo) throws DadoInvalidoException {
        if (valor == null || valor.trim().isEmpty()) {
            throw new DadoInvalidoException("O campo \"" + campo + "\" não pode ficar vazio.");
        }
    }
}
