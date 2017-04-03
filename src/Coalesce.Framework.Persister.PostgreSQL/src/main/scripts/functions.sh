#!/bin/bash
# Globals ----------------------------------------------------------------
declare -x dropExisting=0
declare -x dbUser=""
declare -x dbOwner=""
declare -x psqlShowAll=""
declare -x database=""
declare -x force=0
declare -x dbServerName=""
declare -x schema=""
declare -x dbServerPort=""
declare -x dropRoles=0
declare -x myUser="coalesceservice"
declare -x PSQL=$(which psql 2>/dev/null)

# Displays usage for this script
_help()
{
    echo "NAME"
    echo "       $SCRIPTNAME - create a DSS database"
    echo ""
    echo "SYNOPSIS"
    echo "       $SCRIPTNAME [OPTION]..."
    echo ""
    echo "DESCRIPTION"
    echo "       Creates a database for DSS.  Reads configurations in \$DSS_CONFIG_LOCATION for default database connection parameters."
    echo ""
    echo "OPTIONS"
    echo "       -h hostname"
    echo "              Specifies the host name of the machine on which the server is running."
    echo "       -d dbname"
    echo "              Specifies the name of the database to connect to."
    echo "       -U username"
    echo "              Connect to the database as the user username instead of the default."
    echo "       -s service username"
    echo "              Specifies the username used by connecting applications."
    echo "       -o database owner"
    echo "              Specifies ownership of database contents."
    echo "       -p port"
    echo "              Specifies the TCP port or the local Unix-domain socket file extension on which the server is listening for connections."
    echo "       -n schema"
    echo "              Use this schema."
    echo "       -x psql"
    echo "              Path to psql executable."
    echo "       -a"
    echo "              Print all SQL lines to standard output as they are read."
    echo "       -D"
    echo "              Drop the database before creating it."
    echo "       -R"
    echo "              Drop existing roles before creating them."
    echo "       -f"
    echo "              Never prompt (default: prompt)."
    echo ""

    exit 1
}
declare -xf _help

# Parses the command line parameters
_parseCmdLine() {
    while getopts ":DRU:ad:fh:o:n:p:x:s:" opt; do
        case $opt in
            D) dropExisting=1;;
            R) dropRoles=1;;
            U) dbUser=$OPTARG;;
            s) myUser=$OPTARG;;
            a) psqlShowAll="-a";;
            d) database=$OPTARG;;
            f) force=1;;
            h) dbServerName=$OPTARG;;
            o) dbOwner=$OPTARG;;
            n) schema=$OPTARG;;
            p) dbServerPort=$OPTARG;;
            x) PSQL=$OPTARG;;
            *) echo -e "\n=== ERROR: Unknown parameter \"$opt\"."
               _help;;
        esac
    done
}
declare -xf _parseCmdLine

_runPsql() {
    local COMMAND=$*
    #su - $dbUser -c "$PSQL --command='$COMMAND'"
    $PSQL --command="$COMMAND"
}
declare -xf _runPsql

# Checks that all required settings are set
_checkSettings()
{
    echo "Setttings:"
    echo "- Host:         $dbServerName"
    echo "- Database:     $database"
    echo "- User:         $dbUser"
    echo "- Service User: $myUser"
    echo "- Port:         $dbServerPort"
    echo "- Schema:       $schema"
    echo "- psql:         $PSQL"
    echo "Flags:"
    echo "- dropExisting: $dropExisting"
    echo "- dropRoles:    $dropRoles"
    echo "- psqlShowAll:  $psqlShowAll"
    echo "- force:        $force"

    if [   -z "$dbServerName" -o -z "$database" -o -z "$dbUser" \
        -o -z "$dbServerPort" -o -z "$schema"   -o -z "$PSQL" \
    ]; then
        echo -e "\n===ERROR: Missing parameter."
        _help
    fi

    if [[ "$dbOwner" == "" ]]; then
      echo "dbOwner not set - setting to dbUser"
      dbOwner=${dbUser}
    fi

    echo
    echo "- Owner:        $dbOwner"
}

# Prompt the user to continue
_proceed()
{
    if (( force )); then
        echo "Force flag set.  Not prompting."
    else
        read -t 10 -p " Continue? (y/n) " cont
        if [ "${cont:0:1}" != "y" -a "${cont:0:1}" != "Y" ]; then
            exit 1
        fi
    fi
}

