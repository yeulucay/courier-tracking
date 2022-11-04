package com.migros.couriertracking.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.migros.couriertracking.util.KdTree;
import com.migros.couriertracking.model.Store;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class StoreData {

    private static StoreData instance = null;
    private KdTree tree;

    private StoreData(){}

    public static synchronized StoreData getInstance() {
        if (instance == null) {
            instance = new StoreData();
        }
        File storeFile = new File(
            instance.getClass().getClassLoader().getResource("stores.json").getFile()
        );



//        instance.tree = new KdTree();
        return instance;
    }

    private void readStores() {
        try(InputStream in=Thread.currentThread().getContextClassLoader().getResourceAsStream("YourJsonFile")){
            ObjectMapper mapper = new ObjectMapper();
            Store[] stores = mapper.readValue(in, Store[].class);

            for (Store s: stores) {

            }

        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
