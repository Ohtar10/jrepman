package org.jrepman.integration.rest;

import org.jrepman.JRepman;
import org.jrepman.confloader.ConfLoader;
import org.jrepman.enums.ExportType;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
        Assert.assertEquals(ExportType.PDF.getMediaType(), entity.getHeaders().getContentType().getType()+"/"+entity.getHeaders().getContentType().getSubtype());
    }
}
