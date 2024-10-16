Feature: Airbnb search with map interaction functionality

  Scenario: Verify that a property is displayed on the map
    Given The user navigates to Airbnb.com website
    When The user filters for properties in "Rome, Italy"
    And The user selects a check-in date one week from today
    And The user selects a check-out date one week after check-in
    And The user adds 2 adults and 1 child as guests
    And The user searches for results
    And The user hovers over the first property in the results list
    Then Verify that the property is displayed on the map and the color of the pin changes upon hover
    And The user clicks on the matching property pin on the map
    Then Verify that the details shown in the map popup are the same as the ones shown in the search results