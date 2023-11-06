package com.Wea;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;


public class WeatherRecommendation {
    public WeatherService weatherService;

        public WeatherRecommendation(WeatherService weatherService) {
        this.weatherService = weatherService;
    }


        public String generateRecommendation() {
            double temperature = weatherService.getTemperature();
            double precipitation = weatherService.getPrecipitation();

            String temperatureDescription;
            String clothingDescription;
            String rainingDescription;
            String umbrellaDescription;

            if (temperature <= 15) {
                temperatureDescription = "cold";
                clothingDescription = "warm";
            } else {
                temperatureDescription = "warm";
                clothingDescription = "light";
            }

            if (precipitation > 0) {
                rainingDescription = "currently";
                umbrellaDescription = "do";
            } else {
                rainingDescription = "not";
                umbrellaDescription = "don't";
            }

            return String.format("It is %s so you should wear %s clothing. It is %s raining so you %s need an umbrella.",
                    temperatureDescription, clothingDescription, rainingDescription, umbrellaDescription);
        }



    public static Weather fetchWeatherData(String code, String inputDate) {

            String apiKeyWeather = "0f64237657d7432c9d0163007232110";
            String apiUrl = "https://api.weatherapi.com/v1/forecast.json?key=" + apiKeyWeather + "&q=" + code + "&dt=" + inputDate;

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(3000);

                int responseCode = connection.getResponseCode();

                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONObject jsonResponse = new JSONObject(response.toString());

                    JSONArray forecastday = jsonResponse.getJSONObject("forecast").getJSONArray("forecastday");

                    JSONObject firstDay = forecastday.getJSONObject(0);

                    double maxTemp = firstDay.getJSONObject("day").getDouble("maxtemp_c");
                    double totalPrecipitation = firstDay.getJSONObject("day").getDouble("totalprecip_mm");

                    //System.out.println(maxTemp);
                    //System.out.println(totalPrecipitation);

                    String ipaddress = "";
                    connection.disconnect();

                    WeatherServiceStub weatherServiceStub = new WeatherServiceStub();
                    weatherServiceStub.setTemperature(maxTemp);
                    weatherServiceStub.setPrecipitation(totalPrecipitation);

                    WeatherRecommendation recommendation = new WeatherRecommendation(weatherServiceStub);
                    String result = recommendation.generateRecommendation();
                    System.out.println(result);
                } else {
                    System.out.println("API request failed with response code: " + responseCode);
                }

        } catch (java.net.SocketTimeoutException timeoutException) {
        System.out.println("Connection to the primary service provider timed out. Switching to an alternative service.");
    } catch (Exception e) {
        e.printStackTrace();
    }

    return null;
}

    public static Weather fetchIPAndWeatherData() {
        String ipAddress = null;
        double temperature;
        double precipitation;

        try {
            String apiUrlIP = "https://api.ipgeolocation.io/ipgeo";
            String apiKeyIP = "30d3da6122594e789b698e3e25756f84";

            URL urlIP = new URL(apiUrlIP + "?apiKey=" + apiKeyIP);

            HttpURLConnection connectionIP = (HttpURLConnection) urlIP.openConnection();
            connectionIP.setRequestMethod("GET");
            connectionIP.setConnectTimeout(3000);

            int responseCodeIP = connectionIP.getResponseCode();

            if (responseCodeIP == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connectionIP.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonResponseIP = new JSONObject(response.toString());

                ipAddress = jsonResponseIP.getString("ip");

            } else {
                System.out.println("API request for IP address failed with response code: " + responseCodeIP);
            }

            connectionIP.disconnect();
            System.out.println(ipAddress);

            String apiKeyWeather = "0f64237657d7432c9d0163007232110";

            String apiUrlWeather = "https://api.weatherapi.com/v1/current.json?key=" + apiKeyWeather + "&q=" + ipAddress;
            URL urlWeather = new URL(apiUrlWeather);
            HttpURLConnection connectionWeather = (HttpURLConnection) urlWeather.openConnection();
            connectionWeather.setRequestMethod("GET");

            int responseCodeWeather = connectionWeather.getResponseCode();

            if (responseCodeWeather == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connectionWeather.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonResponseWeather = new JSONObject(response.toString());

                temperature = jsonResponseWeather.getJSONObject("current").getDouble("temp_c");
                precipitation = jsonResponseWeather.getJSONObject("current").getDouble("precip_mm");
                //System.out.println(temperature);
                //System.out.println(precipitation);

                WeatherServiceStub weatherServiceStub = new WeatherServiceStub();
                weatherServiceStub.setTemperature(temperature);
                weatherServiceStub.setPrecipitation(precipitation);

                WeatherRecommendation recommendation = new WeatherRecommendation(weatherServiceStub);
                String result = recommendation.generateRecommendation();
                System.out.println(result);


            } else {
                System.out.println("API request for weather data failed with response code: " + responseCodeWeather);
            }

            connectionWeather.disconnect();
        } catch (java.net.SocketTimeoutException timeoutException) {
            System.out.println("Connection to the primary service provider timed out. Switching to an alternative service.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    return null;
}

    public static void recommendClothingForCurrentLocation() {
        Weather weatherData = WeatherRecommendation.fetchIPAndWeatherData();
    }

    public static String recommendClothingForFutureLocation(String code, String dateOfArrival) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate arrivalDate;

        try {
            arrivalDate = LocalDate.parse(dateOfArrival, dateFormatter);
        } catch (DateTimeParseException e) {
            return "Invalid date format";
        }

        LocalDate currentDate = LocalDate.now();

        // Calculate the difference in days
        long daysDifference = currentDate.until(arrivalDate).getDays();

        if (daysDifference >= 0 && daysDifference <= 10) {
            // Date is within the allowed range
            Weather weatherData = fetchWeatherData(code, dateOfArrival);
            // Rest of your code to process weather data
            return "Date Valid";
        } else {
            return "Date cannot be more than 10 days in the future.";
        }
    }


    public static String processUserChoice(int choice) {
        switch (choice) {
            case 1:
                recommendClothingForCurrentLocation();
                return "Recommendation for Current:";
            case 2:
                Scanner sc = new Scanner(System.in);
                System.out.println("Enter Airport ICAO Code");
                String code = sc.nextLine();
                System.out.println("Enter Date of arrival in YYYY-MM-DD format");
                String dateOfArrival = sc.nextLine();
                recommendClothingForFutureLocation(code,dateOfArrival);
                return "Recommendation for specified date:";
            case 3:
                System.out.println("Goodbye!");
                return "Goodbye!";
            default:
                System.out.println("Invalid choice. Stopping");
                return "Invalid choice. Stopping";
        }
    }

}


