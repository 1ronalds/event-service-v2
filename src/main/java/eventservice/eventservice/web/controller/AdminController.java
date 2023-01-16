package eventservice.eventservice.web.controller;

import eventservice.eventservice.business.service.AdminService;
import eventservice.eventservice.model.EventMinimalDto;
import eventservice.eventservice.swagger.HTTPResponseMessages;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin")
@Log4j2
public class AdminController {
    private final AdminService adminService;

    /**
     * Gets all usernames registered on server
     */
    @ApiOperation(value = "Finds all events including private with parameters")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTTPResponseMessages.HTTP_200),
            @ApiResponse(code = 500, message = HTTPResponseMessages.HTTP_500)
    })
    @GetMapping("/users")
    public ResponseEntity<ArrayList<String>> findAllUsernames(){
        log.info("Controller method findAllUsernames() called");
        return ResponseEntity.ok(adminService.findAllUsernames());
    }

    /**
     * Finds all events including private with given parameters
     * @param country
     * @param city
     * @param dateFrom
     * @param dateTo
     */
    @ApiOperation(value = "Finds all events including private with parameters")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTTPResponseMessages.HTTP_200),
            @ApiResponse(code = 500, message = HTTPResponseMessages.HTTP_500),
            @ApiResponse(code = 404, message = HTTPResponseMessages.HTTP_404)
    })
    @GetMapping("/events")
    public ResponseEntity<List<EventMinimalDto>> findAllEvents(@ApiParam(value = "country, where the event will take place", required = true)
                                                                     @RequestParam(name = "country") String country,
                                                                     @ApiParam(value = "city, where the event will take place")
                                                                     @RequestParam(name = "city", required = false) String city,
                                                                     @ApiParam(value = "The date from which events will take place")
                                                                     @RequestParam(name = "date_from", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate dateFrom,
                                                                     @ApiParam(value = "The date to which events will take place")
                                                                     @RequestParam(name = "date_to", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate dateTo){
        log.info("findAllPublicEvents controller method called with parameters " +
                "country: {}, city: {}, date_from: {}, date_to: {} ", country, city, dateFrom, dateTo);
        return ResponseEntity.ok(adminService.findAllEvents(country, city, dateFrom, dateTo));
    }

    /**
     * Changes role of user
     * @param username
     * @param role
     */
    @ApiOperation(value = "Change role of user")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = HTTPResponseMessages.HTTP_204),
            @ApiResponse(code = 500, message = HTTPResponseMessages.HTTP_500),
            @ApiResponse(code = 404, message = HTTPResponseMessages.HTTP_404)
    })
    @PutMapping("/change-role/{username}")
    public ResponseEntity<Void> changeRole(@ApiParam(value="Username whose role has to be changed") @PathVariable String username,
                                           @ApiParam(value="New role") @RequestParam(name = "role", required = true) String role){
        log.info("changeRole() controller method called");
        adminService.changeRole(username, role);
        return ResponseEntity.noContent().build();
    }
}
