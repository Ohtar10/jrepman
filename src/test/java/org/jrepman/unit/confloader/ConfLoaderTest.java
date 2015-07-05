package org.jrepman.unit.confloader;

import org.jrepman.confloader.ConfLoader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * ConfLoader Test
 *
 * Validates the functions that load the configuration files from the given directory
 * at application start up.
 */
public class ConfLoaderTest {

    @Before
    public void beforeMethod(){
        //Ensure no properties are set before each test
        System.clearProperty(ConfLoader.ENV_HOME_FOLDER);
    }

    /**
     * Validates that after application startup
     * reads the reports_home argument and effectively
     * loads the reports from the home folder.
     */
    @Test
    public void testLoadConfigurationFolder(){
        System.setProperty(ConfLoader.ENV_HOME_FOLDER, System.getProperty("user.dir")+ "/reports_home");
        ConfLoader confLoader = new ConfLoader();
        List<String> reports = confLoader.getReports();
        Assert.assertNotNull(reports);
        Assert.assertEquals(2, reports.size());
        //No exceptions expected
    }

    /**
     * Validates the situation when the class is loaded without
     * a report home folder be specified.
     */
    @Test
    public void testLoadConfigurationWithoutHomeFolderRaisesException(){
        try {
            new ConfLoader();
            Assert.fail("An Exception was expected");
        } catch (Exception e) {
            Assert.assertEquals("The reports.home env variable should be specified at start up", e.getMessage());
        }
    }

    /**
     * Validates the situation when the class is loaded with a report home
     * directory but without a main config file.
     */
    @Test
    public void testLoadConfigurationWithoutMainConfigFileRaisesException(){
        System.setProperty(ConfLoader.ENV_HOME_FOLDER, System.getProperty("user.dir"));
        try {
            new ConfLoader();
            Assert.fail("An Exception was expected");
        } catch (Exception e) {
            Assert.assertEquals("There is no '"+ConfLoader.MAIN_CONFIG_FILE_NAME+"' inside the home folder", e.getMessage());
        }
    }
}
