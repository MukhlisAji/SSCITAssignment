/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mii.ptm.sscit.controller;

import com.bmc.arsys.api.ARServerUser;
import mii.ptm.sscit.domain.ConfigFile;
import mii.ptm.sscit.domain.ConfigurationValue;
import mii.ptm.sscit.remedy.RemedyConnection;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author mukhlisaj
 */
@Controller
public class RemedyController {

    private static Logger logger = Logger.getLogger("Remedy Controller");

    ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
    ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

    RemedyConnection remedyConnection = new RemedyConnection();
    ARServerUser serverUser = remedyConnection.buildconnection(configValue);

    @GetMapping("/welcome")
    public String welcome(Model model) {

        logger.info("Trying to connect " + configValue.getRemedyUsername() + " to server " + configValue.getRemedyServer());
        //Test Connection
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser serverUser = remedyConnection.buildconnection(configValue);

        model.addAttribute("result", "Connected to " + configValue.getRemedyServer());
        model.addAttribute("result1", "Status " + serverUser.getUser());

        return "welcome";
    }

}
