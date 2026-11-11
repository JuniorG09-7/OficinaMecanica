package oficina;

public class RegistroNaoEncontradoException extends OficinaException {
    public RegistroNaoEncontradoException(String registro, int id) {
        super("Não existe " + registro + " com o id " + id + ".");
    }
}
