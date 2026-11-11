package oficina;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class Conexao {
    private static final String ARQUIVO = "banco.properties";

    private Conexao() {
    }

    public static Connection abrir() throws BancoDeDadosException {
        try (FileInputStream arquivo = new FileInputStream("banco.properties")) {
            Properties config = new Properties();
            config.load(arquivo);
            return DriverManager.getConnection(config.getProperty("url"),
                    config.getProperty("usuario"), config.getProperty("senha"));
        } catch (Exception e) {
            throw new BancoDeDadosException("Não foi possível conectar ao MySQL. ...", e);
        }
    }

    public static void criarTabelas() throws BancoDeDadosException {
        try (Connection con = abrir(); Statement st = con.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS carros ("
                    + " id INT PRIMARY KEY AUTO_INCREMENT,"
                    + " placa VARCHAR(7) NOT NULL UNIQUE,"
                    + " modelo VARCHAR(60) NOT NULL,"
                    + " ano INT NOT NULL,"
                    + " cliente VARCHAR(80) NOT NULL)");
            st.execute("CREATE TABLE IF NOT EXISTS pecas ("
                    + " id INT PRIMARY KEY AUTO_INCREMENT,"
                    + " codigo VARCHAR(20) NOT NULL UNIQUE,"
                    + " nome VARCHAR(80) NOT NULL,"
                    + " marca VARCHAR(40) NOT NULL,"
                    + " preco DECIMAL(10,2) NOT NULL)");
            st.execute("CREATE TABLE IF NOT EXISTS diagnosticos ("
                    + " id INT PRIMARY KEY AUTO_INCREMENT,"
                    + " carro_id INT NOT NULL,"
                    + " avaria VARCHAR(200) NOT NULL,"
                    + " gravidade VARCHAR(10) NOT NULL,"
                    + " situacao VARCHAR(10) NOT NULL,"
                    + " peca_id INT NULL,"
                    + " data DATE NOT NULL,"
                    + " FOREIGN KEY (carro_id) REFERENCES carros(id),"
                    + " FOREIGN KEY (peca_id) REFERENCES pecas(id))");
        } catch (SQLException e) {
            throw new BancoDeDadosException("Erro ao criar as tabelas.", e);
        }
    }
}
