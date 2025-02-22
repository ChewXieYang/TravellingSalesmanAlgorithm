import csv
import random
import math
import time
import platform

INF = float('inf')

def read_csv(file_path):
    """
    Reads the CSV file and constructs a distance matrix and city names list.

    The CSV file is expected to have the following format:
    City1,City2,Distance(km)
    CityName1,CityName2,DistanceInKilometers
    CityName3,CityName4,DistanceInKilometers
    ...

    The function returns a tuple containing the city names list and the distance matrix.
    The city names list is a list of strings, where each string is the name of a city.
    The distance matrix is a 2D list of floats, where distance_matrix[i][j] is the distance in kilometers between city i and city j.
    If there is no direct connection between city i and city j, then distance_matrix[i][j] is infinity.
    """
    city_names = []
    city_index_map = {}
    edges = []

    with open(file_path, 'r') as csvfile:
        reader = csv.reader(csvfile)
        header = next(reader)
        if header != ["City1", "City2", "Distance(km)"]:
            raise ValueError("CSV file must have header: City1,City2,Distance(km)")

        for row in reader:
            # For each row in the CSV file, extract the city names and the distance between them
            city1, city2, distance = row[0].strip(), row[1].strip(), float(row[2].strip())

            # If city1 is not already in the city_index_map, add it and its index to the map
            if city1 not in city_index_map:
                city_index_map[city1] = len(city_names)
                city_names.append(city1)

            # If city2 is not already in the city_index_map, add it and its index to the map
            if city2 not in city_index_map:
                city_index_map[city2] = len(city_names)
                city_names.append(city2)

            # Add the edge between the two cities to the edges list
            edges.append((city1, city2, distance))

    # Create a distance matrix with the same number of rows and columns as the number of cities
    n = len(city_names)
    distance_matrix = [[INF] * n for _ in range(n)]

    # For each city, set the distance to itself to 0
    for i in range(n):
        distance_matrix[i][i] = 0

    # For each edge, set the distance between the two cities to the given distance
    for city1, city2, distance in edges:
        index1 = city_index_map[city1]
        index2 = city_index_map[city2]
        distance_matrix[index1][index2] = distance
        distance_matrix[index2][index1] = distance

    return city_names, distance_matrix

def calculate_tour_distance(distance_matrix, tour):
    """
    Calculates the total distance of a given tour.
    """
    total_distance = 0
    for i in range(len(tour) - 1):
        total_distance += distance_matrix[tour[i]][tour[i + 1]]
    total_distance += distance_matrix[tour[-1]][tour[0]]  # Return to start
    return round(total_distance, 2)

def nearest_neighbor(distance_matrix, start_city):
    """
    Implements the Nearest Neighbor algorithm.
    """
    n = len(distance_matrix)
    tour = [start_city]
    visited = [False] * n
    visited[start_city] = True

    current_city = start_city
    for _ in range(n - 1):
        nearest_distance = INF
        nearest_city = -1

        for next_city in range(n):
            if not visited[next_city] and distance_matrix[current_city][next_city] < nearest_distance:
                nearest_distance = distance_matrix[current_city][next_city]
                nearest_city = next_city

        visited[nearest_city] = True
        tour.append(nearest_city)
        current_city = nearest_city

    return tour

def print_tour_with_city_names(tour, city_names):
    """
    Prints the tour with city names.
    """
    tour_names = [city_names[city] for city in tour]
    print(" -> ".join(tour_names) + f" -> {city_names[tour[0]]}")

def main():
    file_path = "DistanceBetweenEuropeanCities.csv"

    try:
        city_names, full_distance_matrix = read_csv(file_path)
        total_cities = len(city_names)

        # Ask the user for the number of cities
        while True:
            try:
                num_cities = int(input(f"Enter the number of cities to use (2-{min(50, total_cities)}): "))
                if 2 <= num_cities <= min(50, total_cities):
                    break
                else:
                    print(f"Please enter a valid number between 2 and {min(50, total_cities)}.")
            except ValueError:
                print("Invalid input. Please enter an integer.")

        # Select a subset of cities
        selected_indices = sorted(random.sample(range(total_cities), num_cities))
        selected_city_names = [city_names[i] for i in selected_indices]

        # Create a reduced distance matrix
        distance_matrix = [[full_distance_matrix[i][j] for j in selected_indices] for i in selected_indices]

        # Run the Nearest Neighbor Algorithm
        n = len(distance_matrix)
        best_distance = INF
        best_tour = None
        best_start_city = -1

        start_time = time.time()

        for start_city in range(n):
            tour = nearest_neighbor(distance_matrix, start_city)
            tour_distance = calculate_tour_distance(distance_matrix, tour)

            print(f"\nTour Starting from {selected_city_names[start_city]}:")
            print_tour_with_city_names(tour, selected_city_names)
            print(f"Distance: {tour_distance:.2f} km")

            if tour_distance < best_distance:
                best_distance = tour_distance
                best_tour = tour
                best_start_city = start_city

        end_time = time.time()
        elapsed_time = (end_time - start_time) * 1000

        print(f"\nBest Tour Starting from {selected_city_names[best_start_city]}:")
        print_tour_with_city_names(best_tour, selected_city_names)
        print(f"Best Distance: {best_distance:.2f} km")

        print(f"\nComputation Time: {elapsed_time:.2f} milliseconds")

        print("\nOperating System:", platform.system())
        print("OS Version:", platform.version())
        print("OS Architecture:", platform.architecture()[0])
        print("Python Version:", platform.python_version())

    except Exception as e:
        print("Error:", str(e))

if __name__ == "__main__":
    main()

