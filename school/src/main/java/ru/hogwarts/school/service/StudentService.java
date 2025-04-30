package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student) {
        student.setId(null); // Сбрасываем ID для создания новой записи
        return studentRepository.save(student);
    }

    public Student getStudent(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Студент с ID " + id + " не найден"));
    }

    public Student updateStudent(Student student) {
        if (!studentRepository.existsById(student.getId())) {
            throw new IllegalArgumentException("Студент с ID " + student.getId() + " не найден");
        }
        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new IllegalArgumentException("Студент с ID " + id + " не найден");
        }
        studentRepository.deleteById(id);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public List<Student> filterStudentsByAge(int age) {
        return studentRepository.findByAge(age);
    }

    public List<Student> findStudentsByAgeBetween(int min, int max) {
        return studentRepository.findByAgeBetween(min, max);
    }

    public Faculty getFacultyByStudentId(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Студент с ID " + studentId + " не найден"));
        if (student.getFaculty() == null) {
            throw new IllegalStateException("У студента с ID " + studentId + " не указан факультет");
        }
        return student.getFaculty();
    }

    // Методы для SQL-запросов
    public int getTotalStudentsCount() {
        return studentRepository.getTotalStudentsCount();
    }

    public double getAverageAge() {
        return studentRepository.getAverageAge();
    }

    public List<Student> getLastFiveStudents() {
        List<Student> lastStudents = studentRepository.findLastFiveStudents();
        return lastStudents.size() > 5 ? lastStudents.subList(0, 5) : lastStudents;
    }
}