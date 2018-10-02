mkdir -p target/coverage-reports
mkdir -p target/classes/com

find -type f -name 'jacoco-ut-*.exec' -exec cp -at target/coverage-reports/. {} +
find -type d -path '**/classes/com/incadencecorp' -exec cp -at target/classes/com/. {} +
mvn verify -N -o
