package com.migros.couriertracking.controller;

import com.migros.couriertracking.dto.TrackDto;
import com.migros.couriertracking.response.DistanceResponse;
import com.migros.couriertracking.response.MessageResponse;
import com.migros.couriertracking.service.CourierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("courier")
@RestController
public class CourierController {

    @Autowired
    private CourierService courierService;

    @PostMapping("track")
    public ResponseEntity trackCourier(@RequestBody TrackDto dto){
        try {
            courierService.trackCourier(dto);
            return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("track has been created"));

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse(ex.getMessage()));
        }
    }

    @GetMapping("track/{courier}")
    public ResponseEntity getDistance(@PathVariable String courier) {
        try {
            double totalTravel = courierService.courierTravel(courier);
            return ResponseEntity.status(HttpStatus.OK).body(new DistanceResponse(totalTravel, "Kilometer"));

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse(ex.getMessage()));
        }
    }
}
