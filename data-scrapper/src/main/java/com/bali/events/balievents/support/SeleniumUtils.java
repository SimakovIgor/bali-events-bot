package com.bali.events.balievents.support;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

@UtilityClass
@Slf4j
public class SeleniumUtils {
    public static String getAttributeByXpath(final WebElement child,
                                             final String xpathExpression,
                                             final String attribute) {
        try { // получаем поле по полному пути (Например: "2024-2-1T23:50+8:00")
            return child.findElement(By.xpath(xpathExpression)).getAttribute(attribute);
        } catch (NoSuchElementException e) {
            log.info("Element not found: {}", e.getMessage());
            return null;
        }
    }

    public static String getAttributeByClass(final WebElement child,
                                             final String className,
                                             final String attribute) {
        try { // получаем поле по имени класса и конечному имени атрибута в указанном блоке
            return child.findElement(By.className(className)).getAttribute(attribute);
        } catch (NoSuchElementException e) {
            log.info("Element not found: {}", e.getMessage());
            return null;
        }
    }
}
