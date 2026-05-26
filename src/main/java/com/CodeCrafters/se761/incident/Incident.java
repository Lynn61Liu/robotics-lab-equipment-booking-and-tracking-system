package com.CodeCrafters.se761.incident;
import java.sql.Date;

import jakarta.persistence.*;
/**
 * Represents an incident related to a booking, including its description, status, creation date,
 * and the associated booking.
 */
@Entity
@Table
public class Incident {
    @Id
    @GeneratedValue(generator = "incident_id_generator")
    @TableGenerator(
            name = "incident_id_generator",
            table = "incident_id_sequence_table",
            pkColumnName = "incident_id_sequence",
            valueColumnName = "next_val",
            pkColumnValue = "incident_id_sequence",
            allocationSize = 1
    )
    @Column(name = "incidentid")
    private Long incident_id;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    @Column(name = "incident_status")
    private String incident_status;
    @Column(name = "created_date")
    private Date created_date;
    @Column(name = "booking_id")
    private Long booking_id;


    //constructors
    public Incident(Long incident_id, String description, String incident_status, Date created_date, Long booking_id) {
        this.incident_id = incident_id;
        this.description = description;
        this.incident_status = incident_status;
        this.created_date = created_date;
        this.booking_id = booking_id;
    }

    public Incident(String description, String incident_status, Date created_date, Long booking_id) {
        this.description = description;
        this.incident_status = incident_status;
        this.created_date = created_date;
        this.booking_id = booking_id;
    }

    public Incident() {

    }
    // getters and setters
    public Long getIncidentId() {
        return incident_id;
    }

    public void setIncidentId(Long incident_id) {
        this.incident_id = incident_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIncident_status() {
        return incident_status;
    }

    public void setIncident_status(String is_resolved) {
        this.incident_status = is_resolved;
    }

    public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
        this.created_date = created_date;
    }

    public long getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(Long booking_id) {
        this.booking_id = booking_id;
    }

    @Override
    public String toString() {
        return "Incident{" +
                "incident_id=" + incident_id +
                ", description='" + description + '\'' +
                ", incident_status='" + incident_status + '\'' +
                ", created_date=" + created_date +
                ", booking_id=" + booking_id +
                '}';
    }
}