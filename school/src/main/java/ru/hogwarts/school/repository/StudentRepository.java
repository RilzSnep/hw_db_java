package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Student;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByAge(int age);
    List<Student> findByAgeBetween(int min, int max);
    List<Student> findByFacultyId(Long facultyId);

    // Получение количества всех студентов
    @Query("SELECT COUNT(s) FROM Student s")
    int getTotalStudentsCount();

    // Получение среднего возраста студентов
    @Query("SELECT AVG(s.age) FROM Student s")
    double getAverageAge();

    // Получение последних 5 студентов по ID (сортировка по убыванию ID)
    @Query("SELECT s FROM Student s ORDER BY s.id DESC")
    List<Student> findLastFiveStudents();
}