package server.dataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseManager
{

    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/studs";
    private static final String DATABASE_USER = "s408840";
    private static final String DATABASE_PASSWORD = "1k0hobfAIavVHBnW";

    public Connection getConnection() throws SQLException
    {
        return DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
    }
}