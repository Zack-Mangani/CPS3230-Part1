package com.Wea;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;


public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("WeatherWear.com");
        System.out.println("---------------");
        System.out.println("1. Recommend clothing for current location");
        System.out.println("2. Recommend clothing for future location");
        System.out.println("3. Exit");
        System.out.println("Enter choice: __");

        int choice = sc.nextInt();
        sc.nextLine();

        WeatherRecommendation.processUserChoice(choice);

    }
}










