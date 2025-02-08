import java.io.*;
import java.util.*;

public class NearestNeighborTSP {
    private static final double INF = Double.MAX_VALUE;

    // Reads a CSV file and returns city names and a distance matrix limited by maxCities
    public static Map<String, Object> readCSV(String filePath, int maxCities) throws IOException {
        List<String> cityNames = new ArrayList<>();
        Map<String, Integer> cityIndexMap = new HashMap<>();
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            line = br.readLine();
            if (line == null || !line.trim().equalsIgnoreCase("City1,City2,Distance(km)")) {
                throw new IOException("CSV file must have header: City1,City2,Distance(km)");
            }

            while ((line = br.readLine()) != null) {
                if (cityNames.size() >= maxCities) break;

                String[] parts = line.split(",");
                if (parts.length != 3) {
                    throw new IOException("Invalid row format: " + line);
                }

                String city1 = parts[0].trim();
                String city2 = parts[1].trim();
                double distance = Double.parseDouble(parts[2].trim());

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

        int n = cityNames.size();
        double[][] distanceMatrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(distanceMatrix[i], INF);
            distanceMatrix[i][i] = 0;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String city1 = parts[0].trim();
                String city2 = parts[1].trim();
                double distance = Double.parseDouble(parts[2].trim());

                if (cityIndexMap.containsKey(city1) && cityIndexMap.containsKey(city2)) {
                    int index1 = cityIndexMap.get(city1);
                    int index2 = cityIndexMap.get(city2);
                    distanceMatrix[index1][index2] = distance;
                    distanceMatrix[index2][index1] = distance;
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("cityNames", cityNames);
        result.put("distanceMatrix", distanceMatrix);
        return result;
    }

    // Calculates the total distance of a given tour
    public static double calculateTourDistance(double[][] distanceMatrix, List<Integer> tour) {
        double totalDistance = 0;
        for (int i = 0; i < tour.size() - 1; i++) {
            totalDistance += distanceMatrix[tour.get(i)][tour.get(i + 1)];
        }
        totalDistance += distanceMatrix[tour.get(tour.size() - 1)][tour.get(0)];
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
    public static void printTourWithCityNames(List<Integer> tour, List<String> cityNames) {
        for (int i : tour) {
            System.out.print(cityNames.get(i) + " -> ");
        }
        System.out.println(cityNames.get(tour.get(0)));
    }

    public static void main(String[] args) {
        String filePath = "DistanceBetweenEuropeanCities.csv";

        try (Scanner scanner = new Scanner(System.in)) { // Auto-closes scanner
            System.out.println("Enter the maximum number of cities to calculate (up to 50):");
            int maxCities = scanner.nextInt();

            if (maxCities < 2 || maxCities > 50) {
                System.err.println("Please enter a number between 2 and 50.");
                return;
            }

            // Read city names and distance matrix with user-defined city limit
            Map<String, Object> data = readCSV(filePath, maxCities);
            List<String> cityNames = (List<String>) data.get("cityNames");
            double[][] distanceMatrix = (double[][]) data.get("distanceMatrix");

            int n = distanceMatrix.length;
            double bestDistance = INF;
            List<Integer> bestTour = null;
            int bestStartCity = -1;

            long startTime = System.nanoTime();

            for (int startCity = 0; startCity < n; startCity++) {
                List<Integer> tour = nearestNeighbor(distanceMatrix, startCity);
                double tourDistance = calculateTourDistance(distanceMatrix, tour);

                System.out.println("\nTour Starting from " + cityNames.get(startCity) + ":");
                printTourWithCityNames(tour, cityNames);
                System.out.println("Distance: " + String.format("%.2f", tourDistance));

                if (tourDistance < bestDistance) {
                    bestDistance = tourDistance;
                    bestTour = tour;
                    bestStartCity = startCity;
                }
            }

            long endTime = System.nanoTime();
            double elapsedTime = (endTime - startTime) / 1_000_000.0;

            System.out.println("\nBest Tour Starting from " + cityNames.get(bestStartCity) + ":");
            printTourWithCityNames(bestTour, cityNames);
            System.out.println("Best Distance: " + String.format("%.2f", bestDistance));
            System.out.printf("\nComputation Time: %.2f milliseconds\n", elapsedTime);

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
