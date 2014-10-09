package FileModifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/*
 * Purpose: Modify the jaxb generated files to: 
 * 1) Add the import of XmlRootElement, 2) remove name from @XmlType, 3) add name to @XmlRootElement
 * 
 * Files and directory are hard coded.
 */

public class CoalesceGeneratedFileModifier {

	public static void main(String[] args) {
		
		//RunWithMultipleClasses();
		RunWithSingleClass();
		
	}
	
	public static void RunWithSingleClass(){
		
		//Get the file list to be modified
		String directory = "C:\\06-jcoalesce\\src\\Coalesce\\src\\main\\java\\com\\incadencecorp\\coalesce\\framework\\generatedjaxb\\";
		//Entity.java should already have @XmlRootElement(name = "entity")
		String[] files = {"Entity.java"};
		
		for (String file : files){
			file = directory + file;
			
			//Modify the files
			FileInputStream fs = null;
	        InputStreamReader in = null;
	        BufferedReader br = null;
	        
	        StringBuffer sb = new StringBuffer();
	        
	        String textinLine;
	        
	        try {
	             fs = new FileInputStream(file);
	             in = new InputStreamReader(fs);
	             br = new BufferedReader(in);
	             
	             String className = "";
	            
	            while(true)
	            {
	                textinLine=br.readLine();
	                if(textinLine==null)
	                    break;
	                
	                //Already exists in single file environment. 
	                //1) Add the import of XmlRootElement
//	                if(textinLine.startsWith("import javax.xml.bind.annotation.XmlSchemaType;")){
//	                	sb.append("import javax.xml.bind.annotation.XmlRootElement;");
//		                sb.append("\n");
//	                }
	                
	                
	                //Already exists in single file environment. 
	                //2) remove name from @XmlType
//	                if(textinLine.startsWith("@XmlType(name = \"")){
//	                	className = textinLine.replace("@XmlType(name = \"", "");
//	                	className = className.substring(0, className.indexOf("\""));
//	                	textinLine = textinLine.replace(className, "");
//	                }
	                
	                //3) add name to @XmlRootElement
	                if(textinLine.trim().startsWith("public static class ")){
	                	int padding = 0;
	                	className = textinLine;
	                	while (className.substring(0, 1).equals(" ")){
	                		padding += 1;
	                		className = className.substring(1);
	                	}
	                	
	                	className = textinLine.trim().replace("public static class ", "");
	                	className = className.replace(" {", "").trim().toLowerCase();
	                	className = "@XmlRootElement(name = \""+ className +"\")";
	                	
	                	while (padding > 0){
	                		className = " " + className;
	                		padding -= 1;
	                	}
	                	
	                	sb.append(className);
		                sb.append("\n");
	                }
		            sb.append(textinLine);
		            sb.append("\n");
                }

	              fs.close();
	              in.close();
	              br.close();

	            } catch (FileNotFoundException e) {
	              e.printStackTrace();
	            } catch (IOException e) {
	              e.printStackTrace();
	            }
	            
	            try{
	                FileWriter fstream = new FileWriter(file);
	                BufferedWriter outobj = new BufferedWriter(fstream);
	                outobj.write(sb.toString());
	                outobj.close();
	                
	            }catch (Exception e){
	              System.err.println("Error: " + e.getMessage());
	            }
	    }
		
	}
	
	public static void RunWithMultipleClasses(){
		//Get the file list to be modified
        String directory = "C:\\06-jcoalesce\\src\\Coalesce\\src\\main\\java\\com\\incadencecorp\\coalesce\\framework\\generatedjaxb\\";
		//Entity.java should already have @XmlRootElement(name = "entity")
		String[] files = {"Field.java", "Fielddefinition.java", "Fieldhistory.java", "Linkage.java", "Linkagesection.java", "Record.java", "Recordset.java", "Section.java"};
		
		for (String file : files){
			file = directory + file;
			
			//Modify the files
			FileInputStream fs = null;
	        InputStreamReader in = null;
	        BufferedReader br = null;
	        
	        StringBuffer sb = new StringBuffer();
	        
	        String textinLine;
	        
	        try {
	             fs = new FileInputStream(file);
	             in = new InputStreamReader(fs);
	             br = new BufferedReader(in);
	             
	             String className = "";
	            
	            while(true)
	            {
	                textinLine=br.readLine();
	                if(textinLine==null)
	                    break;
	                
	                //1) Add the import of XmlRootElement
	                if(textinLine.startsWith("import javax.xml.bind.annotation.XmlSchemaType;")){
	                	sb.append("import javax.xml.bind.annotation.XmlRootElement;");
		                sb.append("\n");
	                }
	                
	                //2) remove name from @XmlType
	                if(textinLine.startsWith("@XmlType(name = \"")){
	                	className = textinLine.replace("@XmlType(name = \"", "");
	                	className = className.substring(0, className.indexOf("\""));
	                	textinLine = textinLine.replace(className, "");
	                }
	                
	                //3) add name to @XmlRootElement
	                if(textinLine.startsWith("public class ")){
	                	sb.append("@XmlRootElement(name = \""+ className.toLowerCase() +"\")");
		                sb.append("\n");
	                }
		            sb.append(textinLine);
		            sb.append("\n");
                }

	              fs.close();
	              in.close();
	              br.close();

	            } catch (FileNotFoundException e) {
	              e.printStackTrace();
	            } catch (IOException e) {
	              e.printStackTrace();
	            }
	            
	            try{
	                FileWriter fstream = new FileWriter(file);
	                BufferedWriter outobj = new BufferedWriter(fstream);
	                outobj.write(sb.toString());
	                outobj.close();
	                
	            }catch (Exception e){
	              System.err.println("Error: " + e.getMessage());
	            }
	    }
		
	}

}
