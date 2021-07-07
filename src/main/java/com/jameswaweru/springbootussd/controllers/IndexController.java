package com.jameswaweru.springbootussd.controllers;


import com.jameswaweru.springbootussd.data.Menu;
import com.jameswaweru.springbootussd.dto.UssdSessionRequest;
import com.jameswaweru.springbootussd.services.MenuService;
import com.jameswaweru.springbootussd.services.UssdRoutingService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@CrossOrigin("*")
public class IndexController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private UssdRoutingService ussdRoutingService;

    /**
     *
     * @return
     * @throws IOException
     */
    @GetMapping(path = "menus")
    public Map<String, Menu> menusLoad() throws IOException {
        return menuService.loadMenus();
    }

    /**
     *
     * @return
     * @throws IOException
     */
    @GetMapping(path = "")
    public String index() throws IOException {
        return "Your have reached us";
    }


    /**
     *
     * @param ussdSessionRequest
     * @return
     * @throws IOException
     */
    @PostMapping(path = "ussd")
    public String ussdIngress(@RequestBody UssdSessionRequest ussdSessionRequest) throws IOException {
        try {
            return ussdRoutingService.menuLevelRouter(
                    ussdSessionRequest.getSessionId(),
                    ussdSessionRequest.getServiceCode(), ussdSessionRequest.getPhoneNumber(),
                    ussdSessionRequest.getText());
        } catch (IOException | JSONException e) {
            return "END " + e.getMessage();
        }
    }
}
