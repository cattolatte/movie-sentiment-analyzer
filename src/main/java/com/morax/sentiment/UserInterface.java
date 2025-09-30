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

    public UserInterface(PredictionService predictor, DatabaseManager dbManager) {
        this.predictor = predictor;
        this.dbManager = dbManager;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== 🎬 Movie Sentiment Analyzer =====");
            System.out.println("1. Analyze Review");
            System.out.println("2. View All Reviews");
            System.out.println("3. Update Review");
            System.out.println("4. Delete Review");
            System.out.println("5. Exit");
            System.out.print("Choose option: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> analyzeReview(scanner);
                    case 2 -> viewReviews();
                    case 3 -> updateReview(scanner);
                    case 4 -> deleteReview(scanner);
                    case 5 -> {
                        System.out.println("👋 Exiting... Goodbye!");
                        return;
                    }
                    default -> throw new InvalidInputException("Invalid menu option!");
                }
            } catch (InvalidInputException e) {
                System.out.println("⚠️ Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("⚠️ Unexpected Error: " + e.getMessage());
                scanner.nextLine();
            }
        }
    }

    private void analyzeReview(Scanner scanner) {
        System.out.print("Enter review: ");
        String text = scanner.nextLine();
        String sentiment = predictor.predictSentiment(text);
        MovieReview review = new MovieReview(text, sentiment);
        dbManager.insertReview(review);
        System.out.println("✅ Prediction: " + sentiment);
    }

    private void viewReviews() {
        List<MovieReview> reviews = dbManager.getAllReviews();
        if (reviews.isEmpty()) {
            System.out.println("No reviews found!");
        } else {
            for (MovieReview r : reviews) {
                System.out.println(r);
            }
        }
    }

    private void updateReview(Scanner scanner) {
        System.out.print("Enter review ID to update: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("New text: ");
        String text = scanner.nextLine();

        String sentiment = predictor.predictSentiment(text);
        dbManager.updateReview(id, text, sentiment);
    }

    private void deleteReview(Scanner scanner) {
        System.out.print("Enter review ID to delete: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        dbManager.deleteReview(id);
    }
}
