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
import static mii.ptm.sscit.controller.AddController.logger;
import mii.ptm.sscit.domain.ConfigFile;
import mii.ptm.sscit.domain.ConfigurationValue;
import mii.ptm.sscit.domain.RemedyAPI;
import mii.ptm.sscit.domain.Response;
import mii.ptm.sscit.domain.SubmodulADD;
import mii.ptm.sscit.domain.SubmodulDelete;
import mii.ptm.sscit.domain.SubmodulUpdate;
import mii.ptm.sscit.model.Add;
import mii.ptm.sscit.model.Delete;
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
public class DeleteController {

    protected static Logger logger = Logger.getLogger("DELETE Controller : ");
    ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
    ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

    RemedyConnection remedyConnection = new RemedyConnection();
    ARServerUser serverUser = remedyConnection.buildconnection(configValue);

    RemedyAPI remedyAPI = new RemedyAPI();

    @GetMapping("/getDelete")
    public String getDelete() {

        long startTime = System.currentTimeMillis();

        List<EntryListInfo> elis = remedyAPI.getRemedyRecordByQuery(serverUser, configValue.getRemedyMiddleFormInterface(), "Status = 0 AND 'Jenis Query' = \"Delete\"");

        try {

            for (EntryListInfo listInfo : elis) {
                SubmodulDelete delete = new SubmodulDelete();
                Entry requestRecord = serverUser.getEntry(configValue.getRemedyMiddleFormInterface(), listInfo.getEntryID(), null);
                delete.sysReqID = getValueFromRemedy(requestRecord, 536870916);
                delete.requestID = getValueFromRemedy(requestRecord, 536870917);
                delete.submitter = getValueFromRemedy(requestRecord, 536870921);
                delete.notes = getValueFromRemedy(requestRecord, 536870920);
                delete.objectDelete = getValueFromRemedy(requestRecord, 536870918);
                int kodeDelete = 0;
                if (delete.objectDelete.contains("Site")) {
                    kodeDelete = 1;
                } else if (delete.objectDelete.contains("Service")) {
                    kodeDelete = 2;
                } else if (delete.objectDelete.contains("Support Group")) {
                    kodeDelete = 3;
                }

                requestRecord.put(7, new Value("1"));
                serverUser.setEntry(configValue.getRemedyMiddleFormInterface(), requestRecord.getEntryId(), requestRecord, null, 0);

                try {
                    Delete del = new Delete();

                    Response delStatus = del.getDelete(delete, kodeDelete);;
                    if (delStatus.getStatus().equals("SUCCESSFUL")) {
                        requestRecord.put(7, new Value("2"));
                    } else {
                        requestRecord.put(7, new Value("3"));
                    }
                } catch (Exception e) {
                    logger.info("error, Something doesn't seem to work at Delete");
                    logger.info("error : " + e);
                    requestRecord.put(7, new Value("3"));
                }

                serverUser.setEntry(configValue.getRemedyMiddleFormInterface(), requestRecord.getEntryId(), requestRecord, null, 0);
            }

        } catch (ARException e) {
            logger.info("error 001 : " + e.toString());
        } catch (Exception e) {
            logger.info("error 001 : " + e.toString());
        }

        long endTime = System.currentTimeMillis();
        long lamawaktu = endTime - startTime;
        logger.info("execution time:" + String.valueOf(lamawaktu) + " milidetik");

        return "result";

    }

    @GetMapping("/hardDeleteApproval")
    public String hardDeleteApproval() {
        RemedyAPI remedyAPI = new RemedyAPI();
        List<EntryListInfo> elis = remedyAPI.getRemedyRecordByQuery(serverUser, configValue.getRemedyMiddleFormCustomApproval(), "Notes LIKE \"%test%\"");

        elis.parallelStream().forEach(listInfo -> {
            try {
                serverUser.deleteEntry(configValue.getRemedyMiddleFormCustomApproval(), listInfo.getEntryID(), 0);

            } catch (ARException ex) {
                logger.info("error : " + ex.toString());
            }
        });

        return "result";
    }

    @GetMapping("/hardDeleteAssignment")
    public String hardDeleteAssignment() {
        RemedyAPI remedyAPI = new RemedyAPI();
        List<EntryListInfo> elis = remedyAPI.getRemedyRecordByQuery(serverUser, configValue.getRemedyFormAssignment(), "Notes LIKE \"%test%\"");

        elis.parallelStream().forEach(listInfo -> {
            try {
                serverUser.deleteEntry(configValue.getRemedyFormAssignment(), listInfo.getEntryID(), 0);

            } catch (ARException ex) {
                logger.info("error : " + ex.toString());
            }
        });

        return "result";
    }

    public String getValueFromRemedy(Entry requestRecord, Object fieldID) {
        if (requestRecord.get(fieldID).getValue() == null) {
            return "";
        }

        return requestRecord.get(fieldID).getValue().toString();
    }
}
