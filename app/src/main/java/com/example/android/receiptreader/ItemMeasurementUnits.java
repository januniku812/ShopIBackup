package com.example.android.receiptreader;

public class ItemMeasurementUnits {
    // all of the following measurement units defined in grams
    static Double POUNDS = 453.592;
    static Double MILLILITERS = 1.00;
    static Double LITERS = 1000.00;
    static Double KILOGRAMS = 1000.00;
    static Double FLUID_OUNCES = 28.34952;
    static Double GRAMS = 1.00;
    static Double GALLONS = 3785.41;
    static Double PINTS = 473.18;
    static Double OUNCES = 28.3495;
    public static Double findRatioBetweenOgMeasurementUnitAndConversionOutcomeUnit(Double ogUnit, Double outcomeUnit){
        return ogUnit / outcomeUnit;
    }
    public static Double returnItemMeasurementUnitClassVarForPriceComparisonUnit(String string){
        if(string.contains("(")) {
            string = string.substring(string.indexOf("(") + 1, string.indexOf(")"));
        }
        if(string.equalsIgnoreCase("lb")) {
            return POUNDS;
        }
        else if(string.equalsIgnoreCase("ml")) {
            return MILLILITERS;
        }
        else if(string.equalsIgnoreCase("l")) {
            return LITERS;
        }
        else if(string.equalsIgnoreCase("kg")) {
            return KILOGRAMS;
        }
        else if(string.equalsIgnoreCase("fl oz")) {
            return FLUID_OUNCES;
        }
        else if(string.equalsIgnoreCase("g")) {
            return GRAMS;
        }
        else if(string.equalsIgnoreCase("gl")) {
            return GALLONS;
        }
        else if(string.equalsIgnoreCase("pt")) {
            return PINTS;
        }
        else if(string.equalsIgnoreCase("oz")) {
            return OUNCES;
        }

        return null;
    }
}
