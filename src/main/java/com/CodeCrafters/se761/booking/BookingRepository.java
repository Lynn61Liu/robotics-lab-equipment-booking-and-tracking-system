package com.CodeCrafters.se761.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import java.util.List;
/**
 * The `BookingRepository` interface is a Spring Data JPA repository that provides access to the underlying database
 * for the `Booking` entity. It contains custom query methods for retrieving and managing booking-related data.
 * These queries are based on specific criteria and parameters and are defined using JPA Query annotations. It is
 * connected to the PSQL database on Aiven.
 *
 * @author Multiple developers
 * @since 07-Sep-2023
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    /**
     * Retrieve a booking by equipment ID.
     *
     * @param equipmentId The ID of the equipment associated with the booking.
     * @return An Optional containing the booking, if found.
     */
    @Query("SELECT s FROM Booking s WHERE s.equipmentid = ?1")
    Optional<Booking> findByEquipmentid(Long equipmentId);
    /**
     * Retrieve a list of bookings by equipment ID.
     *
     * @param equipmentId The ID of the equipment associated with the booking.
     * @return A list containing the bookings, if found.
     */
    @Query("SELECT s FROM Booking s WHERE s.equipmentid = ?1")
    List<Booking> listFindByEquipmentid(Long equipmentId);
    /**
     * Retrieve a booking by UPI.
     *
     * @param UPI The UID of the user associated with the booking.
     * @return A list containing the bookings, if found.
     */
    @Query("SELECT s FROM Booking s WHERE s.bookedBy = ?1")
    List<Booking> findByUPI(String UPI);
    /**
     * Retrieve a booking by UPI.
     *
     * @param id The UID of the user associated with the booking.
     * @return A list containing the bookings, if found.
     */
    @Query("SELECT s FROM Booking s WHERE s.id = ?1")
    Optional<Booking> findById(Long id);
    /**
     * Retrieves a list of bookings with a `reminderStartDate` greater than or equal to the specified `startDate`
     * and a matching `status`. This method is typically used to find bookings with reminders set for the next two days.
     *
     * @param startDate The start date for filtering bookings by `reminderStartDate`.
     * @param status    The status to match for the bookings.
     * @return A list of bookings meeting the criteria.
     */
    @Query("SELECT s FROM Booking s WHERE s.reminderStartDate >= :startDate AND s.status = :status")
    List<Booking> findByStartTimeAndStatusTwoDays(
            @Param("startDate") String startDate,
            @Param("status") String status
    );
    /**
     * Retrieves a list of bookings with a `reminderEndDate` less than the specified `endDate`
     * and a matching `status`. This method is commonly used to find bookings with reminders set for the next two days.
     *
     * @param endDate The end date for filtering bookings by `reminderEndDate`.
     * @param status  The status to match for the bookings.
     * @return A list of bookings meeting the criteria.
     */
    @Query("SELECT s FROM Booking s WHERE s.reminderEndDate < :endDate AND s.status = :status")
    List<Booking> findByEndTimeAndStatusTwoDays(
            @Param("endDate") String endDate,
            @Param("status") String status
    );
    /**
     * Retrieves a list of bookings with an `endTime` less than the specified `endDate`
     * and a matching `status`. This method is typically used to find bookings that have ended on the current day.
     *
     * @param endDate The end date for filtering bookings by `endTime`.
     * @param status  The status to match for the bookings.
     * @return A list of bookings meeting the criteria.
     */
    @Query("SELECT s FROM Booking s WHERE s.endTime < :endDate AND s.status = :status")
    List<Booking> findByEndTimeAndStatusToday(
            @Param("endDate") String endDate,
            @Param("status") String status
    );
}


