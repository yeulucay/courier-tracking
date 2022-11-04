package com.migros.couriertracking.data;

/*
* Courier Singleton Repo
*/
public class CourierData {
    private static CourierData instance = null;

    private CourierData(){}

    public static synchronized CourierData getInstance() {

        if (instance == null) {
            instance = new CourierData();
        }
        return instance;
    }
}
