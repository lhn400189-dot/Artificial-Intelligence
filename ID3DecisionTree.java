import java.io.*;
import java.util.*;

public class ID3DecisionTree {

    // tree node class to represent each decision node or leaf
    static class Node {
        String attribute;  // the attribute this node splits on
        Map<String, Node> children = new HashMap<>();  // children nodes for each attribute value
        String label = null; // if leaf node

        Node(String attr) {
            this.attribute = attr;
        }
    }

    static List<String[]> dataset = new ArrayList<>();  // stores the dataset rows
    static List<String> attributes = new ArrayList<>();  // stores attribute names

    // check if all records have the same class label
    static boolean isPure(List<String[]> data) {
        String first = data.get(0)[data.get(0).length - 1];
        for (String[] row : data) {  // iterate through each record
            if (!row[row.length - 1].equals(first)) return false;
        }
        return true;
    }

    static String majorityClass(List<String[]> data) {
        Map<String, Integer> count = new HashMap<>();
        for (String[] row : data) {
            String label = row[row.length - 1];
            count.put(label, count.getOrDefault(label, 0) + 1);
        }

        String majority = null;
        int maxCount = -1;
        for (Map.Entry<String, Integer> entry : count.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                majority = entry.getKey();
            }
        }
        return majority;  // class that has the biggest count
    }

    // choose the attribute with the highest information gain
    static int chooseBestAttribute(List<String[]> data, List<Integer> attrIndices) {
        double baseEntropy = entropy(data);  // compute entropy before split
        double bestGain = -1;
        int bestAttr = -1;

        for (int i : attrIndices) {
            double newEntropy = 0.0;
            Map<String, List<String[]>> partitions = partitionByAttribute(data, i);
            for (List<String[]> subset : partitions.values()) {
                newEntropy += ((double) subset.size() / data.size()) * entropy(subset);  // weighted sum of entropies of each partition
            }
            double infoGain = baseEntropy - newEntropy;  // calculate the information gain
            if (infoGain > bestGain) {
                bestGain = infoGain;
                bestAttr = i;
            }
        }
        return bestAttr;
    }

    // compute the entropy of the data
    static double entropy(List<String[]> data) {
        Map<String, Integer> labelCount = new HashMap<>();
        for (String[] row : data) {
            String label = row[row.length - 1];
            labelCount.put(label, labelCount.getOrDefault(label, 0) + 1);
        }
        double entropy = 0.0;
        for (int count : labelCount.values()) {
            double p = (double) count / data.size();
            entropy -= p * (Math.log(p) / Math.log(2));  // entropy formula
        }
        return entropy;
    }

    // partition the data based on attribute value
    static Map<String, List<String[]>> partitionByAttribute(List<String[]> data, int attrIndex) {
        Map<String, List<String[]>> partitions = new HashMap<>();
        for (String[] row : data) {
            String key = row[attrIndex];
            partitions.putIfAbsent(key, new ArrayList<>());  // ensure that there is a corresponding value for the attribute in the set.
            partitions.get(key).add(row);  // add the record
        }
        return partitions;
    }

    // recursively print the decision tree
    static void printTree(Node node, String indent) {
        if (node.label != null) {  // leaf node
            System.out.println(indent + "-> " + node.label);
            return;
        }
        for (Map.Entry<String, Node> entry : node.children.entrySet()) {
            System.out.println(indent + node.attribute + " = " + entry.getKey());
            printTree(entry.getValue(), indent + "   ");  // recursive print with indentation
        }
    }

    // recursive function to build the decision tree
    static Node buildTree(List<String[]> data, List<Integer> attrIndices) {
        String majorityLabel = majorityClass(data);  // get majority class
        if (isPure(data)) {  // all records belong to the same class
            Node leaf = new Node(null);
            leaf.label = data.get(0)[data.get(0).length - 1];
            return leaf;
        }
        if (attrIndices.isEmpty()) {  // no more attributes to split
            Node leaf = new Node(null);
            leaf.label = majorityLabel;
            return leaf;
        }

        int bestAttr = chooseBestAttribute(data, attrIndices);  // choose attribute with highest infomation gain
        Node node = new Node(attributes.get(bestAttr));  // create a decision node

        Map<String, List<String[]>> partitions = partitionByAttribute(data, bestAttr);  // split the data by attribute values
        for (Map.Entry<String, List<String[]>> entry : partitions.entrySet()) {
            List<Integer> newAttrIndices = new ArrayList<>(attrIndices);
            newAttrIndices.remove((Integer) bestAttr);  // exclude used attribute            
            node.children.put(entry.getKey(), buildTree(entry.getValue(), newAttrIndices));  // recursive call
        }
        return node;
    }

    public static void main(String[] args) throws IOException {
        // read the CSV file
        String filename = "student_grades.csv";
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String headerLine = br.readLine();  // read attribute names
        attributes.addAll(Arrays.asList(headerLine.split(",")));

        String line;
        while ((line = br.readLine()) != null) {
            dataset.add(line.split(","));  // read each row into dataset
        }
        br.close();

        List<Integer> attrIndices = new ArrayList<>();
        for (int i = 0; i < attributes.size() - 1; i++) attrIndices.add(i);  // list of attribute indices, except label

        Node root = buildTree(dataset, attrIndices);
        printTree(root, "");
    }
}
