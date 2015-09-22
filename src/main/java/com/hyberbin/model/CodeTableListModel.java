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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.table.DefaultTableModel;
import org.jplus.util.ObjectHelper;

/**
 *
 * @author Hyberbin
 */
public final class CodeTableListModel extends DefaultTableModel {

    private final static String[] tableHeader = {"选择", "表名", "备注"};
    private final boolean[] isEditAble = {true, false, false};
    private final static Set<String> DEFAULT_CODE_TABLE = new HashSet<String>(Arrays.asList(("dc_bjkqlx dc_bjqqlx dc_clgl_clfylx dc_cm_bdlx dc_cm_wjlx dc_gzblx dc_kblx dc_sjzdlx dc_yhlx dc_yyxxlx jy_bxlx jy_hjlx jy_sfzjlx jy_szdcxlx jy_zxxbjlx oa_lxrqz001 dc_gzl_lb jy_hygzlb "
            + "jy_jshjlb jy_wjlb jy_xshjlb jy_xslb ga_214_12 ga_324_1 gb_12407 gb_14946_1_a1 gb_14946_1_a10 gb_14946_1_a11 gb_14946_1_a15 gb_14946_1_a62 gb_14946_1_a8 gb_14946_1_a9 gb_14946_1_a9_01 "
            + "gb_16835 gb_16835_1 gb_16835_2 gb_16835_3 gb_2260 gb_2260_1 gb_2260_2 gb_2260_3 gb_2261_1 gb_2261_2 gb_2261_3 gb_2261_4 gb_2659 gb_3304 gb_4658 gb_4761 gb_4762 gb_4881 "
            + "gb_6565 gb_6864 gb_8561 gb_8561_1 gb_8561_2 gb_8563_1 gb_8563_2 gb_8563_3 gb_8563_3_1 gb_8563_3_2 jy_cfmc jy_gatqw jy_gwzy jy_jb jy_bzyd jy_jdfs jy_jldj jy_jlfs jy_jszgzl jy_lgyy "
            + "jy_prqk jy_rkxd jy_rxfs jy_ssmzsyjxms jy_szdqjjsx jy_szdqjjsx jy_xsdqzt jy_xx jy_xxbb jy_xxjyjgjbz jy_zxxkc jy_zxxkcdj "
            + "jy_zxxbzlb dc_kpmd dc_kqlx dc_xxzt dc_cdz dc_gn dc_mkztbm dc_ptyyz dc_rep_module dc_rep_parameter dc_rep_report dc_qxzyy01 dc_cm_yyts").split(" ")));

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

    public CodeTableListModel() {
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
    
    public String getSelectedCodeTables(){
        StringBuilder table=new StringBuilder();
        for (int i = 0; i < this.getRowCount(); i++) {
            boolean b = (Boolean) getValueAt(i, 0);
            if (b) {
                table.append(" ").append(this.getValueAt(i, 1).toString());
            }
        }
        return table.toString();
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

    public void clear() {
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

    public void addTable(String tableName, String comment) {
        boolean selected = DEFAULT_CODE_TABLE.contains(tableName);
        this.addRow(new Object[]{selected, tableName, comment});
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
