package de.ravens.arima.pod.core;

import io.github.cdimascio.dotenv.Dotenv;

public class TokenProvider {
    private static final Dotenv dotenv = Dotenv.load();

    public static String provideToken() {
        return dotenv.get("TOKEN");
    }
}
