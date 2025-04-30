package ru.hogwarts.school.model;

import jakarta.persistence.*;

@Entity
@Table(name = "avatar")
public class Avatar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filePath;

    @OneToOne
    @JoinColumn(name = "student_id")
    private Student student;

    public Avatar() {
    }

    public Avatar(Long id, String filePath, Student student) {
        this.id = id;
        this.filePath = filePath;
        this.student = student;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}