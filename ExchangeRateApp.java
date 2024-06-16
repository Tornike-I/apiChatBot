import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class ExchangeRateApp {
    private static final String EXCHANGE_API_KEY = "0a799fb0850142f64f578916";
    private static final String EXCHANGE_API_URL = "https://v6.exchangerate-api.com/v6/" + EXCHANGE_API_KEY + "/pair/";

    private static final String WEATHER_API_KEY = "b46cafe955b40b6237a28fe9675e3637";  // Replace with your OpenWeatherMap API key
    private static final String WEATHER_API_URL = "http://api.openweathermap.org/data/2.5/weather";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the base currency (e.g., USD): ");
        String baseCurrency = scanner.nextLine().trim().toUpperCase();

        System.out.print("Enter the target currency (e.g., EUR): ");
        String targetCurrency = scanner.nextLine().trim().toUpperCase();

        System.out.print("Enter the location for weather information (e.g., London): ");
        String location = scanner.nextLine().trim();

        String exchangeRate = getExchangeRate(baseCurrency, targetCurrency);
        System.out.println("Exchange Rate from " + baseCurrency + " to " + targetCurrency + ": " + exchangeRate);

        String weather = getWeather(location);
        System.out.println("Weather in " + location + ": " + weather);

        scanner.close();
    }

    private static String getExchangeRate(String baseCurrency, String targetCurrency) {
        String requestUrl = EXCHANGE_API_URL + baseCurrency + "/" + targetCurrency;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String result = response.body();
                return parseExchangeRate(result);
            } else {
                return "Error fetching exchange rate: " + response.statusCode();
            }
        } catch (Exception e) {
            return "Error fetching exchange rate: " + e.getMessage();
        }
    }

    private static String parseExchangeRate(String json) {
        String key = "\"conversion_rate\":";
        int startIndex = json.indexOf(key);
        if (startIndex == -1) {
            return "Invalid response from API";
        }
        startIndex += key.length();
        int endIndex = json.indexOf(",", startIndex);
        if (endIndex == -1) {
            endIndex = json.indexOf("}", startIndex);
        }
        if (endIndex == -1) {
            return "Invalid response format";
        }
        return json.substring(startIndex, endIndex).trim();
    }

    private static String getWeather(String location) {
        String requestUrl = WEATHER_API_URL + "?q=" + location + "&appid=" + WEATHER_API_KEY + "&units=metric";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String result = response.body();
                return parseWeather(result);
            } else {
                return "Error fetching weather information: " + response.statusCode();
            }
        } catch (Exception e) {
            return "Error fetching weather information: " + e.getMessage();
        }
    }

    private static String parseWeather(String json) {
        String tempKey = "\"temp\":";
        int tempStartIndex = json.indexOf(tempKey);
        if (tempStartIndex == -1) {
            return "Invalid response from API";
        }
        tempStartIndex += tempKey.length();
        int tempEndIndex = json.indexOf(",", tempStartIndex);
        if (tempEndIndex == -1) {
            tempEndIndex = json.indexOf("}", tempStartIndex);
        }
        if (tempEndIndex == -1) {
            return "Invalid response format";
        }
        String temperature = json.substring(tempStartIndex, tempEndIndex).trim();

        String weatherKey = "\"description\":\"";
        int weatherStartIndex = json.indexOf(weatherKey);
        if (weatherStartIndex == -1) {
            return "Invalid response from API";
        }
        weatherStartIndex += weatherKey.length();
        int weatherEndIndex = json.indexOf("\"", weatherStartIndex);
        if (weatherEndIndex == -1) {
            return "Invalid response format";
        }
        String description = json.substring(weatherStartIndex, weatherEndIndex).trim();

        return temperature + "°C, " + description;
    }
}
