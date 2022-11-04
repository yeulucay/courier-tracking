package com.migros.couriertracking.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migros.couriertracking.util.KDTree;
import com.migros.couriertracking.model.WayPoint;

import java.io.InputStream;
import java.util.Arrays;

public class StoreData {

    private static StoreData instance;
    private KDTree tree;
    KDTree.KDTNode root;

    private StoreData(){}

    public static synchronized StoreData getInstance() {
        if (instance == null) {
            instance = new StoreData();

            WayPoint[] wayPoints = instance.readStores();
            instance.tree = new KDTree();
            instance.root = instance.tree.createTree(Arrays.asList(wayPoints));
        }

        return instance;
    }

     public WayPoint getNearest(WayPoint wp){
        return this.tree.findNearestWp(this.root, wp);
     }

    private WayPoint[] readStores() {
        try(InputStream in=Thread.currentThread().getContextClassLoader().getResourceAsStream("stores.json")){
            ObjectMapper mapper = new ObjectMapper();
            WayPoint[] wayPoints = mapper.readValue(in, WayPoint[].class);

            return wayPoints;
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
