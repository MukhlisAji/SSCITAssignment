/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mii.ptm.sscit.remedy;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import mii.ptm.sscit.domain.ConfigurationValue;
import org.apache.log4j.Logger;

/**
 *
 * @author mukhlisaj
 */
public class RemedyConnection {
    
    private Logger logger = Logger.getLogger("Remedy Connection");
    
    public ARServerUser buildconnection(ConfigurationValue configValue){
        
        ARServerUser serverUser = new ARServerUser();
        
        serverUser.setServer(configValue.getRemedyServer());
        serverUser.setUser(configValue.getRemedyUsername());
        serverUser.setPassword(configValue.getRemedyPassword());
        serverUser.setPort(Integer.parseInt(configValue.getRemedyPort()));
        
        try {
            serverUser.verifyUser();
            logger.info("Connected to BMC Remedy successfully, server address: " + configValue.getRemedyServer());
        } catch (ARException e) {
            logger.info("Connection Failed : " + e);
        }
        
        return serverUser;
    }
    
}
