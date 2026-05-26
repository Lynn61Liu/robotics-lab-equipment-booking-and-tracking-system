package com.CodeCrafters.se761;

import com.CodeCrafters.se761.booking.*;
import com.CodeCrafters.se761.equipment.Equipment;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.sql.Date;


import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * This class contains unit tests for the BookingController, which is responsible for handling HTTP requests related to bookings.
 * It uses the Spring MVC framework and Mockito for mocking dependencies.
 * Author: Tony Huynh
 */
public class BookingControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ObjectWriter objectWriter = objectMapper.writer();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private final Booking BOOKING_1 = new Booking(

            1L,
            "2023-10-06",
            "2023-10-23",
            "thuy744",
            "Requested",
            45L,
            java.sql.Date.valueOf("2023-10-04"),
            "You're weird",
            java.sql.Date.valueOf("2023-10-04")
    );

    private final Booking BOOKING_2 = new Booking(

            2L,
            "2023-11-20",
            "2023-11-24",
            "thuy744",
            "Requested",
            46L,
            java.sql.Date.valueOf("2023-10-04"),
            "Null2",
            java.sql.Date.valueOf("2023-10-04")
    );

    private final Booking BOOKING_3 = new Booking(

            3L,
            "2023-09-29",
            "2023-10-05",
            "thuy744",
            "Requested",
            48L,
            java.sql.Date.valueOf("2023-10-24"),
            "Null",
            java.sql.Date.valueOf("2023-10-24")
    );

    private final Booking BOOKING_4 = new Booking(

            4L,
            "2023-09-28",
            "2023-10-22",
            "yilu747",
            "Approved",
            48L,
            java.sql.Date.valueOf("2023-09-24"),
            "Null",
            java.sql.Date.valueOf("2023-09-24")
    );

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
    }

    @Test
    public void get_all_bookings_success() throws Exception {
        List<Booking> bookingList = new ArrayList<>(Arrays.asList(BOOKING_1, BOOKING_2, BOOKING_3));

        Mockito.when(bookingService.getAllBookings()).thenReturn(bookingList);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/getallbookings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[1].rejection_reason", is("Null2")));
    }

    @Test
    public void bookingDetail_test() throws Exception {
        Mockito.when(bookingService.getBookingDetailsById(BOOKING_1.getId())).thenReturn(BOOKING_1);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/bookingDetails/{bookingID}", BOOKING_1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    public void bookingDelete_test() throws Exception {
        Mockito.when(bookingRepository.findByEquipmentid(BOOKING_3.getId()))
                .thenReturn(Optional.of(BOOKING_3));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/deleteBooking/{id}", BOOKING_3.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void changeBooking_status_returned() throws Exception {
        Mockito.when(bookingRepository.findByEquipmentid(BOOKING_4.getId()))
                .thenReturn(Optional.of(BOOKING_4));

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/returnedBooking/{id}", BOOKING_3.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Set status to returned"));
    }

    @Test
    public void bookingAdd_success() throws Exception {
        long expectedBookingID = 0;

        Booking new_booking = new Booking();
        new_booking.setId(expectedBookingID);
        new_booking.setStartTime("2023-09-12");
        new_booking.setEndTime("2023-09-25");
        new_booking.setStatus("Requested");
        new_booking.setBookedBy("thuy744");
        new_booking.setEquipmentid(51L);

        Mockito.doNothing().when(bookingService).addNewBooking(new_booking);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new_booking)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"success\",\"message\":\"Data received successfully!\"}")); // Check the response content

    }

    @Test
    public void booking_conflict_test() throws Exception {
        Mockito.when((bookingService.isBookingDateConflict(BOOKING_4.getEquipmentid(), BOOKING_4.getStartTime(), BOOKING_4.getEndTime())))
                .thenReturn(true);


    // Perform the HTTP request with the second Booking instance
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(BOOKING_4)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\": \"error\", \"message\": \"Booking date conflicts with an existing booking.\"}"));
    }

    @Test
    public void booking_edit_test() throws Exception {
        Booking edit_booking = new Booking();
        edit_booking.setId(4L);
        edit_booking.setStartTime("2023-09-28");
        edit_booking.setEndTime("2023-10-22");
        edit_booking.setStatus("Approved");
        edit_booking.setBookedBy("thuy744");
        edit_booking.setEquipmentid(48L);

        Mockito.when(bookingService.getBookingDetailsById(BOOKING_4.getId()))
                .thenReturn(BOOKING_4);

        mockMvc.perform(MockMvcRequestBuilders
                .put("/editBooking/{bookingID}", BOOKING_4.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(edit_booking)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookedBy", is("thuy744")));


    }


}
