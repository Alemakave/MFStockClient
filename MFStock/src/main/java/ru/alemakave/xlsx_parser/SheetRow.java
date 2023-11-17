package ru.alemakave.xlsx_parser;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Objects;

@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE
)
public class SheetRow {
    private Workbook workbook;
    public ArrayList<SheetCell> cells;

    public SheetRow() {}

    public SheetRow(Workbook workbook, ArrayList<SheetCell> cells) {
        this.workbook = workbook;
        this.cells = cells;
    }

    public SheetCell getCell(int column) {
        return cells.get(column);
    }

    public static SheetRow parse(Workbook workbook, Element xmlElement) {
        ArrayList<SheetCell> cells = new ArrayList<>();

        NodeList cellList = xmlElement.getElementsByTagName("c");
        for (int i = 0; i < cellList.getLength(); i++) {
            cells.add(SheetCell.parse(workbook,(Element)cellList.item(i)));
        }

        return new SheetRow(workbook, cells);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SheetRow)) return false;

        SheetRow sheetRow = (SheetRow) o;

        if (!Objects.equals(workbook, sheetRow.workbook)) return false;
        return Objects.equals(cells, sheetRow.cells);
    }

    @Override
    public int hashCode() {
        int result = workbook != null ? workbook.hashCode() : 0;
        result = 31 * result + (cells != null ? cells.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (SheetCell cell : cells) {
            sb.append(cell).append("|");
        }

        return sb.substring(0, sb.length()-1);
    }
}
