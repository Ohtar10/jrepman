package org.jrepman.integration.rest;

import org.apache.velocity.app.VelocityEngine;
import org.jrepman.JRepman;
import org.jrepman.confloader.ConfLoader;
import org.jrepman.domain.ReportDefinition;
import org.jrepman.enums.ExportType;
import org.jrepman.reportloader.ReportLoader;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Execute Report Test
 *
 * Validates that the execute report executes correctly.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = JRepman.class)
@org.springframework.test.context.web.WebAppConfiguration
@IntegrationTest("server.port=0")
@DirtiesContext
public class ExecuteReportTest {

    @Value("${local.server.port}")
    private int port;

    @Value("${report.queue.message}")
    private String pageTitle;

    @Autowired
    private VelocityEngine vengine;

    @Autowired
    private ConfLoader confLoader;

    @Autowired
    private ReportLoader reportLoader;

    /**
     * Set ups the home folder to start the application
     */
    @BeforeClass
    public static void beforeClass(){
        System.setProperty(ConfLoader.ENV_HOME_FOLDER, System.getProperty("user.dir")+ "/reports_home");
    }

    /**
     * Consumes the Report execution resource and
     * deliver it as response.
     */
    @Test
    public void testExecuteAndDeliverReport1(){
        ResponseEntity<byte[]> entity = new TestRestTemplate().getForEntity("http://localhost:"+this.port+"/report1?param1=a&param2=b", byte[].class);
        Assert.assertEquals(HttpStatus.OK, entity.getStatusCode());
        Assert.assertEquals(ExportType.PDF.getMediaType(), entity.getHeaders().getContentType().getType() + "/" + entity.getHeaders().getContentType().getSubtype());
    }

    /**
     * Consumes the Report execution resources and deliver
     * it as response, this cases validates that all the
     * supported data types can be handled
     */
    @Test
    public void testExecuteAndDeliverReport2AllAvailableDataTypes(){
        ResponseEntity<byte[]> entity = new TestRestTemplate().getForEntity("http://localhost:"+this.port+"/report2?stringparam=hello_world&intparam=10&longparam=10&doubleparam=2.4567&dateparam=01012015&datetimeparam=010120151356&collectionparam=a,b,c", byte[].class);
        Assert.assertEquals(HttpStatus.OK, entity.getStatusCode());
        Assert.assertEquals(ExportType.PDF.getMediaType(), entity.getHeaders().getContentType().getType() + "/" + entity.getHeaders().getContentType().getSubtype());
    }

    /**
     * Consumes the Report execution resources but
     * in this time, the app will return in an instant
     * and queue the report generation.
     *
     * JRepman will use the email configuration given at
     * the home folder config file but the sending itself
     * depends on the used service.
     *
     */
    @Test
    public void testExecuteReportAndSendItToEmail(){
        //Prepare the data to the page generation comparison
        confLoader.loadHomeDirectory();
        String reportName = "report1";

        Map<String, Object> model = new HashMap<>();
        Map<String, Object> reportValues = new HashMap<>();

        Path homeFolder = Paths.get(confLoader.getHomeFolder());
        Path reportPath = homeFolder.resolve(reportName);

        ReportDefinition reportDefinition = reportLoader.loadReportDefinition(reportPath.toString());

        reportValues.put("param1", "Hello");
        reportValues.put("param2", "World");

        model.put("pagetitle", pageTitle);
        model.put("report", reportDefinition);
        model.put("emails", new String[]{"hello@world.com"});
        model.put("reportvalues", reportValues);

        ResponseEntity<String> entity = new TestRestTemplate().getForEntity("http://localhost:"+this.port+"/report1/queue?param1=Hello&param2=World&sendto=hello@world.com", String.class);
        Assert.assertEquals(HttpStatus.OK, entity.getStatusCode());
        Assert.assertEquals(entity.getBody(), VelocityEngineUtils.mergeTemplateIntoString(vengine, "queued.vm", "UTF-8", model));
    }
}
