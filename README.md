# Java Movie Sentiment AnalyzerThis project is a complete end-to-end application that performs sentiment analysis on movie reviews. It features a custom-trained deep learning model built with Python and a robust Java application for inference and data management.A high-level overview of the project architecture, showing the Python training pipeline and the Java inference application.FeaturesCustom Machine Learning Model: A DistilBERT transformer model was fine-tuned on the IMDB 50k movie review dataset using Python, PyTorch, and the Hugging Face transformers library.Stable Model Export: The trained PyTorch model is exported to the stable ONNX (Open Neural Network Exchange) format for cross-platform compatibility.Java-based Inference: A pure Java application loads the .onnx model using the Microsoft ONNX Runtime, ensuring high-performance inference without a Python dependency.Accurate Tokenization: Utilizes the Deep Java Library (DJL) implementation of the Hugging Face tokenizer to perfectly replicate the text processing pipeline from Python.Database Integration: Features full CRUD (Create, Read, Update, Delete) functionality, storing movie reviews and their predicted sentiments in a MySQL database.Interactive CLI: A user-friendly command-line interface allows for easy interaction with the application.Object-Oriented Design: The Java code is structured using core OOP principles for maintainability and clarity.Technology StackMachine Learning (Python):Python 3.11PyTorchHugging Face transformers & tokenizersONNXScikit-learn, Pandas, NumPyInference Application (Java):Java 17Maven (for dependency management)Microsoft ONNX RuntimeDeep Java Library (DJL) for TokenizationMySQL Connector/J (for JDBC)Database:MySQLHow To Set Up and RunPrerequisitesGitJava 17 (or higher)Apache MavenPython 3.11A running MySQL server instance1. Clone the Repositorygit clone [https://github.com/YourUsername/java-movie-sentiment-analyzer.git](https://github.com/YourUsername/java-movie-sentiment-analyzer.git)
cd java-movie-sentiment-analyzer
2. Set up the Python Environment (for re-training)These steps are only necessary if you wish to re-train the model yourself. The required .onnx model is already included in the ml_model/ directory.# Navigate to the model directory
cd ml_model

# Create and activate a virtual environment
python3.11 -m venv myenv
source myenv/bin/activate

# Install dependencies
pip install -r requirements.txt

# Run the training script (this will generate sentiment_model_ts.pt)
python3 train_model.py

# Run the export script (this will generate sentiment_model.onnx)
python3 export_onnx.py
3. Set up the MySQL DatabaseEnsure your MySQL server is running.Connect to your MySQL instance and run the following commands to create the database and a dedicated user. (You can also use the root user if you prefer).CREATE DATABASE IF NOT EXISTS movies;
CREATE USER IF NOT EXISTS 'morax'@'localhost' IDENTIFIED BY 'morax123';
GRANT ALL PRIVILEGES ON movies.* TO 'morax'@'localhost';
FLUSH PRIVILEGES;
Note: The Java application will automatically create the required reviews table and seed it with initial data if it doesn't exist.4. Build and Run the Java ApplicationFrom the project's root directory (java-movie-sentiment-analyzer/):Build the project:This command will download all the required Java libraries.mvn clean install
Run the application:mvn exec:java
The interactive menu will appear in your terminal, and you can start analyzing movie reviews!
