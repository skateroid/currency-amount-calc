package com.company.helper;

import com.company.CurrencyCodes;

import java.io.*;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class FilesHelper implements Helper {
    @Override
    public ConcurrentHashMap<CurrencyCodes, BigDecimal> read(
            ConcurrentHashMap<CurrencyCodes, BigDecimal> currencyAmount, File file) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String currencyAndAmount;
            while ((currencyAndAmount = bufferedReader.readLine()) != null) {
                String[] strings = currencyAndAmount.split(" ");
                String currency = strings[0];
                BigDecimal amount = BigDecimal.valueOf(Double.valueOf(strings[1]));
                fillMap(currencyAmount, currency, amount);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return currencyAmount;
    }

    private void fillMap(ConcurrentHashMap<CurrencyCodes, BigDecimal> currencyAmount, String curren,
                         BigDecimal amount) {
        Arrays.stream(CurrencyCodes.values())
                .forEach(it -> {
                    if (it.name().equalsIgnoreCase(curren)) {
                        if (currencyAmount.get(CurrencyCodes.valueOf(curren)) != null) {
                            BigDecimal oldValue = currencyAmount.get(CurrencyCodes.valueOf(curren));
                            BigDecimal newValue = oldValue.add(amount);
                            currencyAmount.put(CurrencyCodes.valueOf(curren), newValue);
                        } else {
                            currencyAmount.put(CurrencyCodes.valueOf(curren), amount);
                        }
                    }
                });
    }
}
