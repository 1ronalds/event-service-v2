package eventservice.eventservice.business.repository;

import eventservice.eventservice.business.repository.model.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {
    List<EventEntity> findAllByCountryAndTypeType(String country, String type);
    List<EventEntity> findAllByCountryAndTypeTypeAndCity(String country, String type, String city);
    List<EventEntity> findAllByCountryAndTypeTypeAndCityAndDateTimeBetween(String country, String type, String city, LocalDateTime dateFrom, LocalDateTime dateTo);
    List<EventEntity> findAllByCountryAndTypeTypeAndDateTimeBetween(String country, String type, LocalDateTime dateFrom, LocalDateTime dateTo);


    List<EventEntity> findAllByCountry(String country);
    List<EventEntity> findAllByCountryAndCity(String country, String city);
    List<EventEntity> findAllByCountryAndCityAndDateTimeBetween(String country, String city, LocalDateTime dateFrom, LocalDateTime dateTo);
    List<EventEntity> findAllByCountryAndDateTimeBetween(String country, LocalDateTime dateFrom, LocalDateTime dateTo);

}
