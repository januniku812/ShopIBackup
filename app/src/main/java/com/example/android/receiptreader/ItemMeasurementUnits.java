package com.example.android.receiptreader;

public class ItemMeasurementUnits {
    // all of the following measurement units defined in grams
    Double POUNDS = 453.592;
    Double MILLILITERS = 1.00;
    Double LITERS = 1000.00;
    Double KILOGRAMS = 1000.00;
    Double FLUID_OUNCES = 28.34952;
    Double GRAMS = 1.00;
    Double GALLONS = 3785.41;
    Double PINTS = 473.18;
    Double OUNCES = 28.3495;
    public Double findRatioBetweenOgMeasurementUnitAndConversionOutcomeUnit(Double ogUnit, Double outcomeUnit){
        return ogUnit / outcomeUnit;
    }
}
