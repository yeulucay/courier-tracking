package com.migros.couriertracking.controller;

import com.migros.couriertracking.dto.TrackDto;
import com.migros.couriertracking.service.CourierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("courier")
@RestController
public class CourierController {

    @Autowired
    private CourierService courierService;
    private Logger logger = LoggerFactory.getLogger(CourierController.class);


    @PostMapping("track")
    public void TrackCourier(@RequestBody TrackDto dto){
        logger.info("courier/track", dto);
    }
}
