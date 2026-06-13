#!/bin/bash

# Définition des variables
APP_NAME="MiniFramework" #nom_projet
SRC_DIR="src/main/java"
WEB_DIR="src/main/webapp"
BUILD_DIR="build"
LIB_DIR="lib"
SERVLET_API_JAR="$LIB_DIR/servlet-api.jar"
RESSOURCE_DIR="src/main/ressource"

# Nettoyage et création du répertoire temporaire
rm -rf $BUILD_DIR
mkdir -p $BUILD_DIR/classes
mkdir -p $BUILD_DIR/lib

# Compilation des fichiers Java avec le JAR des Servlets
find $SRC_DIR -name "*.java" > sources.txt
javac -cp ".:lib/*" -d $BUILD_DIR/classes @sources.txt
rm sources.txt

# Copier les ressources (sql, etc.) dans classes
if [ -d "$RESSOURCE_DIR/sql" ]; then
    cp -r $RESSOURCE_DIR/sql $BUILD_DIR/classes/
fi
cp -r $LIB_DIR/*.jar $BUILD_DIR/lib/

# Générer le fichier JAR avec les classes compilées (racine du JAR)
cd $BUILD_DIR || exit
jar -cvf $APP_NAME.jar -C classes . -C lib .
cd ..

echo ""

echo "Framework cree avec success avec son .jar"

echo ""
