package com.bali.events.balievents.configuration;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

@Configuration
@Slf4j
public class WebDriverConfig {

    @Bean
    @ConditionalOnProperty(name = "webdriver.local", havingValue = "true")
    public WebDriver devWebDriver() {
        return new ChromeDriver();
    }

    @Bean
    @ConditionalOnProperty(name = "webdriver.local", havingValue = "false")
    public WebDriver prodWebDriver() {
        final FirefoxOptions firefoxOptions = new FirefoxOptions();
        final URL removeWebDriver;
        try {
            removeWebDriver = URI.create("http://firefox:4444/wd/hub").toURL();
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
        log.info("Connecting to remote Web Driver..." + removeWebDriver);

        final WebDriver webDriver = new RemoteWebDriver(removeWebDriver, firefoxOptions);
        log.info("Web driver successfully connected!: " + webDriver);

        return webDriver;
    }
}
