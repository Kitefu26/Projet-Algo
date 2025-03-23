package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class Maze {
    private char[][] grid;
    private int rows;
    private int cols;
    private Position start;
    private Position end;
    
    // Constructeur pour un labyrinthe à partir d'un fichier
    public Maze(String filePath) throws IOException {
        loadFromFile(filePath);
    }
    
    // Constructeur pour générer un labyrinthe aléatoire
    public Maze(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        generateRandomMaze();
    }
    
    // Chargement d'un labyrinthe depuis un fichier
    private void loadFromFile(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        
        // Déterminer le nombre de lignes et de colonnes
        String line;
        int lineCount = 0;
        int colCount = 0;
        
        while ((line = reader.readLine()) != null) {
            lineCount++;
            colCount = Math.max(colCount, line.length());
        }
        
        this.rows = lineCount;
        this.cols = colCount;
        this.grid = new char[rows][cols];
        
        // Relire le fichier pour remplir la grille
        reader.close();
        reader = new BufferedReader(new FileReader(filePath));
        
        int row = 0;
        while ((line = reader.readLine()) != null) {
            for (int col = 0; col < line.length(); col++) {
                char cell = line.charAt(col);
                grid[row][col] = cell;
                
                // Identifier les positions de départ et de fin
                if (cell == 'S') {
                    start = new Position(row, col);
                } else if (cell == 'E') {
                    end = new Position(row, col);
                }
            }
            row++;
        }
        
        reader.close();
    }
    
    // Génération d'un labyrinthe aléatoire simple
    private void generateRandomMaze() {
        grid = new char[rows][cols];
        Random random = new Random();
        
        // Remplir le labyrinthe avec des murs
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (i == 0 || j == 0 || i == rows - 1 || j == cols - 1) {
                    grid[i][j] = '#'; // Bordures
                } else {
                    grid[i][j] = random.nextDouble() < 0.3 ? '#' : '='; // 30% de chance d'être un mur
                }
            }
        }
        
        // Placer les points de départ et d'arrivée
        int startRow = random.nextInt(rows - 2) + 1;
        int endRow = random.nextInt(rows - 2) + 1;
        
        grid[startRow][1] = 'S';
        grid[endRow][cols - 2] = 'E';
        
        start = new Position(startRow, 1);
        end = new Position(endRow, cols - 2);
        
        // S'assurer qu'il existe au moins un chemin possible (implémentation simplifiée)
        ensurePathExists();
    }
    
    // Méthode simple pour assurer qu'il existe un chemin (pour la démo)
    private void ensurePathExists() {
        // Créer un chemin horizontal depuis S
        for (int j = start.getCol() + 1; j < cols - 1; j++) {
            grid[start.getRow()][j] = '=';
        }
        
        // Créer un chemin vertical vers E
        int minRow = Math.min(start.getRow(), end.getRow());
        int maxRow = Math.max(start.getRow(), end.getRow());
        
        for (int i = minRow; i <= maxRow; i++) {
            grid[i][cols - 2] = '=';
        }
    }
    
    // Affichage du labyrinthe
    public void printMaze() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(grid[i][j]);
            }
            System.out.println();
        }
    }
    
    // Affichage du labyrinthe avec le chemin de solution
    public void printSolution(boolean[][] path) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (path[i][j] && grid[i][j] != 'S' && grid[i][j] != 'E') {
                    System.out.print('+');
                } else {
                    System.out.print(grid[i][j]);
                }
            }
            System.out.println();
        }
    }
    
    // Getters et setters
    public char[][] getGrid() {
        return grid;
    }
    
    public int getRows() {
        return rows;
    }
    
    public int getCols() {
        return cols;
    }
    
    public Position getStart() {
        return start;
    }
    
    public Position getEnd() {
        return end;
    }
    
    public boolean isWall(int row, int col) {
        return grid[row][col] == '#';
    }
    
    public boolean isPath(int row, int col) {
        return grid[row][col] == '=' || grid[row][col] == 'S' || grid[row][col] == 'E';
    }
}

// Classe pour représenter une position dans le labyrinthe
class Position {
    private int row;
    private int col;
    
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }
    
    public int getRow() {
        return row;
    }
    
    public int getCol() {
        return col;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return row == position.row && col == position.col;
    }
    
    @Override
    public int hashCode() {
        return 31 * row + col;
    }
}