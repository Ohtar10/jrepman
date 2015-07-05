package org.jrepman.reports;

import org.jrepman.enums.ExportType;

import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Map;

/**
 * Report Exporter
 *
 * Main interface to export reports as requested
 */
public interface ReportExporter {

    /**
     * Based on the source location of the report, tries to generate it with the
     * given parameters and the given export type to the given outputstream.
     * @param reportPath Path to the source of the report
     * @param params Parameter map to pass to the report
     * @param exportType Export type of the report according to the enum
     * @param outputStream Outputstream to write the report
     */
    void exportReport(Path reportPath, Map<String, Object> params, ExportType exportType, OutputStream outputStream);

}
