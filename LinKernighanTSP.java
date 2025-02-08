import java.io.*;
import java.util.*;

public class LinKernighanTSP {
    private static final double INF = Double.MAX_VALUE;

    // CSV file reader. Returns distance matrix and city names, limited to MAX_CITIES
    public static Map<String, Object> readCSV(String filePath, int maxCities) throws IOException {
        List<String> cityNames = new ArrayList<>();
        Map<String, Integer> cityIndexMap = new HashMap<>();
        String line;
    
        // Parse the file and build the city list and distance map
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            line = br.readLine(); // Read the header
            if (line == null || !line.trim().equalsIgnoreCase("City1,City2,Distance(km)")) {
                throw new IOException("CSV file must have header: City1,City2,Distance(km)");
            }
    
            while ((line = br.readLine()) != null) {
                if (cityNames.size() >= maxCities) break; // Stop reading beyond the limit
    
                String[] parts = line.split(",");
                if (parts.length != 3) {
                    throw new IOException("Invalid row format: " + line);
                }
    
                String city1 = parts[0].trim();
                String city2 = parts[1].trim();
                double distance = Double.parseDouble(parts[2].trim());
    
                // Add cities to the city list and map if they're within the limit
                if (cityNames.size() < maxCities && !cityIndexMap.containsKey(city1)) {
                    cityIndexMap.put(city1, cityNames.size());
                    cityNames.add(city1);
                }
                if (cityNames.size() < maxCities && !cityIndexMap.containsKey(city2)) {
                    cityIndexMap.put(city2, cityNames.size());
                    cityNames.add(city2);
                }
            }
        }
    
        // Initialize distance matrix for limited cities
        int n = cityNames.size();
        double[][] distanceMatrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(distanceMatrix[i], INF);
            distanceMatrix[i][i] = 0; // Distance to self is 0
        }
    
        // Re-read file to populate the distance matrix
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String city1 = parts[0].trim();
                String city2 = parts[1].trim();
                double distance = Double.parseDouble(parts[2].trim());
    
                if (cityIndexMap.containsKey(city1) && cityIndexMap.containsKey(city2)) {
                    int index1 = cityIndexMap.get(city1);
                    int index2 = cityIndexMap.get(city2);
    
                    // Populate symmetric distance matrix
                    distanceMatrix[index1][index2] = distance;
                    distanceMatrix[index2][index1] = distance;
                }
            }
        }
    
        // Prepare results
        Map<String, Object> result = new HashMap<>();
        result.put("cityNames", cityNames);
        result.put("distanceMatrix", distanceMatrix);
    
        return result;
    }

    // Total distance of the tour
    public static double calculateTourDistance(double[][] distanceMatrix, List<Integer> tour) {
        double totalDistance = 0;
        for (int i = 0; i < tour.size() - 1; i++) {
            totalDistance += distanceMatrix[tour.get(i)][tour.get(i + 1)];
        }
        totalDistance += distanceMatrix[tour.get(tour.size() - 1)][tour.get(0)];
        return roundToTwoDecimals(totalDistance);
    }

    // Rounds double to 2 decimal places
    public static double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    // Perform a two-opt swap
    public static List<Integer> twoOptSwap(List<Integer> tour, int i, int k) {
        List<Integer> newTour = new ArrayList<>(tour.subList(0, i));
        List<Integer> reversedSegment = new ArrayList<>(tour.subList(i, k + 1));
        Collections.reverse(reversedSegment);
        newTour.addAll(reversedSegment);
        newTour.addAll(tour.subList(k + 1, tour.size()));
        return newTour;
    }

    // Lin-Kernighan Heuristic
    public static List<Integer> linKernighan(double[][] distanceMatrix, List<Integer> initialTour) {
        int n = distanceMatrix.length;
        List<Integer> currentTour = new ArrayList<>(initialTour);
        double currentDistance = calculateTourDistance(distanceMatrix, currentTour);

        boolean improved;
        do {
            improved = false;

            for (int i = 1; i < n - 1; i++) {
                for (int j = i + 1; j < n; j++) {
                    List<Integer> newTour = twoOptSwap(currentTour, i, j);
                    double newDistance = calculateTourDistance(distanceMatrix, newTour);

                    if (newDistance < currentDistance) {
                        currentTour = newTour;
                        currentDistance = newDistance;
                        improved = true;
                    }
                }
            }
        } while (improved);

        return currentTour;
    }

    // Generates initial tour starting from specific city
    public static List<Integer> generateInitialTour(int n, int startCity) {
        List<Integer> tour = new ArrayList<>();
        tour.add(startCity); // Fixes starting city
        for (int i = 0; i < n; i++) {
            if (i != startCity) {
                tour.add(i);
            }
        }
        Collections.shuffle(tour.subList(1, tour.size())); // Shuffles remaining cities
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
        
        try (Scanner scanner = new Scanner(System.in)) { // Auto-closes scanner
            System.out.println("Enter the maximum number of cities to calculate (up to 50):");
            int maxCities = scanner.nextInt();
    
            if (maxCities < 2 || maxCities > 50) {
                System.err.println("Please enter a number between 2 and 50.");
                return;
            }
    
            // Read data from CSV file, limited by user-specified maxCities
            Map<String, Object> data = readCSV(filePath, maxCities);
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
                List<Integer> initialTour = generateInitialTour(n, startCity);
                System.out.println("\nStarting from " + cityNames.get(startCity));
                System.out.println("Initial Tour:");
                printTourWithCityNames(initialTour, cityNames);
                double initialDistance = calculateTourDistance(distanceMatrix, initialTour);
                System.out.println("\nInitial Distance: " + String.format("%.2f", initialDistance));
    
                List<Integer> optimizedTour = linKernighan(distanceMatrix, initialTour);
                double optimizedDistance = calculateTourDistance(distanceMatrix, optimizedTour);
    
                System.out.println("\nOptimized Tour:");
                printTourWithCityNames(optimizedTour, cityNames);
                System.out.println("\nOptimized Distance: " + String.format("%.2f", optimizedDistance));
    
                // Update the best tour if this one is better
                if (optimizedDistance < bestDistance) {
                    bestDistance = optimizedDistance;
                    bestTour = optimizedTour;
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
        } catch (InputMismatchException e) {
            System.err.println("Invalid input. Please enter a number between 2 and 50.");
        }
    }
}
