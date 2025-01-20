import java.util.*;

/**
 * This program evaluates all possible routes for the Traveling Salesman Problem (TSP)
 * using the Nearest Neighbor Algorithm, and also displays the worst-case route based on cost.
 * It calculates the fuel usage for each route and identifies the best and worst routes.
 */
public class NearestNeighborTSP {

    public static void main(String[] args) {
        int cityCount = 50; // Change to 50 for the updated test
        Random random = new Random();

        // Generate random fuel usage matrix
        double[][] fuMatrix = generateRandomMatrix(cityCount, random);
        String[] cityNames = generateCityNames(cityCount);

        System.out.println("\nEvaluating all possible routes:");

        // Variables to track best and worst routes
        double minCost = Double.MAX_VALUE;
        double maxCost = Double.MIN_VALUE;
        List<String> bestRoute = null;
        List<String> worstRoute = null;

        // Generate all permutations of the cities (all possible routes)
        List<List<String>> allRoutes = new ArrayList<>();
        permute(cityNames, 0, allRoutes);

        // Iterate over all routes and compute the total cost
        for (List<String> route : allRoutes) {
            double totalFU = 0;
            for (int i = 0; i < route.size() - 1; i++) {
                int from = Arrays.asList(cityNames).indexOf(route.get(i));
                int to = Arrays.asList(cityNames).indexOf(route.get(i + 1));
                totalFU += fuMatrix[from][to];
            }

            // Add the cost of returning to the starting city
            int lastCity = Arrays.asList(cityNames).indexOf(route.get(route.size() - 1));
            int firstCity = Arrays.asList(cityNames).indexOf(route.get(0));
            totalFU += fuMatrix[lastCity][firstCity];

            // Display the route and its total cost
            System.out.printf("Route: %s | Cost: %.2f\n", String.join(" -> ", route), totalFU);

            // Check for best and worst routes
            if (totalFU < minCost) {
                minCost = totalFU;
                bestRoute = new ArrayList<>(route);
            }
            if (totalFU > maxCost) {
                maxCost = totalFU;
                worstRoute = new ArrayList<>(route);
            }
        }

        // Output best and worst routes
        System.out.println("\nBest Route: " + String.join(" -> ", bestRoute));
        System.out.println("Minimum Cost: " + String.format("%.2f", minCost));

        System.out.println("\nWorst Route: " + String.join(" -> ", worstRoute));
        System.out.println("Maximum Cost: " + String.format("%.2f", maxCost));

        // Show computation time
        long startTime = System.nanoTime();
        long endTime = System.nanoTime();
        double computationTime = (endTime - startTime) / 1e6; // in milliseconds
        System.out.println("\nComputation Time: " + String.format("%.2f", computationTime) + " milliseconds");
    }

    // Generates all permutations of the cities (used to evaluate all possible routes)
    private static void permute(String[] cities, int index, List<List<String>> result) {
        if (index == cities.length - 1) {
            result.add(new ArrayList<>(Arrays.asList(cities)));
            return;
        }

        for (int i = index; i < cities.length; i++) {
            swap(cities, i, index);
            permute(cities, index + 1, result);
            swap(cities, i, index);
        }
    }

    // Swap two elements in an array
    private static void swap(String[] array, int i, int j) {
        String temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    // Generate a random symmetric fuel usage matrix
    private static double[][] generateRandomMatrix(int size, Random random) {
        double[][] matrix = new double[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                double value = random.nextDouble() * 100; // Random value between 0 and 100
                matrix[i][j] = value;
                matrix[j][i] = value;
            }
        }

        return matrix;
    }

    // Generate city names (e.g., City 1, City 2, ..., City 50)
    private static String[] generateCityNames(int size) {
        String[] names = new String[size];
        for (int i = 0; i < size; i++) {
            names[i] = "City " + (i + 1);
        }
        return names;
    }
}
