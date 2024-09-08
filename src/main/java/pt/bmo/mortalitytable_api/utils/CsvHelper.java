package pt.bmo.mortalitytable_api.utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.web.multipart.MultipartFile;
import pt.bmo.mortalitytable_api.controllers.dto.MortalityDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.List;

public class CsvHelper {

    public static String TYPE = "text/csv";
    static String[] HEADERS = { "country", "year", "femaleTx", "maleTx" };

    private CsvHelper() { }

    public static boolean isCSVFormat(MultipartFile file) {
        boolean result = TYPE.equals(file.getContentType());
        return result;
    }

    public static List<MortalityDto> csvToMortalityDtoList(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

            return csvParser.getRecords()
                    .stream()
                    .map(csvRecord -> new MortalityDto(
                            null,
                            csvRecord.get("country"),
                            Integer.valueOf(csvRecord.get("year")),
                            BigDecimal.valueOf(Long.valueOf(csvRecord.get("femaleTx"))),
                            BigDecimal.valueOf(Long.valueOf(csvRecord.get("maleTx")))
                    ))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }


}
