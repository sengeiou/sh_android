package com.shootr.mobile.service;

public class Endpoints {

    private Endpoints() {
    }

    /** Create a server with the provided URL. */
    public static Endpoint newFixedEndpoint(final String url) {
        return newFixedEndpoint(url, null);
    }

    /** Create an endpoint with the provided URL and name. */
    public static Endpoint newFixedEndpoint(final String url, final String name) {
        return new Endpoint() {
            @Override public String getUrl() {
                return url;
            }

            @Override public String getName() {
                return name;
            }
        };
    }
}
