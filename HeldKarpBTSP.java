import java.util.Arrays;

public class HeldKarpBTSP {
    // Number of cities
    private final int n;

    // Adjacency matrix for graph
    private final int[][] graph;

    // DP table
    private final int[][] dp;

    // Constructor
    public HeldKarpBTSP(int[][] graph) {
        this.n = graph.length;
        this.graph = graph;
        this.dp = new int[1 << n][n];

        // Initialize DP table
        for (int[] row : dp) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }

        // Base case: Starting at each city from the empty set
        for (int i = 0; i < n; i++) {
            dp[1 << i][i] = 0;
        }
    }

    public int solve() {
        // Iterate over all subsets of cities
        for (int mask = 0; mask < (1 << n); mask++) {
            for (int last = 0; last < n; last++) {
                if ((mask & (1 << last)) == 0) continue; // City 'last' must be in the subset

                for (int prev = 0; prev < n; prev++) {
                    if ((mask & (1 << prev)) == 0 || prev == last) continue;

                    // Transition: Update DP for subset `mask` ending at `last`
                    dp[mask][last] = Math.min(
                        dp[mask][last],
                        Math.max(dp[mask ^ (1 << last)][prev], graph[prev][last])
                    );
                }
            }
        }

        // Reconstruct the solution from DP table
        int result = Integer.MAX_VALUE;
        int fullMask = (1 << n) - 1;

        for (int last = 0; last < n; last++) {
            result = Math.min(result, dp[fullMask][last]);
        }

        return result;
    }

    public static void main(String[] args) {
        int[][] graph = {
            {0, 10, 15, 20},
            {10, 0, 35, 25},
            {15, 35, 0, 30},
            {20, 25, 30, 0}
        };

        // Print the graph
        System.out.println("Graph Representation:");
        for (int[] row : graph) {
            System.out.println(Arrays.toString(row));
        }

        HeldKarpBTSP solver = new HeldKarpBTSP(graph);
        int bottleneckCost = solver.solve();
        System.out.println("Minimum Bottleneck Cost: " + bottleneckCost);
    }
}
