package aml;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.apache.commons.lang.StringUtils;
import org.semanticweb.owlapi.model.OWLOntology;

import aml.match.Alignment;
import aml.match.AutomaticMatcher;
import aml.ontology.RelationshipMap;
import aml.settings.EntityType;

import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;   

public class AutoMatcher {
	
	public static void main(String[] args) throws Exception
	{
		// times
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss yyyy/MM/dd"); 
		LocalDateTime start = LocalDateTime.now();
		
		//define arguments
		String o1 = args[0];
		String o2 = args[1];
		String path = args[2];  
		
		System.out.println(" ");
		System.out.println("Started matching " + o1 + " and " + o2 + " at " + dtf.format(start));
		
		// create new file
		FileWriter fw = new FileWriter(path + "/pairwise.tsv", true);
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter outTime = new PrintWriter(bw);
		
		//get ontology names
		String oo1 = StringUtils.substringBefore(o1, ".");
		String oo2 = StringUtils.substringBefore(o2, ".");
		
		//define file paths
		String sourcePath = path + "/ontologies/" + o1;
		String targetPath = path + "/ontologies/" + o2;
		
		//start AML and open ontologies
		long loadTime = System.currentTimeMillis()/1000;
		AML aml = AML.getInstance();
		aml.openOntologies(sourcePath, targetPath);
		aml.getSource();
		aml.getTarget();
		
		//times again
		loadTime = System.currentTimeMillis()/1000 - loadTime;
		
		//automatic matcher and save alignment
		long matchTime = System.currentTimeMillis()/1000;
		AutomaticMatcher.match();
		matchTime = System.currentTimeMillis()/1000 - matchTime; // times yet again
		Alignment a = aml.getAlignment();
		a.saveRDF(path + "/alignments/alignment_" + oo1 + "_" + oo2 + "_AML.rdf");
		
		// times yet yet again
		LocalDateTime end = LocalDateTime.now();  
		
		//print ontology pair and times to doc
		outTime.println("AML_" + o1 + "/" + o2 + "\t" + loadTime + "\t" + matchTime + "\t" + a.size() + "\t" + dtf.format(start) + "\t" + dtf.format(end));
		outTime.close();
		System.out.println("Final alignment: " + a.size());
		System.out.println("Finished! It is currently: " + dtf.format(end));
		
	}
}
