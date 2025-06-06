package org.example;

import java.sql.*;

public class Login {
    private final String url = Keys.loadProperty("DB.URL");
    private final String username = Keys.loadProperty("DB.USERNAME");
    private final String password = Keys.loadProperty("DB.PASSWORD");

    public int loginUser(String user, String userPassword) {
        int id = 0;
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String selectUser = "SELECT id FROM users WHERE email = ? AND password = ?";
            try (PreparedStatement statement = connection.prepareStatement(selectUser)) {
                statement.setString(1, user);
                statement.setString(2, userPassword);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        id = resultSet.getInt("id");
                    }
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
