package oficina;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CarroDAO implements CrudDAO<Carro> {
    @Override
    public void inserir(Carro c) throws OficinaException {
        c.validar();
        String sql = "INSERT INTO carros (placa, modelo, ano, cliente) VALUES (?, ?, ?, ?)";
        try (Connection con = Conexao.abrir();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(ps, c);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    c.setId(rs.getInt(1));
                }
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RegistroDuplicadoException("Já existe um carro cadastrado com a placa " + c.getPlaca() + ".");
        } catch (SQLException e) {
            throw new BancoDeDadosException("Erro ao cadastrar o carro.", e);
        }
    }

    @Override
    public List<Carro> listar() throws OficinaException {
        List<Carro> carros = new ArrayList<>();
        try (Connection con = Conexao.abrir();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM carros ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                carros.add(montar(rs));
            }
        } catch (SQLException e) {
            throw new BancoDeDadosException("Erro ao listar os carros.", e);
        }
        return carros;
    }

    @Override
    public Carro buscarPorId(int id) throws OficinaException {
        try (Connection con = Conexao.abrir();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM carros WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new RegistroNaoEncontradoException("carro", id);
                }
                return montar(rs);
            }
        } catch (SQLException e) {
            throw new BancoDeDadosException("Erro ao buscar o carro.", e);
        }
    }

    @Override
    public void atualizar(Carro c) throws OficinaException {
        c.validar();
        String sql = "UPDATE carros SET placa = ?, modelo = ?, ano = ?, cliente = ? WHERE id = ?";
        try (Connection con = Conexao.abrir(); PreparedStatement ps = con.prepareStatement(sql)) {
            preencher(ps, c);
            ps.setInt(5, c.getId());
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RegistroDuplicadoException("Já existe um carro cadastrado com a placa " + c.getPlaca() + ".");
        } catch (SQLException e) {
            throw new BancoDeDadosException("Erro ao atualizar o carro.", e);
        }
    }

    @Override
    public void excluir(int id) throws OficinaException {
        try (Connection con = Conexao.abrir();
             PreparedStatement ps = con.prepareStatement("DELETE FROM carros WHERE id = ?")) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new RegistroNaoEncontradoException("carro", id);
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new OficinaException("Este carro tem diagnósticos registrados. Exclua os diagnósticos primeiro.");
        } catch (SQLException e) {
            throw new BancoDeDadosException("Erro ao excluir o carro.", e);
        }
    }

    private void preencher(PreparedStatement ps, Carro c) throws SQLException {
        ps.setString(1, c.getPlaca());
        ps.setString(2, c.getModelo().trim());
        ps.setInt(3, c.getAno());
        ps.setString(4, c.getCliente().trim());
    }

    private Carro montar(ResultSet rs) throws SQLException {
        return new Carro(rs.getInt("id"), rs.getString("placa"), rs.getString("modelo"),
                rs.getInt("ano"), rs.getString("cliente"));
    }
}
