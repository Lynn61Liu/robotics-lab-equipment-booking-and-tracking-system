package com.CodeCrafters.se761.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class WebPageController {

    @GetMapping("/")
    public String showHomePage() {
        return "redirect:/equipmentList";
    }

    @GetMapping("/equipmentList")
    public String showEquipmentListPage() {
        return "equipmentList";
    }

    @GetMapping("/equipmentDetail")
    public String showEquipmentDetailPage(@RequestParam(name = "sysID") String sysID, Model model) {
        return "equipmentDetail";
    }

    @GetMapping("/newBooking/{systemID}")
    public String showNewBookingPage(@PathVariable Long systemID) {
        return "newBooking";
    }
    @GetMapping("/BookingCalendar/{systemID}")
    public String showBookingCalendarPage(@PathVariable Long systemID) {
        return "bookingCalendar";
    }

    @GetMapping("/incidentDetail/{BookingID}")
    public String showIncidentDetailPage(@PathVariable Long BookingID) {
        return "incidentDetail";
    }

   @GetMapping("/newIncident/{BookingID}")
    public String newIncidentPage(@PathVariable Long BookingID) {
        return "newIncident";
    }

    @GetMapping("/viewBooking/{systemID}")
    public String showBookingViewUser(@PathVariable Long systemID) {
        return "bookingViewUser";
    }

    @GetMapping("/studentBookings")
    public String showMyBookingPage() {
        return "studentBookings";
    }


    @GetMapping("/equipmentAdd")
    public String addEquipmentPage() {
        return "equipmentAdd";
    }

    @GetMapping("/allBookings")
    public  String showBookingPage() {
        return "allBookings";
    }

    @GetMapping("/bookingView")
    public String showBookingView() {
        return "bookingViewUser";
    }

    @GetMapping("/allIncidents")
    public String showAllIncidentsPage() {
        return "allIncidents";
    }

    @GetMapping("/dashboard")
    public String showDashBoard() {
        return "dashboard";
    }
}
