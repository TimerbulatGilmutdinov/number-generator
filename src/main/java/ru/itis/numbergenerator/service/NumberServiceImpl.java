package ru.itis.numbergenerator.service;

import org.springframework.stereotype.Service;
import ru.itis.numbergenerator.constant.Region;
import ru.itis.numbergenerator.model.CarNumber;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class NumberServiceImpl implements NumberService {
    private final char[] LETTERS = new char[]{'А', 'В', 'Е', 'К', 'М', 'Н', 'О', 'Р', 'С', 'Т', 'У', 'Х'};
    private final ConcurrentHashMap<CarNumber, Object> generatedCarNumbers = new ConcurrentHashMap<>();
    private final AtomicReference<CarNumber> lastCarNumber = new AtomicReference<>();
    private final Map<Character, Integer> letterIndexMap = new HashMap<>();
    private final Object PRESENT = new Object();
    private final Random random = new Random();
    private final int MAX_COUNT = 10 * 10 * 10 * 12 * 12 * 12;

    {
        for (int i = 0; i < LETTERS.length; i++) {
            char letter = LETTERS[i];
            letterIndexMap.put(letter, i);
        }
    }

    @Override
    public CarNumber getRandom() {
        if (numbersExceeded()) {
            throw new IllegalStateException("No more numbers left");
        }
        CarNumber lastNumber;
        CarNumber newLastCarNumber;
        do {
            lastNumber = lastCarNumber.get();
            newLastCarNumber = new CarNumber(getRandomSeries(), getRandomRegisterNumber(), Region.TATARSTAN);
            if (generatedCarNumbers.contains(newLastCarNumber)) {
                getRandom();
            }
        } while (!lastCarNumber.compareAndSet(lastNumber, newLastCarNumber));
        generatedCarNumbers.put(newLastCarNumber, PRESENT);
        return newLastCarNumber;
    }

    @Override
    public CarNumber getNext() {
        if (numbersExceeded()) {
            throw new IllegalStateException("No more numbers left.");
        }
        CarNumber lastNumber;
        CarNumber newLastCarNumber;
        do {
            lastNumber = lastCarNumber.get();
            if (lastNumber == null) {
                newLastCarNumber = new CarNumber(new char[]{'А', 'А', 'А'}, 0, Region.TATARSTAN);
            } else {
                newLastCarNumber = getNextNumberFor(lastNumber);
            }
            if (generatedCarNumbers.contains(newLastCarNumber)) {
                getNext();
            }
        } while (!lastCarNumber.compareAndSet(lastNumber, newLastCarNumber));
        generatedCarNumbers.put(newLastCarNumber, PRESENT);
        return newLastCarNumber;
    }

    private char[] getRandomSeries() {
        return new char[]{
                LETTERS[random.nextInt(LETTERS.length)],
                LETTERS[random.nextInt(LETTERS.length)],
                LETTERS[random.nextInt(LETTERS.length)]
        };
    }

    private int getRandomRegisterNumber() {
        return (int) (Math.random() * 1000);
    }

    private boolean isLastAvailableNumber(CarNumber carNumber) {
        return Arrays.equals(carNumber.series(), new char[]{
                LETTERS[LETTERS.length - 1],
                LETTERS[LETTERS.length - 1],
                LETTERS[LETTERS.length - 1]
        }) && carNumber.registerNumber() == 999;
    }

    protected CarNumber getNextNumberFor(CarNumber carNumber) {
        if (generatedCarNumbers.size() >= MAX_COUNT) {
            throw new IllegalStateException("No more numbers left.");
        }
        if (isLastAvailableNumber(carNumber)) {
            throw new IllegalStateException("Next number for " + carNumber + " unavailable.");
        }
        int registerNumber;
        char[] series = carNumber.series();
        if (carNumber.registerNumber() + 1 < 1000) {
            registerNumber = carNumber.registerNumber() + 1;
        } else {
            registerNumber = 0;
            if (isLastChar(series[2])) {
                if (isLastChar(series[1])) {
                    int firstCharIndex = letterIndexMap.get(series[0]);
                    series[0] = LETTERS[firstCharIndex + 1];
                    series[1] = LETTERS[0];
                    series[2] = LETTERS[0];
                } else {
                    int secondCharIndex = letterIndexMap.get(series[1]);
                    series[1] = LETTERS[secondCharIndex + 1];
                    series[2] = LETTERS[0];
                }
            } else {
                int thirdCharIndex = letterIndexMap.get(series[2]);
                series[2] = LETTERS[thirdCharIndex + 1];
            }
        }
        return new CarNumber(series, registerNumber, Region.TATARSTAN);
    }

    private boolean isLastChar(char c) {
        return c == LETTERS[LETTERS.length - 1];
    }

    private boolean numbersExceeded() {
        return generatedCarNumbers.size() >= MAX_COUNT;
    }
}
