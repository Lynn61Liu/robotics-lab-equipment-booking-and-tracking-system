package com.CodeCrafters.se761.booking;
import java.sql.Date;

import jakarta.persistence.*;
/**
 * The `Booking` class represents a booking for equipment in the system.
 * It stores information such as the booking's start and end times, the user who made the booking,
 * the booking status, equipment ID, and additional details.
 *
 * This class is used to model and manage booking data within the application and is associated
 * with the `BookingService`, `BookingRepository`, and other classes for data access and business logic.
 *
 * @since 13-Sep-2023
 */
@Entity
@Table
public class Booking {
    // Primary key generated with a custom ID generator
    @Id
    @GeneratedValue(generator = "booking_id_generator")
    @TableGenerator(
            name = "booking_id_generator",
            table = "id_sequence_table",
            pkColumnName = "booking_id_sequence",
            valueColumnName = "next_val",
            pkColumnValue  = "booking_id_sequence",
            allocationSize = 1
    )
    @Column (name = "id")
    private Long id;
    @Column (name = "starttime")
    private String startTime;
    @Column (name = "endtime")
    private String endTime;
    @Column (name = "bookedby")
    private String bookedBy;
    @Column (name = "status")
    private String status;
    @Column (name = "equipmentid")
    private Long equipmentid;
    @Column (name = "request_date")
    private Date request_date;
    @Column (name = "reminderStartDate")
    private String reminderStartDate;
    @Column (name = "reminderEndDate")
    private String reminderEndDate;
    @Column(name = "rejection_reason")
    private String rejection_reason;
    @Column(name = "last_modified")
    private Date last_modified;

    // Constructors
    public Booking(Long id, String StartTime, String EndTime, String bookedBy, String status, Long equipment
            , Date request_date, String rejection_reason, Date last_modified) {
        this.id = id;
        this.startTime = StartTime;
        this.endTime = EndTime;
        this.bookedBy = bookedBy;
        this.status = status;
        this.equipmentid = equipment;
        this.request_date = request_date;
        this.reminderStartDate = reminderStartDate;
        this.reminderEndDate = reminderEndDate;
        this.rejection_reason = rejection_reason;
        this.last_modified = last_modified;
    }

    public Booking(String StartTime, String EndTime, String bookedBy, String status, Long equipment
            , Date request_date, String rejection_reason, Date last_modified){
        this.startTime = StartTime;
        this.endTime = EndTime;
        this.bookedBy = bookedBy;
        this.status = status;
        this.equipmentid = equipment;
        this.request_date = request_date;
        this.reminderStartDate = reminderStartDate;
        this.reminderEndDate = reminderEndDate;
        this.rejection_reason = rejection_reason;
        this.last_modified = last_modified;
    }

    public Booking() {

    }
    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String StartTime) {
        this.startTime = StartTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String EndTime) {
        this.endTime = EndTime;
    }

    public String getBookedBy() {
        return bookedBy;
    }

    public String getRejection_reason() {
        return rejection_reason;
    }

    public void setRejection_reason(String rejection_reason) {
        this.rejection_reason = rejection_reason;
    }

    public Date getLast_modified() {
        return last_modified;
    }

    public void setLast_modified(Date last_modified) {
        this.last_modified = last_modified;
    }

    public Date getRequest_date() {
        return request_date;
    }

    public String getReminderStartDate() {
        return reminderStartDate;
    }

    public String getReminderEndDate() {
        return reminderEndDate;
    }

    public void setRequest_date(Date request_date) {
        this.request_date = request_date;
    }

    public void setReminderStartDate(String reminderStartDate) {
        this.reminderStartDate = reminderStartDate;
    }

    public void setReminderEndDate(String reminderEndDate) {
        this.reminderEndDate = reminderEndDate;
    }

    public void setBookedBy(String bookedby) {
        this.bookedBy = bookedby;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getEquipmentid() {
        return equipmentid;
    }

    public void setEquipmentid(Long equipment) {
        this.equipmentid = equipment;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", Start Time='" + startTime + '\'' +
                ", End Time='" + endTime + '\'' +
                ", Booked By=" + bookedBy + '\'' +
                ", Status =" + status + '\'' +
                ", Equipment ='" + equipmentid + '\'' +
                '}';
    }
}