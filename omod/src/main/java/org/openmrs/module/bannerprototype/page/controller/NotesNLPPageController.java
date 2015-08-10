package org.openmrs.module.bannerprototype.page.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptSet;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.TestOrder;
import org.openmrs.User;
import org.openmrs.api.ConceptService;
import org.openmrs.api.FormService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bannerprototype.SofaDocument;
import org.openmrs.module.bannerprototype.SofaTextMention;
import org.openmrs.module.bannerprototype.bannerprototype;
import org.openmrs.module.bannerprototype.api.NLPService;
import org.openmrs.module.bannerprototype.eval.DocGenerator;
import org.openmrs.module.bannerprototype.nlp.DocumentTagger;
import org.openmrs.module.bannerprototype.nlp.NERTagger;
import org.openmrs.module.bannerprototype.nlp.TaggerFactory;
import org.openmrs.module.bannerprototype.reporting.ReportGenerator;
import org.openmrs.module.bannerprototype.web.wordcloud.WordCloud;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import banner.tokenization.Tokenizer;

/**
*
* @author Eshleman
*/
public class NotesNLPPageController {
	
	String loadModelFileName;
	
	Tokenizer tokenizer;
	NERTagger tagger;
	
	SofaDocument sofaDocument = new SofaDocument();
	List<SofaDocument> allSofaDocuments;
	int sofaDocumentId=0;
	
	boolean reloadDocuments = true;
	
	protected final Log log = LogFactory.getLog(getClass());
	String sofa = "";

   // public void get(PageModel pageModel) {
   public void controller(@RequestParam("patientId") Patient patient,
           PageModel pageModel,
           @RequestParam(value = "returnUrl", required = false) String returnUrl,
           @RequestParam(value = "docId", required = false, defaultValue = "-1") int docId) throws IOException, ClassNotFoundException{
	   
	   	Integer patientId = patient.getId();
	   	System.out.println(patientId);
	   	String patientMRN = patient.getPatientIdentifier().getIdentifier();

		FormService fs = Context.getFormService();
		ConceptService cs = Context.getConceptService();
		ObsService os = Context.getObsService();
		WordCloud wordcloud = new WordCloud();
		
		Concept c = cs.getConceptByName("Text of encounter note");
		List<Obs> obs = os.getObservationsByPersonAndConcept(patient, c);
		
		
		allSofaDocuments = Context.getService(NLPService.class).getSofaDocumentsByPatient(patient);
		
		if(docId == -1 && allSofaDocuments.size() != 0)
			sofaDocument = allSofaDocuments.get(0);
		else
			sofaDocument = Context.getService(NLPService.class).getSofaDocumentById(docId);
		
		for(SofaDocument sd : allSofaDocuments)
		{	
			addToCloud(wordcloud, sd.getAllMentions());
			
		}	
		
		
		//System.out.println(allSofaDocuments.size());
		reloadDocuments = true; // if not redirected from POST, reload documents
		//sofaDocument = allSofaDocuments.get(0);
		if(sofaDocument != null)
		{
			pageModel.addAttribute("docHtml",sofaDocument.getAnnotatedHTML());
		}
		else
			pageModel.addAttribute("docHtml","");
		
		//User u = Context.getAuthenticatedUser();
		//u.getId()
		String adminEmail = Context.getAdministrationService().getGlobalProperty("bannerprototype.adminEmail");
		
		pageModel.addAttribute("returnUrl", returnUrl);
		pageModel.addAttribute("user", Context.getAuthenticatedUser());
		pageModel.addAttribute("sofaDocument",sofaDocument);
		pageModel.put("allSofaDocuments",allSofaDocuments);
		pageModel.addAttribute("sofaDocumentId", sofaDocumentId);
		pageModel.addAttribute("bannerprototype",new bannerprototype());
		pageModel.addAttribute("sofa",sofa);
		pageModel.addAttribute("tagCloudWords", wordcloud.getTopWordsShuffled(30));
		pageModel.addAttribute("patientMRN", patientMRN);
		pageModel.addAttribute("adminEmail",adminEmail);
		
		
		String modelFiles[] = new ClassPathResource("taggers/").getFile().list();

   }
   
   

private void runDocEvalTest()
   {
	   
		DocumentTagger tag = new DocumentTagger();
		//tag.tagDocument("test");
		String srcDocs = "/Users/ryaneshleman/Dropbox/SFSU/openMRS/development/testDocs/";
		
		int docs = 1000;
		
		long startTime,endTime;
		long duration = 0;
		SofaDocument s;
		String doc;
		StringBuffer sb;
		File files[] = new File(srcDocs).listFiles();
		
		for(int i = 0; i < docs; i++)
		{	
			Scanner sc = null;
			try {
				sc = new Scanner(files[i]);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sb = new StringBuffer();
			while(sc.hasNext())
				sb.append("\n"+sc.nextLine());
			
			sc.close();
			doc = sb.toString();
			
			startTime = System.nanoTime();
			tag.tagDocument(doc);
			endTime = System.nanoTime();
			duration += (endTime - startTime);
			if(i % 10 == 0)
				System.out.println("" + i+","+duration / 1000000);
			
			
		}
		

		//long duration = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds.
			
		System.out.println("\nExecution Time for "+docs+" docs:"+ duration/1000000);
  		
	   
   }

private void runReanalysis() {
	   System.out.println("1");
	   DocumentTagger dt = new DocumentTagger();
	   System.out.println("2");
	   List<Concept> concepts = new ArrayList<Concept>();
	   System.out.println("3");
	   concepts.add(Context.getConceptService().getConceptByName("Text of encounter note"));
	   System.out.println("4");
	   Context.getService(NLPService.class).truncateNLPtables();
	   System.out.println("5");
	   
	   
	   System.out.println("Running Analysis");
	   List<Obs> obs =  Context.getObsService().getObservations(null, null, concepts,null, null, null, null, null, null, null, null, false);
	   System.out.println(obs.size());
	   for(Obs o : obs)
	   {   
		   SofaDocument sd = dt.tagDocument(o.getValueText());
		   sd.setPatient((Patient) o.getPerson());
		   Context.getService(NLPService.class).saveSofaDocument(sd);
		   
	   }
}
   
   
   private void addToCloud(WordCloud wordcloud, List<SofaTextMention> mentions) {
	
	   for(SofaTextMention m : mentions)
	   {
		   /*
		   for(String str : m.getMentionText().split(" "))
		   {   
			   wordcloud.addWord(str);
			   //System.out.println(str);
		   }
		   */
		   wordcloud.addWord(m.getMentionText(),m.getMentionType());
		   
		   
	   }
	
}

public String post(@RequestParam(value = "docId", required = false) int docId,
           @RequestParam(value = "returnUrl", required = false) String returnUrl,
           @RequestParam("patientId") Patient patient) {
	   
	   		
	   
	   		//System.out.println("***************in post**********************");
	   		//System.out.println(allSofaDocuments.size());
	        for(SofaDocument sd : allSofaDocuments)
				if(sd.getSofaDocumentId() == docId)
				{	
					sofaDocument = Context.getService(NLPService.class).getSofaDocumentById(docId);
					break;
				}
	        
	        reloadDocuments=false;
	        return "OK";
	        
	    }
   }

