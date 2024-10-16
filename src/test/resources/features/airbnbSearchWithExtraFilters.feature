Feature: Airbnb search functionality with extra filters

  Scenario: Verify that the results and details page match the extra filters
    Given The user navigates to Airbnb.com website
    When The user filters for properties in "Rome, Italy"
    And The user selects a check-in date one week from today
    And The user selects a check-out date one week after check-in
    And The user adds 2 adults and 1 child as guests
    And The user searches for results
    And The user clicks on More filters
    And The user selects 5 bedrooms
    And The user selects Pool from the Facilities section
    And The user clicks on Show places
    Then Verify that the properties displayed on the first page have at least the selected number of bedrooms
    And The user opens the details of the first property
    Then Verify that Pool option is displayed in the Amenities popup under the Facilities category