package org.jrepman.unit.reportloader;

import org.jrepman.confloader.ConfLoader;
import org.jrepman.domain.ReportDefinition;
import org.jrepman.reportloader.ReportLoader;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Report Loader Test
 *
 * Verifies the component that reads and loads a report
 * configuration.
 */

public class ReportLoaderTest {

    private static ConfLoader confLoader;

    @BeforeClass
    public static void beforeClass(){
        System.setProperty(ConfLoader.ENV_HOME_FOLDER, System.getProperty("user.dir")+ "/reports_home");
        confLoader = new ConfLoader();
    }

    /**
     * Test that a report configuration
     * can be loaded.
     */
    @Test
    public void testLoadReportConfiguration(){
        ReportLoader reportLoader = new ReportLoader();
        ReportDefinition reportDefinition = reportLoader.loadReportDefinition(confLoader.getHomeFolder()+"/report1");
        Assert.assertNotNull(reportDefinition);
        Assert.assertEquals("Report 1", reportDefinition.getName());
        Assert.assertEquals(2, reportDefinition.getParams().size());
    }
}
