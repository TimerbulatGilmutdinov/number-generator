package ru.itis.numbergenerator.service;

import ru.itis.numbergenerator.model.CarNumber;

public interface NumberService {
    CarNumber getRandom();
    CarNumber getNext();
}
