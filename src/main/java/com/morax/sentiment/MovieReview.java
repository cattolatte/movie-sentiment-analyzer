package com.morax.sentiment;

public class MovieReview {
    private String text;
    private String sentiment;

    public MovieReview(String text, String sentiment) {
        this.text = text;
        this.sentiment = sentiment;
    }

    public String getText() {
        return text;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    @Override
    public String toString() {
        return "Review: \"" + text + "\" | Sentiment: " + sentiment;
    }
}