package edu.escuelaing.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Luis Giraldo
 * @version 1.0
 */
public class HttpServer {
    static HttpServer instance = null;

    String okResponseHeader = "HTTP/1.1 200 OK\r\n"
            + "Content-Type: text/html\r\n"
            + "\r\n";
    String notFoundResponse = "HTTP/1.1 404 NOT FOUND\r\n"
            + "Content-Type: text/html\r\n"
            + "\r\n";

    String htmlClient = "<!DOCTYPE html>" +
            "<html>" +
            "    <head>" +
            "        <title>Reflective ChatGPT</title>" +
            "        <meta charset=\"UTF-8\">" +
            "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
            "    </head>" +
            "        <h1>Hola dime que necesitas</h1>" +
            "        <form action=\"/hellopost\">" +
            "            <label for=\"postname\">Name:</label><br>" +
            "            <input type=\"text\" id=\"postname\" name=\"name\" value=\"John\"><br><br>" +
            "            <input type=\"button\" value=\"Submit\" onclick=\"loadPostMsg(postname)\">" +
            "        </form>" +
            "        " +
            "        <div id=\"postrespmsg\"></div>" +
            "        " +
            "        <script>" +
            "            function loadPostMsg(name){" +
            "                let url = \"/consulta?comando=\" + name.value;" +
            "" +
            "                fetch (url, {method: 'POST'})" +
            "                    .then(x => x.text())" +
            "                    .then(y => document.getElementById(\"postrespmsg\").innerHTML = y);" +
            "            }" +
            "        </script>" +
            "    </body>" +
            "</html>";


    public static HttpServer getInstance() {
        if (instance != null) {
            return instance;
        }
        instance = new HttpServer();
        return instance;
    }

    public void run() throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(36000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }
        Boolean running = true;
        while (running) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            PrintWriter out = new PrintWriter(
                    clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String inputLine, outputLine;
            String headers = "";
            while ((inputLine = in.readLine()).length() != 0) {
                headers += inputLine + "\r\n";
            }
            StringBuilder body = new StringBuilder();
            while (in.ready()) {
                body.append(in.read());
            }
            String method = headers.split(" ")[0];
            String uri = headers.split(" ")[1];
            String response = okResponseHeader + htmlClient;
            if (uri.contains("consulta")) {
                if (method.equals("POST")) {
                    if (uri.contains("(") && uri.contains(")")) {
                        String requestedMethod = uri.split("comando=")[1].split("\\(")[0];
                        String[] requestedParams = uri.split("comando=")[1].replaceFirst("\\(","*").split("\\*")[1].replace(")","").split(",");
                        System.out.println(Arrays.toString(requestedParams));
                        switch (requestedMethod) {
                            case ("class"):
                                try {
                                    Class<?> searchedClass = Class.forName(requestedParams[0].replace(")",""));
                                    String methodsS = "", fieldsS = "";
                                    for (Field field : searchedClass.getFields()) {
                                        fieldsS += field.toString() + " ";
                                    }
                                    for (Method methodInClass : searchedClass.getMethods()) {
                                        methodsS += methodInClass.getName()+ " ";
                                    }
                                    response = okResponseHeader + "variables:" + fieldsS + "metodos:" + methodsS;
                                } catch (ClassNotFoundException e) {
                                    response = okResponseHeader + "la clase no se encontro";;
                                }
                                break;
                            case ("invoke"):
                                try {
                                    Class<?> searchedClass = Class.forName(requestedParams[0]);
                                    String methodRes = searchedClass.getMethod(requestedParams[1]).invoke(null).toString();
                                    response = okResponseHeader + methodRes;
                                } catch (ClassNotFoundException e) {
                                    throw new RuntimeException(e);
                                } catch (NoSuchMethodException e) {
                                    response = okResponseHeader + "el metodo no se encontro";
                                } catch (InvocationTargetException | IllegalAccessException e) {
                                    response = okResponseHeader + "no se pudo invocar el metodo";
                                }
                                break;
                            case ("unaryInvoke"):
                                try {
                                    Class<?> searchedClass = Class.forName(requestedParams[0]);
                                    ArrayList<String> params = new ArrayList<>();
                                    for (int i = 0; i < searchedClass.getMethod(requestedParams[1]).getParameterCount(); i++){
                                        params.add(requestedParams[2+i]);
                                    }
                                    String methodRes = searchedClass.getMethod(requestedParams[1]).invoke(null,params.toArray()).toString();
                                    response = okResponseHeader + methodRes;
                                } catch (ClassNotFoundException e) {
                                    throw new RuntimeException(e);
                                } catch (NoSuchMethodException e) {
                                    response = okResponseHeader + "el metodo no se encontro";
                                } catch (InvocationTargetException | IllegalAccessException e) {
                                    response = okResponseHeader + "no se pudo invocar el metodo";
                                }
                                break;
                            default:
                                response = okResponseHeader + "metodo no encontrado";
                        }
                    } else {
                        response = okResponseHeader + "ingrese un metodo valido";
                    }
                }
            }
            outputLine = response;
            out.println(outputLine);
            System.out.println(headers + body);
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }
}

