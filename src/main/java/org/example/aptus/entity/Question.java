package org.example.aptus.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Table(name = "questions_db")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Integer questionId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "question_data", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> questionData;

    @Column(name = "marks", nullable = false)
    private Integer marks;

    public Integer getQuestionId() {
        return questionId;
    }

    public Map<String, Object> getQuestionData() {
        return questionData;
    }

    public void setQuestionData(Map<String, Object> questionData) {
        this.questionData = questionData;
    }

    public Integer getMarks() {
        return marks;
    }

    public void setMarks(Integer marks) {
        this.marks = marks;
    }
}