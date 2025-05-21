package ru.hogwarts.school.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Тестовый класс для StudentController с использованием WebMvcTest
@WebMvcTest(StudentController.class)
public class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    // Мокаем StudentService, так как он является зависимостью StudentController
    @MockBean
    private StudentService studentService;

    // Мокаем StudentRepository, так как StudentService зависит от него
    @MockBean
    private StudentRepository studentRepository;

    // Тест для создания студента
    @Test
    public void testCreateStudent() throws Exception {
        Faculty faculty = new Faculty(1L, "Гриффиндор", "Красный", null);
        Student student = new Student(null, "Гарри Поттер", 17, faculty);
        Student savedStudent = new Student(1L, "Гарри Поттер", 17, faculty);

        when(studentService.createStudent(any(Student.class))).thenReturn(savedStudent);

        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Гарри Поттер\", \"age\": 17, \"faculty\": {\"id\": 1, \"name\": \"Гриффиндор\", \"color\": \"Красный\"}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Гарри Поттер"))
                .andExpect(jsonPath("$.age").value(17))
                .andExpect(jsonPath("$.faculty.id").value(1L))
                .andExpect(jsonPath("$.faculty.name").value("Гриффиндор"));
    }

    // Тест для получения студента по ID
    @Test
    public void testGetStudent() throws Exception {
        Faculty faculty = new Faculty(1L, "Гриффиндор", "Красный", null);
        Student student = new Student(1L, "Гарри Поттер", 17, faculty);

        when(studentService.getStudent(1L)).thenReturn(student);

        mockMvc.perform(get("/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Гарри Поттер"))
                .andExpect(jsonPath("$.age").value(17))
                .andExpect(jsonPath("$.faculty.id").value(1L))
                .andExpect(jsonPath("$.faculty.name").value("Гриффиндор"));
    }

    // Тест для обновления студента
    @Test
    public void testUpdateStudent() throws Exception {
        Faculty faculty = new Faculty(1L, "Гриффиндор", "Красный", null);
        Student updatedStudent = new Student(1L, "Рон Уизли", 18, faculty);

        when(studentService.updateStudent(any(Student.class))).thenReturn(updatedStudent);

        mockMvc.perform(put("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, \"name\": \"Рон Уизли\", \"age\": 18, \"faculty\": {\"id\": 1, \"name\": \"Гриффиндор\", \"color\": \"Красный\"}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Рон Уизли"))
                .andExpect(jsonPath("$.age").value(18))
                .andExpect(jsonPath("$.faculty.id").value(1L))
                .andExpect(jsonPath("$.faculty.name").value("Гриффиндор"));
    }

    // Тест для удаления студента
    @Test
    public void testDeleteStudent() throws Exception {
        doNothing().when(studentService).deleteStudent(1L);

        mockMvc.perform(delete("/student/1"))
                .andExpect(status().isOk());
    }

    // Тест для получения всех студентов
    @Test
    public void testGetAllStudents() throws Exception {
        Faculty faculty = new Faculty(1L, "Гриффиндор", "Красный", null);
        List<Student> students = List.of(
                new Student(1L, "Гарри Поттер", 17, faculty),
                new Student(2L, "Рон Уизли", 18, faculty)
        );

        when(studentService.getAllStudents()).thenReturn(students);

        mockMvc.perform(get("/student"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Гарри Поттер"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Рон Уизли"));
    }

    // Тест для фильтрации студентов по возрасту
    @Test
    public void testFilterStudentsByAge() throws Exception {
        Faculty faculty = new Faculty(1L, "Гриффиндор", "Красный", null);
        List<Student> students = List.of(new Student(1L, "Гарри Поттер", 17, faculty));

        when(studentService.filterStudentsByAge(17)).thenReturn(students);

        mockMvc.perform(get("/student/filter?age=17"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Гарри Поттер"))
                .andExpect(jsonPath("$[0].age").value(17));
    }

    // Тест для поиска студентов по диапазону возраста
    @Test
    public void testFindStudentsByAgeBetween() throws Exception {
        Faculty faculty = new Faculty(1L, "Гриффиндор", "Красный", null);
        List<Student> students = List.of(
                new Student(1L, "Гарри Поттер", 17, faculty),
                new Student(2L, "Рон Уизли", 18, faculty)
        );

        when(studentService.findStudentsByAgeBetween(16, 19)).thenReturn(students);

        mockMvc.perform(get("/student/filter-by-age-range?min=16&max=19"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Гарри Поттер"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Рон Уизли"));
    }

    // Тест для получения факультета по ID студента
    @Test
    public void testGetFacultyByStudentId() throws Exception {
        Faculty faculty = new Faculty(1L, "Гриффиндор", "Красный", Collections.emptyList());

        when(studentService.getFacultyByStudentId(1L)).thenReturn(faculty);

        mockMvc.perform(get("/student/1/faculty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Гриффиндор"))
                .andExpect(jsonPath("$.color").value("Красный"));
    }
}