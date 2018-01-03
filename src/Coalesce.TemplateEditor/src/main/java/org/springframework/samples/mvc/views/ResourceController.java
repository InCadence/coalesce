package org.springframework.samples.mvc.views;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.xml.sax.SAXException;

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.plugins.template2java.CoalesceCodeGeneratorIterator;

@Controller
@RequestMapping("/data/*")
public class ResourceController {

    @RequestMapping(value = "download/{json}", method = RequestMethod.GET)
    public void editor(@PathVariable("json") String json, HttpServletResponse response) throws IOException, SAXException
    {

        System.out.println(json);

        CoalesceEntityTemplate template = createTemplate(json);

        String templateName = template.getName();

        File file = new File(templateName + ".xml");
        file.createNewFile();

        System.out.println(file.getAbsolutePath());

        FileUtils.writeStringToFile(file, template.toXml());

        InputStream inputStream = new FileInputStream(file);

        // MIME type of the file
        response.setContentType("application/octet-stream");
        // Response header
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
        // Read from the file and write into the response
        OutputStream outputStream = response.getOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) != -1)
        {
            outputStream.write(buffer, 0, len);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();

        file.delete();
    }

    @RequestMapping(value = "zip/{json}", method = RequestMethod.GET)
    public void zip(@PathVariable("json") String json, HttpServletResponse response)
            throws IOException, SAXException, CoalesceException
    {
        CoalesceEntityTemplate template = createTemplate(json);
        String templateName = template.getName();
        String templateZipName = templateName+".zip";

        File file = new File(templateName);
        file.mkdir();

        CoalesceCodeGeneratorIterator it = new CoalesceCodeGeneratorIterator(Paths.get(file.getAbsolutePath()));
        it.generateCode(template);
        
        FileOutputStream fos = new FileOutputStream(templateZipName);
        ZipOutputStream zos = new ZipOutputStream(fos);
        zipFile(zos, file, null);
        zos.flush();
        fos.flush();
        zos.close();
        fos.close();
        
        File zipFile = new File(templateZipName);
        InputStream inputStream = new FileInputStream(zipFile);

        // MIME type of the file
        response.setContentType("application/octet-stream");
        // Response header
        response.setHeader("Content-Disposition", "attachment; filename=\"" + templateZipName + "\"");
        // Read from the file and write into the response
        OutputStream outputStream = response.getOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) != -1)
        {
            outputStream.write(buffer, 0, len);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();

        FileUtils.deleteDirectory(file);
        zipFile.delete();

    }

    @RequestMapping(value = "template/{json}", method = RequestMethod.POST)
    public void jsonToXml(@PathVariable("json") String json, HttpServletResponse response) throws IOException, SAXException
    {

        String xml = createTemplate(json).toXml();
        response.setContentType("text/xml;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");

        try
        {
            response.getWriter().write(xml);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private CoalesceEntityTemplate createTemplate(String json) throws SAXException, IOException
    {
        JSONObject obj = new JSONObject(json);
        String className = obj.getString("className").replace('-', '.');

        CoalesceEntity entity = new CoalesceEntity();
        entity.initialize();
        entity.setName(obj.getString("name"));
        entity.setSource(obj.getString("source"));
        entity.setVersion(obj.getString("version"));
        entity.setAttribute(CoalesceEntity.ATTRIBUTE_CLASSNAME, className);

        JSONArray jsonSections = obj.getJSONArray("sectionsAsList");

        for (int i = 0; i < jsonSections.length(); i++)
        {
            JSONObject jsonSection = jsonSections.getJSONObject(i);
            String SectionName = jsonSection.getString("name");

            CoalesceSection section = entity.createSection(SectionName);

            JSONArray jsonRecordSets = jsonSection.getJSONArray("recordsetsAsList");

            for (int j = 0; j < jsonRecordSets.length(); j++)
            {

                JSONObject jsonRecordSet = jsonRecordSets.getJSONObject(j);
                String recordsetName = jsonRecordSet.getString("name");
                CoalesceRecordset recordset = section.createRecordset(recordsetName);
                recordset.setMinRecords(jsonRecordSet.getInt("minRecords"));
                recordset.setMaxRecords(jsonRecordSet.getInt("maxRecords"));

                JSONArray jsonFields = jsonRecordSet.getJSONArray("fieldDefinitions");

                for (int k = 0; k < jsonFields.length(); k++)
                {
                    JSONObject jsonField = jsonFields.getJSONObject(k);
                    String fieldName = jsonField.getString("name");
                    String fieldType = jsonField.getString("dataType");
                    ECoalesceFieldDataTypes type = ECoalesceFieldDataTypes.getTypeForCoalesceType(fieldType);

                    recordset.createFieldDefinition(fieldName, type);
                }
            }
        }

        return CoalesceEntityTemplate.create(entity);
    }

    public void zipFile(ZipOutputStream zos, File fileToZip, String parentDir) throws IOException 
    {
        if (fileToZip == null || !fileToZip.exists())
        {
            return;
        }

        String zipEntryName = fileToZip.getName();
        if (parentDir != null && !parentDir.isEmpty())
        {
            zipEntryName = parentDir + "/" + fileToZip.getName();
        }

        if (fileToZip.isDirectory())
        {
            System.out.println("+" + zipEntryName);
            for (File file : fileToZip.listFiles())
            {
                zipFile(zos, file, zipEntryName);
            }
        }
        else
        {
            System.out.println("   " + zipEntryName);
            byte[] buffer = new byte[1024];
            FileInputStream fis = new FileInputStream(fileToZip);
            zos.putNextEntry(new ZipEntry(zipEntryName));
            int length;
            while ((length = fis.read(buffer)) > 0)
            {
                zos.write(buffer, 0, length);
            }
            zos.closeEntry();
            fis.close();
        }
    }

}
