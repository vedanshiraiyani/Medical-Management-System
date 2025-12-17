package ui;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class Billing extends JFrame {

    JTextField codeField, qtyField;
    JLabel nameLabel, priceLabel, totalLabel;

    public Billing() {
        setTitle("Billing");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        codeField = new JTextField();
        qtyField = new JTextField();

        nameLabel = new JLabel("-");
        priceLabel = new JLabel("-");
        totalLabel = new JLabel("-");

        JButton billBtn = new JButton("Generate Bill");

        panel.add(new JLabel("Medicine Code"));
        panel.add(codeField);

        panel.add(new JLabel("Quantity"));
        panel.add(qtyField);

        panel.add(new JLabel("Medicine Name"));
        panel.add(nameLabel);

        panel.add(new JLabel("Price per unit"));
        panel.add(priceLabel);

        panel.add(new JLabel("Total Price"));
        panel.add(totalLabel);

        panel.add(new JLabel());
        panel.add(billBtn);

        add(panel);

        billBtn.addActionListener(e -> generateBill());

        setVisible(true);
    }

    private void generateBill() {

        // 🔹 Basic input validation
        if (codeField.getText().isEmpty() || qtyField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter medicine code and quantity");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            String code = codeField.getText();
            int qtyNeeded = Integer.parseInt(qtyField.getText());

            String fetchSql =
                "SELECT batch_id, name, quant, mrp " +
                "FROM StockBatch " +
                "WHERE code = ? AND quant > 0 " +
                "ORDER BY exp";

            PreparedStatement ps = con.prepareStatement(fetchSql);
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();

            double totalAmount = 0;
            String medicineName = null;

            // 🔹 FEFO stock deduction
            while (rs.next() && qtyNeeded > 0) {

                int batchId = rs.getInt("batch_id");
                int available = rs.getInt("quant");
                double mrp = rs.getDouble("mrp");
                medicineName = rs.getString("name");

                int used = Math.min(available, qtyNeeded);
                totalAmount += used * mrp;
                qtyNeeded -= used;

                PreparedStatement upd = con.prepareStatement(
                    "UPDATE StockBatch SET quant = ? WHERE batch_id = ?");
                upd.setInt(1, available - used);
                upd.setInt(2, batchId);
                upd.executeUpdate();
            }

            if (qtyNeeded > 0) {
                JOptionPane.showMessageDialog(this, "Insufficient stock");
                return;
            }

            int soldQty = Integer.parseInt(qtyField.getText());
            double unitPrice = totalAmount / soldQty;

            // 🔹 Record sale in MedicalReport
            PreparedStatement reportPs = con.prepareStatement(
                "INSERT INTO MedicalReport (code, name, quantity, mrp, price, mdate) " +
                "VALUES (?, ?, ?, ?, ?, ?)");

            reportPs.setString(1, code);
            reportPs.setString(2, medicineName);
            reportPs.setInt(3, soldQty);
            reportPs.setDouble(4, unitPrice);
            reportPs.setDouble(5, totalAmount);
            reportPs.setString(6, LocalDate.now().toString());

            reportPs.executeUpdate();

            // 🔹 Update UI
            nameLabel.setText(medicineName);
            priceLabel.setText(String.valueOf(unitPrice));
            totalLabel.setText(String.valueOf(totalAmount));

            JOptionPane.showMessageDialog(this, "Bill Generated Successfully (FEFO)");

            // 🔹 Clear inputs
            codeField.setText("");
            qtyField.setText("");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error occurred. Check terminal.");
        }
    }
}
