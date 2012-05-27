%define name anyremote-J2ME-client
%define version 4.23

Summary: J2ME client for anyremote
Name: anyremote-J2ME-client
Name: %{name}
Version: %{version}
Release: 1.suse11
License: GPLv2+
Group: Applications/System
Requires:anyremote >= 4.18 
BuildRoot: %{_tmppath}/%{name}-%{version}-build
Source0: http://downloads.sourceforge.net/anyremote/%{name}-%{version}.tar.gz
URL: http://anyremote.sourceforge.net/
BuildArch: noarch

%description
J2ME client for anyremote. Jar file should be uploaded to the cell phone.

%prep
%setup -q

%build
./configure --prefix=/usr --with-wtk=/usr/lib/jvm/WTK2.5.2 --with-proguard=/usr/lib/jvm/proguard4.2

%install
make install DESTDIR=$RPM_BUILD_ROOT

%clean
[ "$RPM_BUILD_ROOT" != "/" ] && [ -d $RPM_BUILD_ROOT ] \
 && rm -rf $RPM_BUILD_ROOT

%files
%defattr(-,root,root)
%{_datadir}/%{name}
