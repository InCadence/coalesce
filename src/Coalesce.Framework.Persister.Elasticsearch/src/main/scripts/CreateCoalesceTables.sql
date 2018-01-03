-- Table: CoalesceEntity

CREATE TABLE :myschema.CoalesceEntity
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
  title text,
  deleted boolean DEFAULT false,
  scope text,
  creator text,
  type text,
  CONSTRAINT CoalesceEntity_pkey PRIMARY KEY (ObjectKey)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE :myschema.CoalesceEntity
  OWNER TO :myowner;
GRANT ALL ON TABLE :myschema.CoalesceEntity TO :myowner;
GRANT ALL ON TABLE :myschema.CoalesceEntity TO :myuser;

-- Table: CoalesceEntityTemplate

CREATE TABLE :myschema.CoalesceEntityTemplate
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
ALTER TABLE :myschema.CoalesceEntityTemplate
  OWNER TO :myowner;
GRANT ALL ON TABLE :myschema.CoalesceEntityTemplate TO :myowner;
GRANT ALL ON TABLE :myschema.CoalesceEntityTemplate TO :myuser;

-- Table: CoalesceLinkage

CREATE TABLE :myschema.CoalesceLinkage
(
  ObjectKey uuid NOT NULL,
  Name text,
  Entity1Key uuid,
  Entity1Name text,
  Entity1Source text,
  Entity1Version text,
  LinkType text,
  LinkLabel text,
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
  CONSTRAINT CoalesceLinkage_pkey PRIMARY KEY (ObjectKey),
  FOREIGN KEY (Entity1Key) REFERENCES coalesce.coalesceentity (objectkey) ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE :myschema.CoalesceLinkage
  OWNER TO :myowner;
GRANT ALL ON TABLE :myschema.CoalesceLinkage TO :myowner;
GRANT ALL ON TABLE :myschema.CoalesceLinkage TO :myuser;

-- Function: coalesceentity_insertorupdate

CREATE OR REPLACE FUNCTION :myschema.coalesceentity_insertorupdate(ivarobjectkey uuid, ivarname text, ivarsource text, ivarversion text, ivarentityid text, ivarentityidtype text, ivarentityxml text, ivardatecreated timestamp with time zone, ivarlastmodified timestamp with time zone, ivartitle text, ivardeleted boolean, ivarscope text, ivarcreator text, ivartype text)
  RETURNS void AS
$BODY$BEGIN
	UPDATE CoalesceEntity
	SET
		Name = ivarname,
		Source = ivarsource,
		Version = ivarversion,
		EntityId = ivarentityid,
		EntityIdType = ivarentityidtype,
		EntityXml = ivarentityxml,
		DateCreated = ivardatecreated,
		LastModified = ivarlastmodified,
		title = ivartitle,
		deleted = ivardeleted,
		scope = ivarscope,
		creator = ivarcreator,
		type = ivartype
	WHERE
		ObjectKey = ivarobjectkey;
IF NOT FOUND THEN
	INSERT INTO CoalesceEntity
		(ObjectKey,
		 Name,
		 Source,
		 Version,
		 EntityId,
		 EntityIdType,
		 EntityXml,
		 DateCreated,
		 LastModified,
		 title,
		 deleted,
		 scope,
		 creator,
		 type)
	VALUES
		(ivarobjectkey,
		 ivarname,
		 ivarsource,
		 ivarversion,
		 ivarentityid,
		 ivarentityidtype,
		 ivarentityxml,
		 ivardatecreated,
		 ivarlastmodified,
    	 ivartitle,
		 ivardeleted,
		 ivarscope,
		 ivarcreator,
		 ivartype);
END IF;
RETURN;
END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  SET search_path = :myschema,:myowner,public;
  
ALTER FUNCTION :myschema.coalesceentity_insertorupdate(uuid, text, text, text, text, text, text, timestamp with time zone, timestamp with time zone, text, boolean, text, text, text)
  OWNER TO :myowner;

-- Function: coalesceentitytemplate_insertorupdate

CREATE OR REPLACE FUNCTION :myschema.coalesceentitytemplate_insertorupdate(ivartemplatekey uuid, ivarname text, ivarsource text, ivarversion text, ivartemplatexml text, ivardatecreated timestamp with time zone, ivarlastmodified timestamp with time zone)
  RETURNS void AS
$BODY$BEGIN
	UPDATE CoalesceEntityTemplate
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
	INSERT INTO CoalesceEntityTemplate
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
  COST 100
  SET search_path = :myschema,:myowner,public;
  
ALTER FUNCTION :myschema.coalesceentitytemplate_insertorupdate(uuid, text, text, text, text, timestamp with time zone, timestamp with time zone)
  OWNER TO :myowner;

-- Function: coalescelinkage_insertorupdate

CREATE OR REPLACE FUNCTION :myschema.coalescelinkage_insertorupdate(ivarobjectkey uuid, ivarname text, ivarentity1key uuid, ivarentity1name text, ivarentity1source text, ivarentity1version text, ivarlinktype text, ivarlinklabel text, ivarlinkstatus text, ivarentity2key uuid, ivarentity2name text, ivarentity2source text, ivarentity2version text, ivarclassificationmarking text, ivarmodifiedby text, ivarinputlanguage text, ivarparentkey uuid, ivarparenttype text, ivardatecreated timestamp with time zone, ivarlastmodified timestamp with time zone)
  RETURNS void AS
$BODY$BEGIN
	UPDATE CoalesceLinkage
	SET
		Name = ivarname,
		Entity1Key = ivarentity1key,
		Entity1Name = ivarentity1name,
		Entity1Source = ivarentity1source,
		Entity1Version = ivarentity1version,
		LinkType = ivarlinktype,
        LinkLabel = ivarlinklabel,
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
	INSERT INTO CoalesceLinkage
		(ObjectKey,
		 Name,
		 Entity1Key,
		 Entity1Name,
		 Entity1Source,
		 Entity1Version,
		 LinkType,
         LinkLabel,
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
         ivarlinklabel,
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
  COST 100
  SET search_path = :myschema,:myowner,public;
  
ALTER FUNCTION :myschema.coalescelinkage_insertorupdate(uuid, text, uuid, text, text, text, text, text, text, uuid, text, text, text, text, text, text, uuid, text, timestamp with time zone, timestamp with time zone)
  OWNER TO :myowner;

-- TODO Phase this outo on next DB wipe (Duplicate Stored Procedure)

CREATE OR REPLACE FUNCTION :myschema.coalescelinkage_insertorupdate2(ivarobjectkey uuid, ivarname text, ivarentity1key uuid, ivarentity1name text, ivarentity1source text, ivarentity1version text, ivarlinktype text, ivarlinklabel text, ivarlinkstatus text, ivarentity2key uuid, ivarentity2name text, ivarentity2source text, ivarentity2version text, ivarclassificationmarking text, ivarmodifiedby text, ivarinputlanguage text, ivarparentkey uuid, ivarparenttype text, ivardatecreated timestamp with time zone, ivarlastmodified timestamp with time zone)
  RETURNS void AS
$BODY$BEGIN
	UPDATE CoalesceLinkage
	SET
		Name = ivarname,
		Entity1Key = ivarentity1key,
		Entity1Name = ivarentity1name,
		Entity1Source = ivarentity1source,
		Entity1Version = ivarentity1version,
		LinkType = ivarlinktype,
        LinkLabel = ivarlinklabel,
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
	INSERT INTO CoalesceLinkage
		(ObjectKey,
		 Name,
		 Entity1Key,
		 Entity1Name,
		 Entity1Source,
		 Entity1Version,
		 LinkType,
         LinkLabel,
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
         ivarlinklabel,
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
  COST 100
  SET search_path = :myschema,:myowner,public;
  
ALTER FUNCTION :myschema.coalescelinkage_insertorupdate2(uuid, text, uuid, text, text, text, text, text, text, uuid, text, text, text, text, text, text, uuid, text, timestamp with time zone, timestamp with time zone)
  OWNER TO :myowner; 
