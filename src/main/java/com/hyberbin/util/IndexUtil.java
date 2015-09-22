/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyberbin.util;

import com.hyberbin.db.CompareMysql;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hyberbin.db.ICompareDb;
import org.jplus.hyb.database.transaction.IDbManager;
import org.jplus.hyb.log.LoggerManager;
import org.jplus.util.FileUtils;
import org.jplus.util.NumberUtils;
import org.jplus.util.ObjectHelper;

/**
 *
 * @author hyberbin
 */
public class IndexUtil {

    private final static org.jplus.hyb.log.Logger log = LoggerManager.getLogger(IndexUtil.class);
    public static Set<String> indexNames = new HashSet();
    static {
        init();
    }

    private final ICompareDb compareDb;

    public IndexUtil(ICompareDb compareDb){
        this.compareDb=compareDb;
    }

    private static void init() {
        try {
            InputStream openStream = IndexUtil.class.getClassLoader().getResource("indexColumns").openStream();
            List<String> readLines = FileUtils.readLines(openStream);
            for (String readLine : readLines) {
                if (!ObjectHelper.isNullOrEmptyString(readLine)) {
                    String cloumn;
                    if (readLine.contains("#")) {
                        String[] split = readLine.split("#");
                        cloumn = split[0].trim().toLowerCase();
                    } else {
                        cloumn = readLine.trim().toLowerCase();
                    }
                    indexNames.add(cloumn);
                    log.debug("add cloumn index name:{}", cloumn);
                }
            }
        } catch (IOException ex) {
            log.error("获取索引字段文件错误！", ex);
        }
    }

    public String getIndexAddString(String table, IDbManager manager) {
        List<Map> showDescribe = compareDb.showDescribe(table, manager);
        StringBuilder sql = new StringBuilder();
        if (ObjectHelper.isNotEmpty(showDescribe)) {
            for (Map describe : showDescribe) {
                String column = describe.get("column_name").toString().trim().toLowerCase();
                String DATA_TYPE = describe.get("DATA_TYPE").toString().trim().toLowerCase();
                if (describe.containsKey("column_key") && (indexNames.contains(column)||column.endsWith("id")) && (DATA_TYPE.contains("int") || (DATA_TYPE.contains("char")&&NumberUtils.parseInt(describe.get("character_maximum_length"))<=32))) {
                    Object key = describe.get("column_key");
                    if (ObjectHelper.isNullOrEmptyString(key)) {
                        sql.append("ALTER TABLE `").append(table).append("` ADD INDEX `").append(column).append("` ( `").append(column).append("` ) ;\n");
                    }
                }
            }
        }
        return sql.toString();
    }
}
