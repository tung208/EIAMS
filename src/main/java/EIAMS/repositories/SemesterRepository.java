package EIAMS.repositories;

import EIAMS.entities.Semester;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SemesterRepository extends JpaRepository<Semester, Integer> {
//    @Query("SELECT s FROM Semester s"+
//            "WHERE (CASE WHEN :name = '' THEN TRUE ELSE s.name LIKE %:name% END) = TRUE" +
//            "AND (CASE WHEN :code = '' THEN TRUE ELSE s.code LIKE %:code% END) = TRUE" )
@Query("SELECT s FROM Semester s " +
        "WHERE (:name = '' or s.name LIKE %:name% )" +
        "AND (:code = '' or s.code LIKE %:code% ) " )
    Page<Semester> findByDynamic(String name, String code, Pageable pageable);
}