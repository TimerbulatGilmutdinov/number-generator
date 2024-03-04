package ru.itis.numbergenerator.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itis.numbergenerator.service.NumberService;

@RestController
@RequiredArgsConstructor
public class MainController {
    private final NumberService numberService;

    @GetMapping("/random")
    public ResponseEntity<String> getRandomCarNumber() {
        return ResponseEntity.ok(numberService.getRandom().toString());
    }

    @GetMapping("/next")
    public ResponseEntity<String> getNextCarNumber() {
        return ResponseEntity.ok(numberService.getNext().toString());
    }
}
