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

package util;

import infra.InfraConstants;
import java.io.File;
import java.io.IOException;


/**
 *
 * @author Chongmyung Park
 */
public class Logger {
    public static String SAVE_DIR = InfraConstants.CACHE_DIR + File.separator + "log";
    String name;
    String outputFile;
    StringBuilder log = new StringBuilder(1024);
    String newline = System.getProperty("line.separator");
    String extension = "txt";
    
    public Logger(String name) {        
        this(name, "txt");
    }
    
    public Logger(String name, String extension) {        
        new File(SAVE_DIR).mkdir();        
        this.name = name;
        this.extension = extension;
        this.outputFile = getLogFilePath(name, extension);
    }    

    public void print(String msg) {
        this.log.append(msg);
    }
    
    public void println(String msg) {
        this.log.append(msg+newline);
    }    
    
    public void print(Object msg) {
        this.log.append(msg.toString());
    }    
    
    public void println(Object msg) {
        this.log.append(msg.toString()+newline);
    }        
    
    public void println() {
        this.log.append(newline);
    }
    
    public void writeLog() throws IOException {
        writeLog(this.outputFile);
    }
    
    public void writeLog(String outputFilePath) throws IOException {
        outputFilePath = FileHelper.getNumberedFileName(outputFilePath);
        FileHelper.writeTextFile(log.toString(), outputFilePath);
    }    
    
    public static String getLogFilePath(String logName, String extension) {
        return Logger.SAVE_DIR + File.separator + logName + "." + extension;
    }
}
