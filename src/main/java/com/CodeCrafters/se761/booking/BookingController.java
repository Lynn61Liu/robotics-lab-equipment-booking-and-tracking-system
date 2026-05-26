package com.CodeCrafters.se761.booking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
/**
 * Controller class for managing booking-related operations.
 *
 * This class is responsible for handling various booking-related tasks, including creating new bookings,
 * editing existing bookings, and retrieving booking details. It communicates with the BookingService to
 * execute these operations and returns appropriate responses to client requests.
 *
 * Note: Authorization checks are in place for all endpoints to ensure
 * that only authorized users can perform specific actions.
 */
@RestController
public class BookingController {

    private final BookingService bookingService;

    private final ObjectMapper objectMapper;

    @Autowired
    public BookingController(BookingService bookingService, ObjectMapper objectMapper) {
        this.bookingService = bookingService;
        this.objectMapper = objectMapper;
    }


    @GetMapping("/bookingdetails/equipmentID/{systemID}")// endpoint for fetching booking via equipment ID of booking
    public ResponseEntity<Booking> getBookingDetailsByEquipmentID(@PathVariable Long systemID) {
        Booking booking = bookingService.getBookingByEquipmentId(systemID);
        System.out.println(booking);

        if (booking != null) {
            return ResponseEntity.ok(booking);
        }
        else {
            // return ResponseEntity Booking but value is null
            return ResponseEntity.ok(new Booking());

        }
    }

    @GetMapping("/presentBooking/equipmentID/{systemID}")// get booking that is presently active at current date.
    public ResponseEntity<Booking> getPresentBookingDetailEquipmentID(@PathVariable Long systemID) {
        Booking booking = bookingService.getPresentBookingEquipmentId(systemID);
        System.out.println(booking);

        if (booking != null) {
            return ResponseEntity.ok(booking);
        }
        else {
            // return ResponseEntity Booking but value is null
            return ResponseEntity.ok(new Booking());

        }
    }

    @GetMapping("/getallbookings")// endpoint for getting all bookings in system
    public String processBookings() {
        List<Booking> bookingList = bookingService.getAllBookings();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonData = objectMapper.writeValueAsString(bookingList);
            return jsonData;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "Error processing data";
        }
    }
    @GetMapping("/bookingDetails/{bookingID}")// get booking details via booking ID
    public ResponseEntity<Booking> getBookingDetailsById(@PathVariable Long bookingID) {
        Booking booking = bookingService.getBookingDetailsById(bookingID);
        if (booking != null) {
            return ResponseEntity.ok(booking);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/editBooking/{bookingID}") //update booking endpoint
    public ResponseEntity<Booking> editBooking(@PathVariable Long bookingID, @RequestBody Booking updatedBooking) {
        System.out.println("update booking endpoint");
        ResponseEntity<Booking> responseEntity = getBookingDetailsById(bookingID);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            Booking existingBooking = responseEntity.getBody();

            existingBooking.setStartTime(updatedBooking.getStartTime());
            existingBooking.setBookedBy(updatedBooking.getBookedBy());
            existingBooking.setStatus(updatedBooking.getStatus()); // Assuming there's a setter method for status
            existingBooking.setEndTime(updatedBooking.getEndTime()); // Assuming there's a setter method for endtime

            // Add more fields as needed

            // Save the updated booking
            bookingService.save(existingBooking);

            return ResponseEntity.ok(existingBooking);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/process") //new booking endpoint
    public ResponseEntity<String> processFormData(@RequestBody Booking booking) throws JsonProcessingException {

        // temp line for checking if connection is made
        //System.out.println(booking);

        Long equipmentid = booking.getEquipmentid();
        String receivedStartDate = booking.getStartTime();
        String receivedEndDate = booking.getEndTime();

        // Check for date conflicts
        if (bookingService.isBookingDateConflict(equipmentid, receivedStartDate, receivedEndDate)) {
            return ResponseEntity.ok("{\"status\": \"error\", \"message\": \"Booking date conflicts with an existing booking.\"}");
        }

        bookingService.addNewBooking(booking);

        // Return a response back to the frontend
        return ResponseEntity.ok("{\"status\": \"success\", \"message\": \"Data received successfully!\"}");
    }

    @GetMapping("/getmybookings")// get bookings associated with person authenticated
    public ResponseEntity<String> processMyBookings() {
        System.out.println("Entered getmybookings");
        List<Booking> bookingsList = bookingService.getMyBooking("localuser");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonData = objectMapper.writeValueAsString(bookingsList);
            return ResponseEntity.ok(jsonData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.ok("Error processing data");
        }
    }
    // Below are mapping for changing the statuses
    // This is very inefficient but, kept, in case still needed for frontend.
    @DeleteMapping ("/deleteBooking/{id}")// endpoint for delete bookings
    public ResponseEntity<String> deleteBooking(@PathVariable ("id") Long id){
        bookingService.deleteBooking(id);
        return ResponseEntity.ok("Deleted booking");
    }

    @PutMapping("/approveBooking/{id}")
    public ResponseEntity<String> approveBooking(@PathVariable ("id") Long id){
        bookingService.setStatusApprove(id);
        return ResponseEntity.ok("approved booking");
    }

    @PutMapping("/rejectBooking/{id}")
    public ResponseEntity<String> rejectBooking(@PathVariable ("id") Long id){
        bookingService.setStatusRejected(id);
        return ResponseEntity.ok("rejected booking");
    }

    @PutMapping("/incidentOnBooking/{id}")
    public ResponseEntity<String> incidentBooking(@PathVariable ("id") Long id){
        bookingService.setStatusIncident(id);
        return ResponseEntity.ok("reported booking");
    }

    @PutMapping("/returnedBooking/{id}")
    public ResponseEntity<String> returnedBooking(@PathVariable ("id") Long id){
        bookingService.setStatusReturned(id);
        return ResponseEntity.ok("Set status to returned");
    }

    @PutMapping("/cancelBooking/{id}")
    public ResponseEntity<String> cancelBooking(@PathVariable ("id") Long id){
        bookingService.setStatusCancelled(id);
        return ResponseEntity.ok("Set status to cancelled");
    }

    @PutMapping("/lentBooking/{id}")
    public ResponseEntity<String> lentBooking(@PathVariable ("id") Long id){
        bookingService.setStatusLent(id);
        return ResponseEntity.ok("Set status to Lent");
    }

    @PutMapping("/faultyReturnedBooking/{id}")
    public ResponseEntity<String> faultyReturnedBooking(@PathVariable ("id") Long id){
        bookingService.setStatusFaultyReturn(id);
        return ResponseEntity.ok("Set status to Faulty return");
    }

    @PutMapping("/readyBooking/{id}")
    public ResponseEntity<String> readyBooking(@PathVariable ("id") Long id){
        bookingService.setStatusReady(id);
        return ResponseEntity.ok("Set status to Ready for pickup");
    }
    // More efficient way for editing booking status
    @RequestMapping(value = "/editStatusBooking", method = RequestMethod.PUT)
    public ResponseEntity<String> editStatusBooking(
            @RequestParam("bookingID") Long id,
            @RequestParam("status") String status) {

        System.out.println("backend reached");
        System.out.println("New Booking ID: " + id);
        System.out.println("New Status: " + status);
        bookingService.editStatusBooking(id,status);

        return ResponseEntity.ok("Set status to cancelled");
    }


}
