package ui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StockManagement extends JFrame {

    JTextField codeField, nameField, qtyField, rateField, mrpField, expField;
    JTable table;
    DefaultTableModel model;

    public StockManagement() {

        setTitle("Stock Management");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        /* ---------- TOP PANEL (FORM + BUTTON) ---------- */
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridLayout(2, 6, 10, 10));

        formPanel.add(new JLabel("Code"));
        formPanel.add(new JLabel("Name"));
        formPanel.add(new JLabel("Quantity"));
        formPanel.add(new JLabel("Rate"));
        formPanel.add(new JLabel("MRP"));
        formPanel.add(new JLabel("Expiry"));

        codeField = new JTextField();
        nameField = new JTextField();
        qtyField  = new JTextField();
        rateField = new JTextField();
        mrpField  = new JTextField();
        expField  = new JTextField();

        formPanel.add(codeField);
        formPanel.add(nameField);
        formPanel.add(qtyField);
        formPanel.add(rateField);
        formPanel.add(mrpField);
        formPanel.add(expField);

        JButton addBtn = new JButton("Add Medicine");
        addBtn.setPreferredSize(new Dimension(150, 30));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addBtn);

        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        /* ---------- TABLE ---------- */
        model = new DefaultTableModel(
                new String[]{"Code", "Name", "Quantity", "Rate", "MRP", "Expiry"}, 0
        );
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        /* ---------- MAIN LAYOUT ---------- */
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        addBtn.addActionListener(e -> addMedicine());

        loadStock();
        setVisible(true); // 🔴 VERY IMPORTANT
    }

    private void addMedicine() {
        try (Connection con = DBConnection.getConnection()) {

            String sql = "INSERT INTO StockTable VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, codeField.getText());
            ps.setString(2, nameField.getText());
            ps.setInt(3, Integer.parseInt(qtyField.getText()));
            ps.setDouble(4, Double.parseDouble(rateField.getText()));
            ps.setDouble(5, Double.parseDouble(mrpField.getText()));
            ps.setString(6, expField.getText());

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Medicine Added");

            model.setRowCount(0);
            loadStock();

            codeField.setText("");
            nameField.setText("");
            qtyField.setText("");
            rateField.setText("");
            mrpField.setText("");
            expField.setText("");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void loadStock() {
        try (Connection con = DBConnection.getConnection()) {

            String sql = "SELECT * FROM StockTable";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getInt("quant"),
                        rs.getDouble("rate"),
                        rs.getDouble("mrp"),
                        rs.getString("exp")
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
