package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.List;

@Service
public class FacultyService {
    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);
    private static final String NOT_FOUND_MESSAGE = "Faculty with ID {} not found";

    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty createFaculty(Faculty faculty) {
        logger.info("Was invoked method for create faculty");
        logger.debug("Creating faculty with name: {}", faculty.getName());
        faculty.setId(null); // Сбрасываем ID для создания новой записи
        return facultyRepository.save(faculty);
    }

    public Faculty getFaculty(Long id) {
        logger.info("Was invoked method for get faculty with ID: {}", id);
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error(NOT_FOUND_MESSAGE, id);
                    return new IllegalArgumentException("Факультет с ID " + id + " не найден");
                });
        logger.debug("Found faculty: {}", faculty.getName());
        return faculty;
    }

    public Faculty updateFaculty(Faculty faculty) {
        logger.info("Was invoked method for update faculty with ID: {}", faculty.getId());
        if (!facultyRepository.existsById(faculty.getId())) {
            logger.error(NOT_FOUND_MESSAGE, faculty.getId());
            throw new IllegalArgumentException("Факультет с ID " + faculty.getId() + " не найден");
        }
        logger.warn("Updating faculty with ID: {}", faculty.getId());
        return facultyRepository.save(faculty);
    }

    public void deleteFaculty(Long id) {
        logger.info("Was invoked method for delete faculty with ID: {}", id);
        if (!facultyRepository.existsById(id)) {
            logger.error(NOT_FOUND_MESSAGE, id);
            throw new IllegalArgumentException("Факультет с ID " + id + " не найден");
        }
        logger.debug("Deleting faculty with ID: {}", id);
        facultyRepository.deleteById(id);
    }

    public List<Faculty> getAllFaculties() {
        logger.info("Was invoked method for get all faculties");
        logger.debug("Retrieving all faculties");
        return facultyRepository.findAll();
    }

    public List<Faculty> filterFacultiesByColor(String color) {
        logger.info("Was invoked method for filter faculties by color: {}", color);
        logger.debug("Filtering faculties with color: {}", color);
        return facultyRepository.findByColorIgnoreCase(color);
    }

    public List<Faculty> findFacultiesByNameOrColor(String search) {
        logger.info("Was invoked method for find faculties by name or color: {}", search);
        logger.debug("Searching faculties with name or color: {}", search);
        return facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(search, search);
    }

    public List<Student> getStudentsByFacultyId(Long id) {
        logger.info("Was invoked method for get students by faculty ID: {}", id);
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error(NOT_FOUND_MESSAGE, id);
                    return new IllegalArgumentException("Факультет с ID " + id + " не найден");
                });
        logger.debug("Found students for faculty: {}", faculty.getName());
        return faculty.getStudents();
    }
}