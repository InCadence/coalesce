package com.incadencecorp.coalesce.framework.objects.Photos;

import com.incadencecorp.coalesce.framework.datamodel.CoalesceBinaryField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceCoordinateField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceDateTimeField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceIntegerField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringField;

public class PhotoRecord extends CoalesceRecord {

    public PhotoRecord()
    {
        // Do Nothing
    }

    public PhotoRecord(CoalesceRecord record)
    {
        super(record);
    }

    public CoalesceBinaryField getPhoto()
    {
        return (CoalesceBinaryField) this.getFieldByName("Photo");
    }

    public CoalesceStringField getOriginalFilename()
    {
        return (CoalesceStringField) this.getFieldByName("OriginalFilename");
    }

    public CoalesceIntegerField getHeight()
    {
        return (CoalesceIntegerField) this.getFieldByName("Height");
    }

    public CoalesceIntegerField getWidth()
    {
        return (CoalesceIntegerField) this.getFieldByName("Width");
    }

    public CoalesceStringField getDescription()
    {
        return (CoalesceStringField) this.getFieldByName("Description");
    }

    public CoalesceStringField getBagTagID()
    {
        return (CoalesceStringField) this.getFieldByName("BagTagID");
    }

    public CoalesceIntegerField getDegreesFromTrueNorth()
    {
        return (CoalesceIntegerField) this.getFieldByName("DegreesFromTrueNorth");
    }

    public CoalesceDateTimeField getDateTaken()
    {
        return (CoalesceDateTimeField) this.getFieldByName("PhotoDateTimeUTC");
    }

    public CoalesceCoordinateField getLocationTaken()
    {
        return (CoalesceCoordinateField) this.getFieldByName("PhotoLocation");
    }

}
