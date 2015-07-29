package org.jrepman.enums;

/**
 * Contains the class definition for all the possible param
 * types that jrepman reports can handle.
 *
 * @author Ohtar10 - Luis Eduardo Ferro Diez
 */
public enum ReportParamDataType {

    STRING("string"),
    INTEGER("integer"),
    DOUBLE("double"),
    DATE("date"),
    DATE_TIME("date_time"),
    LIST("list");


    private String key;

    private ReportParamDataType(String key){
        this.key = key;
    }

    public static ReportParamDataType fromKey(String key){
        for(ReportParamDataType type: ReportParamDataType.values()){
            if(type.key.equals(key)){
                return type;
            }
        }
        return null;
    }

    public String getKey() {
        return key;
    }


}
