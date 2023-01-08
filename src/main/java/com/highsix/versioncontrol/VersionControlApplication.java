package com.highsix.versioncontrol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.LogManager;

@SpringBootApplication
public class VersionControlApplication {

    private static LogManager logManager = LogManager.getLogManager();

    public static void main(String[] args) {
        SpringApplication.run(VersionControlApplication.class, args);

        configureLogging();
    }

    private static void configureLogging(){
        try {
            logManager.readConfiguration(new FileInputStream(System.getProperty("user.dir")+"/src/main/resources/logging.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
