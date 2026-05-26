package com.CodeCrafters.se761.incident;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for managing incidents in the database.
 */
@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {

    /**
     * Find an incident by its incident ID.
     *
     * @param incidentId The ID of the incident to find.
     * @return An Optional containing the incident details or empty if not found.
     */
    @Query("SELECT s FROM Incident s WHERE s.incident_id = ?1")
    Optional<Incident> findByIncidentID(Long incidentId);

    /**
     * Find an incident by its associated booking ID.
     *
     * @param bookingId The ID of the associated booking.
     * @return An Optional containing the incident details or empty if not found.
     */
    @Query("SELECT s FROM Incident s WHERE s.booking_id = ?1")
    Optional<Incident> findByBookingID(long bookingId);
}