package com.jameswaweru.springbootussd.dto.apiresponses.parking;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResponseData {
    @JsonProperty("ChargeID")
    public String chargeID;

    @JsonProperty("VehicleType")
    public String vehicleType;

    @JsonProperty("Amount")
    public String amount;

    @JsonProperty("FeeId")
    public String feeId;
}
