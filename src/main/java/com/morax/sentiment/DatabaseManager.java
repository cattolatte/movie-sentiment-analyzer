package com.morax.sentiment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages all database operations, including initialization,
 * and CRUD (Create, Read, Update, Delete) for Movies and Reviews.
 */
public class DatabaseManager {
    
    // --- Database Connection Details ---
    private static final String URL = "jdbc:mysql://localhost:3306/movies";
    private static final String USER = "morax";
    private static final String PASSWORD = "morax123";

    /**
     * Constructor for the DatabaseManager.
     * Attempts to load the MySQL driver and initialize the database.
     */
    public DatabaseManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            initializeDatabase();
        } catch (ClassNotFoundException e) {
            System.out.println("⚠️ MySQL Driver not found! Make sure the mysql-connector-j JAR is in your pom.xml.");
        }
    }

    /**
     * Checks if the database tables exist, and if not, creates them using SQL scripts.
     */
    private void initializeDatabase() {
        // 'try-with-resources' automatically closes the connection when done.
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            if (!tableExists(conn, "movies") || !tableExists(conn, "reviews")) {
                System.out.println("⚠️ Tables not found, running schema.sql...");
                runSqlFile(conn, "database/schema.sql");

                System.out.println("📥 Loading initial data from data.sql...");
                runSqlFile(conn, "database/data.sql");
            }
        } catch (SQLException e) {
            System.out.println("❌ DB Init Error: " + e.getMessage());
        }
    }

    /**
     * A helper method to check if a specific table exists in the database.
     */
    private boolean tableExists(Connection conn, String tableName) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet rs = meta.getTables(null, null, tableName, null)) {
            return rs.next();
        }
    }

    /**
     * A more robust method to read and execute an SQL script file.
     * It reads the entire file and splits statements by semicolons.
     */
    private void runSqlFile(Connection conn, String filePath) {
        try (Statement stmt = conn.createStatement()) {
            // Read the entire file into a single string
            String sql = new String(Files.readAllBytes(Paths.get(filePath)));
            
            // Remove multi-line comments (/* ... */) which can confuse the splitter
            sql = sql.replaceAll("/\\*[\\s\\S]*?\\*/", ""); 

            // Split the string into individual statements by the semicolon
            String[] allStatements = sql.split(";");

            for (String statement : allStatements) {
                // Remove single-line comments and trim whitespace
                String cleanStatement = statement.replaceAll("--.*", "").trim();
                
                if (!cleanStatement.isEmpty()) {
                    stmt.execute(cleanStatement + ";"); // Add the semicolon back
                }
            }
        } catch (IOException | SQLException e) {
            System.out.println("❌ SQL File Error (" + filePath + "): " + e.getMessage());
        }
    }

    // --- Movie Methods ---

    /**
     * Creates a new movie in the 'movies' table.
     * @param title The title of the new movie.
     * @return The newly created Movie object (with its new ID), or null if it failed.
     */
    public Movie createMovie(String title) {
        String query = "INSERT INTO movies (title) VALUES (?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, title);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int newId = rs.getInt(1);
                        System.out.println("✅ Movie created with ID: " + newId);
                        return new Movie(newId, title);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ DB Create Movie Error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all movies from the 'movies' table.
     * @return A list of Movie objects.
     */
    public List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        String query = "SELECT * FROM movies";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                movies.add(new Movie(
                        rs.getInt("movie_id"),
                        rs.getString("title")
                ));
            }
        } catch (SQLException e) {
            System.out.println("❌ DB Read Movies Error: " + e.getMessage());
        }
        return movies;
    }

    // --- Review Methods ---

    /**
     * Inserts a review, now including the movie_id.
     */
    public void insertReview(MovieReview review) {
        String query = "INSERT INTO reviews (movie_id, review, sentiment) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, review.getMovieId());
            stmt.setString(2, review.getText());
            stmt.setString(3, review.getSentiment());
            stmt.executeUpdate();
            System.out.println("✅ Review saved to DB!");
        } catch (SQLException e) {
            System.out.println("❌ DB Insert Error: " + e.getMessage());
        }
    }

    /**
     * Retrieves all reviews for a SPECIFIC movie.
     * @param movieId The ID of the movie to get reviews for.
     * @return A list of MovieReview objects.
     */
    public List<MovieReview> getAllReviews(int movieId) {
        List<MovieReview> reviews = new ArrayList<>();
        String query = "SELECT * FROM reviews WHERE movie_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(new MovieReview(
                            rs.getInt("id"),
                            rs.getInt("movie_id"),
                            rs.getString("review"),
                            rs.getString("sentiment")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ DB Read Reviews Error: " + e.getMessage());
        }
        return reviews;
    }

    /**
     * Updates an existing review by its unique ID.
     */
    public void updateReview(int id, String newText, String newSentiment) {
        String query = "UPDATE reviews SET review=?, sentiment=? WHERE id=?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newText);
            stmt.setString(2, newSentiment);
            stmt.setInt(3, id);
            stmt.executeUpdate();
            System.out.println("✅ Review updated!");
        } catch (SQLException e) {
            System.out.println("❌ DB Update Error: " + e.getMessage());
        }
    }

    /**
     * Deletes an existing review by its unique ID.
     */
    public void deleteReview(int id) {
        String query = "DELETE FROM reviews WHERE id=?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("✅ Review deleted!");
        } catch (SQLException e) {
            System.out.println("❌ DB Delete Error: " + e.getMessage());
        }
    }
}