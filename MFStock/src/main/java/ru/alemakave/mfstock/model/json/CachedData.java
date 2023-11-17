package ru.alemakave.mfstock.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.alemakave.xlsx_parser.SheetData;

public class CachedData {
    private final String dateTime;
    private final SheetData data;

    public CachedData(@JsonProperty("dateTime") String dateTime, @JsonProperty("data") SheetData data) {
        this.dateTime = dateTime;
        this.data = data;
    }

    public String getDateTime() {
        return dateTime;
    }

    public SheetData getData() {
        return data;
    }
}
