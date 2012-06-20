# 
# anyremote-j2me-client - J2ME client for anyRemote.
#
# Copyright (C) 2008,2009,2010 Mikhail Fedotov <anyremote@mail.ru>
# 
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.

# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.

# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA. 
# 

EMPTY_AUTOMAKE_TARGETS = dvi pdf ps info html tags ctags dist distdir
.PHONY: $(EMPTY_AUTOMAKE_TARGETS)
$(EMPTY_AUTOMAKE_TARGETS):

#    -allowaccessmodification crashes on Motorola-V500

OBFUSCATE_FLAGS=\
    -libraryjars ${WTK_HOME}/lib/midpapi20.jar:${WTK_HOME}/lib/cldcapi10.jar:${WTK_HOME}/lib/jsr082.jar \
    -microedition -overloadaggressively \
    -keep public class Controller extends javax.microedition.midlet.MIDlet

distdir:
	true

dist:
	true

install: all

all: compile compile-nojsr82 preverify preverify-nojsr82 arc16 arc16b arc32 arc32b arc48 arc48b arc64 arc64b arc96 arc96b arc128 arc128b arcfull arcwm
#all: compile compile-nojsr82 preverify preverify-nojsr82 arc00 arc16 arc16b arc32 arc48 arc48b arc32b arc64 arc64b arc96 arc96b arcfull arcwm

quick: compile preverify arc16

arc00:  jar00  obfuscate00  jad00

arc16:  jar16  obfuscate16  jad16
arc16b: jar16b obfuscate16b jad16b

arc32:  jar32  obfuscate32  jad32
arc32b: jar32b obfuscate32b jad32b

arc48:  jar48  obfuscate48  jad48
arc48b: jar48b obfuscate48b jad48b

arc64:  jar64  obfuscate64  jad64
arc64b: jar64b obfuscate64b jad64b

arc96:  jar96  obfuscate96  jad96
arc96b: jar96b obfuscate96b jad96b

arc128:  jar128  obfuscate128  jad128
arc128b: jar128b obfuscate128b jad128b

arcfull: jarfull obfuscatefull jadfull

arcwm:  jarwm  obfuscatewm  jadwm

compile:
	cp -f BtComm.java.jsr82 BtComm.java
	cp -f Controller.java.jsr82 Controller.java
	test -z tmpclasses || mkdir -p -- . tmpclasses
	javac -bootclasspath ${WTK_HOME}/lib/midpapi20.jar:${WTK_HOME}/lib/cldcapi10.jar \
           -g:none -target 1.1 -source 1.3 -classpath ${WTK_HOME}/lib/jsr082.jar \
	   -d tmpclasses *.java
	rm -f Controller.java BtComm.java

compile-nojsr82:
	rm -f BtComm.java
	cp -f Controller.java.nojsr82 Controller.java
	test -z tmpclasses-nojsr82 || mkdir -p -- . tmpclasses-nojsr82
	javac -bootclasspath ${WTK_HOME}/lib/midpapi20.jar:${WTK_HOME}/lib/cldcapi10.jar \
           -g:none -target 1.1 -source 1.3 \
	   -d tmpclasses-nojsr82 *.java
	rm -f Controller.java BtComm.java

preverify:
	test -z classes || mkdir -p -- . classes
	${WTK_HOME}/bin/preverify  -classpath ${WTK_HOME}/lib/cldcapi10.jar:${WTK_HOME}/lib/midpapi20.jar:${WTK_HOME}/lib/jsr082.jar \
	    -d classes tmpclasses

preverify-nojsr82:
	test -z classes-nojsr82 || mkdir -p -- . classes-nojsr82
	${WTK_HOME}/bin/preverify  -classpath ${WTK_HOME}/lib/cldcapi10.jar:${WTK_HOME}/lib/midpapi20.jar \
	    -d classes-nojsr82 tmpclasses-nojsr82

# seems the following does not helps
#Nokia-MIDlet-Background-Event: pause
#MIDletX-No-Command: true

#
# 16x16 icons used in lists !!!
#
jar00:
	cp MANIFEST.template MANIFEST.MF
	jar cfvm anyRemote-nonobf.jar MANIFEST.MF -C classes . anyRemote.png anyRemote64.png 16/file.png 16/click_icon.png 32/click_icon.png 64/click_icon.png 16/default.png 32/default.png 64/default.png

jar16:
	cp MANIFEST.template MANIFEST.MF
	jar cfvm anyRemote-nonobf16.jar MANIFEST.MF -C classes . anyRemote.png anyRemote64.png 16

jar16b:
	cp MANIFEST64.template MANIFEST.MF
	jar cfvm anyRemote-nonobf16b.jar MANIFEST.MF -C classes . anyRemote64.png 16

jar32:
	cp MANIFEST.template MANIFEST.MF
	jar cfvm anyRemote-nonobf32.jar MANIFEST.MF -C classes . anyRemote.png anyRemote64.png 16 32

jar32b:
	cp MANIFEST64.template MANIFEST.MF
	jar cfvm anyRemote-nonobf32b.jar MANIFEST.MF -C classes . anyRemote64.png 16 32

jar48:
	cp MANIFEST.template MANIFEST.MF
	jar cfvm anyRemote-nonobf48.jar MANIFEST.MF -C classes . anyRemote.png anyRemote64.png 16 48

jar48b:
	cp MANIFEST64.template MANIFEST.MF
	jar cfvm anyRemote-nonobf48b.jar MANIFEST.MF -C classes . anyRemote64.png 16 48

jar64:
	cp MANIFEST.template MANIFEST.MF
	jar cfvm anyRemote-nonobf64.jar MANIFEST.MF -C classes . anyRemote.png anyRemote64.png 16 64

jar64b:
	cp MANIFEST64.template MANIFEST.MF
	jar cfvm anyRemote-nonobf64b.jar MANIFEST.MF -C classes . anyRemote64.png 16 64

jar96:
	cp MANIFEST.template MANIFEST.MF
	jar cfvm anyRemote-nonobf96.jar MANIFEST.MF -C classes . anyRemote.png anyRemote64.png anyRemote256.png 16 96

jar96b:
	cp MANIFEST64.template MANIFEST.MF
	jar cfvm anyRemote-nonobf96b.jar MANIFEST.MF -C classes . anyRemote64.png anyRemote256.png 16 96

jar128:
	cp MANIFEST.template MANIFEST.MF
	jar cfvm anyRemote-nonobf128.jar MANIFEST.MF -C classes . anyRemote.png anyRemote64.png anyRemote256.png 16 128

jar128b:
	cp MANIFEST64.template MANIFEST.MF
	jar cfvm anyRemote-nonobf128b.jar MANIFEST.MF -C classes . anyRemote64.png anyRemote256.png 16 128

jarfull:
	cp MANIFEST.template MANIFEST.MF
	jar cfvm anyRemote-nonobf.jar MANIFEST.MF -C classes . anyRemote.png anyRemote64.png anyRemote256.png 16 32 48 64 96 128

jarwm:
	cp MANIFEST-NOJSR82.template MANIFEST.MF
	jar cfvm anyRemote-nonobfwm.jar MANIFEST.MF -C classes-nojsr82 . anyRemote64.png 16 48 64 96 128

obfuscate00:
	java -jar ${PROGUARD_HOME}/lib/proguard.jar -injars anyRemote-nonobf.jar -outjars anyRemote.jar ${OBFUSCATE_FLAGS}
	rm -f anyRemote-nonobf.jar

obfuscate16:
	java -jar ${PROGUARD_HOME}/lib/proguard.jar -injars anyRemote-nonobf16.jar -outjars anyRemote-16.jar ${OBFUSCATE_FLAGS}
	rm -f anyRemote-nonobf16.jar

obfuscate16b:
	java -jar ${PROGUARD_HOME}/lib/proguard.jar -injars anyRemote-nonobf16b.jar -outjars anyRemote-16b.jar ${OBFUSCATE_FLAGS}
	rm -f anyRemote-nonobf16b.jar

obfuscate32:
	java -jar ${PROGUARD_HOME}/lib/proguard.jar -injars anyRemote-nonobf32.jar -outjars anyRemote-32.jar ${OBFUSCATE_FLAGS}
	rm -f anyRemote-nonobf32.jar

obfuscate32b:
	java -jar ${PROGUARD_HOME}/lib/proguard.jar -injars anyRemote-nonobf32b.jar -outjars anyRemote-32b.jar ${OBFUSCATE_FLAGS}
	rm -f anyRemote-nonobf32b.jar

obfuscate48:
	java -jar ${PROGUARD_HOME}/lib/proguard.jar -injars anyRemote-nonobf48.jar -outjars anyRemote-48.jar ${OBFUSCATE_FLAGS}
	rm -f anyRemote-nonobf48.jar

obfuscate48b:
	java -jar ${PROGUARD_HOME}/lib/proguard.jar -injars anyRemote-nonobf48b.jar -outjars anyRemote-48b.jar ${OBFUSCATE_FLAGS}
	rm -f anyRemote-nonobf48b.jar

obfuscate64:
	java -jar ${PROGUARD_HOME}/lib/proguard.jar -injars anyRemote-nonobf64.jar -outjars anyRemote-64.jar ${OBFUSCATE_FLAGS}
	rm -f anyRemote-nonobf64.jar

obfuscate64b:
	java -jar ${PROGUARD_HOME}/lib/proguard.jar -injars anyRemote-nonobf64b.jar -outjars anyRemote-64b.jar ${OBFUSCATE_FLAGS}
	rm -f anyRemote-nonobf64b.jar

obfuscate96:
	java -jar ${PROGUARD_HOME}/lib/proguard.jar -injars anyRemote-nonobf96.jar -outjars anyRemote-96.jar ${OBFUSCATE_FLAGS}
	rm -f anyRemote-nonobf96.jar

obfuscate96b:
	java -jar ${PROGUARD_HOME}/lib/proguard.jar -injars anyRemote-nonobf96b.jar -outjars anyRemote-96b.jar ${OBFUSCATE_FLAGS}
	rm -f anyRemote-nonobf96b.jar

obfuscate128:
	java -jar ${PROGUARD_HOME}/lib/proguard.jar -injars anyRemote-nonobf128.jar -outjars anyRemote-128.jar ${OBFUSCATE_FLAGS}
	rm -f anyRemote-nonobf128.jar

obfuscate128b:
	java -jar ${PROGUARD_HOME}/lib/proguard.jar -injars anyRemote-nonobf128b.jar -outjars anyRemote-128b.jar ${OBFUSCATE_FLAGS}
	rm -f anyRemote-nonobf128b.jar

obfuscatefull:
	java -jar ${PROGUARD_HOME}/lib/proguard.jar -injars anyRemote-nonobf.jar -outjars anyRemote.jar ${OBFUSCATE_FLAGS}
	rm -f anyRemote-nonobf.jar

obfuscatewm:
	java -jar ${PROGUARD_HOME}/lib/proguard.jar -injars anyRemote-nonobfwm.jar -outjars anyRemote-nojsr82.jar ${OBFUSCATE_FLAGS}
	rm -f anyRemote-nonobfwm.jar

jad:
	echo "MIDlet-1: anyRemote, anyRemote.png, Controller" > anyRemote${SUFFIX}.jad
	echo "MIDlet-Jar-Size: "`ls -l anyRemote${SUFFIX}.jar | tr -s ' ' | cut -f 5 -d ' '` >> anyRemote${SUFFIX}.jad
	echo "MIDlet-Jar-URL: anyRemote${SUFFIX}.jar" >> anyRemote${SUFFIX}.jad
	echo "MIDlet-Name: anyRemote" >> anyRemote${SUFFIX}.jad
	echo "MIDlet-Permissions-Opt: javax.microedition.io.Connector.bluetooth.client,javax.microedition.io.Connector.socket,javax.microedition.io.Connector.comm" >> anyRemote${SUFFIX}.jad
	echo "MIDlet-Vendor: Mikhail Fedotov" >> anyRemote${SUFFIX}.jad
	echo "MIDlet-Version: ${PACKAGE_VERSION}" >> anyRemote${SUFFIX}.jad
	echo "MicroEdition-Configuration: CLDC-1.0" >> anyRemote${SUFFIX}.jad
	echo "MicroEdition-Profile: MIDP-2.0" >> anyRemote${SUFFIX}.jad
	cat anyRemote${SUFFIX}.jad|sed 's/MIDlet-Jar-URL: anyRemote/MIDlet-Jar-URL: http:\/\/anyremote.sourceforge.net\/anyRemote/' > anyRemote${SUFFIX}.jad.www

jadwm:
	echo "MIDlet-1: anyRemote, anyRemote.png, Controller" > anyRemote-nojsr82.jad
	echo "MIDlet-Jar-Size: "`ls -l anyRemote-nojsr82.jar | tr -s ' ' | cut -f 5 -d ' '` >> anyRemote-nojsr82.jad
	echo "MIDlet-Jar-URL: anyRemote-nojsr82.jar" >> anyRemote-nojsr82.jad
	echo "MIDlet-Name: anyRemote" >> anyRemote-nojsr82.jad
	echo "MIDlet-Permissions-Opt: javax.microedition.io.Connector.socket,javax.microedition.io.Connector.comm" >> anyRemote-nojsr82.jad
	echo "MIDlet-Vendor: Mikhail Fedotov" >> anyRemote-nojsr82.jad
	echo "MIDlet-Version: ${PACKAGE_VERSION}" >> anyRemote-nojsr82.jad
	echo "MicroEdition-Configuration: CLDC-1.0" >> anyRemote-nojsr82.jad
	echo "MicroEdition-Profile: MIDP-2.0" >> anyRemote-nojsr82.jad
	cat anyRemote-nojsr82.jad|sed 's/MIDlet-Jar-URL: anyRemote/MIDlet-Jar-URL: http:\/\/anyremote.sourceforge.net\/anyRemote/' > anyRemote-nojsr82.jad.www

jad00:
	make jad SUFFIX="-00"

jad16:
	make jad SUFFIX="-16"
	
jad16b:
	make jad SUFFIX="-16b"

jad32:
	make jad SUFFIX="-32"

jad32b:
	make jad SUFFIX="-32b"

jad48:
	make jad SUFFIX="-48"

jad48b:
	make jad SUFFIX="-48b"

jad64:
	make jad SUFFIX="-64"

jad64b:
	make jad SUFFIX="-64b"

jad96:
	make jad SUFFIX="-96"

jad96b:
	make jad SUFFIX="-96b"

jad128:
	make jad SUFFIX="-128"

jad128b:
	make jad SUFFIX="-128b"

jadfull:
	make jad SUFFIX=""

distclean: clean	

clean:	
	rm -rf *class *jar *jad *jad.www MANIFEST.MF tmpclasses classes tmpclasses-nojsr82 classes-nojsr82
