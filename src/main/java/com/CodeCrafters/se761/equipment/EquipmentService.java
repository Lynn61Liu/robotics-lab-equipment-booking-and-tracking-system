package com.CodeCrafters.se761.equipment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.CodeCrafters.se761.notification.NotificationService;

import java.util.List;
import java.util.Optional;

/**
 * This is a service class that handles business logic related to Equipment
 * entities, which acts as an intermediary between the controller and the repository,
 * performing operations on Equipment entities.
 * Author: Jonathon Lee
 */
@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    public EquipmentService(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    /**
     * Retrieves a list of all equipment entities sorted by systemID.
     *
     * @return List of Equipment entities.
     */
    public List<Equipment> getEquipment() {
        Sort sort = Sort.by(Sort.Order.asc("systemID"));
        return equipmentRepository.findAll(sort);
    }

    /**
     * Retrieves detailed information about a specific equipment entity by its unique ID.
     *
     * @param equipmentId Unique identifier of the equipment.
     * @return Equipment entity if found, null otherwise.
     */
    public Equipment getEquipmentDetailsById(Long equipmentId) {
        return equipmentRepository.findById(equipmentId).orElse(null);
    }

    /**
     * Retrieves information about a specific equipment entity by its unique systemID.
     *
     * @param systemID Unique identifier of the equipment.
     * @return Equipment entity if found, null otherwise.
     */
    public Equipment getOneEquipment(Long systemID) {
        return equipmentRepository.findOneBySystemID(systemID);
    }

    /**
     * Updates an existing equipment entity in the database.
     *
     * @param existingEquipment Equipment entity with updated information.
     */
    public void updateEquipment(Equipment existingEquipment) {
        equipmentRepository.save(existingEquipment);
    }

    /**
     * Deletes an equipment entity from the database by its unique systemID.
     *
     * @param systemID Unique identifier of the equipment to be deleted.
     */
    public void deleteEquipmentBySystemID(Long systemID) {
        Equipment equipment = equipmentRepository.findOneBySystemID(systemID);
        if (equipment != null) {
            equipmentRepository.delete(equipment);
        }
    }

    /**
     * Adds a new equipment entity to the database after validation checks.
     *
     * @param newEquipment Equipment entity to be added.
     * @return Unique systemID of the newly added equipment entity.
     * @throws IllegalStateException if an item with the same UOA ID already exists in the database.
     */
    public Long addNewEquipment(Equipment newEquipment) {
        String uoaID = newEquipment.getUoaID();

        // Make sure the UOA ID is not null and not an empty string
        if (uoaID != null && !uoaID.isEmpty()) {
            Optional<Equipment> equipmentOptional = equipmentRepository.findEquipmentByUoaID(uoaID);

            if (equipmentOptional.isPresent()) {
                throw new IllegalStateException("An item with this UOA ID already exists.");
            }
        }
        // Add equipment to database
        Equipment savedEquipment = equipmentRepository.save(newEquipment);

        // Return systemID
        return savedEquipment.getSystemID();

    }
}
