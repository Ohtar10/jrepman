package org.jrepman.reportloader;

import org.jrepman.domain.ReportDefinition;
import org.jrepman.enums.ReportDefinitionProperties;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Report Loader
 *
 * Contains the logic necessary to load the
 * independent report configuration.
 */
@Component
@Scope("singleton")
public class ReportLoader {

    public ReportLoader(){

    }

    /**
     * Based on the definition found in the given path,
     * creates a new instance of ReportDefinition.
     * @param reportPath path where the report source lies
     * @return Report Definition instance
     */
    public ReportDefinition loadReportDefinition(String reportPath){
        Path reportDefinitionFile = Paths.get(reportPath).resolve("report.json");

        JsonParser jsonParser = new BasicJsonParser();
        Map<String, Object> jsonMap = null;

        try {
            jsonMap = jsonParser.parseMap(new String(Files.readAllBytes(reportDefinitionFile)));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read the '"+reportDefinitionFile+"' file", e);
        }

        ReportDefinition reportDefinition = new ReportDefinition();
        Map<String, Class> params = new HashMap<>();
        reportDefinition.setName(jsonMap.get(ReportDefinitionProperties.NAME.getName()).toString());
        reportDefinition.setReportFile(jsonMap.get(ReportDefinitionProperties.REPORT_FILE.getName()).toString());
        reportDefinition.setParams(params);

        Map<String, String> rawParams = (Map<String, String>)jsonMap.get(ReportDefinitionProperties.PARAMS.getName());

        try {
            Set<Map.Entry<String, String>> entrySet = rawParams.entrySet();
            for(Map.Entry<String, String> entry: entrySet){
                params.put(entry.getKey(), Class.forName(entry.getValue()));
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load the params map for '"+reportDefinitionFile+"'");
        }

        return reportDefinition;
    }
}
