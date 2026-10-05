package org.example.aptus.repository;

import org.example.aptus.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository
        extends JpaRepository<Question, Integer> {


    @Query(value = """
            SELECT DISTINCT lang.language
            FROM questions_db q
            CROSS JOIN LATERAL
                jsonb_object_keys(q.question_data) AS lang(language)
            ORDER BY lang.language
            """, nativeQuery = true)
    List<String> findAvailableLanguages();

    @Query(value = """
       SELECT * FROM questions_db WHERE 
     jsonb_exists(question_data, CAST(:language AS text)) 
    ORDER BY RANDOM() LIMIT :limit """, nativeQuery = true)
    List<Question> findRandomQuestionsByLanguage(@Param("language") String language, @Param("limit") int limit);
}