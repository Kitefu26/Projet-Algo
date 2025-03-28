## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).
#   P r o j e t - A l g o 
 
 
# Resolution de Labyrinthe - JavaFX

## Description 📌
Ce projet est une application JavaFX permettant de générer et de résoudre des labyrinthes en utilisant les algorithmes DFS (Depth-First Search) et BFS (Breadth-First Search). L'utilisateur peut charger un labyrinthe depuis un fichier ou en générer un aléatoirement.

## Fonctionnalités 🚀
- Chargement d'un labyrinthe à partir d'un fichier texte.
- Génération d'un labyrinthe aléatoire.
- Visualisation du labyrinthe sous forme de grille.
- Résolution du labyrinthe avec DFS et BFS.
- Affichage du chemin trouvé dans la console.

## Prérequis 🛠
Avant d'exécuter le projet, assure-toi d'avoir installé :

- [Java JDK 11+](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
- [JavaFX](https://openjfx.io/)
- [VS Code avec l'extension Java](https://code.visualstudio.com/docs/languages/java)

## Installation 🔧
Clone le dépôt et ouvre-le dans votre IDE :
```sh
    git clone https://github.com/Kitefu26/Projet-Algo.git
    cd ProjetAlgo
```

## Structure du projet 📂
```
labyrinthe-solver/
│── src/               # Code source
│   ├── model/         # Modèle (classes du labyrinthe, position...)
│   ├── view/          # Interface utilisateur JavaFX
│   ├── controller/    # Contrôleur gérant les interactions
│── lib/               # Dépendances du projet
│── bin/               # Fichiers compilés
│── README.md          # Documentation du projet
```

## Exécution ▶️
1. Ouvre le projet dans VS Code.
2. Compile et exécute l'application :
   ```sh
   javac -d bin -sourcepath src src/Main.java
   java -cp bin Main
   ```

## Exemple de fichier Labyrinthe 🏗
```txt
#####
#S  #
# # #
#  E#
#####
```

- `#` : Mur
- `S` : Départ
- `E` : Arrivée
- ` ` : Chemin possible

## Auteur 👤
- Idy ka DABO - [GitHub](https://github.com/Kitefu26)

## Licence 📜
Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus d'informations.
