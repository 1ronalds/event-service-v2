package eventservice.eventservice.business.repository;

import eventservice.eventservice.business.repository.model.EventEntity;
import eventservice.eventservice.business.repository.model.EventTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {
    List<EventEntity> findAllByCountryAndEventType(String country, EventTypeEntity type);

    List<EventEntity> findAllByCountryAndEventTypeAndCity(String country, EventTypeEntity type, String city);

    List<EventEntity> findAllByCountryAndEventTypeAndCityAndDateTimeBetween(String country, EventTypeEntity type, String city, LocalDateTime dateFrom, LocalDateTime dateTo);

    List<EventEntity> findAllByCountryAndEventTypeAndDateTimeBetween(String country, EventTypeEntity type, LocalDateTime dateFrom, LocalDateTime dateTo);

    @Query(value = "SELECT * FROM event " +
            "LEFT JOIN user ON event.organiser_id = user.user_id " +
            "WHERE " +
            "user.username = :username AND " +
            "(:country IS null OR event_location_country = :country) AND " +
            "(:city IS NULL OR event_location_city = :city) AND " +
            "event_datetime BETWEEN :dateFrom AND :dateTo", nativeQuery = true)
    List<EventEntity> findAllByOrganiserUsernameAndCountryAndCityAndDateTimeBetween(String username,
                                                                                    @Param("country") String country,
                                                                                    @Param("city") String city,
                                                                                    @Param("dateFrom") LocalDateTime dateTimeFrom,
                                                                                    @Param("dateTo") LocalDateTime dateTimeTo);

    @Query(value = "SELECT * FROM event " +
            "LEFT JOIN user ON event.organiser_id = user.user_id " +
            "WHERE " +
            "user.username = :username AND " +
            "(:country IS null OR event_location_country = :country) AND " +
            "(:city IS NULL OR event_location_city = :city)", nativeQuery = true)
    List<EventEntity> findAllByOrganiserUsernameAndCountryAndCity(String username,
                                                                  @Param("country") String country,
                                                                  @Param("city") String city);


    @Query(value = "SELECT * FROM event" +
            "   LEFT OUTER JOIN attendance" +
            "       ON event.event_id = attendance.event_id" +
            "   LEFT OUTER JOIN user" +
            "       ON attendance.user_id = user.user_id" +
            "   WHERE user.username = :username" +
            "       AND (:country IS NULL OR event.event_location_country = :country)" +
            "       AND (:city IS NULL OR event.event_location_city = :city)" +
            "       AND event.event_datetime BETWEEN :dateFrom AND :dateTo", nativeQuery = true)
    List<EventEntity> findAllAttendingByCountryAndCityAndDateTimeBetween(@Param(value = "username") String username,
                                                                         @Param(value = "country") String country,
                                                                         @Param(value = "city") String city,
                                                                         @Param(value = "dateFrom") LocalDateTime dateFrom,
                                                                         @Param(value = "dateTo") LocalDateTime dateTo);

    @Query(value = "SELECT * FROM event" +
            "   LEFT OUTER JOIN attendance" +
            "       ON event.event_id = attendance.event_id" +
            "   LEFT OUTER JOIN user" +
            "       ON attendance.user_id = user.user_id" +
            "   WHERE user.username = :username" +
            "       AND (:country IS NULL OR event.event_location_country = :country)" +
            "       AND (:city IS NULL OR event.event_location_city = :city)", nativeQuery = true)
    List<EventEntity> findAllAttendingByCountryAndCity(@Param(value = "username") String username,
                                                                         @Param(value = "country") String country,
                                                                         @Param(value = "city") String city);
}
