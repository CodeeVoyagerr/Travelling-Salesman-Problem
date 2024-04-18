import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class TSP85900 {

    static class City {
        int x, y;

        public City(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) {
        ArrayList<City> cities = readCitiesFromFile("tsp_85900_1.txt");

        // Simulated Annealing algoritması ile en düşük maliyetli yolun bulunması
        long startTime = System.currentTimeMillis();
        ArrayList<City> solution = simulatedAnnealing(cities);
        long endTime = System.currentTimeMillis();

        // Yolu ve maliyetini yazdır
        int cost = calculateCost(solution);
        System.out.println("En düşük maliyetli yol:");
        for (City city : solution) {
            System.out.print("(" + city.x + ", " + city.y + ") ");
        }
        System.out.println("\nMaliyet: " + cost);
        System.out.println("Çalışma Süresi: " + (endTime - startTime) + " milisaniye");

        // Sonuçları txt dosyasına yaz
        writeResultToFile("output85900.txt", solution, cost, endTime - startTime);
    }

    // Dosyadan şehir verilerini oku
    static ArrayList<City> readCitiesFromFile(String filename) {
        ArrayList<City> cities = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                if (lineNumber > 0) {
                    String[] parts = line.split(" ");
                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);
                    cities.add(new City(x, y));
                }
                lineNumber++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cities;
    }

    // Simulated Annealing algoritması
    static ArrayList<City> simulatedAnnealing(ArrayList<City> cities) {
        double temperature = 10000;
        double coolingRate = 0.003;

        ArrayList<City> currentSolution = new ArrayList<>(cities);
        ArrayList<City> bestSolution = new ArrayList<>(currentSolution);

        Random random = new Random();

        while (temperature > 1) {
            ArrayList<City> newSolution = new ArrayList<>(currentSolution);

            int randomCityIndex1 = random.nextInt(newSolution.size());
            int randomCityIndex2 = random.nextInt(newSolution.size());

            City city1 = newSolution.get(randomCityIndex1);
            City city2 = newSolution.get(randomCityIndex2);

            newSolution.set(randomCityIndex2, city1);
            newSolution.set(randomCityIndex1, city2);

            int currentCost = calculateCost(currentSolution);
            int newCost = calculateCost(newSolution);

            double acceptanceProbability = acceptanceProbability(currentCost, newCost, temperature);

            if (acceptanceProbability > Math.random()) {
                currentSolution = new ArrayList<>(newSolution);
            }

            if (calculateCost(currentSolution) < calculateCost(bestSolution)) {
                bestSolution = new ArrayList<>(currentSolution);
            }

            temperature *= 1 - coolingRate;
        }

        return bestSolution;
    }

    // Kabul olasılığını hesapla
    static double acceptanceProbability(int currentCost, int newCost, double temperature) {
        if (newCost < currentCost) {
            return 1.0;
        }
        return Math.exp((currentCost - newCost) / temperature);
    }

    // İki şehir arasındaki mesafeyi hesapla
    static int distance(City city1, City city2) {
        return (int) Math.sqrt(Math.pow(city1.x - city2.x, 2) + Math.pow(city1.y - city2.y, 2));
    }

    // Yolun maliyetini hesapla
    static int calculateCost(ArrayList<City> solution) {
        int cost = 0;
        for (int i = 0; i < solution.size() - 1; i++) {
            cost += distance(solution.get(i), solution.get(i + 1));
        }
        return cost;
    }

    // Sonuçları txt dosyasına yaz
    static void writeResultToFile(String filename, ArrayList<City> solution, int cost, long duration) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Maliyeti dosyaya yaz
            writer.write("Maliyet: " + cost);
            writer.newLine();

            // Çalışma süresini dosyaya yaz
            writer.write("Çalışma Süresi: " + duration + " milisaniye");
            writer.newLine();

            // TSP yolunu dosyaya yaz
            writer.write("TSP Yolu:");
            writer.newLine();
            for (City city : solution) {
                writer.write("(" + city.x + ", " + city.y + ")");
                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
