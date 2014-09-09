package Coalesce.Framework.Persistance;

import java.util.List;

import org.joda.time.DateTime;

import Coalesce.Common.Exceptions.CoalescePersistorException;
import Coalesce.Framework.DataModel.CoalesceEntityTemplate;
import Coalesce.Framework.DataModel.XsdEntity;

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

public interface ICoalescePersistor {

    public class EntityMetaData {

        public String entityId;
        public String entityType;
        public String entityKey;

    }
    
    public class ElementMetaData {

        public String entityKey;
        public String elementXPath;

    }

    public boolean initialize(ICoalesceCacher Cacher) throws CoalescePersistorException;

    public boolean setEntity(XsdEntity entity, boolean AllowRemoval) throws CoalescePersistorException;

    // Get Entity
    public XsdEntity getEntity(String Key) throws CoalescePersistorException;

    public XsdEntity getEntity(String EntityId, String EntityIdType) throws CoalescePersistorException;

    public XsdEntity getEntity(String Name, String EntityId, String EntityIdType) throws CoalescePersistorException;

    // Get Entity XML
    public String getEntityXml(String Key) throws CoalescePersistorException;

    public String getEntityXml(String EntityId, String EntityIdType) throws CoalescePersistorException;

    public String getEntityXml(String Name, String EntityId, String EntityIdType) throws CoalescePersistorException;

    public Object getFieldValue(String fieldKey) throws CoalescePersistorException;

    public ElementMetaData getXPath(String Key, String ObjectType) throws CoalescePersistorException;

    public DateTime getCoalesceDataObjectLastModified(String Key, String ObjectType) throws CoalescePersistorException;

    public List<String> getCoalesceEntityKeysForEntityId(String EntityId,
                                                         String EntityIdType,
                                                         String EntityName,
                                                         String EntitySource) throws CoalescePersistorException;

    public EntityMetaData getCoalesceEntityIdAndTypeForKey(String Key) throws CoalescePersistorException;

    public byte[] getBinaryArray(String BinaryFieldKey) throws CoalescePersistorException;

    // Deprecated Functions
    public boolean persistEntityTemplate(CoalesceEntityTemplate EntityTemplate) throws CoalescePersistorException;

    // Entity Templates
    public String getEntityTemplateXml(String Key) throws CoalescePersistorException;

    public String getEntityTemplateXml(String Name, String Source, String Version) throws CoalescePersistorException;

    public String getEntityTemplateKey(String Name, String Source, String Version) throws CoalescePersistorException;

    public String getEntityTemplateMetadata() throws CoalescePersistorException;

}