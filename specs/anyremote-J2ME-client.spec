Summary: J2ME client for anyremote
Name: anyremote-J2ME-client
Version: 4.23
Release: 1
License: GPLv2+
Group: Applications/System
Source0: http://downloads.sourceforge.net/anyremote/%{name}-%{version}.tar.gz
Requires: anyremote >= 4.18 
BuildRoot: %{_tmppath}/%{name}-%{version}-%{release}-root-%(%{__id_u} -n)
URL: http://anyremote.sourceforge.net/
BuildArch: noarch

%description
J2ME client for anyremote. Jar file should be uploaded to the cell phone.

%prep
%setup -q

%build
./configure --prefix=/usr --with-wtk=/usr/lib/jvm/WTK2.5.2 --with-proguard=/usr/lib/jvm/proguard4.2

%install
rm -rf $RPM_BUILD_ROOT
make install DESTDIR=$RPM_BUILD_ROOT

%clean
rm -rf $RPM_BUILD_ROOT

%files
%defattr(-,root,root,-)
%{_datadir}/%{name}

%changelog

* Wed Dec 21 18 2010 Mikhail Fedotov <anyremote at mail.ru> - 4.23
- Command Set(popup,...) was added, command Set(text,wrap,...) was removed

* Wed Aug 18 2010 Mikhail Fedotov <anyremote at mail.ru> - 4.22
- Try to upload an icon only once

* Wed Apr 21 2010 Mikhail Fedotov <anyremote at mail.ru> - 4.21
- Fixed bug which prevents to handle all buttons on qwerty devices.
  Tested on Nokia-E71.

* Fri Mar 12 2010 Mikhail Fedotov <anyremote at mail.ru> - 4.20.1
- Small correction.

* Thu Mar 04 2010 Mikhail Fedotov <anyremote at mail.ru> - 4.20
- Enhance support of touchscreen devices (thanks to Alex Klepikov for the 
  patch)

* Thu Feb 11 2010 Mikhail Fedotov <anyremote at mail.ru> - 4.19
- 128x128 icon set was added. Several little fixes.

* Tue Jun 30 2009 Mikhail Fedotov <anyremote at mail.ru> - 4.18
- Enhance support of touchscreen devices (thanks to Jordi Gimenez).

* Fri Jun 19 2009 Mikhail Fedotov <anyremote at mail.ru> - 4.17
- Enhance 48x48 icons handling. Add auto-connect feature.

* Fri May 29 2009 Mikhail Fedotov <anyremote at mail.ru> - 4.16
- Fix issues 48x48 icons handling

* Tue May 05 2009 Mikhail Fedotov <anyremote at mail.ru> - 4.15
- Fix issues with password handling

* Mon Mar 02 2009 Mikhail Fedotov <anyremote at mail.ru> - 4.14
- Support for different icon themes.
  Commands Get(password) and Get(ping) were added.

* Fri Feb 13 2009 Mikhail Fedotov <anyremote at mail.ru> - 4.13
- Optimized image caching. Support auto-image upload feature.
  Tested on E28-E2831.

* Sun Jan 04 2009 Mikhail Fedotov <anyremote at mail.ru> - 4.12
- Support for WinMobile devices (tested on HTC-TyTn and Asus-P535) 

* Fri Oct 24 2008 Mikhail Fedotov <anyremote at mail.ru> - 4.11
- Fixed java crash bug on Nokia-6111. Java client was tested on 
  Samsung SGH-L870

* Mon Sep 29 2008 Mikhail Fedotov <anyremote at mail.ru> - 4.10
- New icon set. Fixed java crash bug on Nokia-6288.

* Thu Sep 11 2008 Mikhail Fedotov <anyremote at mail.ru> - 4.9
- New splash icon. Tested on Samsung-SGH-G600, several issues were fixed.

* Wed Aug 20 2008 Mikhail Fedotov <anyremote at mail.ru> - 4.8
- Fixed recently introduced bug with noncorrect handling of pushing joystick 
  on Nokia's

* Mon Jul 07 2008 Mikhail Fedotov <anyremote at mail.ru> - 4.7
- Tested on Motorola V500 and Nokia 5500 Sport. Better support 
  of non-JSR82-compatible devices and devices without supporting of alpha 
  blending.

* Wed May 14 2008 Mikhail Fedotov <anyremote at mail.ru> - 4.6.1
- Bugfixes for 4.6

* Mon May 06 2008 Mikhail Fedotov <anyremote at mail.ru> - 4.6
- 64x64 icon set was added. Ship different version of JAR with different icon sets.

* Mon Apr 21 2008 Mikhail Fedotov <anyremote at mail.ru> - 4.5
- Corrected to work properly on Motorola-KRZR-K1.
