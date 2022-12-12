package eventservice.eventservice.business.repository.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "event")
@Entity
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    @NotNull
    @Column(name = "event_title")
    @Size(max = 50)
    private String title;

    @NotNull
    @Column(name = "event_description")
    @Size(min = 50, max = 500)
    private String description;

    @NotNull
    @Column(name = "event_location_country")
    @Size(max = 25)
    private String country;

    @NotNull
    @Column(name = "event_location_city")
    @Size(max = 25)
    private String city;

    @NotNull
    @Column(name = "max_attendance")
    private int maxAttendance;

    @NotNull
    @Column(name = "event_datetime")
    private Date dateTime;

    @Column(name = "attendee_count")
    private int attendeeCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organiser_id")
    private UserEntity organiser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type")
    private EventTypeEntity type;
}
