package server.users;

import server.dataBase.DataBaseManager;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthenticationManager
{
    private final DataBaseManager databaseManager;

    public AuthenticationManager(DataBaseManager databaseManager)
    {
        this.databaseManager = databaseManager;
    }

    public User register(String username, String password)
    {
        String passwordHash = generateHash(password);

        try (Connection connection = databaseManager.getConnection())
        {
            String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, passwordHash);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        }

        return new User(username, passwordHash);
    }

    public User login(String username, String password)
    {
        String passwordHash = generateHash(password);

        try (Connection connection = databaseManager.getConnection())
        {
            String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, passwordHash);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
            {
                return new User(username, passwordHash);
            }
            else
            {
                return null;
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error when trying to login", e);
        }
    }

    private String generateHash(String password)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-384");
            byte[] hashedBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashedBytes);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException("Error creating password hash", e);
        }
    }

    private String bytesToHex(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes)
        {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}