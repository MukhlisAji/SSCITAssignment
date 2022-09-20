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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import mii.ptm.sscit.domain.ConfigFile;
import mii.ptm.sscit.domain.ConfigurationValue;
import mii.ptm.sscit.domain.DataInsert;
import mii.ptm.sscit.domain.RemedyAPI;
import mii.ptm.sscit.domain.Response;
import mii.ptm.sscit.domain.SubmodulUpdate;
import mii.ptm.sscit.domain.SupportGroup;
import mii.ptm.sscit.remedy.RemedyConnection;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author mukhlisaj
 */
public class Update {

    private static Logger logger = Logger.getLogger("Update Modul");

    ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
    ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

    RemedyConnection remedyConnection = new RemedyConnection();
    ARServerUser serverUser = remedyConnection.buildconnection(configValue);

    public Response getUpdateSite(SubmodulUpdate update) {
        RemedyAPI remedyAPI = new RemedyAPI();

        try {

            //getDataformStaging
            List<EntryListInfo> siteInfos = remedyAPI.getRemedyRecordByQuery(serverUser, configValue.getRemedyMiddleFormStaging(), "'SR Type Field 30__c' = \"" + update.requestID + "\" AND 'Status__c' = 0");

            SubmodulUpdate getUpdate = new SubmodulUpdate();
            for (EntryListInfo siteInfo : siteInfos) {
                Entry siteRecord = serverUser.getEntry(configValue.getRemedyMiddleFormStaging(), siteInfo.getEntryID(), null);
                getUpdate.oldCompany = getValueFromRemedy(siteRecord, 1000000001);
                getUpdate.oldRegion = getValueFromRemedy(siteRecord, 200000012);
                getUpdate.oldSiteGroup = getValueFromRemedy(siteRecord, 200000007);
                getUpdate.oldSite = getValueFromRemedy(siteRecord, 260000001);
                getUpdate.company = getValueFromRemedy(siteRecord, 536870948);
                getUpdate.region = getValueFromRemedy(siteRecord, 536870949);
                getUpdate.siteGroup = getValueFromRemedy(siteRecord, 536870950);
                getUpdate.site = getValueFromRemedy(siteRecord, 536870951);

            }
            //get and update all old record site

            List<EntryListInfo> oldSiteInfos = remedyAPI.getRemedyRecordByQuery(serverUser, configValue.getRemedyFormAssignment(),
                    "Company = \"" + getUpdate.oldCompany + "\" AND Site = \"" + getUpdate.oldSite + "\" AND Status = 1");

            oldSiteInfos.parallelStream().forEach(oldSiteInfo -> {

                try {
                    Entry recordOldSite = serverUser.getEntry(configValue.getRemedyFormAssignment(), oldSiteInfo.getEntryID(), null);

                    DataInsert dataInsert = new DataInsert();
                    dataInsert.opcat1 = getValueFromRemedy(recordOldSite, 1000000063);
                    dataInsert.opcat2 = getValueFromRemedy(recordOldSite, 1000000064);
                    dataInsert.opcat3 = getValueFromRemedy(recordOldSite, 1000000065);
                    dataInsert.opcat4 = getValueFromRemedy(recordOldSite, 200000003);
                    dataInsert.serviceType = getValueFromRemedy(recordOldSite, 536870914);
                    dataInsert.supportCompany = getValueFromRemedy(recordOldSite, 1000000251);
                    dataInsert.supportOrg = getValueFromRemedy(recordOldSite, 1000000014);
                    dataInsert.supportGroup = getValueFromRemedy(recordOldSite, 1000000217);
                    dataInsert.sgID = getValueFromRemedy(recordOldSite, 1000000079);
                    dataInsert.sgType = getValueFromRemedy(recordOldSite, 536870915);
                    dataInsert.company = getUpdate.company;
                    dataInsert.region = getUpdate.region;
                    dataInsert.siteGroup = getUpdate.siteGroup;
                    dataInsert.site = getUpdate.site;
                    dataInsert.submitter = update.submitter;
                    dataInsert.notes = update.notes;

                    insertDataAssignment(dataInsert);

                    recordOldSite.put(7, new Value(2));
                    recordOldSite.put(536870916, new Value(update.notes));
                    serverUser.setEntry(configValue.getRemedyFormAssignment(), oldSiteInfo.getEntryID(), recordOldSite, null, 0);
                } catch (ARException ex) {
                    logger.info("error : " + ex);
                }

            });

            String formApprovalSite = "PTM:SSC:IT:FormApprovalSite";
            List<EntryListInfo> approvalSites = remedyAPI.getRemedyRecordByQuery(serverUser, formApprovalSite,
                    "Company = \"" + getUpdate.oldCompany + "\" AND Site = \"" + getUpdate.oldSite + "\" AND 'Status__c' = 0");

            approvalSites.parallelStream().forEach(approvalSite -> {

                try {
                    Entry oldApprovalRecord = serverUser.getEntry(formApprovalSite, approvalSite.getEntryID(), null);

                    DataInsert dataInsert = new DataInsert();
                    dataInsert.opcat1 = getValueFromRemedy(oldApprovalRecord, 536870913);
                    dataInsert.opcat2 = getValueFromRemedy(oldApprovalRecord, 536870914);
                    dataInsert.opcat3 = getValueFromRemedy(oldApprovalRecord, 536870915);
                    dataInsert.opcat4 = getValueFromRemedy(oldApprovalRecord, 536870922);
                    dataInsert.serviceType = getValueFromRemedy(oldApprovalRecord, 536870925);
                    dataInsert.supportCompany = getValueFromRemedy(oldApprovalRecord, 536870916);
                    dataInsert.supportOrg = getValueFromRemedy(oldApprovalRecord, 536870917);
                    dataInsert.supportGroup = getValueFromRemedy(oldApprovalRecord, 536870921);
                    dataInsert.sgID = getValueFromRemedy(oldApprovalRecord, 536870927);
                    dataInsert.sgType = getValueFromRemedy(oldApprovalRecord, 536870926);
                    dataInsert.company = getUpdate.company;
                    dataInsert.site = getUpdate.site;
                    dataInsert.submitter = update.submitter;
                    dataInsert.notes = update.notes;

                    insertDataCustApproval(dataInsert);

                    oldApprovalRecord.put(7, new Value(1));
                    oldApprovalRecord.put(536870924, new Value(update.notes));
                    serverUser.setEntry(formApprovalSite, approvalSite.getEntryID(), oldApprovalRecord, null, 0);
                } catch (ARException ex) {
                    logger.info("error : " + ex);

                }

            });

        } catch (ARException e) {

            logger.info("error : " + e);
            return new Response("FAILED", "", "");

        }

        return new Response("SUCCESSFUL", "", "");

    }

    public Response getUpdateService(SubmodulUpdate update) {
        RemedyAPI remedyAPI = new RemedyAPI();

        try {

            //getDataformStaging
            List<EntryListInfo> serviceInfos = remedyAPI.getRemedyRecordByQuery(serverUser, configValue.getRemedyMiddleFormStaging(), "'SR Type Field 30__c' = \"" + update.requestID + "\" AND 'Status__c' = 0");

            SubmodulUpdate getUpdate = new SubmodulUpdate();
            for (EntryListInfo serviceInfo : serviceInfos) {
                Entry siteRecord = serverUser.getEntry(configValue.getRemedyMiddleFormStaging(), serviceInfo.getEntryID(), null);
                getUpdate.oldOpcat1 = getValueFromRemedy(siteRecord, 536870992);
                getUpdate.oldOpcat2 = getValueFromRemedy(siteRecord, 536870993);
                getUpdate.oldOpcat3 = getValueFromRemedy(siteRecord, 536870994);
                getUpdate.oldOpcat4 = getValueFromRemedy(siteRecord, 200000003);
                getUpdate.oldServiceType = getValueFromRemedy(siteRecord, 536871025);
                getUpdate.opcat1 = getValueFromRemedy(siteRecord, 536870957);
                getUpdate.opcat2 = getValueFromRemedy(siteRecord, 536870958);
                getUpdate.opcat3 = getValueFromRemedy(siteRecord, 536870959);
                getUpdate.opcat4 = getValueFromRemedy(siteRecord, 536870960);
                getUpdate.serviceType = getValueFromRemedy(siteRecord, 536871026);

            }
            //get and update all old record service mapping

            List<EntryListInfo> oldServiceInfos = remedyAPI.getRemedyRecordByQuery(serverUser, configValue.getRemedyFormAssignment(),
                    "'Categorization Tier 1' = \"" + getUpdate.oldOpcat1 + "\" AND 'Categorization Tier 3' = \"" + getUpdate.oldOpcat3 + "\" AND 'Product Categorization Tier 1' = \"" + getUpdate.oldOpcat4 + "\" AND Status = 1");
            oldServiceInfos.parallelStream().forEach(oldServiceInfo -> {
                try {
                    Entry recordOldService = serverUser.getEntry(configValue.getRemedyFormAssignment(), oldServiceInfo.getEntryID(), null);
                    DataInsert dataInsert = new DataInsert();
                    dataInsert.opcat1 = getUpdate.opcat1;
                    dataInsert.opcat2 = getUpdate.opcat2;
                    dataInsert.opcat3 = getUpdate.opcat3;
                    dataInsert.opcat4 = getUpdate.opcat4;
                    dataInsert.serviceType = getUpdate.serviceType;
                    dataInsert.supportCompany = getValueFromRemedy(recordOldService, 1000000251);
                    dataInsert.supportOrg = getValueFromRemedy(recordOldService, 1000000014);
                    dataInsert.supportGroup = getValueFromRemedy(recordOldService, 1000000217);
                    dataInsert.sgID = getValueFromRemedy(recordOldService, 1000000079);
                    dataInsert.sgType = getValueFromRemedy(recordOldService, 536870915);
                    dataInsert.company = getValueFromRemedy(recordOldService, 1000000001);
                    dataInsert.region = getValueFromRemedy(recordOldService, 200000012);
                    dataInsert.siteGroup = getValueFromRemedy(recordOldService, 200000007);
                    dataInsert.site = getValueFromRemedy(recordOldService, 260000001);
                    dataInsert.submitter = update.submitter;
                    dataInsert.notes = update.notes;

                    insertDataAssignment(dataInsert);

                    recordOldService.put(7, new Value(2));
                    recordOldService.put(536870916, new Value(update.notes));
                    serverUser.setEntry(configValue.getRemedyFormAssignment(), oldServiceInfo.getEntryID(), recordOldService, null, 0);
                } catch (ARException ex) {
                    logger.info("error : " + ex);
                }

            });

//            for (EntryListInfo oldServiceInfo : oldServiceInfos) {
//                
//            }
            String formApprovalSite = "PTM:SSC:IT:FormApprovalSite";
            List<EntryListInfo> approvalSites = remedyAPI.getRemedyRecordByQuery(serverUser, formApprovalSite,
                    " 'Opcat 1' = \"" + getUpdate.oldOpcat1 + "\" AND 'Opcat 3' = \"" + getUpdate.oldOpcat3 + "\" AND 'Opcat 4' = \"" + getUpdate.oldOpcat4 + "\" AND 'Status__c' = 0");

            approvalSites.parallelStream().forEach(approvalSite -> {
                try {
                    Entry oldApprovalRecord = serverUser.getEntry(formApprovalSite, approvalSite.getEntryID(), null);

                    DataInsert dataInsert = new DataInsert();
                    dataInsert.opcat1 = getUpdate.opcat1;
                    dataInsert.opcat2 = getUpdate.opcat2;
                    dataInsert.opcat3 = getUpdate.opcat3;
                    dataInsert.opcat4 = getUpdate.opcat4;
                    dataInsert.serviceType = getUpdate.serviceType;
                    dataInsert.supportCompany = getValueFromRemedy(oldApprovalRecord, 536870916);
                    dataInsert.supportOrg = getValueFromRemedy(oldApprovalRecord, 536870917);
                    dataInsert.supportGroup = getValueFromRemedy(oldApprovalRecord, 536870921);
                    dataInsert.sgID = getValueFromRemedy(oldApprovalRecord, 536870927);
                    dataInsert.sgType = getValueFromRemedy(oldApprovalRecord, 536870926);
                    dataInsert.company = getValueFromRemedy(oldApprovalRecord, 536870919);
                    dataInsert.site = getValueFromRemedy(oldApprovalRecord, 536870918);
                    dataInsert.submitter = update.submitter;
                    dataInsert.notes = update.notes;

                    insertDataCustApproval(dataInsert);

                    oldApprovalRecord.put(7, new Value(1));
                    oldApprovalRecord.put(536870924, new Value(update.notes));
                    serverUser.setEntry(formApprovalSite, approvalSite.getEntryID(), oldApprovalRecord, null, 0);
                } catch (ARException ex) {
                    logger.info("error : " + ex);
                }

            });

//            for (EntryListInfo approvalSite : approvalSites) {
//                
//            }
        } catch (ARException e) {

            logger.info("error : " + e);

        }

        return new Response("SUCCESSFUL", "", "");

    }

    public Response getUpdateSG(SubmodulUpdate update) {
        RemedyAPI remedyAPI = new RemedyAPI();

        try {

            //getDataformStaging
            List<EntryListInfo> sgInfos = remedyAPI.getRemedyRecordByQuery(serverUser, configValue.getRemedyMiddleFormStaging(), "'SR Type Field 30__c' = \"" + update.requestID + "\" AND 'Status__c' = 0");

            SubmodulUpdate getUpdate = new SubmodulUpdate();
            for (EntryListInfo sgInfo : sgInfos) {
                Entry sgRecord = serverUser.getEntry(configValue.getRemedyMiddleFormStaging(), sgInfo.getEntryID(), null);
                logger.info("record : " + sgRecord);
                getUpdate.oldSupportCompany = getValueFromRemedy(sgRecord, 1000000251);
                getUpdate.oldSupportOrg = getValueFromRemedy(sgRecord, 1000000014);
                getUpdate.oldSupportGroup = getValueFromRemedy(sgRecord, 1000000217);
                getUpdate.oldSupportGroupID = getValueFromRemedy(sgRecord, 536871020);
                getUpdate.oldSgType = getValueFromRemedy(sgRecord, 536870998);
                getUpdate.supportCompany = getValueFromRemedy(sgRecord, 536870954);
                getUpdate.supportOrg = getValueFromRemedy(sgRecord, 536870955);
                getUpdate.supportGroup = getValueFromRemedy(sgRecord, 536870956);
                getUpdate.supportGroupID = getValueFromRemedy(sgRecord, 536870996);
                getUpdate.sgType = getValueFromRemedy(sgRecord, 536871007);
                getUpdate.sgDescription = getValueFromRemedy(sgRecord, 536871016);

            }

            if (getUpdate.sgDescription.contains("Approval Group")) {
                //get and update all old record SG mapping
                List<EntryListInfo> approvalSites = remedyAPI.getRemedyRecordByQuery(serverUser, configValue.getRemedyMiddleFormCustomApproval(),
                        "'Support Group' = \"" + getUpdate.oldSupportGroup + "\" AND 'Status__c' = 0");

                approvalSites.parallelStream().forEach(approvalSite -> {

                    try {
                        Entry oldApprovalRecord = serverUser.getEntry(configValue.getRemedyMiddleFormCustomApproval(), approvalSite.getEntryID(), null);

                        DataInsert dataInsert = new DataInsert();
                        dataInsert.opcat1 = getValueFromRemedy(oldApprovalRecord, 536870913);
                        dataInsert.opcat2 = getValueFromRemedy(oldApprovalRecord, 536870914);
                        dataInsert.opcat3 = getValueFromRemedy(oldApprovalRecord, 536870915);
                        dataInsert.opcat4 = getValueFromRemedy(oldApprovalRecord, 536870922);
                        dataInsert.serviceType = getValueFromRemedy(oldApprovalRecord, 536870925);
                        dataInsert.supportCompany = getUpdate.supportCompany;
                        dataInsert.supportOrg = getUpdate.supportOrg;
                        dataInsert.supportGroup = getUpdate.supportGroup;
                        dataInsert.sgID = getUpdate.supportGroupID;
                        dataInsert.sgType = getUpdate.sgType;
                        dataInsert.company = getValueFromRemedy(oldApprovalRecord, 536870919);
                        dataInsert.site = getValueFromRemedy(oldApprovalRecord, 536870918);
                        dataInsert.submitter = update.submitter;
                        dataInsert.notes = update.notes;

                        insertDataCustApproval(dataInsert);

                        oldApprovalRecord.put(7, new Value(1));
                        oldApprovalRecord.put(536870924, new Value(update.notes));
                        serverUser.setEntry(configValue.getRemedyMiddleFormCustomApproval(), approvalSite.getEntryID(), oldApprovalRecord, null, 0);
                    } catch (ARException ex) {
                        logger.info("error : " + ex.toString());
                    }

                });

            } else {

                List<EntryListInfo> oldSGInfos = remedyAPI.getRemedyRecordByQuery(serverUser, configValue.getRemedyFormAssignment(),
                        "'Support Group ID' = \"" + getUpdate.oldSupportGroupID + "\" AND Status = 1");

                oldSGInfos.parallelStream().forEach(oldSGInfo -> {

                    try {
                        Entry recordOldSG = serverUser.getEntry(configValue.getRemedyFormAssignment(), oldSGInfo.getEntryID(), null);
                        logger.info("old : " + recordOldSG);
                        DataInsert dataInsert = new DataInsert();
                        dataInsert.opcat1 = getValueFromRemedy(recordOldSG, 1000000063);
                        dataInsert.opcat2 = getValueFromRemedy(recordOldSG, 1000000064);
                        dataInsert.opcat3 = getValueFromRemedy(recordOldSG, 1000000065);
                        dataInsert.opcat4 = getValueFromRemedy(recordOldSG, 200000003);
                        dataInsert.serviceType = getValueFromRemedy(recordOldSG, 536870914);
                        dataInsert.supportCompany = getUpdate.supportCompany;
                        dataInsert.supportOrg = getUpdate.supportOrg;
                        dataInsert.supportGroup = getUpdate.supportGroup;
                        dataInsert.sgID = getUpdate.supportGroupID;
                        dataInsert.sgType = getUpdate.sgType;
                        dataInsert.company = getValueFromRemedy(recordOldSG, 1000000001);
                        dataInsert.region = getValueFromRemedy(recordOldSG, 200000012);
                        dataInsert.siteGroup = getValueFromRemedy(recordOldSG, 200000007);
                        dataInsert.site = getValueFromRemedy(recordOldSG, 260000001);
                        dataInsert.submitter = update.submitter;
                        dataInsert.notes = update.notes;

                        insertDataAssignment(dataInsert);

                        recordOldSG.put(7, new Value(2));
                        recordOldSG.put(536870916, new Value(update.notes));
                        serverUser.setEntry(configValue.getRemedyFormAssignment(), oldSGInfo.getEntryID(), recordOldSG, null, 0);
                    } catch (ARException ex) {
                        logger.info("error : " + ex.toString());
                    }

                });
            }

        } catch (ARException e) {

            logger.info("error : " + e);

        }

        return new Response("SUCCESSFUL", "", "");

    }

    public Response getUpdateServiceToSG(SubmodulUpdate update) {
        RemedyAPI remedyAPI = new RemedyAPI();

        try {

            //getDataformStaging
            List<EntryListInfo> serviceInfos = remedyAPI.getRemedyRecordByQuery(serverUser, configValue.getRemedyMiddleFormStaging(), "'SR Type Field 30__c' = \"" + update.requestID + "\" AND 'Status__c' = 0");

            for (EntryListInfo serviceInfo : serviceInfos) {
                Entry sgRecord = serverUser.getEntry(configValue.getRemedyMiddleFormStaging(), serviceInfo.getEntryID(), null);
                SubmodulUpdate getUpdate = new SubmodulUpdate();
                getUpdate.oldOpcat1 = getValueFromRemedy(sgRecord, 536870992);
                getUpdate.oldOpcat2 = getValueFromRemedy(sgRecord, 536870993);
                getUpdate.oldOpcat3 = getValueFromRemedy(sgRecord, 536870994);
                getUpdate.oldOpcat4 = getValueFromRemedy(sgRecord, 200000003);
                getUpdate.oldServiceType = getValueFromRemedy(sgRecord, 536871025);
                getUpdate.oldSupportCompany = getValueFromRemedy(sgRecord, 1000000251);
                getUpdate.oldSupportOrg = getValueFromRemedy(sgRecord, 1000000014);
                getUpdate.oldSupportGroup = getValueFromRemedy(sgRecord, 1000000217);
                getUpdate.oldSupportGroupID = getValueFromRemedy(sgRecord, 536871020);
                getUpdate.oldSgType = getValueFromRemedy(sgRecord, 536870998);
                getUpdate.sgType = getValueFromRemedy(sgRecord, 536871007);

                //sgName manipulation
                String sgLastName = getUpdate.oldSgType.substring(12);
                String newSgLastName = getUpdate.sgType.substring(12);
                String sgName = getUpdate.oldSupportGroup.replace(sgLastName, newSgLastName);

                //get data SG based on Grouping
                String formSupportGroup = "CTM:Support Group";
                List<EntryListInfo> sgInfos = remedyAPI.getRemedyRecordByQuery(serverUser, formSupportGroup, "'Support Group Name' = \"" + sgName + "\" AND 'Support Organization' = \"" + getUpdate.oldSupportOrg + "\"");
                if (sgInfos.isEmpty()) {
                    logger.info("Error : No Match Support Group Found on the Grouping Group Selected!!!");

                } else {
                    SupportGroup sg = new SupportGroup();
                    for (EntryListInfo sgInfo : sgInfos) {
                        Entry siteRecord = serverUser.getEntry(formSupportGroup, sgInfo.getEntryID(), null);
                        getUpdate.supportCompany = getValueFromRemedy(siteRecord, 1000000001);
                        getUpdate.supportOrg = getValueFromRemedy(siteRecord, 1000000014);
                        getUpdate.supportGroup = getValueFromRemedy(siteRecord, 1000000015);
                        getUpdate.supportGroupID = getValueFromRemedy(siteRecord, 1);
                        getUpdate.sgType = getValueFromRemedy(siteRecord, 536870918);

                    }
                }

                String query = "'Categorization Tier 1' = \"" + getUpdate.oldOpcat1 + "\" AND 'Categorization Tier 3' = \"" + getUpdate.oldOpcat3 + "\" AND 'Product Categorization Tier 1' = \"" + getUpdate.oldOpcat1 + "\" AND 'Support Group ID' = \"" + getUpdate.oldSupportGroupID + "\" AND Status = 1";
                Thread t1 = new Thread(() -> {
                    try {
                        logger.info("query : " + query);
                        //get and update all old record SG mapping
                        getDataAssignmentRouting(getUpdate, update, remedyAPI, query);
                    } catch (ARException ex) {
                        logger.info("error : " + ex.toString());
                    }

                });
                Thread t2 = new Thread(() -> {
                    try {
                        //update form approval site
                        String query4Approval = "'Opcat 1' = \"" + getUpdate.oldOpcat1 + "\" AND 'Opcat 3' = \"" + getUpdate.oldOpcat3 + "\" AND 'Opcat 1' = \"" + getUpdate.oldOpcat1 + "\" AND 'Support Group' = \"" + getUpdate.oldSupportGroup + "\" AND 'Status__c' = 0";
                        getDataApproval(getUpdate, update, remedyAPI, query4Approval);
                    } catch (ARException ex) {
                        logger.info("error : " + ex.toString());
                    }
                });

                t1.start();
                t2.start();
            }
        } catch (ARException e) {

            logger.info("error : " + e);

        }

        return new Response("SUCCESSFUL", "", "");

    }

    public Response getUpdateSiteToSG(SubmodulUpdate update) {
        RemedyAPI remedyAPI = new RemedyAPI();

        try {

            //getDataformStaging
            List<EntryListInfo> siteInfos = remedyAPI.getRemedyRecordByQuery(serverUser, configValue.getRemedyMiddleFormStaging(), "'SR Type Field 30__c' = \"" + update.requestID + "\"");
            logger.info("sites : " + siteInfos);

            for (EntryListInfo siteInfo : siteInfos) {
                Entry siteRecord = serverUser.getEntry(configValue.getRemedyMiddleFormStaging(), siteInfo.getEntryID(), null);

                SubmodulUpdate getUpdate = new SubmodulUpdate();
                getUpdate.oldCompany = getValueFromRemedy(siteRecord, 1000000001);
                getUpdate.oldRegion = getValueFromRemedy(siteRecord, 200000012);
                getUpdate.oldSiteGroup = getValueFromRemedy(siteRecord, 200000007);
                getUpdate.oldSite = getValueFromRemedy(siteRecord, 260000001);
                getUpdate.oldSupportCompany = getValueFromRemedy(siteRecord, 1000000251);
                getUpdate.oldSupportOrg = getValueFromRemedy(siteRecord, 1000000014);
                getUpdate.oldSupportGroup = getValueFromRemedy(siteRecord, 1000000217);
                getUpdate.oldSupportGroupID = getValueFromRemedy(siteRecord, 536871020);
                getUpdate.oldSgType = getValueFromRemedy(siteRecord, 536870998);
                getUpdate.supportCompany = getValueFromRemedy(siteRecord, 536870954);
                getUpdate.supportOrg = getValueFromRemedy(siteRecord, 536870955);
                getUpdate.supportGroup = getValueFromRemedy(siteRecord, 536870956);
                getUpdate.supportGroupID = getValueFromRemedy(siteRecord, 536870996);
                getUpdate.sgType = getValueFromRemedy(siteRecord, 536871007);

                //get old data based on site and sg selected
                //and update all old record SG mapping
                Thread t1 = new Thread(() -> {
                    try {
                        String query = "'Company' = \"" + getUpdate.oldCompany + "\" AND 'Site' = \"" + getUpdate.oldSite + "\" AND 'Support Group ID' = \"" + getUpdate.oldSupportGroupID + "\" AND Status = 1";
                        logger.info("query : " + query);
                        getDataAssignmentRouting(getUpdate, update, remedyAPI, query);
                    } catch (ARException ex) {
                        logger.info("error get data assignment : " + ex.toString());
                    }

                });

                //update form approval sitee
                Thread t2 = new Thread(() -> {
                    try {
                        String query4Approval = " 'Company' = \"" + getUpdate.oldCompany + "\" AND 'Site' = \"" + getUpdate.oldSite + "\" AND 'Support Group' = \"" + getUpdate.oldSupportGroup + "\" AND 'Status__c' = 0";
                        logger.info("query App : " + query4Approval);
                        getDataApproval(getUpdate, update, remedyAPI, query4Approval);
                    } catch (ARException ex) {
                        logger.info("error get data Approval : " + ex.toString());
                    }

                });

                t1.start();
                t2.start();
            }
        } catch (ARException e) {

            logger.info("error : " + e);

        }

        return new Response("SUCCESSFUL", "", "");

    }

    public String getValueFromRemedy(Entry requestRecord, Object fieldID) {
        if (requestRecord.get(fieldID).getValue() == null) {
            return "";
        }

        return requestRecord.get(fieldID).getValue().toString();
    }

    public void insertDataAssignment(DataInsert dataInsert) {
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);
        RemedyAPI remedyAPI = new RemedyAPI();
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser serverUser = remedyConnection.buildconnection(configValue);

        String queryAssign;
        if (dataInsert.opcat4.isEmpty()) {
            queryAssign = "Company = \"" + dataInsert.company + "\" AND Site = \"" + dataInsert.site
                    + "\" AND 'Categorization Tier 1' = \"" + dataInsert.opcat1 + "\" AND 'Categorization Tier 2' = \"" + dataInsert.opcat2 + "\" AND 'Categorization Tier 3' = \"" + dataInsert.opcat3
                    + "\" AND 'Support Group ID' = \"" + dataInsert.sgID + "\"";
        } else {
            queryAssign = "Company = \"" + dataInsert.company + "\" AND Site = \"" + dataInsert.site
                    + "\" AND 'Categorization Tier 1' = \"" + dataInsert.opcat1 + "\" AND 'Categorization Tier 2' = \"" + dataInsert.opcat2 + "\" AND 'Categorization Tier 3' = \"" + dataInsert.opcat3 + "\" AND 'Product Categorization Tier 1' = \"" + dataInsert.opcat4
                    + "\" AND 'Support Group ID' = \"" + dataInsert.sgID + "\"";
        }

        List<EntryListInfo> recordLis = remedyAPI.getRemedyRecordByQuery(serverUser, configValue.getRemedyFormAssignment(), queryAssign);

        if (recordLis.isEmpty()) {

            Entry recordEntry = new Entry();
            recordEntry.put(1000000063, new Value(dataInsert.opcat1));
            recordEntry.put(1000000064, new Value(dataInsert.opcat2));
            recordEntry.put(1000000065, new Value(dataInsert.opcat3));
            recordEntry.put(200000003, new Value(dataInsert.opcat4));
            recordEntry.put(1000000251, new Value(dataInsert.supportCompany));
            recordEntry.put(1000000014, new Value(dataInsert.supportOrg));
            recordEntry.put(1000000217, new Value(dataInsert.supportGroup));
            recordEntry.put(1000000079, new Value(dataInsert.sgID));
            recordEntry.put(1000000001, new Value(dataInsert.company));
            recordEntry.put(200000012, new Value(dataInsert.region));
            recordEntry.put(200000007, new Value(dataInsert.siteGroup));
            recordEntry.put(260000001, new Value(dataInsert.site));
            recordEntry.put(536870917, new Value(dataInsert.submitter));
            recordEntry.put(536870916, new Value(dataInsert.notes));
            recordEntry.put(536870914, new Value(dataInsert.serviceType));
            recordEntry.put(536870915, new Value(dataInsert.sgType));

            recordEntry.put(304005500, new Value("Work Order Assignee"));
            recordEntry.put(1000000400, new Value("Work Order Assignee"));
            recordEntry.put(1000000082, new Value("- Global - "));
            recordEntry.put(377771011, new Value("Yes"));

            try {
                String result = serverUser.createEntry(configValue.getRemedyFormAssignment(), recordEntry);
                logger.info("result : " + result.toString());

            } catch (ARException e) {
                // TODO Auto-generated catch block
                logger.info("entry failed " + e.toString());
            }
        } else {
            logger.info("Record already exist : "
                    + "\nSite          : " + dataInsert.site
                    + "\nSupport Group : " + dataInsert.supportGroup
                    + "\nService       : " + dataInsert.opcat1 + " > "
                    + dataInsert.opcat2 + " > " + dataInsert.opcat3 + " > " + dataInsert.opcat4);

        }

    }

    public void insertDataCustApproval(DataInsert dataInsert) {
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser serverUser = remedyConnection.buildconnection(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();
        
        String queryApproval;
        if (dataInsert.opcat4.isEmpty()) {
            queryApproval = "Company = \"" + dataInsert.company + "\" AND Site = \"" + dataInsert.site
                    + "\" AND 'Opcat 1' = \"" + dataInsert.opcat1 + "\" AND 'Opcat 3' = \"" + dataInsert.opcat3
                    + "\" AND 'Support Group' = \"" + dataInsert.supportGroup + "\"";
        } else {
            queryApproval = "Company = \"" + dataInsert.company + "\" AND Site = \"" + dataInsert.site
                    + "\" AND 'Opcat 1' = \"" + dataInsert.opcat1 + "\" AND 'Opcat 3' = \"" + dataInsert.opcat3 + "\" AND 'Opcat 4' = \"" + dataInsert.opcat4
                    + "\" AND 'Support Group' = \"" + dataInsert.supportGroup + "\"";
        }

        List<EntryListInfo> elis = remedyAPI.getRemedyRecordByQuery(serverUser, configValue.getRemedyMiddleFormCustomApproval(), queryApproval);

        if (elis.isEmpty()) {
            Entry recordEntry = new Entry();
            recordEntry.put(536870913, new Value(dataInsert.opcat1));
            recordEntry.put(536870914, new Value(dataInsert.opcat2));
            recordEntry.put(536870915, new Value(dataInsert.opcat3));
            recordEntry.put(536870922, new Value(dataInsert.opcat4));
            recordEntry.put(536870916, new Value(dataInsert.supportCompany));
            recordEntry.put(536870917, new Value(dataInsert.supportOrg));
            recordEntry.put(536870921, new Value(dataInsert.supportGroup));
            recordEntry.put(536870927, new Value(dataInsert.sgID));
            recordEntry.put(536870919, new Value(dataInsert.company));
            recordEntry.put(536870918, new Value(dataInsert.site));
            recordEntry.put(2, new Value(dataInsert.submitter));
            recordEntry.put(536870924, new Value(dataInsert.notes));
            recordEntry.put(536870925, new Value(dataInsert.serviceType));
            recordEntry.put(536870926, new Value(dataInsert.sgType));

            try {
                String result = serverUser.createEntry(configValue.getRemedyMiddleFormCustomApproval(), recordEntry);
                logger.info("result : " + result);
            } catch (ARException e) {
                // TODO Auto-generated catch block
                logger.info("entry failed" + e.toString());
            }
        } else {
            logger.info("Record already exist : "
                    + "\nSite          : " + dataInsert.site
                    + "\nSupport Group : " + dataInsert.supportGroup
                    + "\nService       : " + dataInsert.opcat1 + " > "
                    + dataInsert.opcat2 + " > " + dataInsert.opcat3 + " > " + dataInsert.opcat4);

        }

    }

    public void getDataApproval(SubmodulUpdate getUpdate, SubmodulUpdate update, RemedyAPI remedyAPI, String query) throws ARException {
        //update form approval site

        List<EntryListInfo> approvalSiteInfos = remedyAPI.getRemedyRecordByQuery(serverUser, configValue.getRemedyMiddleFormCustomApproval(),
                query);

        approvalSiteInfos.parallelStream().forEach(approvalSiteInfo -> {
            try {
                Entry oldApprovalRecord = serverUser.getEntry(configValue.getRemedyMiddleFormCustomApproval(), approvalSiteInfo.getEntryID(), null);

                DataInsert dataInsert = new DataInsert();
                //service
                dataInsert.opcat1 = getValueFromRemedy(oldApprovalRecord, 536870913);
                dataInsert.opcat2 = getValueFromRemedy(oldApprovalRecord, 536870914);
                dataInsert.opcat3 = getValueFromRemedy(oldApprovalRecord, 536870915);
                dataInsert.opcat4 = getValueFromRemedy(oldApprovalRecord, 536870922);
                dataInsert.serviceType = getValueFromRemedy(oldApprovalRecord, 536870925);
                //sg
                dataInsert.supportCompany = getUpdate.supportCompany;
                dataInsert.supportOrg = getUpdate.supportOrg;
                dataInsert.supportGroup = getUpdate.supportGroup;
                dataInsert.sgID = getUpdate.supportGroupID;
                dataInsert.sgType = getUpdate.sgType;
                //site
                dataInsert.company = getValueFromRemedy(oldApprovalRecord, 536870918);
                dataInsert.site = getValueFromRemedy(oldApprovalRecord, 536870919);

                dataInsert.submitter = update.submitter;
                dataInsert.notes = update.notes;

                insertDataCustApproval(dataInsert);

                oldApprovalRecord.put(7, new Value(1));
                oldApprovalRecord.put(536870924, new Value(update.notes));

                serverUser.setEntry(configValue.getRemedyMiddleFormCustomApproval(), approvalSiteInfo.getEntryID(), oldApprovalRecord, null, 0);
            } catch (ARException ex) {
                logger.info("error  : " + ex.toString());
            }

        });

    }

    public void getDataAssignmentRouting(SubmodulUpdate getUpdate, SubmodulUpdate update, RemedyAPI remedyAPI, String query) throws ARException {
        List<EntryListInfo> oldSGInfos = remedyAPI.getRemedyRecordByQuery(serverUser, configValue.getRemedyFormAssignment(), query);

        oldSGInfos.parallelStream().forEach(oldSGInfo -> {
            try {
                Entry recordOldSG = serverUser.getEntry(configValue.getRemedyFormAssignment(), oldSGInfo.getEntryID(), null);

                DataInsert dataInsert = new DataInsert();
                //service
                dataInsert.opcat1 = getValueFromRemedy(recordOldSG, 1000000063);
                dataInsert.opcat2 = getValueFromRemedy(recordOldSG, 1000000064);
                dataInsert.opcat3 = getValueFromRemedy(recordOldSG, 1000000065);
                dataInsert.opcat4 = getValueFromRemedy(recordOldSG, 200000003);
                dataInsert.serviceType = getValueFromRemedy(recordOldSG, 536870914);
                //sg
                dataInsert.supportCompany = getUpdate.supportCompany;
                dataInsert.supportOrg = getUpdate.supportOrg;
                dataInsert.supportGroup = getUpdate.supportGroup;
                dataInsert.sgID = getUpdate.supportGroupID;
                dataInsert.sgType = getUpdate.sgType;
                //site
                dataInsert.company = getValueFromRemedy(recordOldSG, 1000000001);
                dataInsert.region = getValueFromRemedy(recordOldSG, 200000012);
                dataInsert.siteGroup = getValueFromRemedy(recordOldSG, 200000007);
                dataInsert.site = getValueFromRemedy(recordOldSG, 260000001);

                dataInsert.submitter = update.submitter;
                dataInsert.notes = update.notes;

                insertDataAssignment(dataInsert);

                recordOldSG.put(7, new Value(2));
                recordOldSG.put(536870916, new Value(update.notes));
                serverUser.setEntry(configValue.getRemedyFormAssignment(), oldSGInfo.getEntryID(), recordOldSG, null, 0);
            } catch (ARException ex) {
                logger.info("error : " + ex.toString());
            }

        });

    }
}
