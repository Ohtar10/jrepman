package org.jrepman.reports;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import org.jrepman.enums.ExportType;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Map;

/**
 * Jasper Report Exporter
 *
 * Jasper Report implementation to export reports.
 *
 */
@Component
@Scope("singleton")
public class JasperReportExporter implements ReportExporter{

    @Override
    public void exportReport(Path reportPath, Map<String, Object> params, ExportType exportType, OutputStream outputStream) {
        try {
            JasperReport report = (JasperReport)JRLoader.loadObject(reportPath.toFile());
            JasperPrint print = JasperFillManager.fillReport(report, params);

            JasperExportManager.exportReportToPdfStream(print, outputStream);
        } catch (JRException e) {
            throw new RuntimeException("Error Executing the report", e);
        }
    }

}
