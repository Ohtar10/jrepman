package org.jrepman.confloader;

import org.springframework.boot.json.BasicJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * Contains the necessary logic to load
 * the report configurations from the
 * home folder specified program argument.
 */
@Component
@Scope("singleton")
public class ConfLoader {

    public static String ENV_HOME_FOLDER = "reports.home";
    public static String MAIN_CONFIG_FILE_NAME = "jrepman.json";
    public static String MAIN_CONFIG_REPORTS_PROPERTY = "reports";

    private List<String> reports;

    private String homeFolder;

    public ConfLoader(){
        loadHomeDirectory();
    }

    /**
     * Look up in the home directory for the corresponding
     * reporting configuration.
     */
    public void loadHomeDirectory(){
        homeFolder = System.getProperty(ENV_HOME_FOLDER);
        if(homeFolder == null)
            throw new RuntimeException("The reports.home env variable should be specified at start up");

        Path reportsHome = Paths.get(homeFolder);
        Path mainConfig = reportsHome.resolve(MAIN_CONFIG_FILE_NAME);

        if(!Files.exists(mainConfig))
            throw new RuntimeException("There is no '"+MAIN_CONFIG_FILE_NAME+"' inside the home folder");

        JsonParser jsonParser = new BasicJsonParser();
        Map<String, Object> jsonMap = null;

        try {
            jsonMap = jsonParser.parseMap(new String(Files.readAllBytes(mainConfig)));
        } catch (IOException e) {
            throw new RuntimeException("Filed to read the '"+MAIN_CONFIG_FILE_NAME+"' file", e);
        }

        reports = (List<String>)jsonMap.get(MAIN_CONFIG_REPORTS_PROPERTY);
    }

    /**
     * Gets the current report list based on the last time
     * the main config file was read.
     * @return List of strings with the report names
     */
    public List<String> getReports() {
        return reports;
    }

    /**
     * Gets the home folder defined at start up.
     * @return String with the home folder.
     */
    public String getHomeFolder(){
        return homeFolder;
    }

}
