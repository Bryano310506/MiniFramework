#!/bin/bash

# Définition des variables
APP_NAME="MiniFramework" #nom_projet
SRC_DIR="src/main/java"
WEB_DIR="src/main/webapp"
BUILD_DIR="build"
LIB_DIR="lib"
TOMCAT_WEBAPPS="/home/itu/Documents/Docs/tomcat-10/webapps"
MYSQL="$LIB_DIR/mysql-connector-j-9.5.0.jar"
SERVLET_API_JAR="$LIB_DIR/servlet-api.jar"
RESSOURCE_DIR="src/main/ressource"

# Nettoyage et création du répertoire temporaire
rm -rf $BUILD_DIR
mkdir -p $BUILD_DIR/bootstrap
mkdir -p $BUILD_DIR/assets/css
mkdir -p $BUILD_DIR/WEB-INF/classes
mkdir -p $BUILD_DIR/WEB-INF/lib

# Compilation des fichiers Java avec le JAR des Servlets
find $SRC_DIR -name "*.java" > sources.txt
javac -cp ".:lib/*" -d $BUILD_DIR/WEB-INF/classes @sources.txt
rm sources.txt

# Copier les ressources (sql, etc.) dans WEB-INF/classes
cp -r $RESSOURCE_DIR/sql $BUILD_DIR/WEB-INF/classes/

# Copier les fichiers web (web.xml, JSP, etc.)
cp -r $WEB_DIR/*.jsp $BUILD_DIR/
cp -r $WEB_DIR/*.js $BUILD_DIR/
cp -r $WEB_DIR/views/* $BUILD_DIR/
cp -r $WEB_DIR/bootstrap/* $BUILD_DIR/bootstrap/
cp -r $WEB_DIR/assets/css/* $BUILD_DIR/assets/css/
cp -r $WEB_DIR/*.xml $BUILD_DIR/WEB-INF/

# Copier les fichiers (.jar) dans WEB-INF/lib
cp -r $MYSQL $BUILD_DIR/WEB-INF/lib/
cp -r $SERVLET_API_JAR $BUILD_DIR/WEB-INF/lib/
cp -r $LIB_DIR/gson-2.10.1.jar $BUILD_DIR/WEB-INF/lib/

# Générer le fichier .war dans le dossier build
cd $BUILD_DIR || exit
jar -cvf $APP_NAME.war *
cd ..

# Déploiement dans Tomcat
cp -f $BUILD_DIR/$APP_NAME.war $TOMCAT_WEBAPPS/

echo ""

echo "Déploiement terminé. Redémarrez Tomcat si nécessaire."

echo ""
