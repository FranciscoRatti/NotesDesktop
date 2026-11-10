clear
rm -rf out
javac -cp "lib/*" -d out src/**/*.java
java -cp "out:lib/*" main.NotesDesktop
