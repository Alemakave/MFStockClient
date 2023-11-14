package ru.alemakave.mfstock.model.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.DateFormat;
import java.util.Date;

public class DateTimeJson {
    @JsonProperty("dateTime")
    private String dateTimeString;

    @JsonCreator
    public DateTimeJson(@JsonProperty("dateTime") String dateTimeString) {
        this.dateTimeString = dateTimeString;
    }

    @JsonCreator(mode = JsonCreator.Mode.DISABLED)
    public DateTimeJson(Date date, DateFormat dateFormat) {
        this.dateTimeString = dateFormat.format(date);
    }

    public String getDateTimeString() {
        return dateTimeString;
    }

    @Override
    public String toString() {
        return "{" +
                "dateTimeString='" + dateTimeString + '\'' +
                '}';
    }
}
