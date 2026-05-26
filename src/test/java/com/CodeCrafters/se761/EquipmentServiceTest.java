package com.CodeCrafters.se761;

import com.CodeCrafters.se761.equipment.Equipment;
import com.CodeCrafters.se761.equipment.EquipmentRepository;
import com.CodeCrafters.se761.equipment.EquipmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This is a JUnit test class for EquipmentService, which tests the requests sent to the
 * EquipmentController class.
 * Author: Jonathon Lee
 */
public class EquipmentServiceTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @InjectMocks
    private EquipmentService equipmentService;

    private final Equipment EQUIPMENT_1 = new Equipment(
            1L,
            "Oscilloscope",
            "Google",
            "3",
            "fyan123",
            "UOA4985927",
            1,
            "303",
            "This item is cool.",
            "On desk",
            100,
            50,
            200,
            "400W",
            "Phase 1",
            Timestamp.valueOf("2023-10-11 12:00:00"),
            Timestamp.valueOf("2023-10-11 12:00:00"),
            "https://example.com/sample-image.jpg",
            Timestamp.valueOf("2023-10-20 12:00:00"),
            60,
            true,
            "Fung Yang",
            "Safety Requirements",
            "Operating Requirements"
    );
    private final Equipment EQUIPMENT_2 = new Equipment(
            1L,
            "Watch",
            "Google",
            "3",
            "fyan123",
            "UOA4985927",
            1,
            "303",
            "This item is cool.",
            "On desk",
            100,
            50,
            200,
            "400W",
            "Phase 1",
            Timestamp.valueOf("2023-10-11 12:00:00"),
            Timestamp.valueOf("2023-10-11 12:00:00"),
            "https://example.com/sample-image.jpg",
            Timestamp.valueOf("2023-10-20 12:00:00"),
            60,
            true,
            "Fung Yang",
            "Safety Requirements",
            "Operating Requirements"
    );
    private final Equipment EQUIPMENT_3 = new Equipment(
            1L,
            "Robot",
            "Google",
            "3",
            "fyan123",
            "UOA4985927",
            1,
            "303",
            "This item is cool.",
            "On desk",
            100,
            50,
            200,
            "400W",
            "Phase 1",
            Timestamp.valueOf("2023-10-11 12:00:00"),
            Timestamp.valueOf("2023-10-11 12:00:00"),
            "https://example.com/sample-image.jpg",
            Timestamp.valueOf("2023-10-20 12:00:00"),
            60,
            true,
            "Fung Yang",
            "Safety Requirements",
            "Operating Requirements"
    );

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getEquipment_success() {
        Equipment equipment1 = EQUIPMENT_1;
        equipment1.setSystemID(1L);
        Equipment equipment2 = EQUIPMENT_2;
        equipment2.setSystemID(2L);
        List<Equipment> equipmentList = Arrays.asList(equipment1, equipment2);

        Mockito.when(equipmentRepository.findAll(Mockito.any(Sort.class))).thenReturn(equipmentList);
        List<Equipment> result = equipmentService.getEquipment();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getSystemID());
        assertEquals(2L, result.get(1).getSystemID());

        Mockito.verify(equipmentRepository, Mockito.times(1)).findAll(Mockito.any(Sort.class));
    }

    @Test
    public void getEquipmentDetailsById_successWhenExists() {
        Long equipmentId = 1L;
        Equipment expectedEquipment = EQUIPMENT_1;
        expectedEquipment.setSystemID(equipmentId);
        Mockito.when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(expectedEquipment));

        Equipment result = equipmentService.getEquipmentDetailsById(equipmentId);

        assertEquals(expectedEquipment, result);
    }

    @Test
    public void getEquipmentDetailsById_failureWhenNotExists() {
        Long equipmentId = 1L;
        Mockito.when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.empty());

        Equipment result = equipmentService.getEquipmentDetailsById(equipmentId);

        assertEquals(null, result);
    }

    @Test
    void getOneEquipment_success() {
        Long systemID = 1L;
        Equipment expectedEquipment = EQUIPMENT_1;
        Mockito.when(equipmentRepository.findOneBySystemID(systemID)).thenReturn(expectedEquipment);

        Equipment result = equipmentService.getOneEquipment(systemID);

        Mockito.verify(equipmentRepository, Mockito.times(1)).findOneBySystemID(systemID);
        assertEquals(expectedEquipment, result);
    }

    @Test
    void updateEquipment_success() {
        Equipment existingEquipment = EQUIPMENT_1;

        equipmentService.updateEquipment(existingEquipment);

        Mockito.verify(equipmentRepository, Mockito.times(1)).save(existingEquipment);
    }

    @Test
    void deleteEquipmentBySystemID_successWhenEquipmentExists() {
        Long systemID = 1L;
        Equipment equipment = EQUIPMENT_1;
        Mockito.when(equipmentRepository.findOneBySystemID(systemID)).thenReturn(equipment);

        equipmentService.deleteEquipmentBySystemID(systemID);

        Mockito.verify(equipmentRepository, Mockito.times(1)).delete(equipment);
    }

    @Test
    void deleteEquipmentBySystemID_failureWhenEquipmentDoesNotExist() {
        Long systemID = 1L;
        Mockito.when(equipmentRepository.findOneBySystemID(systemID)).thenReturn(null);

        equipmentService.deleteEquipmentBySystemID(systemID);

        Mockito.verify(equipmentRepository, Mockito.never()).delete(Mockito.any());
    }

    /* Should save and return systemID */
    @Test
    void addNewEquipment_successWhenUoaIDIsUnique() {
        Equipment newEquipment = EQUIPMENT_1;
        newEquipment.setUoaID("uniqueUoaID");

        Mockito.when(equipmentRepository.findEquipmentByUoaID("uniqueUoaID")).thenReturn(Optional.empty());

        Equipment savedEquipment = EQUIPMENT_1;
        savedEquipment.setSystemID(1L);
        Mockito.when(equipmentRepository.save(newEquipment)).thenReturn(savedEquipment);

        Long result = equipmentService.addNewEquipment(newEquipment);

        assertEquals(1L, result);
        Mockito.verify(equipmentRepository, Mockito.times(1)).findEquipmentByUoaID("uniqueUoaID");
        Mockito.verify(equipmentRepository, Mockito.times(1)).save(newEquipment);
    }

    // Show throw exception
    @Test
    void addNewEquipment_failureWhenUoaIDAlreadyExists() {
        Equipment newEquipment = EQUIPMENT_1;
        newEquipment.setUoaID("existingUoaID");

        Mockito.when(equipmentRepository.findEquipmentByUoaID("existingUoaID"))
                .thenReturn(Optional.of(new Equipment()));

        assertThrows(IllegalStateException.class, () -> {
            equipmentService.addNewEquipment(newEquipment);
        });

        Mockito.verify(equipmentRepository, Mockito.times(1)).findEquipmentByUoaID("existingUoaID");
        Mockito.verify(equipmentRepository, Mockito.never()).save(Mockito.any());
    }



}