package EIAMS.repositories;

import EIAMS.entities.Lecturer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LecturerRepository extends JpaRepository<Lecturer,Integer> {
}
