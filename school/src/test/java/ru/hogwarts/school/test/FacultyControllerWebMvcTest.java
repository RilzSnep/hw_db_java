package ru.hogwarts.school.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Тестовый класс для FacultyController с использованием WebMvcTest
@WebMvcTest(FacultyController.class)
public class FacultyControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    // Мокаем FacultyService, так как он является зависимостью FacultyController
    @MockBean
    private FacultyService facultyService;

    // Мокаем репозитории, так как FacultyService зависит от них
    @MockBean
    private FacultyRepository facultyRepository;

    @MockBean
    private StudentRepository studentRepository;

    // Тест для создания факультета
    @Test
    public void testCreateFaculty() throws Exception {
        Faculty faculty = new Faculty(null, "Гриффиндор", "Красный", Collections.emptyList());
        Faculty savedFaculty = new Faculty(1L, "Гриффиндор", "Красный", Collections.emptyList());

        when(facultyService.createFaculty(any(Faculty.class))).thenReturn(savedFaculty);

        mockMvc.perform(post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Гриффиндор\", \"color\": \"Красный\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Гриффиндор"))
                .andExpect(jsonPath("$.color").value("Красный"));
    }

    // Тест для получения факультета по ID
    @Test
    public void testGetFaculty() throws Exception {
        Faculty faculty = new Faculty(1L, "Гриффиндор", "Красный", Collections.emptyList());

        when(facultyService.getFaculty(1L)).thenReturn(faculty);

        mockMvc.perform(get("/faculty/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Гриффиндор"))
                .andExpect(jsonPath("$.color").value("Красный"));
    }

    // Тест для обновления факультета
    @Test
    public void testUpdateFaculty() throws Exception {
        Faculty updatedFaculty = new Faculty(1L, "Слизерин", "Зеленый", Collections.emptyList());

        when(facultyService.updateFaculty(any(Faculty.class))).thenReturn(updatedFaculty);

        mockMvc.perform(put("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, \"name\": \"Слизерин\", \"color\": \"Зеленый\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Слизерин"))
                .andExpect(jsonPath("$.color").value("Зеленый"));
    }

    // Тест для удаления факультета
    @Test
    public void testDeleteFaculty() throws Exception {
        doNothing().when(facultyService).deleteFaculty(1L);

        mockMvc.perform(delete("/faculty/1"))
                .andExpect(status().isOk());
    }

    // Тест для получения всех факультетов
    @Test
    public void testGetAllFaculties() throws Exception {
        List<Faculty> faculties = List.of(
                new Faculty(1L, "Гриффиндор", "Красный", Collections.emptyList()),
                new Faculty(2L, "Слизерин", "Зеленый", Collections.emptyList())
        );

        when(facultyService.getAllFaculties()).thenReturn(faculties);

        mockMvc.perform(get("/faculty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Гриффиндор"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Слизерин"));
    }

    // Тест для фильтрации факультетов по цвету
    @Test
    public void testFilterFacultiesByColor() throws Exception {
        List<Faculty> faculties = List.of(new Faculty(1L, "Гриффиндор", "Красный", Collections.emptyList()));

        when(facultyService.filterFacultiesByColor("Красный")).thenReturn(faculties);

        mockMvc.perform(get("/faculty/filter?color=Красный"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Гриффиндор"))
                .andExpect(jsonPath("$[0].color").value("Красный"));
    }

    // Тест для поиска факультетов по имени или цвету
    @Test
    public void testFindFacultiesByNameOrColor() throws Exception {
        List<Faculty> faculties = List.of(new Faculty(1L, "Гриффиндор", "Красный", Collections.emptyList()));

        when(facultyService.findFacultiesByNameOrColor("Гриффиндор")).thenReturn(faculties);

        mockMvc.perform(get("/faculty/search?search=Гриффиндор"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Гриффиндор"))
                .andExpect(jsonPath("$[0].color").value("Красный"));
    }

    // Тест для получения студентов по ID факультета
    @Test
    public void testGetStudentsByFacultyId() throws Exception {
        Faculty faculty = new Faculty(1L, "Гриффиндор", "Красный", null);
        List<Student> students = List.of(new Student(1L, "Гарри Поттер", 17, faculty));

        when(facultyService.getStudentsByFacultyId(1L)).thenReturn(students);

        mockMvc.perform(get("/faculty/1/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Гарри Поттер"))
                .andExpect(jsonPath("$[0].age").value(17));
    }
}