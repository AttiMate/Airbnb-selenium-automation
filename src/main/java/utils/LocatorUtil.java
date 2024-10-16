package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.openqa.selenium.By;

public class LocatorUtil {

  private static Properties locators;

  static {
    locators = new Properties();
    try {
      FileInputStream fileInputStream =
          new FileInputStream("src/main/java/resources/locators.properties");
      locators.load(fileInputStream);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static By getLocator(String key) {
    String locator = locators.getProperty(key);
    if (locator == null) {
      throw new IllegalArgumentException(
          "Locator with key: " + key + " not found in locators.properties");
    }

    String[] locatorParts = locator.split(":", 2);
    if (locatorParts.length < 2) {
      throw new IllegalArgumentException(
          "Locator format for key: " + key + " is incorrect. Expected format is 'type:value'");
    }

    String type = locatorParts[0].trim();
    String value = locatorParts[1].trim();

    switch (type) {
      case "id":
        return By.id(value);
      case "css":
        return By.cssSelector(value);
      case "xpath":
        return By.xpath(value);
      default:
        throw new IllegalArgumentException("Unsupported locator type: " + type);
    }
  }
}
