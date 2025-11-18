import java.util.*;

public class MyClass {
    public static void main(String[] args) {
       int [][] cost_matrix = {
               {0,0,0,6,1,0,0,0,0,0},
               {5,0,2,0,0,0,0,0,0,0},
               {9,3,0,0,0,0,0,0,0,0},
               {0,0,1,0,2,0,0,0,0,0},
               {6,0,0,0,0,2,0,0,0,0},
               {0,0,0,7,0,0,0,0,0,0},
               {0,0,0,0,2,0,0,0,0,0},
               {0,9,0,0,0,0,0,0,0,0},
               {0,0,0,5,0,0,0,0,0,0},
               {0,0,0,0,0,8,7,0,0,0}
       };

       int [] heuristic_vector = {5,7,3,4,6,8,5,0,0,0};

        // Identify the Goal States and save them in a new vector
        // Collect all goal state indices where heuristic == 0
        List<Integer> goalStates = new ArrayList<>();
        for (int i = 0; i < heuristic_vector.length; i++) {
            if (heuristic_vector[i] == 0) {
                goalStates.add(i);
            }
        }

        // Node names for printing
        String[] names = {"A", "B", "C", "D", "E", "H", "J", "G1", "G2", "G3"};
        int start = 0;
        int minCost = Integer.MAX_VALUE;
        String bestGoal = " ";

        // Run A* for each goal and track the cheapest path
        for (int goal : goalStates) {
            System.out.println("\nSearching from A to " + names[goal]);
            int cost = aStar(cost_matrix, heuristic_vector, start, goal, names);
            if (cost < minCost) {
                minCost = cost;
                bestGoal = names[goal];
            }
        }

        System.out.println("\nThe cheapest path is to " + bestGoal + " with total cost: " + minCost);

    }

    /**
     * Node class for A* search
     * index: node identifier
     * costFromStart: g(n), cost from start to this node
     * estimatedTotalCost: f(n)=g(n)+h(n), used for priority ordering
     * parent: reference to reconstruct the final path
     */
    static class Node implements Comparable<Node> {
        int index;
        int costFromStart;
        int estimatedTotalCost;
        Node parent;

        public Node(int index, int costFromStart, int estimatedTotalCost, Node parent) {
            this.index = index;
            this.costFromStart = costFromStart;
            this.estimatedTotalCost = estimatedTotalCost;
            this.parent = parent;
        }

        public int compareTo (Node other) {
            // Compare by f(n) value for priority queue
            return this.estimatedTotalCost - other.estimatedTotalCost;
        }
    }

    /**
     * Print the path by backtracking from goal node to start
     */
    public static void printPath(Node node, String[] names) {
        List<String> path = new ArrayList<>();
        while (node != null) {
            path.add(names[node.index]);
            node = node.parent;
        }
        Collections.reverse(path);
        System.out.println("Path: " + String.join("-> ", path));
    }

    /**
     * A* search implementation
     * @param graph adjacency matrix (edges may be stored in reverse rows)
     * @param heuristic heuristic estimates h(n)
     * @param start start node index
     * @param goal goal node index
     * @param names node name array for printing
     * @return cost of the optimal path, or Integer.MAX_VALUE if unreachable
     */
    public static int aStar(int[][] graph, int[] heuristic, int start, int goal, String[] names) {
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        boolean[] visited = new boolean[graph.length];

        // Initialize: add start node with g(start)=0, f(start)=h(start)
        openSet.add(new Node(start, 0, heuristic[start], null));
        int cycles = 0;

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            cycles++;

            if (visited[current.index]) continue;
            visited[current.index] = true;

            // If goal is reached, print stats and return cost
            if (current.index == goal) {
                System.out.println("Reached goal: " + names[goal]);
                System.out.println("Total cost: " + current.costFromStart);
                System.out.println("Cycles: " + cycles);
                printPath(current,names);
                return current.costFromStart;
            }

            // Explore neighbors using reversed storage: graph[i][current] = cost of current->i
            for (int i = 0; i < graph.length; i++) {
                int edgeCost = graph[i][current.index];
                if (edgeCost > 0 && !visited[i]) {
                    int g = current.costFromStart + edgeCost;
                    int f = g + heuristic[i];
                    openSet.add(new Node(i, g, f, current));
                }
            }

        }
        // Return MAX if goal is not reachable
        return Integer.MAX_VALUE;
    }
}
