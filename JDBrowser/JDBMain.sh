#!/bin/bash
cd ~/workspace.stable/JDBrowser
java -Djava.library.path=/usr/lib -classpath /usr/share/java-gnome/lib/gtk2.6.jar:/usr/share/java-gnome/lib/gnome2.10.jar:.:/usr/share/java-gnome/lib/glade2.10.jar:/home/sorenm/workspace.stable/eclub2-srv/tools/ojdbc14.jar JDBMain
cd -