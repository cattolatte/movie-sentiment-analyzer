package com.morax.sentiment;

import java.util.List;
import java.util.Scanner;

class InvalidInputException extends Exception {
    public InvalidInputException(String message) {
        super(message);
    }
}

public class UserInterface {
    private final PredictionService predictor;
    private final DatabaseManager dbManager;
    private final Scanner scanner; // Scanner is now a class member to be used by all methods

    public UserInterface(PredictionService predictor, DatabaseManager dbManager) {
        this.predictor = predictor;
        this.dbManager = dbManager;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Main application loop. Shows the high-level movie management menu.
     */
    public void start() {
        while (true) {
            System.out.println("\n===== 🎬 Movie Sentiment Analyzer =====");
            System.out.println("1. Select a Movie to manage");
            System.out.println("2. Create a New Movie");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");

            try {
                String input = scanner.nextLine();
                switch (input) {
                    case "1" -> selectMovieMenu();
                    case "2" -> createNewMovie();
                    case "3" -> {
                        System.out.println("👋 Exiting... Goodbye!");
                        scanner.close(); // Close the scanner when exiting
                        return;
                    }
                    default -> throw new InvalidInputException("Invalid menu option!");
                }
            } catch (InvalidInputException e) {
                System.out.println("⚠️ Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("⚠️ Unexpected Error: " + e.getMessage());
            }
        }
    }

    /**
     * Menu for selecting an existing movie.
     */
    private void selectMovieMenu() throws InvalidInputException {
        System.out.println("\n--- Select a Movie ---");
        List<Movie> movies = dbManager.getAllMovies();
        if (movies.isEmpty()) {
            System.out.println("No movies found. Please create a new movie first.");
            return;
        }

        for (Movie movie : movies) {
            System.out.println(movie); // Uses the Movie.toString() method (e.g., "1. Inception")
        }
        System.out.print("Enter Movie ID to select (or 'B' to go back): ");
        String input = scanner.nextLine();

        if (input.equalsIgnoreCase("B")) {
            return;
        }

        try {
            int movieId = Integer.parseInt(input);
            // Check if the selected ID is in the list
            Movie selectedMovie = movies.stream()
                    .filter(movie -> movie.getId() == movieId)
                    .findFirst()
                    .orElse(null);

            if (selectedMovie != null) {
                // If the movie is found, show the review menu for it
                showReviewMenu(selectedMovie);
            } else {
                throw new InvalidInputException("Movie ID not found.");
            }
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Invalid input. Please enter a number.");
        }
    }

    /**
     * Logic for creating a new movie.
     */
    private void createNewMovie() {
        System.out.print("\nEnter new movie title: ");
        String title = scanner.nextLine();
        if (title.trim().isEmpty()) {
            System.out.println("Title cannot be empty.");
            return;
        }

        Movie newMovie = dbManager.createMovie(title);
        if (newMovie != null) {
            System.out.println("Successfully created: " + newMovie);
            // After creating, go directly to the review menu for this new movie
            showReviewMenu(newMovie);
        } else {
            System.out.println("Failed to create movie (it may already exist).");
        }
    }

    /**
     * Shows the sub-menu for managing reviews for a specific movie.
     */
    private void showReviewMenu(Movie movie) {
        while (true) {
            System.out.println("\n--- Managing Reviews for: " + movie.getTitle() + " ---");
            System.out.println("1. Analyze New Review");
            System.out.println("2. View All Reviews");
            System.out.println("3. Update a Review");
            System.out.println("4. Delete a Review");
            System.out.println("5. Back to Movie List");
            System.out.print("Choose option: ");

            try {
                String input = scanner.nextLine();
                switch (input) {
                    case "1" -> analyzeReview(movie.getId());
                    case "2" -> viewReviews(movie.getId());
                    case "3" -> updateReview(); // Update/Delete don't need movie ID, they use review ID
                    case "4" -> deleteReview();
                    case "5" -> {
                        return; // Go back to the main movie menu
                    }
                    default -> throw new InvalidInputException("Invalid menu option!");
                }
            } catch (InvalidInputException e) {
                System.out.println("⚠️ Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("⚠️ Unexpected Error: " + e.getMessage());
            }
        }
    }

    /**
     * Analyzes a new review for a specific movie.
     * @param movieId The movie this review belongs to.
     */
    private void analyzeReview(int movieId) {
        System.out.print("Enter review: ");
        String text = scanner.nextLine();
        
        // Get sentiment from the prediction service
        String sentiment = predictor.predictSentiment(text);
        
        // Create the new review object with the movie ID
        MovieReview review = new MovieReview(movieId, text, sentiment);
        
        // Save it to the database
        dbManager.insertReview(review);
        System.out.println("✅ Prediction: " + sentiment);
    }

    /**
     * Views all reviews for a specific movie.
     * @param movieId The movie to get reviews for.
     */
    private void viewReviews(int movieId) {
        List<MovieReview> reviews = dbManager.getAllReviews(movieId);
        if (reviews.isEmpty()) {
            System.out.println("No reviews found for this movie.");
        } else {
            System.out.println("\n--- Reviews for: " + movieId + " ---"); // A bit redundant, but clear
            for (MovieReview r : reviews) {
                // Uses the updated MovieReview.toString() to show the Review ID
                System.out.println(r);
            }
        }
    }

    /**
     * Updates an existing review. This works by the review's unique 'id',
     * so it doesn't need to know about the movie.
     */
    private void updateReview() throws InvalidInputException {
        System.out.print("Enter Review ID to update (check 'View All Reviews' for ID): ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Invalid ID. Please enter a number.");
        }

        System.out.print("New text: ");
        String text = scanner.nextLine();

        // Re-analyze the sentiment for the new text
        String sentiment = predictor.predictSentiment(text);
        dbManager.updateReview(id, text, sentiment);
    }

    /**
     * Deletes an existing review by its unique 'id'.
     */
    private void deleteReview() throws InvalidInputException {
        System.out.print("Enter Review ID to delete (check 'View All Reviews' for ID): ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Invalid ID. Please enter a number.");
        }
        dbManager.deleteReview(id);
    }
}