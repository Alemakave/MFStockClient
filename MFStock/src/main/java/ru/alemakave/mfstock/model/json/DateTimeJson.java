package ru.alemakave.mfstock.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.nio.file.attribute.FileTime;
import java.sql.Date;
import java.text.DateFormat;

public class DateTimeJson {
    @JsonProperty("dateTime")
    private String dateTimeString;

    public String getDateTimeString() {
        return dateTimeString;
    }

    public void setDateTimeString(FileTime time, DateFormat dateFormat) {
        this.dateTimeString = dateFormat.format(time);
    }

    @Override
    public String toString() {
        return "{" +
                "dateTimeString='" + dateTimeString + '\'' +
                '}';
    }
}
