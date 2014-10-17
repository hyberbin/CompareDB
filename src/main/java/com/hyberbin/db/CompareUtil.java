/*
 * Copyright 2014 Hyberbin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Email:hyberbin@qq.com
 */
package com.hyberbin.db;

import com.hyberbin.bean.DbLinkBean;
import java.io.File;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.jplus.hyb.database.crud.DatabaseAccess;
import org.jplus.hyb.database.crud.Hyberbin;
import org.jplus.hyb.database.transaction.IDbManager;
import org.jplus.hyb.log.Logger;
import org.jplus.hyb.log.LoggerManager;
import org.jplus.util.FileUtils;
import org.jplus.util.ObjectHelper;

/**
 *
 * @author Hyberbin
 */
public class CompareUtil {

    private final static Logger log = LoggerManager.getLogger(CompareUtil.class);
    private final static SimpleDateFormat FORMAT=new SimpleDateFormat("yyyyMMddHHmmss");
    private final static String EXE_FILE_EXTENDS=System.getProperty("os.name").toLowerCase().contains("windows")?".bat":".sh";

    private CompareUtil() {
    }

    public static List<Map> showTables(String table, IDbManager manager) {
        Hyberbin hyberbin = new Hyberbin(manager);
        String sql = "select TABLE_NAME,TABLE_COMMENT from INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA=database() and TABLE_TYPE <> 'VIEW' ";
        if (!ObjectHelper.isNullOrEmptyString(table)) {
            sql += " and TABLE_NAME like '%" + table.trim() + "%'";
        }
        try {
            return hyberbin.getMapList(sql);
        } catch (SQLException ex) {
            log.error("showTables table:{} error!", table, ex);
        }
        return null;
    }

    public static String showOneCreate(String tableName, IDbManager manager) {
        Hyberbin soEasy = new Hyberbin(manager);
        List<Map> mapList;
        try {
            mapList = soEasy.getMapList("show create table " + tableName);
            return mapList.isEmpty() ? "" : mapList.get(0).get("create table").toString();
        } catch (SQLException ex) {
            log.error("showOneCreate table:{} error!", tableName, ex);
        }
        return "";
    }

    public static String getDbName(IDbManager manager) {
        DatabaseAccess access = new DatabaseAccess(manager);
        try {
            Object queryUnique = access.queryUnique("select database()");
            return queryUnique.toString();
        } catch (SQLException ex) {
            log.error("getDbName error!", ex);
        }
        return null;
    }
    
    public static String mysqldumpDb(String database, DbLinkBean dbLinkBean) {
        Date now=new Date();
        String filename = database+FORMAT.format(now)+".sql";
        String exe = "mysqldump -u" + dbLinkBean.getUser() + " -p" + dbLinkBean.getPassword() + " -h" + dbLinkBean.getHost()+ " -P" + dbLinkBean.getPort()+ " -R  --skip-lock-tables --extended-insert=true --default-character-set=utf8 " + database +" > "+filename;
        File file = new File("dump"+EXE_FILE_EXTENDS);
        log.debug("write cmd:{} to file:dump"+EXE_FILE_EXTENDS, exe);
        StringBuilder info=new StringBuilder();
        try {
            FileUtils.writeStringToFile(file, exe, "utf-8");
            Process exec = Runtime.getRuntime().exec("./dump"+EXE_FILE_EXTENDS);
            exec.waitFor();
            InputStream errorStream = exec.getErrorStream();
            List<String> readLines = FileUtils.readLines(errorStream);
            if (ObjectHelper.isNotEmpty(readLines)) {
                for (String string : readLines) {
                    log.error(string);
                    info.append(string);
                }
            }
        } catch (Exception ex) {
            log.error("mysqldump error! cmd:{}", ex, exe);
        }
        return info.toString();
    }

    public static String mysqldump(String tables, String database, DbLinkBean dbLinkBean) {
        String exe = "mysqldump -u" + dbLinkBean.getUser() + " -p" + dbLinkBean.getPassword() + " -h" + dbLinkBean.getHost()+ " -P" + dbLinkBean.getPort()+ " -R  --skip-lock-tables --extended-insert=true --default-character-set=utf8 " + database + " " + tables + " > dump.sql";
        File file = new File("dump"+EXE_FILE_EXTENDS);
        log.debug("write cmd:{} to file:dump"+EXE_FILE_EXTENDS, exe);
        StringBuilder info=new StringBuilder();
        try {
            FileUtils.writeStringToFile(file, exe, "utf-8");
            Process exec = Runtime.getRuntime().exec("./dump"+EXE_FILE_EXTENDS);
            exec.waitFor();
            InputStream errorStream = exec.getErrorStream();
            List<String> readLines = FileUtils.readLines(errorStream);
            if (ObjectHelper.isNotEmpty(readLines)) {
                for (String string : readLines) {
                    log.error(string);
                    info.append(string);
                }
            }
        } catch (Exception ex) {
            log.error("mysqldump error! cmd:{}", ex, exe);
        }
        return info.toString();
    }

    public static String mysql(String dbFileName,String database, DbLinkBean dbLinkBean) {
        String exe = "mysql --default-character-set=utf8 -u" + dbLinkBean.getUser() + " -p" + dbLinkBean.getPassword() + " -h" + dbLinkBean.getHost()+ " -P" + dbLinkBean.getPort()+ " " + database + " " + " < "+dbFileName;
        File file = new File("mysql"+EXE_FILE_EXTENDS);
        log.debug("write cmd:{} to file:mysql"+EXE_FILE_EXTENDS, exe);
        StringBuilder info=new StringBuilder();
        try {
            FileUtils.writeStringToFile(file, exe, "utf-8");
            Process exec = Runtime.getRuntime().exec("./mysql"+EXE_FILE_EXTENDS);
            exec.waitFor();
            InputStream errorStream = exec.getErrorStream();
            List<String> readLines = FileUtils.readLines(errorStream);
            if (ObjectHelper.isNotEmpty(readLines)) {
                for (String string : readLines) {
                    log.error(string);
                    info.append(string);
                }
            }
        } catch (Exception ex) {
            log.error("mysql error! cmd:{}", ex, exe);
        }
        return info.toString();
    }
}
