package EIAMS.services.interfaces;

import EIAMS.dtos.StatusDto;
import EIAMS.entities.Status;
import EIAMS.exception.EntityNotFoundException;

public interface StatusServiceInterface {
    void update(int semesterId, int update, int value) throws EntityNotFoundException;

    void create(int semesterId);

    void delete(int semesterId);
}
