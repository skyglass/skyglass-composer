Feature: Get hard-coded temperature for various postal codes

  Scenario: get temperature for correct postal codes in celsius
    When I get the temperature for postal code 2000 in celsius
    Then the temperature is -3.5 in celsius
    When I get the temperature for postal code 4000 in celsius
    Then the temperature is 2 in celsius
    When I get the temperature for postal code 7000 in celsius
    Then the temperature is 6.3 in celsius

  Scenario: get temperature for correct postal codes in fahrenheit
    When I get the temperature for postal code 2000 in fahrenheit
    Then the temperature is 25.7 in fahrenheit
    When I get the temperature for postal code 4000 in fahrenheit
    Then the temperature is 35.6 in fahrenheit
    When I get the temperature for postal code 7000 in fahrenheit
    Then the temperature is 43.3 in fahrenheit

  Scenario: get humidity for correct postal codes
    When I get the temperature for postal code 2000 in celsius
    Then the humidity is 30 per cent
    When I get the temperature for postal code 4000 in celsius
    Then the humidity is 35 per cent
    When I get the temperature for postal code 7000 in celsius
    Then the humidity is 40 per cent