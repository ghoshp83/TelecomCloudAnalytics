package com.pralay.configuration.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class DataInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String fileName;
    private List<Map<String, String>> values; // Holds the information of
                                              // Equipment Id and KPI's Values

    public DataInfo() {
    }

    public DataInfo(String fileName, List<Map<String, String>> values) {
        this.values = values;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<Map<String, String>> getValues() {
        return values;
    }

    public void setValues(List<Map<String, String>> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "DataInfo [fileName = " + fileName + ", values = " + values + "]";
    }

}
