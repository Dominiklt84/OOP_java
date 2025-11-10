import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Data_base {
    private static final String url = "jdbc:mysql://localhost:3306/akademine_IS?useSSL=false&serverTimezone=UTC";
    private static final String user = "root";
    private static final String password = "";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Nepavyko užkrauti MySQL",e);
        }
    }
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url,user,password);
    }
}
