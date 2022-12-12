package eventservice.eventservice.model;

import eventservice.eventservice.business.repository.model.EventTypeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
    private Long id;
    private String title;
    private String description;
    private String country;
    private String city;
    private int maxAttendance;
    private Date dateTime;
    private int attendeeCount;
    private UserDto organiser;
    private EventTypeDto type;
}
