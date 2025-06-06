package org.example;

import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;

import java.io.IOException;
import java.sql.*;
import java.util.Objects;

public class DatabaseManager {
    private final String url = Keys.loadProperty("DB.URL");
    private final String username = Keys.loadProperty("DB.USERNAME");
    private final String password = Keys.loadProperty("DB.PASSWORD");
    private final Translator translator;

    public DatabaseManager(Translator translator) {
        this.translator = translator;
    }

        public void saveTranslation(String sourceLang, String sourceWord, String targetLang, String targetWord) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String insertTranslation = "INSERT INTO translations (" + sourceLang + ", " + targetLang + ") VALUES (?, ?)";
            String insertTranslationWithEnglish = "INSERT INTO translations (" + sourceLang + ", " + targetLang + ", en) VALUES (?, ?, ?)";
            String updateTranslationWithTwoWords = "UPDATE translations SET " + sourceLang + " = ?, " + targetLang + " = ? WHERE en = ?";

            String insertQuery = insertTranslationWithEnglish;
            if(Objects.equals(sourceLang, "en") || Objects.equals(targetLang, "en")){
                insertQuery = insertTranslation;
            }

            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {

                if(Objects.equals(sourceLang, "en") || Objects.equals(targetLang, "en")){
                    insertStatement.setString(1, sourceWord);
                    insertStatement.setString(2, targetWord);
                } else {
                    insertStatement.setString(1, sourceWord);
                    insertStatement.setString(2, targetWord);
                    insertStatement.setString(3, translator.translate(targetWord, targetLang, "en"));
                }

                try {
                    insertStatement.executeUpdate();
                } catch (PSQLException e) {
                    if(Objects.equals(e.getSQLState(), PSQLState.UNIQUE_VIOLATION.getState())){

                        String updateQuery = updateTranslationWithTwoWords;
                        try(PreparedStatement updateStatement = connection.prepareStatement(updateQuery)){
                                updateStatement.setString(1, sourceWord);
                                updateStatement.setString(2, targetWord);
                                updateStatement.setString(3, translator.translate(targetWord, targetLang, "en"));
                            updateStatement.executeUpdate();
                        }

                    } else {
                        System.err.println("Error when saving the translation to the database: " + e.getMessage());
                    }

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            System.err.println("Error when saving the translation to the database: " + e.getMessage());
        }
    }

    public String getTranslation(String targetLang, String sourceLang, String word) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "SELECT " + targetLang + " FROM translations WHERE " + sourceLang + " = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, word);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString(targetLang);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving translation from database: " + e.getMessage());
        }
        return null;
    }
}