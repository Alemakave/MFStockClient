package ru.alemakave.xlsx_parser;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.w3c.dom.Element;

@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE
)
public class SheetCell {
    public String value;

    public SheetCell() {}

    public SheetCell(String cellValue) {
        value = cellValue;
    }

    public SheetCell(Workbook workbook, int valueIndex) {
        this.value = workbook.cellValues.get(valueIndex);
    }

    public String getValue() {
        return value;
    }

    public static SheetCell parse(Workbook workbook, Element xmlElement) {
        return new SheetCell(workbook, Integer.parseInt(xmlElement.getElementsByTagName("v").item(0).getTextContent()));
    }

    @Override
    public String toString() {
        return value;
    }
}
