package ru.hogwarts.school.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import ru.hogwarts.school.model.Student;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTestRestTemplateTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        // Очищаем базу перед каждым тестом
        // Получаем всех студентов и удаляем их
        var studentsResponse = restTemplate.getForEntity("/student", Student[].class);
        if (studentsResponse.getBody() != null) {
            for (Student student : studentsResponse.getBody()) {
                restTemplate.delete("/student/" + student.getId());
            }
        }

        // Добавляем тестовых студентов для тестов, которым нужны минимум 6 студентов
        if (!this.getClass().getSimpleName().equals("StudentControllerTestRestTemplateTest")) {
            return;
        }
        restTemplate.postForEntity("/student", new Student(null, "Анна", 20), Student.class);
        restTemplate.postForEntity("/student", new Student(null, "Борис", 22), Student.class);
        restTemplate.postForEntity("/student", new Student(null, "Виктор", 24), Student.class);
        restTemplate.postForEntity("/student", new Student(null, "Галина", 26), Student.class);
        restTemplate.postForEntity("/student", new Student(null, "Дмитрий", 28), Student.class);
        restTemplate.postForEntity("/student", new Student(null, "Елена", 30), Student.class);
    }

    @Test
    void createStudent() {
        Student student = new Student(null, "Harry", 20);

        var response = restTemplate.postForEntity("/student", student, Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Harry");
        assertThat(response.getBody().getAge()).isEqualTo(20);
    }

    @Test
    void getStudent() {
        Student student = new Student(null, "Harry", 20);
        var createResponse = restTemplate.postForEntity("/student", student, Student.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();

        Long id = createResponse.getBody().getId();
        var response = restTemplate.getForEntity("/student/" + id, Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Harry");
        assertThat(response.getBody().getAge()).isEqualTo(20);
    }

    @Test
    void updateStudent() {
        Student student = new Student(null, "Harry", 20);
        var createResponse = restTemplate.postForEntity("/student", student, Student.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();

        Long id = createResponse.getBody().getId();
        Student updatedStudent = new Student(id, "Harry Potter", 21);
        restTemplate.put("/student/" + id, updatedStudent);

        var response = restTemplate.getForEntity("/student/" + id, Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Harry Potter");
        assertThat(response.getBody().getAge()).isEqualTo(21);
    }

    @Test
    void getNamesStartingWithA() {
        // Очищаем базу вручную для теста
        var studentsResponse = restTemplate.getForEntity("/student", Student[].class);
        if (studentsResponse.getBody() != null) {
            for (Student student : studentsResponse.getBody()) {
                restTemplate.delete("/student/" + student.getId());
            }
        }

        restTemplate.postForEntity("/student", new Student(null, "Анна", 20), Student.class);
        restTemplate.postForEntity("/student", new Student(null, "Алексей", 22), Student.class);
        restTemplate.postForEntity("/student", new Student(null, "Борис", 24), Student.class);

        var response = restTemplate.getForEntity("/student/names-starting-with-a", String[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly("АЛЕКСЕЙ", "АННА");
    }

    @Test
    void calculateAverageAgeUsingStream() {
        // Очищаем базу вручную для теста
        var studentsResponse = restTemplate.getForEntity("/student", Student[].class);
        if (studentsResponse.getBody() != null) {
            for (Student student : studentsResponse.getBody()) {
                restTemplate.delete("/student/" + student.getId());
            }
        }

        restTemplate.postForEntity("/student", new Student(null, "Анна", 20), Student.class);
        restTemplate.postForEntity("/student", new Student(null, "Алексей", 22), Student.class);
        restTemplate.postForEntity("/student", new Student(null, "Борис", 24), Student.class);

        var response = restTemplate.getForEntity("/student/average-age-stream", Double.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(22.0);
    }

    @Test
    void printStudentNamesParallel() {
        var response = restTemplate.getForEntity("/student/print-parallel", Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        // Проверка логики выводится в консоль, поэтому проверяем только статус
    }

    @Test
    void printStudentNamesSynchronized() {
        var response = restTemplate.getForEntity("/student/print-synchronized", Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        // Проверка логики выводится в консоль, поэтому проверяем только статус
    }
}