import java.util.Arrays;
import java.util.Random;

public class HillClimbingColoring {

    // All available colors: b = blue, r = red, o = orange, j = jungle
    static final String[] ALL_COLORS = {"b","r","o","j"};
    static final int REGION_COUNT = 13;

    // Will be initialized based on the value of k
    static String[] COLORS;

    // Adjacency matrix: 1 indicates that two regions are adjacent
    static final int[][] ADJ_MATRIX = {
            {1,1,0,0,0,0,0,0,0,0,0,1,1},
            {1,1,1,0,0,0,0,0,0,0,0,1,0},
            {0,1,1,1,0,0,0,0,0,0,0,1,0},
            {0,0,1,1,1,0,0,0,0,0,1,0,0},
            {0,0,0,1,1,1,0,0,0,0,0,0,0},
            {0,0,0,0,1,1,1,0,0,1,0,0,0},
            {0,0,0,0,0,1,1,1,1,0,0,0,0},
            {0,0,0,0,0,0,1,1,1,0,0,0,0},
            {0,0,0,0,0,0,1,1,1,0,0,0,0},
            {0,0,0,0,0,1,0,0,0,1,0,0,0},
            {0,0,0,1,0,0,0,0,0,0,1,1,0},
            {1,1,1,0,0,0,0,0,0,0,1,1,1},
            {1,0,0,0,0,0,0,0,0,0,0,1,1},
    };

    // Check if a state is the goal
    static boolean isGoalState(String[] state) {
        return computeHeuristic(state) == 0;
    }

    // Heuristic function: count the number of adjacent regions with the same color
    static int computeHeuristic(String[] state) {
        int h = 0;
        for (int i = 0; i < REGION_COUNT; i++) {
            for (int j = i+1; j < REGION_COUNT; j++) {
                if (ADJ_MATRIX[i][j] == 1 && state[i].equals(state[j])){
                    h++;
                }
            }
        }
        return h;
    }

    // Cost function: sum the cost of all regions based on their colors
    static int computeCost(String[] state) {
        int cost = 0;
        for (String c : state) {
            switch (c) {
                case "b": cost += 1; break;
                case "r": cost += 2; break;
                case "o": cost += 3; break;
                case "j": cost += 5; break;
            }
        }
        return cost;
    }

    // Hill-climbing algorithm to reduce conflicts by changing one color at a time
    static String[]hillClimb(String[] initialState) {
        String[] current = Arrays.copyOf(initialState,REGION_COUNT);
        int currentH = computeHeuristic(current);

        while (true) {
            String[] bestNeighbor = null;
            int bestH = currentH;

            // Try changing each region to a different color
            for (int i = 0; i < REGION_COUNT; i++) {
                for (String color : COLORS) {
                    if (!color.equals(current[i])){
                        String[] neighbor = Arrays.copyOf(current,REGION_COUNT);
                        neighbor[i] = color;
                        int h = computeHeuristic(neighbor);
                        if (h < bestH) {
                            bestH = h;
                            bestNeighbor = neighbor;
                        }
                    }
                }
            }

            // Stop if no improvement
            if (bestNeighbor == null || bestH >= currentH) break;

            current = bestNeighbor;
            currentH = bestH;
        }

        return current;
    }

    // Generate a random initial state using k colors
    static String[] generateRandomState(int k) {
        String[] state = new String[REGION_COUNT];
        Random rand = new Random();
        for (int i = 0; i < REGION_COUNT; i++) {
            state[i] = COLORS[rand.nextInt(k)];
        }
        return state;
    }

    public static void main(String[] args) {
        // Read k (number of colors) from command line argument
        if (args.length < 1) {
            System.err.println("Usage: java HillClimbingColoring <k>");
            return;
        }

        int k;

        try {
            k = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("k must be an integer.");
            return;
        }

        if (k < 1 || k > ALL_COLORS.length) {
            System.err.println("Please enter a k value between 1 and " + ALL_COLORS.length);
            return;
        }

        COLORS = Arrays.copyOf(ALL_COLORS, k);

        // Multiple restarts to avoid getting stuck in local minima
        int numRestarts = 100;
        String[] bestState = null;
        int bestH = Integer.MAX_VALUE;

        for (int i = 0; i < numRestarts; i++) {
            String[] state = generateRandomState(k);
            String[] result = hillClimb(state);
            int h = computeHeuristic(result);
            if (h < bestH) {
                bestH = h;
                bestState = result;
            }
            // stop early if solution found
            if (h == 0) break;
        }


        // Output the best result found
        System.out.println("\n=== Best Result After " + numRestarts + " Restarts ===");
        String[] regions = {"BC","AB","SK","MB","ON","QC","NB","NS","PEI","NL","NU","NT","YT"};
        for (int i = 0; i < REGION_COUNT; i++) {
            System.out.printf("%s: %s%n", regions[i], bestState[i]);
        }
        System.out.println("Final h(S): " + computeHeuristic(bestState));
        System.out.println("Total cost: " + computeCost(bestState));
        System.out.println("Is goal state: " + isGoalState(bestState));
    }
}
