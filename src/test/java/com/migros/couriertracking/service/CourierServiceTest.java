package com.migros.couriertracking.service;

import com.migros.couriertracking.data.StoreData;
import com.migros.couriertracking.model.WayPoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CourierServiceTest {

    @Autowired
    private CourierService courierService;

    @Test
    void testFindNearestStore(){
        // a waypoint near Ortakoy MMM Migros
        WayPoint pt = new WayPoint("a point", 41.055784, 29.0210295);
        WayPoint nearest = StoreData.getInstance().getNearest(pt);

        assertEquals(nearest.name(), "Ortaköy MMM Migros");
    }
}
