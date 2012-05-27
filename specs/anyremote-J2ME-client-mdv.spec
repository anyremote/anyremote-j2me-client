%define name anyremote-J2ME-client
%define version 4.23
%define release %mkrel 1

Summary: J2ME client for anyremote
Name: %{name}
Version: %{version}
Release: %{release}
License: GPLv2+
Group: Applications/System
Requires: anyremote >= 4.18 
BuildRoot:  %{_tmppath}/%{name}-%{version}-%{release}-buildroot
Source0: http://downloads.sourceforge.net/anyremote/%{name}-%{version}.tar.gz
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
%makeinstall INSTALLDIR=$RPM_BUILD_ROOT

%clean
rm -rf $RPM_BUILD_ROOT

%files
%defattr(-,root,root,-)
%{_datadir}/%{name}

