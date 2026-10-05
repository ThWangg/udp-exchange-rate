package com.exchangerate.client.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class RateTableCellRenderer extends DefaultTableCellRenderer {

    private final Object[][] tableData;

    public RateTableCellRenderer(Object[][] tableData) {
        this.tableData = tableData;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // cot time
        if (column == 0 || row == 0) {
            c.setForeground(Color.BLACK);
            return c;
        }

        // so sanh voi dong lien truoc
        int prevRow = row - 1;
        if (prevRow < 0) {
            c.setForeground(Color.BLACK);
            return c;
        }

        try {
            Object prevVal = tableData[prevRow][column];
            Object curVal = tableData[row][column];

            if (prevVal == null || curVal == null || prevVal.toString().isEmpty() || curVal.toString().isEmpty()) {
                c.setForeground(Color.BLACK);
                return c;
            }

            double prev = Double.parseDouble(prevVal.toString());
            double cur = Double.parseDouble(curVal.toString());

            if (cur > prev) {
                c.setForeground(new Color(0, 150, 0));
            } else if (cur < prev) {
                c.setForeground(Color.RED);
            } else {
                c.setForeground(Color.BLACK);
            }
        } catch (NumberFormatException e) {
            c.setForeground(Color.BLACK);
        }

        return c;
    }
}
