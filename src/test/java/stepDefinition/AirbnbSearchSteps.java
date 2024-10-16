package stepDefinition;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.LocalDate;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import pages.AirbnbHomePage;
import pages.AirbnbResultsPage;
import utils.WebdriverWaitUtil;

public class AirbnbSearchSteps {
  WebDriver driver;
  AirbnbHomePage homePage;
  AirbnbResultsPage resultsPage;
  WebdriverWaitUtil waitUtil;
  LocalDate checkInDate;
  LocalDate checkOutDate;
  int guestCount = 0;
  String location;

  @Before
  public void setup() {
    WebDriverManager.chromedriver().setup();
    driver = new ChromeDriver();
    driver.manage().window().maximize();
    driver.manage().deleteAllCookies();
    waitUtil = new WebdriverWaitUtil(driver);
  }

  @Given("The user navigates to Airbnb.com website")
  public void navigateToAirbnb() {
    driver.get("https://www.airbnb.com/");
    homePage = new AirbnbHomePage(driver, waitUtil);
  }

  @When("The user filters for properties in {string}")
  public void theUserFiltersPropertiesByLocation(String locationInput) {
    this.location = locationInput.split(",")[0];
    homePage.filterLocation(locationInput);
  }

  @And("The user selects a check-in date one week from today")
  public void theUserSelectsCheckInDate() {
    // The `checkInDate` variable is used here to store the expected check-in date for verification
    // purposes.
    checkInDate = LocalDate.now().plusDays(7);

    homePage.selectCheckInDate();
  }

  @And("The user selects a check-out date one week after check-in")
  public void theUserSelectsCheckOutDate() {
    // The `checkOutDate` variable is used here to store the expected check-out date for
    // verification purposes.
    checkOutDate = checkInDate.plusDays(7);

    homePage.selectCheckOutDate();
  }

  @And("The user adds {int} adults and {int} child as guests")
  public void theUserAddsGuests(int adults, int children) {
    guestCount = adults + children;
    homePage.addGuests(adults, children);
  }

  @And("The user searches for results")
  public void theUserSearchesForResults() {
    homePage.searchForResults();
    resultsPage = new AirbnbResultsPage(driver, waitUtil);
    resultsPage.waitForResultsHeaderToContainText();
  }

  @Then("Verify that the applied filters are correct")
  public void verifyFilters() {
    assert resultsPage.verifyLocationInHeader(location) : "Location filter in header is incorrect";
    assert resultsPage.verifyLocationInFilterSummary(location)
        : "Location filter in filter summary is incorrect";

    // Get expected date filter from the results page
    String expectedDateFilter = resultsPage.getExpectedDateFilter(checkInDate, checkOutDate);
    System.out.println("Expected Date Filter: " + expectedDateFilter); // Debugging line

    // Verify the date filter contains check-in and check-out dates
    assert resultsPage.verifyDateFilterSummary(expectedDateFilter) : "Date filter is incorrect!";

    assert resultsPage.verifyGuestsFilterSummary(guestCount) : "Guest filter is incorrect!";
  }

  @And(
      "Verify that the properties displayed on the first page can accommodate at least the selected number of guests")
  public void verifyPropertiesCanAccommodateGuests() {
    // Ensure resultsPage has been initialized
    assert resultsPage != null : "Results page not initialized";

    boolean allPropertiesAccommodateGuests =
        resultsPage.verifyPropertiesAccommodateGuests(guestCount);

    // Assert that all properties can accommodate the guests
    assert allPropertiesAccommodateGuests
        : "Not all properties can accommodate the required number of guests.";
  }

  @And("The user clicks on More filters")
  public void theUserClicksOnMoreFilters() {
    resultsPage.clickMoreFilters();
  }

  @And("The user selects {int} bedrooms")
  public void theUserSelectsBedrooms(int numberOfBedrooms) {
    resultsPage.selectNumberOfBedrooms(numberOfBedrooms);
  }

  @And("The user selects Pool from the Facilities section")
  public void theUserSelectsPoolFromFacilities() {
    resultsPage.selectPoolFacility();
  }

  @And("The user clicks on Show places")
  public void theUserClicksOnShowPlaces() {
    resultsPage.clickShowPlaces();
  }

  @Then(
      "Verify that the properties displayed on the first page have at least the selected number of bedrooms")
  public void verifyPropertiesHaveAtLeastTheSelectedBedrooms() {
    int requiredBedrooms = resultsPage.getRequiredBedrooms(); // Get the required number of bedrooms
    boolean result = resultsPage.verifyPropertiesHaveAtLeastBedrooms(requiredBedrooms);
    assert result : "Some properties do not meet the filter criteria.";
  }

  @And("The user opens the details of the first property")
  public void theUserOpensTheDetailsOfTheFirstProperty() {
    resultsPage.openFirstProperty();
  }

  @Then("Verify that Pool option is displayed in the Amenities popup under the Facilities category")
  public void verifyPoolIsDisplayedInAmenitiesUnderFacilities() {
    resultsPage.verifyPoolInAmenities();
  }

  @And("The user hovers over the first property in the results list")
  public void theUserHoversOverFirstProperty() {
    // Hover over the first listing
    resultsPage.hoverOverFirstListingAfterPinIdentification();
  }

  @Then(
      "Verify that the property is displayed on the map and the color of the pin changes upon hover")
  public void verifyPropertyIsDisplayedOnMapAndPinColourChanges() {
    // Verify if the color has changed for the matching pin after the hover action
    resultsPage.verifyMapPinColorChangeOnHover();
  }

  @And("The user clicks on the matching property pin on the map")
  public void theUserClicksOnTheMatchingPropertyPin() {
    resultsPage.clickOnMatchingPin();
  }

  @Then(
      "Verify that the details shown in the map popup are the same as the ones shown in the search results")
  public void verifyDetailsMatchBetweenListingAndPinPopup() {
    resultsPage.verifyPinPopupDetailsMatchListing();
  }

  @After
  public void tearDown() {
    if (driver != null) {
      driver.quit();
    }
  }
}
