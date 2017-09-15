-- Depercated

-- Note: After running the create script to create the database, you must first connect to
-- the CoalesceDatabase before running this script.

-- Table: CoalesceEntity

-- DROP TABLE CoalesceEntity;

CREATE TABLE "coalesce".CoalesceEntity
(
  ObjectKey uuid NOT NULL,
  Name text,
  Source text,
  Version text,
  EntityId text,
  EntityIdType text,
  EntityXml text,
  DateCreated timestamp with time zone,
  LastModified timestamp with time zone,
  CONSTRAINT CoalesceEntity_pkey PRIMARY KEY (ObjectKey)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "coalesce".CoalesceEntity
  OWNER TO postgres;
GRANT ALL ON TABLE "coalesce".CoalesceEntity TO postgres;
GRANT ALL ON TABLE "coalesce".CoalesceEntity TO public;

-- Table: CoalesceEntityTemplate

-- DROP TABLE CoalesceEntityTemplate;

CREATE TABLE "coalesce".CoalesceEntityTemplate
(
  TemplateKey uuid NOT NULL,
  Name text,
  Source text,
  Version text,
  TemplateXml text,
  DateCreated timestamp with time zone,
  LastModified timestamp with time zone,
  CONSTRAINT CoalesceEntityTemplate_pkey PRIMARY KEY (TemplateKey)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "coalesce".CoalesceEntityTemplate
  OWNER TO postgres;
GRANT ALL ON TABLE "coalesce".CoalesceEntityTemplate TO postgres;
GRANT ALL ON TABLE "coalesce".CoalesceEntityTemplate TO public;

-- Table: CoalesceField

-- DROP TABLE CoalesceField;

CREATE TABLE "coalesce".CoalesceField
(
  ObjectKey uuid NOT NULL,
  Name text,
  Value text,
  DataType text,
  InputLanguage text,
  ClassificationMarking text,
  ModifiedBy text,
  ParentKey uuid,
  ParentType text,
  DateCreated timestamp with time zone,
  LastModified timestamp with time zone,
  PreviousHistoryKey uuid,
  CONSTRAINT CoalesceField_pkey PRIMARY KEY (ObjectKey)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "coalesce".CoalesceField
  OWNER TO postgres;
GRANT ALL ON TABLE "coalesce".CoalesceField TO postgres;
GRANT ALL ON TABLE "coalesce".CoalesceField TO public;

-- Table: CoalesceFieldBinaryData

-- DROP TABLE CoalesceFieldBinaryData;

CREATE TABLE "coalesce".CoalesceFieldBinaryData
(
  ObjectKey uuid NOT NULL,
  BinaryObject bytea,
  Filename text,
  MimeType text,
  Extension text,
  Length bigint,
  DateCreated timestamp with time zone,
  LastModified timestamp with time zone,
  CONSTRAINT CoalesceFieldBinaryData_pkey PRIMARY KEY (ObjectKey)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "coalesce".CoalesceFieldBinaryData
  OWNER TO postgres;
GRANT ALL ON TABLE "coalesce".CoalesceFieldBinaryData TO postgres;
GRANT ALL ON TABLE "coalesce".CoalesceFieldBinaryData TO public;

-- Table: CoalesceFieldDefinition

-- DROP TABLE CoalesceFieldDefinition;

CREATE TABLE "coalesce".CoalesceFieldDefinition
(
  ObjectKey uuid NOT NULL,
  Name text,
  ParentKey uuid,
  ParentType text,
  DateCreated timestamp with time zone,
  LastModified timestamp with time zone,
  CONSTRAINT CoalesceFieldDefinition_pkey PRIMARY KEY (ObjectKey)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "coalesce".CoalesceFieldDefinition
  OWNER TO postgres;
GRANT ALL ON TABLE "coalesce".CoalesceFieldDefinition TO postgres;
GRANT ALL ON TABLE "coalesce".CoalesceFieldDefinition TO public;

-- Table: CoalesceFieldHistory

-- DROP TABLE CoalesceFieldHistory;

CREATE TABLE "coalesce".CoalesceFieldHistory
(
  ObjectKey uuid NOT NULL,
  Name text,
  Value text,
  DataType text,
  InputLanguage text,
  ClassificationMarking text,
  ModifiedBy text,
  ParentKey uuid,
  ParentType text,
  DateCreated timestamp with time zone,
  LastModified timestamp with time zone,
  PreviousHistoryKey uuid,
  CONSTRAINT CoalesceFieldHistory_pkey PRIMARY KEY (ObjectKey)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "coalesce".CoalesceFieldHistory
  OWNER TO postgres;
GRANT ALL ON TABLE "coalesce".CoalesceFieldHistory TO postgres;
GRANT ALL ON TABLE "coalesce".CoalesceFieldHistory TO public;

-- Table: CoalesceLinkage

-- DROP TABLE CoalesceLinkage;

CREATE TABLE "coalesce".CoalesceLinkage
(
  ObjectKey uuid NOT NULL,
  Name text,
  Entity1Key uuid,
  Entity1Name text,
  Entity1Source text,
  Entity1Version text,
  LinkType text,
  LinkStatus text,
  Entity2Key uuid,
  Entity2Name text,
  Entity2Source text,
  Entity2Version text,
  ClassificationMarking text,
  ModifiedBy text,
  InputLanguage text,
  ParentKey uuid,
  ParentType text,
  DateCreated timestamp with time zone,
  LastModified timestamp with time zone,
  CONSTRAINT CoalesceLinkage_pkey PRIMARY KEY (ObjectKey)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "coalesce".CoalesceLinkage
  OWNER TO postgres;
GRANT ALL ON TABLE "coalesce".CoalesceLinkage TO postgres;
GRANT ALL ON TABLE "coalesce".CoalesceLinkage TO public;

-- Table: CoalesceLinkageSection

-- DROP TABLE CoalesceLinkageSection;

CREATE TABLE "coalesce".CoalesceLinkageSection
(
  ObjectKey uuid NOT NULL,
  Name text,
  ParentKey uuid,
  ParentType text,
  DateCreated timestamp with time zone,
  LastModified timestamp with time zone,
  CONSTRAINT CoalesceLinkageSection_pkey PRIMARY KEY (ObjectKey)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "coalesce".CoalesceLinkageSection
  OWNER TO postgres;
GRANT ALL ON TABLE "coalesce".CoalesceLinkageSection TO postgres;
GRANT ALL ON TABLE "coalesce".CoalesceLinkageSection TO public;

-- Table: CoalesceObjectMap

-- DROP TABLE CoalesceObjectMap;

CREATE TABLE "coalesce".CoalesceObjectMap
(
  ParentObjectKey uuid NOT NULL,
  ParentObjectType text,
  ObjectKey uuid NOT NULL,
  ObjectType text,
  CONSTRAINT CoalesceObjectMap_pkey PRIMARY KEY (ParentObjectKey, ObjectKey)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "coalesce".CoalesceObjectMap
  OWNER TO postgres;
GRANT ALL ON TABLE "coalesce".CoalesceObjectMap TO postgres;
GRANT ALL ON TABLE "coalesce".CoalesceObjectMap TO public;

-- Table: CoalesceRecord

-- DROP TABLE CoalesceRecord;

CREATE TABLE "coalesce".CoalesceRecord
(
  ObjectKey uuid NOT NULL,
  Name text,
  ParentKey uuid,
  ParentType text,
  DateCreated timestamp with time zone,
  LastModified timestamp with time zone,
  CONSTRAINT CoalesceRecord_pkey PRIMARY KEY (ObjectKey)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "coalesce".CoalesceRecord
  OWNER TO postgres;
GRANT ALL ON TABLE "coalesce".CoalesceRecord TO postgres;
GRANT ALL ON TABLE "coalesce".CoalesceRecord TO public;

-- Table: CoalesceRecordSet

-- DROP TABLE CoalesceRecordSet;

CREATE TABLE "coalesce".CoalesceRecordSet
(
  ObjectKey uuid NOT NULL,
  Name text,
  ParentKey uuid,
  ParentType text,
  DateCreated timestamp with time zone,
  LastModified timestamp with time zone,
  CONSTRAINT CoalesceRecordSet_pkey PRIMARY KEY (ObjectKey)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "coalesce".CoalesceRecordSet
  OWNER TO postgres;
GRANT ALL ON TABLE "coalesce".CoalesceRecordSet TO postgres;
GRANT ALL ON TABLE "coalesce".CoalesceRecordSet TO public;

-- Table: CoalesceSection

-- DROP TABLE CoalesceSection;

CREATE TABLE "coalesce".CoalesceSection
(
  ObjectKey uuid NOT NULL,
  Name text,
  ParentKey uuid,
  ParentType text,
  DateCreated timestamp with time zone,
  LastModified timestamp with time zone,
  CONSTRAINT CoalesceSection_pkey PRIMARY KEY (ObjectKey)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "coalesce".CoalesceSection
  OWNER TO postgres;
GRANT ALL ON TABLE "coalesce".CoalesceSection TO postgres;
GRANT ALL ON TABLE "coalesce".CoalesceSection TO public;

-- Function: coalesceentity_insertorupdate(uuid, text, text, text, text, text, text, timestamp with time zone, timestamp with time zone)

DROP FUNCTION coalesceentity_insertorupdate(uuid, text, text, text, text, text, text, timestamp with time zone, timestamp with time zone);

CREATE OR REPLACE FUNCTION "coalesce".coalesceentity_insertorupdate(ivarobjectkey uuid, ivarname text, ivarsource text, ivarversion text, ivarentityid text, ivarentityidtype text, ivarentityxml text, ivardatecreated timestamp with time zone, ivarlastmodified timestamp with time zone)
  RETURNS void AS
$BODY$BEGIN
	UPDATE "coalesce".CoalesceEntity
	SET
		Name = ivarname,
		Source = ivarsource,
		Version = ivarversion,
		EntityId = ivarentityid,
		EntityIdType = ivarentityidtype,
		EntityXml = ivarentityxml,
		DateCreated = ivardatecreated,
		LastModified = ivarlastmodified
	WHERE
		ObjectKey = ivarobjectkey;
IF NOT FOUND THEN
	INSERT INTO "coalesce".CoalesceEntity 
		(ObjectKey, 
		 Name, 
		 Source, 
		 Version, 
		 EntityId, 
		 EntityIdType, 
		 EntityXml, 
		 DateCreated, 
		 LastModified)
	VALUES
		(ivarobjectkey,
		 ivarname,
		 ivarsource,
		 ivarversion,
		 ivarentityid,
		 ivarentityidtype,
		 ivarentityxml,
		 ivardatecreated,
		 ivarlastmodified);
END IF;
RETURN;
END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION "coalesce".coalesceentity_insertorupdate(uuid, text, text, text, text, text, text, timestamp with time zone, timestamp with time zone)
  OWNER TO postgres;

-- Function: coalesceentitytemplate_insertorupdate(uuid, text, text, text, text, timestamp with time zone, timestamp with time zone)

DROP FUNCTION coalesceentitytemplate_insertorupdate(uuid, text, text, text, text, timestamp with time zone, timestamp with time zone);

CREATE OR REPLACE FUNCTION "coalesce".coalesceentitytemplate_insertorupdate(ivartemplatekey uuid, ivarname text, ivarsource text, ivarversion text, ivartemplatexml text, ivardatecreated timestamp with time zone, ivarlastmodified timestamp with time zone)
  RETURNS void AS
$BODY$BEGIN
	UPDATE "coalesce".CoalesceEntityTemplate
	SET
		Name = ivarname,
		Source = ivarsource,
		Version = ivarversion,
		TemplateXml = ivartemplatexml,
		DateCreated = ivardatecreated,
		LastModified = ivarlastmodified
	WHERE
		(Name = ivarname) AND (Source = ivarsource) AND (Version = ivarversion);
IF NOT FOUND THEN
	INSERT INTO "coalesce".CoalesceEntityTemplate 
		(TemplateKey, 
		 Name, 
		 Source, 
		 Version, 
		 TemplateXml, 
		 DateCreated, 
		 LastModified)
	VALUES
		(ivartemplatekey,
		 ivarname,
		 ivarsource,
		 ivarversion,
		 ivartemplatexml,
		 ivardatecreated,
		 ivarlastmodified);
END IF;
RETURN;
END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION "coalesce".coalesceentitytemplate_insertorupdate(uuid, text, text, text, text, timestamp with time zone, timestamp with time zone)
  OWNER TO postgres;

-- Function: coalescefield_insertorupdate(uuid, text, text, text, text, text, text, uuid, text, timestamp with time zone, timestamp with time zone, uuid)

DROP FUNCTION coalescefield_insertorupdate(uuid, text, text, text, text, text, text, uuid, text, timestamp with time zone, timestamp with time zone, uuid);

CREATE OR REPLACE FUNCTION "coalesce".coalescefield_insertorupdate(ivarobjectkey uuid, ivarname text, ivarvalue text, ivardatatype text, ivarinputlanguage text, ivarclassificationmarking text, ivarmodifiedby text, ivarparentkey uuid, ivarparenttype text, ivardatecreated timestamp with time zone, ivarlastmodified timestamp with time zone, ivarprevioushistorykey uuid)
  RETURNS void AS
$BODY$BEGIN
	UPDATE "coalesce".CoalesceField
	SET
		Name = ivarname,
		Value = ivarvalue,
		DataType = ivardatatype,
		InputLanguage = ivarinputlanguage,
		ClassificationMarking = ivarclassificationmarking,
		ModifiedBy = ivarmodifiedby,
		ParentKey = ivarparentkey,
		ParentType = ivarparenttype,
		DateCreated = ivardatecreated,
		LastModified = ivarlastmodified,
		PreviousHistoryKey = ivarprevioushistorykey
	WHERE
		ObjectKey = ivarobjectkey;
IF NOT FOUND THEN
	INSERT INTO "coalesce".CoalesceField 
		(ObjectKey, 
		 Name, 
		 Value, 
		 DataType, 
		 InputLanguage, 
		 ClassificationMarking, 
		 ModifiedBy, 
		 ParentKey,
		 ParentType,
		 DateCreated, 
		 LastModified,
		 PreviousHistoryKey)
	VALUES
		(ivarobjectkey,
		 ivarname,
		 ivarvalue,
		 ivardatatype,
		 ivarinputlanguage,
		 ivarclassificationmarking,
		 ivarmodifiedby,
		 ivarparentkey,
		 ivarparenttype,
		 ivardatecreated,
		 ivarlastmodified,
		 ivarprevioushistorykey);
END IF;
RETURN;
END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION "coalesce".coalescefield_insertorupdate(uuid, text, text, text, text, text, text, uuid, text, timestamp with time zone, timestamp with time zone, uuid)
  OWNER TO postgres;

-- Function: coalescefieldbinarydata_insertorupdate(uuid, bytea, text, text, text, bigint, timestamp with time zone, timestamp with time zone)

DROP FUNCTION coalescefieldbinarydata_insertorupdate(uuid, bytea, text, text, text, bigint, timestamp with time zone, timestamp with time zone);

CREATE OR REPLACE FUNCTION "coalesce".coalescefieldbinarydata_insertorupdate(ivarobjectkey uuid, ivarbinaryobject bytea, ivarfilename text, ivarmimetype text, ivarextension text, ivarlength bigint, ivardatecreated timestamp with time zone, ivarlastmodified timestamp with time zone)
  RETURNS void AS
$BODY$BEGIN
	UPDATE "coalesce".CoalesceFieldBinaryData
	SET
		BinaryObject = ivarbinaryobject,
		Filename = ivarfilename,
		MimeType = ivarmimetype,
		Extension = ivarextension,
		Length = ivarlength,
		DateCreated = ivardatecreated,
		LastModified = ivarlastmodified
	WHERE
		ObjectKey = ivarobjectkey;
IF NOT FOUND THEN
	INSERT INTO "coalesce".CoalesceFieldBinaryData 
		(ObjectKey, 
		 BinaryObject, 
		 Filename, 
		 MimeType, 
		 Extension, 
		 Length, 
		 DateCreated, 
		 LastModified)
	VALUES
		(ivarobjectkey,
		 ivarbinaryobject,
		 ivarfilename,
		 ivarmimetype,
		 ivarextension,
		 ivarlength,
		 ivardatecreated,
		 ivarlastmodified);
END IF;
RETURN;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION "coalesce".coalescefieldbinarydata_insertorupdate(uuid, bytea, text, text, text, bigint, timestamp with time zone, timestamp with time zone)
  OWNER TO postgres;

-- Function: coalescefielddefinition_insertorupdate(uuid, text, uuid, text, timestamp with time zone, timestamp with time zone)

DROP FUNCTION coalescefielddefinition_insertorupdate(uuid, text, uuid, text, timestamp with time zone, timestamp with time zone);

CREATE OR REPLACE FUNCTION "coalesce".coalescefielddefinition_insertorupdate(ivarobjectkey uuid, ivarname text, ivarparentkey uuid, ivarparenttype text, ivardatecreated timestamp with time zone, ivarlastmodified timestamp with time zone)
  RETURNS void AS
$BODY$BEGIN
	UPDATE "coalesce".CoalesceFieldDefinition
	SET
		Name = ivarname,
		ParentKey = ivarparentkey,
		ParentType = ivarparenttype,
		DateCreated = ivardatecreated,
		LastModified = ivarlastmodified
	WHERE
		ObjectKey = ivarobjectkey;
IF NOT FOUND THEN
	INSERT INTO "coalesce".CoalesceFieldDefinition 
		(ObjectKey, 
		 Name, 
		 ParentKey, 
		 ParentType, 
		 DateCreated, 
		 LastModified)
	VALUES
		(ivarobjectkey,
		 ivarname,
		 ivarparentkey,
		 ivarparenttype,
		 ivardatecreated,
		 ivarlastmodified);
END IF;
RETURN;
END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION "coalesce".coalescefielddefinition_insertorupdate(uuid, text, uuid, text, timestamp with time zone, timestamp with time zone)
  OWNER TO postgres;

-- Function: coalescefieldhistory_insertorupdate(uuid, text, text, text, text, text, text, uuid, text, timestamp with time zone, timestamp with time zone, uuid)

DROP FUNCTION coalescefieldhistory_insertorupdate(uuid, text, text, text, text, text, text, uuid, text, timestamp with time zone, timestamp with time zone, uuid);

CREATE OR REPLACE FUNCTION "coalesce".coalescefieldhistory_insertorupdate(ivarobjectkey uuid, ivarname text, ivarvalue text, ivardatatype text, ivarinputlanguage text, ivarclassificationmarking text, ivarmodifiedby text, ivarparentkey uuid, ivarparenttype text, ivardatecreated timestamp with time zone, ivarlastmodified timestamp with time zone, ivarprevioushistorykey uuid)
  RETURNS void AS
$BODY$BEGIN
	UPDATE "coalesce".CoalesceFieldHistory
	SET
		Name = ivarname,
		Value = ivarvalue,
		DataType = ivardatatype,
		InputLanguage = ivarinputlanguage,
		ClassificationMarking = ivarclassificationmarking,
		ModifiedBy = ivarmodifiedby,
		ParentKey = ivarparentkey,
		ParentType = ivarparenttype,
		DateCreated = ivardatecreated,
		LastModified = ivarlastmodified,
		PreviousHistoryKey = ivarprevioushistorykey
	WHERE
		ObjectKey = ivarobjectkey;
IF NOT FOUND THEN
	INSERT INTO "coalesce".CoalesceFieldHistory 
		(ObjectKey, 
		 Name,
		 Value,
		 DataType,
		 InputLanguage,
		 ClassificationMarking,
		 ModifiedBy, 
		 ParentKey, 
		 ParentType, 
		 DateCreated, 
		 LastModified,
		 PreviousHistoryKey)
	VALUES
		(ivarobjectkey,
		 ivarname,
		 ivarvalue,
		 ivardatatype,
		 ivarinputlanguage,
		 ivarclassificationmarking,
		 ivarmodifiedby,		 
		 ivarparentkey,
		 ivarparenttype,
		 ivardatecreated,
		 ivarlastmodified,
		 ivarprevioushistorykey);
END IF;
RETURN;
END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION "coalesce".coalescefieldhistory_insertorupdate(uuid, text, text, text, text, text, text, uuid, text, timestamp with time zone, timestamp with time zone, uuid)
  OWNER TO postgres;

-- Function: coalescelinkage_insertorupdate(uuid, text, uuid, text, text, text, text, text, uuid, text, text, text, text, text, text, uuid, text, timestamp with time zone, timestamp with time zone)

DROP FUNCTION coalescelinkage_insertorupdate(uuid, text, uuid, text, text, text, text, text, uuid, text, text, text, text, text, text, uuid, text, timestamp with time zone, timestamp with time zone);

CREATE OR REPLACE FUNCTION "coalesce".coalescelinkage_insertorupdate(ivarobjectkey uuid, ivarname text, ivarentity1key uuid, ivarentity1name text, ivarentity1source text, ivarentity1version text, ivarlinktype text, ivarlinkstatus text, ivarentity2key uuid, ivarentity2name text, ivarentity2source text, ivarentity2version text, ivarclassificationmarking text, ivarmodifiedby text, ivarinputlanguage text, ivarparentkey uuid, ivarparenttype text, ivardatecreated timestamp with time zone, ivarlastmodified timestamp with time zone)
  RETURNS void AS
$BODY$BEGIN
	UPDATE "coalesce".CoalesceLinkage
	SET
		Name = ivarname,
		Entity1Key = ivarentity1key,
		Entity1Name = ivarentity1name,
		Entity1Source = ivarentity1source,
		Entity1Version = ivarentity1version,
		LinkType = ivarlinktype,
		LinkStatus = ivarlinkstatus,
		Entity2Key = ivarentity2key,
		Entity2Name = ivarentity2name,
		Entity2Source = ivarentity2source,
		Entity2Version = ivarentity2version,
		ClassificationMarking = ivarclassificationmarking,
		ModifiedBy = ivarmodifiedby,
		InputLanguage = ivarinputlanguage,
		ParentKey = ivarparentkey,
		ParentType = ivarparenttype,
		DateCreated = ivardatecreated,
		LastModified = ivarlastmodified
	WHERE
		ObjectKey = ivarobjectkey;
IF NOT FOUND THEN
	INSERT INTO "coalesce".CoalesceLinkage 
		(ObjectKey, 
		 Name, 
		 Entity1Key,
		 Entity1Name,
		 Entity1Source,
		 Entity1Version,
		 LinkType,
		 LinkStatus,
		 Entity2Key,
		 Entity2Name,
		 Entity2Source,
		 Entity2Version,
		 ClassificationMarking,
		 ModifiedBy,
		 InputLanguage,
		 ParentKey, 
		 ParentType, 
		 DateCreated, 
		 LastModified)
	VALUES
		(ivarobjectkey,
		 ivarname,
		 ivarentity1key,
		 ivarentity1name,
		 ivarentity1source,
		 ivarentity1version,
		 ivarlinktype,
		 ivarlinkstatus,
		 ivarentity2key,
		 ivarentity2name,
		 ivarentity2source,
		 ivarentity2version,
		 ivarclassificationmarking,
		 ivarmodifiedby,
		 ivarinputlanguage,
		 ivarparentkey,
		 ivarparenttype,
		 ivardatecreated,
		 ivarlastmodified);
END IF;
RETURN;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION "coalesce".coalescelinkage_insertorupdate(uuid, text, uuid, text, text, text, text, text, uuid, text, text, text, text, text, text, uuid, text, timestamp with time zone, timestamp with time zone)
  OWNER TO postgres;

-- Function: coalescelinkagesection_insertorupdate(uuid, text, uuid, text, timestamp with time zone, timestamp with time zone)

DROP FUNCTION coalescelinkagesection_insertorupdate(uuid, text, uuid, text, timestamp with time zone, timestamp with time zone);

CREATE OR REPLACE FUNCTION "coalesce".coalescelinkagesection_insertorupdate(ivarobjectkey uuid, ivarname text, ivarparentkey uuid, ivarparenttype text, ivardatecreated timestamp with time zone, ivarlastmodified timestamp with time zone)
  RETURNS void AS
$BODY$BEGIN
	UPDATE "coalesce".CoalesceLinkageSection
	SET
		Name = ivarname,
		ParentKey = ivarparentkey,
		ParentType = ivarparenttype,
		DateCreated = ivardatecreated,
		LastModified = ivarlastmodified
	WHERE
		ObjectKey = ivarobjectkey;
IF NOT FOUND THEN
	INSERT INTO "coalesce".CoalesceLinkageSection 
		(ObjectKey, 
		 Name, 
		 ParentKey, 
		 ParentType, 
		 DateCreated, 
		 LastModified)
	VALUES
		(ivarobjectkey,
		 ivarname,
		 ivarparentkey,
		 ivarparenttype,
		 ivardatecreated,
		 ivarlastmodified);
END IF;
RETURN;
END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION "coalesce".coalescelinkagesection_insertorupdate(uuid, text, uuid, text, timestamp with time zone, timestamp with time zone)
  OWNER TO postgres;

-- Function: coalesceobjectmap_insert(uuid, text, uuid, text)

DROP FUNCTION coalesceobjectmap_insert(uuid, text, uuid, text);

CREATE OR REPLACE FUNCTION "coalesce".coalesceobjectmap_insert(ivarparentobjectkey uuid, ivarparentobjecttype text, ivarobjectkey uuid, ivarobjecttype text)
  RETURNS void AS
$BODY$BEGIN

PERFORM ObjectKey FROM "coalesce".CoalesceObjectMap WHERE (ParentObjectKey = ivarParentObjectKey) AND (ObjectKey = ivarobjectkey);

IF NOT FOUND THEN
	INSERT INTO "coalesce".CoalesceObjectMap 
		(ParentObjectKey, 
		 ParentObjectType, 
		 ObjectKey, 
		 ObjectType)
	VALUES
		(ivarparentobjectkey,
		 ivarparentobjecttype,
		 ivarobjectkey,
		 ivarobjecttype);
END IF;
RETURN;
END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION "coalesce".coalesceobjectmap_insert(uuid, text, uuid, text)
  OWNER TO postgres;

-- Function: coalescerecord_insertorupdate(uuid, text, uuid, text, timestamp with time zone, timestamp with time zone)

DROP FUNCTION coalescerecord_insertorupdate(uuid, text, uuid, text, timestamp with time zone, timestamp with time zone);

CREATE OR REPLACE FUNCTION "coalesce".coalescerecord_insertorupdate(ivarobjectkey uuid, ivarname text, ivarparentkey uuid, ivarparenttype text, ivardatecreated timestamp with time zone, ivarlastmodified timestamp with time zone)
  RETURNS void AS
$BODY$BEGIN
	UPDATE "coalesce".CoalesceRecord
	SET
		Name = ivarname,
		ParentKey = ivarparentkey,
		ParentType = ivarparenttype,
		DateCreated = ivardatecreated,
		LastModified = ivarlastmodified
	WHERE
		ObjectKey = ivarobjectkey;
IF NOT FOUND THEN
	INSERT INTO "coalesce".CoalesceRecord 
		(ObjectKey, 
		 Name, 
		 ParentKey, 
		 ParentType, 
		 DateCreated, 
		 LastModified)
	VALUES
		(ivarobjectkey,
		 ivarname,
		 ivarparentkey,
		 ivarparenttype,
		 ivardatecreated,
		 ivarlastmodified);
END IF;
RETURN;
END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION "coalesce".coalescerecord_insertorupdate(uuid, text, uuid, text, timestamp with time zone, timestamp with time zone)
  OWNER TO postgres;

-- Function: coalescerecordset_insertorupdate(uuid, text, uuid, text, timestamp with time zone, timestamp with time zone)

DROP FUNCTION coalescerecordset_insertorupdate(uuid, text, uuid, text, timestamp with time zone, timestamp with time zone);

CREATE OR REPLACE FUNCTION "coalesce".coalescerecordset_insertorupdate(ivarobjectkey uuid, ivarname text, ivarparentkey uuid, ivarparenttype text, ivardatecreated timestamp with time zone, ivarlastmodified timestamp with time zone)
  RETURNS void AS
$BODY$BEGIN
	UPDATE "coalesce".CoalesceRecordSet
	SET
		Name = ivarname,
		ParentKey = ivarparentkey,
		ParentType = ivarparenttype,
		DateCreated = ivardatecreated,
		LastModified = ivarlastmodified
	WHERE
		ObjectKey = ivarobjectkey;
IF NOT FOUND THEN
	INSERT INTO "coalesce".CoalesceRecordSet 
		(ObjectKey, 
		 Name, 
		 ParentKey, 
		 ParentType, 
		 DateCreated, 
		 LastModified)
	VALUES
		(ivarobjectkey,
		 ivarname,
		 ivarparentkey,
		 ivarparenttype,
		 ivardatecreated,
		 ivarlastmodified);
END IF;
RETURN;
END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION "coalesce".coalescerecordset_insertorupdate(uuid, text, uuid, text, timestamp with time zone, timestamp with time zone)
  OWNER TO postgres;

-- Function: coalescesection_insertorupdate(uuid, text, uuid, text, timestamp with time zone, timestamp with time zone)

DROP FUNCTION coalescesection_insertorupdate(uuid, text, uuid, text, timestamp with time zone, timestamp with time zone);

CREATE OR REPLACE FUNCTION "coalesce".coalescesection_insertorupdate(ivarobjectkey uuid, ivarname text, ivarparentkey uuid, ivarparenttype text, ivardatecreated timestamp with time zone, ivarlastmodified timestamp with time zone)
  RETURNS void AS
$BODY$BEGIN
	UPDATE "coalesce".CoalesceSection
	SET
		Name = ivarname,
		ParentKey = ivarparentkey,
		ParentType = ivarparenttype,
		DateCreated = ivardatecreated,
		LastModified = ivarlastmodified
	WHERE
		ObjectKey = ivarobjectkey;
IF NOT FOUND THEN
	INSERT INTO "coalesce".CoalesceSection 
		(ObjectKey, 
		 Name, 
		 ParentKey, 
		 ParentType, 
		 DateCreated, 
		 LastModified)
	VALUES
		(ivarobjectkey,
		 ivarname,
		 ivarparentkey,
		 ivarparenttype,
		 ivardatecreated,
		 ivarlastmodified);
END IF;
RETURN;
END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION "coalesce".coalescesection_insertorupdate(uuid, text, uuid, text, timestamp with time zone, timestamp with time zone)
  OWNER TO postgres;
