package org.jrepman.integration.rest;


import org.apache.velocity.app.VelocityEngine;
import org.jrepman.JRepman;
import org.jrepman.confloader.ConfLoader;
import org.junit.AfterClass;
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

import java.util.HashMap;
import java.util.Map;

/**
 * org.jrepman.JRepman Status Test
 *
 * Validates the basic operations for the status page of the service.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = JRepman.class)
@org.springframework.test.context.web.WebAppConfiguration
@IntegrationTest("server.port=0")
@DirtiesContext
public class JRepmanStatusTest {

    @Value("${local.server.port}")
    private int port;

    @Value("${application.message}")
    private String pageTitle;

    @Autowired
    private VelocityEngine vengine;

    @Autowired
    private ConfLoader confLoader;

    @BeforeClass
    public static void beforeClass(){
        System.setProperty(ConfLoader.ENV_HOME_FOLDER, System.getProperty("user.dir")+ "/reports_home");
    }

    @AfterClass
    public static void afterClass(){
        System.clearProperty(ConfLoader.ENV_HOME_FOLDER);
    }

    /**
     * Test that the main status page can be obtained
     * @throws Exception
     */
    @Test
    public void testGetStatusPage() throws Exception{

        confLoader.loadHomeDirectory();
        Map<String, Object> model = new HashMap<>();
        model.put("pagetitle", pageTitle);
        model.put("reportlist", confLoader.getReports());

        ResponseEntity<String> entity = new TestRestTemplate().getForEntity("http://localhost:"+this.port, String.class);

        Assert.assertEquals(HttpStatus.OK, entity.getStatusCode());
        Assert.assertEquals(entity.getBody(), VelocityEngineUtils.mergeTemplateIntoString(vengine, "index.vm", "UTF-8", model));


    }
}
