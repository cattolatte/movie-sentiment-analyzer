package com.morax.sentiment;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import ai.djl.huggingface.tokenizers.Encoding;
import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class PredictionService {

private final OrtEnvironment env;
private final OrtSession session;
private final HuggingFaceTokenizer tokenizer;

public PredictionService(String modelPath, String tokenizerPath) {
    try {
        this.env = OrtEnvironment.getEnvironment();
        this.session = env.createSession(modelPath, new OrtSession.SessionOptions());
        
        Map<String, String> options = new HashMap<>();
        options.put("maxLength", "128");
        options.put("padding", "true");
        options.put("truncation", "true");
        this.tokenizer = HuggingFaceTokenizer.newInstance(Paths.get(tokenizerPath), options);

        System.out.println("✅ ONNX Model and Tokenizer loaded successfully.");
        System.out.println("Model Input Names: " + session.getInputNames());

    } catch (Exception e) {
        throw new RuntimeException("Failed to initialize PredictionService", e);
    }
}

public String predictSentiment(String text) {
    try {
        Encoding encoding = tokenizer.encode(text);
        long[] inputIds = encoding.getIds();
        long[] attentionMask = encoding.getAttentionMask();

        long[][] inputIds2D = { inputIds };
        long[][] attentionMask2D = { attentionMask };

        OnnxTensor inputIdsTensor = OnnxTensor.createTensor(env, inputIds2D);
        OnnxTensor attentionMaskTensor = OnnxTensor.createTensor(env, attentionMask2D);

        Map<String, OnnxTensor> inputs = new HashMap<>();
        inputs.put("input_ids", inputIdsTensor);
        inputs.put("attention_mask", attentionMaskTensor);

        try (OrtSession.Result results = session.run(inputs)) {
            float[][] outputLogits = (float[][]) results.get(0).getValue();
            
            float negativeLogit = outputLogits[0][0];
            float positiveLogit = outputLogits[0][1];

            return (positiveLogit > negativeLogit) ? "Positive" : "Negative";
        }
    } catch (OrtException e) {
        System.err.println("❌ Prediction error: " + e.getMessage());
        e.printStackTrace();
        return "Error";
    }
}


}