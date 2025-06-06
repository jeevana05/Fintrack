import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;


public class PredictYearlyBudgetUI {
    private JFrame frame;
    private JButton predictButton;
    private JTextArea resultArea;
    private JPanel chartPanel;
	private String userId;

    public PredictYearlyBudgetUI() {
		this.userId = userId;
        frame = new JFrame("Predict Yearly Budget");
        frame.setSize(1200, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(52, 152, 219));
        JLabel titleLabel = new JLabel("Predict Yearly Budget");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        frame.add(headerPanel, BorderLayout.NORTH);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 40));
        buttonPanel.setBackground(new Color(236, 240, 241));

        predictButton = new JButton("Predict Budget");
        predictButton.setFont(new Font("Arial", Font.BOLD, 18));
        predictButton.setBackground(new Color(96, 209, 110));
        predictButton.setForeground(Color.WHITE);
        predictButton.setFocusPainted(false);
        predictButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        predictButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                predictButton.setBackground(new Color(39, 174, 96));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                predictButton.setBackground(new Color(46, 204, 113));
            }
        });

        predictButton.addActionListener(this::predictBudget);
        buttonPanel.add(predictButton);
        frame.add(buttonPanel, BorderLayout.CENTER);

        // Result Panel
        JPanel resultPanel = new JPanel();
        resultPanel.setBackground(new Color(236, 240, 241));
        resultArea = new JTextArea(8, 40);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        resultArea.setEditable(false);
        resultArea.setWrapStyleWord(true);
        resultArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        resultPanel.add(scrollPane);
        frame.add(resultPanel, BorderLayout.SOUTH);

        // Chart Panel
        chartPanel = new JPanel();
        chartPanel.setBackground(Color.WHITE);
        frame.add(chartPanel, BorderLayout.EAST);

        frame.setVisible(true);
    }

    // 1. Fetch prediction from backend
    private void predictBudget(ActionEvent e) {
    try {
        int currentYear = LocalDate.now().getYear();
        URI uri = new URI("http://localhost:8080/transactions/report/yearly?userId=" + userId + "&year=" + currentYear);
        URL url = uri.toURL();

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() == 200) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder jsonBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonBuilder.append(line);
                }
                String response = jsonBuilder.toString();
                parseAndDisplayBudget(response); // This should handle JSON like {"income":12345.67, "expense":9876.54}
            }
        } else {
            updateResult("Failed to fetch yearly prediction: " + conn.getResponseCode());
        }

    } catch (Exception ex) {
        updateResult("Error fetching prediction");
        ex.printStackTrace();
    }
}


    // 2. Parse and show budget + chart
    private void parseAndDisplayBudget(String json) {
    if (json == null || json.isEmpty()) {
        updateResult("No data received.");
        return;
    }

    if (json.contains("error")) {
        updateResult("User not found. Please try again.");
        return;
    }

    try {
        if (json.contains("currentBudget")) {
            // Handle budget prediction response
            String currentBudgetStr = json.split("\"currentBudget\":")[1].split(",")[0].trim();
            double currentBudget = Double.parseDouble(currentBudgetStr);

            String futureBudgetsStr = json.split("\"futureBudgets\":")[1]
                    .replace("[", "")
                    .replace("]", "")
                    .replace("}", "")
                    .trim();
            String[] futureBudgets = futureBudgetsStr.split(",");

            double year1 = Double.parseDouble(futureBudgets[0].trim());
            double year2 = Double.parseDouble(futureBudgets[1].trim());
            double year3 = Double.parseDouble(futureBudgets[2].trim());

            StringBuilder result = new StringBuilder("📊 Budget Prediction:\n");
            result.append("Current Budget: ₹").append(currentBudget).append("\n");
            result.append("Year 1 (6% Inflation): ₹").append(year1).append("\n");
            result.append("Year 2 (7% Inflation): ₹").append(year2).append("\n");
            result.append("Year 3 (8% Inflation): ₹").append(year3).append("\n");
            updateResult(result.toString());

            showBudgetChart(currentBudget, year1, year2, year3);
        } 
        else if (json.contains("income") && json.contains("expense")) {
            // Handle yearly report response
            String incomeStr = json.split("\"income\":")[1].split(",")[0].trim();
            String expenseStr = json.split("\"expense\":")[1]
                                   .replace("}", "")
                                   .trim();

            double income = Double.parseDouble(incomeStr);
            double expense = Double.parseDouble(expenseStr);

            StringBuilder result = new StringBuilder("📈 Yearly Financial Report:\n");
            result.append("Total Income: ₹").append(income).append("\n");
            result.append("Total Expense: ₹").append(expense).append("\n");
            result.append("Net Savings: ₹").append(income - expense);
            updateResult(result.toString());
        } 
        else {
            updateResult("Unknown response format.");
        }
    } catch (Exception e) {
        e.printStackTrace();
        updateResult("Error parsing budget data.");
    }
}


    // 3. Show the chart
    private void showBudgetChart(double currentBudget, double year1, double year2, double year3) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(currentBudget, "Budget", "Current Year");
        dataset.addValue(year1, "Budget", "Year 1 (6% Inflation)");
        dataset.addValue(year2, "Budget", "Year 2 (7% Inflation)");
        dataset.addValue(year3, "Budget", "Year 3 (8% Inflation)");

        JFreeChart barChart = ChartFactory.createBarChart(
                "Budget Prediction",
                "Year",
                "Amount ($)",
                dataset
        );

        CategoryPlot plot = barChart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(200, 99, 71));

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryMargin(0.2);
        domainAxis.setTickLabelFont(new Font("SansSerif", Font.BOLD, 14));
        domainAxis.setMaximumCategoryLabelWidthRatio(0.9f);
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        chartPanel.removeAll();
        chartPanel.add(new ChartPanel(barChart));
        chartPanel.revalidate();
        chartPanel.repaint();
    }

    // 4. Update result area
    private void updateResult(String message) {
        SwingUtilities.invokeLater(() -> resultArea.setText(message));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PredictYearlyBudgetUI::new);
    }
}