package com.CodeCrafters.se761.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class NotificationController {

    @Autowired
    private NotificationService notificationService;


    /**
     * Handles a POST request to send booking confirmation email.
     *
     * @param jsonString The JSON string containing recipient, subject, date, and booking status.
     * @throws Exception
     */
    @PostMapping("/send-booking-confirmation")
    public void sendBookingConfirmationEmail(@RequestBody String jsonString) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonString);

            String recipientUPI = jsonNode.get("recipientUPI").asText();
            String subject = jsonNode.get("subject").asText();
            String date = jsonNode.get("date").asText();
            String bookingStatus = jsonNode.get("bookingStatus").asText();

            notificationService.sendBookingConfirmationEmail(recipientUPI, subject, date, bookingStatus);
        } catch (Exception e) {

        }
    }

    /**
     * Handles a POST request to send equipment due email.
     *
     * @param jsonString The JSON string containing recipient, subject, equipment ID, and due time.
     * @throws Exception
     */
    @PostMapping("/send-equipment-due")
    public void sendEquipmentDueEmail(@RequestBody String jsonString) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonString);

            String recipientUPI = jsonNode.get("recipientUPI").asText();
            String subject = jsonNode.get("subject").asText();
            String equipmentID = jsonNode.get("equipmentID").asText();
            String dueTime = jsonNode.get("dueTime").asText();

            notificationService.sendEquipmentDueEmail(recipientUPI, subject, equipmentID, dueTime);
        } catch (Exception e) {

        }
    }


}
