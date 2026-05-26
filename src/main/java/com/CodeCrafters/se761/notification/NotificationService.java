package com.CodeCrafters.se761.notification;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${app.mail.from-address:no-reply@example.com}")
    private String fromAddress;

    /**
     * Sends a booking confirmation email.
     *
     * @param recipientUPI The recipient's UPI.
     * @param subject The subject of the email.
     * @param date The date of the booking.
     * @param bookingStatus The status of the booking.
     */
    public void sendBookingConfirmationEmail(String recipientUPI, String subject, String date, String bookingStatus) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(recipientUPI + "@aucklanduni.ac.nz");
        message.setText("Your booking for " + date + " is " + bookingStatus + ".");
        message.setSubject(subject);

        javaMailSender.send(message);
    }

    /**
     * Sends an equipment due email.
     *
     * @param recipientUPI The recipient's UPI.
     * @param subject The subject of the email.
     * @param equipmentID The ID of the equipment.
     * @param dueTime The due time of the equipment.
     */
    public void sendEquipmentDueEmail(String recipientUPI, String subject, String equipmentID, String dueTime) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(recipientUPI + "@auckland.ac.nz");
        message.setText("The equipment " + equipmentID + " is due at " + dueTime + ".");
        message.setSubject(subject);

        javaMailSender.send(message);
    }
}
