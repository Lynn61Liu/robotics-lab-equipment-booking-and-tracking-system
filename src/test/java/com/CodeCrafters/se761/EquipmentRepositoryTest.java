package com.CodeCrafters.se761;

import com.CodeCrafters.se761.equipment.Equipment;
import com.CodeCrafters.se761.equipment.EquipmentRepository;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This is a JUnit test class for EquipmentRepository, which tests the requests sent to the
 * EquipmentService class.
 * Author: Jonathon Lee
 */
@RunWith(MockitoJUnitRunner.class)
public class EquipmentRepositoryTest {

    @Mock
    private EquipmentRepository equipmentRepository;

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

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void findOneBySystemID_success() {
        Long systemID = 1L;
        Equipment expectedEquipment = EQUIPMENT_1;
        expectedEquipment.setSystemID(systemID);

        Mockito.when(equipmentRepository.findOneBySystemID(systemID)).thenReturn(expectedEquipment);

        Equipment result = equipmentRepository.findOneBySystemID(systemID);

        assertEquals(expectedEquipment, result);
    }

    @Test
    public void findEquipmentByUoaID_success() {
        String uoaID = "UOA123";
        Equipment expectedEquipment = EQUIPMENT_1;
        expectedEquipment.setUoaID(uoaID);

        Mockito.when(equipmentRepository.findEquipmentByUoaID(uoaID)).thenReturn(Optional.of(expectedEquipment));

        Optional<Equipment> result = equipmentRepository.findEquipmentByUoaID(uoaID);

        assertEquals(expectedEquipment, result.orElse(null));
    }

}
