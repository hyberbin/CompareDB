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
import com.hyberbin.compare.IColumnMerge;
import com.hyberbin.compare.MysqlColumnMerge;
import com.hyberbin.util.CmdUtil;
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
import org.jplus.util.ObjectHelper;

/**
 *
 * @author Hyberbin
 */
public class CompareMysql implements ICompareDb {

    private final static Logger log = LoggerManager.getLogger(CompareMysql.class);
    private final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");


    @Override
    public List<Map> showTables(String table, IDbManager manager) {
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

    @Override
    public String showOneCreate(String tableName, IDbManager manager) {
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

    /**
     * 在自动添加索引的时候获取表结构
     * @param tableName
     * @param manager
     * @return
     */
    @Override
    public List<Map> showDescribe(String tableName, IDbManager manager) {
        Hyberbin hyberbin = new Hyberbin(manager);
        try {
            return hyberbin.getMapList("select COLUMN_NAME,COLUMN_KEY,DATA_TYPE,CHARACTER_MAXIMUM_LENGTH from information_schema.`COLUMNS` WHERE  TABLE_SCHEMA=database() and TABLE_NAME='" + tableName + "' ");
        } catch (SQLException ex) {
            log.error("showDescribe table:{} error!", tableName, ex);
        }
        return null;
    }

    @Override
    public String getDbName(IDbManager manager) {
        DatabaseAccess access = new DatabaseAccess(manager);
        try {
            Object queryUnique = access.queryUnique("select database()");
            return queryUnique.toString();
        } catch (SQLException ex) {
            log.error("getDbName error!", ex);
        }
        return null;
    }
    
    @Override
    public List<Map> getTableStructs(IDbManager manager, String tableName){
        try {
            List<Map> mapList = new Hyberbin(manager).getMapList("select \n"
                    + "cast(ifnull(character_maximum_length,\n"
                    + "case when numeric_scale is null or numeric_scale=0 then numeric_precision else concat(numeric_precision,',',numeric_scale) end\n"
                    + "\n"
                    + ") as char)\n"
                    + "as length , column_name as `column`, column_comment as `comment`,data_type from information_schema.columns  where table_schema=database() and table_name='" 
                    + tableName + "'");
            return mapList;
        } catch (Exception ex) {
            log.error("getTableStructs error!", ex);
        }
        return null;
    }

    @Override
    public String exportDb(String database, DbLinkBean dbLinkBean) {
        Date now = new Date();
        String filename = database + FORMAT.format(now) + ".sql";
        CmdUtil cmd = new CmdUtil("./mysqldump", new String[]{"-u" + dbLinkBean.getUser() + " -p" + dbLinkBean.getPassword() + " -h" + dbLinkBean.getHost() + " -P" + dbLinkBean.getPort() + " -R  --skip-lock-tables --extended-insert=true --default-character-set=utf8 " + database + " --result-file=" + filename});
        return cmd.getErrors();
    }

    @Override
    public String export(String tables, String database, DbLinkBean dbLinkBean) {
        CmdUtil cmd = new CmdUtil("./mysqldump", new String[]{"-u" + dbLinkBean.getUser() + " -p" + dbLinkBean.getPassword() + " -h" + dbLinkBean.getHost() + " -P" + dbLinkBean.getPort() + " -R  --skip-lock-tables --extended-insert=true --default-character-set=utf8 " + database + " " + tables + " --result-file=dump.sql"});
        return cmd.getErrors();
    }

    @Override
    public String importDb(String dbFileName, String database, DbLinkBean dbLinkBean) {
        CmdUtil cmd = new CmdUtil("./mysql", new String[]{"--default-character-set=utf8 -u" + dbLinkBean.getUser() + " -p" + dbLinkBean.getPassword() + " -h" + dbLinkBean.getHost() + " -P" + dbLinkBean.getPort(), "use " + database, "source " + dbFileName});
        return cmd.getErrors();
    }

    @Override
    public IColumnMerge getColumnMerge(String table, String createTableSql) {
        return new MysqlColumnMerge(table, createTableSql);
    }
}
