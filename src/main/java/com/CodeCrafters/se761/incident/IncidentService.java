package com.CodeCrafters.se761.incident;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;

import java.util.List;
/**
 * Service class for managing incidents and their statuses.
 */
@Service
public class IncidentService {
    private final IncidentRepository incidentRepository;
    /**
     * Constructs an instance of the IncidentService.
     *
     * @param incidentRepository The repository for managing incidents.
     */
    @Autowired
    public IncidentService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }
    /**
     * Adds a new incident to the system.
     *
     * @param incident The incident to be added.
     */
    public void addNewIncident(Incident incident) {
        incidentRepository.save(incident);
    }
    /**
     * Retrieves a list of all incidents in the system.
     *
     * @return A list of all incidents.
     */
    public List<Incident> getAllIncidents() {
        return incidentRepository.findAll();
    }
    /**
     * Sets the status of an incident to "Pending."
     *
     * @param id The ID of the incident to update.
     */
    @Transactional
    public void setStatusPending(long id) {
        Optional<Incident> optionalIncident = incidentRepository.findById(id);

        if (optionalIncident.isPresent()) {
            Incident incident = optionalIncident.get();
            incident.setIncident_status("Pending");
        } else {
            throw new IllegalStateException("Booking with id " + id + " does not exist");
        }
    }
    /**
     * Sets the status of an incident to "More Info Requested."
     *
     * @param id The ID of the incident to update.
     */
    @Transactional
    public void setStatusInfoRequest(long id) {
        Optional<Incident> optionalIncident = incidentRepository.findById(id);

        if (optionalIncident.isPresent()) {
            Incident incident = optionalIncident.get();
            incident.setIncident_status("More Info Requested");
        } else {
            throw new IllegalStateException("Booking with id " + id + " does not exist");
        }
    }
    /**
     * Sets the status of an incident to "Closed."
     *
     * @param id The ID of the incident to update.
     */
    @Transactional
    public void setStatusClosed(long id) {
        Optional<Incident> optionalIncident = incidentRepository.findById(id);

        if (optionalIncident.isPresent()) {
            Incident incident = optionalIncident.get();
            incident.setIncident_status("Closed");
        } else {
            throw new IllegalStateException("Booking with id " + id + " does not exist");
        }
    }
    /**
     * Sets the status of an incident to "Cancelled."
     *
     * @param id The ID of the incident to update.
     */
    @Transactional
    public void setStatusCancelled(long id) {
        Optional<Incident> optionalIncident = incidentRepository.findById(id);

        if (optionalIncident.isPresent()) {
            Incident incident = optionalIncident.get();
            incident.setIncident_status("Cancelled");
        } else {
            throw new IllegalStateException("Booking with id " + id + " does not exist");
        }
    }
    //deleting Incident
    public void deleteIncident(Long id){

        boolean exists = incidentRepository.existsById(id);
        if (!exists){
            throw new IllegalStateException("Booking with id " + id + " does not exist");
        }
        incidentRepository.deleteById(id);
    }
    // fetch individual Incidents using Incident IDs
    public Incident getIncidentByIncidentId(Long incidentId) {
        return incidentRepository.findByIncidentID(incidentId).orElse(null);
    }
    /**
     * Saves changes to an existing incident.
     *
     * @param incident The modified incident to be saved.
     */
    public void save(Incident incident) {
        incidentRepository.save(incident);
    }
    /**
     * Retrieves the incident ID associated with a booking ID.
     *
     * @param bookingID The ID of the booking.
     * @return The incident ID or a message indicating the incident is not found.
     */
    public String getIncidentIDWithBookingID(Long bookingID) {
        Optional<Incident> incident = incidentRepository.findByBookingID(bookingID);
        if (incident.isPresent()) {
            return incident.get().getIncidentId().toString();
        } else {
            return "Incident not found";
        }
    }
}