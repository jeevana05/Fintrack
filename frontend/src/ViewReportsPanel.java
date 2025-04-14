import javax.swing.*;
import java.awt.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class ViewReportsPanel extends JPanel {
    private String userId;

    public ViewReportsPanel(String userId) {
        this.userId = userId;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        JLabel label = new JLabel("View Financial Reports", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(new Color(33, 150, 243));
        add(label, BorderLayout.NORTH);

        JPanel chartsPanel = new JPanel(new GridLayout(2, 1));
        add(chartsPanel, BorderLayout.CENTER);

        // Load charts asynchronously
        SwingUtilities.invokeLater(() -> {
            JFreeChart monthlyChart = createMonthlyChart();
            JFreeChart yearlyChart = createYearlyChart();

            if (monthlyChart != null)
                chartsPanel.add(new ChartPanel(monthlyChart));
            if (yearlyChart != null)
                chartsPanel.add(new ChartPanel(yearlyChart));
        });
    }

    private JFreeChart createMonthlyChart() {
        try {
            String monthCode = "04"; // April
            URL url = new URL("http://localhost:8080/transactions/report/monthly?userId=" + userId + "&monthCode=" + monthCode);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            Scanner sc = new Scanner(conn.getInputStream()).useDelimiter("\\A");
            String json = sc.hasNext() ? sc.next() : "";
            sc.close();

            Map<String, Double> report = parseJsonReport(json);
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            dataset.addValue(report.getOrDefault("income", 0.0), "Income", "April");
            dataset.addValue(report.getOrDefault("expense", 0.0), "Expense", "April");

            return ChartFactory.createBarChart("Monthly Income vs Expense", "Type", "Amount (₹)", dataset);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private JFreeChart createYearlyChart() {
        try {
            int year = java.time.LocalDate.now().getYear();
            URL url = new URL("http://localhost:8080/transactions/report/yearly?userId=" + userId + "&year=" + year);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            Scanner sc = new Scanner(conn.getInputStream()).useDelimiter("\\A");
            String json = sc.hasNext() ? sc.next() : "";
            sc.close();

            Map<String, Double> report = parseJsonReport(json);
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            dataset.addValue(report.getOrDefault("income", 0.0), "Income", String.valueOf(year));
            dataset.addValue(report.getOrDefault("expense", 0.0), "Expense", String.valueOf(year));

            return ChartFactory.createBarChart("Yearly Income vs Expense", "Type", "Amount (₹)", dataset);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Map<String, Double> parseJsonReport(String json) {
        // Very simple parser for {"income":123.0,"expense":456.0}
        double income = 0, expense = 0;
        try {
            json = json.replaceAll("[{}\"]", "");
            for (String part : json.split(",")) {
                String[] kv = part.split(":");
                if (kv[0].equals("income")) income = Double.parseDouble(kv[1]);
                else if (kv[0].equals("expense")) expense = Double.parseDouble(kv[1]);
            }
        } catch (Exception ignored) {}
        return Map.of("income", income, "expense", expense);
    }
}
