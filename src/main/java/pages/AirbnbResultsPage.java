package pages;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.interactions.Actions;
import utils.LocatorUtil;
import utils.WebdriverWaitUtil;

public class AirbnbResultsPage {

  WebDriver driver;
  WebdriverWaitUtil waitUtil;
  private int requiredBedrooms;
  private String firstListingTitle;
  private String firstListingPrice;
  private WebElement matchingPin;

  public AirbnbResultsPage(WebDriver driver, WebdriverWaitUtil waitUtil) {
    this.driver = driver;
    this.waitUtil = waitUtil;
  }

  By searchResultsHeader = LocatorUtil.getLocator("search_results_header");
  By locationFilterSummary = LocatorUtil.getLocator("location_filter_summary");
  By dateFilterSummary = LocatorUtil.getLocator("date_filter_summary");
  By guestsFilterSummary = LocatorUtil.getLocator("guests_filter_summary");
  By listingSummary = LocatorUtil.getLocator("listing_summary");
  By accommodatesGuestsSummary = LocatorUtil.getLocator("accommodates_guests_summary");
  By moreFiltersButton = LocatorUtil.getLocator("more_filters_button");
  By addBedroomButton = LocatorUtil.getLocator("add_bedroom_button");
  By showMoreButton = LocatorUtil.getLocator("show_more_button");
  By bedroomsInDetailsLocator = LocatorUtil.getLocator("bedrooms_in_details");
  By poolFacilityButton = LocatorUtil.getLocator("pool_facility_button");
  By showPlacesButton = LocatorUtil.getLocator("show_places_button");
  By bedroomsInDetails = LocatorUtil.getLocator("bedrooms_in_details");
  By showAllAmenitiesButtonLocator = LocatorUtil.getLocator("show_all_amenities_button");
  By parkingFacilitiesContainerLocator = LocatorUtil.getLocator("parking_and_facilities");
  By closePopupButtonLocator = LocatorUtil.getLocator("close_translation_popup");
  By mapPinsLocator = LocatorUtil.getLocator("common_map_marker");
  By firstListingSummaryLocator = LocatorUtil.getLocator("first_listing_summary");
  By pinPopupSummary = LocatorUtil.getLocator("pin_popup_summary");

  public void waitForResultsHeaderToContainText() {
    waitUtil.waitForCondition(
        driver -> {
          WebElement headerElement = driver.findElement(searchResultsHeader);
          String headerText = headerElement.getText().trim();

          System.out.println("Current header text during wait: " + headerText);

          return !headerText.isEmpty();
        },
        "The search results header did not contain any text within the expected time.");
  }

  public boolean verifyLocationInHeader(String location) {
    WebElement headerElement = waitUtil.waitForElementVisible(searchResultsHeader);
    String headerText = headerElement.getText().trim();

    System.out.println("Search Results Header Text: " + headerText);

    return headerText.contains(location);
  }

  public boolean verifyLocationInFilterSummary(String location) {
    WebElement locationElement = waitUtil.waitForElementVisible(locationFilterSummary);
    String locationFilterSummaryText = locationElement.getText().trim();

    System.out.println("Location Filter Summary Text: " + locationFilterSummaryText);

    return locationFilterSummaryText.contains(location);
  }

  public boolean verifyDateFilterSummary(String expectedDate) {
    WebElement dateElement = waitUtil.waitForElementVisible(dateFilterSummary);
    String dateFilterSummaryText = dateElement.getText().trim();

    String normalizedDateText = dateFilterSummaryText.replace("–", "-").replace("—", "-").trim();
    String normalizedExpectedDate = expectedDate.replace("–", "-").replace("—", "-").trim();

    System.out.println("Normalized Date Filter Summary Text: " + normalizedDateText);
    System.out.println("Normalized Expected Date Filter Summary Text: " + normalizedExpectedDate);

    return normalizedDateText.equals(normalizedExpectedDate);
  }

  public boolean verifyGuestsFilterSummary(int expectedGuests) {
    WebElement guestsElement = waitUtil.waitForElementVisible(guestsFilterSummary);
    String guestFiltersSummaryText = guestsElement.getText().trim();

    // Extract only the numeric part of the guest text
    String numericGuestText = guestFiltersSummaryText.replaceAll("[^\\d]", "").trim();
    int actualGuests = numericGuestText.isEmpty() ? 0 : Integer.parseInt(numericGuestText);

    System.out.println("Guests Filter Summary Text: " + guestFiltersSummaryText);
    System.out.println("Extracted Actual Guests: " + actualGuests);
    System.out.println("Expected Guests: " + expectedGuests);

    return guestFiltersSummaryText.contains(expectedGuests + " guests");
  }

  public String getExpectedDateFilter(LocalDate checkInDate, LocalDate checkOutDate) {
    DateTimeFormatter monthDayFormatter = DateTimeFormatter.ofPattern("MMM d");
    DateTimeFormatter monthDayYearFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy");

    // Check if the check-in and check-out dates are in the same year
    if (checkInDate.getYear() == checkOutDate.getYear()) {
      // Check if the check-in and check-out dates are in the same month
      if (checkInDate.getMonth() == checkOutDate.getMonth()) {
        // Format: "Oct 19 - 26"
        return checkInDate.format(monthDayFormatter)
            + " - "
            + checkOutDate.format(DateTimeFormatter.ofPattern("d"));
      } else {
        // Format: "Oct 19 - Nov 3"
        return checkInDate.format(monthDayFormatter)
            + " - "
            + checkOutDate.format(monthDayFormatter);
      }
    } else {
      // Different years case: Format "Dec 28, 2024 - Jan 5, 2025"
      return checkInDate.format(monthDayYearFormatter)
          + " - "
          + checkOutDate.format(monthDayYearFormatter);
    }
  }

  public boolean verifyPropertiesAccommodateGuests(int requiredGuests) {
    // Find all the listing summaries on the page, excluding the ones proposed for similar dates
    List<WebElement> listings = driver.findElements(listingSummary);

    int listingNumber = 1; // Start listing count from 1 for better readability

    for (WebElement listing : listings) {
      System.out.println("Verifying listing #" + listingNumber + "...");

      try {
        // Attempt to get bed information
        String bedsText = listing.getText().trim();
        int numberOfBeds = extractNumberOfBeds(bedsText);

        System.out.println(
            "Listing #" + listingNumber + " - Number of beds found: " + numberOfBeds);

        // Assume each bed can accommodate 2 guests
        int possibleGuests = numberOfBeds * 2;

        if (possibleGuests >= requiredGuests) {
          System.out.println("Listing #" + listingNumber + " can accommodate required guests.\n");
          listingNumber++;
          continue; // Move to the next listing
        }
      } catch (Exception e) {
        // Handle case where bed information is not available or insufficient
        System.out.println(
            "Listing #"
                + listingNumber
                + " - Beds information not found or insufficient, opening listing to verify.\n");
      }

      // Open listing in new tab for further verification
      Actions action = new Actions(driver);
      action.moveToElement(listing).click().perform();

      // Switch to the new tab that was opened
      ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
      driver.switchTo().window(tabs.get(1));

      // Verify the guest count in the detailed view
      verifyGuestsInDetailedView(requiredGuests);

      // Print a message indicating that the listing can accommodate the required guests after
      // detailed verification
      System.out.println("Listing #" + listingNumber + " can accommodate required guests.\n");

      // Close the current tab and switch back to the original tab
      driver.close();
      driver.switchTo().window(tabs.get(0));

      // Wait for the page to reload properly before continuing
      waitUtil.waitForElementVisible(searchResultsHeader);

      // Increment the listing number after verification is complete
      listingNumber++;
    }

    // If all listings are verified without issue, return true
    return true;
  }

  private int extractNumberOfBeds(String bedsText) {
    // Split the text into parts using common delimiters like commas or newlines
    String[] textParts = bedsText.split("[,\\n]");

    // Initialize the number of beds to zero
    int numberOfBeds = 0;

    // Iterate through each part to find the bed-related information
    for (String part : textParts) {
      part = part.trim().toLowerCase();

      // Check if the part contains the word "bed"
      if (part.contains("bed")) {
        // Use regex to extract the number before "bed"
        String numericPart = part.replaceAll("[^\\d]", "").trim();

        if (!numericPart.isEmpty()) {
          numberOfBeds = Integer.parseInt(numericPart);
          break; // Exit the loop once we have found the bed count
        }
      }
    }

    return numberOfBeds;
  }

  private void verifyGuestsInDetailedView(int requiredGuests) {
    WebElement accommodatesElement = waitUtil.waitForElementVisible(accommodatesGuestsSummary);
    String accommodatesText = accommodatesElement.getText().trim();

    // Extract the number of guests from the text
    String numericPart = accommodatesText.replaceAll("[^\\d]", "");
    int actualGuests = numericPart.isEmpty() ? 0 : Integer.parseInt(numericPart);

    System.out.println("Verified number of guests from detailed view: " + actualGuests);
    System.out.println("Required number of guests: " + requiredGuests);

    // Assert the number of guests
    if (actualGuests < requiredGuests) {
      throw new AssertionError("The listing cannot accommodate the required number of guests.");
    }
  }

  public void clickMoreFilters() {
    // Click the more filters button
    WebElement moreFiltersElement = waitUtil.waitForElementClickable(moreFiltersButton);
    moreFiltersElement.click();
  }

  public void selectNumberOfBedrooms(int numberOfBedrooms) {
    // Store the number of bedrooms as an instance variable for later verification
    this.requiredBedrooms = numberOfBedrooms;

    // Scroll to the + button to ensure it is visible
    WebElement addButtonElement = waitUtil.waitForElementVisible(addBedroomButton);
    scrollToElement(addButtonElement);

    // Click the + button the required number of times
    for (int i = 0; i < numberOfBedrooms; i++) {
      try {
        addButtonElement.click();
        System.out.println("Clicking + button for bedrooms: " + (i + 1) + " times");
      } catch (Exception e) {
        throw new RuntimeException(
            "Failed to click on '+' button for bedrooms: attempt #" + (i + 1), e);
      }
    }
  }

  public int getRequiredBedrooms() {
    return this.requiredBedrooms; // Getter to access the required number of bedrooms
  }

  public void selectPoolFacility() {
    // Scroll to the Show More button
    WebElement showMoreElement = waitUtil.waitForElementVisible(showMoreButton);
    scrollToElement(showMoreElement);
    waitUtil.waitForElementClickable(showMoreButton).click();
    System.out.println("Show More clicked.");

    WebElement poolFacilityElement = waitUtil.waitForElementVisible(poolFacilityButton);
    scrollToElement(poolFacilityElement);
    waitUtil.waitForElementClickable(poolFacilityButton).click();
    System.out.println("Pool facility selected.");
  }

  public void clickShowPlaces() {
    waitUtil.waitForElementClickable(showPlacesButton).click();
    System.out.println("Show places clicked.");
    waitUtil.waitForElementVisible(searchResultsHeader);
  }

  public boolean verifyPropertiesHaveAtLeastBedrooms(int requiredBedrooms) {
    // Find all the listing summaries on the page, excluding the ones proposed for similar dates
    try {
      // Initial wait for the listings to be present on the page
      waitUtil.waitForElementsVisible(listingSummary);
    } catch (Exception e) {
      System.out.println("Error waiting for listings to appear: " + e.getMessage());
      return false;
    }

    int listingNumber = 1;

    while (true) {
      List<WebElement> listings = driver.findElements(listingSummary);

      if (listingNumber > listings.size()) {
        break; // End the loop if we have verified all available listings
      }

      WebElement listing =
          listings.get(listingNumber - 1); // Access the listing based on the current count

      System.out.println("Verifying listing #" + listingNumber + " for number of bedrooms...");

      try {
        // Attempt to get bedroom information from the listing summary
        String bedroomsText = listing.getText().trim();
        int numberOfBedrooms = extractNumberOfBedrooms(bedroomsText);

        System.out.println(
            "Listing #" + listingNumber + " - Number of bedrooms found: " + numberOfBedrooms);

        // If sufficient bedrooms are found, print and skip to the next listing
        if (numberOfBedrooms >= requiredBedrooms) {
          System.out.println(
              "Listing #" + listingNumber + " can accommodate the required number of bedrooms.\n");
          listingNumber++;
          continue; // Move to the next listing
        } else {
          System.out.println(
              "Listing #" + listingNumber + " does not have enough bedrooms, verifying in detail.");
        }
      } catch (Exception e) {
        // Handle case where bedroom information is not available
        System.out.println(
            "Listing #"
                + listingNumber
                + " - Bedroom information not found or insufficient, opening listing to verify.\n");
      }

      // Open listing in new tab for further verification if the number of bedrooms was not
      // available or insufficient
      try {
        Actions action = new Actions(driver);
        action.moveToElement(listing).click().perform();

        // Switch to the new tab that was opened
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        if (tabs.size() < 2) {
          throw new RuntimeException("Failed to open listing in a new tab.");
        }
        driver.switchTo().window(tabs.get(1));

        // Wait for the detailed page to load properly
        //  By bedroomsInDetailsLocator = LocatorUtil.getLocator("bedrooms_in_details");
        waitUtil.waitForElementVisible(bedroomsInDetailsLocator);

        // Verify the bedroom count in the detailed view
        verifyBedroomsInDetailedView(requiredBedrooms); // Pass requiredBedrooms as parameter

        System.out.println(
            "Listing #"
                + listingNumber
                + " can accommodate the required number of bedrooms (verified in detail).\n");

        // Close the current tab and switch back to the original tab
        driver.close();
        driver.switchTo().window(tabs.get(0));

        // Wait for the original page to load properly before continuing
        waitUtil.waitForElementsVisible(listingSummary);
      } catch (Exception e) {
        System.out.println(
            "Error opening or verifying listing #"
                + listingNumber
                + " in detailed view: "
                + e.getMessage());
      }

      // Increment the listing number after verification is complete
      listingNumber++;
    }

    // If all listings are verified without issue, return true
    return true;
  }

  private int extractNumberOfBedrooms(String bedroomsText) {
    // Split the text into parts using common delimiters like commas or newlines
    String[] textParts = bedroomsText.split("[,\\n]");

    // Initialize the number of bedrooms to zero
    int numberOfBedrooms = 0;

    // Iterate through each part to find the bedroom-related information
    for (String part : textParts) {
      part = part.trim().toLowerCase();

      // Check if the part contains the word "bedroom"
      if (part.contains("bedroom")) {
        // Use regex to extract the number before "bedroom"
        String numericPart = part.replaceAll("[^\\d]", "").trim();

        if (!numericPart.isEmpty()) {
          numberOfBedrooms = Integer.parseInt(numericPart);
          break; // Exit the loop once we have found the bedroom count
        }
      }
    }

    return numberOfBedrooms;
  }

  private void verifyBedroomsInDetailedView(int requiredBedrooms) {
    WebElement bedroomsElement = waitUtil.waitForElementVisible(bedroomsInDetails);
    String bedroomsText = bedroomsElement.getText().trim();

    // Extract the number of bedrooms from the detailed view text
    String numericPart = bedroomsText.replaceAll("[^\\d]", "");
    int actualBedrooms = numericPart.isEmpty() ? 0 : Integer.parseInt(numericPart);

    System.out.println("Verified number of bedrooms from detailed view: " + actualBedrooms);
    System.out.println("Required number of bedrooms: " + requiredBedrooms);

    // Assert the number of bedrooms
    if (actualBedrooms < requiredBedrooms) {
      throw new AssertionError("The found bedrooms are less then the selected bedrooms.");
    }
  }

  public void openFirstProperty() {
    try {
      // Step 1: Wait until the first property is visible
      WebElement firstListing = waitUtil.waitForElementVisible(listingSummary);

      // Get the first listing and click it to open the details
      Actions action = new Actions(driver);
      action.moveToElement(firstListing).click().perform();

      // Switch to the new tab that was opened
      ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
      if (tabs.size() < 2) {
        throw new RuntimeException("Failed to open listing in a new tab.");
      }
      driver.switchTo().window(tabs.get(1));

      System.out.println("Opened the first property in a new tab.");

    } catch (Exception e) {
      System.out.println("Error opening the first listing in a new tab: " + e.getMessage());
    }
  }

  public void closePopupIfPresent() {
    try {
      WebElement closePopupButton = waitUtil.waitForElementClickable(closePopupButtonLocator);
      closePopupButton.click();
      System.out.println("Translation pop-up closed.");
    } catch (Exception e) {
      // Ignore if no pop-up appears, as it's not critical if the pop-up isn't present
      System.out.println("No translation pop-up detected or already closed.");
    }
  }

  public void verifyPoolInAmenities() {
    // Step 1: Close the translation pop-up if it appears
    closePopupIfPresent();

    // Step 2: Wait for the "Show All Amenities" button to be visible, scroll to it, and then click
    try {
      // Wait for the amenities button to be visible
      WebElement showAllAmenitiesButton =
          waitUtil.waitForElementVisible(showAllAmenitiesButtonLocator);

      // Scroll to the amenities button to make sure it is in view
      scrollToElement(showAllAmenitiesButton);

      // Wait until the button is clickable and then click
      waitUtil.waitForElementClickable(showAllAmenitiesButtonLocator).click();
      System.out.println("'Show All Amenities' button clicked.");
    } catch (Exception e) {
      // Throw a runtime exception so that the test will fail if clicking the button fails
      throw new RuntimeException(
          "Error clicking 'Show All Amenities' button: " + e.getMessage(), e);
    }

    // Step 3: Scroll down in the popup window and verify if "Pool" is displayed
    try {
      WebElement parkingFacilitiesContainer =
          waitUtil.waitForElementVisible(parkingFacilitiesContainerLocator);
      scrollToElement(parkingFacilitiesContainer);

      // Verify if "Pool" is displayed in the parking and facilities container
      String containerText = parkingFacilitiesContainer.getText().toLowerCase();
      if (containerText.contains("pool")) {
        System.out.println("Verified: Pool is listed under Parking and Facilities.");
      } else {
        throw new AssertionError("Pool is NOT listed under Parking and Facilities.");
      }
    } finally {
      // Always ensure the tab is closed and switch back to the first tab
      driver.close();
      ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
      driver.switchTo().window(tabs.get(0));
    }
  }

  public Map<String, Map<String, String>> extractPinDetailsBeforeHover() {
    // Step 0: Wait for pins to be visible
    waitUtil.waitForElementsVisible(mapPinsLocator);

    // Step 1: Get all map pins and store their prices, titles, and initial details
    List<WebElement> mapPins = driver.findElements(mapPinsLocator);

    // Map to store price and title -> pin data before hover
    Map<String, Map<String, String>> pinDataMap = new HashMap<>();

    for (WebElement pin : mapPins) {
      try {
        // Extract price and title from pin using extractPriceAndTitleFromPin
        Map<String, String> pinData = extractPriceAndTitleFromPin(pin);
        String pinPrice = pinData.getOrDefault("price", "");
        String pinTitle = pinData.getOrDefault("title", "");

        if (!pinPrice.isEmpty() && !pinTitle.isEmpty()) {
          pinDataMap.put(pinPrice + "-" + pinTitle, pinData);
          System.out.println("Pin data before hover: " + pinData);
        }
      } catch (Exception e) {
        System.out.println("Error extracting data for pin: " + e.getMessage());
      }
    }

    return pinDataMap;
  }

  public void hoverOverFirstListingAfterPinIdentification() {
    try {
      // Wait for the first listing to be visible
      WebElement firstListing = waitUtil.waitForElementVisible(firstListingSummaryLocator);

      // Extract title and price from the first listing
      Map<String, String> listingData = extractPriceAndTitleFromListing(firstListing);
      String listingTitle = listingData.getOrDefault("title", "");
      String listingPrice = listingData.getOrDefault("price", "");

      if (listingTitle.isEmpty() || listingPrice.isEmpty()) {
        throw new RuntimeException("First listing title or price is missing");
      }

      System.out.println("First listing title: " + listingTitle);
      System.out.println("First listing price: " + listingPrice);

      // Store the listing title and price for later use
      this.firstListingTitle = listingTitle;
      this.firstListingPrice = listingPrice;

      // Hover over the first listing to trigger the pin change on the map
      Actions actions = new Actions(driver);
      actions.moveToElement(firstListing).perform();
      System.out.println("Hovered over the first property in the results list.");

      // Hold the hover for 3 seconds before proceeding to ensure UI change happens
      Thread.sleep(3000); // Increased wait to ensure the UI has time to reflect changes
    } catch (Exception e) {
      throw new RuntimeException("Error hovering over the first property: " + e.getMessage(), e);
    }
  }

  public void verifyMapPinColorChangeOnHover() {
    // Step 0: Extract pin details before any hovering
    // Map<String, Map<String, String>> pinPriceTitleMapBeforeHover =
    // extractPinDetailsBeforeHover();

    // Step 1: Get all map pins after extracting initial data
    List<WebElement> mapPins = driver.findElements(mapPinsLocator);

    // Step 2: Find the matching pin by title and price
    System.out.println("First listing title for verification: " + firstListingTitle);
    System.out.println("First listing price for verification: " + firstListingPrice);

    WebElement matchingPin = getMatchingPin(mapPins, firstListingTitle, firstListingPrice);

    if (matchingPin == null) {
      throw new RuntimeException(
          "No matching pin found for the listing title: "
              + firstListingTitle
              + " and price: "
              + firstListingPrice);
    }

    // Step 3: Wait until the first listing is not hovered
    waitForListingToBeNotHovered();

    // Step 4: Take the first screenshot of the pin before any hover occurs
    takeScreenshotOfPin(matchingPin, "beforeHover.png");

    // Step 5: Hover over the first listing and stay hovered
    hoverOverFirstListingAndWait();

    // Step 6: Take a screenshot of the pin during hover
    takeScreenshotOfPin(matchingPin, "afterHover.png");

    // Step 7: Compare the two screenshots to verify if the color changed
    compareScreenshots("beforeHover.png", "afterHover.png");
  }

  private void waitForListingToBeNotHovered() {
    try {
      // WebElement firstListing = waitUtil.waitForElementVisible(firstListingSummaryLocator);
      Actions actions = new Actions(driver);

      // Move to a "safe" area to make sure the listing is not hovered
      WebElement headerElement = driver.findElement(By.tagName("header"));
      actions.moveToElement(headerElement).perform();

      Thread.sleep(1000);

      System.out.println(
          "Ensured that the first listing is not being hovered before taking the first screenshot.");
    } catch (Exception e) {
      throw new RuntimeException(
          "Error ensuring that the first listing is not hovered: " + e.getMessage(), e);
    }
  }

  private void takeScreenshotOfPin(WebElement pin, String fileName) {
    try {
      Thread.sleep(1000);
      File screenshot = pin.getScreenshotAs(OutputType.FILE);
      Path screenshotPath = new File(fileName).toPath();

      Files.copy(screenshot.toPath(), screenshotPath, StandardCopyOption.REPLACE_EXISTING);
      System.out.println("Captured " + fileName);
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException("Unable to save screenshot: " + e.getMessage(), e);
    }
  }

  public void hoverOverFirstListingAndWait() {
    try {
      // Wait for the first listing to be visible
      WebElement firstListing = waitUtil.waitForElementVisible(firstListingSummaryLocator);

      Actions actions = new Actions(driver);
      actions.moveToElement(firstListing).perform();

      // Hold the hover for a longer duration to ensure color change is captured
      Thread.sleep(3000); // Increased wait to make sure hover effect is fully visible
      System.out.println("Hovered over the first listing and waiting for UI to reflect changes.");
    } catch (InterruptedException e) {
      throw new RuntimeException("Interrupted while waiting for UI changes after hovering.", e);
    } catch (Exception e) {
      throw new RuntimeException(
          "Error while hovering over the first listing: " + e.getMessage(), e);
    }
  }

  private void compareScreenshots(String beforeFileName, String afterFileName) {
    try {
      BufferedImage imgBefore = ImageIO.read(new File(beforeFileName));
      BufferedImage imgAfter = ImageIO.read(new File(afterFileName));

      if (imgBefore == null || imgAfter == null) {
        throw new IOException("One of the screenshots could not be loaded for comparison.");
      }

      boolean colorChanged = !bufferedImagesAreEqual(imgBefore, imgAfter);

      if (colorChanged) {
        System.out.println("Verified: The pin color changed after hovering over the listing.");
      } else {
        throw new AssertionError("Pin color did not change after hovering over the listing.");
      }

    } catch (IOException e) {
      throw new RuntimeException(
          "Unable to process screenshots for comparison: " + e.getMessage(), e);
    }
  }

  private boolean bufferedImagesAreEqual(BufferedImage img1, BufferedImage img2) {
    if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
      return false;
    }

    for (int y = 0; y < img1.getHeight(); y++) {
      for (int x = 0; x < img1.getWidth(); x++) {
        if (img1.getRGB(x, y) != img2.getRGB(x, y)) {
          return false;
        }
      }
    }
    return true;
  }

  // Helper methods for extracting price and title
  private Map<String, String> extractPriceAndTitleFromPin(WebElement pin) {
    Map<String, String> pinData = new HashMap<>();
    try {
      // Locate the first span element inside the pin that contains the title and price.
      WebElement titleAndPriceSpan = pin.findElement(By.tagName("span"));

      // Extract the text from the first span, which contains both the title and price.
      String pinText = titleAndPriceSpan.getText();
      System.out.println("Full pin text extracted for analysis: " + pinText); // For debugging

      // Use regex to match the title and the price parts.
      Pattern pattern = Pattern.compile("^(.*?),\\s*([\\d,]+)\\s*lei");
      Matcher matcher = pattern.matcher(pinText);

      if (matcher.find()) {
        String title = matcher.group(1).trim(); // Extract the title before the comma
        String price = matcher.group(2).trim(); // Extract the price with commas intact

        // Output extracted details for debugging purposes
        System.out.println("Extracted title from pin: " + title);
        System.out.println("Extracted price from pin: " + price);

        // Ensure title and price are not empty before adding to the map
        if (!title.isEmpty() && !price.isEmpty()) {
          pinData.put("title", title);
          pinData.put("price", price);
        } else {
          System.out.println("Title or price is empty for the pin. Skipping extraction.");
        }
      } else {
        System.out.println("Regex did not match the expected format for pin text: " + pinText);
      }

    } catch (Exception e) {
      System.out.println("Unable to extract price and title from pin: " + e.getMessage());
    }
    return pinData;
  }

  private Map<String, String> extractPriceAndTitleFromListing(WebElement listing) {
    Map<String, String> priceAndTitle = new HashMap<>();
    try {
      // Extract the full text of the listing
      String listingText = listing.getText();
      System.out.println("Full listing text: " + listingText); // For debugging

      // Extract the title (assumed to be the first line)
      String[] textParts = listingText.split("\\n");
      String title = textParts[0].trim();

      // Use regex to find the price that ends with " lei per night"
      String price = "";
      Pattern pattern = Pattern.compile("([\\d,]+)(?=\\s*lei per night)");
      Matcher matcher = pattern.matcher(listingText);
      if (matcher.find()) {
        price = matcher.group(1); // Keep the commas in the price
      }

      if (title.isEmpty() || price.isEmpty()) {
        throw new RuntimeException("Unable to extract title or price from listing text.");
      }

      priceAndTitle.put("title", title);
      priceAndTitle.put("price", price);

      System.out.println("Extracted title from listing: " + title);
      System.out.println("Extracted price from listing: " + price);
    } catch (Exception e) {
      throw new RuntimeException(
          "Unable to extract price and title from listing: " + e.getMessage());
    }
    return priceAndTitle;
  }

  private WebElement getMatchingPin(
      List<WebElement> pins, String listingTitle, String listingPrice) {
    for (WebElement pin : pins) {
      Map<String, String> pinData = extractPriceAndTitleFromPin(pin);

      String pinTitle = pinData.get("title");
      String pinPrice = pinData.get("price");

      if (pinTitle != null
          && pinPrice != null
          && pinTitle.equalsIgnoreCase(listingTitle)
          && pinPrice.equals(listingPrice)) {
        System.out.println(
            "Matching pin found for the listing: " + pinTitle + " with price: " + pinPrice);

        // Set the instance variable
        this.matchingPin = pin;
        return pin; // Return the pin if both the title and price match
      }
    }
    throw new RuntimeException(
        "No matching pin found for the listing title: "
            + listingTitle
            + " and price: "
            + listingPrice);
  }

  public void clickOnMatchingPin() {
    try {
      if (matchingPin == null) {
        throw new RuntimeException("No matching pin found to click.");
      }

      // Click on the matching pin
      matchingPin.click();
      System.out.println(
          "Clicked on the matching pin: "
              + firstListingTitle
              + " with price: "
              + firstListingPrice);

      // Wait for the popup to appear
      waitUtil.waitForElementVisible(pinPopupSummary);
      System.out.println("Pin popup summary is visible.");

    } catch (NoSuchElementException e) {
      throw new RuntimeException("Unable to click on the matching pin: " + e.getMessage(), e);
    } catch (Exception e) {
      throw new RuntimeException(
          "Unexpected error while clicking the matching pin: " + e.getMessage(), e);
    }
  }

  public void verifyPinPopupDetailsMatchListing() {
    try {
      // Step 1: Extract details from the listing summary
      WebElement firstListing = waitUtil.waitForElementVisible(firstListingSummaryLocator);
      String listingText = firstListing.getText();
      System.out.println("Full listing text: " + listingText);

      // Extract relevant info from the listing text, removing unwanted parts after "·"
      String[] listingDetailsArray = listingText.split("·");
      String listingRelevantText =
          listingDetailsArray[0].trim(); // Extract part before "·" to remove unwanted details

      // Split listing details by line to create a list of key details
      List<String> listingDetailsList =
          new ArrayList<>(Arrays.asList(listingRelevantText.split("\\n")));

      // Remove duplicate "Free cancellation" if present
      listingDetailsList = removeDuplicateItems(listingDetailsList, "Free cancellation");

      // Use regex to find the rating parts, but stop after the first occurrence of "reviews"
      Pattern ratingPattern =
          Pattern.compile("(\\d+\\.\\d+\\sout\\sof\\s\\d+\\saverage\\srating,\\s\\d+\\sreviews)");
      Matcher listingMatcher = ratingPattern.matcher(listingText);

      if (listingMatcher.find()) {
        listingDetailsList.add(listingMatcher.group());
      }

      System.out.println("Extracted listing details for comparison: " + listingDetailsList);

      // Step 2: Extract details from the pin popup summary
      WebElement pinPopup = waitUtil.waitForElementVisible(pinPopupSummary);
      String pinPopupText = pinPopup.getText();
      System.out.println("Full pin popup text: " + pinPopupText);

      // Extract relevant details from pin popup text, removing unwanted parts after "·"
      String[] pinPopupDetailsArray = pinPopupText.split("·");
      String pinPopupRelevantText =
          pinPopupDetailsArray[0].trim(); // Extract part before "·" to remove unwanted details

      // Split pin popup details by line to create a list of key details
      List<String> pinPopupDetailsList =
          new ArrayList<>(Arrays.asList(pinPopupRelevantText.split("\\n")));

      // Remove duplicate "Free cancellation" if present
      pinPopupDetailsList = removeDuplicateItems(pinPopupDetailsList, "Free cancellation");

      // Extract rating details from pin popup text, but only add the first occurrence
      Matcher pinPopupMatcher = ratingPattern.matcher(pinPopupText);
      if (pinPopupMatcher.find()) {
        pinPopupDetailsList.add(pinPopupMatcher.group());
      }

      // Ensure no further extraction after the first valid rating
      pinPopupDetailsList = limitDetailsToFirstRating(pinPopupDetailsList);

      System.out.println("Extracted pin popup details for comparison: " + pinPopupDetailsList);

      // Step 3: Compare the listing details with the pin popup details
      if (listingDetailsList.equals(pinPopupDetailsList)) {
        System.out.println(
            "Verified: The details shown in the map popup match the ones shown in the search results.");
      } else {
        throw new AssertionError(
            "Details mismatch! Listing details: \n"
                + listingDetailsList
                + "\nPin popup details: \n"
                + pinPopupDetailsList);
      }

    } catch (NoSuchElementException e) {
      throw new RuntimeException(
          "Unable to find necessary elements for verifying popup details: " + e.getMessage(), e);
    } catch (Exception e) {
      throw new RuntimeException(
          "Unexpected error during verification of popup details: " + e.getMessage(), e);
    }
  }

  // Helper method to remove duplicate items from a list
  private List<String> removeDuplicateItems(List<String> list, String itemToCheck) {
    List<String> resultList = new ArrayList<>();
    boolean itemSeen = false;

    for (String item : list) {
      if (item.equalsIgnoreCase(itemToCheck)) {
        if (!itemSeen) {
          resultList.add(item); // Add only the first occurrence
          itemSeen = true;
        }
      } else {
        resultList.add(item);
      }
    }

    return resultList;
  }

  // Helper method to stop the extraction after the first rating occurrence
  private List<String> limitDetailsToFirstRating(List<String> detailsList) {
    List<String> limitedDetailsList = new ArrayList<>();
    Pattern ratingPattern =
        Pattern.compile("(\\d+\\.\\d+\\sout\\sof\\s\\d+\\saverage\\srating,\\s\\d+\\sreviews)");

    boolean ratingFound = false;

    for (String detail : detailsList) {
      limitedDetailsList.add(detail);

      Matcher matcher = ratingPattern.matcher(detail);
      if (matcher.find() && !ratingFound) {

        break;
      }
    }

    return limitedDetailsList;
  }

  // Helper method to scroll to an element using JavaScript
  private void scrollToElement(WebElement element) {
    JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
    jsExecutor.executeScript(
        "arguments[0].scrollIntoView({block: 'center', inline: 'center'});", element);
  }
}
