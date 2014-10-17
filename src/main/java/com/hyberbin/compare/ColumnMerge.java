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
package com.hyberbin.compare;

import com.hyberbin.bean.ColumnBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jplus.util.ObjectHelper;

/**
 *
 * @author Hyberbin
 */
public class ColumnMerge implements IColumnMerge {

    private final Map<String, String> columnMap = new HashMap<String, String>();
    private final List<String> updateList = new ArrayList(0);
    private final List<String> dropList = new ArrayList(0);
    private final List<String> addList = new ArrayList(0);
    private final String table;
    private String createTable = null;
    private final StringBuilder status = new StringBuilder();
    private boolean newTable = false;

    public ColumnMerge(String table, String createTableSql) {
        String[] sql = createTableSql.split("\n");
        for (String clo : sql) {
            if (clo.trim().startsWith("`")) {
                columnMap.put(getCloName(clo), clo.substring(0, clo.length() - 1));
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
                str.append("alter table ").append(table).append(" modify column ").append(sql).append(";\n");
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
                str.append("alter table ").append(table).append(" add column ").append(sql).append(";\n");
            }
        }
        return str.toString();
    }

    @Override
    public String getAllChange() {
        if (newTable) {            
            return (createTable+";\n").replaceFirst("CREATE TABLE", "CREATE TABLE if not exists");
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
        String[] sql = createTableSql.split("\n");
        List<ColumnBean> list = new ArrayList(0);
        for (String clo : sql) {
            if (clo.trim().startsWith("`")) {
                list.add(new ColumnBean(getCloName(clo), clo.substring(0, clo.length() - 1)));
            }
        }
        return list;
    }

    private static String getCloName(String ddl) {
        ddl = ddl.trim();
        return ddl.substring(1, ddl.indexOf("`", 1));
    }

}
