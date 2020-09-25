package com.hyberbin.model;

import com.hyberbin.frame.CompareFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonEditor extends DefaultCellEditor {
    private DefaultCellEditor defaultCellEditor=new DefaultCellEditor(new JTextField());
    protected JButton button;
    private String label="";
    private boolean isPushed;
    private CompareFrame compareFrame;

    public ButtonEditor(CompareFrame compareFrame) {
        super(new JTextField());
        button = new JButton();
        button.setOpaque(true);
        this.compareFrame=compareFrame;
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });

    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        label = (String)value;
        if(value==null||((String)value).trim().length()==0){
            defaultCellEditor.stopCellEditing();
            isPushed = true;
            return defaultCellEditor.getTableCellEditorComponent(table, value, isSelected, row, column);
        }
        if (isSelected) {
            button.setForeground(table.getSelectionForeground());
            button.setBackground(table.getSelectionBackground());
        } else {
            button.setForeground(table.getForeground());
            button.setBackground(table.getBackground());
        }
        button.setText("跳转外键");
        isPushed = true;
        return button;
    }

    public Object getCellEditorValue() {
        if (isPushed) {
            String table=label.split("[.]")[0];
            System.out.println(table+"=="+label);
            compareFrame.loadTableDesc(table);
        }
        isPushed = false;
        return label;
    }

    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }

    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }
}