package oficina;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class DiagnosticoDAO implements CrudDAO<Diagnostico> {
    private static final String SELECT = "SELECT d.*, c.placa, p.nome AS nome_peca FROM diagnosticos d"
            + " JOIN carros c ON c.id = d.carro_id"
            + " LEFT JOIN pecas p ON p.id = d.peca_id";

    @Override
    public void inserir(Diagnostico d) throws OficinaException {
        d.validar();
        String sql = "INSERT INTO diagnosticos (carro_id, avaria, gravidade, situacao, peca_id, data)"
                + " VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = Conexao.abrir();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(ps, d);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    d.setId(rs.getInt(1));
                }
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new OficinaException("O carro ou a peça informados não existem.");
        } catch (SQLException e) {
            throw new BancoDeDadosException("Erro ao registrar o diagnóstico.", e);
        }
    }

    @Override
    public List<Diagnostico> listar() throws OficinaException {
        return consultar(SELECT + " ORDER BY d.id", null);
    }

    public List<Diagnostico> listarPorCarro(int carroId) throws OficinaException {
        return consultar(SELECT + " WHERE d.carro_id = ? ORDER BY d.id", carroId);
    }

    @Override
    public Diagnostico buscarPorId(int id) throws OficinaException {
        List<Diagnostico> lista = consultar(SELECT + " WHERE d.id = ?", id);
        if (lista.isEmpty()) {
            throw new RegistroNaoEncontradoException("diagnóstico", id);
        }
        return lista.get(0);
    }

    @Override
    public void atualizar(Diagnostico d) throws OficinaException {
        d.validar();
        String sql = "UPDATE diagnosticos SET carro_id = ?, avaria = ?, gravidade = ?, situacao = ?,"
                + " peca_id = ?, data = ? WHERE id = ?";
        try (Connection con = Conexao.abrir(); PreparedStatement ps = con.prepareStatement(sql)) {
            preencher(ps, d);
            ps.setInt(7, d.getId());
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new OficinaException("O carro ou a peça informados não existem.");
        } catch (SQLException e) {
            throw new BancoDeDadosException("Erro ao atualizar o diagnóstico.", e);
        }
    }

    @Override
    public void excluir(int id) throws OficinaException {
        try (Connection con = Conexao.abrir();
             PreparedStatement ps = con.prepareStatement("DELETE FROM diagnosticos WHERE id = ?")) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new RegistroNaoEncontradoException("diagnóstico", id);
            }
        } catch (SQLException e) {
            throw new BancoDeDadosException("Erro ao excluir o diagnóstico.", e);
        }
    }

    private List<Diagnostico> consultar(String sql, Integer parametro) throws OficinaException {
        List<Diagnostico> lista = new ArrayList<>();
        try (Connection con = Conexao.abrir(); PreparedStatement ps = con.prepareStatement(sql)) {
            if (parametro != null) {
                ps.setInt(1, parametro);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(montar(rs));
                }
            }
        } catch (SQLException e) {
            throw new BancoDeDadosException("Erro ao consultar os diagnósticos.", e);
        }
        return lista;
    }

    private void preencher(PreparedStatement ps, Diagnostico d) throws SQLException {
        ps.setInt(1, d.getCarroId());
        ps.setString(2, d.getAvaria().trim());
        ps.setString(3, d.getGravidade());
        ps.setString(4, d.getSituacao());
        if (d.getPecaId() == null) {
            ps.setNull(5, Types.INTEGER);
        } else {
            ps.setInt(5, d.getPecaId());
        }
        ps.setDate(6, Date.valueOf(d.getData()));
    }

    private Diagnostico montar(ResultSet rs) throws SQLException {
        int pecaId = rs.getInt("peca_id");
        Diagnostico d = new Diagnostico(rs.getInt("id"), rs.getInt("carro_id"), rs.getString("avaria"),
                rs.getString("gravidade"), rs.getString("situacao"), rs.wasNull() ? null : pecaId,
                rs.getDate("data").toLocalDate());
        d.setPlaca(rs.getString("placa"));
        d.setNomePeca(rs.getString("nome_peca"));
        return d;
    }
}
