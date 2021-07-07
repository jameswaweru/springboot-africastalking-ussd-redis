package com.jameswaweru.springbootussd.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Menu {
    @JsonProperty("id")
    private String id;

    @JsonProperty("menu_level")
    private String menuLevel;

    @JsonProperty("menu_prev_level")
    private String menuPrevLevel;


    @JsonProperty("text")
    private String text;

    @JsonProperty("menu_options")
    private List<MenuOption> menuOptions;

    @JsonProperty("action")
    private String action;

    @JsonProperty("max_selections")
    private Integer maxSelections;

    @JsonProperty("inputType")
    private String inputType;

    @JsonProperty("externalAction")
    private String externalAction;

}
