package com.morax.sentiment;

public class Main {
public static void main(String[] args) {

    System.out.println("🎬 Welcome to Movie Sentiment Analyzer!");

    DatabaseManager dbManager = new DatabaseManager();

    String modelPath = "ml_model/sentiment_model.onnx";
    String tokenizerPath = "ml_model/sentiment_transformer_tokenizer";

    PredictionService predictor = new PredictionService(modelPath, tokenizerPath);

    UserInterface ui = new UserInterface(predictor, dbManager);
    ui.start();
}


}