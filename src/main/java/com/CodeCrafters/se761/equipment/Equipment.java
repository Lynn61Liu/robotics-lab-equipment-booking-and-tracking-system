package com.CodeCrafters.se761.equipment;


import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Represents an equipment instance in the system
 * Author: Jonathon Lee
 */
@Entity
@Table
public class Equipment {
    @Id
    @GeneratedValue(generator = "equipment_systemid_generator")
    @TableGenerator(
            name = "equipment_systemid_generator",
            table = "systemid_sequence_table",
            pkColumnName = "equipment_systemid_sequence",
            valueColumnName = "next_val",
            pkColumnValue = "equipment_systemid_sequence",
            allocationSize = 1
    )
    @Column(name = "systemid") // names of the column in the actual online database
    public Long systemID;
    @Column(name = "description")
    public String description;
    @Column(name = "brand")
    public String brand;
    @Column(name = "model")
    public String model;
    @Column(name = "uoaid")
    public String uoaID;
    @Column(name = "serial_number")
    public String serialNumber;
    @Column(name = "building")
    public Integer building;
    @Column(name = "room")
    public String room;
    @Column(name = "notes", columnDefinition = "TEXT")
    public String notes;
    @Column(name = "normal_position")
    public String normalPosition;
    @Column(name = "width_mm")
    public Integer widthMM;
    @Column(name = "depth_mm")
    public Integer depthMM;
    @Column(name = "height_mm")
    public Integer heightMM;
    @Column(name = "power_rating")
    public String powerRating;
    @Column(name = "phase_1_or_3")
    public String phase1or3;
    @Column(name = "last_edit_time")
    public Timestamp lastEditTime;
    @Column(name = "last_booked_day")
    public Timestamp lastBookedDay;
    @Column(name = "image_url", columnDefinition = "TEXT")
    public String imageURL;
    @Transient
    public Timestamp dueDay; // no need for column as it will be calculated
    @Column(name = "max_use_day")
    public Integer maxUseDay;
    @Column(name = "pickup_allowed")
    public Boolean pickupAllowed;
    @Column(name = "booked_by")
    public String bookedBy;
    @Column(name = "safety_requirements", columnDefinition = "TEXT")
    public String safetyRequirements;
    @Column(name = "operating_requirements", columnDefinition = "TEXT")
    public String operatingRequirements;

    public Equipment(Long systemID,
                     String description,
                     String brand,
                     String model,
                     String uoaID,
                     String serialNumber,
                     Integer building,
                     String room,
                     String notes,
                     String normalPosition,
                     Integer widthMM,
                     Integer depthMM,
                     Integer heightMM,
                     String powerRating,
                     String phase1or3,
                     Timestamp lastEditTime,
                     Timestamp lastBookedDay,
                     String imageURL,
                     Timestamp dueDay,
                     Integer maxUseDay,
                     Boolean pickupAllowed,
                     String bookedBy,
                     String safetyRequirements,
                     String operatingRequirements) {
        this.systemID = systemID;
        this.description = description;
        this.brand = brand;
        this.model = model;
        this.uoaID = uoaID;
        this.serialNumber = serialNumber;
        this.building = building;
        this.room = room;
        this.notes = notes;
        this.normalPosition = normalPosition;
        this.widthMM = widthMM;
        this.depthMM = depthMM;
        this.heightMM = heightMM;
        this.powerRating = powerRating;
        this.phase1or3 = phase1or3;
        this.lastEditTime = lastEditTime;
        this.lastBookedDay = lastBookedDay;
        this.imageURL = imageURL;
        this.dueDay = dueDay;
        this.maxUseDay = maxUseDay;
        this.pickupAllowed = pickupAllowed;
        this.bookedBy = bookedBy;
        this.safetyRequirements = safetyRequirements;
        this.operatingRequirements = operatingRequirements;
    }

    public Equipment(String description,
                     String brand,
                     String model,
                     String uoaID,
                     String serialNumber,
                     Integer building,
                     String room,
                     String notes,
                     String normalPosition,
                     Integer widthMM,
                     Integer depthMM,
                     Integer heightMM,
                     String powerRating,
                     String phase1or3,
                     Timestamp lastEditTime,
                     Timestamp lastBookedDay,
                     String imageURL,
                     Timestamp dueDay,
                     Integer maxUseDay,
                     Boolean pickupAllowed,
                     String bookedBy,
                     String safetyRequirements,
                     String operatingRequirements) {
        this.description = description;
        this.brand = brand;
        this.model = model;
        this.uoaID = uoaID;
        this.serialNumber = serialNumber;
        this.building = building;
        this.room = room;
        this.notes = notes;
        this.normalPosition = normalPosition;
        this.widthMM = widthMM;
        this.depthMM = depthMM;
        this.heightMM = heightMM;
        this.powerRating = powerRating;
        this.phase1or3 = phase1or3;
        this.lastEditTime = lastEditTime;
        this.lastBookedDay = lastBookedDay;
        this.imageURL = imageURL;
        this.dueDay = dueDay;
        this.maxUseDay = maxUseDay;
        this.pickupAllowed = pickupAllowed;
        this.bookedBy = bookedBy;
        this.safetyRequirements = safetyRequirements;
        this.operatingRequirements = operatingRequirements;
    }

    public Equipment() {
    }



    public Long getSystemID() {
        return systemID;
    }

    public void setSystemID(Long systemID) {
        this.systemID = systemID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getUoaID() {
        return uoaID;
    }

    public void setUoaID(String uoaID) {
        this.uoaID = uoaID;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Integer getBuilding() {
        return building;
    }

    public void setBuilding(Integer building) {
        this.building = building;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getNormalPosition() {
        return normalPosition;
    }

    public void setNormalPosition(String normalPosition) {
        this.normalPosition = normalPosition;
    }

    public Integer getWidthMM() {
        return widthMM;
    }

    public void setWidthMM(Integer widthMM) {
        this.widthMM = widthMM;
    }

    public Integer getDepthMM() {
        return depthMM;
    }

    public void setDepthMM(Integer depthMM) {
        this.depthMM = depthMM;
    }

    public Integer getHeightMM() {
        return heightMM;
    }

    public void setHeightMM(Integer heightMM) {
        this.heightMM = heightMM;
    }

    public String getPowerRating() {
        return powerRating;
    }

    public void setPowerRating(String powerRating) {
        this.powerRating = powerRating;
    }

    public String getPhase1or3() {
        return phase1or3;
    }

    public void setPhase1or3(String phase1or3) {
        this.phase1or3 = phase1or3;
    }

    public Timestamp getLastEditTime() {
        return lastEditTime;
    }

    public void setLastEditTime(Timestamp lastEditTime) {
        this.lastEditTime = lastEditTime;
    }

    public Timestamp getLastBookedDay() {
        return lastBookedDay;
    }

    public void setLastBookedDay(Timestamp lastBookedDay) {
        this.lastBookedDay = lastBookedDay;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Timestamp getDueDay() {
        return calculateDueDay();
    }

    public void setDueDay(Timestamp dueDay) {
        this.dueDay = dueDay;
    }

    public Integer getMaxUseDay() {
        return maxUseDay;
    }

    public void setMaxUseDay(Integer maxUseDay) {
        this.maxUseDay = maxUseDay;
    }

    public Boolean getPickupAllowed() {
        return pickupAllowed;
    }

    public void setPickupAllowed(Boolean pickupAllowed) {
        this.pickupAllowed = pickupAllowed;
    }

    public String getBookedBy() {
        return bookedBy;
    }

    public void setBookedBy(String bookedBy) {
        this.bookedBy = bookedBy;
    }

    public String getSafetyRequirements() {
        return safetyRequirements;
    }

    public void setSafetyRequirements(String safetyRequirements) {
        this.safetyRequirements = safetyRequirements;
    }

    public String getOperatingRequirements() {
        return operatingRequirements;
    }

    public void setOperatingRequirements(String operatingRequirements) {
        this.operatingRequirements = operatingRequirements;
    }

    @Override
    public String toString() {
        return "Equipment{" +
                "systemID=" + systemID +
                ", description='" + description + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", uoaID='" + uoaID + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", building=" + building +
                ", room='" + room + '\'' +
                ", notes='" + notes + '\'' +
                ", normalPosition='" + normalPosition + '\'' +
                ", widthMM=" + widthMM +
                ", depthMM=" + depthMM +
                ", heightMM=" + heightMM +
                ", powerRating='" + powerRating + '\'' +
                ", phase1or3='" + phase1or3 + '\'' +
                ", lastEditTime=" + lastEditTime +
                ", lastBookedDay=" + lastBookedDay +
                ", imageURL='" + imageURL + '\'' +
                ", dueDay=" + dueDay +
                ", maxUseDay=" + maxUseDay +
                ", pickupAllowed=" + pickupAllowed +
                ", bookedBy='" + bookedBy + '\'' +
                ", safetyRequirements='" + safetyRequirements + '\'' +
                ", operatingRequirements='" + operatingRequirements + '\'' +
                '}';
    }

    /**
     * Calculate the due day based on the last booked day and maximum allowed usage days.
     *
     * @return Timestamp representing the due day, or null if lastBookedDay or maxUseDay is null.
     */
    public Timestamp calculateDueDay() {
        if (lastBookedDay != null && maxUseDay != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(lastBookedDay);
            calendar.add(Calendar.DAY_OF_MONTH, maxUseDay);
            return new Timestamp(calendar.getTime().getTime());
        }
        return null;
    }

}
