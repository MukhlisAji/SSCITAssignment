/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mii.ptm.sscit.model;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.EntryListInfo;
import com.bmc.arsys.api.Value;
import java.util.List;
import java.util.logging.Level;
import mii.ptm.sscit.domain.ConfigFile;
import mii.ptm.sscit.domain.ConfigurationValue;
import mii.ptm.sscit.domain.DataInsert;
import mii.ptm.sscit.domain.RemedyAPI;
import mii.ptm.sscit.domain.Response;
import mii.ptm.sscit.domain.SubmodulADD;
import mii.ptm.sscit.domain.SubmodulDelete;
import mii.ptm.sscit.domain.SubmodulUpdate;
import mii.ptm.sscit.remedy.RemedyConnection;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author mukhlisaj
 */
public class Delete {

    private static Logger logger = Logger.getLogger("Delete Modul");

    ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
    ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

    RemedyConnection remedyConnection = new RemedyConnection();
    ARServerUser serverUser = remedyConnection.buildconnection(configValue);

    public Response getDelete(SubmodulDelete delete, int objectType) {
        RemedyAPI remedyAPI = new RemedyAPI();

        try {

            //getDataformStaging
            List<EntryListInfo> siteInfos = remedyAPI.getRemedyRecordByQuery(serverUser, configValue.getRemedyMiddleFormStaging(), "'SR Type Field 30__c' = \"" + delete.requestID + "\"");

            SubmodulDelete getDelete = new SubmodulDelete();
            for (EntryListInfo siteInfo : siteInfos) {
                Entry siteRecord = serverUser.getEntry(configValue.getRemedyMiddleFormStaging(), siteInfo.getEntryID(), null);
                logger.info("records : " + siteRecord);
                getDelete.company = getValueFromRemedy(siteRecord, 1000000001);
                getDelete.region = getValueFromRemedy(siteRecord, 200000012);
                getDelete.siteGroup = getValueFromRemedy(siteRecord, 200000007);
                getDelete.site = getValueFromRemedy(siteRecord, 260000001);
                getDelete.opcat1 = getValueFromRemedy(siteRecord, 536870992);
                getDelete.opcat2 = getValueFromRemedy(siteRecord, 536870993);
                getDelete.opcat3 = getValueFromRemedy(siteRecord, 536870994);
                getDelete.opcat4 = getValueFromRemedy(siteRecord, 200000003);
                getDelete.supportCompany = getValueFromRemedy(siteRecord, 1000000251);
                getDelete.supportOrg = getValueFromRemedy(siteRecord, 1000000014);
                getDelete.supportGroup = getValueFromRemedy(siteRecord, 1000000217);
                getDelete.supportGroupID = getValueFromRemedy(siteRecord, 536871020);

            }
            String query;
            String queryApp;
            if (objectType == 1) {
                query = "Company = \"" + getDelete.company + "\" AND Region = \"" + getDelete.region + "\" AND 'Site Group' = \"" + getDelete.siteGroup + "\" AND Site = \"" + getDelete.site + "\" AND Status = 1 ";
                queryApp = "Company = \"" + getDelete.company + "\" AND Site = \"" + getDelete.site + "\" AND 'Status__c' = 0 ";
            } else if (objectType == 2) {
                query = "'Categorization Tier 3' = \"" + getDelete.opcat3 + "\" AND 'Product Categorization Tier 1' = \"" + getDelete.opcat4 + "\" AND Status = 1";
                queryApp = "'Opcat 3' = \"" + getDelete.opcat3 + "\" AND 'Opcat 4' = \"" + getDelete.opcat4 + "\" AND 'Status__c' = 0 ";
            } else if (objectType == 3) {
                query = "'Support Group ID' = \"" + getDelete.supportGroupID + "\" AND Status = 1";
                queryApp = "'Support Group ID' = \"" + getDelete.supportGroupID + "\" AND 'Status__c' = 0 ";
            } else {
                return new Response("FAILED", "error", "object type not found");
            }

            Thread t1 = new Thread(() -> {
                try {
                    //get and offline record
                    deleteAssignmentRouting(delete, remedyAPI, query);
                } catch (ARException ex) {
                    logger.info("error delete : " + ex.toString());
                }
            });

            Thread t2 = new Thread(() -> {
                try {
                    deleteCustomApproval(delete, remedyAPI, queryApp);
                } catch (ARException ex) {
                    logger.info("error delete : " + ex.toString());
                }
            });

            t1.start();
            t2.start();

        } catch (ARException e) {
            logger.info("error : " + e);
        }

        return new Response("SUCCESSFUL", "-", "-");
    }

    public String getValueFromRemedy(Entry requestRecord, Object fieldID) {
        if (requestRecord.get(fieldID).getValue() == null) {
            return "";
        }

        return requestRecord.get(fieldID).getValue().toString();
    }

    public synchronized void deleteAssignmentRouting(SubmodulDelete delete, RemedyAPI remedyAPI, String query) throws ARException {
        List<EntryListInfo> oldInfos = remedyAPI.getRemedyRecordByQuery(serverUser, configValue.getRemedyFormAssignment(), query);

        oldInfos.parallelStream().forEach(oldInfo -> {

            try {
                Entry recordOld = serverUser.getEntry(configValue.getRemedyFormAssignment(), oldInfo.getEntryID(), null);

                recordOld.put(7, new Value(2));
                recordOld.put(536870916, new Value(delete.notes));
                serverUser.setEntry(configValue.getRemedyFormAssignment(), oldInfo.getEntryID(), recordOld, null, 0);
            } catch (ARException ex) {
                logger.info("error delete record : " + ex.toString());
            }

        });

    }

    public synchronized void deleteCustomApproval(SubmodulDelete delete, RemedyAPI remedyAPI, String query) throws ARException {
        String formApprovalSite = "PTM:SSC:IT:FormApprovalSite";
        List<EntryListInfo> oldInfos = remedyAPI.getRemedyRecordByQuery(serverUser, formApprovalSite, query);

        oldInfos.parallelStream().forEach(oldInfo -> {

            try {
                Entry recordOld = serverUser.getEntry(formApprovalSite, oldInfo.getEntryID(), null);

                recordOld.put(7, new Value(1));
                recordOld.put(536870924, new Value(delete.notes));
                serverUser.setEntry(formApprovalSite, oldInfo.getEntryID(), recordOld, null, 0);
            } catch (ARException ex) {
                logger.info("error delete record approval : " + ex.toString());
            }

        });

    }

}
