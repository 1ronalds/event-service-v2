package eventservice.eventservice.business.repository.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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

    @Column(name = "event_title")
    private String title;

    @Column(name = "event_description")
    private String description;

    @Column(name = "event_location_country")
    private String country;

    @Column(name = "event_location_city")
    private String city;

    @Column(name = "max_attendance")
    private int maxAttendance;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm") //necessary for test to work
    @Column(name = "event_datetime")
    private LocalDateTime dateTime;

    @Column(name = "attendee_count")
    private int attendeeCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organiser_id")
    private UserEntity organiser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type")
    private EventTypeEntity type;

    @ManyToMany
    @JoinTable(
            name = "attendance",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<UserEntity> attendees;

    public void addAttendee(UserEntity user){
        attendeeCount++;
        attendees.add(user);
    }

    public void removeAttendee(UserEntity userEntity) {
        attendeeCount--;
        attendees.remove(userEntity);
    }

    public boolean containsAttendance(Long userId){
        for (UserEntity user : attendees) {
            if (Objects.equals(user.getId(), userId)){
                return true;
            }
        }
        return false;
    }
}
