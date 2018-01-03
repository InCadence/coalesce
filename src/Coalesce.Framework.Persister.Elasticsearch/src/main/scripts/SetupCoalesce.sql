CREATE SCHEMA :myschema;
GRANT ALL ON SCHEMA :myschema TO :myowner;

\i CreateCoalesceTables.sql
\i CreateCoalesceIndexes.sql
\i unescape.sql
