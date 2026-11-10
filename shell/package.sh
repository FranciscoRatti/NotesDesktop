javac -cp "lib/*" -d out src/**/*.java
jar -cfm NotesDesktop.jar src/MANIFEST.txt -C out .
zip -r NotesDesktop.zip NotesDesktop.jar README.md resources lib shell/install.sh
