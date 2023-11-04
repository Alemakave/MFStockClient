package ru.alemakave.xlsx_parser;

public class Main {
    public static void main(String[] args) {
        try {
            XLSX xlsx = new XLSX("C:\\Users\\Alemakave\\Desktop\\12.xlsx");
            System.out.println(xlsx);
            System.out.println(xlsx.getDocPropsCore().documentCreated.toString());
            System.out.println(xlsx.getDocPropsCore().documentModified.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}