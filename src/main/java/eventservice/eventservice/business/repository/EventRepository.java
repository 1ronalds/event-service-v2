package eventservice.eventservice.business.repository;

import eventservice.eventservice.business.repository.model.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {
    List<EventEntity> findAllByCountryAndTypeType(String country, String type);

    List<EventEntity> findAllByCountryAndTypeTypeAndCity(String country, String type, String city);

    List<EventEntity> findAllByCountryAndTypeTypeAndCityAndDateTimeBetween(String country, String type, String city, LocalDateTime dateFrom, LocalDateTime dateTo);


    List<EventEntity> findAllByOrganiserUsernameAndCountry(String username, String country);

    List<EventEntity> findAllByOrganiserUsernameAndCountryAndCity(String username, String country, String city);

    List<EventEntity> findAllByOrganiserUsernameAndCountryAndCityAndDateTimeBetween(String username, String country, String city, LocalDateTime dateFrom, LocalDateTime dateTo);

    List<EventEntity> findAllByOrganiserUsernameAndCountryAndDateTimeBetween(String username, String country, LocalDateTime dateFrom, LocalDateTime dateTo);

    List<EventEntity> findAllByOrganiserUsernameAndCity(String username, String city);

    List<EventEntity> findAllByOrganiserUsernameAndCityAndDateTimeBetween(String username, String city, LocalDateTime dateFrom, LocalDateTime dateTo);

    List<EventEntity> findAllByOrganiserUsernameAndDateTimeBetween(String username, LocalDateTime dateFrom, LocalDateTime dateTo);

    @Query(value = "SELECT * FROM event" +
            "   LEFT OUTER JOIN attendance" +
            "       ON event.event_id = attendance.event_id" +
            "   LEFT OUTER JOIN user" +
            "       ON attendance.user_id = user.user_id" +
            "   WHERE user.username = :username" +
            "       AND event.event_location_country = :country" +
            "       AND event.event_location_city = :city" +
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
            "       AND event.event_location_country = :country" +
            "       AND event.event_location_city = :city", nativeQuery = true)
    List<EventEntity> findAllAttendingByCountryAndCity(@Param(value = "username") String username,
                                                            @Param(value = "country") String country,
                                                            @Param(value = "city") String city);

    @Query(value = "SELECT * FROM event" +
            "   LEFT OUTER JOIN attendance" +
            "       ON event.event_id = attendance.event_id" +
            "   LEFT OUTER JOIN user" +
            "       ON attendance.user_id = user.user_id" +
            "   WHERE user.username = :username" +
            "       AND event.event_location_country = :country" +
            "       AND event.event_datetime BETWEEN :dateFrom AND :dateTo", nativeQuery = true)
    List<EventEntity> findAllAttendingByCountryAndDateTimeBetween(@Param(value = "username") String username,
                                                                         @Param(value = "country") String country,
                                                                         @Param(value = "dateFrom") LocalDateTime dateFrom,
                                                                         @Param(value = "dateTo") LocalDateTime dateTo);

    @Query(value = "SELECT * FROM event" +
            "   LEFT OUTER JOIN attendance" +
            "       ON event.event_id = attendance.event_id" +
            "   LEFT OUTER JOIN user" +
            "       ON attendance.user_id = user.user_id" +
            "   WHERE user.username = :username" +
            "       AND event.event_location_country = :country", nativeQuery = true)
    List<EventEntity> findAllAttendingByCountry(@Param(value = "username") String username,
                                                        @Param(value = "country") String country);

    @Query(value = "SELECT * FROM event" +
            "   LEFT OUTER JOIN attendance" +
            "       ON event.event_id = attendance.event_id" +
            "   LEFT OUTER JOIN user" +
            "       ON attendance.user_id = user.user_id" +
            "   WHERE user.username = :username" +
            "       AND event.event_location_city = :city" +
            "       AND event.event_datetime BETWEEN :dateFrom AND :dateTo", nativeQuery = true)
    List<EventEntity> findAllAttendingByCityAndDateTimeBetween(@Param(value = "username") String username,
                                                                         @Param(value = "city") String city,
                                                                         @Param(value = "dateFrom") LocalDateTime dateFrom,
                                                                         @Param(value = "dateTo") LocalDateTime dateTo);

    @Query(value = "SELECT * FROM event" +
            "   LEFT OUTER JOIN attendance" +
            "       ON event.event_id = attendance.event_id" +
            "   LEFT OUTER JOIN user" +
            "       ON attendance.user_id = user.user_id" +
            "   WHERE user.username = :username" +
            "       AND event.event_location_city = :city", nativeQuery = true)
    List<EventEntity> findAllAttendingByCity(@Param(value = "username") String username,
                                                    @Param(value = "city") String city);

    @Query(value = "SELECT * FROM event" +
            "   LEFT OUTER JOIN attendance" +
            "       ON event.event_id = attendance.event_id" +
            "   LEFT OUTER JOIN user" +
            "       ON attendance.user_id = user.user_id" +
            "   WHERE user.username = :username" +
            "       AND event.event_datetime BETWEEN :dateFrom AND :dateTo", nativeQuery = true)
    List<EventEntity> findAllAttendingByDateTimeBetween(@Param(value = "username") String username,
                                                                @Param(value = "dateFrom") LocalDateTime dateFrom,
                                                                @Param(value = "dateTo") LocalDateTime dateTo);
    List<EventEntity> findAllByCountryAndTypeTypeAndDateTimeBetween(String country, String type, LocalDateTime dateFrom, LocalDateTime dateTo);
}
