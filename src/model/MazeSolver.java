package model;

import java.util.*;
import java.util.function.Consumer;

public class MazeSolver {
    private Maze maze;
    private boolean[][] visited;
    private boolean[][] path;
    private boolean[][] currentExploration;
    private int steps;
    private boolean solutionFound;
    private long executionTime;
    
    public MazeSolver(Maze maze) {
        this.maze = maze;
        this.visited = new boolean[maze.getRows()][maze.getCols()];
        this.path = new boolean[maze.getRows()][maze.getCols()];
        this.currentExploration = new boolean[maze.getRows()][maze.getCols()];
        this.steps = 0;
        this.solutionFound = false;
    }
    
    // Version DFS avec animation
    public void animateDFS(Consumer<MazeSolver> onStep) {
        // Réinitialiser les tableaux
        visited = new boolean[maze.getRows()][maze.getCols()];
        path = new boolean[maze.getRows()][maze.getCols()];
        currentExploration = new boolean[maze.getRows()][maze.getCols()];
        steps = 0;
        solutionFound = false;
        
        Position start = maze.getStart();
        
        // Lancer le DFS dans un thread séparé pour ne pas bloquer l'interface
        new Thread(() -> {
            dfsRecursiveWithAnimation(start.getRow(), start.getCol(), onStep);
            onStep.accept(this); // Appel final pour indiquer que l'algorithme est terminé
        }).start();
    }
    
    private boolean dfsRecursiveWithAnimation(int row, int col, Consumer<MazeSolver> onStep) {
        // Vérifier si on est hors des limites ou sur un mur
        if (row < 0 || row >= maze.getRows() || col < 0 || col >= maze.getCols() 
                || maze.isWall(row, col) || visited[row][col]) {
            return false;
        }
        
        // Marquer comme visité
        visited[row][col] = true;
        currentExploration[row][col] = true;
        steps++;
        
        // Notifier l'interface graphique pour mettre à jour l'affichage
        onStep.accept(this);
        
        // Courte pause pour l'animation
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        Position current = new Position(row, col);
        Position end = maze.getEnd();
        
        // Si on a trouvé la sortie
        if (current.equals(end)) {
            path[row][col] = true;
            solutionFound = true;
            return true;
        }
        
        // Explorer dans les 4 directions
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // haut, bas, gauche, droite
        
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            
            if (dfsRecursiveWithAnimation(newRow, newCol, onStep)) {
                // Si un chemin a été trouvé, ajouter la cellule actuelle au chemin
                path[row][col] = true;
                return true;
            }
        }
        
        // Retirer cette cellule de l'exploration courante car c'est un cul-de-sac
        currentExploration[row][col] = false;
        onStep.accept(this); // Mise à jour de l'affichage
        
        return false;
    }
    
    // Version BFS avec animation
    public void animateBFS(Consumer<MazeSolver> onStep) {
        // Réinitialiser les tableaux
        visited = new boolean[maze.getRows()][maze.getCols()];
        path = new boolean[maze.getRows()][maze.getCols()];
        currentExploration = new boolean[maze.getRows()][maze.getCols()];
        steps = 0;
        solutionFound = false;
        
        Position start = maze.getStart();
        Position end = maze.getEnd();
        
        // Lancer le BFS dans un thread séparé
        new Thread(() -> {
            // File pour BFS
            Queue<Position> queue = new LinkedList<>();
            
            // Map pour tracer le chemin
            Map<Position, Position> parentMap = new HashMap<>();
            
            queue.add(start);
            visited[start.getRow()][start.getCol()] = true;
            currentExploration[start.getRow()][start.getCol()] = true;
            
            onStep.accept(this); // Mise à jour initiale
            
            boolean found = false;
            
            while (!queue.isEmpty() && !found) {
                Position current = queue.poll();
                steps++;
                
                // Retirer de l'exploration courante car on l'a traité
                currentExploration[current.getRow()][current.getCol()] = false;
                onStep.accept(this);
                
                // Pause pour l'animation
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                if (current.equals(end)) {
                    found = true;
                    solutionFound = true;
                    break;
                }
                
                // Explorer dans les 4 directions
                int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // haut, bas, gauche, droite
                
                for (int[] dir : directions) {
                    int newRow = current.getRow() + dir[0];
                    int newCol = current.getCol() + dir[1];
                    
                    // Vérifier si la position est valide
                    if (newRow >= 0 && newRow < maze.getRows() && newCol >= 0 && newCol < maze.getCols()
                            && !maze.isWall(newRow, newCol) && !visited[newRow][newCol]) {
                        
                        Position neighbor = new Position(newRow, newCol);
                        queue.add(neighbor);
                        visited[newRow][newCol] = true;
                        currentExploration[newRow][newCol] = true;
                        parentMap.put(neighbor, current);
                        
                        onStep.accept(this); // Mise à jour pour chaque nouvelle cellule visitée
                        
                        // Pause brève pour l'animation
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            
            // Reconstituer le chemin si trouvé
            if (found) {
                Position current = end;
                while (!current.equals(start)) {
                    path[current.getRow()][current.getCol()] = true;
                    current = parentMap.get(current);
                    
                    onStep.accept(this); // Mise à jour pour montrer le chemin
                    
                    // Pause pour l'animation du chemin
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                path[start.getRow()][start.getCol()] = true;
                onStep.accept(this); // Mise à jour finale
            }
        }).start();
    }
    
    // Méthodes classiques pour une résolution sans animation
    public boolean solveDFS() {
        long startTime = System.currentTimeMillis();
        
        // Réinitialiser les tableaux
        visited = new boolean[maze.getRows()][maze.getCols()];
        path = new boolean[maze.getRows()][maze.getCols()];
        steps = 0;
        
        Position start = maze.getStart();
        
        boolean result = dfsRecursive(start.getRow(), start.getCol());
        
        long endTime = System.currentTimeMillis();
        System.out.println("DFS terminé en " + (endTime - startTime) + " ms");
        System.out.println("Nombre d'étapes: " + steps);
        
        return result;
    }
    
    private boolean dfsRecursive(int row, int col) {
        // Vérifier si on est hors des limites ou sur un mur
        if (row < 0 || row >= maze.getRows() || col < 0 || col >= maze.getCols() 
                || maze.isWall(row, col) || visited[row][col]) {
            return false;
        }
        
        // Marquer comme visité
        visited[row][col] = true;
        steps++;
        
        Position current = new Position(row, col);
        Position end = maze.getEnd();
        
        // Si on a trouvé la sortie
        if (current.equals(end)) {
            path[row][col] = true;
            return true;
        }
        
        // Explorer dans les 4 directions
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // haut, bas, gauche, droite
        
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            
            if (dfsRecursive(newRow, newCol)) {
                // Si un chemin a été trouvé, ajouter la cellule actuelle au chemin
                path[row][col] = true;
                return true;
            }
        }
        
        return false;
    }
    
    public boolean solveBFS() {
        long startTime = System.currentTimeMillis();
        
        // Réinitialiser les tableaux
        visited = new boolean[maze.getRows()][maze.getCols()];
        path = new boolean[maze.getRows()][maze.getCols()];
        steps = 0;
        
        Position start = maze.getStart();
        Position end = maze.getEnd();
        
        // File pour BFS
        Queue<Position> queue = new LinkedList<>();
        
        // Map pour tracer le chemin
        Map<Position, Position> parentMap = new HashMap<>();
        
        queue.add(start);
        visited[start.getRow()][start.getCol()] = true;
        
        boolean found = false;
        
        while (!queue.isEmpty() && !found) {
            Position current = queue.poll();
            steps++;
            
            if (current.equals(end)) {
                found = true;
                break;
            }
            
            // Explorer dans les 4 directions
            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // haut, bas, gauche, droite
            
            for (int[] dir : directions) {
                int newRow = current.getRow() + dir[0];
                int newCol = current.getCol() + dir[1];
                
                // Vérifier si la position est valide
                if (newRow >= 0 && newRow < maze.getRows() && newCol >= 0 && newCol < maze.getCols()
                        && !maze.isWall(newRow, newCol) && !visited[newRow][newCol]) {
                    
                    Position neighbor = new Position(newRow, newCol);
                    queue.add(neighbor);
                    visited[newRow][newCol] = true;
                    parentMap.put(neighbor, current);
                }
            }
        }
        
        // Reconstituer le chemin si trouvé
        if (found) {
            Position current = end;
            while (!current.equals(start)) {
                path[current.getRow()][current.getCol()] = true;
                current = parentMap.get(current);
            }
            path[start.getRow()][start.getCol()] = true;
        }
        
        long endTime = System.currentTimeMillis();
        System.out.println("BFS terminé en " + (endTime - startTime) + " ms");
        System.out.println("Nombre d'étapes: " + steps);
        
        return found;
    }

    // Méthode pour comparaison directe
    public Map<String, Object> compareAlgorithms() {
        Map<String, Object> results = new HashMap<>();
        
        // Exécuter DFS
        boolean dfsSolved = solveDFS();
        long dfsTime = this.executionTime;
        int dfsSteps = this.steps;
        boolean[][] dfsPath = new boolean[maze.getRows()][maze.getCols()];
        for (int i = 0; i < maze.getRows(); i++) {
            dfsPath[i] = Arrays.copyOf(path[i], maze.getCols());
        }
        
        // Exécuter BFS
        boolean bfsSolved = solveBFS();
        long bfsTime = this.executionTime;
        int bfsSteps = this.steps;
        boolean[][] bfsPath = new boolean[maze.getRows()][maze.getCols()];
        for (int i = 0; i < maze.getRows(); i++) {
            bfsPath[i] = Arrays.copyOf(path[i], maze.getCols());
        }
        
        // Stocker les résultats
        results.put("dfsTime", dfsTime);
        results.put("bfsTime", bfsTime);
        results.put("dfsSteps", dfsSteps);
        results.put("bfsSteps", bfsSteps);
        results.put("dfsSolved", dfsSolved);
        results.put("bfsSolved", bfsSolved);
        results.put("dfsPath", dfsPath);
        results.put("bfsPath", bfsPath);
        
        return results;
    }
    
    // Getters
    public boolean[][] getPath() {
        return path;
    }
    
    public boolean[][] getVisited() {
        return visited;
    }
    
    public boolean[][] getCurrentExploration() {
        return currentExploration;
    }
    
    public int getSteps() {
        return steps;
    }
    
    public boolean isSolutionFound() {
        return solutionFound;
    }

    public long getExecutionTime() {
        return executionTime;
    }
}

