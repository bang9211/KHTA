/*
 * Copyright (C) 2015 Software&System Lab. Kangwon National University.
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

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class KHTAParam {
    public static final String SECTION_DIR = "section";
    public static final String CONFIG_DIR = "config";
    
    //btn Text
    public static final String SECTION_SAVE = "Create Section";
    public static final String SECTION_LOADING = "Section saving..";
    
    public static final int MISSING_DATA = -1;
    
    //DB
    public final static String DB_NAME = "korex";
    public final static String DB_URL = "jdbc:mysql://210.115.47.193:3306/" + DB_NAME;
    public final static String USER_ID = "snslab";
    public final static String PASSWORD = "snslab^06513";
}
