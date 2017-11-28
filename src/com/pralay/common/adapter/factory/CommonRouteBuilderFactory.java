package com.pralay.common.adapter.factory;

import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pralay.common.adapter.processor.CommonRouteBuilder;
import com.pralay.configuration.model.ServerData;

public class CommonRouteBuilderFactory {

    final static Logger LOG = LoggerFactory.getLogger(CommonRouteBuilderFactory.class);

    public static RouteBuilder getRouteBuilder(ServerData serverData) {
        LOG.info("Entering the getRouteBuilder() of RouteBuilderFactory.");
        try {
            return new CommonRouteBuilder(serverData);
        } catch (Exception e) {
            LOG.error("Error in RouteBuilderFactory " + e.getMessage());
        } finally {
            LOG.info("Exiting the getRouteBuilder() of RouteBuilderFactory.");
        }
        return null;
    }
}
