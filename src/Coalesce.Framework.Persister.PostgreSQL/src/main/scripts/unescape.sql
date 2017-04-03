CREATE OR REPLACE FUNCTION :myschema.unescape(input varchar)
RETURNS varchar AS
$BODY$
DECLARE
BEGIN
    IF length(input) >= 2 AND left(input,1) = '"' AND right(input,1) = '"' THEN
        RETURN regexp_replace(substring(input FROM 2 FOR length(input) - 2),'""','"','g');
    END IF;

    RETURN input;
END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE
COST 100;

COMMENT ON FUNCTION :myschema.unescape(varchar) IS 'Equivalent to StringEscapeUtils.unescapeCsv()';

ALTER FUNCTION :myschema.unescape(varchar) OWNER TO :myowner;
