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
import java.sql.SQLException;
import java.util.List;
import org.jplus.hyb.database.config.DbConfig;
import org.jplus.hyb.database.config.SimpleConfigurator;
import org.jplus.hyb.database.crud.DatabaseAccess;
import org.jplus.hyb.database.crud.Hyberbin;
import org.jplus.hyb.database.transaction.IDbManager;
import org.jplus.hyb.database.transaction.SimpleManager;
import org.jplus.hyb.log.Logger;
import org.jplus.hyb.log.LoggerManager;
import org.jplus.util.NumberUtils;
import org.jplus.util.ObjectHelper;

/**
 *
 * @author Hyberbin
 */
public class SqliteDao {

    protected static Logger log = LoggerManager.getLogger(SqliteDao.class);

    private static IDbManager getManager() {
        log.debug("getManager: jdbc:sqlite:data.db");
        IDbManager manager=new SimpleManager("sqlite");
        SimpleConfigurator.addConfigurator(new DbConfig("org.sqlite.JDBC", "jdbc:sqlite:./data.db", "", "","sqlite"));
        return manager;
    }

    private static void createDatabaseTable() {
        String sql = "create table dblist(id integer primary key autoincrement,lable text,url text,host text,user text,password text)";
        DatabaseAccess databaseAccess = new DatabaseAccess(getManager());
        try {
            databaseAccess.update(sql);
        } catch (SQLException ex) {
            log.error("创建dblist表错误！", ex);
        }
    }

    public static boolean existTable(String table) {
        try {
            log.debug("in existTable");
            String sql = "SELECT count(*) FROM sqlite_master where type='table' and name='" + table + "'";
            log.debug("sql:" + sql);
            DatabaseAccess databaseAccess = new DatabaseAccess(getManager());
            int count = NumberUtils.parseInt(databaseAccess.queryUnique(sql));
            log.debug("count:" + count);
            log.debug("out existTable");
            return count > 0;
        } catch (SQLException ex) {
            log.error("检查表：{}是否存在错误！", ex,table);
        }
        return false;
    }

    public static List<DbLinkBean> getDblinkInfo() {
        if (!existTable("dblist")) {
            createDatabaseTable();
        }
        Hyberbin hyberbin = new Hyberbin(new DbLinkBean(), getManager());
        List showAll = null;
        try {
            showAll = hyberbin.showAll();
        } catch (SQLException ex) {
            log.error("查询所有dblist出错", ex);
        }
        return showAll;
    }

    public static void clearDblink() {
        Hyberbin hyberbin = new Hyberbin(new DbLinkBean(), getManager());
        try {
            hyberbin.delete("where 1=1");
        } catch (SQLException ex) {
            log.error("clearDblink出错", ex);
        }
    }

    public static void insertDbLink(DbLinkBean dbLinkBean) {
        if (!ObjectHelper.isNullOrEmptyString(dbLinkBean.getUrl())) {
            Hyberbin hyberbin = new Hyberbin(dbLinkBean, getManager());
            try {
                hyberbin.insert("id");
            } catch (SQLException ex) {
                log.error("insertDbLink出错", ex);
            }
        }
    }
}
