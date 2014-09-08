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

import Coalesce.Common.Runtime.CoalesceSettings;
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

    public XsdEntity GetCoalesceEntity(String Key)
    {
        return this._Persister.GetEntity(Key);
    }

    public XsdEntity GetEntity(String EntityId, String EntityIdType)
    {
        return this._Persister.GetEntity(EntityId, EntityIdType);
    }

    public XsdEntity GetEntity(String Name, String EntityId, String EntityIdType)
    {
        return this._Persister.GetEntity(Name, EntityId, EntityIdType);
    }

    public String GetEntityXml(String Key)
    {
        return this._Persister.GetEntityXml(Key);
    }

    public String GetEntityXml(String EntityId, String EntityIdType)
    {
        return this._Persister.GetEntityXml(EntityId, EntityIdType);
    }

    public String GetEntityXml(String Name, String EntityId, String EntityIdType)
    {
        return this._Persister.GetEntityXml(Name, EntityId, EntityIdType);
    }

    /*--------------------------------------------------------------------------
    	EntityID Functions
    --------------------------------------------------------------------------*/

    public String GetCoalesceEntityKeyForEntityId(String EntityId, String EntityIdType, String EntityName)
    {
        return this.GetCoalesceEntityKeyForEntityId(EntityId, EntityIdType, EntityName, null);
    }

    public String GetCoalesceEntityKeyForEntityId(String EntityId,
                                                  String EntityIdType,
                                                  String EntityName,
                                                  String EntitySource)
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
    {
        return this.GetCoalesceEntityKeysForEntityId(EntityId, EntityIdType, EntityName, null);
    }

    public List<String> GetCoalesceEntityKeysForEntityId(String EntityId,
                                                         String EntityIdType,
                                                         String EntityName,
                                                         String EntitySource)
    {

        List<String> list = new ArrayList<String>();

        String[] EntityIdList = EntityId.split(",");
        String[] EntityIdTypeList = EntityIdType.split(",");

        if (EntityIdList.length == EntityIdTypeList.length)
        {

            for (int i = 0; i < EntityIdTypeList.length; i++)
            {

                list.addAll(this._Persister.GetCoalesceEntityKeysForEntityId(EntityIdList[i],
                                                                             EntityIdTypeList[i],
                                                                             EntityName,
                                                                             EntitySource));

            }

        }

        return list;

    }

    public EntityMetaData GetCoalesceEntityIdAndTypeForKey(String Key)
    {
        return this._Persister.GetCoalesceEntityIdAndTypeForKey(Key);
    }

    /*--------------------------------------------------------------------------
    	Other Entity Functions
    --------------------------------------------------------------------------*/

    public DateTime GetCoalesceEntityLastModified(String key, String objectType)
    {
        return this._Persister.GetCoalesceDataObjectLastModified(key, objectType);
    }

    public boolean SaveCoalesceEntity(XsdEntity entity)
    {
        return this.SaveCoalesceEntity(entity, false);
    }

    public boolean SaveCoalesceEntity(XsdEntity entity, boolean AllowRemoval)
    {
        return this._Persister.SetEntity(entity, AllowRemoval);
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

    public String GetCoalesceFieldValue(String FieldKey)
    {
        return (String) this._Persister.GetFieldValue(FieldKey);
    }

    public XsdRecord GetCoalesceRecord(String Key)
    {
        XsdRecord record = null;

        ElementMetaData metaData = this._Persister.GetXPath(Key, "record");
        if (metaData != null)
        {
            XsdEntity entity = this._Persister.GetEntity(metaData.entityKey);
            if (entity != null)
            {
                record = (XsdRecord) entity.GetDataObjectForNamePath(metaData.elementXPath);
            }
        }

        return record;
    }

    public void GetCoalesceFieldAndEntityByFieldKey(String Key)
    {
        // TODO: Not Implemented
    }

    public XsdField GetCoalesceFieldByFieldKey(String Key)
    {
        XsdField field = null;

        ElementMetaData metaData = this._Persister.GetXPath(Key, "field");

        if (metaData != null)
        {
            XsdEntity entity = this._Persister.GetEntity(metaData.entityKey);

            if (entity != null)
            {
                field = (XsdField) entity.GetCoalesceDataObjectForKey(Key);
            }
        }

        return field;
    }

    /*--------------------------------------------------------------------------
    	Template Functions
    --------------------------------------------------------------------------*/

    public boolean SaveCoalesceEntityTemplate(CoalesceEntityTemplate template)
    {
        return this._Persister.PersistEntityTemplate(template);
    }

    public CoalesceEntityTemplate GetCoalesceEntityTemplate(String Name, String Source, String Version) throws SAXException,
            IOException
    {

        CoalesceEntityTemplate template = new CoalesceEntityTemplate();

        // Initialize Template
        template.Initialize(this.GetCoalesceEntityTemplateXml(Name, Source, Version));

        return template;

    }

    public String GetCoalesceEntityTemplateXml(String Key)
    {
        return this._Persister.GetEntityTemplateXml(Key);
    }

    public String GetCoalesceEntityTemplateXml(String Name, String Source, String Version)
    {
        return this._Persister.GetEntityTemplateXml(Name, Source, Version);
    }

    public String GetCoalesceEntityTemplateKey(String Name, String Source, String Version)
    {
        return this._Persister.GetEntityTemplateKey(Name, Source, Version);
    }

    public String GetCoalesceEntityTemplateMetadata()
    {
        return this._Persister.GetEntityTemplateMetadata();
    }

    public XsdEntity CreateEntityFromTemplate(String Name, String Source, String Version)
    {

        String Xml = this.GetCoalesceEntityTemplateXml(Name, Source, Version);

        XsdEntity entity = new XsdEntity();
        entity.Initialize(Xml);

        return entity;

    }

    /*--------------------------------------------------------------------------
    	Sync Shell Functions
    --------------------------------------------------------------------------*/

    public void GetCoalesceEntitySyncShell(String Key)
    {

        // Get Entity
        // XsdEntity entity = this.GetCoalesceEntity(Key);

        // TODO: Create Sync Shell

    }

}
