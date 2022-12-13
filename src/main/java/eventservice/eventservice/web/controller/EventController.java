package eventservice.eventservice.web.controller;

import eventservice.eventservice.business.service.EventService;
import eventservice.eventservice.model.EventDto;
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

    @ApiOperation(value = "Finds all public events by country and city, date interval if specified")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The request is successful"),
            @ApiResponse(code = 400, message = "Missed required parameters, parameters not valid")
    })
    @GetMapping(value = "/events")
    public ResponseEntity<List<EventDto>> findAllPublicEvents(@ApiParam(value = "country, where the event will take place", required = true)
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
}
