import itertools

def calculate_cost(graph, route):
    """Calculate the cost of a given route."""
    cost = 0
    for i in range(len(route) - 1):
        cost += graph[route[i]][route[i + 1]]
    # Add the cost to return to the starting city
    cost += graph[route[-1]][route[0]]
    return cost

def solve_tsp(graph, city_names):
    """Solve the Travelling Salesman Problem using brute force."""
    n = len(graph)
    cities = list(range(1, n))  # Start from city 1 to n-1 (city 0 is the starting point)

    # Generate all permutations of the cities
    permutations = itertools.permutations(cities)

    min_cost = float('inf')
    max_cost = float('-inf')
    best_route = None
    worst_route = None

    print("Evaluating all possible routes:")

    for route in permutations:
        full_route = [0] + list(route) + [0]  # Include the starting and ending city
        cost = calculate_cost(graph, full_route)

        # Print the route and its cost
        route_str = " -> ".join(city_names[city] for city in full_route)
        print(f"Route: {route_str} | Cost: {cost:.2f}")

        # Update best route
        if cost < min_cost:
            min_cost = cost
            best_route = full_route

        # Update worst route
        if cost > max_cost:
            max_cost = cost
            worst_route = full_route

    # Print best route and cost
    if best_route:
        best_route_str = " -> ".join(city_names[city] for city in best_route)
        print(f"\nBest Route: {best_route_str}\nMinimum Cost: {min_cost:.2f}")

    # Print worst route and cost
    if worst_route:
        worst_route_str = " -> ".join(city_names[city] for city in worst_route)
        print(f"\nWorst Route: {worst_route_str}\nMaximum Cost: {max_cost:.2f}")

if __name__ == "__main__":
    # Example graph (distance matrix)
    graph = [
        [0, 4.04, 6.41, 2.44, 2.98],
        [4.04, 0, 8.27, 7.15, 3.96],
        [6.41, 8.27, 0, 3.33, 4.32],
        [2.44, 7.15, 3.33, 0, 4.48],
        [2.98, 3.96, 4.32, 4.48, 0]
    ]

    city_names = ["Sarajevo", "Zagreb", "Skopje", "Podgorica", "Belgrade"]

    solve_tsp(graph, city_names)