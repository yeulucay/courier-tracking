# Courier Tracking

The API stores courier tracks and logs if the courier comes closer 
to any store in the **resources/stores.json**

K-Dimensional Tree structure is used to store the 'Stores Data' in memory 
to be able to search fast in case large amount of data.

All data storage is in **CourierData** and **StoreData** classes. 

| Path                         | Description                     |
|------------------------------|---------------------------------|
 | POST /courier/track          | Creates new track for courier   |
| GET /courier/track/{courier} | Gets total distance for courier |

**p.s.** If the courier gets into 100m radius of any store, system logs the courier and store info.

## Test

1st track to store
```shell
curl --location --request POST 'http://localhost:8080/courier/track' \
--header 'Content-Type: application/json' \
--data-raw '{
    "courier": "courier1",
    "lat": 40.9923304,
    "lng": 29.1244227
}'
```
2nd track to store
```shell
curl --location --request POST 'http://localhost:8080/courier/track' \
--header 'Content-Type: application/json' \
--data-raw '{
    "courier": "courier1",
    "lat": 40.986109,
    "lng":29.1161291
}'
```
Get distance
```shell
curl --location --request GET 'http://localhost:8080/courier/track/courier1'
```