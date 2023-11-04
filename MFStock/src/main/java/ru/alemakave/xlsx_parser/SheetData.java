package ru.alemakave.xlsx_parser;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE
)
public class SheetData {
    private Workbook workbook;
    public ArrayList<SheetRow> rows;

    public SheetData() {}

    public SheetData(Workbook workbook, ArrayList<SheetRow> rows) {
        this.workbook = workbook;
        this.rows = rows;
    }

    public String getValue(int row, int column) {
        return rows.get(row).getCell(column).getValue();
    }

    public static SheetData parse(Workbook workbook, Element xmlElement) {
        ArrayList<SheetRow> rows = new ArrayList<>();

        NodeList rowList = xmlElement.getElementsByTagName("row");
        for (int i = 0; i < rowList.getLength(); i++) {
            rows.add(SheetRow.parse(workbook, (Element)rowList.item(i)));
        }

        return new SheetData(workbook, rows);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (SheetRow row : rows) {
            sb.append(row).append("\n");
        }

        return sb.substring(0, sb.length()-1);
    }
}
