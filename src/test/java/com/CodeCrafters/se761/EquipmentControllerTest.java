package com.CodeCrafters.se761;

import com.CodeCrafters.se761.equipment.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * This is a JUnit test class for EquipmentController, which tests the HTTP requests send
 * to/from the frontend.
 * Author: Jonathon Lee
 */
public class EquipmentControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private EquipmentService equipmentService;

    @InjectMocks
    private EquipmentController equipmentController;

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
        this.mockMvc = MockMvcBuilders.standaloneSetup(equipmentController).build();
    }

    @Test
    public void processEquipment_success() throws Exception {
        List<Equipment> equipmentList = new ArrayList<>(Arrays.asList(EQUIPMENT_1, EQUIPMENT_2, EQUIPMENT_3));

        Mockito.when(equipmentService.getEquipment()).thenReturn(equipmentList);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/getall")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[2].description", is("Robot")));
    }

    @Test
    public void equipmentDetail_success() throws Exception {
        Mockito.when(equipmentService.getOneEquipment(EQUIPMENT_1.getSystemID())).thenReturn(EQUIPMENT_1);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/equipmentIndividual")
                        .param("systemID", String.valueOf(EQUIPMENT_1.getSystemID()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is("Oscilloscope")));
    }

    @Test
    public void getNewBookingPageWithEquipmentDetails_success() throws Exception {
        Mockito.when(equipmentService.getEquipmentDetailsById(EQUIPMENT_1.getSystemID())).thenReturn(EQUIPMENT_1);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/equipment/{systemID}", EQUIPMENT_1.getSystemID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.description", is("Oscilloscope")));
    }

    @Test
    public void equipmentDelete_success() throws Exception {
        Mockito.when(equipmentRepository.findOneBySystemID(EQUIPMENT_1.getSystemID())).thenReturn(EQUIPMENT_1);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/equipmentDelete")
                        .param("systemID", String.valueOf(EQUIPMENT_1.getSystemID()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void equipmentEdit_success() throws Exception {
        EquipmentDTO equipmentDTO = new EquipmentDTO();
        equipmentDTO.setDescription("Updated Description");
        equipmentDTO.setBuilding("303");
        equipmentDTO.setDepthMM("50");
        equipmentDTO.setHeightMM("100");
        equipmentDTO.setWidthMM("150");
        equipmentDTO.setMaxUseDay("30");

        Mockito.when(equipmentService.getEquipmentDetailsById(1L)).thenReturn(EQUIPMENT_1);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/equipmentEdit/{systemID}", EQUIPMENT_1.getSystemID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(equipmentDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is("Updated Description")));
    }

    @Test
    public void equipmentAdd_success() throws Exception {
        // The systemID must be manually set here to 0 since the test cannot access the database
        // where the systemID is generally set
        long expectedSystemID = 0;

        Equipment newEquipment = new Equipment();
        newEquipment.setSystemID(expectedSystemID);
        newEquipment.setDescription("Test Equipment");
        newEquipment.setDepthMM(50);
        newEquipment.setHeightMM(100);
        newEquipment.setWidthMM(150);
        newEquipment.setMaxUseDay(30);
        System.out.println(newEquipment.systemID);

        Mockito.when(equipmentService.addNewEquipment(newEquipment)).thenReturn(expectedSystemID);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/equipmentAdd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEquipment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Equipment added successfully!"))
                .andExpect(jsonPath("$.redirectUrl")
                        .value("http://localhost:8080/equipmentDetail?sysID=" + expectedSystemID))
                .andDo(MockMvcResultHandlers.print());
    }

}
