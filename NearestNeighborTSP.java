import java.io.*;
import java.util.*;

public class NearestNeighborTSP {
    private static final double INF = Double.MAX_VALUE;

    // CSV file reader. Returns a map containing the distance matrix and city names
    public static Map<String, Object> readCSV(String filePath) throws IOException {
        List<String> cityNames = new ArrayList<>(); // List to store city names
        Map<String, Integer> cityIndexMap = new HashMap<>(); // Map to store city name to index mapping
        String line; // Variable to hold each line read from the file

        // Try-with-resources to ensure the BufferedReader is closed after use
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            line = br.readLine(); // Read the header line
            // Check if the header line is present and correctly formatted
            if (line == null || !line.trim().equalsIgnoreCase("City1,City2,Distance(km)")) {
                throw new IOException("CSV file must have header: City1,City2,Distance(km)");
            }

            // Read each line of the CSV file
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(","); // Split line into components
                // Validate the row format
                if (parts.length != 3) {
                    throw new IOException("Invalid row format: " + line);
                }

                String city1 = parts[0].trim(); // First city
                String city2 = parts[1].trim(); // Second city
                double distance = Double.parseDouble(parts[2].trim()); // Distance between the cities

                // Add the first city to the city list and map if it doesn't exist
                if (!cityIndexMap.containsKey(city1)) {
                    cityIndexMap.put(city1, cityNames.size()); // Map city to its index
                    cityNames.add(city1); // Add city to list
                }
                // Add the second city to the city list and map if it doesn't exist
                if (!cityIndexMap.containsKey(city2)) {
                    cityIndexMap.put(city2, cityNames.size()); // Map city to its index
                    cityNames.add(city2); // Add city to list
                }
            }
        }

        int n = cityNames.size(); // Number of unique cities
        double[][] distanceMatrix = new double[n][n]; // Initialize the distance matrix
        for (int i = 0; i < n; i++) {
            Arrays.fill(distanceMatrix[i], INF); // Fill with INF to represent no direct connection
            distanceMatrix[i][i] = 0; // Distance to self is always 0
        }

        // Re-read the file to populate the distance matrix
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // Skip the header line
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(","); // Split line into components
                String city1 = parts[0].trim(); // First city
                String city2 = parts[1].trim(); // Second city
                double distance = Double.parseDouble(parts[2].trim()); // Distance between the cities

                int index1 = cityIndexMap.get(city1); // Retrieve index of the first city
                int index2 = cityIndexMap.get(city2); // Retrieve index of the second city

                // Populate the symmetric distance matrix with the distance
                distanceMatrix[index1][index2] = distance;
                distanceMatrix[index2][index1] = distance;
            }
        }

        // Prepare the result map to return
        Map<String, Object> result = new HashMap<>();
        result.put("cityNames", cityNames); // Add city names to the result
        result.put("distanceMatrix", distanceMatrix); // Add distance matrix to the result

        return result; // Return the result map
    }

    // Calculates the total distance of a given tour
    public static double calculateTourDistance(double[][] distanceMatrix, List<Integer> tour) {
        // Initialize total distance to 0
        double totalDistance = 0;
        
        // Loop through the tour list to calculate the distance between consecutive cities
        for (int i = 0; i < tour.size() - 1; i++) {
            // Add the distance from the current city to the next city in the tour
            totalDistance += distanceMatrix[tour.get(i)][tour.get(i + 1)];
        }
        
        // Add the distance from the last city back to the starting city to complete the tour
        totalDistance += distanceMatrix[tour.get(tour.size() - 1)][tour.get(0)];
        
        // Round the total distance to two decimal places and return it
        return roundToTwoDecimals(totalDistance);
    }

    // Rounds double to 2 decimal places
    public static double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    // Nearest Neighbor Algorithm
    public static List<Integer> nearestNeighbor(double[][] distanceMatrix, int startCity) {
        int n = distanceMatrix.length;
        List<Integer> tour = new ArrayList<>();
        boolean[] visited = new boolean[n];
        int currentCity = startCity;

        visited[currentCity] = true;
        tour.add(currentCity);

        for (int i = 1; i < n; i++) {
            double nearestDistance = INF;
            int nearestCity = -1;

            // Find the nearest unvisited city
            for (int j = 0; j < n; j++) {
                if (!visited[j] && distanceMatrix[currentCity][j] < nearestDistance) {
                    nearestDistance = distanceMatrix[currentCity][j];
                    nearestCity = j;
                }
            }

            visited[nearestCity] = true;
            tour.add(nearestCity);
            currentCity = nearestCity;
        }

        return tour;
    }

    // Prints tour with city names
    public static void printTourWithCityNames(List<Integer> tour, List<String> cityNames) {
        for (int i : tour) {
            System.out.print(cityNames.get(i) + " -> ");
        }
        System.out.println(cityNames.get(tour.get(0))); // Returns to start
    }

    public static void main(String[] args) {
        // File path to CSV file
        String filePath = "DistanceBetweenEuropeanCities.csv";

        try {
            // Read data from CSV file
            Map<String, Object> data = readCSV(filePath);
            List<String> cityNames = (List<String>) data.get("cityNames");
            double[][] distanceMatrix = (double[][]) data.get("distanceMatrix");

            int n = distanceMatrix.length;

            double bestDistance = INF;
            List<Integer> bestTour = null;
            int bestStartCity = -1;

            // Computation time begins
            long startTime = System.nanoTime();

            // Tour optimization from each city
            for (int startCity = 0; startCity < n; startCity++) {
                List<Integer> tour = nearestNeighbor(distanceMatrix, startCity);
                double tourDistance = calculateTourDistance(distanceMatrix, tour);

                System.out.println("\nTour Starting from " + cityNames.get(startCity) + ":");
                printTourWithCityNames(tour, cityNames);
                System.out.println("Distance: " + String.format("%.2f", tourDistance));

                // Update the best tour if this one is better
                if (tourDistance < bestDistance) {
                    bestDistance = tourDistance;
                    bestTour = tour;
                    bestStartCity = startCity;
                }
            }

            // Computation time ends
            long endTime = System.nanoTime();

            // Elapsed time in milliseconds
            double elapsedTime = (endTime - startTime) / 1_000_000.0;

            // Best overall tour
            System.out.println("\nBest Tour Starting from " + cityNames.get(bestStartCity) + ":");
            printTourWithCityNames(bestTour, cityNames);
            System.out.println("Best Distance: " + String.format("%.2f", bestDistance));

            // Time taken to calculate
            System.out.printf("\nComputation Time: %.2f milliseconds\n", elapsedTime);

            // Hardware + Software declaration
            System.out.println("\nOperating System: " + System.getProperty("os.name"));
            System.out.println("OS Version: " + System.getProperty("os.version"));
            System.out.println("OS Architecture: " + System.getProperty("os.arch"));
            System.out.println("User Name: " + System.getProperty("user.name"));
            System.out.println("Java Version: " + System.getProperty("java.version"));
            System.out.println("Java Vendor: " + System.getProperty("java.vendor"));
        } catch (IOException e) {
            System.err.println("Error reading the CSV file: " + e.getMessage());
        }
    }
}
