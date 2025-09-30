package com.morax.sentiment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/movies";
    private static final String USER = "morax";
    private static final String PASSWORD = "morax123";

    public DatabaseManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            initializeDatabase();
        } catch (ClassNotFoundException e) {
            System.out.println("⚠️ MySQL Driver not found!");
        }
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            if (!tableExists(conn, "reviews")) {
                System.out.println("⚠️ Table not found, running schema.sql...");
                runSqlFile(conn, "database/schema.sql"); // Make sure schema.sql is correct

                System.out.println("📥 Loading initial data from data.sql...");
                runSqlFile(conn, "database/data.sql"); // Ensure data.sql is present with data to insert
            }
        } catch (SQLException e) {
            System.out.println("❌ DB Init Error: " + e.getMessage());
        }
    }

    private boolean tableExists(Connection conn, String tableName) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet rs = meta.getTables(null, null, tableName, null)) {
            return rs.next();
        }
    }

    private void runSqlFile(Connection conn, String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath));
             Statement stmt = conn.createStatement()) {

            StringBuilder sql = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--")) {
                    continue;
                }
                sql.append(line);
                if (line.endsWith(";")) {
                    stmt.execute(sql.toString());
                    sql.setLength(0);
                }
            }
        } catch (IOException | SQLException e) {
            System.out.println("❌ SQL File Error (" + filePath + "): " + e.getMessage());
        }
    }

    public void insertReview(MovieReview review) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String query = "INSERT INTO reviews (review, sentiment) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, review.getText());
            stmt.setString(2, review.getSentiment());
            stmt.executeUpdate();
            System.out.println("✅ Review saved to DB!");
        } catch (SQLException e) {
            System.out.println("❌ DB Insert Error: " + e.getMessage());
        }
    }

    public List<MovieReview> getAllReviews() {
        List<MovieReview> reviews = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String query = "SELECT * FROM reviews";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                reviews.add(new MovieReview(
                        rs.getString("review"),
                        rs.getString("sentiment")
                ));
            }
        } catch (SQLException e) {
            System.out.println("❌ DB Read Error: " + e.getMessage());
        }
        return reviews;
    }

    public void updateReview(int id, String newText, String newSentiment) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String query = "UPDATE reviews SET review=?, sentiment=? WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, newText);
            stmt.setString(2, newSentiment);
            stmt.setInt(3, id);
            stmt.executeUpdate();
            System.out.println("✅ Review updated!");
        } catch (SQLException e) {
            System.out.println("❌ DB Update Error: " + e.getMessage());
        }
    }

    public void deleteReview(int id) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String query = "DELETE FROM reviews WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("✅ Review deleted!");
        } catch (SQLException e) {
            System.out.println("❌ DB Delete Error: " + e.getMessage());
        }
    }
}
