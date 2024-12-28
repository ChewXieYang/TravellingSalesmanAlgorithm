def nearest_neighbor_algorithm(fu_matrix):
    """
    Nearest Neighbor Algorithm for the Traveling Salesman Problem (TSP).

    Given a matrix of fuel consumption between cities, this algorithm visits each
    city once and returns to the starting city. The algorithm starts at an
    arbitrary city and at each step, moves to the nearest unvisited city, until
    all cities have been visited.

    Parameters
    ----------
    fu_matrix : 2D list or numpy array
        A matrix of fuel consumption between cities, where fu_matrix[i][j]
        represents the fuel consumption when traveling from city i to city j.

    Returns
    -------
    total_fu : float
        The total fuel consumption for the entire tour.

    """
    
    # City names corresponding to the indices in the FU matrix
    city_names = ["Sarajevo", "Zagreb", "Skopje", "Podgorica", "Belgrade"]

    n = len(fu_matrix)
    visited = [False] * n
    current_city = 0
    total_fu = 0
    visited[current_city] = True

    print(f"Starting at city: {city_names[current_city]}")

    # Iterate over all cities (except the first one)
    for _ in range(1, n):
        min_fu = float('inf')
        next_city = -1
        for j in range(n):
            # Check if the city has not been visited before and if the fuel
            # consumption between the current city and the next city is lower
            # than the current minimum fuel consumption
            if not visited[j] and fu_matrix[current_city][j] < min_fu:
                min_fu = fu_matrix[current_city][j]
                next_city = j
        
        # Debugging output for each step
        print(f"Current city: {city_names[current_city]}")
        print(f"Nearest unvisited city: {city_names[next_city]}")
        print(f"Fuel consumption for this move: {min_fu:.2f}")

        total_fu += min_fu
        visited[next_city] = True
        current_city = next_city

    # The last move is to return to the starting city
    total_fu += fu_matrix[current_city][0]
    print(f"Returning from city {city_names[current_city]} to city {city_names[0]} (Fuel Consumption: {fu_matrix[current_city][0]:.2f})")

    return total_fu

# FU matrix for 5 cities (Sarajevo, Zagreb, etc.)
fu_matrix = [
    [0, 4.04, 6.41, 2.44, 2.98],
    [4.04, 0, 8.27, 7.15, 3.96],
    [6.41, 8.27, 0, 3.33, 4.32],
    [2.44, 7.15, 3.33, 0, 4.48],
    [2.98, 3.96, 4.32, 4.48, 0]
]

total_fu = nearest_neighbor_algorithm(fu_matrix)
print(f"Total Fuel Usage (FU): {total_fu:.2f}")
