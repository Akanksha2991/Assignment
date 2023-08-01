package nimesa;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class WeatherForecast {
    private static final String API_KEY = "YOUR_API_KEY";
    private static final String API_URL = "https://samples.openweathermap.org/data/2.5/forecast/hourly?q=London,us&appid=b6907d289e10d714a6e88b30761fae22";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter city name: ");
        String city = scanner.nextLine();

        String apiUrl = String.format(API_URL, city, API_KEY);

        try {
            String weatherData = getWeatherData(apiUrl);
            if (weatherData == null) {
                System.out.println("Failed to fetch weather data.");
                return;
            }

            JSONObject jsonData = new JSONObject(weatherData);
           
            while (true) {
                printMenu();
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();

                if (choice == 0) {
                    System.out.println("Exiting the program.");
                    break;
                }

                System.out.print("Enter the date (yyyy-mm-dd): ");
                scanner.nextLine(); // Clear buffer
                String date = scanner.nextLine();

                switch (choice) {
                    case 1:
                        double temperature = getTemperature(jsonData, date);
                        if (temperature != Double.MIN_VALUE) {
                            System.out.printf("Temperature on %s: %.2f Kelvin%n", date, temperature);
                        } else {
                            System.out.println("Data not available for the given date.");
                        }
                        break;
                    case 2:
                        double windSpeed = getWindSpeed(jsonData, date);
                        if (windSpeed != Double.MIN_VALUE) {
                            System.out.printf("Wind Speed on %s: %.2f m/s%n", date, windSpeed);
                        } else {
                            System.out.println("Data not available for the given date.");
                        }
                        break;
                    case 3:
                        double pressure = getPressure(jsonData, date);
                        if (pressure != Double.MIN_VALUE) {
                            System.out.printf("Pressure on %s: %.2f hPa%n", date, pressure);
                        } else {
                            System.out.println("Data not available for the given date.");
                        }
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a valid option.");
                        break;
                }
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static String getWeatherData(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNextLine()) {
                response.append(scanner.nextLine());
            }
            scanner.close();
            return response.toString();
        } else {
            return null;
        }
    }

    private static double getTemperature(JSONObject jsonData, String date) {
        JSONArray list = jsonData.getJSONArray("list");
        for (int i = 0; i < list.length(); i++) {
            JSONObject forecast = list.getJSONObject(i);
            String forecastDate = forecast.getString("dt_txt").substring(0, 10);
            if (forecastDate.equals(date)) {
                return forecast.getJSONObject("main").getDouble("temp");
            }
        }
        return Double.MIN_VALUE;
    }

    private static double getWindSpeed(JSONObject jsonData, String date) {
        JSONArray list = jsonData.getJSONArray("list");
        for (int i = 0; i < list.length(); i++) {
            JSONObject forecast = list.getJSONObject(i);
            String forecastDate = forecast.getString("dt_txt").substring(0, 10);
            if (forecastDate.equals(date)) {
                return forecast.getJSONObject("wind").getDouble("speed");
            }
        }
        return Double.MIN_VALUE;
    }

    private static double getPressure(JSONObject jsonData, String date) {
        JSONArray list = jsonData.getJSONArray("list");
        for (int i = 0; i < list.length(); i++) {
            JSONObject forecast = list.getJSONObject(i);
            String forecastDate = forecast.getString("dt_txt").substring(0, 10);
            if (forecastDate.equals(date)) {
                return forecast.getJSONObject("main").getDouble("pressure");
            }
        }
        return Double.MIN_VALUE;
    }

    private static void printMenu() {
        System.out.println("\nOptions:");
        System.out.println("1. Get weather");
        System.out.println("2. Get Wind Speed");
        System.out.println("3. Get Pressure");
        System.out.println("0. Exit");
    }
}
