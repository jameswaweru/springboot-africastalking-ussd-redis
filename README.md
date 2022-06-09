# springboot-africastalking-ussd-redis
This is a ussd application done in springboot &amp; redis for session management. 
I used africastalking for testing

I have replicated kakamega's revenue collection ussd flows

# Testing this api
Ensure you have redis installed

To install redis on windows you can refer to this video 
https://www.youtube.com/watch?v=188Fy-oCw4w 
or  Microsoft's archive repo https://github.com/microsoftarchive/redis/releases

Also set your parking api base url , whose sample is as bellow response 
{
  "status": 1,
  "message": "Success",
  "response_data": {
    "ChargeID": "2",
    "VehicleType": "Bus",
    "Amount": "300.00",
    "FeeId": "118"
  }
}

# Testing with postman
- Build/ run the application
- Testing endpoint: http://localhost:8087/ussd (POST requests)
- Testing payload
  {
      "sessionId":1212889999899992,
      "serviceCode":"*223#",
      "phoneNumber":254726765977,
      "text":"0"
  }
- ensure sessionId is unique if you have a new testing session

This is a test to \
create  a PR
