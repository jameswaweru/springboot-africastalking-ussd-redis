package com.jameswaweru.springbootussd.dto.apiresponses.parking;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ParkingFeeChargesApiResponse {
    @JsonProperty("status")
    public int status;

    @JsonProperty("message")
    public String message;

    @JsonProperty("response_data")
    public ResponseData response_data;
}
