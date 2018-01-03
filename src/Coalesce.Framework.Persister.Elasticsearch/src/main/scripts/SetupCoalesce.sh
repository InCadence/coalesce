#!/bin/bash
SCRIPTPATH=$(readlink -f $0)
SCRIPTNAME=$(basename $0)
MYTAG=$(basename $0 .sh)
DIRECTORY=$(dirname $SCRIPTPATH)
declare -x ROLES_CONF="$DIRECTORY/roles.conf"
declare -x ROLES_SQL="$DIRECTORY/createroles.sql"

cd $DIRECTORY

# Source functions
# - global variables are declared in functions
source functions.sh
if [[ -z "$PSQL" ]]; then
    printf -- "[$MYTAG] ERROR: command psql not installed or in PATH\n"
    exit 1
fi

echo "=== Running $SCRIPTNAME $*"

_parseCmdLine $*
_checkSettings
_proceed

PSQL_CMD="$PSQL $psqlShowAll -h $dbServerName -U $dbUser -p $dbServerPort"
PSQL_CMD="$PSQL_CMD --set=mydatabase=$database"
PSQL_CMD="$PSQL_CMD --set=myowner=$dbOwner"
PSQL_CMD="$PSQL_CMD --set=myuser=$myUser"
PSQL_CMD="$PSQL_CMD --set=myschema=$schema"

$PSQL_CMD -c "DROP SCHEMA postgis;"
if [[ "${dbUser}" == "postgres" ]]; then
  $PSQL_CMD -c "DROP ROLE enterprisedb;"
  $PSQL_CMD -c "CREATE ROLE enterprisedb LOGIN SUPERUSER INHERIT CREATEDB CREATEROLE REPLICATION CONNECTION LIMIT 10;"
  $PSQL_CMD -c "ALTER USER enterprisedb WITH PASSWORD 'enterprisedb';"
fi

if (( dropExisting )); then
    printf -- "[$MYTAG] Existing '$database' database will be dropped\n"
    _proceed
    $PSQL_CMD $dbUser -c "DROP DATABASE \"$database\""
fi

# Create Database
$PSQL_CMD -f CreateCoalesceDB.sql

# Create uuid-ossp extension
$PSQL_CMD $database -c "CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\";"

# Add DSS tables and functions
$PSQL_CMD $database -f SetupCoalesce.sql

$PSQL -U $dbUser -d $database -tAc "DROP SCHEMA postgis cascade;"
$PSQL -U $dbUser -d $database -tAc "CREATE SCHEMA postgis AUTHORIZATION enterprisedb;"
$PSQL -U $dbUser -d $database -tAc "CREATE EXTENSION postgis WITH SCHEMA postgis;"
$PSQL_CMD -c "ALTER DATABASE $database SET search_path TO \"\$user\", postgis, public, topology;"

echo "=== End $SCRIPTNAME $*"

touch /usr/local/bin/DSS_SETUP_INSTALLED

exit 0
