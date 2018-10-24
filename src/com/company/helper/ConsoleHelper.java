package com.company.helper;

import com.company.CurrencyCodes;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class ConsoleHelper implements Helper {
    @Override
    public ConcurrentHashMap<CurrencyCodes, BigDecimal> read(ConcurrentHashMap<CurrencyCodes, BigDecimal> currencyAmount, File file) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String currencyAndAmount = "";
            while (!currencyAndAmount.equalsIgnoreCase("go")) {
                currencyAndAmount = bufferedReader.readLine();

                if (currencyAndAmount.equalsIgnoreCase("go")) continue;

                String[] strings = currencyAndAmount.split(" ");
                String currency = strings[0].toUpperCase();
                BigDecimal amount = BigDecimal.valueOf(Double.valueOf(strings[1]));
                currencyAmount = fillMap(currencyAmount, currency, amount);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return currencyAmount;
    }

    public ConcurrentHashMap<CurrencyCodes, BigDecimal> fillMap(
            ConcurrentHashMap<CurrencyCodes, BigDecimal> currencyAmount, String curren, BigDecimal amount) {

        Logger log = Logger.getLogger(ConsoleHelper.class.getName());
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

        boolean isValid = Arrays.stream(CurrencyCodes.values())
                .anyMatch(f -> f.name().equalsIgnoreCase(curren));
        if (!isValid) {
            log.warning(String.format("Валюта '%s' не поддерживается", curren));
        }
        return currencyAmount;
    }
}
