package com.morax.sentiment;

/**
 * A simple POJO class to represent a Movie.
 * This class demonstrates Encapsulation.
 */
public class Movie {
    private int id;
    private String title;

    // Constructor for creating a Movie object from database data
    public Movie(int id, String title) {
        this.id = id;
        this.title = title;
    }

    // --- Getters ---
    
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    /**
     * Overriding the toString() method to provide a clean,
     * user-friendly string representation for menus.
     */
    @Override
    public String toString() {
        // Example output: "1. Inception"
        return id + ". " + title;
    }
}