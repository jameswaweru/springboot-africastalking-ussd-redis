package com.jameswaweru.springbootussd.services.apiCalls;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.jameswaweru.springbootussd.configs.ApplicationProperties;
import com.jameswaweru.springbootussd.dto.apiresponses.parking.ParkingFeeChargesApiResponse;
import com.jameswaweru.springbootussd.dto.requests.FetchParkingFeeRequest;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class ParkingApiImplementation implements ParkingApi{

    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;

    Logger logger = LoggerFactory.getLogger(ParkingApiImplementation.class);


    public ParkingApiImplementation(OkHttpClient okHttpClient, ObjectMapper objectMapper){
        this.okHttpClient = okHttpClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public ParkingFeeChargesApiResponse FetchParkingFeeCharges(FetchParkingFeeRequest payload) {

//        {{base_url}}/api/parking/getcharge?DurationID=1&VehicleTypeID=2

        Request request = new Request.Builder()
                .url(String.format("%s?DurationID=%s&VehicleTypeID=%s", ApplicationProperties.parkingApi+"/api/parking/getcharge", payload.getDurationID(),payload.getVehicleTypeID()))
                .get()
                .build();


        logger.info("request string ..."+request.toString());


        try {
            Response response = okHttpClient.newCall(request).execute();

            assert response.body() != null;

            // use Jackson to Decode the ResponseBody ...
            return objectMapper.readValue(response.body().string(), ParkingFeeChargesApiResponse.class);
        } catch (IOException e) {
            log.error(String.format("Could not get access token. -> %s", e.getLocalizedMessage()));
            return null;
        }

    }

}
