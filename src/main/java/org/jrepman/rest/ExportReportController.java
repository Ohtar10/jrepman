package org.jrepman.rest;

import org.apache.log4j.Logger;
import org.jrepman.confloader.ConfLoader;
import org.jrepman.domain.ReportDefinition;
import org.jrepman.enums.ExportType;
import org.jrepman.reportloader.ReportLoader;
import org.jrepman.reports.ReportExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Export Report Controller
 *
 * This is the main class for report generation, from here
 * depending on the request, a series of decisions will be
 * taken in order to generate and deliver the report.
 *
 * @author Ohtar10 - Luis Eduardo Ferro Diez
 */
@Controller
public class ExportReportController {


    private static final Logger LOGGER = Logger.getLogger(ExportReportController.class);

    private static final String DATE_FORMAT = "ddMMyyyy";

    private static final String DATE_TIME_FORMAT = "ddMMyyyyHHmm";

    private static final String LIST_SEPARATOR_CHAR = ",";

    @Value("${report.queue.message}")
    private String pageTitle;

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
        ReportDefinition reportDefinition = getReportDefinition(reportName);
        //Then generate the report parameters
        Map<String, Object> paramsToReport = generateParamsMap(reportDefinition, params);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        reportExporter.exportReport(reportDefinition.getReportPath().resolve(reportDefinition.getReportFile()), paramsToReport, ExportType.PDF, bos);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(ExportType.PDF.getMediaType()));
        String filename = ExportType.PDF.getFileWithExtension(reportName);
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<>(bos.toByteArray(), headers, HttpStatus.OK);

        return response;
    }

    /**
     * This version takes the report and arguments and delegate its execution
     * to a thread and responses right away.
     *
     * It must contain a the sendto param to know where to deliver
     * the report after it is finished.
     *
     * @param reportName The report name on path
     * @param params The parameters
     * @return page with the response
     */
    @RequestMapping(value = "/{report}/queue", method = RequestMethod.GET)
    public String queueReport(Map<String, Object> model, @PathVariable("report") String reportName,
                              @RequestParam Map<String, String> params){

        //Extract the sendto param first since this is no a param to send to the report
        String sendto = params.remove("sendto");
        //Then load the report definition
        ReportDefinition reportDefinition = getReportDefinition(reportName);
        //Then generate the report parameters
        Map<String, Object> paramsToReport = generateParamsMap(reportDefinition, params);

        //TODO queue the report generation

        model.put("pagetitle", pageTitle);
        model.put("report", reportDefinition);
        model.put("emails", sendto.split(","));
        model.put("reportvalues", paramsToReport);

        //Prepare the response
        return "queued";
    }


    /**
     * According to the given report name, it loads the corresponding configuration.
     * @param reportName The report name to be loaded.
     * @return Report Definition Instance
     */
    private ReportDefinition getReportDefinition(String reportName){
        confLoader.loadHomeDirectory();
        Path homeFolder = Paths.get(confLoader.getHomeFolder());
        Path reportPath = homeFolder.resolve(reportName);

        ReportDefinition reportDefinition = reportLoader.loadReportDefinition(reportPath.toString());

        return reportDefinition;
    }

    /**
     * Based on the given report definition and the received request parameters
     * it generates a new map with the final parameters to send to the report
     * generation process
     * @param reportDefinition Report Definition Instance
     * @param params Request parameters
     * @return Map with the parameters to send to the report
     */
    private Map<String, Object> generateParamsMap(ReportDefinition reportDefinition, Map<String, String> params){
        Map<String, Object> paramsToReport = new HashMap<>();
        Set<Map.Entry<String, String>> incomingParamsSet = params.entrySet();

        for(Map.Entry<String, String> entry: incomingParamsSet){
            DateFormat sdf;
            try {
                switch(reportDefinition.getParams().get(entry.getKey())){
                    case STRING:
                        paramsToReport.put(entry.getKey(), entry.getValue());
                        break;
                    case INTEGER:
                        paramsToReport.put(entry.getKey(), Integer.parseInt(entry.getValue()));
                        break;
                    case DOUBLE:
                        paramsToReport.put(entry.getKey(), Double.parseDouble(entry.getValue()));
                        break;
                    case DATE:
                        sdf = new SimpleDateFormat(DATE_FORMAT);
                        paramsToReport.put(entry.getKey(), sdf.parse(entry.getValue()));
                        break;
                    case DATE_TIME:
                        sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
                        paramsToReport.put(entry.getKey(), sdf.parse(entry.getValue()));
                        break;
                    case LIST:
                        paramsToReport.put(entry.getKey(), Arrays.asList(entry.getValue().split(LIST_SEPARATOR_CHAR)));
                        break;
                }
            } catch (ParseException e) {
                LOGGER.warn(MessageFormat.format("There is a problem with the report {0} and one of his params, please check", reportDefinition.getName()), e);
            }
        }
        return paramsToReport;
    }
}
