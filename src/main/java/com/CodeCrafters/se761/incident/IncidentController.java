package com.CodeCrafters.se761.incident;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * The controller class that handles HTTP requests related to incidents.
 */
@RestController
public class IncidentController {

    private final IncidentService incidentService;

    private final ObjectMapper objectMapper;

    @Autowired
    public IncidentController(IncidentService incidentService, ObjectMapper objectMapper) {
        this.incidentService = incidentService;
        this.objectMapper = objectMapper;
    }
    /**
     * Handles the POST request to add a new incident.
     *
     * @param incident The incident data to be added.
     * @return A response indicating the success or failure of the operation.
     * @throws JsonProcessingException If there is an issue processing JSON data.
     */
    @PostMapping("/AddIncident")
    public ResponseEntity<String> processFormData(@RequestBody Incident incident) throws JsonProcessingException {

        // temp line for checking if connection is made
        System.out.println("/process mapping reaching and postFormData method started");

        Date currentDate = new Date();
        java.sql.Date sqlCurrentDate = new java.sql.Date(currentDate.getTime());
        incident.setCreated_date(sqlCurrentDate);

        // printing booking
        System.out.println(incident);

        incidentService.addNewIncident(incident);

        // Return a response back to the frontend
        return ResponseEntity.ok("{\"status\": \"success\", \"message\": \"Data received successfully!\"}");
    }
    /**
     * Handles the PUT request to update an existing incident by its ID.
     *
     * @param incident_id    The ID of the incident to be updated.
     * @param updatedIncident The updated incident data.
     * @return A response indicating the success or failure of the operation.
     */
    @PutMapping("/incidentUpdate/{incident_id}") //update incident endpoint
    public ResponseEntity<Incident> editIncident(@PathVariable Long incident_id, @RequestBody Incident updatedIncident) {
        System.out.println("update incident endpoint");
        ResponseEntity<Incident> responseEntity = getIncidentDetailsById(incident_id);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            Incident existingIncident = responseEntity.getBody();

            existingIncident.setDescription(updatedIncident.getDescription());
            existingIncident.setIncident_status(updatedIncident.getIncident_status());

            // Add more fields as needed

            // Save the updated incident
            incidentService.save(existingIncident);

            return ResponseEntity.ok(existingIncident);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    /**
     * Handles the GET request to retrieve details of all incidents.
     *
     * @return A response containing the JSON representation of all incidents.
     */
    @GetMapping("/getAllIncidents")
    public ResponseEntity<String> processIncidents() {
        List<Incident> incidentList = incidentService.getAllIncidents();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        try {
            String jsonData = objectMapper.writeValueAsString(incidentList);
            return ResponseEntity.ok(jsonData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.ok("Error processing data");
        }
    }
    /**
     * Handles the GET request to retrieve details of an incident by its ID.
     *
     * @param incident_id The ID of the incident to retrieve.
     * @return A response containing the JSON representation of the incident.
     */
    @GetMapping("/incident/{incident_id}")
    public ResponseEntity<Incident> getIncidentDetailsById(@PathVariable Long incident_id) {
        Incident incident = incidentService.getIncidentByIncidentId(incident_id);
        if (incident != null) {
            return ResponseEntity.ok(incident);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    /**
     * Handles the GET request to find the incident ID associated with a booking ID.
     *
     * @param bookingID The booking ID to find the associated incident ID.
     * @return A response containing the incident ID or an error message.
     */
    @GetMapping("/getIncidentIDWithBookingID/{bookingID}")
    public ResponseEntity<String> findIncidentIDWithBookingID(@PathVariable Long bookingID) {
        String incidentID = incidentService.getIncidentIDWithBookingID(bookingID);
        if (incidentID == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Incident not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse.toString());
        }
        return ResponseEntity.ok(incidentID);
    }
    /**
     * The following endpoints are related to changing the statuses of incident
     *
     * @param id The booking ID to find the associated incident ID.
     * @return A response containing the status
     */
    @PutMapping("/pendingIncident/{id}")
    public ResponseEntity<String> pending_incident(@PathVariable ("id") Long id){
        incidentService.setStatusPending(id);
        return ResponseEntity.ok("pending incident");
    }

    @PutMapping("/moreInfoIncident/{id}")
    public ResponseEntity<String> info_request_incident(@PathVariable ("id") Long id){
        incidentService.setStatusInfoRequest(id);
        return ResponseEntity.ok("More info requested");
    }

    @PutMapping("/closedIncident/{id}")
    public ResponseEntity<String> closed_incident(@PathVariable ("id") Long id){
        incidentService.setStatusClosed(id);
        return ResponseEntity.ok("Closed Incident");
    }

    @PutMapping("/CancelledIncident/{id}")
    public ResponseEntity<String> Cancelled_incident(@PathVariable ("id") Long id){
        incidentService.setStatusCancelled(id);
        return ResponseEntity.ok("Cancelled Incident");
    }

    @GetMapping("/incidentDetails/{incidentID}")
    public ResponseEntity<Incident> getIncidentDetailsByIncidentID(@PathVariable Long incidentID) {
        Incident incident = incidentService.getIncidentByIncidentId(incidentID);

        if (incident != null) {
            return ResponseEntity.ok(incident);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping ("/deleteIncident/{id}")
    public ResponseEntity<String> deleteIncident(@PathVariable ("id") Long id){
        incidentService.deleteIncident(id);
        return ResponseEntity.ok("Deleted Incident");
    }
}