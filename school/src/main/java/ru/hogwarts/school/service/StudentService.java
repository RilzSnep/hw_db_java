package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;
import java.util.stream.Collectors;

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
        student.setId(null);
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

    public List<String> getNamesStartingWithA() {
        logger.info("Was invoked method for get names starting with A");
        logger.debug("Filtering and sorting student names starting with A");
        return studentRepository.findAll().stream()
                .map(Student::getName)
                .filter(name -> name.startsWith("А"))
                .map(String::toUpperCase)
                .sorted()
                .collect(Collectors.toList());
    }

    public double calculateAverageAgeUsingStream() {
        logger.info("Was invoked method for calculate average age using stream");
        logger.debug("Calculating average age using stream");
        List<Student> students = studentRepository.findAll();
        if (students.isEmpty()) {
            logger.warn("No students found for calculating average age");
            return 0.0;
        }
        return students.stream()
                .mapToInt(Student::getAge)
                .average()
                .orElse(0.0);
    }

    public void printStudentNamesParallel() {
        logger.info("Was invoked method for print student names in parallel");
        List<Student> students = studentRepository.findAll();
        if (students.size() < 6) {
            logger.error("Not enough students to print (need at least 6, found {})", students.size());
            throw new IllegalStateException("В базе должно быть минимум 6 студентов");
        }

        // Первые два имени в основном потоке
        System.out.println("Main thread: " + students.get(0).getName());
        System.out.println("Main thread: " + students.get(1).getName());

        // Третий и четвертый студент в первом параллельном потоке
        new Thread(() -> {
            System.out.println("Thread 1: " + students.get(2).getName());
            System.out.println("Thread 1: " + students.get(3).getName());
        }).start();

        // Пятый и шестой студент во втором параллельном потоке
        new Thread(() -> {
            System.out.println("Thread 2: " + students.get(4).getName());
            System.out.println("Thread 2: " + students.get(5).getName());
        }).start();
    }

    // Синхронизированный метод для вывода имени
    public synchronized void printStudentName(String name, String threadName) {
        System.out.println(threadName + ": " + name);
    }

    public void printStudentNamesSynchronized() {
        logger.info("Was invoked method for print student names in synchronized mode");
        List<Student> students = studentRepository.findAll();
        if (students.size() < 6) {
            logger.error("Not enough students to print (need at least 6, found {})", students.size());
            throw new IllegalStateException("В базе должно быть минимум 6 студентов");
        }

        // Первые два имени в основном потоке
        printStudentName(students.get(0).getName(), "Main thread");
        printStudentName(students.get(1).getName(), "Main thread");

        // Третий и четвертый студент в первом параллельном потоке
        new Thread(() -> {
            printStudentName(students.get(2).getName(), "Thread 1");
            printStudentName(students.get(3).getName(), "Thread 1");
        }).start();

        // Пятый и шестой студент во втором параллельном потоке
        new Thread(() -> {
            printStudentName(students.get(4).getName(), "Thread 2");
            printStudentName(students.get(5).getName(), "Thread 2");
        }).start();
    }
}