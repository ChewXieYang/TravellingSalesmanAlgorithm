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
    boolean[] visited = new boolean[n]; // Array to track visited cities
    int currentCity = 0; // Start at the first city: Sarajevo
    double totalFU = 0; // Initialize total fuel consumption
    visited[currentCity] = true; // Mark the starting city as visited

    // Output the starting city
    System.out.println("Starting at city: " + cityNames[currentCity]);

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

        // Output debug information for each step
        System.out.println("Current city: " + cityNames[currentCity]);
        System.out.println("Nearest unvisited city: " + cityNames[nextCity]);
        System.out.println("Fuel consumption for this move: " + minFU);

        totalFU += minFU; // Add the fuel consumed for this step to the total
        visited[nextCity] = true; // Mark the next city as visited
        currentCity = nextCity; // Move to the next city
    }

    // Return to the starting city after visiting all cities
    double returnFuel = fuMatrix[currentCity][0]; // Calculate fuel to return to start
    totalFU += returnFuel; // Add the return fuel consumption to the total
    System.out.println("Returning from city " + cityNames[currentCity] + " to city " + cityNames[0] + " (Fuel Consumption: " + returnFuel + ")");

    // Output the total fuel consumption including the return trip
    System.out.println("Total Fuel Usage (FU): " + totalFU);
}
}
