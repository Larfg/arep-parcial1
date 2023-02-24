package edu.escuelaing.app;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        HttpServer httpServer = HttpServer.getInstance();
        try {
            httpServer.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}