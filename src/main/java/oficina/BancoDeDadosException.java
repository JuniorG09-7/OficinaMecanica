package oficina;

public class BancoDeDadosException extends OficinaException {
    public BancoDeDadosException(String mensagem, Throwable causa) {
        super(mensagem + " Detalhe: " + causa.getMessage(), causa);
    }

    public BancoDeDadosException(String mensagem) {
        super(mensagem);
    }
}
