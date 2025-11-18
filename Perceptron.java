import java.util.Arrays;

public class Perceptron {
    
    // sigmoid activation function
    public static double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }
    
    // derivative of sigmoid function
    public static double sigmoidDerivative(double x) {
        return x * (1 - x);
    }
    
    // class for define a simple perceptron including the code for prediction and training
    public static class simplePerceptron {
        private double[] weights;
        private double bias;
        private double learningRate = 0.1;
        
        public simplePerceptron() {
            // initialize weights and bias randomly
            weights = new double[3];
            for (int i = 0; i < weights.length; i++) {
                weights[i] = Math.random() * 2 - 1; // random values between -1 and 1
            }
            bias = Math.random() * 2 - 1;
        }
        
        public double predict(double[] inputs) {
            // calculate weighted sum
            double weightedSum = bias;
            for (int i = 0; i < weights.length; i++) {
                weightedSum += weights[i] * inputs[i];
            }
            
            // apply activation function
            return sigmoid(weightedSum);
        }
        
        public void train(double[][] trainingInputs, double[][] trainingOutputs, int epochs) {
            for (int epoch = 0; epoch < epochs; epoch++) {
                double epochError = 0.0; // accumulate error for this epoch

                for (int i = 0; i < trainingInputs.length; i++) {
                    // forward propogation
                    double prediction = predict(trainingInputs[i]);
                    double error = trainingOutputs[i][0] - prediction;  // y-prediction
                    double delta = error * sigmoidDerivative(prediction);
                    
                    // backward pass, update weights and bias
                    for (int j = 0; j < weights.length; j++) {
                        weights[j] += trainingInputs[i][j] * delta * learningRate;
                    }
                    bias += delta * learningRate;

                    // accumulate squared error for this sample
                    epochError += 0.5 * error * error; // MSE for one sample
                }

                // calculate average error for this epoch
                double avgEpochError = epochError / trainingInputs.length;
                
                // print error at specific epochs
                if ((epoch + 1) % 100 == 0 || epoch == 0) {
                    System.out.printf("Epoch %4d: Average Training Error = %.6f%n", 
                                    epoch, avgEpochError);
                }
            }
        }
        
        public double[] getWeights() {
            return weights;
        }
        
        public double getBias() {
            return bias;
        }
    }
    
    public static class Train_Test {
        public static void trainAndTest() {
            // define training data
            double[][] inputs = new double[][]{
                {0, 0, 1}, // instance 1
                {1, 1, 1}, // instance 2
                {1, 0, 1}, // instance 3
                {0, 1, 1}  // instance 4
            };
            
            double[][] outputs = new double[][]{
                {0}, // Fake
                {1}, // Real
                {1}, // Real
                {0}  // Fake
            };
            
            // create and train perceptron
            simplePerceptron perceptron = new simplePerceptron();
            System.out.println("Initial weights: " + Arrays.toString(perceptron.getWeights()));
            System.out.println("Initial bias: " + perceptron.getBias());
            
            // train for 1000 epochs
            perceptron.train(inputs, outputs, 1000);
            
            System.out.println("\nAfter training:");
            System.out.println("Final weights: " + Arrays.toString(perceptron.getWeights()));
            System.out.println("Final bias: " + perceptron.getBias());
            
            // test the trained perceptron
            double[] testInstance = {0, 0, 0}; // instance 5: Off, Off, Off
            double prediction = perceptron.predict(testInstance);
            
            System.out.println("\nTesting new instance [0, 0, 0]:");
            System.out.println("Raw output: " + prediction);
            System.out.println("Classification: " + (prediction > 0.5 ? "Real" : "Fake"));
        }
    }
    
    public static void main(String[] args) {
        Train_Test.trainAndTest();
    }
}