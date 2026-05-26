package com.CodeCrafters.se761.equipment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * This is a repository interface for performing CRUD operations on Equipment entities,
 * which extends JpaRepository, providing basic database operations for Equipment entities.
 * This class sends and retrieves information to/from the 3rd party database, Aiven for
 * information about equipment.
 * Author: Jonathon Lee
 */
@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    /**
     * Retrieves an equipment entity by its unique systemID.
     *
     * @param systemID Unique identifier of the equipment.
     * @return Equipment entity if found, null otherwise.
     */
    Equipment findOneBySystemID(Long systemID);

    /**
     * Custom query to find an equipment entity by its unique uoaID.
     *
     * @param uoaID Unique identifier of the equipment within the University of Auckland system.
     * @return Optional containing the equipment entity if found, empty otherwise.
     */
    @Query("SELECT e from Equipment e where e.uoaID = ?1")
    Optional<Equipment> findEquipmentByUoaID(String uoaID);
}