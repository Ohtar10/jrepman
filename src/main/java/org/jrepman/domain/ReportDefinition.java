package org.jrepman.domain;

import java.util.Map;

/**
 * Report Definition.
 *
 * POJO representing a report definition
 * according to what is placed on the json
 * configuration.
 */
public class ReportDefinition {

    private String name;
    private String reportFile;
    private Map<String, Class> params;

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

    public Map<String, Class> getParams() {
        return params;
    }

    public void setParams(Map<String, Class> params) {
        this.params = params;
    }
}
