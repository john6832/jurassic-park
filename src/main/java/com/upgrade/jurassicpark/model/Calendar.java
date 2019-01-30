package com.upgrade.jurassicpark.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(description = "Object encapsulating a list of days with the Jurassic Park availability")
public class Calendar {

    public Calendar(List<Day> days) {
        this.days = days;
    }

    @ApiModelProperty(value = "Days queried with Jurassic Park current availability")
    private List<Day> days;

    public List<Day> getDays() {
        return days;
    }

}
