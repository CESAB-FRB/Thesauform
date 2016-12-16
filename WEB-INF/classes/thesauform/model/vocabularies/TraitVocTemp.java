package thesauform.model.vocabularies;

import com.hp.hpl.jena.rdf.model.*;

import thesauform.model.ThesauformConfiguration;

public class TraitVocTemp extends Voc {

	// Properties
	public static Property abbreviation = null;
	public static Property synonym = null;
	public static Property reference = null;
	public static Property prefUnit = null;
	public static Property vote = null;
	public static Property formalName = null;
	public static Property cat = null;

	// Resource
	public static Resource Trait = null;

	static {
		try {
			// URI
			setUri(ThesauformConfiguration.term_uri + ThesauformConfiguration.uriTraitTmp);
			// Properties
			abbreviation = getCProperty("abbreviation");
			synonym = getCProperty("synonym");
			reference = getCProperty("reference");
			prefUnit = getCProperty("prefUnit");
			vote = getCProperty("vote");
			formalName = getCProperty("formalName");
			cat = getCProperty("cat");
			// Resource
			Trait = getCResource("Trait");
		} catch (Exception e) {
			ThesauformConfiguration.thesauform_logger.error(String.class.getName()+ ": failure" + e.getMessage());
		}
	}
}
