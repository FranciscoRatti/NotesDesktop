sudo -v
cd NotesDesktop

# Config
mkdir ~/.config/notesdesktop/
cp resources/config.properties ~/.config/notesdesktop/

# Estaticos
sudo mkdir /usr/share/notesdesktop/
sudo cp resources/*.ttf /usr/share/notesdesktop/
sudo cp resources/icon.png /usr/share/notesdesktop/
sudo chmod a+r /usr/share/notesdesktop/icon.png

# Binario
sudo mkdir /usr/lib/notesdesktop/
sudo cp NotesDesktop.jar /usr/lib/notesdesktop/

# Librerias
sudo cp -r lib/ /usr/lib/notesdesktop/

# Entrada
sudo cp resources/notesdesktop.desktop /usr/share/applications/
sudo update-desktop-database
