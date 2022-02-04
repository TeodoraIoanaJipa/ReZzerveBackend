package com.teo.foodzzzbackend.model;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class RestaurantsAppLogger {
    private static RestaurantsAppLogger reference;
    private final String logFile = "demo_log.txt";
    private PrintWriter writer;

    public RestaurantsAppLogger() {
        try {
            FileWriter fw = new FileWriter(logFile);
            writer = new PrintWriter(fw, true);
        } catch (IOException e) {
        }
    }

    public static RestaurantsAppLogger getInstance() {
        if (reference == null) {
            reference = new RestaurantsAppLogger();
        }
        return reference;
    }


    public void logUserLogin(String username) {
        writer.println("User logger in : " + username);
    }

    public void logRestaurantView(Restaurant restaurant, String username) {
        writer.println("Restaurant with id " + restaurant.getId() + " was visualised by " + username);
    }
}
