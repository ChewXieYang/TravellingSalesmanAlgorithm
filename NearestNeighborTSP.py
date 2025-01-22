import numpy as np
import random
import time
import matplotlib.pyplot as plt

# Function to generate random cities
def generate_cities(num_cities):
    return np.random.rand(num_cities, 2) * 100  # Random coordinates in a 100x100 area

# Function to calculate the distance matrix
def calculate_distance_matrix(cities):
    num_cities = len(cities)
    distance_matrix = np.zeros((num_cities, num_cities))
    for i in range(num_cities):
        for j in range(num_cities):
            if i != j:
                distance_matrix[i][j] = np.linalg.norm(cities[i] - cities[j])
    return distance_matrix

# Nearest Neighbour Algorithm
def nearest_neighbour(distance_matrix):
    n = len(distance_matrix)
    visited = [False] * n
    total_distance = 0
    current_city = 0
    visited[current_city] = True

    for _ in range(1, n):
        nearest_city = -1
        nearest_distance = float('inf')

        for j in range(n):
            if not visited[j] and distance_matrix[current_city][j] < nearest_distance:
                nearest_distance = distance_matrix[current_city][j]
                nearest_city = j

        visited[nearest_city] = True
        total_distance += nearest_distance
        current_city = nearest_city

    total_distance += distance_matrix[current_city][0]  # Return to starting city
    return total_distance

# Experiment function to run the Nearest Neighbour algorithm and measure time
def run_experiment(num_cities):
    cities = generate_cities(num_cities)
    distance_matrix = calculate_distance_matrix(cities)

    # Measure Nearest Neighbour
    start_time = time.time()
    nn_distance = nearest_neighbour(distance_matrix)
    nn_time = time.time() - start_time

    return nn_distance, nn_time

# Main function to execute experiments for different city counts
def main():
    city_counts = [10, 20, 50]
    nn_times = []
    nn_distances = []

    for count in city_counts:
        distance, time_taken = run_experiment(count)
        nn_distances.append(distance)
        nn_times.append(time_taken)

    # Print results
    for i in range(len(city_counts)):
        print(f"Cities: {city_counts[i]}, Distance: {nn_distances[i]}, Time: {nn_times[i]:.6f} seconds")

    # Plotting the results
    plt.plot(city_counts, nn_times, label='Nearest Neighbour', marker='o')

    plt.xlabel('Number of Cities')
    plt.ylabel('Running Time (seconds)')
    plt.title('Running Time of Nearest Neighbour Algorithm')
    plt.legend()
    plt.grid()
    plt.show()

if __name__ == "__main__":
    main()
