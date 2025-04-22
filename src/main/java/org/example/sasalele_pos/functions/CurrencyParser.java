package org.example.sasalele_pos.functions;

public class CurrencyParser {

    /**
     * Converts a currency string in the format "Rp. xxx.xxx,xx" to an integer or double.
     *
     * @param currencyString The currency string (e.g., "Rp. 20.000,00").
     * @return The numeric value as an integer or double.
     */
    public static double convertCurrencyToDouble(String currencyString) {
        // Step 1: Remove "Rp." and trim any leading/trailing spaces
        currencyString = currencyString.replace("Rp.", "").trim();

        // Step 2: Remove thousands separator (dot)
        currencyString = currencyString.replace(".", "");

        // Step 3: Replace decimal separator (comma) with a dot
        currencyString = currencyString.replace(",", ".");

        // Step 4: Parse the cleaned-up string into a double
        try {
            return Double.parseDouble(currencyString);
        } catch (NumberFormatException e) {
            System.err.println("Error: Invalid currency format: " + currencyString);
            return 0;  // Return 0 if there's an error parsing the currency string
        }
    }
}
