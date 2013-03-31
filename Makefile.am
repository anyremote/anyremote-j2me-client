# 
# anyremote-j2me-client - J2ME client for anyRemote.
#
# Copyright (C) 2008-2013 Mikhail Fedotov <anyremote@mail.ru>
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

SUBDIRS = src

dist_pkgdata_DATA = ChangeLog README COPYING

EXTRA_DIST = INSTALL AUTHORS src/Makefile* src/*java \
             src/BtComm.java.jsr82 src/Controller.java.jsr82 src/Controller.java.nojsr82 \
             src/*png src/16 src/32 src/48 src/64 src/96 src/128\
	     src/*.jar src/*.jad \
	     src/MANIFEST.template src/MANIFEST64.template src/MANIFEST-NOJSR82.template

install-data-local:
	test -z $(DESTDIR)$(pkgdatadir) || mkdir -p -- . $(DESTDIR)$(pkgdatadir)
	cp -r src/*jad src/*jar $(DESTDIR)$(pkgdatadir)
	find $(DESTDIR)$(pkgdatadir) -type f -exec chmod 644 {} \;
	find $(DESTDIR)$(pkgdatadir) -type d -exec chmod 755 {} \;

uninstall-local:
	rm -rf $(DESTDIR)$(pkgdatadir)

quick:
	cd src;make quick

# Icon themes support
themes-list:
	ls -1 src/icon-themes

dist-themes:
	tar cvf anyremote-j2me-client-icon-themes-$(PACKAGE_VERSION).tar src/icon-themes genIconSources.sh genTheme.sh genClientJar resize-and-save-as-png.scm README.icon_themes
	gzip anyremote-j2me-client-icon-themes-$(PACKAGE_VERSION).tar

dist-jar:
	mkdir src/tmp
	cp src/*jar src/tmp
	cp src/anyRemote-16.jad.www   src/tmp/anyRemote-16.jad
	cp src/anyRemote-32.jad.www   src/tmp/anyRemote-32.jad
	cp src/anyRemote-48.jad.www   src/tmp/anyRemote-48.jad
	cp src/anyRemote-64.jad.www   src/tmp/anyRemote-64.jad
	cp src/anyRemote-96.jad.www   src/tmp/anyRemote-96.jad
	cp src/anyRemote-128.jad.www  src/tmp/anyRemote-128.jad
	cp src/anyRemote-16b.jad.www  src/tmp/anyRemote-16b.jad
	cp src/anyRemote-32b.jad.www  src/tmp/anyRemote-32b.jad
	cp src/anyRemote-48b.jad.www  src/tmp/anyRemote-48b.jad
	cp src/anyRemote-64b.jad.www  src/tmp/anyRemote-64b.jad
	cp src/anyRemote-96b.jad.www  src/tmp/anyRemote-96b.jad
	cp src/anyRemote-128b.jad.www src/tmp/anyRemote-128b.jad
	cp src/anyRemote.jad.www src/tmp/anyRemote.jad
	cp src/anyRemote-nojsr82.jad.www src/tmp/anyRemote-nojsr82.jad
	cd src/tmp;tar cvf ../../anyremote-j2me-client-bin-$(PACKAGE_VERSION).tar *
	rm -rf src/tmp
	gzip anyremote-j2me-client-bin-$(PACKAGE_VERSION).tar

themes-all: bluecurve crystalproject crystalsvg gartoon gion gnome gperfection2 human ice kids nuvola1 nuvola2 oxygen-blue oxygen-green rodent tangerine tango

gen_jar:
	./genClientJar -t ${THEME} -j src/anyRemote-16b.jar
	./genClientJar -t ${THEME} -j src/anyRemote-16.jar
	./genClientJar -t ${THEME} -j src/anyRemote-32b.jar
	./genClientJar -t ${THEME} -j src/anyRemote-32.jar
	./genClientJar -t ${THEME} -j src/anyRemote-64b.jar
	./genClientJar -t ${THEME} -j src/anyRemote-64.jar
	./genClientJar -t ${THEME} -j src/anyRemote-96b.jar
	./genClientJar -t ${THEME} -j src/anyRemote-96.jar
	./genClientJar -t ${THEME} -j src/anyRemote-128b.jar
	./genClientJar -t ${THEME} -j src/anyRemote-128.jar
	./genClientJar -t ${THEME} -j src/anyRemote-nojsr82.jar
       
bluecurve:
	make gen_jar THEME="bluecurve"

crystalproject:
	make gen_jar THEME="crystalproject"

crystalsvg:
	make gen_jar THEME="crystalsvg"

gartoon:
	make gen_jar THEME="gartoon"

gion:
	make gen_jar THEME="gion"

gnome:
	make gen_jar THEME="gnome"

gperfection2:
	make gen_jar THEME="gperfection2"

human:
	make gen_jar THEME="human"

ice:
	make gen_jar THEME="ice"

kids:
	make gen_jar THEME="kids"

nuvola1:
	make gen_jar THEME="nuvola1"

nuvola2:
	make gen_jar THEME="nuvola2"

oxygen-blue:
	make gen_jar THEME="oxygen-blue"

oxygen-green:
	make gen_jar THEME="oxygen-green"

rodent:
	make gen_jar THEME="rodent"

tangerine:
	make gen_jar THEME="tangerine"

tango:
	make gen_jar THEME="tango"

themes-gen:
	./genTheme.sh bluecurve
	./genTheme.sh crystalproject
	./genTheme.sh crystalsvg
	./genTheme.sh gartoon
	./genTheme.sh gion
	./genTheme.sh gnome
	./genTheme.sh gperfection2
	./genTheme.sh human
	./genTheme.sh ice
	./genTheme.sh kids
	./genTheme.sh nuvola1
	./genTheme.sh nuvola2
	./genTheme.sh oxygen-blue
	./genTheme.sh oxygen-green
	./genTheme.sh rodent
	./genTheme.sh tangerine
	./genTheme.sh tango

