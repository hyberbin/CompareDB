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
package com.hyberbin.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.table.DefaultTableModel;
import org.jplus.util.ObjectHelper;

/**
 *
 * @author Hyberbin
 */
public final class TableListModel extends DefaultTableModel {

    private final static String[] tableHeader = {"选择", "表名","备注", "状态", "结果", "SQL"};
    private final boolean[] isEditAble = {true, false, false, false, false, true};
    private int finished=0;

    /**
     * 默认情况下这个方法不用重新实现的，但是这样就会造成如果这个列式boolean的类型，就当做string来处理了
     * 如果是boolean的类型那么用checkbox来显示
     * @return
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return Boolean.class;
        } else {
            return String.class;
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return isEditAble[column]; //To change body of generated methods, choose Tools | Templates.
    }

    public TableListModel() {
        setColumnIdentifiers(tableHeader);
    }

    /**
     * 设置全选
     */
    public void selectAll() {
        for (int i = 0; i < this.getRowCount(); i++) {
            this.setValueAt(true, i, 0);
        }
    }

    public int getSelectedCount() {
        int count = 0;
        for (int i = 0; i < this.getRowCount(); i++) {
            boolean b = (Boolean) getValueAt(i, 0);
            if (b) {
                count++;
            }
        }
        return count;
    }

    /**
     * 选中所有表
     */
    public List<String> getSelectedTables() {
        List<String> list = new ArrayList<String>(0);
        for (int i = 0; i < this.getRowCount(); i++) {
            boolean b = (Boolean) getValueAt(i, 0);
            if (b) {
                list.add(this.getValueAt(i, 1).toString());
            }
        }
        return list;
    }
    
    public List<String> getSelectedTablesComments() {
        List<String> list = new ArrayList<String>(0);
        for (int i = 0; i < this.getRowCount(); i++) {
            boolean b = (Boolean) getValueAt(i, 0);
            if (b) {
                list.add(this.getValueAt(i, 2).toString());
            }
        }
        return list;
    }

    /**
     * 反选所有表
     */
    public void reSelectAllTable() {
        for (int i = 0; i < this.getRowCount(); i++) {
            boolean b = (Boolean) getValueAt(i, 0);
            this.setValueAt(!b, i, 0);
        }
    }
    
    public void clear(){
        getDataVector().clear();
    }

    /**
     * 选中所有表
     */
    public void selectAllTable() {
        for (int i = 0; i < this.getRowCount(); i++) {
            this.setValueAt(true, i, 0);
        }
    }

    /**
     * 全不选
     */
    public void unselectAll() {
        for (int i = 0; i < this.getRowCount(); i++) {
            this.setValueAt(false, i, 0);
        }
    }

    public void addTable(String tableName,String comment) {
        this.addRow(new Object[]{true, tableName,comment, "未比较", "", ""});
    }

    public void ini() {
        for (int i = 0; i < this.getRowCount(); i++) {
            this.setValueAt("未比较", i, 3);
            this.setValueAt("", i, 4);
            this.setValueAt("", i, 5);
        }
    }

    public void finishRow(int row, String staus, String sql) {
        this.setValueAt("已完成", row, 3);
        this.setValueAt(staus, row, 4);
        if (!ObjectHelper.isNullOrEmptyString(sql)) {
            this.setValueAt(sql, row, 5);
            finished++;
        }
    }

    public void finishRow(String table, String staus, String sql) {
        for (int i = 0; i < getRowCount(); i++) {
            if (table.equals(getValueAt(i, 1))) {
                this.setValueAt("已完成", i, 3);
                this.setValueAt(staus, i, 4);
                if (!ObjectHelper.isNullOrEmptyString(sql)) {
                    this.setValueAt(sql, i, 5);
                }
                finished++;
                break;
            }
        }

    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    public int getFinished() {
        return finished;
    }

    public void addTables(final List<Map> tableList) {
        new Thread() {

            @Override
            public void run() {
                if (ObjectHelper.isNotEmpty(tableList)) {
                    for (Map map : tableList) {
                        Object table_name = map.get("TABLE_NAME");
                        Object table_comment = map.get("TABLE_COMMENT");
                        addTable(table_name.toString(), table_comment==null?"":table_comment.toString());
                    }
                }
            }

        }.start();
    }
}
