package org.openmrs.module.bannerprototype.nlp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;

import banner.Sentence;
//import banner.tagging.CRFTagger;
import banner.tokenization.Tokenizer;
import banner.tagging.Mention;
import com.sfsu.bannertrain.train.CRFTagger;

/**
 * this is a wrapper class for the BANNER CRF tagger
 * @author ryaneshleman
 *
 */
public class NERTagger implements Serializable {
	CRFTagger tagger;
	String taggerName;
	Tokenizer tokenizer;
	ArrayList<Mention> mentions;
	ArrayList<NamedEntity> namedEntities;

	public NERTagger()
	{
		tagger = TaggerFactory.getTagger();
		taggerName = TaggerFactory.getTaggerName();
		tokenizer = TaggerFactory.getTokenizer();
		
		mentions = new ArrayList<Mention>();
		namedEntities = new ArrayList<NamedEntity>();
		
		
	}
	
	public ArrayList<NamedEntity> tag(String sofa)
	{
		// if the global configuration has changed since tagger was initialized
		if(TaggerFactory.isNewtaggerRequired(taggerName))
		{	
			tagger = TaggerFactory.getTagger();
			taggerName = TaggerFactory.getTaggerName();
		}
		
		
		namedEntities.clear();

		List<Concept> matchedConcepts = new ArrayList<Concept>();
		String mentionText;
		
		mentions.clear();
		Sentence sentence = new Sentence(sofa);
		tokenizer.tokenize(sentence);
		
		//perform CRF tagging
		tagger.tag(sentence);
		
		mentions.addAll(sentence.getMentions());
		
		//extract tags and construct NamedEntity objects
		for(Mention m : mentions)
		{
			mentionText = m.getText();


			namedEntities.add(new NamedEntity(m,matchedConcepts));
		}
		
		return namedEntities;
		
	}

}
