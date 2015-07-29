package org.jrepman.rest;

import org.jrepman.confloader.ConfLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * Status Controller
 *
 * Status Page Controller
 *
 * @author Ohtar10 - Luis Eduardo Ferro Diez
 */
@Controller
public class StatusController {

    @Value("${application.message}")
    private String pageTitle;

    @Autowired
    private ConfLoader confLoader;

    @RequestMapping("/")
    public String index(Map<String, Object> model){
        confLoader.loadHomeDirectory();
        model.put("pagetitle", pageTitle);
        model.put("reportlist", confLoader.getReports());
        return "index";
    }
}
