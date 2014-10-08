package com.incadencecorp.coalesce.framework.objects.Photos;

import java.io.IOException;

import org.jdom2.JDOMException;

import com.drew.imaging.ImageProcessingException;
import com.incadencecorp.coalesce.common.exceptions.CoalesceCryptoException;
import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.common.helpers.DocumentProperties;
import com.incadencecorp.coalesce.common.helpers.FileHelper;
import com.incadencecorp.coalesce.common.runtime.CoalesceSettings;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.objects.ActionBaseEntity;

public class PhotoGalleryEntity extends ActionBaseEntity {

    // ----------------------------------------------------------------------//
    // Static Properties
    // ----------------------------------------------------------------------//

    public static final String SOURCE = "Photos: Supporting";

    // ----------------------------------------------------------------------//
    // Private Member Variables
    // ----------------------------------------------------------------------//

    private CoalesceRecordset _photographRecordset;

    // ----------------------------------------------------------------------//
    // Initialization
    // ----------------------------------------------------------------------//

    @Override
    public boolean initialize()
    {
        if (!initializeEntity(PhotoGalleryEntity.SOURCE, "1.0", "")) return false;
        
        // Initialize References
        return this.initializeReferences();
    }

    @Override
    protected boolean initializeEntity(String source, String version, String title)
    {
        CoalesceSection section;

        // Already Initialized?
        if (_photographRecordset != null) return false;

        // Initialize Entity
        if (!super.initializeEntity(source, version, title)) return false;

        // Create Live Section
        section = CoalesceSection.create(this, "Photographs Section");
        _photographRecordset = CoalesceRecordset.create(section, "Photographs Recordset");

        CoalesceFieldDefinition.create(_photographRecordset, "Photo", ECoalesceFieldDataTypes.FILE_TYPE);
        CoalesceFieldDefinition.create(_photographRecordset, "OriginalFilename", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(_photographRecordset, "Height", ECoalesceFieldDataTypes.INTEGER_TYPE);
        CoalesceFieldDefinition.create(_photographRecordset, "Width", ECoalesceFieldDataTypes.INTEGER_TYPE);
        CoalesceFieldDefinition.create(_photographRecordset, "Description", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(_photographRecordset, "BagTagID", ECoalesceFieldDataTypes.STRING_TYPE);
        CoalesceFieldDefinition.create(_photographRecordset, "DegreesFromTrueNorth", ECoalesceFieldDataTypes.INTEGER_TYPE);
        CoalesceFieldDefinition.create(_photographRecordset, "PhotoDateTimeUTC", ECoalesceFieldDataTypes.DATE_TIME_TYPE);
        CoalesceFieldDefinition.create(_photographRecordset, "PhotoLocation", ECoalesceFieldDataTypes.GEOCOORDINATE_TYPE);

        return true;
    }

    @Override
    protected boolean initializeReferences()
    {
        if (!super.initializeReferences()) return false;

        if (_photographRecordset == null)
        {
            _photographRecordset = (CoalesceRecordset) getDataObjectForNamePath(getName()
                    + "/Photographs Section/Photographs Recordset");

        }

        return _photographRecordset != null;
    }

    // ----------------------------------------------------------------------//
    // Entity Fields
    // ----------------------------------------------------------------------//

    public PhotoRecord[] getPhotos()
    {
        // Initialize Array
        PhotoRecord[] records = new PhotoRecord[_photographRecordset.getRecords().size()];

        // Add Records
        for (int ii = 0; ii < _photographRecordset.getRecords().size(); ii++)
        {
            // Convert From CoalesceRecord to PhotoRecord
            records[ii] = new PhotoRecord(_photographRecordset.getRecords().get(ii));
        }

        // Return List
        return records;
    }

    public PhotoRecord addPhoto(String fileName) throws CoalesceDataFormatException
    {
        PhotoRecord record = null;

        try
        {
            DocumentProperties properties = new DocumentProperties();
            if (properties.initialize(fileName, CoalesceSettings.getUseEncryption()))
            {
                record = addPhoto(properties);
            }
        }
        catch (ImageProcessingException | CoalesceCryptoException | IOException | JDOMException e)
        {
            record = null;
        }

        return record;
    }

    public PhotoRecord addPhoto(DocumentProperties properties) throws CoalesceDataFormatException
    {
        try
        {
            // Load Photo
            byte[] bytes = FileHelper.getFileAsByteArray(properties.getFullFilename());

            // Create Record
            return addPhoto(bytes, properties);
        }
        catch (IOException e)
        {
            return null;
        }
    }

    public PhotoRecord addPhoto(byte[] bytes, DocumentProperties properties) throws CoalesceDataFormatException
    {
        // Create Record
        PhotoRecord record = new PhotoRecord(_photographRecordset.addNew());

        // Set Settings
        record.getPhoto().setValue(bytes);
        record.getOriginalFilename().setValue(properties.getFilename());
        record.getHeight().setValue(properties.getImageHeight());
        record.getWidth().setValue(properties.getImageWidth());
        record.getDescription().setValue(properties.getDescription());
        record.getDateTaken().setValue(properties.getCreated());
        record.getLocationTaken().setValue(properties.getLatitude(), properties.getLongitude());

        // Return Record
        return record;
    }

}
