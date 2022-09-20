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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import mii.ptm.sscit.domain.ConfigFile;
import mii.ptm.sscit.domain.ConfigurationValue;
import mii.ptm.sscit.domain.DataInsert;
import mii.ptm.sscit.domain.RemedyAPI;
import mii.ptm.sscit.domain.RemedyAttachment;
import mii.ptm.sscit.domain.Response;
import mii.ptm.sscit.domain.SubmodulADD;
import mii.ptm.sscit.domain.SupportGroup;
import mii.ptm.sscit.domain.Services;
import mii.ptm.sscit.remedy.RemedyConnection;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author mukhlisaj
 */
public class ManualUpload {

    Logger logger = Logger.getLogger("Manual Upload : ");

    ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
    ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

    RemedyConnection remedyConnection = new RemedyConnection();
    ARServerUser serverUser = remedyConnection.buildconnection(configValue);

    public Response getManualUpload(SubmodulADD add) throws IOException, ARException {
        RemedyAPI remedyAPI = new RemedyAPI();
        String workInfoForm = "SRM:WorkInfo";

        final String formSupportGroup = "CTM:Support Group";
        final String formService = "PTM:SSC:IT:Opcat Tier 4";

        List<RemedyAttachment> remedyAttachments = remedyAPI.getRemedyAttachmentbySchemaQuery(serverUser, workInfoForm, "'Request Number' = \"" + add.requestID + "\" AND 'Summary' = \"Attachment\"");

        for (RemedyAttachment remedyAttachment : remedyAttachments) {
            DataInsert dataInsert = new DataInsert();

            //cara bacanya
            byte[] fis = remedyAttachment.getAttachedFile();
            InputStream inputStream = new ByteArrayInputStream(fis);

            Workbook workbook = new XSSFWorkbook(inputStream);

//            ReadCellData(workbook, 0, 0);
            Sheet sheet = workbook.getSheetAt(0);
            int rows; // No of rows
            rows = sheet.getPhysicalNumberOfRows();

            for (int r = 1; r < rows; r++) {
                dataInsert.opcat1 = ReadCellData(workbook, r, 0);
                dataInsert.opcat2 = ReadCellData(workbook, r, 1);
                dataInsert.opcat3 = ReadCellData(workbook, r, 2);
                dataInsert.opcat4 = ReadCellData(workbook, r, 3);
                dataInsert.supportCompany = ReadCellData(workbook, r, 4);
                dataInsert.supportOrg = ReadCellData(workbook, r, 5);
                dataInsert.supportGroup = ReadCellData(workbook, r, 6);
                dataInsert.company = ReadCellData(workbook, r, 7);
                dataInsert.site = ReadCellData(workbook, r, 8);
                dataInsert.notes = add.notes;
                dataInsert.submitter = add.submitter;

                List<EntryListInfo> sgInfos = remedyAPI.getRemedyRecordByQuery(serverUser, formSupportGroup, "'Support Group Name' = \"" + dataInsert.supportGroup + "\" AND 'Company' = \"" + dataInsert.supportCompany + "\"");
                SupportGroup sg = new SupportGroup();
                for (EntryListInfo sgInfo : sgInfos) {
                    Entry sgRecord = serverUser.getEntry(formSupportGroup, sgInfo.getEntryID(), null);
                    dataInsert.sgID = getValueFromRemedy(sgRecord, 1);
                    dataInsert.sgType = getValueFromRemedy(sgRecord, 536870918);
                    sg.description = getValueFromRemedy(sgRecord, 1000000000);

                }

                List<EntryListInfo> serviceInfos = remedyAPI.getRemedyRecordByQuery(serverUser, formService,
                        "'Operational Category 1' = \"" + dataInsert.opcat1 + "\" AND 'Operational Category 2' = \"" + dataInsert.opcat2 + "\" AND 'Operational Category 3' = \"" + dataInsert.opcat3
                        + "\" AND 'Operational Category 4' = \"" + dataInsert.opcat4 + "\"");
                Services service = new Services();
                for (EntryListInfo serviceInfo : serviceInfos) {
                    Entry serviceRecord = serverUser.getEntry(formService, serviceInfo.getEntryID(), null);
                    dataInsert.serviceType = getValueFromRemedy(serviceRecord, 536870913);
                }

                if (sg.description.contains("Approval Group")) {
                    insertDataCustApproval(dataInsert);
                } else {
                    insertDataAssignment(dataInsert);

                }
            }
        }
        return new Response("SUCCESSFUL", "", "");
    }

    public String ReadCellData(Workbook wb, int eRow, int eColumn) {
        String value;
        Sheet sheet = wb.getSheetAt(0);
        Row row = sheet.getRow(eRow);
        Cell cell = row.getCell(eColumn);
        value = cell.getStringCellValue();

        if (value == null) {
            return "";
        }

        return value;
    }

    public void insertDataAssignment(DataInsert dataInsert) {
        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser serverUser = remedyConnection.buildconnection(configValue);
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
            recordEntry.put(536870919, new Value(dataInsert.company));
            recordEntry.put(536870918, new Value(dataInsert.site));
            recordEntry.put(2, new Value(dataInsert.submitter));
            recordEntry.put(536870924, new Value(dataInsert.notes));
            recordEntry.put(536870925, new Value(dataInsert.serviceType));
            recordEntry.put(536870926, new Value(dataInsert.sgType));

            String formApproval = "PTM:SSC:IT:FormApprovalSite";
            try {
                String result = serverUser.createEntry(formApproval, recordEntry);
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

    public String getValueFromRemedy(Entry requestRecord, Object fieldID) {
        if (requestRecord.get(fieldID).getValue() == null) {
            return "";
        }

        return requestRecord.get(fieldID).getValue().toString();
    }
}
