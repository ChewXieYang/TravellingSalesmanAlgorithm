import java.util.ArrayList;

/**
 * This program evaluates all possible routes for the Traveling Salesman Problem
 * (TSP) and
 * prints out the best and worst routes found along with their costs. It also
 * records the
 * time taken to complete the computation.
 * 
 * @author MYRA OPHELIA IMAN
 * @version 1.0
 * @since 2024-12-30
 * @testDevice: Testing was conducted on an MSI GF75 Thin 9SC, with Windows 11
 *              Pro (Version 24H2, Build 26100.2605).
 *              Processor: Intel(R) Core(TM) i7-9750H @ 2.60GHz, 8.00 GB RAM
 *              (7.85 GB usable), 64-bit OS.
 */

public class NearestNeighborTSP {

    public static void main(String[] args) {
        
        // Fuel consumption matrix representing distances between cities
        double[][] fuMatrix = {
                { 0, 4.04, 6.41, 2.44, 2.98 }, // Sarajevo
                { 4.04, 0, 8.27, 7.15, 3.96 }, // Zagreb
                { 6.41, 8.27, 0, 3.33, 4.32 }, // Skopje
                { 2.44, 7.15, 3.33, 0, 4.48 }, // Podgorica
                { 2.98, 3.96, 4.32, 4.48, 0 } // Belgrade
        };

        // Array of city names corresponding to the indices in fuMatrix
        String[] cityNames = { "Sarajevo", "Zagreb", "Skopje", "Podgorica", "Belgrade" };
        int n = cityNames.length; // Total number of cities

        // Variables to keep track of the minimum and maximum fuel costs found
        double minCost = Double.MAX_VALUE;
        double maxCost = Double.MIN_VALUE;

        // Variables to store the best and worst routes found
        ArrayList<String> bestRoute = null;
        ArrayList<String> worstRoute = null;

        // Record the start time of the computation
        long startTime = System.nanoTime();

        System.out.println("Evaluating all possible routes:");

        // Loop through each city as the starting point for the route
        for (int startCity = 0; startCity < n; startCity++) {
            boolean[] visited = new boolean[n]; // Array to track visited cities
            ArrayList<String> currentRoute = new ArrayList<>(); // List to store the current route
            double totalFU = 0; // Total fuel consumption for the current route

            int currentCity = startCity; // Start from the current city
            visited[currentCity] = true; // Mark the starting city as visited
            currentRoute.add(cityNames[currentCity]); // Add starting city to the route

            // Loop to visit the remaining cities
            for (int i = 1; i < n; i++) {
                double minFU = Double.MAX_VALUE; // Variable to find the nearest unvisited city
                int nextCity = -1; // Variable to track the index of the next city

                // Find the nearest unvisited city
                for (int j = 0; j < n; j++) {
                    if (!visited[j] && fuMatrix[currentCity][j] < minFU) {
                        minFU = fuMatrix[currentCity][j];
                        nextCity = j;
                    }
                }

                // Update total fuel consumption and mark the city as visited
                totalFU += minFU;
                visited[nextCity] = true;
                currentRoute.add(cityNames[nextCity]); // Add next city to the route
                currentCity = nextCity; // Move to the next city
            }

            // Return to the starting city to complete the tour
            totalFU += fuMatrix[currentCity][startCity];
            currentRoute.add(cityNames[startCity]); // Add the starting city to the end of the route

            // Print the current route and its cost
            System.out.println(
                    "Route: " + String.join(" -> ", currentRoute) + " | Cost: " + String.format("%.2f", totalFU));

            // Check if the current route is the best or worst found so far
            if (totalFU < minCost) {
                minCost = totalFU;
                bestRoute = new ArrayList<>(currentRoute);
            }
            if (totalFU > maxCost) {
                maxCost = totalFU;
                worstRoute = new ArrayList<>(currentRoute);
            }
        }
       


        // Record the end time of the computation
        long endTime = System.nanoTime();
        double computationTime = (endTime - startTime) / 1e6; // Convert time to milliseconds

        // Output the best and worst routes found along with their costs
        System.out.println("\nBest Route: " + String.join(" -> ", bestRoute));
        System.out.println("Minimum Cost: " + String.format("%.2f", minCost));

        System.out.println("\nWorst Route: " + String.join(" -> ", worstRoute));
        System.out.println("Maximum Cost: " + String.format("%.2f", maxCost));

        // Output the computation time
        System.out.println("\nComputation Time: " + String.format("%.2f", computationTime) + " milliseconds");
        
        System.out.println("Testing on device: MSI GF75 Thin 9SC");
        System.out.println("Operating System: Windows 11 Pro (Version 24H2, Build 26100.2605)");
    }
}
