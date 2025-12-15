package ui;

import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {

    public Dashboard() {
        setTitle("Medical Store Management - Dashboard");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JButton stockBtn = new JButton("Stock Management");
        JButton billingBtn = new JButton("Billing");
        JButton reportBtn = new JButton("Reports");
        JButton logoutBtn = new JButton("Logout");

        panel.add(stockBtn);
        panel.add(billingBtn);
        panel.add(reportBtn);
        panel.add(logoutBtn);

        add(panel);

        logoutBtn.addActionListener(e -> {
            dispose();
            new Login().setVisible(true);
        });
    }
}
