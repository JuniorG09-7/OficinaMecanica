package oficina;

import java.util.List;

public interface CrudDAO<T> {
    void inserir(T objeto) throws OficinaException;

    List<T> listar() throws OficinaException;

    T buscarPorId(int id) throws OficinaException;

    void atualizar(T objeto) throws OficinaException;

    void excluir(int id) throws OficinaException;
}
