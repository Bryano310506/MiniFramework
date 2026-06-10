#! /bin/bash

# tsy manaraka norme fa tsy maninona

rm -rf out

mkdir out

find src -name "*.java" >> source.txt

javac -cp "lib/*" -d out/ @source.txt

rm source.txt

mkdir out/lib/

cp lib/mysql-connector-j-9.5.0.jar out/lib/

java -cp "out:out/lib/mysql-connector-j-9.5.0.jar" main.Test

echo ""

echo "Compilation terminer avec succes"