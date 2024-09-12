package org.klodnicki.util;

import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.*;
import java.util.List;

public class CSVUtil {

    //this method will work with any type <T>, for example: List<Employee> T would be Employee, List<Badge> T would be Badge
    public static <T> void exportToCSV(String fileName, List<T> list, ColumnPositionMappingStrategy<T> strategy)
            throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        try (FileWriter writer = new FileWriter(fileName)) {
            StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writer)
                    .withMappingStrategy(strategy)
                    .build();
            beanToCsv.write(list);
        }
    }

    public static <T> List<T> importFromCSV(String fileName, Class<T> clazz) throws IOException {
        try (Reader reader = new FileReader(fileName)) {
            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                    .withType(clazz)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            return csvToBean.parse();
        }
    }
}
