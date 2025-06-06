package org.example;

import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProject {
    private final String url = Keys.loadProperty("DB.URL");
    private final String username = Keys.loadProperty("DB.USERNAME");
    private final String password = Keys.loadProperty("DB.PASSWORD");

    public List<String> getProjects(int id) {
        List<String> projectNames = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String select = "SELECT DISTINCT project_name FROM user_translations WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(select)) {
                statement.setInt(1, id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String projectName = resultSet.getString("project_name");
                        projectNames.add(projectName);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return projectNames;
    }

    public List<String> getLanguage(int id, String projectName){
        List<String> languages = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String select = "SELECT DISTINCT language FROM user_translations WHERE id = ? AND project_name = ?";
            try (PreparedStatement statement = connection.prepareStatement(select)) {
                statement.setInt(1, id);
                statement.setString(2, projectName);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String language = resultSet.getString("language");
                        languages.add(language);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return languages;
    }

    public Map<String, Integer> getProjectInfo(int id, String projectName){
        Map<String, Integer> map = new HashMap<>();
        String select = "SELECT DISTINCT language, COUNT(key) as key_val FROM user_translations WHERE id = ? and project_name = ? GROUP BY language";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(select)) {
            statement.setInt(1, id);
            statement.setString(2, projectName);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String key = resultSet.getString("language");
                    int count = resultSet.getInt("key_val");

                    map.put(key, count);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return map;
    }
}
