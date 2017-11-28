package com.pralay.common.adapter.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdapterFlow {

    static CamelContext camelContext;
    final static Logger LOGGING = LoggerFactory.getLogger(AdapterFlow.class);

    public static void init(SimpleRegistry simpleRegistry) {
        camelContext = new DefaultCamelContext(simpleRegistry);
    }

    public static void addRoute(RouteBuilder routeBuilder) {
        LOGGING.info("Entering the AdapterFlow -> addRoute() of RouteBuilderFactory.");
        try {
            camelContext.addRoutes(routeBuilder);
        } catch (Exception e) {
            LOGGING.error("Entering the AdapterFlow -> addRoute() of RouteBuilderFactory :: " + e.getMessage());
        }
    }

    public static void start() {
        // camelContext = new DefaultCamelContext(simpleRegistry);
        LOGGING.info("Entering the AdapterFlow -> startCamel() of RouteBuilderFactory.");
        try {
            camelContext.start();
        } catch (Exception e) {
            LOGGING.error("Entering the AdapterFlow -> startCamel() of RouteBuilderFactory :: " + e.getMessage());
            stop();
        }
    }

    public static void stop() {
        LOGGING.info("Entering the AdapterFlow -> stopCamel() of RouteBuilderFactory.");
        try {
            if (camelContext != null)
                camelContext.stop();
        } catch (Exception e) {
            LOGGING.info("Entering the AdapterFlow -> stopCamel() of RouteBuilderFactory :: " + e.getMessage());
        }

    }

}
