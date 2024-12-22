import java.util.Arrays;

public class HeldKarpTSP {
    private static final int INF = Integer.MAX_VALUE / 2; // Prevent integer overflow for large numbers

    public static int tsp(int[][] distance) {
        int n = distance.length;
        int[][] dp = new int[1 << n][n]; // DP table: dp[mask][i]
        
        // Initialize DP table with a large value
        for (int[] row : dp) {
            Arrays.fill(row, INF);
        }
        
        // Base case: Starting from city 0 to other cities
        dp[1][0] = 0; // Start at city 0, with only city 0 visited
        
        // Iterate over all subsets of cities
        for (int mask = 1; mask < (1 << n); mask++) {
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) == 0) continue; // Skip if city i is not in the subset
                
                for (int j = 0; j < n; j++) {
                    if ((mask & (1 << j)) != 0 || distance[j][i] == INF) continue; // Skip if city j is already in the subset
                    int prevMask = mask ^ (1 << i); // Remove city i from the subset
                    dp[mask][i] = Math.min(dp[mask][i], dp[prevMask][j] + distance[j][i]);
                }
            }
        }
        
        // Find the minimum cost of returning to the starting city
        int finalMask = (1 << n) - 1; // All cities visited
        int minCost = INF;
        for (int i = 1; i < n; i++) {
            if (distance[i][0] != INF) {
                minCost = Math.min(minCost, dp[finalMask][i] + distance[i][0]);
            }
        }
        
        return minCost;
    }

    public static void main(String[] args) {
        // Example distance matrix (symmetric)
        int[][] distance = {
            {0, 10, 15, 20},
            {10, 0, 35, 25},
            {15, 35, 0, 30},
            {20, 25, 30, 0}
        };

        int result = tsp(distance);
        System.out.println("The minimum cost to complete the TSP is: " + result);
    }
}