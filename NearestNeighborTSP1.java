import java.io.*;
import java.util.*;

public class NearestNeighborTSP1 {
    private static final double INF = Double.MAX_VALUE;
    private static final int MAX_CITIES = 50;

    // CSV file reader. Returns a map containing the distance matrix and city names
    public static Map<String, Object> readCSV(String filePath) throws IOException {
        List<String> cityNames = new ArrayList<>();
        Map<String, Integer> cityIndexMap = new HashMap<>();
        String line;
    
        // Read all cities and distances
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            line = br.readLine();
            if (line == null || !line.trim().equalsIgnoreCase("City1,City2,Distance(km)")) {
                throw new IOException("CSV file must have header: City1,City2,Distance(km)");
            }
    
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 3) {
                    throw new IOException("Invalid row format: " + line);
                }
    
                String city1 = parts[0].trim();
                String city2 = parts[1].trim();
                double distance = Double.parseDouble(parts[2].trim());
    
                // Add cities to the map and list
                if (!cityIndexMap.containsKey(city1)) {
                    cityIndexMap.put(city1, cityNames.size());
                    cityNames.add(city1);
                }
                if (!cityIndexMap.containsKey(city2)) {
                    cityIndexMap.put(city2, cityNames.size());
                    cityNames.add(city2);
                }
            }
        }
    
        // Limit the number of cities to MAX_CITIES
        int n = Math.min(cityNames.size(), MAX_CITIES);
        double[][] distanceMatrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(distanceMatrix[i], INF);
            distanceMatrix[i][i] = 0;
        }
    
        // Re-read the file to populate the truncated distance matrix
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // Skip the header line
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String city1 = parts[0].trim();
                String city2 = parts[1].trim();
                double distance = Double.parseDouble(parts[2].trim());
    
                Integer index1 = cityIndexMap.get(city1);
                Integer index2 = cityIndexMap.get(city2);
    
                // Only populate the matrix if the cities are within the first MAX_CITIES
                if (index1 != null && index2 != null && index1 < n && index2 < n) {
                    distanceMatrix[index1][index2] = distance;
                    distanceMatrix[index2][index1] = distance;
                }
            }
        }
    
        // Prepare the result map
        Map<String, Object> result = new HashMap<>();
        result.put("cityNames", cityNames.subList(0, n)); // Limit city names to MAX_CITIES
        result.put("distanceMatrix", distanceMatrix);
    
        return result;
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
