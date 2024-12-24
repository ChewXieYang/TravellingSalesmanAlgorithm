def held_karp_tsp(distance):
    """
    Solve the Traveling Salesman Problem (TSP) using the Held-Karp algorithm.
    : distance: A 2D list representing the distance matrix.
    :return: The minimum cost to complete the TSP.
    """
    INF = float('inf')  # Define infinity
    n = len(distance)  # Number of cities
    dp = [[INF] * n for _ in range(1 << n)]  # DP table: dp[mask][i]

    dp[1][0] = 0  # Base case: Start at city 0 with only city 0 visited

    # Iterate over all subsets of cities
    for mask in range(1 << n):
        for i in range(n):
            if (mask & (1 << i)) == 0:  # Skip if city i is not in the subset
                continue

            # Iterate over all possible previous cities
            for j in range(n):
                if not (mask & (1 << j)) or distance[j][i] == INF:  # Skip invalid transitions
                    continue

                prev_mask = mask ^ (1 << i)  # Remove city i from the subset
                dp[mask][i] = min(dp[mask][i], dp[prev_mask][j] + distance[j][i])  # Update DP table

    # Compute the minimum cost of completing the tour
    final_mask = (1 << n) - 1  # All cities visited
    min_cost = INF  # Initialize minimum cost as infinity
    for i in range(1, n):  # Consider all cities for the last leg of the tour
        if distance[i][0] != INF:
            min_cost = min(min_cost, dp[final_mask][i] + distance[i][0])  # Add the cost to return to city 0

    return min_cost  # Return the minimum cost of the TSP


if __name__ == "__main__":
    distance = [
        [0, 10, 15, 20],
        [10, 0, 35, 25],
        [15, 35, 0, 30],
        [20, 25, 30, 0]
    ]

    result = held_karp_tsp(distance)
    print("The minimum cost to complete the TSP is:", result)  # Expected output: 80
