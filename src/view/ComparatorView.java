package view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class ComparatorView {
    
    public static void showComparison(String title, long dfsTime, long bfsTime, int dfsSteps, int bfsSteps, boolean dfsSolved, boolean bfsSolved) {
        Stage stage = new Stage();
        stage.setTitle("Comparaison des algorithmes");
        
        BorderPane root = new BorderPane();
        
        // En-tête
        Label titleLabel = new Label("Comparaison des algorithmes pour: " + title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setPadding(new Insets(10));
        root.setTop(titleLabel);
        
        // Tableau comparatif
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        // En-têtes du tableau
        grid.add(new Label(""), 0, 0);
        grid.add(new Label("DFS"), 1, 0);
        grid.add(new Label("BFS"), 2, 0);
        
        // Lignes du tableau
        grid.add(new Label("Temps d'exécution:"), 0, 1);
        grid.add(new Label(dfsTime + " ms"), 1, 1);
        grid.add(new Label(bfsTime + " ms"), 2, 1);
        
        grid.add(new Label("Nombre d'étapes:"), 0, 2);
        grid.add(new Label(String.valueOf(dfsSteps)), 1, 2);
        grid.add(new Label(String.valueOf(bfsSteps)), 2, 2);
        
        grid.add(new Label("Solution trouvée:"), 0, 3);
        grid.add(new Label(dfsSolved ? "Oui" : "Non"), 1, 3);
        grid.add(new Label(bfsSolved ? "Oui" : "Non"), 2, 3);
        
        VBox centerBox = new VBox(20);
        centerBox.setPadding(new Insets(10));
        centerBox.getChildren().add(grid);
        
        // Graphiques
        // Graphique pour le temps d'exécution
        CategoryAxis xAxis1 = new CategoryAxis();
        NumberAxis yAxis1 = new NumberAxis();
        BarChart<String, Number> timeChart = new BarChart<>(xAxis1, yAxis1);
        timeChart.setTitle("Temps d'exécution (ms)");
        yAxis1.setLabel("Millisecondes");
        xAxis1.setLabel("Algorithme");
        
        XYChart.Series<String, Number> timeSeries = new XYChart.Series<>();
        timeSeries.getData().add(new XYChart.Data<>("DFS", dfsTime));
        timeSeries.getData().add(new XYChart.Data<>("BFS", bfsTime));
        timeChart.getData().add(timeSeries);
        
        // Graphique pour le nombre d'étapes
        CategoryAxis xAxis2 = new CategoryAxis();
        NumberAxis yAxis2 = new NumberAxis();
        BarChart<String, Number> stepsChart = new BarChart<>(xAxis2, yAxis2);
        stepsChart.setTitle("Nombre d'étapes");
        yAxis2.setLabel("Étapes");
        xAxis2.setLabel("Algorithme");
        
        XYChart.Series<String, Number> stepsSeries = new XYChart.Series<>();
        stepsSeries.getData().add(new XYChart.Data<>("DFS", dfsSteps));
        stepsSeries.getData().add(new XYChart.Data<>("BFS", bfsSteps));
        stepsChart.getData().add(stepsSeries);
        
        centerBox.getChildren().addAll(timeChart, stepsChart);
        
        // Analyse
        Label analysisLabel = new Label("Analyse comparative:");
        analysisLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        String analysisText = generateAnalysis(dfsTime, bfsTime, dfsSteps, bfsSteps);
        Label analysisContent = new Label(analysisText);
        analysisContent.setWrapText(true);
        
        VBox analysisBox = new VBox(10);
        analysisBox.setPadding(new Insets(10));
        analysisBox.getChildren().addAll(analysisLabel, analysisContent);
        centerBox.getChildren().add(analysisBox);
        
        root.setCenter(centerBox);
        
        Scene scene = new Scene(root, 800, 700);
        stage.setScene(scene);
        stage.show();
    }
    
    private static String generateAnalysis(long dfsTime, long bfsTime, int dfsSteps, int bfsSteps) {
        StringBuilder sb = new StringBuilder();
        
        // Analyse du temps
        sb.append("• Temps d'exécution: ");
        if (dfsTime < bfsTime) {
            sb.append("DFS est plus rapide de ").append(bfsTime - dfsTime).append(" ms (")
              .append(String.format("%.1f", ((double) bfsTime / dfsTime - 1) * 100)).append("% plus rapide).\n\n");
        } else if (bfsTime < dfsTime) {
            sb.append("BFS est plus rapide de ").append(dfsTime - bfsTime).append(" ms (")
              .append(String.format("%.1f", ((double) dfsTime / bfsTime - 1) * 100)).append("% plus rapide).\n\n");
        } else {
            sb.append("Les deux algorithmes ont un temps d'exécution équivalent.\n\n");
        }
        
        // Analyse des étapes
        sb.append("• Nombre d'étapes: ");
        if (dfsSteps < bfsSteps) {
            sb.append("DFS explore moins de cellules (").append(bfsSteps - dfsSteps)
              .append(" cellules de moins, soit ").append(String.format("%.1f", ((double) bfsSteps / dfsSteps - 1) * 100))
              .append("% de moins).\n\n");
        } else if (bfsSteps < dfsSteps) {
            sb.append("BFS explore moins de cellules (").append(dfsSteps - bfsSteps)
              .append(" cellules de moins, soit ").append(String.format("%.1f", ((double) dfsSteps / bfsSteps - 1) * 100))
              .append("% de moins).\n\n");
        } else {
            sb.append("Les deux algorithmes explorent le même nombre de cellules.\n\n");
        }
        
        // Caractéristiques générales
        sb.append("• Caractéristiques:\n");
        sb.append("  - DFS: Explore en profondeur d'abord, peut trouver un chemin rapidement mais pas nécessairement le plus court. Utilise moins de mémoire.\n");
        sb.append("  - BFS: Explore en largeur d'abord, trouve toujours le chemin le plus court en nombre de cellules. Utilise plus de mémoire.\n\n");
        
        // Recommandation
        sb.append("• Recommandation: ");
        if (dfsTime < bfsTime && dfsSteps <= bfsSteps) {
            sb.append("Pour ce labyrinthe, DFS est globalement plus efficace.");
        } else if (bfsTime < dfsTime && bfsSteps <= dfsSteps) {
            sb.append("Pour ce labyrinthe, BFS est globalement plus efficace.");
        } else if (dfsTime < bfsTime) {
            sb.append("Si la rapidité est prioritaire, utilisez DFS. Si trouver le chemin optimal est prioritaire, utilisez BFS.");
        } else {
            sb.append("Si trouver le chemin optimal est prioritaire, utilisez BFS. BFS garantit le chemin le plus court.");
        }
        
        return sb.toString();
    }
}