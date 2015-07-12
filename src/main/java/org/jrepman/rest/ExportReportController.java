package org.jrepman.rest;

import org.jrepman.confloader.ConfLoader;
import org.jrepman.domain.ReportDefinition;
import org.jrepman.enums.ExportType;
import org.jrepman.reportloader.ReportLoader;
import org.jrepman.reports.ReportExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Export Report Controller
 *
 * This is the main class for report generation, from here
 * depending on the request, a series of decisions will be
 * taken in order to generate and deliver the report.
 */
@Controller
public class ExportReportController {

    @Autowired
    private ConfLoader confLoader;

    @Autowired
    private ReportLoader reportLoader;

    @Autowired
    private ReportExporter reportExporter;

    @RequestMapping(value = "/{report}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> exportReport(@PathVariable("report") String reportName,
                               @RequestParam Map<String, String> params){

        //First load the report definition
        confLoader.loadHomeDirectory();
        Path homeFolder = Paths.get(confLoader.getHomeFolder());
        Path reportPath = homeFolder.resolve(reportName);

        ReportDefinition reportDefinition = reportLoader.loadReportDefinition(reportPath.toString());

        Map<String, Object> paramsToReport = new HashMap<>();
        Set<Map.Entry<String, String>> incomingParamsSet = params.entrySet();

        for(Map.Entry<String, String> entry: incomingParamsSet){
            paramsToReport.put(entry.getKey(), reportDefinition.getParams().get(entry.getKey()).cast(entry.getValue()));
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        reportExporter.exportReport(reportPath.resolve(reportDefinition.getReportFile()), paramsToReport, ExportType.PDF, bos);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(ExportType.PDF.getMediaType()));
        String filename = ExportType.PDF.getFileWithExtension(reportName);
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<>(bos.toByteArray(), headers, HttpStatus.OK);

        return response;
    }
}
