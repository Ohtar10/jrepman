package org.jrepman.domain;

import org.jrepman.enums.ReportParamDataType;

import java.nio.file.Path;
import java.util.Map;

/**
 * Report Definition.
 *
 * POJO representing a report definition
 * according to what is placed on the json
 * configuration.
 *
 * @author Ohtar10 - Luis Eduardo Ferro Diez
 */
public class ReportDefinition {

    private String name;
    private String reportFile;
    private Path reportPath;
    private Map<String, ReportParamDataType> params;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReportFile() {
        return reportFile;
    }

    public void setReportFile(String reportFile) {
        this.reportFile = reportFile;
    }

    public Map<String, ReportParamDataType> getParams() {
        return params;
    }

    public void setParams(Map<String, ReportParamDataType> params) {
        this.params = params;
    }

    public Path getReportPath() {
        return reportPath;
    }

    public void setReportPath(Path reportPath) {
        this.reportPath = reportPath;
    }
}
