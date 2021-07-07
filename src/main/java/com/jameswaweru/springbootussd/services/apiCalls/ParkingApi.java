package com.jameswaweru.springbootussd.services.apiCalls;


import com.jameswaweru.springbootussd.dto.apiresponses.parking.ParkingFeeChargesApiResponse;
import com.jameswaweru.springbootussd.dto.requests.FetchParkingFeeRequest;

public interface ParkingApi {

    ParkingFeeChargesApiResponse FetchParkingFeeCharges(FetchParkingFeeRequest request);

}
