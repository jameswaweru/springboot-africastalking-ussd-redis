package com.jameswaweru.springbootussd.dto;

import lombok.Data;

@Data
public class UssdSessionRequest {
    private String sessionId;
    private String serviceCode;
    private String phoneNumber;
    private String text;
}
