import java.util.*;

public class LinKernighanTSP {
    private static final double INF = Double.MAX_VALUE;

    // Calculate the total distance of the tour
    public static double calculateTourDistance(double[][] distanceMatrix, List<Integer> tour) {
        double totalDistance = 0;
        for (int i = 0; i < tour.size() - 1; i++) {
            totalDistance += distanceMatrix[tour.get(i)][tour.get(i + 1)];
        }
        totalDistance += distanceMatrix[tour.get(tour.size() - 1)][tour.get(0)];
        return roundToTwoDecimals(totalDistance);
    }

    // Round a double to two decimal places
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

    // Lin-Kernighan heuristic for solving TSP
    public static List<Integer> linKernighan(double[][] distanceMatrix, List<Integer> initialTour) {
        int n = distanceMatrix.length;
        List<Integer> currentTour = new ArrayList<>(initialTour);
        double currentDistance = calculateTourDistance(distanceMatrix, currentTour);

        boolean improved;
        do {
            improved = false;

            for (int i = 1; i < n - 1; i++) {
                for (int k = i + 1; k < n; k++) {
                    List<Integer> newTour = twoOptSwap(currentTour, i, k);
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

    // Generate an initial tour starting from a specific city
    public static List<Integer> generateInitialTour(int n, int startCity) {
        List<Integer> tour = new ArrayList<>();
        tour.add(startCity); // Fix the starting city
        for (int i = 0; i < n; i++) {
            if (i != startCity) {
                tour.add(i);
            }
        }
        Collections.shuffle(tour.subList(1, tour.size())); // Shuffle remaining cities
        return tour;
    }

    // Print the tour with city names
    public static void printTourWithCityNames(List<Integer> tour, List<String> cityNames) {
        for (int i : tour) {
            System.out.print(cityNames.get(i) + " -> ");
        }
        System.out.println(cityNames.get(tour.get(0))); // Return to start
    }

    public static void main(String[] args) {
        // Example distance matrix (symmetric) with double values
        double[][] distanceMatrix = {
            {0.0, 4.04, 6.41, 2.44, 2.98},
            {4.04, 0.0, 8.27, 7.15, 3.96},
            {6.41, 8.27, 0.0, 3.33, 4.32},
            {2.44, 7.15, 3.33, 0.0, 4.48},
            {2.98, 3.96, 4.32, 4.48, 0.0}
        };

        // City names corresponding to the indices
        List<String> cityNames = Arrays.asList("Sarajevo", "Zagreb", "Skopje", "Podgorica", "Belgrade");

        int n = distanceMatrix.length;

        double bestDistance = INF;
        List<Integer> bestTour = null;
        int bestStartCity = -1;

        // Start timing the computation
        long startTime = System.nanoTime();

        // Optimize tours starting from each city
        for (int startCity = 0; startCity < n; startCity++) {
            List<Integer> initialTour = generateInitialTour(n, startCity);
            System.out.println("\nStarting from " + cityNames.get(startCity));
            System.out.println("Initial Tour:");
            printTourWithCityNames(initialTour, cityNames);
            double initialDistance = calculateTourDistance(distanceMatrix, initialTour);
            System.out.println("Initial Distance: " +  String.format("%.2f", initialDistance));

            List<Integer> optimizedTour = linKernighan(distanceMatrix, initialTour);
            double optimizedDistance = calculateTourDistance(distanceMatrix, optimizedTour);

            System.out.println("Optimized Tour:");
            printTourWithCityNames(optimizedTour, cityNames);
            System.out.println("Optimized Distance: " + String.format("%.2f", optimizedDistance));

            // Update the best tour if this one is better
            if (optimizedDistance < bestDistance) {
                bestDistance = optimizedDistance;
                bestTour = optimizedTour;
                bestStartCity = startCity;
            }
        }

        // Stop timing the computation
        long endTime = System.nanoTime();

        // Calculate the elapsed time in milliseconds
        double elapsedTime = (endTime - startTime) / 1_000_000.0;

        // Output the best overall tour
        System.out.println("\nBest Tour Starting from " + cityNames.get(bestStartCity) + ":");
        printTourWithCityNames(bestTour, cityNames);
        System.out.println("Best Distance: " + String.format("%.2f", bestDistance));

        // Time taken
        System.out.printf("\nComputation Time: %.2f milliseconds\n", elapsedTime);

        // Hardware + Software declaration
        System.out.println("Operating System: " + System.getProperty("os.name"));
        System.out.println("OS Version: " + System.getProperty("os.version"));
        System.out.println("OS Architecture: " + System.getProperty("os.arch"));
        System.out.println("User Name: " + System.getProperty("user.name"));
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("Java Vendor: " + System.getProperty("java.vendor"));
    }
}
