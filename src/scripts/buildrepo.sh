#!/bin/sh -x
curdir=`pwd`
user=dboyd
mvndir=${curdir}/mvnrepo
#sedcmd=`/bin/echo -n "sed -e 's#%MVNDIR%#"${mvndir}#"' <"${curdir}"/mvnsettings.xml >"${mvndir}"/settings.xml"`
echo ${sedcmd}
rm -rf coalescesrc
mkdir coalescesrc
rm -rf mvnrepo
mkdir ${mvndir}
sed -e "s#%MVNDIR%#${mvndir}#" <${curdir}/mvnsettings.xml >${mvndir}/settings.xml
cd coalescesrc
git clone http://${user}@10.15.0.62:7990/scm/coal/06-jcoalesce.git
cd 06-jcoalesce/src/Coalesce.Bom
mvn --global-settings ${mvndir}/settings.xml clean install -DskipTests -P bundles
cd ${curdir}
tar cvzf mvnrepo.tgz ./mvnrepo
