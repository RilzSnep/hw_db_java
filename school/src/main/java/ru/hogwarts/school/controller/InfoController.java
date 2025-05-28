package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Stream;

@RestController
public class InfoController {
    @Value("${server.port}")
    private int port;

    @GetMapping("/port")
    public int getPort() {
        return port;
    }

    @GetMapping("/calculate-sum")
    public int calculateSum() {
        int n = 1_000_000;
        // Оптимизация 1: Используем формулу арифметической прогрессии
        int sumByFormula = (n * (n + 1)) / 2;

        // Оптимизация 2: Используем parallelStream для сравнения
        int sumByStream = Stream.iterate(1, a -> a + 1)
                .limit(n)
                .parallel() // Параллельная обработка
                .reduce(0, Integer::sum);

        // Проверяем, что результаты совпадают
        if (sumByFormula != sumByStream) {
            throw new IllegalStateException("Sum calculation mismatch");
        }

        return sumByFormula; // Возвращаем результат по формуле (быстрее)
    }
}