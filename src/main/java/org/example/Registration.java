package org.example;

import org.postgresql.util.PSQLException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Registration {
    private final String url = Keys.loadProperty("DB.URL");
    private final String username = Keys.loadProperty("DB.USERNAME");
    private final String password = Keys.loadProperty("DB.PASSWORD");

    public int registrationUser(String user, String userPassword){
        int flag = 0;
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String insertUser = "INSERT INTO users (email, password) VALUES (?, ?);";

            try(PreparedStatement insertStatement = connection.prepareStatement(insertUser)){
                insertStatement.setString(1, user);
                insertStatement.setString(2, userPassword);
                insertStatement.executeUpdate();
            } catch (PSQLException e){
                flag++;
                System.err.println("User has already exist " + e.getMessage());

            }
        } catch (SQLException e) {
            flag++;
            throw new RuntimeException(e);
        }
        return flag;
    }
}
