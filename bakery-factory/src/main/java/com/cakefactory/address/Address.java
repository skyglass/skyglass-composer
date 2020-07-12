package com.cakefactory.address;

import lombok.Data;

@Data
public class Address {
    private final String addressLine1;
    private final String addressLine2;
    private final String postcode;
}
