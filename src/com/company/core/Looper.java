package com.company.core;

import com.company.CurrencyCodes;
import com.company.helper.ConsoleHelper;
import com.company.helper.FilesHelper;
import com.company.helper.Helper;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class Looper {
    private File file;
    private ConcurrentHashMap<CurrencyCodes, BigDecimal> bigDecimalMap;
    private ConsoleHelper consoleHelper;
    private Logger log;
    private Properties properties;


    public Looper() {
        try {
            FileInputStream fis = new FileInputStream("config.properties");
            properties = new Properties();
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        bigDecimalMap = new ConcurrentHashMap<>();
        consoleHelper = new ConsoleHelper();
        log = Logger.getLogger(Looper.class.getName());
        file = new File(properties.getProperty("filepath"));
    }

    public void start() {
        Helper filesHelper;

        if (Files.exists(file.toPath())) {
            filesHelper = new FilesHelper();
            bigDecimalMap = filesHelper.read(bigDecimalMap, file);
            printCurrentAmount(bigDecimalMap);

            startDaemonThread();
            readLineLoop();

        } else {
            filesHelper = new ConsoleHelper();
            bigDecimalMap = filesHelper.read(bigDecimalMap, null);
            printCurrentAmount(bigDecimalMap);

            startDaemonThread();
            readLineLoop();
        }
    }

    private void startDaemonThread() {
        Thread t1 = new Thread(this::printLoop);
        t1.setDaemon(true);
        t1.start();
    }

    private void readLineLoop() {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String currencyAndAmount = "";
            while (!currencyAndAmount.equalsIgnoreCase("quit")) {
                currencyAndAmount = bufferedReader.readLine();
                String[] strings = currencyAndAmount.split(" ");
                if (currencyAndAmount.equalsIgnoreCase("quit")) continue;
                consoleHelper.fillMap(bigDecimalMap, strings[0].toUpperCase(),
                        BigDecimal.valueOf(Double.valueOf(strings[1])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Введите имя файла для сохранения:");
            String fileName = "";
            try {
                fileName = bufferedReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Path file = Paths.get(String.format("%s.txt", fileName));
            List<String> lines = new ArrayList<>();
            bigDecimalMap.forEach((key, value) -> lines.add(key.name() + " " + value));
            try {
                Files.write(file, lines, Charset.forName("UTF-8"));
                log.info("Файл успешно сохранен");
            } catch (IOException e) {
                log.warning("Не удалось сохранить файл");
                e.printStackTrace();
            }
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void printLoop() {
        while (true) {
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            printCurrentAmount(bigDecimalMap);
        }
    }

    private void printCurrentAmount(ConcurrentHashMap<CurrencyCodes, BigDecimal> map) {
        map.entrySet().stream()
                .filter(entry -> !entry.getValue().equals(BigDecimal.ZERO))
                .filter(entry -> !entry.getValue().equals(BigDecimal.valueOf(0.0)))
                .forEach(entry -> System.out.println(entry.getKey() + " " + entry.getValue().toString()));
    }
}
