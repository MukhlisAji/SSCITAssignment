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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import mii.ptm.sscit.domain.ConfigFile;
import mii.ptm.sscit.domain.ConfigurationValue;
import mii.ptm.sscit.domain.DataInsert;
import mii.ptm.sscit.domain.RemedyAPI;
import mii.ptm.sscit.domain.Response;
import mii.ptm.sscit.domain.Services;
import mii.ptm.sscit.domain.Site;
import mii.ptm.sscit.domain.SubmodulADD;
import mii.ptm.sscit.domain.SupportGroup;
import mii.ptm.sscit.remedy.RemedyConnection;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author mukhlisaj
 */
public class Add {

    private final String sgForm = "CTM:Support Group";

    private static Logger logger = Logger.getLogger("Add Modul");

    public Response getADDSite(SubmodulADD add) {
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser serverUser = remedyConnection.buildconnection(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();

        try {

            //getDataformStaging
            List<EntryListInfo> siteInfos = remedyAPI.getRemedyRecordByQuery(serverUser, configValue.getRemedyMiddleFormStaging(), "'SR Type Field 30__c' = \"" + add.requestID + "\" AND 'Status__c' = 0");
            SubmodulADD addSite = new SubmodulADD();
            for (EntryListInfo siteInfo : siteInfos) {
                Entry siteRecord = serverUser.getEntry(configValue.getRemedyMiddleFormStaging(), siteInfo.getEntryID(), null);
                addSite.company = getValueFromRemedy(siteRecord, 1000000001);
                addSite.region = getValueFromRemedy(siteRecord, 200000012);
                addSite.siteGroup = getValueFromRemedy(siteRecord, 200000007);
                addSite.site = getValueFromRemedy(siteRecord, 260000001);

                addSite.companyTobeCopied = getValueFromRemedy(siteRecord, 536870948);
                addSite.regionTobeCopied = getValueFromRemedy(siteRecord, 536870949);
                addSite.siteGroupTobeCopied = getValueFromRemedy(siteRecord, 536870950);
                addSite.siteTobeCopied = getValueFromRemedy(siteRecord, 536870951);

            }

            //get and update all old record site
            List<EntryListInfo> similarSiteInfos = remedyAPI.getRemedyRecordByQuery(serverUser, configValue.getRemedyFormAssignment(),
                    "Company = \"" + addSite.companyTobeCopied + "\" AND Site = \"" + addSite.siteTobeCopied + "\" AND Status = 1");

            similarSiteInfos.parallelStream().forEach(oldSiteInfo -> {

                try {
                    Entry recordSimilarSite = serverUser.getEntry(configValue.getRemedyFormAssignment(), oldSiteInfo.getEntryID(), null);

                    DataInsert dataInsert = new DataInsert();
                    dataInsert.opcat1 = getValueFromRemedy(recordSimilarSite, 1000000063);
                    dataInsert.opcat2 = getValueFromRemedy(recordSimilarSite, 1000000064);
                    dataInsert.opcat3 = getValueFromRemedy(recordSimilarSite, 1000000065);
                    dataInsert.opcat4 = getValueFromRemedy(recordSimilarSite, 200000003);
                    dataInsert.serviceType = getValueFromRemedy(recordSimilarSite, 536870914);
                    dataInsert.supportCompany = getValueFromRemedy(recordSimilarSite, 1000000251);
                    dataInsert.supportOrg = getValueFromRemedy(recordSimilarSite, 1000000014);
                    dataInsert.supportGroup = getValueFromRemedy(recordSimilarSite, 1000000217);
                    dataInsert.sgID = getValueFromRemedy(recordSimilarSite, 1000000079);
                    dataInsert.sgType = getValueFromRemedy(recordSimilarSite, 536870915);
                    dataInsert.company = addSite.company;
                    dataInsert.region = addSite.region;
                    dataInsert.siteGroup = addSite.siteGroup;
                    dataInsert.site = addSite.site;
                    dataInsert.submitter = add.submitter;
                    dataInsert.notes = add.notes;

                    insertDataAssignment(dataInsert);
                } catch (ARException ex) {
                    logger.info("error : " + ex);
                }

            });

            String formApprovalSite = "PTM:SSC:IT:FormApprovalSite";
            List<EntryListInfo> approvalSites = remedyAPI.getRemedyRecordByQuery(serverUser, formApprovalSite,
                    "Company = \"" + addSite.companyTobeCopied + "\" AND Site = \"" + addSite.siteTobeCopied + "\" AND 'Status__c' = 0");

            approvalSites.parallelStream().forEach(approvalSite -> {

                try {
                    Entry similarApprovalRecord = serverUser.getEntry(formApprovalSite, approvalSite.getEntryID(), null);

                    DataInsert dataInsert = new DataInsert();
                    dataInsert.opcat1 = getValueFromRemedy(similarApprovalRecord, 536870913);
                    dataInsert.opcat2 = getValueFromRemedy(similarApprovalRecord, 536870914);
                    dataInsert.opcat3 = getValueFromRemedy(similarApprovalRecord, 536870915);
                    dataInsert.opcat4 = getValueFromRemedy(similarApprovalRecord, 536870922);
                    dataInsert.serviceType = getValueFromRemedy(similarApprovalRecord, 536870925);
                    dataInsert.supportCompany = getValueFromRemedy(similarApprovalRecord, 536870916);
                    dataInsert.supportOrg = getValueFromRemedy(similarApprovalRecord, 536870917);
                    dataInsert.supportGroup = getValueFromRemedy(similarApprovalRecord, 536870921);
                    dataInsert.sgID = getValueFromRemedy(similarApprovalRecord, 536870927);
                    dataInsert.sgType = getValueFromRemedy(similarApprovalRecord, 536870926);
                    dataInsert.company = addSite.company;
                    dataInsert.site = addSite.site;
                    dataInsert.submitter = add.submitter;
                    dataInsert.notes = add.notes;

                    insertDataCustApproval(dataInsert);

                } catch (ARException ex) {
                    logger.info("error : " + ex);

                }

            });

        } catch (ARException e) {

            logger.info("error : " + e);
            return new Response("FAILED", "", "");

        }
        return new Response("SUCCESSFUL", "-", "-");
    }

    public Response getADDService(SubmodulADD add) {
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser serverUser = remedyConnection.buildconnection(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();

        try {

            //getDataformStaging
            SubmodulADD addService = new SubmodulADD();
            List<EntryListInfo> serviceInfos = remedyAPI.getRemedyRecordByQuery(serverUser, configValue.getRemedyMiddleFormStaging(), "'SR Type Field 30__c' = \"" + add.requestID + "\" AND 'Status__c' = 0");

            logger.info("add : " + add.requestID);
            for (EntryListInfo serviceInfo : serviceInfos) {
                Entry serviceRecord = serverUser.getEntry(configValue.getRemedyMiddleFormStaging(), serviceInfo.getEntryID(), null);
                //service
                addService.opcat1 = getValueFromRemedy(serviceRecord, 536870992);
                addService.opcat2 = getValueFromRemedy(serviceRecord, 536870993);
                addService.opcat3 = getValueFromRemedy(serviceRecord, 536870994);
                addService.opcat4 = getValueFromRemedy(serviceRecord, 200000003);
                addService.serviceType = getValueFromRemedy(serviceRecord, 536871025);

                addService.supportCompany = getValueFromRemedy(serviceRecord, 1000000251);
                addService.supportOrg = getValueFromRemedy(serviceRecord, 1000000014);
                addService.supportGroup = getValueFromRemedy(serviceRecord, 1000000217);
                addService.sgID = getValueFromRemedy(serviceRecord, 536871020);
                addService.sgType = getValueFromRemedy(serviceRecord, 536870998);
                addService.sgDescription = getValueFromRemedy(serviceRecord, 536871016);

                SubmodulADD siteInfo = new SubmodulADD();

                List<String[]> siteRecord = new ArrayList<String[]>();

                if (addService.sgDescription.contains("Approval Group")) {

                    List<EntryListInfo> sgAssignments = remedyAPI.getRemedyRecordByQuery(serverUser, configValue.getRemedyMiddleFormCustomApproval(),
                            "'Support Group' = \"" + addService.supportGroup + "\" AND 'Status__c' = 0");

                    for (EntryListInfo sgAssignment : sgAssignments) {

                        try {
                            siteInfo = new SubmodulADD();
                            Entry sgAssignmentRecord = serverUser.getEntry(configValue.getRemedyMiddleFormCustomApproval(), sgAssignment.getEntryID(), null);
                            siteInfo.company = getValueFromRemedy(sgAssignmentRecord, 536870919);
                            siteInfo.region = "";
                            siteInfo.siteGroup = "";
                            siteInfo.site = getValueFromRemedy(sgAssignmentRecord, 536870918);

                            siteRecord.add(new String[]{siteInfo.company, siteInfo.region, siteInfo.siteGroup, siteInfo.site});

                        } catch (ARException ex) {
                            logger.info("error : " + ex.toString());
                        }
                    }

                } else {
                    List<EntryListInfo> sgAssignments = remedyAPI.getRemedyRecordByQuery(serverUser, configValue.getRemedyFormAssignment(),
                            "'Support Group ID' = \"" + addService.sgID + "\" AND Status = 1");

                    for (EntryListInfo sgAssignment : sgAssignments) {

                        try {
                            siteInfo = new SubmodulADD();
                            Entry sgAssignmentRecord = serverUser.getEntry(configValue.getRemedyFormAssignment(), sgAssignment.getEntryID(), null);
                            siteInfo.company = getValueFromRemedy(sgAssignmentRecord, 1000000001);
                            siteInfo.region = getValueFromRemedy(sgAssignmentRecord, 200000012);
                            siteInfo.siteGroup = getValueFromRemedy(sgAssignmentRecord, 200000007);
                            siteInfo.site = getValueFromRemedy(sgAssignmentRecord, 260000001);

                            siteRecord.add(new String[]{siteInfo.company, siteInfo.region, siteInfo.siteGroup, siteInfo.site});

                        } catch (ARException ex) {
                            logger.info("error : " + ex.toString());
                        }
                    }

                }

                siteRecord.stream().map(Arrays::asList)
                        .distinct()
                        .forEach(x -> {
                            DataInsert dataInsert = new DataInsert();
                            //Service
                            dataInsert.opcat1 = addService.opcat1;
                            dataInsert.opcat2 = addService.opcat2;
                            dataInsert.opcat3 = addService.opcat3;
                            dataInsert.opcat4 = addService.opcat4;
                            dataInsert.serviceType = addService.serviceType;

                            //SG
                            dataInsert.supportCompany = addService.supportCompany;
                            dataInsert.supportOrg = addService.supportOrg;
                            dataInsert.supportGroup = addService.supportGroup;
                            dataInsert.sgID = addService.sgID;
                            dataInsert.sgType = addService.sgType;

                            //Site
                            dataInsert.company = x.get(0);
                            dataInsert.region = x.get(1);
                            dataInsert.siteGroup = x.get(2);
                            dataInsert.site = x.get(3);

                            dataInsert.submitter = add.submitter;
                            dataInsert.notes = add.notes;

                            if (addService.sgDescription.contains("Approval Group")) {
                                insertDataCustApproval(dataInsert);
                            } else {
                                insertDataAssignment(dataInsert);
                            }
                        });

            }
        } catch (ARException e) {

            logger.info("error : " + e);

            return new Response("FAILED", "error", e.toString());
        }

        return new Response(
                "SUCCESSFUL", "-", "-");
    }

    public Response
            getADDSupportGroup(SubmodulADD add) {
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class
        );
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser serverUser = remedyConnection.buildconnection(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();

        try {
            //getDataformStaging
            List<EntryListInfo> sgInfos = remedyAPI.getRemedyRecordByQuery(serverUser, configValue.getRemedyMiddleFormStaging(), "'SR Type Field 30__c' = \"" + add.requestID + "\" AND 'Status__c' = 0");
            SubmodulADD addSg = new SubmodulADD();
            for (EntryListInfo sgInfo : sgInfos) {
                Entry sgRecord = serverUser.getEntry(configValue.getRemedyMiddleFormStaging(), sgInfo.getEntryID(), null);
                addSg.supportCompany = getValueFromRemedy(sgRecord, 1000000251);
                addSg.supportOrg = getValueFromRemedy(sgRecord, 1000000014);
                addSg.supportGroup = getValueFromRemedy(sgRecord, 1000000217);
                addSg.sgID = getValueFromRemedy(sgRecord, 536871020);
                addSg.sgType = getValueFromRemedy(sgRecord, 536870998);
                addSg.sgDescription = getValueFromRemedy(sgRecord, 536871016);

            }

            List<Site> sites = new ArrayList<>();
            if (addSg.sgType.contains("Centralized")) {
            } else {
                //get site  distributed
                Site site = new Site();
                List<EntryListInfo> siteInfos = remedyAPI.getRemedyRecordByQuery(serverUser, configValue.getRemedyMiddleFormStaging(),
                        "'SR Type Field 30__c' = \"" + add.requestID + "\" AND '(New Site) Site' != $NULL$ AND 'Status__c' = 0");
                for (EntryListInfo siteInfo : siteInfos) {
                    Entry siteRecord = serverUser.getEntry(configValue.getRemedyMiddleFormStaging(), siteInfo.getEntryID(), null);
                    site = new Site();
                    site.company = getValueFromRemedy(siteRecord, 1000000001);
                    site.region = getValueFromRemedy(siteRecord, 200000012);
                    site.siteGroup = getValueFromRemedy(siteRecord, 200000007);
                    site.site = getValueFromRemedy(siteRecord, 260000001);

                    sites.add(site);

                }

            }

            //get service distributed/centralized
            List<EntryListInfo> servicesInfos = remedyAPI.getRemedyRecordByQuery(serverUser, configValue.getRemedyMiddleFormStaging(),
                    "'SR Type Field 30__c' = \"" + add.requestID + "\" AND 'Operational Categorization 12__c' != $NULL$ AND 'Status__c' = 0");
            Services service = new Services();
            List<Services> services = new ArrayList<>();
            for (EntryListInfo serviceInfo : servicesInfos) {
                Entry serviceRecord = serverUser.getEntry(configValue.getRemedyMiddleFormStaging(), serviceInfo.getEntryID(), null);
                service = new Services();
                service.opcat1 = getValueFromRemedy(serviceRecord, 536870992);
                service.opcat2 = getValueFromRemedy(serviceRecord, 536870993);
                service.opcat3 = getValueFromRemedy(serviceRecord, 536870994);
                service.opcat4 = getValueFromRemedy(serviceRecord, 200000003);
                service.serviceType = getValueFromRemedy(serviceRecord, 536871025);

                services.add(service);
            }

            services.stream().forEach(x -> {
                System.out.println("site app : " + x.opcat4);
            });

            services.parallelStream().forEach(i -> {
                sites.stream().forEach(j -> {
                    DataInsert dataInsert = new DataInsert();
                    //Service
                    dataInsert.opcat1 = i.opcat1;
                    dataInsert.opcat2 = i.opcat2;
                    dataInsert.opcat3 = i.opcat3;
                    dataInsert.opcat4 = i.opcat4;
                    dataInsert.serviceType = i.serviceType;

                    //SG
                    dataInsert.supportCompany = addSg.supportCompany;
                    dataInsert.supportOrg = addSg.supportOrg;
                    dataInsert.supportGroup = addSg.supportGroup;
                    dataInsert.sgID = addSg.sgID;
                    dataInsert.sgType = addSg.sgType;

                    //Site
                    dataInsert.company = j.company;
                    dataInsert.region = j.region;
                    dataInsert.siteGroup = j.siteGroup;
                    dataInsert.site = j.site;

                    dataInsert.submitter = add.submitter;
                    dataInsert.notes = add.notes;

                    if (addSg.sgDescription.contains("Approval Group")) {
                        insertDataCustApproval(dataInsert);
                    } else {
                        insertDataAssignment(dataInsert);
                    }
                });
            });

        } catch (ARException e) {
            logger.info("error : " + e);
            return new Response("FAILED", "102", "-");
        }

        return new Response(
                "SUCCESSFUL", "-", "-");
    }

    public Response
            getADDHybrid(SubmodulADD add) {
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class
        );
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser serverUser = remedyConnection.buildconnection(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();

        try {
            //getDataHybrid
            SubmodulADD addHybrid = new SubmodulADD();
            List<SubmodulADD> addHybrids = new ArrayList<>();
            List<EntryListInfo> hybridInfos = remedyAPI.getRemedyRecordByQuery(serverUser, configValue.getRemedyMiddleFormStaging(), "'SR Type Field 30__c' = \"" + add.requestID + "\"");

            logger.info("add : " + add.sysReqID);
            for (EntryListInfo hybridInfo : hybridInfos) {
                Entry hybridRecord = serverUser.getEntry(configValue.getRemedyMiddleFormStaging(), hybridInfo.getEntryID(), null);
                //service
                addHybrid = new SubmodulADD();

                addHybrid.opcat1 = getValueFromRemedy(hybridRecord, 536870992);
                addHybrid.opcat2 = getValueFromRemedy(hybridRecord, 536870993);
                addHybrid.opcat3 = getValueFromRemedy(hybridRecord, 536870994);
                addHybrid.opcat4 = getValueFromRemedy(hybridRecord, 200000003);
                addHybrid.serviceType = getValueFromRemedy(hybridRecord, 536871025);
                //sg
                addHybrid.supportCompany = getValueFromRemedy(hybridRecord, 1000000251);
                addHybrid.supportOrg = getValueFromRemedy(hybridRecord, 1000000014);
                addHybrid.supportGroup = getValueFromRemedy(hybridRecord, 1000000217);
                addHybrid.sgDescription = getValueFromRemedy(hybridRecord, 536871016);
                addHybrid.sgID = getValueFromRemedy(hybridRecord, 536871020);
                //site
                addHybrid.company = getValueFromRemedy(hybridRecord, 1000000001);
                addHybrid.region = getValueFromRemedy(hybridRecord, 200000012);
                addHybrid.siteGroup = getValueFromRemedy(hybridRecord, 200000007);
                addHybrid.site = getValueFromRemedy(hybridRecord, 260000001);
                addHybrid.sgType = getValueFromRemedy(hybridRecord, 536870998);

                addHybrids.add(addHybrid);

                logger.info(" add : " + hybridInfos);
            }

            for (int i = 0; i < addHybrids.size(); i++) {
                DataInsert dataInsert = new DataInsert();
                dataInsert.opcat1 = addHybrids.get(i).opcat1;
                dataInsert.opcat2 = addHybrids.get(i).opcat2;
                dataInsert.opcat3 = addHybrids.get(i).opcat3;
                dataInsert.opcat4 = addHybrids.get(i).opcat4;
                dataInsert.serviceType = addHybrids.get(i).serviceType;
                dataInsert.supportCompany = addHybrids.get(i).supportCompany;
                dataInsert.supportOrg = addHybrids.get(i).supportOrg;
                dataInsert.supportGroup = addHybrids.get(i).supportGroup;
                dataInsert.sgID = addHybrids.get(i).sgID;
                dataInsert.sgType = addHybrids.get(i).sgType;
                dataInsert.company = addHybrids.get(i).company;
                dataInsert.region = addHybrids.get(i).region;
                dataInsert.siteGroup = addHybrids.get(i).siteGroup;
                dataInsert.site = addHybrids.get(i).site;
                dataInsert.submitter = add.submitter;
                dataInsert.notes = add.notes;

                if (addHybrids.get(i).sgDescription.contains("Approval Group")) {
                    insertDataCustApproval(dataInsert);
                } else {
                    insertDataAssignment(dataInsert);
                }

            }

        } catch (ARException e) {
            logger.info("error 002 :" + e.toString());
            return new Response("FAILED", "101", "error retrieving datas");
        } catch (Exception f) {
            logger.info("error 002 :" + f.toString());
            return new Response("FAILED", "101", "error retrieving datas");
        }

        return new Response(
                "SUCCESSFUL", "-", "-");
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

        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser serverUser = remedyConnection.buildconnection(configValue);
        Entry recordEntry = new Entry();
        RemedyAPI remedyAPI = new RemedyAPI();

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

        List<EntryListInfo> elis = remedyAPI.getRemedyRecordByQuery(serverUser, configValue.getRemedyFormAssignment(), queryAssign);
        if (elis.isEmpty()) {

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

    public void insertDataCustApproval(DataInsert dataInsert) {
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser serverUser = remedyConnection.buildconnection(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();
        Entry recordEntry = new Entry();
        
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

            recordEntry.put(
                    536870913, new Value(dataInsert.opcat1));
            recordEntry.put(
                    536870914, new Value(dataInsert.opcat2));
            recordEntry.put(
                    536870915, new Value(dataInsert.opcat3));
            recordEntry.put(
                    536870922, new Value(dataInsert.opcat4));
            recordEntry.put(
                    536870916, new Value(dataInsert.supportCompany));
            recordEntry.put(
                    536870917, new Value(dataInsert.supportOrg));
            recordEntry.put(
                    536870921, new Value(dataInsert.supportGroup));
            recordEntry.put(
                    536870919, new Value(dataInsert.company));
            recordEntry.put(
                    536870918, new Value(dataInsert.site));
            recordEntry.put(
                    2, new Value(dataInsert.submitter));
            recordEntry.put(
                    536870924, new Value(dataInsert.notes));
            recordEntry.put(
                    536870925, new Value(dataInsert.serviceType));
            recordEntry.put(
                    536870926, new Value(dataInsert.sgType));

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

}
