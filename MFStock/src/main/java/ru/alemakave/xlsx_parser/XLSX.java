package ru.alemakave.xlsx_parser;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static ru.alemakave.slib.utils.XMLUtils.readXMLDocumentFromInputStream;

public class XLSX {
    private ZipFile xlsxFile;
    private ArrayList<ZipEntry> entries = new ArrayList<>();
    private Workbook workbook;
    private DocProps docPropsCore;

    public XLSX(String filePath) throws Exception {
        this(new File(filePath));
    }

    public XLSX(File file) throws Exception {
        this.xlsxFile = new ZipFile(file);
        try {
            load();
            findWorkbook();
            findDocProps();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean findDocProps() throws Exception {
        for (ZipEntry entry : entries) {
            if (entry.getName().equals("docProps/core.xml")) {
                docPropsCore = new DocProps(xlsxFile.getInputStream(entry));
                return true;
            }
        }

        return false;
    }

    private boolean findWorkbook() throws Exception {
        for (ZipEntry entry : entries) {
            if (entry.getName().equals("xl/workbook.xml")) {
                workbook = new Workbook(xlsxFile.getInputStream(entry));
                workbook.cellValues = getCellsValue();
                workbook.sheets.addAll(getSheets(workbook.getSheetsID()));
                return true;
            }
        }

        return false;
    }

    private ArrayList<String> getCellsValue() throws Exception {
        ArrayList<String> result = new ArrayList<>();

        for (ZipEntry entry : entries) {
            if (entry.getName().equals("xl/sharedStrings.xml")) {
                Document sharedStringsDocument = readXMLDocumentFromInputStream(xlsxFile.getInputStream(entry));
                NodeList nodes = sharedStringsDocument.getElementsByTagName("t");
                for (int i = 0; i < nodes.getLength(); i++) {
                    result.add(nodes.item(i).getTextContent());
                }
                break;
            }
        }

        return result;
    }

    private ArrayList<Sheet> getSheets(int[] ids) throws IOException {
        ArrayList<Sheet> result = new ArrayList<>();

        for (ZipEntry entry : entries) {
            for (int id : ids) {
                if (entry.getName().equals("xl/worksheets/sheet" + id + ".xml")) {
                    result.add(new Sheet(workbook, xlsxFile.getInputStream(entry)));
                }
            }
        }

        return result;
    }

    @Deprecated
    private void load() throws IOException {
        Enumeration<? extends ZipEntry> entries = xlsxFile.entries();

        while (entries.hasMoreElements()) {
            this.entries.add(entries.nextElement());
        }
    }

    public Workbook getWorkbook() {
        return workbook;
    }

    public DocProps getDocPropsCore() {
        return docPropsCore;
    }

    @Override
    public String toString() {
        return workbook.toString();
    }
}
