package org.jrepman.enums;

/**
 * Report Definition Properties
 *
 * @author Ohtar10 - Luis Eduardo Ferro Diez
 */
public enum ReportDefinitionProperties {


    NAME("name"),
    REPORT_FILE("report_file"),
    PARAMS("params");


    private String name;

    private ReportDefinitionProperties(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
