package view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos; // Add this import statement

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Maze;
import model.MazeSolver;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class MazeApplication extends Application {
    private Maze maze;
    private MazeSolver solver;
    private Canvas mazeCanvas;
    private GraphicsContext gc;
    private int cellSize = 20;
    private Label statusLabel;
    private String currentMazeName = "Labyrinthe";
    @SuppressWarnings("unused")
    private boolean[][] path;  
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Résolution de Labyrinthe");
        
        // Création des composants
        BorderPane root = new BorderPane();
        
        VBox leftBar = createLeftBar(primaryStage);
        root.setLeft(leftBar);

        
        // Zone centrale avec le canvas
        mazeCanvas = new Canvas(800, 600);
        gc = mazeCanvas.getGraphicsContext2D();
        root.setCenter(mazeCanvas);
        
        // Section inférieure avec les statistiques
        statusLabel = new Label("Prêt.");
        root.setBottom(statusLabel);
        BorderPane.setMargin(statusLabel, new Insets(10));
        
        Scene scene = new Scene(root, 850, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private VBox createLeftBar(Stage stage) {
        VBox leftBar = new VBox(10);
        leftBar.setAlignment(Pos.TOP_LEFT); // Align buttons to the left


        leftBar.setPadding(new Insets(10));

        
        Button loadButton = new Button("Charger un labyrinthe");
        loadButton.setOnAction(e -> loadMazeFromFile(stage));
        
        Button generateButton = new Button("Générer un labyrinthe");
        generateButton.setOnAction(e -> showGenerateMazeDialog());
        
        Button solveDFSButton = new Button("Résoudre avec DFS");
        solveDFSButton.setOnAction(e -> {
            if (maze != null && solver != null) {
                boolean solved = solver.solveDFS();
                drawMaze();
                updateStatus("DFS", solved, solver.getSteps(), solver.getExecutionTime());
            } else {
                statusLabel.setText("Veuillez d'abord créer ou charger un labyrinthe.");
            }
        });
        
        Button solveBFSButton = new Button("Résoudre avec BFS");
        solveBFSButton.setOnAction(e -> {
            if (maze != null && solver != null) {
                boolean solved = solver.solveBFS();
                drawMaze();
                updateStatus("BFS", solved, solver.getSteps(), solver.getExecutionTime());
            } else {
                statusLabel.setText("Veuillez d'abord créer ou charger un labyrinthe.");
            }
        });
        
        Button animateDFSButton = new Button("Animer DFS");
        animateDFSButton.setOnAction(e -> {
            if (maze != null && solver != null) {
                statusLabel.setText("Animation DFS en cours...");
                solver.animateDFS(solverState -> 
                    Platform.runLater(() -> {
                        drawMazeWithAnimation(solverState);
                        if (solverState.isSolutionFound()) {
                            updateStatus("DFS", true, solverState.getSteps(), solverState.getExecutionTime());
                        } else {
                            statusLabel.setText("Animation DFS en cours... Étapes: " + solverState.getSteps());
                        }
                    })
                );
            } else {
                statusLabel.setText("Veuillez d'abord créer ou charger un labyrinthe.");
            }
        });
        
        Button animateBFSButton = new Button("Animer BFS");
        animateBFSButton.setOnAction(e -> {
            if (maze != null && solver != null) {
                statusLabel.setText("Animation BFS en cours...");
                solver.animateBFS(solverState -> 
                    Platform.runLater(() -> {
                        drawMazeWithAnimation(solverState);
                        if (solverState.isSolutionFound()) {
                            updateStatus("BFS", true, solverState.getSteps(), solverState.getExecutionTime());
                        } else {
                            statusLabel.setText("Animation BFS en cours... Étapes: " + solverState.getSteps());
                        }
                    })
                );
            } else {
                statusLabel.setText("Veuillez d'abord créer ou charger un labyrinthe.");
            }
        });

        // Nouveau bouton pour la comparaison
        Button compareButton = new Button("Comparer les algorithmes");
        compareButton.setOnAction(e -> {
            if (maze != null && solver != null) {
                statusLabel.setText("Comparaison des algorithmes en cours...");
                
                // Exécuter la comparaison dans un thread séparé pour ne pas bloquer l'interface
                new Thread(() -> {
                    Map<String, Object> results = solver.compareAlgorithms();
                    
                    Platform.runLater(() -> {
                        // Afficher la fenêtre de comparaison
                        ComparatorView.showComparison(
                            currentMazeName,
                            (long) results.get("dfsTime"),
                            (long) results.get("bfsTime"),
                            (int) results.get("dfsSteps"),
                            (int) results.get("bfsSteps"),
                            (boolean) results.get("dfsSolved"),
                            (boolean) results.get("bfsSolved")
                        );
                        
                        // Dessiner le chemin BFS (généralement le plus optimal)
                        path = (boolean[][]) results.get("bfsPath");
                        drawMaze();
                        statusLabel.setText("Comparaison effectuée. Affichage du chemin BFS.");
                    });
                }).start();
            } else {
                statusLabel.setText("Veuillez d'abord créer ou charger un labyrinthe.");
            }
        });
        
        Button clearButton = new Button("Effacer");
        clearButton.setOnAction(e -> {
            if (maze != null) {
                drawMazeOnly();
                statusLabel.setText("Solution effacée.");
            }
        });
        
        leftBar.getChildren().addAll(generateButton, loadButton, solveDFSButton, solveBFSButton, 
                                    animateDFSButton, animateBFSButton, compareButton, clearButton);
        return leftBar;

    }
    
    private void loadMazeFromFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Ouvrir un fichier de labyrinthe");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers texte", "*.txt")
        );
        
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                maze = new Maze(file.getAbsolutePath());
                solver = new MazeSolver(maze);
                resizeCanvas();
                drawMazeOnly();
                statusLabel.setText("Labyrinthe chargé: " + file.getName());
            } catch (IOException e) {
                showAlert("Erreur", "Impossible de charger le fichier", e.getMessage());
            }
        }
    }
    
    private void showGenerateMazeDialog() {
        Dialog<int[]> dialog = new Dialog<>();
        dialog.setTitle("Générer un labyrinthe");
        dialog.setHeaderText("Spécifiez la taille du labyrinthe");
        
        // Boutons
        ButtonType generateButtonType = new ButtonType("Générer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(generateButtonType, ButtonType.CANCEL);
        
        // Champs pour la taille
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        
        TextField rowsField = new TextField("15");
        TextField colsField = new TextField("20");
        
        content.getChildren().addAll(
                new Label("Nombre de lignes:"), rowsField,
                new Label("Nombre de colonnes:"), colsField
        );
        
        dialog.getDialogPane().setContent(content);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == generateButtonType) {
                try {
                    int rows = Integer.parseInt(rowsField.getText());
                    int cols = Integer.parseInt(colsField.getText());
                    return new int[]{rows, cols};
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(dimensions -> {
            int rows = dimensions[0];
            int cols = dimensions[1];
            
            maze = new Maze(rows, cols);
            solver = new MazeSolver(maze);
            resizeCanvas();
            drawMazeOnly();
            statusLabel.setText("Labyrinthe généré: " + rows + "×" + cols);
        });
    }
    
    private void resizeCanvas() {
        int width = maze.getCols() * cellSize;
        int height = maze.getRows() * cellSize;
        
        mazeCanvas.setWidth(width);
        mazeCanvas.setHeight(height);
    }
    
    private void drawMazeOnly() {
        if (maze == null) return;
        
        gc.clearRect(0, 0, mazeCanvas.getWidth(), mazeCanvas.getHeight());
        
        char[][] grid = maze.getGrid();
        for (int row = 0; row < maze.getRows(); row++) {
            for (int col = 0; col < maze.getCols(); col++) {
                int x = col * cellSize;
                int y = row * cellSize;
                
                // Dessiner d'abord le fond de la cellule
                gc.setFill(Color.WHITE);
                gc.fillRect(x, y, cellSize, cellSize);
                
                // Ensuite traiter le type de cellule
                switch (grid[row][col]) {
                    case '#':
                        gc.setFill(Color.BLACK);
                        gc.fillRect(x, y, cellSize, cellSize);
                        break;
                    case 'S':
                        gc.setFill(Color.GREEN);
                        gc.fillRect(x, y, cellSize, cellSize);
                        gc.setFill(Color.BLACK);
                        gc.fillText("S", x + cellSize/2 - 5, y + cellSize/2 + 5);
                        break;
                    case 'E':
                        gc.setFill(Color.RED);
                        gc.fillRect(x, y, cellSize, cellSize);
                        gc.setFill(Color.BLACK);
                        gc.fillText("E", x + cellSize/2, y + cellSize/2);
                        break;
                }
                
                // Dessiner les bordures des cellules
                gc.setStroke(Color.GRAY);
                gc.strokeRect(x, y, cellSize, cellSize);
            }
        }
    }
    
    private void drawMaze() {
        drawMazeOnly();
        
        // Dessiner le chemin solution
        boolean[][] path = solver.getPath();
        for (int row = 0; row < maze.getRows(); row++) {
            for (int col = 0; col < maze.getCols(); col++) {
                if (path[row][col] && maze.getGrid()[row][col] != 'S' && maze.getGrid()[row][col] != 'E') {
                    int x = col * cellSize;
                    int y = row * cellSize;
                    
                    gc.setFill(Color.LIGHTBLUE);
                    gc.fillRect(x, y, cellSize, cellSize);
                    
                    gc.setFill(Color.BLUE);
                    gc.fillOval(x + cellSize/3, y + cellSize/3, cellSize/3, cellSize/3);
                    
                    gc.setStroke(Color.GRAY);
                    gc.strokeRect(x, y, cellSize, cellSize);
                }
            }
        }
    }
    
    private void drawMazeWithAnimation(MazeSolver solverState) {
        drawMazeOnly();
        
        // Dessiner les cellules visitées
        boolean[][] visited = solverState.getVisited();
        for (int row = 0; row < maze.getRows(); row++) {
            for (int col = 0; col < maze.getCols(); col++) {
                if (visited[row][col] && maze.getGrid()[row][col] != 'S' && maze.getGrid()[row][col] != 'E') {
                    int x = col * cellSize;
                    int y = row * cellSize;
                    
                    gc.setFill(Color.LIGHTGRAY);
                    gc.fillRect(x, y, cellSize, cellSize);
                    
                    gc.setStroke(Color.GRAY);
                    gc.strokeRect(x, y, cellSize, cellSize);
                }
            }
        }
        
        // Dessiner les cellules actuellement explorées (front d'exploration)
        boolean[][] currentExploration = solverState.getCurrentExploration();
        for (int row = 0; row < maze.getRows(); row++) {
            for (int col = 0; col < maze.getCols(); col++) {
                if (currentExploration[row][col] && maze.getGrid()[row][col] != 'S' && maze.getGrid()[row][col] != 'E') {
                    int x = col * cellSize;
                    int y = row * cellSize;
                    
                    gc.setFill(Color.ORANGE);
                    gc.fillRect(x, y, cellSize, cellSize);
                    
                    gc.setStroke(Color.GRAY);
                    gc.strokeRect(x, y, cellSize, cellSize);
                }
            }
        }
        
        // Dessiner le chemin solution trouvé
        boolean[][] path = solverState.getPath();
        for (int row = 0; row < maze.getRows(); row++) {
            for (int col = 0; col < maze.getCols(); col++) {
                if (path[row][col] && maze.getGrid()[row][col] != 'S' && maze.getGrid()[row][col] != 'E') {
                    int x = col * cellSize;
                    int y = row * cellSize;
                    
                    gc.setFill(Color.LIGHTBLUE);
                    gc.fillRect(x, y, cellSize, cellSize);
                    
                    gc.setFill(Color.BLUE);
                    gc.fillOval(x + cellSize/3, y + cellSize/3, cellSize/3, cellSize/3);
                    
                    gc.setStroke(Color.GRAY);
                    gc.strokeRect(x, y, cellSize, cellSize);
                }
            }
        }
        
        // Redessiner S et E par-dessus pour qu'ils soient bien visibles
        char[][] grid = maze.getGrid();
        for (int row = 0; row < maze.getRows(); row++) {
            for (int col = 0; col < maze.getCols(); col++) {
                int x = col * cellSize;
                int y = row * cellSize;
                
                if (grid[row][col] == 'S') {
                    gc.setFill(Color.GREEN);
                    gc.fillRect(x, y, cellSize, cellSize);
                    gc.setFill(Color.BLACK);
                    gc.fillText("S", x + cellSize/2 - 5, y + cellSize/2 + 5);
                } else if (grid[row][col] == 'E') {
                    gc.setFill(Color.GREEN);
                    gc.fillRect(x, y, cellSize, cellSize);
                    gc.setFill(Color.WHITE);
                    gc.fillText("E", x + cellSize/2 - 5, y + cellSize/2 + 5);
                }
            }
        }
    }
    
    private void updateStatus(String algorithm, boolean solved, int steps, long time) {
        if (solved) {
            statusLabel.setText("Solution trouvée avec " + algorithm + " en " + steps + " étapes et " + time + " ms.");
        } else {
            statusLabel.setText("Aucune solution trouvée avec " + algorithm + ".");
        }
    }
    
    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
