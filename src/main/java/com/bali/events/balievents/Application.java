package com.bali.events.balievents;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner() {
        return args -> {
            WebDriver webDriver = new ChromeDriver();
            webDriver.get("https://thebeatbali.com/bali-events/");
//          webDriver.findElement(By.id("cookie_action_close_header")).click();
//          Thread.sleep(25000);
            WebElement element = webDriver.findElement(By.xpath("/html/body/div[1]/div[2]/div/div/main/article/div/div/section[3]/div/div[2]/div/div[2]/div/div/div[4]"));
            var childs = element.findElements(By.xpath("./child::*"));
            int ii = 1;
            for (WebElement child : childs) {
                System.out.println("Child: " + ii + "\n" + child.getAttribute("outerHTML") + "\n\n\n");
                ii ++;
//              String address = child.findElement(By.xpath("//p/a/span[3]/span[2]")).getAttribute("data-location_address");
//              System.out.println(address);
            }
            webDriver.quit(); // закрываем окно браузера
        };
    }

}
