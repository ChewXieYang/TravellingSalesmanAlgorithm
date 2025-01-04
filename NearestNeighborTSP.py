import time

def nearest_neighbor_algorithm(fu_matrix, start_city=0):
    """
    Nearest Neighbor Algorithm for the Traveling Salesman Problem (TSP).
    Runs the algorithm starting from a specific city.

    Parameters
    ----------
    fu_matrix : 2D list or numpy array
        A matrix of fuel consumption between cities.
    start_city : int
        The starting city index (default is 0).

    Returns
    -------
    total_fu : float
        The total fuel consumption for the entire tour.
    """
    # City names corresponding to the indices in the FU matrix
    city_names = ["Sarajevo", "Zagreb", "Skopje", "Podgorica", "Belgrade"]

    n = len(fu_matrix)
    visited = [False] * n
    current_city = start_city
    total_fu = 0
    visited[current_city] = True

    # Iterate over all cities (except the first one)
    for _ in range(1, n):
        min_fu = float('inf')
        next_city = -1
        for j in range(n):
            if not visited[j] and fu_matrix[current_city][j] < min_fu:
                min_fu = fu_matrix[current_city][j]
                next_city = j

        total_fu += min_fu
        visited[next_city] = True
        current_city = next_city

    # The last move is to return to the starting city
    total_fu += fu_matrix[current_city][start_city]
    return total_fu


def analyze_algorithm(fu_matrix):
    """
    Analyze the Nearest Neighbor Algorithm by calculating the best case, worst case,
    and execution time for all possible starting cities.

    Parameters
    ----------
    fu_matrix : 2D list or numpy array
        A matrix of fuel consumption between cities.

    Returns
    -------
    results : dict
        Contains best case, worst case, and elapsed time for execution.
    """
    n = len(fu_matrix)
    best_case = float('inf')
    worst_case = float('-inf')
    best_city = -1
    worst_city = -1

    start_time = time.time()

    for start_city in range(n):
        total_fu = nearest_neighbor_algorithm(fu_matrix, start_city=start_city)
        if total_fu < best_case:
            best_case = total_fu
            best_city = start_city
        if total_fu > worst_case:
            worst_case = total_fu
            worst_city = start_city

    end_time = time.time()
    elapsed_time = end_time - start_time

    results = {
        "best_case": best_case,
        "best_city": best_city,
        "worst_case": worst_case,
        "worst_city": worst_city,
        "elapsed_time": elapsed_time
    }
    return results


# FU matrix for 5 cities (Sarajevo, Zagreb, etc.)
fu_matrix = [
    [0, 4.04, 6.41, 2.44, 2.98],
    [4.04, 0, 8.27, 7.15, 3.96],
    [6.41, 8.27, 0, 3.33, 4.32],
    [2.44, 7.15, 3.33, 0, 4.48],
    [2.98, 3.96, 4.32, 4.48, 0]
]

results = analyze_algorithm(fu_matrix)

print(f"Best Case: {results['best_case']:.2f} (Starting at city {results['best_city']})")
print(f"Worst Case: {results['worst_case']:.2f} (Starting at city {results['worst_city']})")
print(f"Time Elapsed: {results['elapsed_time']:.4f} seconds")
