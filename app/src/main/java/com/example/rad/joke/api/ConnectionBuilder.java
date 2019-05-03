package com.example.rad.joke.api;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;



class ConnectionBuilder {



    static final int BUFFER_SIZE = 4096;

    private String method;
    private String url;


    ConnectionBuilder() {
    }

    ConnectionBuilder withUrl(String url) {
        this.url = url;
        return this;
    }


    HttpURLConnection get() throws ApiException {
        this.method = "GET";
        return build();
    }

    private HttpURLConnection build() throws ApiException {

        try {
            final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Accept-Language", "pl-PL");
            connection.connect();
            return connection;
        } catch (IOException e) {
            throw new ApiException(500, "IOException", e);
        }
    }

}