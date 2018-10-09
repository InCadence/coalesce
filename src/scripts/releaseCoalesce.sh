#!/bin/bash
name="Coalesce"
tagname="coalesce"
parentpom="../../pom.xml"

preVersion=$(cat ${parentpom} | grep -E -m 1 -o "<version>(.*)</version>" | sed -e 's,.*<version>\([^<]*\)</version>.*,\1,g')
version=${preVersion}

echo "Current version: ${preVersion}"

case $version in
    *-SNAPSHOT) isSnapshot=true;;
    * ) isSnapshot=false;;
esac

while true; do
    read -p "Update Versions (yes / no / exit)? " doVersions
    case $doVersions in
        y | yes)
		git fetch

                read -p "New Version: " version

                case $version in
                    *-SNAPSHOT) isSnapshot=true;;
                    * ) isSnapshot=false;;
                esac

		echo "Maven Setting version to ${version}..."

                case $version in
	            *-SNAPSHOT) isSnapshot=true;;
	            * ) isSnapshot=false;;
                esac

		echo "isSnapshot set to: ${isSnapshot}"

		mvn versions:set -DnewVersion=${version} -f ${parentpom}

		#set tags
		if [ "${isSnapshot}" = false ] ; then

		    sed -i "s|\(<changelog.end.tag>\)[^<>]*\(</changelog.end.tag>\)|\1"${tagname}-${version}"\2|" ${parentpom}

		else

		    sed -i "s|\(<changelog.end.tag>\)[^<>]*\(</changelog.end.tag>\)|\1"HEAD"\2|" ${parentpom}
		    sed -i "s|\(<changelog.start.tag>\)[^<>]*\(</changelog.start.tag>\)|\1"${tagname}-${preVersion}"\2|" ${parentpom}

		fi

		echo "Maven Commiting version..."

		mvn versions:commit -DnewVersion=${version} -f ${parentpom}

		git status

		if [ "${isSnapshot}" = false ] ; then

		    echo "Grepping for snapshot versions"

		    grep -ir "${preVersion}" ../ --include="pom.xml"
		    grep -ir "SNAPSHOT" ../ --include="pom.xml"

		    echo "Done"

		fi
		break;;
	n | no) break;;
	exit) exit 0;;
        * ) echo "Please enter yes, no or exit";;
    esac
done

function release {

    path=$1

    echo "Deploying ${name}..."
    mvn clean deploy -DskipTests -P bundles,react,dist -f ${path}

    while true; do
	read -p "Did you run out of memory? " doSuck
	case $doSuck in
	    y | yes) rerun=true; break;;
	    n | no) rerun=false; break;;
	    * ) echo "Please enter yes or no";;
	esac
    done

    if [ "${rerun}" = true ] ; then

	read -p "Enter artifact Id to resume from: " artifactid

	echo "Resuming deploy..."
	mvn clean deploy -DskipTests -P bundles,react,dist -f ${path} -rf :${artifactid}

    fi
}

while true; do
    read -p "Deploy (${tagname} / done)? " doDeploy
    case $doDeploy in
        ${tagname}) release ${parentpom};;
	exit)  exit 0;;
 	done) break;;
	* ) echo "Invalid Selction";;
    esac
done

while true; do
    read -p "Commit (yes / no / exit)? " doCommit
    case $doCommit in
        y | yes)
	    git add ../../ -u
        git commit -m "${name} version ${version}"

	    if [ "${isSnapshot}" = false ] ; then

		git tag ${tagname}-${version}

	    fi

            break;;

	n | no) break;;
        exit)  exit 0;;
        * ) echo "Please enter yes, no or exit";;
    esac
done

while true; do
    read -p "Push to master (yes / no / exit)? " doPush
    case $doPush in
        y | yes)
	    echo "pushing code to master"
	    git push origin HEAD:refs/heads/release


	    if [ "${isSnapshot}" = false ] ; then
		echo 'pushing tag to master'
		git push origin tag ${tagname}-${version}

	    fi

	    break;;

	n | no) break;;
        exit)  exit 0;;
	* ) echo "Please enter yes, no or exit";;
    esac
done


