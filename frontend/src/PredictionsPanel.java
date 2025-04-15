import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;

public class PredictionsPanel extends JPanel {

    private JTextField userIdField;
    private JButton predictButton;
    private JTextArea resultArea;
    private JPanel chartPanel;
	private String userId;

    /* public PredictionsPanel(String userId) {
		this.userId = userId;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        JLabel label = new JLabel("Predictions Based on Spending Habits", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(new Color(33, 150, 243));
        add(label, BorderLayout.NORTH);

        // Remove user ID field and show only the button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(new Color(236, 240, 241));
        buttonPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        predictButton = new JButton("Predict Budget");
        predictButton.setFont(new Font("Arial", Font.BOLD, 18));
        predictButton.setBackground(new Color(96, 209, 110));
        predictButton.setForeground(Color.WHITE);
        predictButton.setFocusPainted(false);
        predictButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        predictButton.addActionListener(this::predictBudget);

        buttonPanel.add(predictButton);
        add(buttonPanel, BorderLayout.CENTER);

        resultArea = new JTextArea(6, 40);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        resultArea.setWrapStyleWord(true);
        resultArea.setLineWrap(true);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        //add(scrollPane, BorderLayout.SOUTH);

        chartPanel = new JPanel();
        chartPanel.setPreferredSize(new Dimension(500, 400));
        chartPanel.setBackground(Color.WHITE);
        add(chartPanel, BorderLayout.EAST);
    } */
/* 	public PredictionsPanel(String userId) {
    this.userId = userId;
    setLayout(new BorderLayout());
    setBackground(new Color(245, 245, 245));

    JLabel label = new JLabel("Predictions Based on Spending Habits", SwingConstants.CENTER);
    label.setFont(new Font("Arial", Font.BOLD, 18));
    label.setForeground(new Color(33, 150, 243));
    add(label, BorderLayout.NORTH);

    // Button panel (left side)
    JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
    leftPanel.setBackground(new Color(236, 240, 241));
    leftPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

    predictButton = new JButton("Predict Budget");
    predictButton.setFont(new Font("Arial", Font.BOLD, 18));
    predictButton.setBackground(new Color(96, 209, 110));
    predictButton.setForeground(Color.WHITE);
    predictButton.setFocusPainted(false);
    predictButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    predictButton.addActionListener(this::predictBudget);

    leftPanel.add(predictButton);

    // Chart panel (right side)
    chartPanel = new JPanel();
    chartPanel.setPreferredSize(new Dimension(500, 400));
    chartPanel.setBackground(Color.WHITE);
    chartPanel.setLayout(new BorderLayout());

    // Split pane between left and right
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, chartPanel);
    splitPane.setDividerLocation(300); // Adjust width for left panel
    splitPane.setResizeWeight(0.2); // Ratio: 20% left, 80% right
    splitPane.setEnabled(false); // Prevent dragging if desired

    add(splitPane, BorderLayout.CENTER);
} */
    public PredictionsPanel(String userId) {
        this.userId = userId;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // Title
        JLabel label = new JLabel("Predictions Based on Spending Habits", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(new Color(33, 150, 243));
        add(label, BorderLayout.NORTH);

        // Left panel: Button + Result Area
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(236, 240, 241));
        leftPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Predict Budget button
        predictButton = new JButton("Predict Budget");
        predictButton.setFont(new Font("Arial", Font.BOLD, 18));
        predictButton.setBackground(new Color(96, 209, 110));
        predictButton.setForeground(Color.WHITE);
        predictButton.setFocusPainted(false);
        predictButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        predictButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        predictButton.addActionListener(this::predictBudget);
        leftPanel.add(predictButton);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20))); // spacing

        // Result Area
        resultArea = new JTextArea(6, 30);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        resultArea.setWrapStyleWord(true);
        resultArea.setLineWrap(true);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(scrollPane);

        // Right panel: Chart
        chartPanel = new JPanel();
        chartPanel.setPreferredSize(new Dimension(500, 400));
        chartPanel.setBackground(Color.WHITE);

        // SplitPane to separate left (button/text) and right (chart)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, chartPanel);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.3); // left panel gets less space
        add(splitPane, BorderLayout.CENTER);
    }

    private void predictBudget(ActionEvent e) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                updateResult("User ID is missing. Please log in again.");
                return;
            }

            URI uri = new URI("http://localhost:8080/predict/fetch?userId=" + userId);
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String response = reader.readLine();
                parseAndDisplayBudget(response);
            }

        } catch (Exception ex) {
            updateResult("Error fetching prediction");
            ex.printStackTrace();
        }
    }

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
            result.append("Current Budget: $").append(currentBudget).append("\n");
            result.append("Year 1 (6% Inflation): $").append(year1).append("\n");
            result.append("Year 2 (7% Inflation): $").append(year2).append("\n");
            result.append("Year 3 (8% Inflation): $").append(year3).append("\n");
            updateResult(result.toString());

            showBudgetChart(currentBudget, year1, year2, year3);

        } catch (Exception e) {
            e.printStackTrace();
            updateResult("Error parsing budget data.");
        }
    }

    private void showBudgetChart(double currentBudget, double year1, double year2, double year3) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(currentBudget, "Budget", "Current Year");
        dataset.addValue(year1, "Budget", "Year 1 (6% Inflation)");
        dataset.addValue(year2, "Budget", "Year 2 (7% Inflation)");
        dataset.addValue(year3, "Budget", "Year 3 (8% Inflation)");

        JFreeChart barChart = ChartFactory.createBarChart("Budget Prediction", "Year", "Amount ($)", dataset);
        CategoryPlot plot = barChart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(200, 99, 71));

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        domainAxis.setTickLabelFont(new Font("SansSerif", Font.BOLD, 14));
        domainAxis.setCategoryMargin(0.3f);

        chartPanel.removeAll();
        chartPanel.add(new ChartPanel(barChart));
        chartPanel.revalidate();
        chartPanel.repaint();
    }

    private void updateResult(String message) {
        SwingUtilities.invokeLater(() -> resultArea.setText(message));
    }
}
