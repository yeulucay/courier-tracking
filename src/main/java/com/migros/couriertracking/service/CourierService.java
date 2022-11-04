package com.migros.couriertracking.service;

import com.migros.couriertracking.data.CourierData;
import com.migros.couriertracking.data.StoreData;
import com.migros.couriertracking.dto.TrackDto;
import com.migros.couriertracking.model.WayPoint;
import com.migros.couriertracking.util.GeoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class CourierService {

    private Logger logger = LoggerFactory.getLogger(CourierService.class);

    /**
     * Add track to courier's tracks list
     * Then, finds the nearest store from the StoreData. It uses KDTree data structure.
     * When it finds the nearest, then gets the distance with it.
     * CourierData structure holds courier info, like track history and last closed store and time.
     * */
    public void trackCourier(TrackDto dto) throws Exception  {
        WayPoint courierPoint = new WayPoint(dto.courier(), dto.lat(), dto.lng());

        try {
            CourierData.getInstance().addTrack(courierPoint);
        } catch(Exception ex) {
            logger.error("error while storing courier track data", ex);
            throw new Exception("internal server error");
        }

        // nearest store to the courier's current location
        WayPoint nearest = StoreData.getInstance().getNearest(courierPoint);

        // distance to nearest in KM
        double distance = GeoUtil.getInstance().getDistance(
                dto.lat(),
                dto.lng(),
                nearest.lat(),
                nearest.lng());

        CourierData.CourierInfo courierInfo = CourierData.getInstance().getInfo(dto.courier());

        if ((distance <= 0.1 && courierInfo == null) ||
                (distance <= 0.1 && courierInfo != null
                        && (courierInfo.lastStore() != nearest.name() || courierInfo.lastEntrance().plusSeconds(60).isBefore(Instant.now())))) {
            // if courier is close to any store
            logger.info(String.format("courier %s is close to: %s", dto.courier(), nearest.name()));

            try {
                CourierData.getInstance().setInfo(dto.courier(), new CourierData.CourierInfo(nearest.name(), Instant.now()));
            } catch(Exception ex) {
                //TODO: rollback or any backup is required
                logger.error("error while storing courier info", ex);
                throw new Exception("internal server error");
            }
        }
    }

    /**
     * Brings courier travel distance.
     * First brings all track list from CourierData.
     * Then, calculates total travel distance.
     * */
    public double courierTravel(String courier) {
        double totalDistance = 0.0;
        List<WayPoint> tracks = CourierData.getInstance().getTrackList(courier);
        if (tracks.size() < 2) {
            return 0.0;
        }

        for(int i=1; i<tracks.size(); i++) {
            WayPoint prev = tracks.get(i-1);
            WayPoint current = tracks.get(i);
            double distance = GeoUtil.getInstance().getDistance(
                    prev.lat(),
                    prev.lng(),
                    current.lat(),
                    current.lng());
            totalDistance += distance;
        }

        return totalDistance;
    }
}
