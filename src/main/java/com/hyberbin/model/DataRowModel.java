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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.table.DefaultTableModel;
import org.jplus.hyb.database.bean.FieldColumn;
import org.jplus.util.ObjectHelper;

/**
 *
 * @author Hyberbin
 */
public class DataRowModel extends DefaultTableModel{
    private final List<FieldColumn> columns;
    private static final SimpleDateFormat DATE_FORMAT=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    public DataRowModel(List<FieldColumn> columns){
        this.columns=columns;
        getDataVector().clear();
        columnIdentifiers.clear();
        for(FieldColumn column:columns){
            addColumn(column.getColumn());
        }
    }

    public void addRow(Map map) {
        List<String> data=new ArrayList(0);
        for(FieldColumn column:columns){
            data.add(converToString(map.get(column.getColumn())));
        }
        this.addRow(data.toArray(new String[]{}));
    }
    
    public void addRow(List<Map> maps) {
        if(ObjectHelper.isNotEmpty(maps)){
            for(Map map:maps){
                addRow(map);
            }
        }
    }
    
    private String converToString(Object data){
        if(data==null){
            return "";
        }else if(data instanceof Date){
            return DATE_FORMAT.format((Date)data);
        }else {
            return data.toString();
        }
    }
    
}
