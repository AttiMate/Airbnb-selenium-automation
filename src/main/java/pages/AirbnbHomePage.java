package pages;

import org.openqa.selenium.*;
import utils.LocatorUtil;
import utils.WebdriverWaitUtil;

public class AirbnbHomePage {

  WebDriver driver;
  WebdriverWaitUtil waitUtil;
  JavascriptExecutor jsExecutor;

  public AirbnbHomePage(WebDriver driver, WebdriverWaitUtil waitUtil) {
    this.driver = driver;
    this.waitUtil = waitUtil;
    this.jsExecutor = (JavascriptExecutor) driver;
  }

  By locationInputField = LocatorUtil.getLocator("location_input_field");
  By checkInButton = LocatorUtil.getLocator("check_in_button");
  By checkInDateButton = LocatorUtil.getLocator("check_in_date");
  By checkOutDateButton = LocatorUtil.getLocator("check_out_date");
  By addGuestsButton = LocatorUtil.getLocator("add_guests_button");
  By addAdultButton = LocatorUtil.getLocator("add_adult_button");
  By addChildButton = LocatorUtil.getLocator("add_child_button");
  By searchForResultsButton = LocatorUtil.getLocator("search_for_results_button");

  public void filterLocation(String location) {
    waitUtil.waitForElementVisible(locationInputField).sendKeys(location);
  }

  // Please Note: The check-in and check-out dates are selected using a static locator.
  // The locators for picking dates are pre-defined as explained in locators.properties.
  public void selectCheckInDate() {
    waitUtil.waitForElementClickable(checkInButton).click();
    waitUtil.waitForElementClickable(checkInDateButton).click();
  }

  public void selectCheckOutDate() {
    waitUtil.waitForElementClickable(checkOutDateButton).click();
  }

  public void addGuests(int adults, int children) {
    waitUtil.waitForElementClickable(addGuestsButton).click();

    for (int i = 0; i < adults; i++) {
      waitUtil.waitForElementClickable(addAdultButton).click();
    }

    for (int i = 0; i < children; i++) {
      waitUtil.waitForElementClickable(addChildButton).click();
    }
  }

  public void searchForResults() {
    waitUtil.waitForElementClickable(searchForResultsButton).click();
  }
}
