package com.hyberbin.db;

import com.hyberbin.bean.DbLinkBean;
import com.hyberbin.compare.IColumnMerge;
import org.jplus.hyb.database.transaction.IDbManager;

import java.util.List;
import java.util.Map;

/**
 * Created by hyberbin on 15/9/17.
 */
public interface ICompareDb {
    List<Map> showTables(String table, IDbManager manager);

    String showOneCreate(String tableName, IDbManager manager);

    /**
     * 在自动添加索引的时候获取表结构
     * @param tableName
     * @param manager
     * @return
     */
    List<Map> showDescribe(String tableName, IDbManager manager);

    String getDbName(IDbManager manager);

    List<Map> getTableStructs(IDbManager manager, String tableName);

    String exportDb(String database, DbLinkBean dbLinkBean);

    String export(String tables, String database, DbLinkBean dbLinkBean);

    String importDb(String dbFileName, String database, DbLinkBean dbLinkBean);

    IColumnMerge getColumnMerge(String table, String createTableSql);
}
