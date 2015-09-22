package com.hyberbin.compare;

import com.hyberbin.bean.ColumnBean;
import org.jplus.util.ObjectHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hyberbin on 15/9/17.
 */
public class OracleColumnMerge implements IColumnMerge {

    private final Map<String, String> columnMap = new HashMap<String, String>();
    private final List<String> updateList = new ArrayList(0);
    private final List<String> dropList = new ArrayList(0);
    private final List<String> addList = new ArrayList(0);
    private final String table;
    private String createTable = null;
    private final StringBuilder status = new StringBuilder();
    private boolean newTable = false;

    public OracleColumnMerge(String table, String createTableSql) {
        createTableSql=getTableDdl(createTableSql);//去掉表空间的描述
        String tempsql=createTableSql.substring(createTableSql.indexOf("(")+1).trim();
        String[] sql = tempsql.split("\n");
        for (String clo : sql) {
            clo=clo.trim();
            if (clo.startsWith("\"")) {
                int last_quot=clo.endsWith(",")?clo.length()-1:clo.length();
                columnMap.put(getCloName(clo), clo.substring(0, last_quot).trim());
            }
        }
        this.table = table;
        createTable = createTableSql;
    }

    @Override
    public String getUpdateList() {
        StringBuilder str = new StringBuilder();
        if (ObjectHelper.isNotEmpty(updateList)) {
            status.append(" 有修改字段 ");
            for (String sql : updateList) {
                str.append("alter table ").append(table).append(" modify ( ").append(sql).append(");\n");
            }
        }
        return str.toString();
    }

    @Override
    public String getDropList() {
        StringBuilder str = new StringBuilder();
        if (ObjectHelper.isNotEmpty(dropList)) {
            status.append(" 有删除字段 ");
            for (String sql : dropList) {
                str.append("alter table ").append(table).append(" drop column ").append(sql).append(";\n");
            }
        }
        return str.toString();
    }

    @Override
    public String getAddList() {
        StringBuilder str = new StringBuilder();
        addList.addAll(columnMap.values());
        if (ObjectHelper.isNotEmpty(addList)) {
            status.append(" 有新增字段 ");
            for (String sql : addList) {
                str.append("alter table ").append(table).append(" add ( ").append(sql).append(");\n");
            }
        }
        return str.toString();
    }

    @Override
    public String getAllChange() {
        if (newTable) {
            return (createTable+";\n");//.replaceFirst("CREATE TABLE", "CREATE TABLE if not exists");
        }
        StringBuilder str = new StringBuilder();
        str.append(getAddList());
        //str.append(getDropList());
        str.append(getUpdateList());
        return str.toString();
    }

    @Override
    public String getStatus() {
        return ObjectHelper.isNullOrEmptyString(status.toString())?"一致":status.toString();
    }

    @Override
    public void merge(String createTableSql) {
        createTableSql=getTableDdl(createTableSql);
        if(createTableSql.equals(createTable)){
            status.append("一致");
            columnMap.clear();
            return;
        }
        List<ColumnBean> columnBeans = getColumnBeans(createTableSql);
        if (ObjectHelper.isNotEmpty(columnBeans)) {
            for (ColumnBean columnBean : columnBeans) {
                addColumn(columnBean);
            }
        } else {
            status.append("新增表");
            newTable = true;
        }
    }

    private void addColumn(ColumnBean column) {
        String columnStr = columnMap.get(column.getName());
        if (columnStr == null) {//标准SQL中没有这个字段，移除
            dropList.add(columnStr);
        } else if (columnStr.equals(column.getSql())) {//两个字段一模一样
        } else {//不一样那么更新这个字段
            updateList.add(columnStr);
        }
        columnMap.remove(column.getName());
    }

    private List<ColumnBean> getColumnBeans(String createTableSql) {
        List<ColumnBean> list = new ArrayList(0);
        String tempsql=createTableSql.substring(createTableSql.indexOf("(")+1).trim();
        String[] sql = tempsql.split("\n");
        for (String clo : sql) {
            clo=clo.trim();
            if (clo.startsWith("\"")) {
                list.add(new ColumnBean(getCloName(clo), clo.substring(0, clo.length() - 1).trim()));
            }
        }
        return list;
    }

    private static String getCloName(String ddl) {
        ddl = ddl.trim();
        return ddl.substring(1, ddl.indexOf("\"", 1));
    }

    private static String getTableDdl(String ddl){
        String[] split = ddl.split("\n");
        StringBuilder builder=new StringBuilder();
        for(String sql:split){
            sql=sql.trim();
            if(sql.contains("TABLESPACE")){//去掉表空间的描述
                if(sql.endsWith(";")){
                    sql=";";
                }else {
                    sql="";
                }
            }
            builder.append(sql+"\n");
        }
        return builder.toString();
    }

}
