package com.CodeCrafters.se761.booking;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.*;

import com.CodeCrafters.se761.notification.NotificationService;

/**
 * The `BookingService` class provides essential business logic and services for managing booking operations.
 * It acts as an intermediary between the controller layer and the data access layer, handling booking-related
 * tasks such as creating, retrieving, updating, and deleting bookings.
 *
 * @author Multiple developers
 * @since 07-Sep-2023
 */
@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }
    /**
     * Checks if there's a date conflict with a new booking request for a given equipment.
     *
     * @param equipmentId   The ID of the equipment.
     * @param newStartDate  The start date of the new booking.
     * @param newEndDate    The end date of the new booking.
     * @return True if a date conflict exists; false otherwise.
     */
    public boolean isBookingDateConflict(Long equipmentId, String newStartDate, String newEndDate) {
        // Retrieve existing bookings for the same equipment
        List<Booking> existingBookings = bookingRepository.listFindByEquipmentid(equipmentId);
        // Create a SimpleDateFormat for your date format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            // Parse the new start and end dates
            Date parsedNewStartDate = sdf.parse(newStartDate);
            Date parsedNewEndDate = sdf.parse(newEndDate);

            // Check for date conflicts
            for (Booking existingBooking : existingBookings) {
                String existingStartTime = existingBooking.getStartTime();
                String existingEndTime = existingBooking.getEndTime();
                String status = existingBooking.getStatus();

                // Check if the status is "Returned" or "Cancelled" and ignore conflicts
                if ("Returned".equals(status) || "Cancelled".equals(status) || "Faulty Return".equals(status)) {
                    continue;
                }

                // Parse existing date strings
                Date parsedExistingStartTime = sdf.parse(existingStartTime);
                Date parsedExistingEndTime = sdf.parse(existingEndTime);

                // Check for date conflicts
                if (parsedNewEndDate.before(parsedExistingStartTime) || parsedNewStartDate.after(parsedExistingEndTime)) {
                    // No conflict found
                    continue;
                } else {
                    // Date conflict found
                    return true;
                }
            }
        } catch (ParseException e) {
            // Handle parsing errors if needed
            e.printStackTrace();
        }

        // No conflicts found
        return false;
    }
    /**
     * Retrieves the booking associated with a specific equipment.
     *
     * @param equipmentId The ID of the equipment.
     * @return The associated booking or null if not found.
     */
    public Booking getBookingByEquipmentId(Long equipmentId) {
        return bookingRepository.findByEquipmentid(equipmentId).orElse(null);
    }
    /**
     * Retrieves the current booking for a specific equipment on the current date.
     *
     * @param equipmentId The ID of the equipment.
     * @return The current booking or null if none exists.
     */
    public Booking getPresentBookingEquipmentId(Long equipmentId) {
        java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
        List<Booking> bookingList = bookingRepository.listFindByEquipmentid(equipmentId);

        // Create a SimpleDateFormat for date format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            for (Booking booking : bookingList) {
                String startTime = booking.getStartTime();
                String endTime = booking.getEndTime();

                // Parse the booking's start and end times as dates
                Date startDate = sdf.parse(startTime);
                Date endDate = sdf.parse(endTime);

                // Check if both start and end times fall within the current date
                if (currentDate.equals(startDate) || currentDate.equals(endDate) ||
                        (currentDate.after(startDate) && currentDate.before(endDate))) {
                    return booking; // Return the first matching booking
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null; // Return null if no matching booking is found
    }
    /**
     * Adds a new booking to the database.
     *
     * @param booking The booking to be added.
     */
    public void addNewBooking(Booking booking) {
        bookingRepository.save(booking);
    }
    /**
     * Retrieves all bookings in the system.
     *
     * @return A list of all bookings.
     */
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
    /**
     * Retrieves booking details by its ID.
     *
     * @param bookingId The ID of the booking to retrieve.
     * @return The booking details or null if not found.
     */
    public Booking getBookingDetailsById(Long bookingId) {
        return bookingRepository.findById(bookingId).orElse(null);
    }
    /**
     * Saves changes to an existing booking.
     *
     * @param booking The modified booking to be saved.
     */
    public void save(Booking booking) {
        bookingRepository.save(booking);
    }

    public List<Booking> getMyBooking(String upi) {
        return bookingRepository.findByUPI(upi);
    }
    /**
     * Retrieves bookings associated with a specific user's UPI.
     *
     * @param upi The UPI of the user.
     * @return A list of bookings associated with the user.
     */
    @Autowired
    private NotificationService notificationService;
    /**
     * Deletes a booking by its ID, if it exists.
     *
     * @param id The ID of the booking to be deleted.
     */
    public void deleteBooking(Long id) {
        boolean exists = bookingRepository.existsById(id);
        if (!exists) {
            throw new IllegalStateException("Booking with id " + id + " does not exist");
        }
        bookingRepository.deleteById(id);
    }

    @Transactional
    public void setStatusApprove(long id) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);

        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            booking.setStatus("Approved");

            // Calculate the reminder date (2 days before the booking date)
            LocalDate startTimeAsDate = LocalDate.parse(booking.getStartTime());
            LocalDate endTimeAsDate = LocalDate.parse(booking.getEndTime());

            // Calculate the reminder date (2 days before the booking date)
            LocalDate reminderStartDate = startTimeAsDate.minusDays(2);
            LocalDate reminderEndDate = endTimeAsDate.minusDays(2);

            booking.setReminderStartDate(reminderStartDate.toString());
            booking.setReminderEndDate(reminderEndDate.toString());

            bookingRepository.save(booking);
            notificationService.sendBookingConfirmationEmail(booking.getBookedBy(), "Booking Status", booking.getStartTime(), booking.getStatus());
        } else {
            throw new IllegalStateException("Booking with id " + id + " does not exist");
        }
    }
    /**
     * the following functions are for changing the status of bookings
     * @param id The id of the booking
     */
    @Transactional
    public void setStatusRejected(long id) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);

        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            booking.setStatus("Rejected");
            bookingRepository.save(booking);
            // Send the rejection email
            notificationService.sendBookingConfirmationEmail(booking.getBookedBy(), "Booking Status", booking.getStartTime(), booking.getStatus());
        } else {
            throw new IllegalStateException("Booking with id " + id + " does not exist");
        }
    }

    @Transactional
    public void setStatusIncident(long id) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);

        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            booking.setStatus("Incident");
            bookingRepository.save(booking);
        } else {
            throw new IllegalStateException("Booking with id " + id + " does not exist");
        }
    }

    @Transactional
    public void setStatusReturned(long id) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);

        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            booking.setStatus("Safely Return");
            bookingRepository.save(booking);
        } else {
            throw new IllegalStateException("Booking with id " + id + " does not exist");
        }

    }

    @Transactional
    public void setStatusCancelled(long id) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);

        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            booking.setStatus("Cancelled");
            bookingRepository.save(booking);
            notificationService.sendBookingConfirmationEmail(booking.getBookedBy(), "Booking Status", booking.getStartTime(), booking.getStatus());
        } else {
            throw new IllegalStateException("Booking with id " + id + " does not exist");
        }
    }

    @Transactional
    public void setStatusLent(long id) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);

        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            booking.setStatus("Lent");
        } else {
            throw new IllegalStateException("Booking with id " + id + " does not exist");
        }
    }

    @Transactional
    public void setStatusFaultyReturn(long id) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);

        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            booking.setStatus("Faulty Return");
        } else {
            throw new IllegalStateException("Booking with id " + id + " does not exist");
        }
    }

    @Transactional
    public void setStatusReady(Long id) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);

        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            booking.setStatus("Waiting For Pickup");
        } else {
            throw new IllegalStateException("Booking with id " + id + " does not exist");
        }
    }
    /**
     * function to change booking status, with parameter to hold status string
     * @param id id of the booking
     * @param status string that is the status of the booking
     */
    public void editStatusBooking(Long id, String status) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            System.out.println("Old Status: " + booking.getStatus());
            booking.setStatus(status);
            bookingRepository.save(booking);
            System.out.println("Updated Status: " + booking.getStatus());

        } else {
            throw new IllegalStateException("Booking with id " + id + " does not exist");
        }
    }
    /**
     * Sends notifications to the student for bookings due in two days.
     */
    @Scheduled(cron = "0 0 0 * * ?") // Run daily at midnight
    public void sendBookingNotificationsDueTwoDays() {
        LocalDate currentDate = LocalDate.now();

        List<Booking> bookingsToNotify = bookingRepository.findByStartTimeAndStatusTwoDays(
                currentDate.toString(),
                "Approved"
        );

        for (Booking booking : bookingsToNotify) {
            // Send a reminder email to the user for the approved booking
            notificationService.sendBookingConfirmationEmail(
                    booking.getBookedBy(),
                    "Booking Reminder",
                    booking.getStartTime(),
                    booking.getStatus()
            );
        }
    }
    /**
     * Sends notifications to the admin (Fung Yang) for bookings due in two days.
     */
    @Scheduled(cron = "0 0 0 * * ?") // Run daily at midnight
    public void sendEquipmentNotificationsDueTwoDays() {
        LocalDate currentDate = LocalDate.now();
        LocalDate twoDaysFromNow = currentDate.plusDays(2);

        List<Booking> bookingsToNotify = bookingRepository.findByEndTimeAndStatusTwoDays(
                currentDate.toString(),
                "Approved"
        );

        for (Booking booking : bookingsToNotify) {
            String equipmentId = String.valueOf(booking.getEquipmentid()); // Use the new method to get equipment ID

            if (equipmentId != null) {
                // Send an equipment reminder email
                notificationService.sendEquipmentDueEmail(
                        "fung.yang",
                        "Equipment Due Soon",
                        equipmentId,
                        booking.getEndTime()
                );
            }
        }
    }


    @Scheduled(cron = "0 0 0 * * ?") // Run daily at midnight
    public void sendBookingAndEquipmentNotificationsDueToday() {
        LocalDate currentDate = LocalDate.now();

        List<Booking> bookingsToNotify = bookingRepository.findByEndTimeAndStatusToday(
                currentDate.toString(),
                "Approved"
        );

        for (Booking booking : bookingsToNotify) {
            String equipmentId = String.valueOf(booking.getEquipmentid());

            if (equipmentId != null) {
                // Send an equipment reminder email
                notificationService.sendEquipmentDueEmail(
                        "fung.yang",
                        "Equipment Due Today",
                        equipmentId,
                        booking.getEndTime()
                );
            }
        }
    }

}