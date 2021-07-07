package com.jameswaweru.springbootussd.services;

import com.jameswaweru.springbootussd.Utils.Utilities;
import com.jameswaweru.springbootussd.configs.AppConstants;
import com.jameswaweru.springbootussd.data.Menu;
import com.jameswaweru.springbootussd.data.MenuOption;
import com.jameswaweru.springbootussd.data.UssdSession;
import com.jameswaweru.springbootussd.dto.apiresponses.parking.ParkingFeeChargesApiResponse;
import com.jameswaweru.springbootussd.dto.requests.FetchParkingFeeRequest;
import com.jameswaweru.springbootussd.services.apiCalls.ParkingApi;
import org.apache.commons.text.StringSubstitutor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UssdRoutingService {

    @Autowired
    private MenuService menuService;

    @Autowired
    private SessionService sessionService; //using redis


    @Autowired
    ParkingApi parkingApi;

    private static final Logger LOGGER= LoggerFactory.getLogger(UssdRoutingService.class);

    /**
     * 
     * @param sessionId
     * @param serviceCode
     * @param phoneNumber
     * @param text
     * @return
     * @throws IOException
     */
    public String menuLevelRouter(String sessionId, String serviceCode, String phoneNumber, String text)
            throws IOException, JSONException {
        Map<String, Menu> menus = menuService.loadMenus();
        Map<String, String> variablesMap = new HashMap<>();
        variablesMap.put("message", "");



        UssdSession session = checkAndSetSession(sessionId, serviceCode, phoneNumber, text);


        LOGGER.info("session details: "+session.toString());

        //LOGGER.info("session variables: "+session.getSessionVariables());

        /**
         * Check if response has some value
         */
        if (text.length() > 0) {


            return getNextMenuItem(session, menus , text);
        }

        else {

            return replaceVariable(variablesMap,menus.get(session.getCurrentMenuLevel()).getText());
        }
    }

    /**
     * 
     * @param session
     * @param menus
     * @return
     * @throws IOException
     */
    public String getNextMenuItem(UssdSession session, Map<String, Menu> menus, String africastalkingText) throws IOException, JSONException {

        Map<String, String> variablesMap = new HashMap<>();
        variablesMap.put("message", "");

        String [] africasTalkingLevels = africastalkingText.split("\\*");
        String currentCustomerInputFromAfricastalking = africasTalkingLevels[africasTalkingLevels.length - 1];

        String[] levels = session.getText().split("\\*");
        String lastValue = levels[levels.length - 1];
        Menu menuLevel = menus.get(session.getCurrentMenuLevel()); //the previous menu customer was presented with

        LOGGER.info("lastValue:"+lastValue+" | menuMaxSelections:"+menuLevel.getMaxSelections()+"| currentMenuLevel:"
                +session.getCurrentMenuLevel()+" | inputType: "+menuLevel.getInputType());


        if(Utilities.isNumeric(currentCustomerInputFromAfricastalking)
                && Integer.parseInt(currentCustomerInputFromAfricastalking) > menuLevel.getMaxSelections()){
            variablesMap.put("message", "Invalid optionaa");
            return replaceVariable(variablesMap, menus.get(session.getCurrentMenuLevel()).getText());
        }

        if(menuLevel.getInputType().equals(AppConstants.SELECT_INPUT_TYPE)
                && !Utilities.isNumeric(currentCustomerInputFromAfricastalking)){
            variablesMap.put("message", "Invalid input");
            return replaceVariable(variablesMap, menus.get(session.getCurrentMenuLevel()).getText());
        }



        if(currentCustomerInputFromAfricastalking.equals(AppConstants.BACK_OPTION)){

            session.setCurrentMenuLevel(session.getPreviousMenuLevel());
            UssdSession currentSession =  updateSessionMenuLevel(session ,session.getPreviousMenuLevel());

            return  replaceVariable(variablesMap, getMenu(currentSession.getPreviousMenuLevel()));
        }

        if(currentCustomerInputFromAfricastalking.equals(AppConstants.BACK_TO_HOME_OPTION)){
            session.setCurrentMenuLevel("1");
            session =  updateSessionMenuLevel(session ,"1");
            return  replaceVariable(variablesMap, getMenu("1"));
        }




        if (menuLevel.getInputType().equals(AppConstants.SELECT_INPUT_TYPE)
                &&  Integer.parseInt(lastValue) <= menuLevel.getMaxSelections()) {
            MenuOption menuOption = menuLevel.getMenuOptions().get(Integer.parseInt(lastValue) - 1);
            saveCustomerInput(session,currentCustomerInputFromAfricastalking, menuLevel);
            return  replaceVariable( variablesMap, processMenuOption(session, menuOption, menuLevel));
        }

        if(menuLevel.getInputType().equals(AppConstants.TEXT_INPUT_TYPE)){

            if(levels.length > 2){
                String previousValue = levels[levels.length - 2];
                LOGGER.info("lastValue:"+lastValue+" | previousValue:"+previousValue);
                saveCustomerInput(session,currentCustomerInputFromAfricastalking, menuLevel);
                MenuOption menuOption = new MenuOption();
                if(menuLevel.getMenuOptions().size() > 1){
                    menuOption = menuLevel.getMenuOptions().get(Integer.parseInt(previousValue) - 1);
                    return  replaceVariable( getVariablesMap(
                            menuOption.getNextMenuLevel(),session),
                            processMenuOption(session, menuOption, menuLevel)
                    );
                }
            }


            LOGGER.info("Hey seee me...myNextMenuLevel is:"+menuLevel.getMenuOptions().get(0).getNextMenuLevel());

            return  replaceVariable(getVariablesMap(menuLevel.getMenuOptions().get(0).getNextMenuLevel(), session), getMenu(menuLevel.getMenuOptions().get(0).getNextMenuLevel()));
        }

        variablesMap.put("message", "Invalid optiondd");
        return replaceVariable(variablesMap, menus.get(session.getCurrentMenuLevel()).getText());           //menus.get(session.getCurrentMenuLevel()).getText();
    }

    private Map<String, String> getVariablesMap(String menuLevel, UssdSession session) throws IOException, JSONException {
        Map<String, Menu> menus = menuService.loadMenus();
        Menu menuLevelDetails = menus.get(menuLevel);
        LOGGER.info("Fetching variable for menu level : "+menuLevel);
        Map<String, String> variables = new HashMap<>();
        if(!Utilities.isEmpty(menuLevelDetails.getExternalAction())
                &&  menuLevelDetails.getExternalAction().equals(AppConstants.FETCH_PARKING_FEE_CHARGES_ACTIONS)){

            FetchParkingFeeRequest request = new FetchParkingFeeRequest();
            request.setDurationID(1);
            request.setVehicleTypeID(1);
            ParkingFeeChargesApiResponse apiResponse = parkingApi.FetchParkingFeeCharges(request);
            LOGGER.info("Api response .."+apiResponse.toString());
            if(apiResponse.getStatus() == 1){
                variables.put("amount" , apiResponse.getResponse_data().getAmount());
            }

            String carRegDetails = "";
            JSONObject objectWithCarReg = Utilities.getTheObjectWithNeededValue(new JSONArray(session.getSessionVariables()),"carRegDetails", LOGGER);
            LOGGER.info("Returned car object:"+objectWithCarReg.toString());
            if(objectWithCarReg.has("carRegDetails")){
                carRegDetails = objectWithCarReg.getString("carRegDetails");
            }
            variables.put("carRegDetails", carRegDetails);
            variables.put("message", "");
            return variables;
        }
        return variables;
    }

    private void saveCustomerInput(UssdSession session, String customerInput, Menu menu) throws JSONException {
        String currentSessionVariables = session.getSessionVariables();

        LOGGER.info("Checking menu level .."+menu.getMenuLevel());

        if(menu.getMenuLevel().equals(AppConstants.SELECT_VEHICLE_CATEGORY_LEVEL)){
            currentSessionVariables = saveSessionVariable(session, "vehicleCategory" , customerInput);
        }

        if(menu.getMenuLevel().equals(AppConstants.ENTER_VEHICLE_REG_DETAILS_MENU_LEVEL)
                || menu.getMenuLevel().equals(AppConstants.ENTER_VEHICLE_REG_DETAILS_TO_CHECK_PARKING_STATUS_MENU_LEVEL) ){
            currentSessionVariables = saveSessionVariable(session, "carRegDetails" , customerInput);
        }

        if(menu.getMenuLevel().equals(AppConstants.ENTER_BUSINESS_ID_MENU_LEVEL)){
            currentSessionVariables = saveSessionVariable(session, "businessId" , customerInput);
        }

        if(menu.getMenuLevel().equals(AppConstants.ENTER_BILL_NUMBER_MENU_LEVEL)){
            currentSessionVariables = saveSessionVariable(session, "billNumber" , customerInput);
        }

        LOGGER.info("Current session details "+currentSessionVariables);
    }

    /**
     * 
     * @param menuLevel
     * @return
     * @throws IOException
     */
    public String getMenu(String menuLevel) throws IOException {
        Map<String, Menu> menus = menuService.loadMenus();
        return menus.get(menuLevel).getText();
    }

    /**
     * 
     * @param menuOption
     * @return
     * @throws IOException
     */
    public String processMenuOption(UssdSession session, MenuOption menuOption, Menu menuLevel) throws IOException {
        LOGGER.info("checking menu option type ..."+menuOption.getType());
        if (menuOption.getType().equals("response")) {
            return processMenuOptionResponses(session, menuOption, menuLevel);
        } else if (menuOption.getType().equals("level")) {
            updateSessionMenuLevel(session, menuOption.getNextMenuLevel());
            return getMenu(menuOption.getNextMenuLevel());
        } else {
            return "CON ";
        }
    }

    public String saveSessionVariable(UssdSession session , String key, String value)
            throws JSONException {

        String currentVariables = session.getSessionVariables();
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        if(Utilities.isEmpty(currentVariables)){
            LOGGER.info("Session variables empty..");
            jsonObject.put(key, value);
            jsonArray.put(jsonObject);
            session.setSessionVariables(jsonArray.toString());
            sessionService.update(session);
            //ussdSessionRepo.save(session);
            LOGGER.info("Updated session details.."+session.toString());
            return session.getSessionVariables();
        }

        LOGGER.info("Session variables not empty..");

        jsonArray = new JSONArray(currentVariables);
        jsonObject.put(key, value);

        jsonArray = Utilities.checkObjectDuplicates(jsonArray, key, LOGGER);

        LOGGER.info("Returned json array after checking duplicates :"+ jsonArray.toString());

        jsonArray.put(jsonObject);
        session.setSessionVariables(jsonArray.toString());
        sessionService.update(session);
       // ussdSessionRepo.save(session);
        LOGGER.info("Updated session details.."+session.toString());

        return session.getSessionVariables().toString();


    }

    /**
     * 
     * @param menuOption
     * @return
     */
    public String processMenuOptionResponses(UssdSession session, MenuOption menuOption, Menu menuLevel) throws IOException {

        LOGGER.info("Processing menu option response ...");

        String response = menuOption.getResponse();
        Map<String, String> variablesMap = new HashMap<>();

        if(menuLevel.getExternalAction().equals(AppConstants.FETCH_PARKING_FEE_CHARGES_ACTIONS)){

            LOGGER.info("fetching parking fee from api ...");

            updateSessionMenuLevel(session, menuOption.getNextMenuLevel());
            variablesMap.put("message","Have fetched car details");
            return  replaceVariable(variablesMap, getMenu(menuOption.getNextMenuLevel()));
        }

        return  replaceVariable(variablesMap, getMenu(menuOption.getNextMenuLevel()));
    }

    /**
     * 
     * @param variablesMap
     * @param response
     * @return
     */
    public String replaceVariable(Map<String, String> variablesMap, String response) {
        StringSubstitutor sub = new StringSubstitutor(variablesMap);
        return sub.replace(response);
    }

    /**
     * 
     * @param session
     * @param menuLevel
     * @return
     */
    public UssdSession updateSessionMenuLevel(UssdSession session, String menuLevel) {
        LOGGER.info("Updating menu level: set setPreviousMenuLevel to :"+session.getCurrentMenuLevel()+" | set  setCurrentMenuLevel to :"+menuLevel);
        session.setPreviousMenuLevel(session.getCurrentMenuLevel());
        session.setCurrentMenuLevel(menuLevel);
        return sessionService.update(session);
        //return ussdSessionRepo.save(session);
    }

    /**
     * Check, Set or update the existing session with the provided Session Id
     * 
     * @param sessionId
     * @param serviceCode
     * @param phoneNumber
     * @param text
     * @return
     */
    public UssdSession checkAndSetSession(String sessionId, String serviceCode, String phoneNumber, String text) throws IOException {
        UssdSession session = sessionService.get(sessionId);
        //UssdSession session = ussdSessionRepo.findBySessionId(sessionId);

        if (session != null ){

            LOGGER.info("Session before editing:"+session.toString());

            if(!Utilities.isEmpty(session.getText()) && !Utilities.isEmpty(text)){
                String[] levels = session.getText().split("\\*");
                String[] receivedTextLevels = text.split("\\*");
                String currentInput = receivedTextLevels[receivedTextLevels.length - 1];
                String previousInputInMySessionString = levels[levels.length - 1];


                if(levels.length > 0  && Utilities.isNumeric(currentInput)){


                    if(currentInput.equals(AppConstants.BACK_TO_HOME_OPTION)){
                        session.setText("");
                        return sessionService.update(session);
                        //return ussdSessionRepo.save(session);
                    }

                    if(currentInput.equals(AppConstants.BACK_OPTION) && levels.length == 1){
                        session.setText("");
                        session.setCurrentMenuLevel("1");
                        session.setPreviousMenuLevel("1");
                        return sessionService.update(session);
                        //return ussdSessionRepo.save(session);
                    }

                    if(currentInput.equals(AppConstants.BACK_OPTION)){

                        //getting the option had selected
                        Menu menuLevel = menuService.loadMenus().get(session.getCurrentMenuLevel());
                        List<MenuOption> menuOptions = menuLevel.getMenuOptions();
                        LOGGER.info("User previous input:"+session.getText().substring(session.getText().length() - 1)+" menuLevel:"+session.getCurrentMenuLevel()+"|prevMenuLevel:"+menuLevel.getMenuPrevLevel());
                        //LOGGER.info("menuOptions:"+menuOptions.toString());
                        LOGGER.info("Session text before truncating .."+session.getText());
                        LOGGER.info("Truncating session text to .."+session.getText().substring(0, session.getText().length() - 2));
                        session.setPreviousMenuLevel(menuLevel.getMenuPrevLevel());
                        session.setText(session.getText().substring(0, session.getText().length() - 2));
                        return sessionService.update(session);
                        //return ussdSessionRepo.save(session);
                    }
                }

            }

            String[] receivedTextLevels = text.split("\\*");
            String currentInput = receivedTextLevels[receivedTextLevels.length - 1];
            if(Utilities.isEmpty(session.getText()) && Utilities.isNumeric(currentInput)){
                LOGGER.info("There is session but string is empty..");
                session.setText(currentInput);
                return sessionService.update(session);
               // return ussdSessionRepo.save(session);
            }

            LOGGER.info("There is session but string is not empty..string length:"+session.getText().length()+", this is the text:"+session.getText());
            if(Utilities.isNumeric(currentInput)){
                session.setText(session.getText()+"*"+currentInput);
            }

            return sessionService.update(session);
            //return ussdSessionRepo.save(session);
        }


        session = new UssdSession();
        session.setCurrentMenuLevel("1");
        session.setPreviousMenuLevel("0");
        session.setId(sessionId);
//        session.setSessionId(sessionId);
        session.setPhoneNumber(phoneNumber);
        session.setServiceCode(serviceCode);
        session.setText(text);

        return sessionService.createUssdSession(session);
        //return ussdSessionRepo.save(session);
    }
}
