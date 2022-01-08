package com.teo.foodzzzbackend.repository;

import com.teo.foodzzzbackend.model.Reservation;
import com.teo.foodzzzbackend.model.ReservationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    Reservation save(Reservation reservation);

    Optional<List<Reservation>> findAllByUserId(Long id);

    @Query("SELECT new com.teo.foodzzzbackend.model.ReservationDTO(res.reservationId, res.reservationDate, res.numberOfPersons, " +
            "res.reservationHour,res.tableNumber,r.id, r.restaurantName, res.user.id) "
            + "FROM Reservation res LEFT JOIN res.restaurant r where res.user.id = ?1 " +
            "and res.restaurant.id = ?2 and res.reservationId<>?3 " +
            "ORDER BY res.reservationDate desc, res.reservationHour desc")
    List<ReservationDTO> findAllReservationsAndRestaurantsByUserIdAndRestaurantId(Long id, Integer restaurantId, Integer reservationId);

    @Query("SELECT new com.teo.foodzzzbackend.model.ReservationDTO(res.reservationId, res.reservationDate, res.numberOfPersons, " +
            "res.reservationHour, res.tableNumber,r.id, r.restaurantName, res.user.id) "
            + "FROM Reservation res LEFT JOIN res.restaurant r where res.user.id = ?1 " +
            "ORDER BY res.reservationDate desc, res.reservationHour desc")
    List<ReservationDTO> findAllReservationsAndRestaurantsByUserId(Long id);

    @Query("SELECT new com.teo.foodzzzbackend.model.ReservationDTO(res.reservationId, res.reservationDate, res.numberOfPersons, " +
            "res.reservationHour, res.tableNumber,r.id, r.restaurantName, res.user.id) "
            + "FROM Reservation res LEFT JOIN res.restaurant r where res.reservationDate > current_date()")
    List<ReservationDTO> findAllReservationsForConfirmation();

    @Query("SELECT new com.teo.foodzzzbackend.model.ReservationDTO(res.reservationId, res.reservationDate, res.numberOfPersons, " +
            "res.reservationHour,res.tableNumber,r.id, r.restaurantName, res.user.id) "
            + "FROM Reservation res LEFT JOIN res.restaurant r where res.user.id = ?1 " +
            "ORDER BY res.reservationDate desc, res.reservationHour desc")
    Page<ReservationDTO> findAllReservationsAndRestaurantsByUserId(Long id, Pageable pageable);

    @Query("SELECT new com.teo.foodzzzbackend.model.ReservationDTO(res.reservationId, res.reservationDate, res.numberOfPersons, " +
            "res.reservationHour, res.tableNumber,r.id, r.restaurantName, res.user.id) "
            + "FROM Reservation res LEFT JOIN res.restaurant r where r.id = ?1 and res.reservationDate = ?2 " +
            "and res.reservationHour = ?3 ORDER BY res.reservationHour desc")
    List<ReservationDTO> findAllReservationsByRestaurantIdAndReservationDate(Integer id,
                                                                             Date reservationDate, String reservationHour);

    Optional<List<Reservation>> findAllByRestaurantIdOrderByReservationDateDesc(int id);

    @Query("SELECT new com.teo.foodzzzbackend.model.ReservationDTO(res.reservationId, res.reservationDate, res.numberOfPersons, " +
            "res.reservationHour, res.tableNumber,r.id, r.restaurantName, res.user.id) "
            + "FROM Reservation res LEFT JOIN res.restaurant r where res.user.id = ?1 " +
            "and res.reservationId in (select max(r.reservationId) " +
            "FROM Reservation r " +
            "where r.reservationDate = (select max(rezerv.reservationDate) " +
            "from Reservation rezerv " +
            "where rezerv.user.id = ?1 and rezerv.restaurant.id = r.restaurant.id " +
            "group by rezerv.restaurant.id ) and r.user.id = ?1 " +
            "group by r.user.id, r.restaurant.id, r.reservationDate) " +
            "ORDER BY res.reservationDate desc, res.reservationHour desc")
    Page<ReservationDTO> findAllByUserIdPageable(Long id, Pageable pageable);

    Optional<List<Reservation>> findAllByRestaurantIdAndReservationStatus(Integer restaurantId, String reservationStatus);

    @Modifying
    @Transactional
    @Query("update Reservation r set r.reservationStatus = 'accepted' where r.reservationId = ?1")
    void changeReservationStatusToAccepted(Integer reservationId);

    @Modifying
    @Transactional
    @Query("update Reservation r set r.reservationStatus = 'declined' where r.reservationId = ?1")
    void changeReservationStatusToDeclined(Integer reservationId);
}
