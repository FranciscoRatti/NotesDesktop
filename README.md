# Notes Desktop

Un cliente de escritorio para **Notes Hub**, mi aplicacion web para gestionar notas. Esta pensada
para que hostees tu centro de notas y luego accedas desde cualquier dispositivo usando un cliente.

![vista-previa.png]

### Indices

- [Caracteristicas](#caracteristicas)

## Caracteristicas

Esta desarrollado en java usando **curl** para ejecutar las peticiones http y la libreria
**javax.swing** para la interfaz grafica. <br>
Permite ejecutar operaciones de consulta, modificacion, insercion y borrado de notas. Permite
asignar notas como favoritas para que se habran al inicio de cada arranque.

## Dependencias

### curl

##### Para ejecutar peticiones http al servidor.

Debian=`sudo apt install curl` <br>
Fedora=`sudo dnf install curl` <br>
Arch=`sudo pacman -S curl` <br>
OpenSUSE=`sudo zypper install curl` <br>

### NotesHub

##### El servidor que hostea las notas.

Para instalar el servidor de notas puedes seguir su [guia de instalacion](https://github.com/FranciscoRatti/NotesHub#instalacion)

## Instalacion

Para instalarlo puedes descargar el archivo **NotesDesktop.zip** y descomprimirlo.

```
curl -L -O https://github.com/FranciscoRatti/NotesDesktop/releases/download/v0.0.1-BETA/NotesDesktop.zip
unzip NotesDesktop.zip -d NotesDesktop ; rm NotesDesktop.zip
```

Dentro del directorio **NotesDesktop/shell** se encuentra un script llamado **install.sh**.

```
./NotesDesktop/shell/install.sh
```

Por ultimo puedes eliminar los archivos descomprimidos:

```
rm -rf NotesDesktop
```

Si necesitas DESINSTALARLO puedes ejecutar lo siguiente para eliminar los archivos instalados:

```
sudo rm -rf ~/.config/notesdesktop /usr/share/notesdesktop /usr/lib/notesdesktop /usr/share/applications/notesdesktop.desktop
```
