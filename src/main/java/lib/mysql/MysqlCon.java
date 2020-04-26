package lib.mysql;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MysqlCon {
    Properties prop;
    InputStream inputStream;
    String propFile = "config.properties";
    Connection con;

    public MysqlCon() throws IOException, SQLException {
        prop = new Properties();
        inputStream = getClass().getClassLoader().getResourceAsStream(propFile);

        if (inputStream != null) {
            prop.load(inputStream);
        } else {
            throw new FileNotFoundException("property file '" + propFile + "' not found in the classpath");
        }
        con = DriverManager.getConnection(
                prop.getProperty("connAddress"),
                prop.getProperty("mysqlLogin"),
                prop.getProperty("mysqlPassword")
        );
    }

    public Connection getConnection() { return con; }
}
