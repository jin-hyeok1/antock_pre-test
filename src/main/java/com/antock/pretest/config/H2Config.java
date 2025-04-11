package com.antock.pretest.config;

import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
@Slf4j
public class H2Config {
    private Server webServer;
    @Value("${h2.console.port}")
    private Integer port;
    @EventListener(ContextRefreshedEvent.class)
    public void start() throws SQLException {
        log.info("started h2 console at port {}", port);
        this.webServer = Server.createWebServer("-webPort", port.toString()).start();
    }

    @EventListener(ContextClosedEvent.class)
    public void stop() {
        log.info("stopped h2 console at port {}", port);
        this.webServer.stop();
    }
}
