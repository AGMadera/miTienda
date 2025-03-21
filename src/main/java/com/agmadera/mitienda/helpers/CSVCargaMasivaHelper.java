package com.agmadera.mitienda.helpers;

import com.agmadera.mitienda.entities.ProductoEntity;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CSVCargaMasivaHelper {
    public static String TYPE = "text/csv";
    static String[] HEADERs = { "id", "nombre", "calidad", "marco" };

    public static boolean hasCSVFormat(MultipartFile archivo){
        if (!TYPE.equals(archivo.getContentType())) {
                return false;
            }
            return true;
    }

    public static List<ProductoEntity> csv2Productos(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader()
                     .withIgnoreHeaderCase().withTrim());) {

            List<ProductoEntity> productoList = new ArrayList<ProductoEntity>();

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            for (CSVRecord csvRecord : csvRecords) {
                ProductoEntity producto = new ProductoEntity(
                        /*Long.parseLong(csvRecord.get("id")),
                        csvRecord.get("nombre"),
                        csvRecord.get("calidad"),
                        Boolean.parseBoolean(csvRecord.get("marco"))*/
                );
                producto.setId(Long.valueOf(csvRecord.get("id")));
                producto.setNombre(csvRecord.get("nombre"));
                producto.setCalidad(csvRecord.get("calidad"));
                producto.setMarco(Boolean.parseBoolean(csvRecord.get("marco")));
                productoList.add(producto);
            }
            return productoList;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }

}
