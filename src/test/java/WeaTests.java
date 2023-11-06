import com.Wea.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class WeaTests {
    WeatherServiceStub weatherServiceStub;
    WeatherRecommendation recommendation;

    @BeforeEach
    public void setup() {
        weatherServiceStub = new WeatherServiceStub();
        recommendation = new WeatherRecommendation(weatherServiceStub);
    }

    @Test
    public void testGenerateRecommendationWithColdAndNoRain() {
        // SETUP
        weatherServiceStub.setTemperature(10.0);  // Cold
        weatherServiceStub.setPrecipitation(0.0);  // No rain

        // SETUP
        WeatherRecommendation recommendation = new WeatherRecommendation(weatherServiceStub);

        // EXERCISE
        String result = recommendation.generateRecommendation();

        // VERIFY
        assertEquals("It is cold so you should wear warm clothing. It is not raining so you don't need an umbrella.", result);
    }

    @Test
    public void testGenerateRecommendationWithWarmAndRain() {
        // SETUP
        weatherServiceStub.setTemperature(16.0);  // Warm
        weatherServiceStub.setPrecipitation(1.0);  // Rain

        // SETUP
        WeatherRecommendation recommendation = new WeatherRecommendation(weatherServiceStub);

        // EXERCISE
        String result = recommendation.generateRecommendation();

        // VERIFY
        assertEquals("It is warm so you should wear light clothing. It is currently raining so you do need an umbrella.", result);
    }


    @Test
    public void testFetchWeatherWithInvalidData() {
        // SETUP
        String code = "abc"; // invalid airport code
        String inputDate = "2023-11-31"; // invalid date

        // SETUP
        WeatherRecommendation recommendation = new WeatherRecommendation(weatherServiceStub);

        // EXERCISE
        String weatherData = String.valueOf(recommendation.fetchWeatherData(code, inputDate));

        // VERIFY
        assertEquals("null",weatherData);
    }

    @Test
    public void testRecommendClothingForFutureLocation_ValidDate() {
        // SETUP
        String code = "mla"; // valid airport code
        String inputDate = "2023-11-08"; // valid date
        // SETUP
        WeatherRecommendation recommendation = new WeatherRecommendation(weatherServiceStub);
        // EXERCISE
        String result = recommendation.recommendClothingForFutureLocation(code, inputDate);
        // VERIFY
        assertEquals("Date Valid", result);
    }

    @Test
    public void testRecommendClothingForFutureLocation_InvalidDate() {
        // SETUP
        String code = "mla"; // invalid airport code
        String inputDate = "2023-11-15"; // invalid date

        // EXERCISE
        String result = recommendation.recommendClothingForFutureLocation(code, inputDate);

        // VERIFY
        assertEquals("Date cannot be more than 10 days in the future.", result);
    }

    @Test
    public void testRecommendClothingForFutureLocation_InvalidDateFormat() {
        // SETUP
        String code = "BHR"; // valid airport code
        String inputDate = "2023-1122-10"; // invalid date format

        // EXERCISE
        WeatherRecommendation recommendation = new WeatherRecommendation(weatherServiceStub);
        // EXERCISE
        String result = recommendation.recommendClothingForFutureLocation("BHR", "2023-1122-10");
        // VERIFY
        assertEquals("Invalid date format", result);
    }


    @Test
    public void testProcessUserChoice_Choice1() {
        // SETUP
        String result = recommendation.processUserChoice(1);
        // EXERCISE
        WeatherRecommendation recommendation = new WeatherRecommendation(weatherServiceStub);
        // VERIFY
        assertEquals("Recommendation for Current:", result);
    }


    @Test
    public void testProcessUserChoice_Choice2() {
        // SETUP
        String code = "JFK";
        String dateOfArrival = "2023-10-26";

        String userInput = code + "\n" + dateOfArrival + "\n";
        InputStream inputStream = new ByteArrayInputStream(userInput.getBytes());
        InputStream originalIn = System.in;
        System.setIn(inputStream);

        // EXERCISE
        WeatherRecommendation recommendation = new WeatherRecommendation(weatherServiceStub);

        // Call the processUserChoice method with choice 2
        String result = recommendation.processUserChoice(2);

        // Reset System.in to the original input stream
        System.setIn(originalIn);

    }
    @Test
    public void testProcessUserChoice_Choice3() {
        // SETUP
        String result = recommendation.processUserChoice(3);

        // EXERCISE
        WeatherRecommendation recommendation = new WeatherRecommendation(weatherServiceStub);

        // VERIFY
        assertEquals("Goodbye!", result);
    }

    @Test
    public void testProcessUserChoice_InvalidChoice() {
        // SETUP
        String result = recommendation.processUserChoice(0);

        // EXERCISE
        WeatherRecommendation recommendation = new WeatherRecommendation(weatherServiceStub);

        // VERIFY
        assertEquals("Invalid choice. Stopping", result);
    }

}
