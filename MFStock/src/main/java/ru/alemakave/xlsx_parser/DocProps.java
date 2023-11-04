package ru.alemakave.xlsx_parser;

import org.w3c.dom.Document;

import java.io.InputStream;
import java.time.LocalDateTime;

import static ru.alemakave.slib.utils.XMLUtils.readXMLDocumentFromInputStream;

public class DocProps {
    public String documentCreator;
    public String documentLastModifiedBy;
    public LocalDateTime documentCreated;
    public LocalDateTime documentModified;

    public DocProps(InputStream dataStream) {
        try {
            load(dataStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void load(InputStream stream) throws Exception {
        Document workbookDocument = readXMLDocumentFromInputStream(stream);
        documentCreator = workbookDocument.getElementsByTagName("dc:creator").item(0).getTextContent();
        documentLastModifiedBy = workbookDocument.getElementsByTagName("cp:lastModifiedBy").item(0).getTextContent();
    }
}
