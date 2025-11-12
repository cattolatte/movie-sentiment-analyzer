package com.morax.sentiment;

public class MovieReview {
    private int id;         // The review's unique ID from the database
    private int movieId;    // The movie this review belongs to
    private String text;
    private String sentiment;

    /**
     * Constructor for creating a NEW review before saving to the DB
     * (the DB will auto-generate the 'id')
     */
    public MovieReview(int movieId, String text, String sentiment) {
        this.movieId = movieId;
        this.text = text;
        this.sentiment = sentiment;
    }

    /**
     * Constructor for creating a review object from data FETCHED from the DB
     */
    public MovieReview(int id, int movieId, String text, String sentiment) {
        this.id = id;
        this.movieId = movieId;
        this.text = text;
        this.sentiment = sentiment;
    }

    // --- Getters ---
    
    public int getId() {
        return id;
    }
    
    public int getMovieId() {
        return movieId;
    }

    public String getText() {
        return text;
    }

    public String getSentiment() {
        return sentiment;
    }

    // --- Setters ---
    
    public void setText(String text) {
        this.text = text;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    /**
     * Updated toString() to show the review ID, which is needed
     * for the Update and Delete operations.
     */
    @Override
    public String toString() {
        // Example output: "Review [ID: 3] | Sentiment: Positive | Review: "This was great!""
        return "Review [ID: " + id + "] | Sentiment: " + sentiment + " | Review: \"" + text + "\"";
    }
}