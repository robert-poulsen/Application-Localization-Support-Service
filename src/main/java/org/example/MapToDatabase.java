package org.example;

import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MapToDatabase {
    private final String url = Keys.loadProperty("DB.URL");
    private final String username = Keys.loadProperty("DB.USERNAME");
    private final String password = Keys.loadProperty("DB.PASSWORD");

    public void saveUserTranslations(int id, String projectName, String language, Map<String, String> map) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String insertTranslation = "INSERT INTO user_translations VALUES (?, ?, ?, ?, ?)";
            String deleteTranslation = "DELETE FROM user_translations WHERE id = ? AND project_name = ? AND language = ?";

            try(PreparedStatement deleteStatement = connection.prepareStatement(deleteTranslation)){
                deleteStatement.setInt(1, id);
                deleteStatement.setString(2, projectName);
                deleteStatement.setString(3, language);
                deleteStatement.executeUpdate();
            }
            for(Map.Entry<String, String> entry : map.entrySet()){
                try(PreparedStatement insertStatement = connection.prepareStatement(insertTranslation)){
                    insertStatement.setInt(1, id);
                    insertStatement.setString(2, projectName);
                    insertStatement.setString(3, language);
                    insertStatement.setString(4, entry.getKey());
                    insertStatement.setString(5, entry.getValue());
                    insertStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUserTranslation(int id, String projectName, String language, String key){
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "SELECT translation FROM user_translations WHERE id = ? AND project_name = ? AND language = ? AND key = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, id);
                statement.setString(2, projectName);
                statement.setString(3, language);
                statement.setString(4, key);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                       return resultSet.getString("translation");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Помилка при отриманні перекладу з бази даних: " + e.getMessage());
        }
        return null;
    }

    public Map<String, String> getMap(int id, String projectName, String language) {
        Map<String, String> map = new HashMap<>();

        String query = "SELECT key, translation FROM user_translations WHERE id = ? AND project_name = ? AND language = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);
            statement.setString(2, projectName);
            statement.setString(3, language);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String key = resultSet.getString("key");
                    String translation = resultSet.getString("translation");
                    map.put(key, translation);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return map;
    }
}
