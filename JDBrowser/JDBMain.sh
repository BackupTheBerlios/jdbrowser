#!/bin/bash
JAVA_GNOME=`java-config -p \`java-config -l | grep -i libgnome-java | awk '{print $1}' | awk '{gsub(/\[/,""); print}' | awk '{gsub(/\]/,""); print}'\``
JAVA_GTK=`java-config -p \`java-config -l | grep -i libgtk-java | awk '{print $1}' | awk '{gsub(/\[/,""); print}' | awk '{gsub(/\]/,""); print}'\``
JAVA_GLADE=`java-config -p \`java-config -l | grep -i libglade-java | awk '{print $1}' | awk '{gsub(/\[/,""); print}' | awk '{gsub(/\]/,""); print}'\``
JAVA_GLIB=`java-config -p \`java-config -l | grep -i glib-java | awk '{print $1}' | awk '{gsub(/\[/,""); print}' | awk '{gsub(/\]/,""); print}'\``

echo $JAVA_GNOME
cd ~/workspace.newstable/JDBrowser
java -Djava.library.path=/usr/lib -classpath /usr/share/jdbc-mysql/lib/jdbc-mysql.jar:$JAVA_GTK:$JAVA_GNOME:.:$JAVA_GLADE:$JAVA_GLIB:/home/sorenm/workspace.newstable/eclub2-srv/tools/ojdbc14.jar:lib/xstream-1.1.2.jar main.JDBMain
cd -

