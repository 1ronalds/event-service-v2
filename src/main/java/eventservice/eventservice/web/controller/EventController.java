package eventservice.eventservice.web.controller;

import eventservice.eventservice.business.service.EventService;
import eventservice.eventservice.model.EventMinimalDto;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;
@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/v1")
public class EventController {
    private final EventService eventService;
    /**
     * Finds all public events based on certain criteria through parameter input
     * @param country - the country, where the event is taking place
     * @param city  - the city, where the event is taking place
     * @param dateFrom - the start of the date interval, which is used to find events
     *                   that are taking place during a certain time period
     * @param dateTo  - the end of the date interval, which is used to find events that
     *                  are taking place during a certain time period
     * */
    @ApiOperation(value = "Finds all public events by country and city, date interval if specified")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The request is successful"),
            @ApiResponse(code = 400, message = "Missed required parameters, parameters not valid")
    })
    @GetMapping(value = "/events")
    public ResponseEntity<List<EventMinimalDto>> findAllPublicEvents(@ApiParam(value = "country, where the event will take place", required = true)
                                                                  @RequestParam(name = "country") String country,
                                                                     @ApiParam(value = "city, where the event will take place")
                                                                  @RequestParam(name = "city", required = false) String city,
                                                                     @ApiParam(value = "The date from which events will take place")
                                                                  @RequestParam(name = "date_from", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateFrom,
                                                                     @ApiParam(value = "The date to which events will take place")
                                                                  @RequestParam(name = "date_to", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateTo){
        log.info("findAllPublicEvents controller method called with parameters " +
                "country: {}, city: {}, date_from: {}, date_to: {} ", country, city, dateFrom, dateTo);
        return ResponseEntity.ok(eventService.findAllPublicEvents(country, city, dateFrom, dateTo));
    }

    @GetMapping(value = "/events/user/{user_name}")
    public ResponseEntity<List<EventMinimalDto>> findAllUserCreatedAndOrAttendingEvents(
                                                            @ApiParam(value = "username of the user, which is used to filter out the user's events")
                                                                @PathVariable(name = "user_name") String username,
                                                            @ApiParam(value = "display value, which determines which events are returned")
                                                                @RequestParam(value = "display", required = true) String displayValue,
                                                            @ApiParam(value = "country, where the event will take place")
                                                                @RequestParam(name = "country", required = false) String country,
                                                            @ApiParam(value = "city, where the event will take place")
                                                                @RequestParam(name = "city", required = false) String city,
                                                            @ApiParam(value = "The date from which events will take place")
                                                                @RequestParam(name = "date_from", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateFrom,
                                                            @ApiParam(value = "The date to which events will take place")
                                                                @RequestParam(name = "date_to", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateTo){
        log.info("findAllUserCreatedAndOrAttendingEvents controller method called with parameters " +
                "username: {}, display: {}, country: {}, city: {}, date_from: {}, date_to: {} ", username, displayValue, country, city,
                dateFrom, dateTo);
        return ResponseEntity.ok(eventService.findAllUserCreatedAndOrAttendingEvents(username, displayValue, country, city, dateFrom, dateTo));
    }
}
