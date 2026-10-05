package org.example.aptus.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Table(name = "questions_db")
@Getter
@Setter
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Integer questionId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(
            name = "question_data",
            columnDefinition = "jsonb",
            nullable = false
    )
    private Map<String, Object> questionData;

    @Column(name = "marks", nullable = false)
    private Integer marks;
}