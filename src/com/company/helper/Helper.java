package com.company.helper;

import com.company.CurrencyCodes;

import java.io.File;
import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;

public interface Helper {
    ConcurrentHashMap<CurrencyCodes, BigDecimal> read(ConcurrentHashMap<CurrencyCodes, BigDecimal> map, File file);
}
