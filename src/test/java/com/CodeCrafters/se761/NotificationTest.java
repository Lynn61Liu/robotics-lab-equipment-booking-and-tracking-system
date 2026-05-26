package com.CodeCrafters.se761;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.CodeCrafters.se761.notification.NotificationController;
import com.CodeCrafters.se761.notification.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class NotificationTest {

    @Autowired
    private MockMvc mockMvc;
    @Mock
    private NotificationService notificationService;


    @InjectMocks
    private NotificationController notificationController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();

    }

    @Test
    public void sendBookingConfirmationEmail_success() throws Exception {

        String jsonString = "{\"recipientUPI\":\"test@domain.com\",\"subject\":\"Test Subject\",\"date\":\"2023-10-15\",\"bookingStatus\":\"Confirmed\"}";


        mockMvc.perform(MockMvcRequestBuilders
                        .post("/send-booking-confirmation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().isOk());


        verify(notificationService).sendBookingConfirmationEmail("test@domain.com", "Test Subject", "2023-10-15", "Confirmed");
    }


    @Test
    public void sendEquipmentDueEmail_success() throws Exception {
        String jsonString = "{\"recipientUPI\":\"test@domain.com\",\"subject\":\"Test Subject\",\"equipmentID\":\"E001\",\"dueTime\":\"2023-10-16 12:00\"}";

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/send-equipment-due")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().isOk());

        verify(notificationService).sendEquipmentDueEmail("test@domain.com", "Test Subject", "E001", "2023-10-16 12:00");
    }


}
