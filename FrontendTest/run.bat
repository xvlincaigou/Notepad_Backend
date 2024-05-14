@echo off
javac -cp json.jar communicationTest.java
java -cp .;json.jar communicationTest %1