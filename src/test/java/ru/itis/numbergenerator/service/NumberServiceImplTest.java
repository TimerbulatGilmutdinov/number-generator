package ru.itis.numbergenerator.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.itis.numbergenerator.constant.Region;
import ru.itis.numbergenerator.model.CarNumber;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class NumberServiceImplTest {
    private NumberServiceImpl numberService;


    @BeforeEach
    void setUp() {
        numberService = new NumberServiceImpl();
    }

    @Test
    void testNextNumberForUnchangingSeries() {
        CarNumber carNumber = new CarNumber(new char[]{'С', 'В', 'А'}, 399, Region.TATARSTAN);
        CarNumber expectedNumber = new CarNumber(carNumber.series(), 400, Region.TATARSTAN);
        assertThat(expectedNumber).isEqualTo(numberService.getNextNumberFor(carNumber));
    }

    @Test
    void testNextNumberForChangingOneSeriesChar() {
        CarNumber carNumber = new CarNumber(new char[]{'А', 'А', 'А'}, 999, Region.TATARSTAN);
        CarNumber expectedNumber = new CarNumber(new char[]{'А', 'А', 'В'}, 0, Region.TATARSTAN);
        assertThat(expectedNumber).isEqualTo(numberService.getNextNumberFor(carNumber));
    }

    @Test
    void testNextNumberForChangingTwoSeriesChars() {
        CarNumber carNumber = new CarNumber(new char[]{'А', 'А', 'Х'}, 999, Region.TATARSTAN);
        CarNumber expectedNumber = new CarNumber(new char[]{'А', 'В', 'А'}, 0, Region.TATARSTAN);
        assertThat(expectedNumber).isEqualTo(numberService.getNextNumberFor(carNumber));
    }

    @Test
    void testNextNumberForChangingThreeSeriesChars() {
        CarNumber carNumber = new CarNumber(new char[]{'А', 'Х', 'Х'}, 999, Region.TATARSTAN);
        CarNumber expectedNumber = new CarNumber(new char[]{'В', 'А', 'А'}, 0, Region.TATARSTAN);
        assertThat(expectedNumber).isEqualTo(numberService.getNextNumberFor(carNumber));
    }

    @Test
    void testFirstNextNumberIsNotNull() {
        assertThat(numberService.getNext()).isNotNull();
    }

    @Test
    void testNextForLastAvailableNumberThrowsIllegalState() {
        CarNumber lastNumber = new CarNumber(new char[]{'Х', 'Х', 'Х'}, 999, Region.TATARSTAN);
        assertThatThrownBy(() -> numberService.getNextNumberFor(lastNumber)).isInstanceOf(IllegalStateException.class).hasMessage("Next number for " + lastNumber + " unavailable.");
    }

    @Test
    void testAnyRequestWithExceededNumbersThrowsIllegalState() {
        CarNumber number = new CarNumber(new char[]{'А', 'А', 'А'}, 0, Region.TATARSTAN);
        assertThatThrownBy(() -> numberService.getNextNumberFor(number)).isInstanceOf(IllegalStateException.class).hasMessage("No more numbers left.");
    }

    @Test
    void testUniqueNumber() {
        CarNumber carNumber = numberService.getRandom();
        ConcurrentHashMap<CarNumber, Object> map = new ConcurrentHashMap<>();
        Object stub = new Object();
        //max count is 10^3*12^3
        int maxNumsCount = 10 * 10 * 10 * 12 * 12;
        int threadsCount = 20;
        int carNumsPerThread = maxNumsCount / threadsCount;
        try (ExecutorService executor = Executors.newFixedThreadPool(threadsCount)) {
            for (int i = 0; i < threadsCount; i++) {
                executor.submit(() -> {
                    for (int j = 0; j < carNumsPerThread; j++) {
                        var f = numberService.getNext();
                        map.put(f, stub);
                    }
                });
            }
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
            assertThat(map.contains(carNumber)).isFalse();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testConcurrentNextRequestsDoesNotMakeRaceCondition() {
        ConcurrentHashMap<CarNumber, Object> map = new ConcurrentHashMap<>();
        Object stub = new Object();
        int threadsCount = 20;
        int carNumsPerThread = 50;
        try (ExecutorService executor = Executors.newFixedThreadPool(threadsCount)) {
            for (int i = 0; i < threadsCount; i++) {
                executor.submit(() -> {
                    for (int j = 0; j < carNumsPerThread; j++) {
                        var f = numberService.getNext();
                        map.put(f, stub);
                    }
                });
            }
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
            assertThat(map.size()).isEqualTo(threadsCount * carNumsPerThread);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testConcurrentRandomRequestsDoesNotMakeRaceCondition() {
        ConcurrentHashMap<CarNumber, Object> map = new ConcurrentHashMap<>();
        Object stub = new Object();
        int threadsCount = 20;
        int carNumsPerThread = 50;
        try (ExecutorService executor = Executors.newFixedThreadPool(threadsCount)) {
            for (int i = 0; i < threadsCount; i++) {
                executor.submit(() -> {
                    for (int j = 0; j < carNumsPerThread; j++) {
                        var f = numberService.getRandom();
                        map.put(f, stub);
                    }
                });
            }
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
            assertThat(map.size()).isEqualTo(threadsCount * carNumsPerThread);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
