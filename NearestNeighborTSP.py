def nearest_neighbor_algorithm(fu_matrix, start_city):
    """
    Nearest Neighbor Algorithm for the Traveling Salesman Problem (TSP).
    Calculates the route and total fuel consumption starting from a given city.

    Parameters
    ----------
    fu_matrix : 2D list
        A matrix of fuel consumption between cities.
    start_city : int
        The index of the starting city.

    Returns
    -------
    route : list
        The sequence of visited cities in the route.
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
    route = [city_names[current_city]]

    # Visit all cities
    for _ in range(1, n):
        min_fu = float('inf')
        next_city = -1
        for j in range(n):
            if not visited[j] and fu_matrix[current_city][j] < min_fu:
                min_fu = fu_matrix[current_city][j]
                next_city = j

        total_fu += min_fu
        visited[next_city] = True
        route.append(city_names[next_city])
        current_city = next_city

    # Return to the starting city
    total_fu += fu_matrix[current_city][start_city]
    route.append(city_names[start_city])

    return route, total_fu


def evaluate_all_routes(fu_matrix):
    """
    Evaluate all possible Nearest Neighbor routes starting from each city.

    This function takes a 2D list of fuel consumption between cities and
    evaluates all possible Nearest Neighbor routes starting from each city.
    The best and worst routes are determined and printed to the console,
    together with the computation time.

    Parameters
    ----------
    fu_matrix : 2D list
        A matrix of fuel consumption between cities.

    Returns
    -------
    None
    """
    # Get the names of the cities
    city_names = ["Sarajevo", "Zagreb", "Skopje", "Podgorica", "Belgrade"]

    # Get the number of cities
    n = len(fu_matrix)

    # Initialize the best and worst routes
    best_route = None
    best_cost = float('inf')
    worst_route = None
    worst_cost = float('-inf')

    # Print a message indicating that we are about to evaluate all possible routes
    print("Evaluating all possible routes:")

    # Measure computation time
    import time
    start_time = time.time()

    # Iterate over all cities as the starting city
    for start_city in range(n):
        # Evaluate the route and get the total fuel consumption
        route, total_fu = nearest_neighbor_algorithm(fu_matrix, start_city)

        # Print the route and cost
        print(f"Route: {' -> '.join(route)} | Cost: {total_fu:.2f}")

        # Update the best and worst routes
        if total_fu < best_cost:
            best_cost = total_fu
            best_route = route
        if total_fu > worst_cost:
            worst_cost = total_fu
            worst_route = route

    # Measure computation time
    end_time = time.time()
    computation_time = (end_time - start_time) * 1000  # Convert to milliseconds

    # Print results
    print("\nBest Route: " + " -> ".join(best_route))
    print(f"Minimum Cost: {best_cost:.2f}")
    print("\nWorst Route: " + " -> ".join(worst_route))
    print(f"Maximum Cost: {worst_cost:.2f}")
    print(f"\nComputation Time: {computation_time:.2f} milliseconds")
    print("\nTesting on device: MSI GF75 Thin 9SC")
    print("Operating System: Windows 11 Pro (Version 24H2, Build 26100.2605)")


# FU matrix for 5 cities (Sarajevo, Zagreb, etc.)
fu_matrix = [
    [0, 4.04, 6.41, 2.44, 2.98],
    [4.04, 0, 8.27, 7.15, 3.96],
    [6.41, 8.27, 0, 3.33, 4.32],
    [2.44, 7.15, 3.33, 0, 4.48],
    [2.98, 3.96, 4.32, 4.48, 0]
]

# Evaluate all Nearest Neighbor routes
evaluate_all_routes(fu_matrix)
