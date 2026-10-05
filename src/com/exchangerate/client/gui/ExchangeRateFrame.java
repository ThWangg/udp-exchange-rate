package com.exchangerate.client.gui;

import com.exchangerate.common.ExchangeRateData;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ExchangeRateFrame extends JFrame {

    private static final int MAX_ROWS = 10;
    private static final String[] COLUMNS = { "Time", "Tokyo", "Paris", "Seoul" };

    private final Object[][] tableData = new Object[MAX_ROWS][4];

    private final DefaultTableModel model;
    private final JTable table;

    // trang thai pause cho tung cot (0: tat ca, 1: tokyo, 2: paris, 3: seoul)
    private final boolean[] paused = { false, false, false, false };
    private final JButton[] buttons = new JButton[4];
    private final JLabel[] statusLabels = new JLabel[4];

    // dem so ban ghi da nhan (tinh tu ban ghi dau tien)
    private int receiveCount = 0;

    public ExchangeRateFrame() {
        super("Exchange Rate - UDP");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // khoi tao du lieu rong
        for (int r = 0; r < MAX_ROWS; r++) {
            for (int c = 0; c < 4; c++) {
                tableData[r][c] = "";
            }
        }

        model = new DefaultTableModel(tableData, COLUMNS) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(22);
        table.getTableHeader().setReorderingAllowed(false);

        // gan renderer cho cac cot ty gia
        RateTableCellRenderer renderer = new RateTableCellRenderer(tableData);
        for (int c = 0; c < 4; c++) {
            table.getColumnModel().getColumn(c).setCellRenderer(renderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(500, 220));
        add(scrollPane, BorderLayout.CENTER);

        // panel chua nut va status
        JPanel controlPanel = new JPanel(new GridLayout(2, 4));

        String[] colNames = { "Time (All)", "Tokyo", "Paris", "Seoul" };
        for (int i = 0; i < 4; i++) {
            final int idx = i;
            buttons[i] = new JButton("Pause");
            buttons[i].addActionListener(e -> togglePause(idx));
            controlPanel.add(buttons[i]);
        }

        for (int i = 0; i < 4; i++) {
            statusLabels[i] = new JLabel("running", SwingConstants.CENTER);
            controlPanel.add(statusLabels[i]);
        }

        add(controlPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void togglePause(int idx) {
        if (idx == 0) {
            // nhan nut time: dao trang thai tat ca
            boolean newState = !paused[0];
            for (int i = 0; i < 4; i++) {
                paused[i] = newState;
                updateButtonAndStatus(i);
            }
        } else {
            paused[idx] = !paused[idx];
            updateButtonAndStatus(idx);
        }
    }

    private void updateButtonAndStatus(int idx) {
        if (paused[idx]) {
            buttons[idx].setText("Resume");
            statusLabels[idx].setText("pause");
        } else {
            buttons[idx].setText("Pause");
            statusLabels[idx].setText("running");
        }
    }

    // goi tu client moi khi nhan du lieu moi
    public void updateData(ExchangeRateData data) {
        SwingUtilities.invokeLater(() -> {
            // tat ca deu pause thi bo qua
            if (paused[0] && paused[1] && paused[2] && paused[3])
                return;

            receiveCount++;

            // tinh vi tri dong can ghi
            int row;
            if (receiveCount == 1) {
                row = 0; // dong dau tien la base row
            } else {
                // dong 2 den dong 10 (index 1..9), ghi de xoay vong
                row = 1 + (receiveCount - 2) % 9;
            }

            // cap nhat du lieu, cot bi pause se de rong ""
            tableData[row][0] = paused[0] ? "" : data.time;
            tableData[row][1] = paused[1] ? "" : String.valueOf(data.tokyo);
            tableData[row][2] = paused[2] ? "" : String.valueOf(data.paris);
            tableData[row][3] = paused[3] ? "" : String.valueOf(data.seoul);

            // cap nhat model de JTable refresh
            for (int c = 0; c < 4; c++) {
                model.setValueAt(tableData[row][c], row, c);
            }
        });
    }
}
