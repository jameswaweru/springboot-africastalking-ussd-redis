package com.jameswaweru.springbootussd.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MenuOption {

    private String type;

    private String response;

    private String optionInfo;

    @JsonProperty("next_menu_level")
    private String nextMenuLevel;

    @JsonProperty("previousMenuLevel")
    private String previousMenuLevel;

    //private MenuOptionAction action;

    private String inputValue;

}
