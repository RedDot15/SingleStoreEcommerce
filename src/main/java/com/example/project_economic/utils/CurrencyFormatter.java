package com.example.project_economic.utils;

public class CurrencyFormatter {
    public static String getFormattedCurrency(String priceNumber) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < priceNumber.length(); i++) {
            char currentChar = priceNumber.charAt(i);
            if (Character.isDigit(currentChar) && i > 0 && (priceNumber.length() - i) % 3 == 0) result.append('.');
            result.append(currentChar);
        }
        return result.toString() + '₫';
    }
}
