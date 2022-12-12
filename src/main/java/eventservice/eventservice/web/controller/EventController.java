package eventservice.eventservice.web.controller;

import eventservice.eventservice.business.service.EventService;
import eventservice.eventservice.model.EventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/v1")
public class EventController {
    private final EventService eventService;

    @GetMapping(value = "/events")
    public ResponseEntity<List<EventDto>> findAllPublicEvents(@RequestParam(name = "country") String country,
                                                              @RequestParam(name = "city", required = false) String city,
                                                              @RequestParam(name = "date_from", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateFrom,
                                                              @RequestParam(name = "date_to", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateTo){
        return ResponseEntity.ok(eventService.findAllPublicEvents(country, city, dateFrom, dateTo));
    }
}
