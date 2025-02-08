import java.io.*;
import java.util.*;

public class NearestNeighborTSP1 {
    private static final double INF = Double.MAX_VALUE;

    // Reads a CSV file and returns city names and a distance matrix limited by maxCities
    // The CSV file should have a header with columns City1, City2, Distance(km)
    // The file is read twice: first to get the list of city names and their indices,
    // and second to fill in the distance matrix with the distances read from the file
    public static Map<String, Object> readCSV(String filePath, int maxCities) throws IOException {
        // List to store city names
        List<String> cityNames = new ArrayList<>();
        // Map to associate city names with their indices
        Map<String, Integer> cityIndexMap = new HashMap<>();
        String line; // Variable to hold each line read from the file

        // Try-with-resources to ensure BufferedReader is closed after use
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // Read the first line of the CSV file, which should be the header
            line = br.readLine();
            // Check if the header is present and correctly formatted
            if (line == null || !line.trim().equalsIgnoreCase("City1,City2,Distance(km)")) {
                throw new IOException("CSV file must have header: City1,City2,Distance(km)");
            }

            // Read the rest of the lines containing city pairs and distances
            while ((line = br.readLine()) != null) {
                // Stop reading if the maximum number of cities is reached
                if (cityNames.size() >= maxCities) break;

                // Split the line by commas to extract city names and distance
                String[] parts = line.split(",");
                // Ensure the line has exactly 3 parts: city1, city2, distance
                if (parts.length != 3) {
                    throw new IOException("Invalid row format: " + line);
                }

                // Trim any extra spaces around city names and parse the distance
                String city1 = parts[0].trim();
                String city2 = parts[1].trim();
                double distance = Double.parseDouble(parts[2].trim());

                // Add city1 to the list and map if it's not already present
                if (cityNames.size() < maxCities && !cityIndexMap.containsKey(city1)) {
                    cityIndexMap.put(city1, cityNames.size());
                    cityNames.add(city1);
                }
                // Add city2 to the list and map if it's not already present
                if (cityNames.size() < maxCities && !cityIndexMap.containsKey(city2)) {
                    cityIndexMap.put(city2, cityNames.size());
                    cityNames.add(city2);
                }
            }
        }

        // Get the number of cities added to the list
        int n = cityNames.size();
        // Initialize a distance matrix with default values of INF
        double[][] distanceMatrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(distanceMatrix[i], INF); // Fill each row with INF
            distanceMatrix[i][i] = 0; // Distance from a city to itself is 0
        }

        // Re-read the file to fill in the distance matrix
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // Skip the header line
            while ((line = br.readLine()) != null) {
                // Split the line to get city names and distance
                String[] parts = line.split(",");
                String city1 = parts[0].trim();
                String city2 = parts[1].trim();
                double distance = Double.parseDouble(parts[2].trim());

                // If both cities are in the map, update the distance matrix
                if (cityIndexMap.containsKey(city1) && cityIndexMap.containsKey(city2)) {
                    int index1 = cityIndexMap.get(city1);
                    int index2 = cityIndexMap.get(city2);
                    distanceMatrix[index1][index2] = distance;
                    distanceMatrix[index2][index1] = distance; // Symmetric distance
                }
            }
        }

        // Prepare the result map containing city names and distance matrix
        Map<String, Object> result = new HashMap<>();
        result.put("cityNames", cityNames);
        result.put("distanceMatrix", distanceMatrix);
        return result; // Return the result map
    }

    // Calculates the total distance of a given tour
    // This method takes a distance matrix and a tour (i.e. a list of city indices)
    // and returns the total distance of the tour by summing up the distances between
    // each pair of consecutive cities in the tour.
    // The total distance is calculated by summing up the distances between each pair
    // of consecutive cities in the tour, and then adding the distance from the last
    // city back to the first city to complete the loop.
    // Finally, the total distance is rounded to two decimal places using the
    // roundToTwoDecimals() method.
    public static double calculateTourDistance(double[][] distanceMatrix, List<Integer> tour) {
        double totalDistance = 0;
        for (int i = 0; i < tour.size() - 1; i++) {
            // Add the distance from the current city to the next city in the tour
            totalDistance += distanceMatrix[tour.get(i)][tour.get(i + 1)];
        }
        // Add the distance from the last city back to the first city to complete the loop
        totalDistance += distanceMatrix[tour.get(tour.size() - 1)][tour.get(0)];
        // Round the total distance to two decimal places
        return roundToTwoDecimals(totalDistance);
    }

    // Rounds a double value to two decimal places
    public static double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    // Nearest Neighbor Algorithm for solving the TSP
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
    // This method takes a list of city indices and a list of city names,
    // and prints the tour with city names.
    // The tour is printed in the order of the city indices,
    // with each city name followed by an arrow (->) and a space.
    // The last city is followed by the first city to complete the loop.
    public static void printTourWithCityNames(List<Integer> tour, List<String> cityNames) {
        // Iterate over the tour and print the city name for each city index
        for (int i : tour) {
            // Get the city name for the current city index
            String cityName = cityNames.get(i);
            // Print the city name followed by an arrow and a space
            System.out.print(cityName + " -> ");
        }
        // Print the first city name to complete the loop
        System.out.println(cityNames.get(tour.get(0)));
    }

    public static void main(String[] args) {
        // Define the file path to the CSV file containing distances between cities
        String filePath = "DistanceBetweenEuropeanCities.csv";

        // Use try-with-resources to automatically close the Scanner after use
        try (Scanner scanner = new Scanner(System.in)) {
            // Prompt the user to enter the maximum number of cities to process
            System.out.println("Enter the maximum number of cities to calculate (up to 50):");
            int maxCities = scanner.nextInt(); // Read the user input for the number of cities

            // Validate that the input is within the allowed range (2 to 50)
            if (maxCities < 2 || maxCities > 50) {
                System.err.println("Please enter a number between 2 and 50.");
                return; // Exit the program if the input is invalid
            }

            // Invoke the readCSV method to get city names and distance matrix based on the input limit
            Map<String, Object> data = readCSV(filePath, maxCities);
            List<String> cityNames = (List<String>) data.get("cityNames"); // Retrieve city names
            double[][] distanceMatrix = (double[][]) data.get("distanceMatrix"); // Retrieve distance matrix

            int n = distanceMatrix.length; // Determine the number of cities
            double bestDistance = INF; // Initialize the best distance to infinity
            List<Integer> bestTour = null; // Initialize the best tour as null
            int bestStartCity = -1; // Initialize the best start city index as -1

            // Record the start time of the computation for performance measurement
            long startTime = System.nanoTime();

            // Iterate over each city as a potential starting point for the tour
            for (int startCity = 0; startCity < n; startCity++) {
                // Generate a tour starting from the current city using the nearest neighbor algorithm
                List<Integer> tour = nearestNeighbor(distanceMatrix, startCity);
                // Calculate the total distance of the generated tour
                double tourDistance = calculateTourDistance(distanceMatrix, tour);

                // Print the generated tour and its total distance
                System.out.println("\nTour Starting from " + cityNames.get(startCity) + ":");
                printTourWithCityNames(tour, cityNames);
                System.out.println("Distance: " + String.format("%.2f", tourDistance));

                // Update the best tour if the current tour has a shorter distance
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

            // Print the best tour found and its total distance
            System.out.println("\nBest Tour Starting from " + cityNames.get(bestStartCity) + ":");
            printTourWithCityNames(bestTour, cityNames);
            System.out.println("Best Distance: " + String.format("%.2f", bestDistance));
            // Print the computation time in milliseconds
            System.out.printf("\nComputation Time: %.2f milliseconds\n", elapsedTime);

            // Print system and Java environment information for reference
            System.out.println("\nOperating System: " + System.getProperty("os.name"));
            System.out.println("OS Version: " + System.getProperty("os.version"));
            System.out.println("OS Architecture: " + System.getProperty("os.arch"));
            System.out.println("User Name: " + System.getProperty("user.name"));
            System.out.println("Java Version: " + System.getProperty("java.version"));
            System.out.println("Java Vendor: " + System.getProperty("java.vendor"));

        } catch (IOException e) {
            // Catch and handle exceptions related to reading the CSV file
            System.err.println("Error reading the CSV file: " + e.getMessage());
        } catch (InputMismatchException e) {
            // Catch and handle invalid input for the number of cities
            System.err.println("Invalid input. Please enter a number between 2 and 50.");
        }
    }
}
