/*
 * Copyright (C) 2011 NATSRL @ UMD (University Minnesota Duluth, US) and
 * Software and System Laboratory @ KNU (Kangwon National University, Korea) 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package vissimcom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import util.FileHelper;

/**
 *
 * @author Chongmyung Park
 */
public class VISSIMHelper {
    public static VISSIMDetector detectorOption = VISSIMDetector.NULL;
    /**
     * Loads desired speed decision list from VISSIM case file
     * @param contents
     * @return 
     */    
    public static ArrayList<String> loadDesiredSpeedDecisionsFromCasefile(String caseFile) {

        String contents;
        try {
            contents = FileHelper.readTextFile(caseFile);
        } catch (IOException ex) {
            return null;
        }
        
        if (contents == null || contents.isEmpty()) {
            return null;
        }

        ArrayList<String> dsds = new ArrayList<String>();

        // get detector id from text
        String regx = "DESIRED_SPEED_DECISION ([0-9]+) NAME \"(.*?)\" LABEL";
        Pattern p = Pattern.compile(regx);
        Matcher matcher = p.matcher(contents);
        while (matcher.find()) {
            String dname = matcher.group(2).trim();
            if (!dname.isEmpty()) {
                dsds.add(dname);
            }
        }

        return dsds;
    }

    /**
     * Loads signal group list from VISSIM case file
     * @param contents
     * @return 
     */
    public static ArrayList<String> loadSignalGroupsFromCasefile(String caseFile) {
        
        String contents;
        try {
            contents = FileHelper.readTextFile(caseFile);
        } catch (IOException ex) {
            return null;
        }
        
        if (contents == null || contents.isEmpty()) {
            return null;
        }

        ArrayList<String> sgs = new ArrayList<String>();

        // get detector id from text
        String regx = "SIGNAL_GROUP ([0-9]+)  NAME \"(.*?)\"";
        Pattern p = Pattern.compile(regx);
        Matcher matcher = p.matcher(contents);
        while (matcher.find()) {
            String dname = matcher.group(2).trim();
            if (!dname.isEmpty()) {
                sgs.add(dname);
            }
        }

        return sgs;
    }    
    
    /**
     * Loades detector list from VISSIM casefile
     * @param contents
     * @return 
     */
    public static ArrayList<String> loadDetectorsFromCasefile(String caseFile) {
        
        String contents;
        try {
            contents = FileHelper.readTextFile(caseFile);
        } catch (IOException ex) {
            return null;
        }
        
        if (contents == null || contents.isEmpty()) {
            return null;
        }
        
        ArrayList<String> detectors;
        
        /**
         * find DataCollector
         */
        detectors = loadDetectorDatas(contents,"COLLECTION_POINT");
        if(detectors != null){
//            System.out.println("COLL : "+detectors.size());
            detectorOption = VISSIMDetector.DATACOLLECTOR;
            return detectors;
        }
        
        /**
         * find Detector
         */
        detectors = loadDetectorDatas(contents,"DETECTOR");
        if(detectors == null){
            detectorOption = VISSIMDetector.NULL;
            return null;
        }else{
            detectorOption = VISSIMDetector.DETECTOR;
//            System.out.println("DET : "+detectors.size());
        }

        return detectors;
    }    
    
    private static ArrayList<String> loadDetectorDatas(String contents, String strx) {
        ArrayList<String> detectors = new ArrayList<String>();

        // get detector id from text
        String regx = strx + " (.*?) NAME \"(DM.............)";
//        System.out.println(regx);
        Pattern p = Pattern.compile(regx);
        Matcher matcher = p.matcher(contents);
        
        while (matcher.find()) {
            String dname = matcher.group(2);
            if (!dname.isEmpty()) {
                detectors.add(dname.trim());
            }
        }
        
        if(detectors.isEmpty())
            return null;
        
        return detectors;
    }
    
    public static int loadSimulationDuration(String caseFile) throws IOException {        
        String contents = FileHelper.readTextFile(caseFile);
        
        if (contents == null || contents.isEmpty()) {
            System.out.println("Cannot find signal head(meter) in case file");
            System.exit(-1);
        }
        String[] regx = {"SIMULATION_DURATION ([0-9]+)","SIMULATION_DURATION  ([0-9]+)"};
        int duration = -1;
        // get detector id from text
        for(String reg : regx){
            duration = fineDuration(reg,contents);
            if(duration > 0)
                break;
        }
        
        return duration;
    }
    
    public static int fineDuration(String regx, String contents) {
        Pattern p = Pattern.compile(regx);
        Matcher matcher = p.matcher(contents);
        
        while (matcher.find()) {
            String dname = matcher.group(1).trim();
            if (!dname.isEmpty()) {
                System.out.println("SIGNAL : " + dname);
                return Integer.parseInt(dname);
            }
        }
        
        return -1;
    }    

    public static ArrayList<String> loadDMSsFromCasefile(String caseFile) throws IOException{
        ArrayList<String> DMSs = new ArrayList<String>();
        String contents = FileHelper.readTextFile(caseFile);
        
        if(contents == null || contents.isEmpty()){
            System.out.println("Cannot find case file");
            System.exit(-1);
        }

        // get detector id from text
        String regx = "DESIRED_SPEED_DECISION ([0-9]+) NAME \"(.*?)\"";
        Pattern p = Pattern.compile(regx);
        Matcher matcher = p.matcher(contents);
        while (matcher.find()) {
            String dname = matcher.group(2).trim();
            if (!dname.isEmpty()) {
                DMSs.add(dname);
            }
        }
        
        return DMSs;
    }
}
