/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mii.ptm.sscit.controller;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.EntryListInfo;
import com.bmc.arsys.api.Value;
import java.util.List;
import mii.ptm.sscit.domain.ConfigFile;
import mii.ptm.sscit.domain.ConfigurationValue;
import mii.ptm.sscit.domain.RemedyAPI;
import mii.ptm.sscit.domain.Response;
import mii.ptm.sscit.domain.SubmodulADD;
import mii.ptm.sscit.model.Add;
import mii.ptm.sscit.model.ManualUpload;
import mii.ptm.sscit.model.MetaFunction;
import mii.ptm.sscit.remedy.RemedyConnection;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author mukhlisaj
 */
@Controller
public class AddController {

    ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
    ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

    RemedyConnection remedyConnection = new RemedyConnection();
    ARServerUser serverUser = remedyConnection.buildconnection(configValue);

    RemedyAPI remedyAPI = new RemedyAPI();

    protected static Logger logger = Logger.getLogger("ADD Controller : ");

    @GetMapping("/getADD")
    public String AddHybrid() {
        long startTime = System.currentTimeMillis();

        List<EntryListInfo> elis = remedyAPI.getRemedyRecordByQuery(serverUser, configValue.getRemedyMiddleFormInterface(), "Status = 0 AND 'Jenis Query' = \"Add\"");

        try {

            for (EntryListInfo listInfo : elis) {
                SubmodulADD add = new SubmodulADD();
                Entry requestRecord = serverUser.getEntry(configValue.getRemedyMiddleFormInterface(), listInfo.getEntryID(), null);
                add.sysReqID = getValueFromRemedy(requestRecord, 536870916);
                add.requestID = getValueFromRemedy(requestRecord, 536870917);
                add.submitter = getValueFromRemedy(requestRecord, 536870921);
                add.notes = getValueFromRemedy(requestRecord, 536870920);
                add.objectQuery = getValueFromRemedy(requestRecord, 536870918);

                requestRecord.put(7, new Value("1"));
                serverUser.setEntry(configValue.getRemedyMiddleFormInterface(), requestRecord.getEntryId(), requestRecord, null, 0);
                Response addSiteStatus = new Response("", "", "");
                try {
                    Add addSite = new Add();

                    switch (add.objectQuery) {
                        case "New Hybrid":
                            addSiteStatus = addSite.getADDHybrid(add);
                            break;
                        case "New Site":
                            addSiteStatus = addSite.getADDSite(add);
                            break;
                        case "New Service":
                            addSiteStatus = addSite.getADDService(add);
                            break;
                        case "New Support Group":
                            addSiteStatus = addSite.getADDSupportGroup(add);
                            break;
                    }

                    if (addSiteStatus.getStatus().equals("SUCCESSFUL")) {
                        requestRecord.put(7, new Value("2"));
                    } else {
                        requestRecord.put(7, new Value("3"));
                    }
                } catch (Exception e) {
                    logger.info("error, Something doesn't seem to work at add");
                    logger.info("error : " + e);
                    logger.info("error : " + addSiteStatus.getErrorMessage());
                    requestRecord.put(7, new Value("3"));
                }

                serverUser.setEntry(configValue.getRemedyMiddleFormInterface(), requestRecord.getEntryId(), requestRecord, null, 0);
            }

        } catch (ARException e) {
            logger.info("error 001 : " + e.toString());
        } catch (Exception e) {
            logger.info("error 002 : " + e.toString());
        }

        long endTime = System.currentTimeMillis();
        long lamawaktu = endTime - startTime;
        logger.info("execution time:" + String.valueOf(lamawaktu) + " milidetik");

        return "result";

    }

    @GetMapping("/getManualUpload")
    public String manualUpload() {
        List<EntryListInfo> elis = remedyAPI.getRemedyRecordByQuery(serverUser, configValue.getRemedyMiddleFormInterface(), "Status = 0 AND 'Jenis Query' = \"Manual Upload\"");

        try {

            for (EntryListInfo listInfo : elis) {
                SubmodulADD add = new SubmodulADD();
                Entry requestRecord = serverUser.getEntry(configValue.getRemedyMiddleFormInterface(), listInfo.getEntryID(), null);
                add.sysReqID = getValueFromRemedy(requestRecord, 536870916);
                add.requestID = getValueFromRemedy(requestRecord, 536870917);
                add.submitter = getValueFromRemedy(requestRecord, 536870921);
                add.notes = getValueFromRemedy(requestRecord, 536870920);

                requestRecord.put(7, new Value("1"));
                serverUser.setEntry(configValue.getRemedyMiddleFormInterface(), requestRecord.getEntryId(), requestRecord, null, 0);

                try {
                    Response manualUploadStatus = new Response("", "", "");

                    ManualUpload mu = new ManualUpload();
                    manualUploadStatus = mu.getManualUpload(add);
                    if (manualUploadStatus.getStatus().equals("SUCCESSFUL")) {
                        requestRecord.put(7, new Value("2"));
                    } else {
                        requestRecord.put(7, new Value("3"));
                    }
                } catch (Exception e) {
                    logger.info("error, Something doesn't seem to work at manual Upload");
                    requestRecord.put(7, new Value("3"));
                }

                serverUser.setEntry(configValue.getRemedyMiddleFormInterface(), requestRecord.getEntryId(), requestRecord, null, 0);
            }

        } catch (ARException e) {
            logger.info("error 001 : " + e.toString());
        } catch (Exception e) {
            logger.info("error 001 : " + e.toString());
        }

        return "result";
    }

    public String getValueFromRemedy(Entry requestRecord, Object fieldID) {
        if (requestRecord.get(fieldID).getValue() == null) {
            return "";
        }

        return requestRecord.get(fieldID).getValue().toString();
    }
}
