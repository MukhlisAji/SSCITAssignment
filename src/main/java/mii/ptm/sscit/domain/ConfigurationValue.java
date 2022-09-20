/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mii.ptm.sscit.domain;

/**
 *
 * @author mukhlisaj
 */
public class ConfigurationValue {
    
    private String remedyServer;
    private String remedyUsername, remedyPassword;
    private String remedyPort;
    private String remedyMiddleFormStaging;
    private String remedyMiddleFormInterface;
    private String remedyFormAssignment;
    private String remedyMiddleFormCustomApproval;

    public ConfigurationValue(String remedyServer, String remedyUsername, String remedyPassword, String remedyPort, String remedyMiddleFormStaging, String remedyMiddleFormInterface, String remedyFormAssignment, String remedyMiddleFormCustomApproval) {
        this.remedyServer = remedyServer;
        this.remedyUsername = remedyUsername;
        this.remedyPassword = remedyPassword;
        this.remedyPort = remedyPort;
        this.remedyMiddleFormStaging = remedyMiddleFormStaging;
        this.remedyMiddleFormInterface = remedyMiddleFormInterface;
        this.remedyFormAssignment = remedyFormAssignment;
        this.remedyMiddleFormCustomApproval = remedyMiddleFormCustomApproval;
    }

    public String getRemedyServer() {
        return remedyServer;
    }

    public void setRemedyServer(String remedyServer) {
        this.remedyServer = remedyServer;
    }

    public String getRemedyUsername() {
        return remedyUsername;
    }

    public void setRemedyUsername(String remedyUsername) {
        this.remedyUsername = remedyUsername;
    }

    public String getRemedyPassword() {
        return remedyPassword;
    }

    public void setRemedyPassword(String remedyPassword) {
        this.remedyPassword = remedyPassword;
    }

    public String getRemedyPort() {
        return remedyPort;
    }

    public void setRemedyPort(String remedyPort) {
        this.remedyPort = remedyPort;
    }

    public String getRemedyMiddleFormStaging() {
        return remedyMiddleFormStaging;
    }

    public void setRemedyMiddleFormStaging(String remedyMiddleFormStaging) {
        this.remedyMiddleFormStaging = remedyMiddleFormStaging;
    }

    public String getRemedyMiddleFormInterface() {
        return remedyMiddleFormInterface;
    }

    public void setRemedyMiddleFormInterface(String remedyMiddleFormInterface) {
        this.remedyMiddleFormInterface = remedyMiddleFormInterface;
    }

    public String getRemedyFormAssignment() {
        return remedyFormAssignment;
    }

    public void setRemedyFormAssignment(String remedyFormAssignment) {
        this.remedyFormAssignment = remedyFormAssignment;
    }

    public String getRemedyMiddleFormCustomApproval() {
        return remedyMiddleFormCustomApproval;
    }

    public void setRemedyMiddleFormCustomApproval(String remedyMiddleFormCustomApproval) {
        this.remedyMiddleFormCustomApproval = remedyMiddleFormCustomApproval;
    }

    
    
}


