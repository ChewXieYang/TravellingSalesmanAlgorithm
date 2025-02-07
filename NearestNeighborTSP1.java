import java.io.*;
import java.util.*;

public class NearestNeighborTSP1 {
    private static final double INF = Double.MAX_VALUE;
    private static final int MAX_CITIES = 10; // set 10 , 25, 50 - for the dataset 

    // Reads a CSV file and returns a map containing the distance matrix and city names
    // 
    // The CSV file should have the following format:
    // City1,City2,Distance(km)
    // 
    // The distance matrix is a 2D array where the element at row i and column j
    // represents the distance from city i to city j.
    //
    // The city names are stored in a list, and the distance matrix is populated
    // such that the element at row i and column j represents the distance from
    // cityNames[i] to cityNames[j].
    //
    // The city names and distance matrix are both limited to MAX_CITIES if the
    // input file has more than MAX_CITIES cities.
    //
    // This method throws an IOException if the input file does not have the
    // correct header or if any row has an invalid format.
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
        // Initialize the total distance of the tour to 0
        double totalDistance = 0;
        
        // Loop through the tour list to calculate the total distance of the tour
        for (int i = 0; i < tour.size() - 1; i++) {
            // Get the current city and the next city in the tour
            int currentCity = tour.get(i);
            int nextCity = tour.get(i + 1);
            
            // Add the distance from the current city to the next city in the tour
            // to the total distance
            totalDistance += distanceMatrix[currentCity][nextCity];
        }
        
        // Add the distance from the last city back to the starting city to complete the tour
        // and add it to the total distance
        int lastCity = tour.get(tour.size() - 1);
        int startCity = tour.get(0);
        totalDistance += distanceMatrix[lastCity][startCity];
        
        // Round the total distance to two decimal places and return it
        return roundToTwoDecimals(totalDistance);
    }

    // Rounds double to 2 decimal places
    public static double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    // Nearest Neighbor Algorithm for solving the Traveling Salesman Problem
    public static List<Integer> nearestNeighbor(double[][] distanceMatrix, int startCity) {
        int n = distanceMatrix.length; // Number of cities
        List<Integer> tour = new ArrayList<>(); // List to store the tour (order of cities visited)
        boolean[] visited = new boolean[n]; // Array to track visited cities
        int currentCity = startCity; // Start the tour at the specified start city

        // Mark the start city as visited and add it to the tour
        visited[currentCity] = true;
        tour.add(currentCity);

        // Loop to visit all cities
        for (int i = 1; i < n; i++) {
            double nearestDistance = INF; // Initialize nearest distance to infinity
            int nearestCity = -1; // Initialize nearest city index to -1 (undefined)

            // Find the nearest unvisited city
            for (int j = 0; j < n; j++) {
                // If city 'j' is unvisited and its distance from current city is less than the nearest distance
                if (!visited[j] && distanceMatrix[currentCity][j] < nearestDistance) {
                    nearestDistance = distanceMatrix[currentCity][j]; // Update nearest distance
                    nearestCity = j; // Update nearest city index
                }
            }

            // Mark the nearest city as visited and add it to the tour
            visited[nearestCity] = true;
            tour.add(nearestCity);
            currentCity = nearestCity; // Update the current city to the nearest city
        }

        return tour; // Return the completed tour
    }

    // Prints tour with city names
    // 
    // This function takes a tour (a list of city indices) and a list of city names
    // and prints out the tour with the city names instead of the indices.
    // 
    // For example, if the tour is [0, 1, 2, 0] and the city names are ["Paris", "London", "Berlin"],
    // then the function will print "Paris -> London -> Berlin -> Paris"
    // 
    // The function is useful for debugging and for displaying the results of the TSP algorithm
    public static void printTourWithCityNames(List<Integer> tour, List<String> cityNames) {
        // Loop through the tour list and print the city name at each index
        for (int i : tour) {
            System.out.print(cityNames.get(i) + " -> ");
        }
        // Print the city name at the starting index to complete the tour
        System.out.println(cityNames.get(tour.get(0))); 
    }

    public static void main(String[] args) {
        // Define the file path to the CSV file containing the distance data
        String filePath = "DistanceBetweenEuropeanCities.csv";

        try {
            // Read city names and their distance matrix from the CSV file
            Map<String, Object> data = readCSV(filePath);
            List<String> cityNames = (List<String>) data.get("cityNames");
            double[][] distanceMatrix = (double[][]) data.get("distanceMatrix");

            // Number of cities in the distance matrix
            int n = distanceMatrix.length;

            // Initialize variables to track the best tour and its distance
            double bestDistance = INF;
            List<Integer> bestTour = null;
            int bestStartCity = -1;

            // Record the start time of the computation for performance measurement
            long startTime = System.nanoTime();

            // Attempt to optimize the tour starting from each city
            for (int startCity = 0; startCity < n; startCity++) {
                // Generate a tour using the nearest neighbor heuristic from the start city
                List<Integer> tour = nearestNeighbor(distanceMatrix, startCity);
                // Calculate the total distance of the generated tour
                double tourDistance = calculateTourDistance(distanceMatrix, tour);

                // Print the tour and its distance starting from the given city
                System.out.println("\nTour Starting from " + cityNames.get(startCity) + ":");
                printTourWithCityNames(tour, cityNames);
                System.out.println("Distance: " + String.format("%.2f", tourDistance));

                // Update the best tour if the current tour is shorter
                if (tourDistance < bestDistance) {
                    bestDistance = tourDistance;
                    bestTour = tour;
                    bestStartCity = startCity;
                }
            }

            // Record the end time of the computation
            long endTime = System.nanoTime();

            // Calculate the elapsed time in milliseconds
            double elapsedTime = (endTime - startTime) / 1_000_000.0;

            // Print the best tour found and its distance
            System.out.println("\nBest Tour Starting from " + cityNames.get(bestStartCity) + ":");
            printTourWithCityNames(bestTour, cityNames);
            System.out.println("Best Distance: " + String.format("%.2f", bestDistance));

            // Print the total computation time
            System.out.printf("\nComputation Time: %.2f milliseconds\n", elapsedTime);

            // Print information about the operating system and Java environment
            System.out.println("\nOperating System: " + System.getProperty("os.name"));
            System.out.println("OS Version: " + System.getProperty("os.version"));
            System.out.println("OS Architecture: " + System.getProperty("os.arch"));
            System.out.println("User Name: " + System.getProperty("user.name"));
            System.out.println("Java Version: " + System.getProperty("java.version"));
            System.out.println("Java Vendor: " + System.getProperty("java.vendor"));
        } catch (IOException e) {
            // Print an error message if there's an issue reading the CSV file
            System.err.println("Error reading the CSV file: " + e.getMessage());
        }
    }
}
