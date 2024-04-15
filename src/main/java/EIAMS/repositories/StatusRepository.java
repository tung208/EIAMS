package EIAMS.repositories;

import EIAMS.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatusRepository extends JpaRepository<Status,Integer> {
    Optional<Status> findBySemesterId(Integer semesterId);

    // Xóa các bản ghi dựa trên semesterId
    void deleteBySemesterId(Integer semesterId);
}
