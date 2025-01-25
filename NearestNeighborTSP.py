import csv
import math
import time
import platform

INF = float('inf')

def read_csv(file_path):
    """
    Reads the CSV file and constructs a distance matrix and city names list.
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
            city1, city2, distance = row[0].strip(), row[1].strip(), float(row[2].strip())
            if city1 not in city_index_map:
                city_index_map[city1] = len(city_names)
                city_names.append(city1)
            if city2 not in city_index_map:
                city_index_map[city2] = len(city_names)
                city_names.append(city2)
            edges.append((city1, city2, distance))

    n = len(city_names)
    distance_matrix = [[INF] * n for _ in range(n)]
    for i in range(n):
        distance_matrix[i][i] = 0

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
    number_of_cities = int(input("Enter the number of cities to consider (up to 50): "))

    try:
        city_names, distance_matrix = read_csv(file_path)
        n = len(distance_matrix)

        if number_of_cities > n:
            raise ValueError(f"The CSV file contains only {n} cities, but {number_of_cities} were requested.")
        if number_of_cities < 2:
            raise ValueError("You must select at least 2 cities to run the algorithm.")

        best_distance = INF
        best_tour = None
        best_start_city = -1

        start_time = time.time()

        for start_city in range(number_of_cities):
            tour = nearest_neighbor(distance_matrix, start_city)
            tour_distance = calculate_tour_distance(distance_matrix, tour)

            print(f"\nTour Starting from {city_names[start_city]}:")
            print_tour_with_city_names(tour, city_names)
            print(f"Distance: {tour_distance:.2f} km")

            if tour_distance < best_distance:
                best_distance = tour_distance
                best_tour = tour
                best_start_city = start_city

        end_time = time.time()
        elapsed_time = (end_time - start_time) * 1000

        print(f"\nBest Tour Starting from {city_names[best_start_city]}:")
        print_tour_with_city_names(best_tour, city_names)
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
