import java.util.Arrays;

public class NearestNeighborTSP {

    public static void main(String[] args) {
        // Create a matrix representing the fuel consumption between each pair of cities
        double[][] fuMatrix = {
            {0, 4.04, 6.41, 2.44, 2.98}, // Sarajevo
            {4.04, 0, 8.27, 7.15, 3.96}, // Zagreb
            {6.41, 8.27, 0, 3.33, 4.32}, // Skopje
            {2.44, 7.15, 3.33, 0, 4.48}, // Podgorica
            {2.98, 3.96, 4.32, 4.48, 0}   // Belgrade
        };

        // Define the names of the cities, corresponding to the indices of the FU matrix
        String[] cityNames = {"Sarajevo", "Zagreb", "Skopje", "Podgorica", "Belgrade"};
        int n = fuMatrix.length; // Number of cities

        double bestCase = Double.MAX_VALUE;
        double worstCase = Double.MIN_VALUE;
        double[] fuelUsages = new double[n];
        int bestStartCity = -1;
        int worstStartCity = -1;

        // Measure start time
        long startTime = System.nanoTime();

        // Loop through all possible starting cities
        for (int startCity = 0; startCity < n; startCity++) {
            boolean[] visited = new boolean[n]; // Reset visited array
            int currentCity = startCity; // Set current city to the start city
            double totalFU = 0; // Initialize total fuel consumption
            visited[currentCity] = true; // Mark the starting city as visited

            // Implement the Nearest Neighbor Algorithm to visit all cities
            for (int i = 1; i < n; i++) {
                double minFU = Double.MAX_VALUE; // Initialize minimum fuel consumption
                int nextCity = -1; // Initialize the next city index

                // Search for the nearest unvisited city
                for (int j = 0; j < n; j++) {
                    if (!visited[j] && fuMatrix[currentCity][j] < minFU) {
                        minFU = fuMatrix[currentCity][j];
                        nextCity = j;
                    }
                }

                totalFU += minFU; // Add the fuel consumed for this step to the total
                visited[nextCity] = true; // Mark the next city as visited
                currentCity = nextCity; // Move to the next city
            }

            // Return to the starting city after visiting all cities
            double returnFuel = fuMatrix[currentCity][startCity]; // Calculate fuel to return to start
            totalFU += returnFuel; // Add the return fuel consumption to the total

            // Track the total fuel usage for this starting city
            fuelUsages[startCity] = totalFU;

            // Update best and worst cases
            if (totalFU < bestCase) {
                bestCase = totalFU;
                bestStartCity = startCity;
            }
            if (totalFU > worstCase) {
                worstCase = totalFU;
                worstStartCity = startCity;
            }
        }

        // Measure end time
        long endTime = System.nanoTime();
        double elapsedTimeInSeconds = (endTime - startTime) / 1e9; // Convert nanoseconds to seconds

        // Output the best and worst cases
        System.out.println("Best case: " + bestCase + " (Starting city: " + cityNames[bestStartCity] + ")");
        System.out.println("Worst case: " + worstCase + " (Starting city: " + cityNames[worstStartCity] + ")");
        System.out.println("Fuel usage for all starting cities: " + Arrays.toString(fuelUsages));

        // Output the time elapsed
        System.out.printf("Time elapsed: %.6f seconds%n", elapsedTimeInSeconds);
    }
}

