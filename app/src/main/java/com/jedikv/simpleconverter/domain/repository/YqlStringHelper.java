package com.jedikv.simpleconverter.domain.repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kurian on 31/10/2016.
 */

public class YqlStringHelper {

    /**
     * Build the YQL statement to pass into the request
     *
     * @param currencyPairs list of currency pairs to pass in e.g. USDGBP, USDCHF...
     * @return the YQL statement to execute the request
     */
    public String generateYQLCurrencyQuery(List<String> targetCurrencies,
                                           String sourceCurrency) {

        List<String> currencyPairs = createReverseFromPairs(targetCurrencies, sourceCurrency);

        StringBuilder sb = new StringBuilder("select * from yahoo.finance.xchange where pair in ");
        sb.append("(");
        for (int i = 0; i < currencyPairs.size(); i++) {
            sb.append("\"").append(currencyPairs.get(i)).append("\"");

            //Check if it's not the last entry in the list
            if (i < currencyPairs.size() - 1) {
                sb.append(",");
            }
        }
        sb.append(")");

        return sb.toString();
    }


    /**
     * Create the reverse pairs for
     *
     * @param targetCurrencies list of currencies to get the converted values for
     * @param sourceCurrency   the currency at the source of the conversion
     * @return A string list of currency pairs
     */
    private List<String> createReverseFromPairs(List<String> targetCurrencies,
                                                String sourceCurrency) {

        List<String> currencyPairs = new ArrayList<>();
        for (String targetCurrency : targetCurrencies) {
            currencyPairs.add(targetCurrency + sourceCurrency);
            currencyPairs.add(sourceCurrency + targetCurrency);
        }

        return currencyPairs;
    }

}