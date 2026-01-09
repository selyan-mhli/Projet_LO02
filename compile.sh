#!/bin/bash
# Script de compilation pour Linux/Mac

echo "Compilation du projet Jest..."

# Creer le dossier classes s il n existe pas
mkdir -p classes

# Compiler tous les fichiers Java
find src -name "*.java" > sources.txt
javac -d classes @sources.txt

if [ $? -eq 0 ]; then
    echo "Compilation reussie !"
    # Copier les ressources (images) dans le classpath
    if [ -d "src/CARD_IMAGES" ]; then
        mkdir -p classes/CARD_IMAGES
        cp -R src/CARD_IMAGES/. classes/CARD_IMAGES/
    fi
    # Copier aussi les cartes d'extension (5-8)
    if [ -d "src/CARD_EXTENSION" ]; then
        mkdir -p classes/CARD_EXTENSION
        cp -R src/CARD_EXTENSION/. classes/CARD_EXTENSION/
    fi
    rm sources.txt
else
    echo "Erreur de compilation."
    rm sources.txt
    exit 1
fi
