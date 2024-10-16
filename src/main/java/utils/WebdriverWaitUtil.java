package utils;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WebdriverWaitUtil {

  WebDriver driver;
  WebDriverWait wait;

  public WebdriverWaitUtil(WebDriver driver) {
    this.driver = driver;
    this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
  }

  public WebElement waitForElementVisible(By locator) {
    return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
  }

  public WebElement waitForElementClickable(By locator) {
    return wait.until(ExpectedConditions.elementToBeClickable(locator));
  }

  public void waitForElementsVisible(By locator) {
    wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
  }

  public <V> void waitForCondition(ExpectedCondition<V> condition, String errorMessage) {
    try {
      wait.until(condition);
    } catch (Exception e) {
      System.out.println(errorMessage);
      throw e;
    }
  }
}
