package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;

@Service
public class StudentService {
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);
    private static final String NOT_FOUND_MESSAGE = "Student with ID {} not found";
    private static final String NO_FACULTY_MESSAGE = "Student with ID {} has no faculty assigned";

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student) {
        logger.info("Was invoked method for create student");
        logger.debug("Creating student with name: {}", student.getName());
        student.setId(null); // Сбрасываем ID для создания новой записи
        return studentRepository.save(student);
    }

    public Student getStudent(Long id) {
        logger.info("Was invoked method for get student with ID: {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error(NOT_FOUND_MESSAGE, id);
                    return new IllegalArgumentException("Студент с ID " + id + " не найден");
                });
        logger.debug("Found student: {}", student.getName());
        return student;
    }

    public Student updateStudent(Student student) {
        logger.info("Was invoked method for update student with ID: {}", student.getId());
        if (!studentRepository.existsById(student.getId())) {
            logger.error(NOT_FOUND_MESSAGE, student.getId());
            throw new IllegalArgumentException("Студент с ID " + student.getId() + " не найден");
        }
        logger.warn("Updating student with ID: {}", student.getId());
        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        logger.info("Was invoked method for delete student with ID: {}", id);
        if (!studentRepository.existsById(id)) {
            logger.error(NOT_FOUND_MESSAGE, id);
            throw new IllegalArgumentException("Студент с ID " + id + " не найден");
        }
        logger.debug("Deleting student with ID: {}", id);
        studentRepository.deleteById(id);
    }

    public List<Student> getAllStudents() {
        logger.info("Was invoked method for get all students");
        logger.debug("Retrieving all students");
        return studentRepository.findAll();
    }

    public List<Student> filterStudentsByAge(int age) {
        logger.info("Was invoked method for filter students by age: {}", age);
        logger.debug("Filtering students with age: {}", age);
        return studentRepository.findByAge(age);
    }

    public List<Student> findStudentsByAgeBetween(int min, int max) {
        logger.info("Was invoked method for find students by age between {} and {}", min, max);
        logger.debug("Searching students with age between {} and {}", min, max);
        return studentRepository.findByAgeBetween(min, max);
    }

    public Faculty getFacultyByStudentId(Long id) {
        logger.info("Was invoked method for get faculty by student ID: {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error(NOT_FOUND_MESSAGE, id);
                    return new IllegalArgumentException("Студент с ID " + id + " не найден");
                });
        if (student.getFaculty() == null) {
            logger.warn(NO_FACULTY_MESSAGE, id);
            throw new IllegalArgumentException("У студента с ID " + id + " не назначен факультет");
        }
        logger.debug("Found faculty: {}", student.getFaculty().getName());
        return student.getFaculty();
    }

    public int getTotalStudentsCount() {
        logger.info("Was invoked method for get total students count");
        logger.debug("Calculating total students count");
        return studentRepository.getTotalStudentsCount();
    }

    public double getAverageAge() {
        logger.info("Was invoked method for get average age");
        logger.debug("Calculating average age of students");
        return studentRepository.getAverageAge();
    }

    public List<Student> getLastFiveStudents() {
        logger.info("Was invoked method for get last five students");
        logger.debug("Retrieving last five students by ID");
        return studentRepository.findLastFiveStudents();
    }
}