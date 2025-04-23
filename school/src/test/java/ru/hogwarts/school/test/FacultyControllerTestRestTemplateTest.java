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
public class FacultyControllerTestRestTemplateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/faculty";
    }

    @Test
    void createFaculty_shouldReturnCreatedFaculty() {
        // Arrange
        Faculty faculty = new Faculty(null, "Slytherin", "green", null);

        // Act
        ResponseEntity<Faculty> response = restTemplate.postForEntity(baseUrl, faculty, Faculty.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Slytherin");
        assertThat(response.getBody().getColor()).isEqualTo("green");
    }

    @Test
    void getFaculty_shouldReturnFacultyById() {
        // Arrange
        Faculty faculty = new Faculty(null, "Ravenclaw", "blue", null);
        ResponseEntity<Faculty> createResponse = restTemplate.postForEntity(baseUrl, faculty, Faculty.class);
        Long facultyId = createResponse.getBody().getId();

        // Act
        ResponseEntity<Faculty> response = restTemplate.getForEntity(baseUrl + "/" + facultyId, Faculty.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(facultyId);
        assertThat(response.getBody().getName()).isEqualTo("Ravenclaw");
        assertThat(response.getBody().getColor()).isEqualTo("blue");
    }

    @Test
    void getFaculty_shouldReturn404ForNonExistentId() {
        // Act
        ResponseEntity<Faculty> response = restTemplate.getForEntity(baseUrl + "/999", Faculty.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateFaculty_shouldReturnUpdatedFaculty() {
        // Arrange
        Faculty faculty = new Faculty(null, "Hufflepuff", "yellow", null);
        ResponseEntity<Faculty> createResponse = restTemplate.postForEntity(baseUrl, faculty, Faculty.class);
        Long facultyId = createResponse.getBody().getId();
        Faculty updatedFaculty = new Faculty(facultyId, "Hufflepuff Updated", "yellow", null);

        // Act
        ResponseEntity<Faculty> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.PUT,
                new HttpEntity<>(updatedFaculty),
                Faculty.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(facultyId);
        assertThat(response.getBody().getName()).isEqualTo("Hufflepuff Updated");
        assertThat(response.getBody().getColor()).isEqualTo("yellow");
    }


    @Test
    void getAllFaculties_shouldReturnListOfFaculties() {
        // Arrange
        restTemplate.postForEntity(baseUrl, new Faculty(null, "Faculty1", "color1", null), Faculty.class);
        restTemplate.postForEntity(baseUrl, new Faculty(null, "Faculty2", "color2", null), Faculty.class);

        // Act
        ResponseEntity<List> response = restTemplate.getForEntity(baseUrl, List.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void filterFacultiesByColor_shouldReturnFilteredFaculties() {
        // Arrange
        restTemplate.postForEntity(baseUrl, new Faculty(null, "Faculty3", "black", null), Faculty.class);
        restTemplate.postForEntity(baseUrl, new Faculty(null, "Faculty4", "black", null), Faculty.class);

        // Act
        ResponseEntity<List> response = restTemplate.getForEntity(baseUrl + "/filter?color=black", List.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void findFacultiesByNameOrColor_shouldReturnFaculties() {
        // Arrange
        restTemplate.postForEntity(baseUrl, new Faculty(null, "Faculty5", "purple", null), Faculty.class);
        restTemplate.postForEntity(baseUrl, new Faculty(null, "Faculty6", "purple", null), Faculty.class);

        // Act
        ResponseEntity<List> response = restTemplate.getForEntity(baseUrl + "/search?search=purple", List.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void getStudentsByFacultyId_shouldReturnStudents() {
        // Arrange
        Faculty faculty = new Faculty(null, "Faculty7", "orange", null);
        ResponseEntity<Faculty> facultyResponse = restTemplate.postForEntity(baseUrl, faculty, Faculty.class);
        Long facultyId = facultyResponse.getBody().getId();

        Student student1 = new Student(null, "Student1", 15, facultyResponse.getBody());
        Student student2 = new Student(null, "Student2", 16, facultyResponse.getBody());
        restTemplate.postForEntity("http://localhost:" + port + "/student", student1, Student.class);
        restTemplate.postForEntity("http://localhost:" + port + "/student", student2, Student.class);

        // Act
        ResponseEntity<List> response = restTemplate.getForEntity(
                baseUrl + "/" + facultyId + "/students",
                List.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isEqualTo(2);
    }
}