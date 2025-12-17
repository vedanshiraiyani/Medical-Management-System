package ui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Reports extends JFrame {

    JTable table;
    DefaultTableModel model;

    public Reports() {
        setTitle("Sales Reports");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(
                new String[]{"Code", "Name", "Quantity Sold", "MRP", "Total Price", "Date"}, 0
        );

        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        add(scrollPane, BorderLayout.CENTER);

        loadReports();

        setVisible(true);
    }

    private void loadReports() {
        try (Connection con = DBConnection.getConnection()) {

            String sql =
                "SELECT code, name, quantity, mrp, price, mdate " +
                "FROM MedicalReport " +
                "ORDER BY mdate DESC";

            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getDouble("mrp"),
                        rs.getDouble("price"),
                        rs.getString("mdate")
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading reports");
        }
    }
}
