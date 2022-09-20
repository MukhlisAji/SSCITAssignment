/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mii.ptm.sscit.model;

import com.bmc.arsys.api.Entry;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author mukhlisaj
 */
public class MetaFunction {

    public void SoapLog(String message) {

        Logger logger = Logger.getLogger("Log File :");
        try {
            Date date = new Date();
            String strDateFormat = "dd-MM-yyyy HH:mm:ss";
            DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
            String formattedDate = dateFormat.format(date);

            File file = new File("E:\\Users\\Mukhlish.Aji\\sscintegration\\log\\ARoutingRecord.txt");
            ///Users/ferry.hendrayana/Personal/Workspace/sscintegration/log/

            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw;
            if (file.length() >= 300000) {
                fw = new FileWriter(file.getAbsoluteFile(), false);
            } else {
                fw = new FileWriter(file.getAbsoluteFile(), true);
            }
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(formattedDate + " : " + message + "\n");
            bw.close();

        } catch (IOException e) {
            logger.info("error creating file " + e.toString());
        }
    }

}
