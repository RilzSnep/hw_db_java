package ru.hogwarts.school.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTestRestTemplateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/student";
    }

    @Test
    void createStudent_shouldReturnCreatedStudent() {
        // Arrange
        Student student = new Student(null, "Harry Potter", 15, null);

        // Act
        ResponseEntity<Student> response = restTemplate.postForEntity(baseUrl, student, Student.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Harry Potter");
        assertThat(response.getBody().getAge()).isEqualTo(15);
    }

    @Test
    void getStudent_shouldReturnStudentById() {
        // Arrange
        Student student = new Student(null, "Hermione Granger", 16, null);
        ResponseEntity<Student> createResponse = restTemplate.postForEntity(baseUrl, student, Student.class);
        Long studentId = createResponse.getBody().getId();

        // Act
        ResponseEntity<Student> response = restTemplate.getForEntity(baseUrl + "/" + studentId, Student.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(studentId);
        assertThat(response.getBody().getName()).isEqualTo("Hermione Granger");
        assertThat(response.getBody().getAge()).isEqualTo(16);
    }

    @Test
    void getStudent_shouldReturn404ForNonExistentId() {
        // Act
        ResponseEntity<Student> response = restTemplate.getForEntity(baseUrl + "/999", Student.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateStudent_shouldReturnUpdatedStudent() {
        // Arrange
        Student student = new Student(null, "Ron Weasley", 15, null);
        ResponseEntity<Student> createResponse = restTemplate.postForEntity(baseUrl, student, Student.class);
        Long studentId = createResponse.getBody().getId();
        Student updatedStudent = new Student(studentId, "Ron Weasley Updated", 16, null);

        // Act
        ResponseEntity<Student> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.PUT,
                new HttpEntity<>(updatedStudent),
                Student.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(studentId);
        assertThat(response.getBody().getName()).isEqualTo("Ron Weasley Updated");
        assertThat(response.getBody().getAge()).isEqualTo(16);
    }



    @Test
    void getAllStudents_shouldReturnListOfStudents() {
        // Arrange
        restTemplate.postForEntity(baseUrl, new Student(null, "Luna Lovegood", 14, null), Student.class);
        restTemplate.postForEntity(baseUrl, new Student(null, "Neville Longbottom", 15, null), Student.class);

        // Act
        ResponseEntity<List> response = restTemplate.getForEntity(baseUrl, List.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void filterStudentsByAge_shouldReturnFilteredStudents() {
        // Arrange
        restTemplate.postForEntity(baseUrl, new Student(null, "Ginny Weasley", 14, null), Student.class);
        restTemplate.postForEntity(baseUrl, new Student(null, "Fred Weasley", 17, null), Student.class);

        // Act
        ResponseEntity<List> response = restTemplate.getForEntity(baseUrl + "/filter?age=14", List.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    void findStudentsByAgeBetween_shouldReturnStudentsInRange() {
        // Arrange
        restTemplate.postForEntity(baseUrl, new Student(null, "George Weasley", 17, null), Student.class);
        restTemplate.postForEntity(baseUrl, new Student(null, "Percy Weasley", 19, null), Student.class);

        // Act
        ResponseEntity<List> response = restTemplate.getForEntity(
                baseUrl + "/filter-by-age-range?min=16&max=18",
                List.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    void getFacultyByStudentId_shouldReturnFaculty() {
        // Arrange
        Faculty faculty = new Faculty(null, "Gryffindor", "red", null);
        ResponseEntity<Faculty> facultyResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/faculty",
                faculty,
                Faculty.class
        );
        Long facultyId = facultyResponse.getBody().getId();

        Student student = new Student(null, "Cho Chang", 15, facultyResponse.getBody());
        ResponseEntity<Student> studentResponse = restTemplate.postForEntity(baseUrl, student, Student.class);
        Long studentId = studentResponse.getBody().getId();

        // Act
        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                baseUrl + "/" + studentId + "/faculty",
                Faculty.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(facultyId);
        assertThat(response.getBody().getName()).isEqualTo("Gryffindor");
    }
}