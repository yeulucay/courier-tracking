package com.migros.couriertracking.data;

import com.migros.couriertracking.model.WayPoint;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

/*
* Courier Singleton Repo
*/
public class CourierData {
    private static CourierData instance = null;

    private Map<String, List<WayPoint>> courierTracks;
    private Map<String, CourierInfo> courierInfo;

    private CourierData(){}

    public static synchronized CourierData getInstance() {

        if (instance == null) {
            instance = new CourierData();
        }
        return instance;
    }

    public void addTrack(WayPoint wp) {
        String name = String.valueOf(wp.name());
        List<WayPoint> tracks = courierTracks.get(name);
        tracks.add(wp);
        courierTracks.put(name, tracks);
    }

    public List<WayPoint> getTrackList(String name) {
        return courierTracks.get(name);
    }

    public CourierInfo getInfo(String name) {
        return courierInfo.get(name);
    }

    public void setInfo(String name, CourierInfo info) {
        courierInfo.put(name, info);
    }

    /**
     * Courier Info stores the last visited store of the courier and its time.
     * */
    public record CourierInfo(String lastStore, Instant lastEntrance) { }
}
