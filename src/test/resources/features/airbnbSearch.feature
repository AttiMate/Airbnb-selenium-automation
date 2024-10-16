Feature: Airbnb search functionality

  Scenario: Verify that the results match the search criteria
    Given The user navigates to Airbnb.com website
    When The user filters for properties in "Rome, Italy"
    And The user selects a check-in date one week from today
    And The user selects a check-out date one week after check-in
    And The user adds 2 adults and 1 child as guests
    And The user searches for results
    Then Verify that the applied filters are correct
    And Verify that the properties displayed on the first page can accommodate at least the selected number of guests