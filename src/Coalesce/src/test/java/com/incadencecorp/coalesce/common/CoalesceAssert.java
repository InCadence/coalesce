package com.incadencecorp.coalesce.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.incadencecorp.coalesce.common.helpers.DocumentThumbnailHelper.DocumentThumbnailResults;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkageSection;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;

public class CoalesceAssert {

    private static BufferedImage EXPECTED_DESERT_THUMBNAIL;

    // Make class static
    private CoalesceAssert()
    {

    }

    /*--------------------------------------------------------------------------
    Public Static Functions
    --------------------------------------------------------------------------*/

    public static void assertXmlEquals(String expected, String actual)
    {
        expected = expected.replaceAll("<\\?xml.*\\?>", "");
        expected = expected.replaceAll("\\s+", "").replaceAll("[^.]...Z\\\"", "Z\\\"");

        actual = actual.replaceAll("<\\?xml.*\\?>", "");
        actual = actual.replaceAll("\\s+", "").replaceAll("[^.]...Z\\\"", "Z\\\"");

        assertEquals(expected, actual);
    }

    public static void assertThumbnail(DocumentThumbnailResults actual) throws IOException
    {
        assertEquals(1024, actual.getOriginalWidth());
        assertEquals(768, actual.getOriginalHeight());

        assertThumbnail(actual.getThumbnail());
    }

    public static void assertThumbnail(BufferedImage actual) throws IOException
    {
        assertTrue("Thumbnail is not correct", testImagesEqual(CoalesceAssert.getExpectedThumbnail(), actual));
    }

    public static boolean testImagesEqual(BufferedImage img1, BufferedImage img2)
    {
        if (img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight())
        {
            for (int x = 0; x < img1.getWidth(); x++)
            {
                for (int y = 0; y < img1.getHeight(); y++)
                {
                    if (img1.getRGB(x, y) != img2.getRGB(x, y)) return false;
                }
            }
        }
        else
        {
            return false;
        }
        return true;
    }

    public static void assertTemplateCreation(CoalesceEntity expectedEntity, CoalesceEntity templateEntity)
    {
        // Check entity attributes
        assertNotEquals(expectedEntity.getKey().toLowerCase(), templateEntity.getKey().toLowerCase());
        assertEquals(expectedEntity.getName(), templateEntity.getName());
        assertEquals(expectedEntity.getSource(), templateEntity.getSource());
        assertEquals(expectedEntity.getVersion(), templateEntity.getVersion());
        assertEquals("", templateEntity.getEntityId());
        assertEquals("", templateEntity.getEntityIdType());
        assertEquals(templateEntity.getName(), templateEntity.getTitle());

        // Check linkage section
        CoalesceLinkageSection expectedLinkageSection = expectedEntity.getLinkageSection();
        CoalesceLinkageSection templateLinkageSection = templateEntity.getLinkageSection();

        assertTrue(templateLinkageSection.getLinkages().isEmpty());
        assertNotEquals(expectedLinkageSection.getKey().toLowerCase(), templateLinkageSection.getKey().toLowerCase());
        assertEquals(expectedLinkageSection.getName(), templateLinkageSection.getName());

        assertTemplateSection(expectedEntity.getSectionsAsList(), templateEntity.getSectionsAsList());

    }

    public static void assertTemplateSection(List<CoalesceSection> expectedSections,
                                             List<CoalesceSection> templateSections)
    {
        Map<String, CoalesceSection> templateSectionsByName = new HashMap<String, CoalesceSection>();
        for (CoalesceSection templateSection : templateSections)
        {
            templateSectionsByName.put(templateSection.getName(), templateSection);
        }

        assertEquals(expectedSections.size(), templateSectionsByName.size());

        for (CoalesceSection expectedSection: expectedSections)
        {
            CoalesceSection templateSection = templateSectionsByName.get(expectedSection.getName());

            assertNotNull("Expected section not found in template section list", templateSection);
            assertNotEquals(expectedSection.getKey().toLowerCase(), templateSection.getKey().toLowerCase());
            assertEquals(expectedSection.getName(), templateSection.getName());

            assertTemplateRecordSection(expectedSection.getRecordsetsAsList(), templateSection.getRecordsetsAsList());

        }

    }

    public static void assertTemplateRecordSection(List<CoalesceRecordset> expectedRecordsets,
                                                   List<CoalesceRecordset> templateRecordsets)
    {
        Map<String, CoalesceRecordset> templateRecordsetsByName = new HashMap<String, CoalesceRecordset>();
        for (CoalesceRecordset templateRecordset : templateRecordsets)
        {
            templateRecordsetsByName.put(templateRecordset.getName(), templateRecordset);
        }

        assertEquals(expectedRecordsets.size(), templateRecordsetsByName.size());

        for (CoalesceRecordset expectedRecordset : expectedRecordsets)
        {
            CoalesceRecordset templateRecordset = templateRecordsetsByName.get(expectedRecordset.getName());

            assertNotNull("Expected recordset not found in template recordset list", templateRecordset);
            assertNotEquals(expectedRecordset.getKey().toLowerCase(), templateRecordset.getKey().toLowerCase());
            assertEquals(expectedRecordset.getName(), templateRecordset.getName());
            assertTrue("Template recordset should not have records", templateRecordset.getRecords().isEmpty());

            assertFieldDefinitions(expectedRecordset.getFieldDefinitions(), templateRecordset.getFieldDefinitions());

        }
    }

    public static void assertFieldDefinitions(List<CoalesceFieldDefinition> expectedFieldDefinitions,
                                                      List<CoalesceFieldDefinition> templateFieldDefinitions)
    {

        assertEquals(expectedFieldDefinitions.size(), templateFieldDefinitions.size());

        Map<String, CoalesceFieldDefinition> templateFieldDefinitionMap = new HashMap<String, CoalesceFieldDefinition>();
        for (CoalesceFieldDefinition templateFieldDef : templateFieldDefinitions)
        {
            templateFieldDefinitionMap.put(templateFieldDef.getName(), templateFieldDef);
        }

        for (CoalesceFieldDefinition expectedFieldDef : expectedFieldDefinitions)
        {
            CoalesceFieldDefinition templateFieldDef = templateFieldDefinitionMap.get(expectedFieldDef.getName());

            assertNotNull("Expected field definition not found in template field definition list", templateFieldDef);
            assertNotEquals(expectedFieldDef.getKey().toLowerCase(), templateFieldDef.getKey().toLowerCase());

            assertEquals(expectedFieldDef.getName(), templateFieldDef.getName());
            assertEquals(expectedFieldDef.getDataType(), templateFieldDef.getDataType());
            assertEquals(expectedFieldDef.getDefaultValue(), templateFieldDef.getDefaultValue());
            assertEquals(expectedFieldDef.getDefaultClassificationMarking(),
                         templateFieldDef.getDefaultClassificationMarking());
            assertEquals(expectedFieldDef.getLabel(), templateFieldDef.getLabel());
            assertEquals(expectedFieldDef.getNoIndex(), templateFieldDef.getNoIndex());
            assertEquals(expectedFieldDef.isDisableHistory(), templateFieldDef.isDisableHistory());

        }
    }

    /*--------------------------------------------------------------------------
    Private Static Functions
    --------------------------------------------------------------------------*/

    private static BufferedImage getExpectedThumbnail() throws IOException
    {
        if (CoalesceAssert.EXPECTED_DESERT_THUMBNAIL == null)
        {
            String testPath = CoalesceUnitTestSettings.getResourceAbsolutePath("desert_thumb.png");
            CoalesceAssert.EXPECTED_DESERT_THUMBNAIL = ImageIO.read(new File(testPath));
        }

        return CoalesceAssert.EXPECTED_DESERT_THUMBNAIL;
    }
}
