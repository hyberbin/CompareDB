/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyberbin.compare;

/**
 *
 * @author Hyberbin
 */
public interface IColumnMerge {

    String getAddList();

    String getAllChange();

    String getDropList();

    String getStatus();

    String getUpdateList();

    void merge(String createTableSql);
    
}
