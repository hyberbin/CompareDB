package com.hyberbin.db;

import com.hyberbin.bean.DbLinkBean;
import com.hyberbin.compare.IColumnMerge;
import com.hyberbin.compare.OracleColumnMerge;
import org.jplus.hyb.database.crud.DatabaseAccess;
import org.jplus.hyb.database.crud.Hyberbin;
import org.jplus.hyb.database.transaction.IDbManager;
import org.jplus.hyb.log.Logger;
import org.jplus.hyb.log.LoggerManager;
import org.jplus.util.ObjectHelper;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by hyberbin on 15/9/17.
 */
public class CompareOracle implements ICompareDb {
    private final static Logger log = LoggerManager.getLogger(CompareOracle.class);
    @Override
    public List<Map> showTables(String table, IDbManager manager) {
        Hyberbin hyberbin = new Hyberbin(manager);
        String sql="select TABLE_NAME ,comments as \"TABLE_COMMENT\"  from USER_tab_comments where INSTR(TABLE_NAME,'$')=0  ";
        if (!ObjectHelper.isNullOrEmptyString(table)) {
            sql += " and TABLE_NAME like '%" + table.toUpperCase().trim() + "%'";
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
            mapList = soEasy.getMapList("select to_char(dbms_metadata.get_ddl('TABLE','"+tableName.toUpperCase()+"' ))as ddl from dual,user_tables where upper(table_name) like '%"+tableName.toUpperCase()+"%'");
            return mapList.isEmpty() ? "" : mapList.get(0).get("ddl").toString();
        } catch (SQLException ex) {
            log.error("showOneCreate table:{} error!", tableName, ex);
        }
        return "";
    }

    /**
     * 在自动添加索引的时候获取表结构
     *
     * @param tableName
     * @param manager
     * @return
     */
    @Override
    public List<Map> showDescribe(String tableName, IDbManager manager) {
        String sql="select \"columns\".data_length as \"CHARACTER_MAXIMUM_LENGTH\" ,\"columns\".COLUMN_ID AS \"COLUMN_KEY\" ,\"columns\".COLUMN_NAME AS \"COLUMN_NAME\" , " +
                "\"comments\".COMMENTS AS \"COMMENT\",\"columns\".DATA_TYPE AS \"DATA_TYPE\" " +
                " from user_tab_columns \"columns\", " +
                " user_col_comments \"comments\" " +
                " where \"columns\".Table_Name= \"comments\".Table_Name AND \"columns\".COLUMN_NAME =\"comments\".COLUMN_NAME  and upper(\"columns\".table_name) like '%" + tableName + "%' order by \"columns\".COLUMN_ID ";
        Hyberbin hyberbin = new Hyberbin(manager);
        try {
            return hyberbin.getMapList(sql);
        } catch (SQLException ex) {
            log.error("showDescribe table:{} error!", tableName, ex);
        }
        return null;
    }

    @Override
    public String getDbName(IDbManager manager) {
        DatabaseAccess access = new DatabaseAccess(manager);
        try {
            Object queryUnique = access.queryUnique("select name from v$database");
            return queryUnique.toString();
        } catch (SQLException ex) {
            log.error("getDbName error!", ex);
        }
        return null;
    }

    @Override
    public List<Map> getTableStructs(IDbManager manager, String tableName) {
        try {
            String sql="select \"columns\".data_length as \"length\"  ,\"columns\".COLUMN_NAME AS \"column\" , " +
                    "\"comments\".COMMENTS AS \"comment\",\"columns\".DATA_TYPE AS \"data_type\" " +
                    " from user_tab_columns \"columns\", " +
                    " user_col_comments \"comments\" " +
                    " where \"columns\".Table_Name= \"comments\".Table_Name AND \"columns\".COLUMN_NAME =\"comments\".COLUMN_NAME  and upper(\"columns\".table_name) like '%" + tableName+"%'";
            List<Map> mapList = new Hyberbin(manager).getMapList(sql);
            return mapList;
        } catch (Exception ex) {
            log.error("getTableStructs error!", ex);
        }
        return null;
    }

    @Override
    public String exportDb(String database, DbLinkBean dbLinkBean) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String export(String tables, String database, DbLinkBean dbLinkBean) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String importDb(String dbFileName, String database, DbLinkBean dbLinkBean) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IColumnMerge getColumnMerge(String table, String createTableSql) {
        return new OracleColumnMerge(table, createTableSql);
    }
}
