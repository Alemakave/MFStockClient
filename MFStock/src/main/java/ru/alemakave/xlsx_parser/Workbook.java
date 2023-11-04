package ru.alemakave.xlsx_parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;

import static ru.alemakave.slib.utils.XMLUtils.readXMLDocumentFromInputStream;

public class Workbook {
    private int[] sheetsID;
    public ArrayList<Sheet> sheets = new ArrayList<>();
    public ArrayList<String> cellValues = new ArrayList<>();

    public Workbook(InputStream dataStream) {
        try {
            load(dataStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void load(InputStream stream) throws Exception {
        Document workbookDocument = readXMLDocumentFromInputStream(stream);
        NodeList nodes = workbookDocument.getElementsByTagName("sheets").item(0).getChildNodes();
        sheetsID = new int[nodes.getLength()];
        for (int i = 0; i < nodes.getLength(); i++){
            sheetsID[i] = Integer.parseInt(((Element)nodes.item(i)).getAttribute("sheetId"));
        }
    }

    public int[] getSheetsID() {
        return sheetsID;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Sheet");

        for (int i = 0; i < sheetsID.length; i++) {
            sb.append(String.format("[%d]:\n", sheetsID[i]));

            Sheet sheet = sheets.get(i);
            sb.append(sheet.toString()).append("\n\n");
        }

        return sb.substring(0, sb.length()-2);
    }
}
