package com.jameswaweru.springbootussd.dto.requests;

import lombok.Data;

@Data
public class FetchParkingFeeRequest {
    public int DurationID;
    private int VehicleTypeID;
}
