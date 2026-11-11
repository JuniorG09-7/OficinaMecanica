package oficina;

public class OficinaException extends Exception {
    public OficinaException(String mensagem) {
        super(mensagem);
    }

    public OficinaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
