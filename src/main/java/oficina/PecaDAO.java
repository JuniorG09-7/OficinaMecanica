package oficina;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PecaDAO implements CrudDAO<Peca> {
    @Override
    public void inserir(Peca p) throws OficinaException {
        p.validar();
        String sql = "INSERT INTO pecas (codigo, nome, marca, preco) VALUES (?, ?, ?, ?)";
        try (Connection con = Conexao.abrir();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(ps, p);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setId(rs.getInt(1));
                }
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RegistroDuplicadoException("Já existe uma peça cadastrada com o código " + p.getCodigo() + ".");
        } catch (SQLException e) {
            throw new BancoDeDadosException("Erro ao cadastrar a peça.", e);
        }
    }

    @Override
    public List<Peca> listar() throws OficinaException {
        List<Peca> pecas = new ArrayList<>();
        try (Connection con = Conexao.abrir();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM pecas ORDER BY nome");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                pecas.add(montar(rs));
            }
        } catch (SQLException e) {
            throw new BancoDeDadosException("Erro ao listar as peças.", e);
        }
        return pecas;
    }

    @Override
    public Peca buscarPorId(int id) throws OficinaException {
        try (Connection con = Conexao.abrir();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM pecas WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new RegistroNaoEncontradoException("peça", id);
                }
                return montar(rs);
            }
        } catch (SQLException e) {
            throw new BancoDeDadosException("Erro ao buscar a peça.", e);
        }
    }

    @Override
    public void atualizar(Peca p) throws OficinaException {
        p.validar();
        String sql = "UPDATE pecas SET codigo = ?, nome = ?, marca = ?, preco = ? WHERE id = ?";
        try (Connection con = Conexao.abrir(); PreparedStatement ps = con.prepareStatement(sql)) {
            preencher(ps, p);
            ps.setInt(5, p.getId());
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RegistroDuplicadoException("Já existe uma peça cadastrada com o código " + p.getCodigo() + ".");
        } catch (SQLException e) {
            throw new BancoDeDadosException("Erro ao atualizar a peça.", e);
        }
    }

    @Override
    public void excluir(int id) throws OficinaException {
        try (Connection con = Conexao.abrir();
             PreparedStatement ps = con.prepareStatement("DELETE FROM pecas WHERE id = ?")) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new RegistroNaoEncontradoException("peça", id);
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new OficinaException("Esta peça está sendo usada em um diagnóstico e não pode ser excluída.");
        } catch (SQLException e) {
            throw new BancoDeDadosException("Erro ao excluir a peça.", e);
        }
    }

    private void preencher(PreparedStatement ps, Peca p) throws SQLException {
        ps.setString(1, p.getCodigo());
        ps.setString(2, p.getNome().trim());
        ps.setString(3, p.getMarca().trim());
        ps.setDouble(4, p.getPreco());
    }

    private Peca montar(ResultSet rs) throws SQLException {
        return new Peca(rs.getInt("id"), rs.getString("codigo"), rs.getString("nome"),
                rs.getString("marca"), rs.getDouble("preco"));
    }
}
