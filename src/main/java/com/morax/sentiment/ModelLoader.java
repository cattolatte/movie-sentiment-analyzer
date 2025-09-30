package com.morax.sentiment;

abstract class BaseModelLoader {
    protected String modelPath;

    public BaseModelLoader(String modelPath) {
        this.modelPath = modelPath;
    }

    public abstract void loadModel();

    public String getModelPath() {
        return modelPath;
    }
}

public class ModelLoader extends BaseModelLoader {
    private boolean loaded;

    public ModelLoader(String modelPath) {
        super(modelPath);
        this.loaded = false;
    }

    @Override
    public void loadModel() {
        System.out.println("📂 Loading ONNX model from: " + modelPath);
        loaded = true;
    }

    // Overloading
    public boolean isLoaded() {
        return loaded;
    }

    public boolean isLoaded(boolean verbose) {
        if (verbose) {
            System.out.println("Model loaded status: " + loaded);
        }
        return loaded;
    }

    // Inner class
    public static class ModelInfo {
        private final String name;
        private final String format;

        public ModelInfo(String name, String format) {
            this.name = name;
            this.format = format;
        }

        @Override
        public String toString() {
            return "ModelInfo{name='" + name + "', format='" + format + "'}";
        }
    }
}