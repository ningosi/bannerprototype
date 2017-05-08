package org.openmrs.module.bannerprototype;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Patient;
import org.openmrs.module.bannerprototype.wordcloud.Word;

public class SofaDocumentUI extends BaseOpenmrsData implements Serializable {
	
	private String uuid;
	
	private Patient patient;
	
	private Date dateCreated;
	
	private int mentionCount;
	
	private String diagnosis;
	
	private String provider;
	
	private String location;
	
	private List<Word> problemWordList;
	
	private List<Word> treatmentWordList;
	
	private List<Word> testWordList;
	
	public SofaDocumentUI(String uuid, Date dateCreated, String provider, String location, String diagnosis) {
		this.uuid = uuid;
		this.dateCreated = dateCreated;
		this.provider = provider;
		this.location = location;
		this.diagnosis = diagnosis;
		this.setMentionCount(1);
	}
	
	public void incrementCount() {
		this.setMentionCount(this.getMentionCount() + 1);
	}
	
	public String getDiagnosis() {
		return diagnosis;
	}
	
	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}
	
	public String getProvider() {
		return provider;
	}
	
	public void setProvider(String provider) {
		this.provider = provider;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	@Override
	public Integer getId() {
		
		return null;
	}
	
	@Override
	public void setId(Integer arg0) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * uuid is a unique identifier for the SofaDocument
	 * 
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}
	
	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	/**
	 * @return the dateCreated
	 */
	public Date getDateCreated() {
		return dateCreated;
	}
	
	/**
	 * @param dateCreated the dateCreated to set
	 */
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	/**
	 * @return the patient
	 */
	public Patient getPatient() {
		return patient;
	}
	
	/**
	 * @param patient the patient to set
	 */
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	/**
	 * Added to handle ERROR - BaseRestController.handleException(106) Could not write JSON:
	 * Conflicting getter definitions for property "voided"
	 */
	@Override
	public Boolean isVoided() {
		return super.isVoided();
	}
	
	/**
	 * @return the mentionCount
	 */
	public int getMentionCount() {
		return mentionCount;
	}
	
	/**
	 * @param mentionCount the mentionCount to set
	 */
	public void setMentionCount(int mentionCount) {
		this.mentionCount = mentionCount;
	}
	
	public List<Word> getProblemWordList() {
		return problemWordList;
	}
	
	public void setProblemWordList(List<Word> problemWordList) {
		this.problemWordList = problemWordList;
	}
	
	public List<Word> getTreatmentWordList() {
		return treatmentWordList;
	}
	
	public void setTreatmentWordList(List<Word> treatmentWordList) {
		this.treatmentWordList = treatmentWordList;
	}
	
	public List<Word> getTestWordList() {
		return testWordList;
	}
	
	public void setTestWordList(List<Word> testWordList) {
		this.testWordList = testWordList;
	}
}
