package com.CodeCrafters.se761.equipment;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * This is a controller class handling HTTP requests related to equipment operations.
 * It is the first layer of the controller-service-repository model for the backend.
 * Author: Jonathon Lee
 */
@RestController
public class EquipmentController {

    private final EquipmentService equipmentService;

    @Autowired
    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    /**
     * Handles GET request to retrieve equipment details by systemID.
     *
     * @param systemID ID of the equipment to retrieve.
     * @return ResponseEntity containing Equipment details or 404 if not found.
     */
    @GetMapping("/equipment/{systemID}")
    public ResponseEntity<Equipment> getNewBookingPageWithEquipmentDetails(@PathVariable Long systemID) {
        Equipment equipment = equipmentService.getEquipmentDetailsById(systemID);
        if (equipment != null) {
            return ResponseEntity.ok(equipment);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Handles GET request to retrieve all equipment details.
     *
     * @return JSON representation of the list of Equipment objects.
     */
    @GetMapping("/getall")
    public String processEquipment() {
        List<Equipment> equipmentList = equipmentService.getEquipment();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonData = objectMapper.writeValueAsString(equipmentList);
            return jsonData;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "Error processing data";
        }
    }

    /**
     * Handles GET request to retrieve equipment details by systemID as a query parameter.
     *
     * @param systemID ID of the equipment to retrieve.
     * @return JSON representation of Equipment details or an error message.
     */
    @GetMapping("/equipmentIndividual")
    public String equipmentDetail(@RequestParam("systemID") Long systemID) {
        System.out.println("Received systemID: " + systemID);
        Equipment equipmentItem = equipmentService.getOneEquipment(systemID);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonData = objectMapper.writeValueAsString(equipmentItem);
            return jsonData;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "Error processing data";
        }
    }

    /**
     * Handles DELETE request to delete equipment by systemID.
     *
     * @param systemID ID of the equipment to delete.
     * @return Success message or 500 Internal Server Error on failure.
     */
    @DeleteMapping("/equipmentDelete")
    public ResponseEntity<String> equipmentDelete(@RequestParam("systemID") Long systemID) {
        try {
            equipmentService.deleteEquipmentBySystemID(systemID);

            return ResponseEntity.ok("{\"status\": \"success\", \"message\": \"Equipment deleted successfully!\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Handles PUT request to edit equipment details by systemID.
     *
     * @param systemID ID of the equipment to edit.
     * @param equipmentDTO DTO containing updated equipment details.
     * @return ResponseEntity containing updated Equipment object or 404 if not found.
     */
    @PutMapping("/equipmentEdit/{systemID}")
    public ResponseEntity<Equipment> equipmentEdit(
            @PathVariable Long systemID,
            @RequestBody EquipmentDTO equipmentDTO) {
        try {
            Equipment existingEquipment = equipmentService.getEquipmentDetailsById(systemID);

            if (existingEquipment != null) {
                // Updating values
                existingEquipment.setDescription(equipmentDTO.getDescription());
                existingEquipment.setBrand(equipmentDTO.getBrand());
                existingEquipment.setBuilding(Integer.valueOf(equipmentDTO.getBuilding()));
                existingEquipment.setImageURL(equipmentDTO.getImageURL());
                existingEquipment.setModel(equipmentDTO.getModel());
                existingEquipment.setNormalPosition(equipmentDTO.getNormalPosition());
                existingEquipment.setNotes(equipmentDTO.getNotes());
                existingEquipment.setPhase1or3(equipmentDTO.getPhase1or3());
                existingEquipment.setPickupAllowed(equipmentDTO.getPickupAllowed());
                existingEquipment.setPowerRating(equipmentDTO.getPowerRating());
                existingEquipment.setRoom(equipmentDTO.getRoom());
                existingEquipment.setSerialNumber(equipmentDTO.getSerialNumber());
                existingEquipment.setUoaID(equipmentDTO.getUoaID());
                existingEquipment.setLastEditTime(equipmentDTO.getLastEditTime());
                existingEquipment.setSafetyRequirements(equipmentDTO.getSafetyRequirements());
                existingEquipment.setOperatingRequirements(equipmentDTO.getOperatingRequirements());

                // Handle null values for integer fields
                if (equipmentDTO.getDepthMM() != null) {
                    existingEquipment.setDepthMM(Integer.valueOf(equipmentDTO.getDepthMM()));
                }
                if (equipmentDTO.getHeightMM() != null) {
                    existingEquipment.setHeightMM(Integer.valueOf(equipmentDTO.getHeightMM()));
                }
                if (equipmentDTO.getWidthMM() != null) {
                    existingEquipment.setWidthMM(Integer.valueOf(equipmentDTO.getWidthMM()));
                }
                if (equipmentDTO.getMaxUseDay() != null) {
                    existingEquipment.setMaxUseDay(Integer.valueOf(equipmentDTO.getMaxUseDay()));
                }

                // Save to database
                equipmentService.updateEquipment(existingEquipment);

                return ResponseEntity.ok(existingEquipment);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Handles POST request to add new equipment.
     *
     * @param newEquipment Equipment object containing details of the new equipment.
     * @return Success message with redirect URL or 500 Internal Server Error on failure.
     */
    @PostMapping("/equipmentAdd")
    public ResponseEntity<String> equipmentAdd(@RequestBody Equipment newEquipment) {
        Long systemID = equipmentService.addNewEquipment(newEquipment);

        // Redirect to equipmentDetail for the specific systemID
        String redirectUrl = "http://localhost:8080/equipmentDetail?sysID=" + systemID;

        return ResponseEntity.ok("{\"status\": \"success\", \"message\": \"Equipment added successfully!\", " +
                "\"redirectUrl\": \"" + redirectUrl + "\"}");
    }

}   




  