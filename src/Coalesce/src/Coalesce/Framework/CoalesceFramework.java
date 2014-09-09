package Coalesce.Framework;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.joda.time.DateTime;
import org.xml.sax.SAXException;

import Coalesce.Common.Exceptions.CoalescePersistorException;
import Coalesce.Common.Runtime.CoalesceSettings;
import Coalesce.Framework.DataModel.CoalesceEntitySyncShell;
import Coalesce.Framework.DataModel.CoalesceEntityTemplate;
import Coalesce.Framework.DataModel.XsdEntity;
import Coalesce.Framework.DataModel.XsdField;
import Coalesce.Framework.DataModel.XsdRecord;
import Coalesce.Framework.Persistance.ICoalescePersistor;
import Coalesce.Framework.Persistance.ICoalescePersistor.ElementMetaData;
import Coalesce.Framework.Persistance.ICoalescePersistor.EntityMetaData;

/*-----------------------------------------------------------------------------'
 Copyright 2014 - InCadence Strategic Solutions Inc., All Rights Reserved

 Notwithstanding any contractor copyright notice, the Government has Unlimited
 Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 of this work other than as specifically authorized by these DFARS Clauses may
 violate Government rights in this work.

 DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 Unlimited Rights. The Government has the right to use, modify, reproduce,
 perform, display, release or disclose this computer software and to have or
 authorize others to do so.

 Distribution Statement D. Distribution authorized to the Department of
 Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 -----------------------------------------------------------------------------*/

public class CoalesceFramework {

    /*--------------------------------------------------------------------------
    	Private Member Variables
    --------------------------------------------------------------------------*/

    private ICoalescePersistor _Persister;
    private boolean _IsInitialized = false;
    private String _imageFormat;

    /*--------------------------------------------------------------------------
    	Public Functions
    --------------------------------------------------------------------------*/

    public boolean Initialize(ICoalescePersistor persister)
    {

        this._Persister = persister;
        this._IsInitialized = true;
        this._imageFormat = CoalesceSettings.GetImageFormat();

        return true;
    }

    public boolean IsInitialized()
    {
        return this._IsInitialized;
    }

    /*--------------------------------------------------------------------------
    	Get Entity
    --------------------------------------------------------------------------*/

    public XsdEntity GetCoalesceEntity(String Key) throws CoalescePersistorException
    {
        return this._Persister.getEntity(Key);
    }

    public XsdEntity GetEntity(String EntityId, String EntityIdType) throws CoalescePersistorException
    {
        return this._Persister.getEntity(EntityId, EntityIdType);
    }

    public XsdEntity GetEntity(String Name, String EntityId, String EntityIdType) throws CoalescePersistorException
    {
        return this._Persister.getEntity(Name, EntityId, EntityIdType);
    }

    public String GetEntityXml(String Key) throws CoalescePersistorException
    {
        return this._Persister.getEntityXml(Key);
    }

    public String GetEntityXml(String EntityId, String EntityIdType) throws CoalescePersistorException
    {
        return this._Persister.getEntityXml(EntityId, EntityIdType);
    }

    public String GetEntityXml(String Name, String EntityId, String EntityIdType) throws CoalescePersistorException
    {
        return this._Persister.getEntityXml(Name, EntityId, EntityIdType);
    }

    /*--------------------------------------------------------------------------
    	EntityID Functions
    --------------------------------------------------------------------------*/

    public String GetCoalesceEntityKeyForEntityId(String EntityId, String EntityIdType, String EntityName)
            throws CoalescePersistorException
    {
        return this.GetCoalesceEntityKeyForEntityId(EntityId, EntityIdType, EntityName, null);
    }

    public String GetCoalesceEntityKeyForEntityId(String EntityId,
                                                  String EntityIdType,
                                                  String EntityName,
                                                  String EntitySource) throws CoalescePersistorException
    {

        String EntityKey = null;

        List<String> list = this.GetCoalesceEntityKeysForEntityId(EntityId, EntityIdType, EntityName, EntitySource);

        if (!list.isEmpty())
        {

            EntityKey = list.get(0);

        }

        return EntityKey;
    }

    public List<String> GetCoalesceEntityKeysForEntityId(String EntityId, String EntityIdType, String EntityName)
            throws CoalescePersistorException
    {
        return this.GetCoalesceEntityKeysForEntityId(EntityId, EntityIdType, EntityName, null);
    }

    public List<String> GetCoalesceEntityKeysForEntityId(String EntityId,
                                                         String EntityIdType,
                                                         String EntityName,
                                                         String EntitySource) throws CoalescePersistorException
    {

        List<String> list = new ArrayList<String>();

        String[] EntityIdList = EntityId.split(",");
        String[] EntityIdTypeList = EntityIdType.split(",");

        if (EntityIdList.length == EntityIdTypeList.length)
        {

            for (int i = 0; i < EntityIdTypeList.length; i++)
            {

                list.addAll(this._Persister.getCoalesceEntityKeysForEntityId(EntityIdList[i],
                                                                             EntityIdTypeList[i],
                                                                             EntityName,
                                                                             EntitySource));

            }

        }

        return list;

    }

    public EntityMetaData GetCoalesceEntityIdAndTypeForKey(String Key) throws CoalescePersistorException
    {
        return this._Persister.getCoalesceEntityIdAndTypeForKey(Key);
    }

    /*--------------------------------------------------------------------------
    	Other Entity Functions
    --------------------------------------------------------------------------*/

    public DateTime GetCoalesceEntityLastModified(String key, String objectType) throws CoalescePersistorException
    {
        return this._Persister.getCoalesceDataObjectLastModified(key, objectType);
    }

    public boolean SaveCoalesceEntity(XsdEntity entity) throws CoalescePersistorException
    {
        return this.SaveCoalesceEntity(entity, false);
    }

    public boolean SaveCoalesceEntity(XsdEntity entity, boolean AllowRemoval) throws CoalescePersistorException
    {
        return this._Persister.setEntity(entity, AllowRemoval);
    }

    public boolean CreateFieldThumbnail(String Filename) throws IOException
    {
        File imageFile = new File(Filename);
        String imageName = imageFile.getName();
        String imageFileFormat = imageName.substring(imageName.length() - 3);
        File imageDir = imageFile.getParentFile();

        // create thumbnail name
        String thumbnailName = imageName.substring(0, imageName.length() - 4) + "_" + "thumbnail." + _imageFormat;
        File thumbnail = new File(imageDir, thumbnailName);

        // create thumbnail
        if (!thumbnail.exists() && imageFileFormat.equals(_imageFormat))
        {
            BufferedImage img = ImageIO.read(imageFile);
            BufferedImage imgThumbnail = Scalr.resize(img, 100, 100, Scalr.OP_ANTIALIAS);
            ImageIO.write(imgThumbnail, _imageFormat, thumbnail);
            return true;
        }
        else
        {
            // thumbnail already exists or image format does not match settings
            return false;
        }
    }

    public boolean CreateFieldThumbnail(XsdField Field) throws IOException
    {
        return this.CreateFieldThumbnail(Field.GetCoalesceFullFilename());
    }

    public String GetCoalesceFieldValue(String FieldKey) throws CoalescePersistorException
    {
        return (String) this._Persister.getFieldValue(FieldKey);
    }

    public XsdRecord GetCoalesceRecord(String Key) throws CoalescePersistorException
    {
        XsdRecord record = null;

        ElementMetaData metaData = this._Persister.getXPath(Key, "record");
        if (metaData != null)
        {
            XsdEntity entity = this._Persister.getEntity(metaData.entityKey);
            if (entity != null)
            {
                record = (XsdRecord) entity.getDataObjectForNamePath(metaData.elementXPath);
            }
        }

        return record;
    }

    public XsdField GetCoalesceFieldByFieldKey(String Key) throws CoalescePersistorException
    {
        XsdField field = null;

        ElementMetaData metaData = this._Persister.getXPath(Key, "field");

        if (metaData != null)
        {
            XsdEntity entity = this._Persister.getEntity(metaData.entityKey);

            if (entity != null)
            {
                field = (XsdField) entity.getCoalesceDataObjectForKey(Key);
            }
        }

        return field;
    }

    /*--------------------------------------------------------------------------
    	Template Functions
    --------------------------------------------------------------------------*/

    public boolean SaveCoalesceEntityTemplate(CoalesceEntityTemplate template) throws CoalescePersistorException
    {
        return this._Persister.persistEntityTemplate(template);
    }

    public CoalesceEntityTemplate GetCoalesceEntityTemplate(String Name, String Source, String Version) throws SAXException,
            IOException, CoalescePersistorException
    {

        CoalesceEntityTemplate template = new CoalesceEntityTemplate();

        // Initialize Template
        template.Initialize(this.GetCoalesceEntityTemplateXml(Name, Source, Version));

        return template;

    }

    public String GetCoalesceEntityTemplateXml(String Key) throws CoalescePersistorException
    {
        return this._Persister.getEntityTemplateXml(Key);
    }

    public String GetCoalesceEntityTemplateXml(String Name, String Source, String Version) throws CoalescePersistorException
    {
        return this._Persister.getEntityTemplateXml(Name, Source, Version);
    }

    public String GetCoalesceEntityTemplateKey(String Name, String Source, String Version) throws CoalescePersistorException
    {
        return this._Persister.getEntityTemplateKey(Name, Source, Version);
    }

    public String GetCoalesceEntityTemplateMetadata() throws CoalescePersistorException
    {
        return this._Persister.getEntityTemplateMetadata();
    }

    public XsdEntity CreateEntityFromTemplate(String Name, String Source, String Version) throws CoalescePersistorException
    {

        String Xml = this.GetCoalesceEntityTemplateXml(Name, Source, Version);

        XsdEntity entity = new XsdEntity();
        entity.initialize(Xml);

        return entity;

    }

    /*--------------------------------------------------------------------------
    	Sync Shell Functions
    --------------------------------------------------------------------------*/

    public CoalesceEntitySyncShell GetCoalesceEntitySyncShell(String Key) throws CoalescePersistorException, SAXException,
            IOException
    {
        return CoalesceEntitySyncShell.Create(this.GetCoalesceEntity(Key));
    }

}
