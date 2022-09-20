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
import mii.ptm.sscit.domain.SubmodulDelete;
import mii.ptm.sscit.domain.SubmodulUpdate;
import mii.ptm.sscit.model.Delete;
import mii.ptm.sscit.model.Update;
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
public class UpdateController {

    protected static Logger logger = Logger.getLogger("UPDATE Controller : ");
    ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
    ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

    RemedyConnection remedyConnection = new RemedyConnection();
    ARServerUser serverUser = remedyConnection.buildconnection(configValue);

    RemedyAPI remedyAPI = new RemedyAPI();

    @GetMapping("/getUpdate")
    public String getUpdate() {
        long startTime = System.currentTimeMillis();

        List<EntryListInfo> elis = remedyAPI.getRemedyRecordByQuery(serverUser, configValue.getRemedyMiddleFormInterface(), "Status = 0 AND 'Jenis Query' = \"Update\"");

        try {

            for (EntryListInfo listInfo : elis) {
                SubmodulUpdate update = new SubmodulUpdate();
                Entry requestRecord = serverUser.getEntry(configValue.getRemedyMiddleFormInterface(), listInfo.getEntryID(), null);
                update.sysReqID = getValueFromRemedy(requestRecord, 536870916);
                update.requestID = getValueFromRemedy(requestRecord, 536870917);
                update.submitter = getValueFromRemedy(requestRecord, 536870921);
                update.notes = getValueFromRemedy(requestRecord, 536870920);
                update.objectUpdate = getValueFromRemedy(requestRecord, 536870918);

                requestRecord.put(7, new Value("1"));
                serverUser.setEntry(configValue.getRemedyMiddleFormInterface(), requestRecord.getEntryId(), requestRecord, null, 0);
                Update getUpdate = new Update();

                try {
                    Response updateStatus = new Response("", "", "");

                    switch (update.objectUpdate) {
                        case "Update Site Change Same Mapping":
                            updateStatus = getUpdate.getUpdateSite(update);
                            break;
                        case "Update Service Change Same Mapping":
                            updateStatus = getUpdate.getUpdateService(update);
                            break;
                        case "Update Support Group Change Same Mapping":
                            updateStatus = getUpdate.getUpdateSG(update);
                            break;
                        case "Service to Support Group Mapping Change":
                            updateStatus = getUpdate.getUpdateServiceToSG(update);
                            break;
                        case "Site to Support Group Mapping Change":
                            updateStatus = getUpdate.getUpdateSiteToSG(update);
                            break;
                    }

                    if (updateStatus.getStatus().equals("SUCCESSFUL")) {
                        requestRecord.put(7, new Value("2"));
                    } else {
                        requestRecord.put(7, new Value("3"));
                    }
                } catch (Exception e) {
                    logger.info("error, Something doesn't seem to work at Update");
                    logger.info("error : " + e);
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

    public String getValueFromRemedy(Entry requestRecord, Object fieldID) {
        if (requestRecord.get(fieldID).getValue() == null) {
            return "";
        }

        return requestRecord.get(fieldID).getValue().toString();
    }

}
