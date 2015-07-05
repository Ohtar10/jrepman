package org.jrepman.unit.rest;

import org.jrepman.JRepman;
import org.jrepman.confloader.ConfLoader;
import org.jrepman.enums.ExportType;
import org.jrepman.reports.ReportExporter;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Validates the module that generates the Jasper Reports
 * alone.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = JRepman.class)
public class ExecuteReportTest {

    @Autowired
    private ReportExporter reportsExporter;

    @BeforeClass
    public static void beforeClass(){
        System.setProperty(ConfLoader.ENV_HOME_FOLDER, System.getProperty("user.dir")+ "/reports_home");
    }

    @AfterClass
    public static void afterClass(){
        System.clearProperty(ConfLoader.ENV_HOME_FOLDER);
    }

    /**
     * This test validates that a jasper report can be invoked within
     * the internal classes.
     */
    @Test
    public void testExecuteReportAlone() throws Exception{
        Path userDir = Paths.get(System.getProperty("user.dir"));
        Path reportPath = userDir.resolve("reports_home/report1/report1.jasper");
        Path exportPath =userDir.resolve("export1.pdf");

        try {
            FileOutputStream fos = new FileOutputStream(exportPath.toFile());

            Map<String, Object> data = new HashMap<>();
            data.put("param1", "Hello World");

            reportsExporter.exportReport(reportPath, data, ExportType.PDF, fos);

            Assert.assertTrue(Files.exists(exportPath));
            Assert.assertTrue(Files.size(exportPath)>0);
        } catch (FileNotFoundException e) {
            Assert.fail("Report Generation Failed");
        } finally {
            Files.deleteIfExists(exportPath);
        }
    }

    /**
     * This test validates that a jasper report can be invoked with the
     * configuration loaded at start up.
     */
    @Test
    public void testExecuteReportFromJRepmanConfiguration(){

    }

}
