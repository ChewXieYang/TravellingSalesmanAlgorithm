import java.util.ArrayList;
import java.util.List;

public class BruteForceTSP {

    // Method to calculate the cost of a given route
    private static double calculateCost(double[][] graph, List<Integer> route) {
        double cost = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            cost += graph[route.get(i)][route.get(i + 1)];
        }
        // Add the cost to return to the starting city
        cost += graph[route.get(route.size() - 1)][route.get(0)];
        return cost;
    }

    // Helper method to generate all permutations of cities
    private static void generatePermutations(List<Integer> cities, int start, List<List<Integer>> permutations) {
        if (start == cities.size() - 1) {
            permutations.add(new ArrayList<>(cities));
            return;
        }
        for (int i = start; i < cities.size(); i++) {
            // Swap cities[start] and cities[i]
            java.util.Collections.swap(cities, start, i);
            // Recurse
            generatePermutations(cities, start + 1, permutations);
            // Swap back
            java.util.Collections.swap(cities, start, i);
        }
    }

    // Method to solve the Travelling Salesman Problem using brute force
    public static void solveTSP(double[][] graph, String[] cityNames) {
        int n = graph.length;
        List<Integer> cities = new ArrayList<>();
        for (int i = 1; i < n; i++) { // Start from city 1 to n-1 (city 0 is the starting point)
            cities.add(i);
        }

        List<List<Integer>> permutations = new ArrayList<>();
        generatePermutations(cities, 0, permutations);

        double minCost = Double.MAX_VALUE;
        double maxCost = Double.MIN_VALUE;
        List<Integer> bestRoute = null;
        List<Integer> worstRoute = null;

        // Iterate through all possible routes and calculate their cost
        System.out.println("Evaluating all possible routes:");
        for (List<Integer> route : permutations) {
            List<Integer> fullRoute = new ArrayList<>();
            fullRoute.add(0); // Starting city
            fullRoute.addAll(route); // Adds other cities
            fullRoute.add(0); // Return to starting city

            double cost = calculateCost(graph, fullRoute);

        
            System.out.print("Route: ");
            for (int i = 0; i < fullRoute.size(); i++) {
                System.out.print(cityNames[fullRoute.get(i)]);
                if (i < fullRoute.size() - 1) {
                    System.out.print(" -> ");
                }
            }
            System.out.printf(" | Cost: %.2f\n", cost);

            // Best Route Calculation
            if (cost < minCost) {
                minCost = cost;
                bestRoute = fullRoute;
            }

            // Worst Route Calculation
            if (cost > maxCost) {
                maxCost = cost;
                worstRoute = fullRoute;
            }
        }

        // Best route and cost
        if (bestRoute != null) {
            System.out.print("\nBest Route: ");
            for (int i = 0; i < bestRoute.size(); i++) {
                System.out.print(cityNames[bestRoute.get(i)]);
                if (i < bestRoute.size() - 1) {
                    System.out.print(" -> ");
                }
            }
            System.out.printf("\nMinimum Cost: %.2f\n", minCost);
        }

        // Worst route and cost
        if (worstRoute != null) {
            System.out.print("\nWorst Route: ");
            for (int i = 0; i < worstRoute.size(); i++) {
                System.out.print(cityNames[worstRoute.get(i)]);
                if (i < worstRoute.size() - 1) {
                    System.out.print(" -> ");
                }
            }
            System.out.printf("\nMaximum Cost: %.2f\n", maxCost);
        }
    }

    public static void main(String[] args) {
        // Example graph (distance matrix)
        double[][] graph = {
            {0, 4.04, 6.41, 2.44, 2.98},
            {4.04, 0, 8.27, 7.15, 3.96},
            {6.41, 8.27, 0, 3.33, 4.32},
            {2.44, 7.15, 3.33, 0, 4.48},
            {2.98, 3.96, 4.32, 4.48, 0}
        };

        String[] cityNames = {"Sarajevo", "Zagreb", "Skopje", "Podgorica", "Belgrade"};

        solveTSP(graph, cityNames);
    }
}