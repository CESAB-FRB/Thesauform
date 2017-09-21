package thesauform.model.vocabularies;

import com.hp.hpl.jena.rdf.model.Property;

import thesauform.model.ThesauformConfiguration;

public class ChangeVoc extends Voc {

	// Properties
	public static Property insert = null;
	public static Property update = null;
	public static Property delete = null;
	public static Property comment = null;
	public static Property hasProperty = null;
	public static Property hasValue = null;
	public static Property contribution = null;
	public static Property vote = null;
	public static Property hasVote = null;
	public static Property validated = null;
	public static Property hasValidated = null;
	public static Property invalidated = null;
	public static Property hasInvalidated = null;

	static {
		try {
			// URI
			setUri(ThesauformConfiguration.term_uri + ThesauformConfiguration.uriChange);
			// Properties
			insert = getCProperty("insert");
			update = getCProperty("update");
			delete = getCProperty("delete");
			comment = getCProperty("comment");
			hasProperty = getCProperty("hasProperty");
			hasValue = getCProperty("hasValue");
			contribution = getCProperty("contribution");
			vote = getCProperty("vote");
			hasVote = getCProperty("hasVote");
			validated = getCProperty("validated");
			hasValidated = getCProperty("hasValidated");
			invalidated = getCProperty("invalidated");
			hasInvalidated = getCProperty("hasInvalidated");
		} catch (Exception e) {
			ThesauformConfiguration.thesauform_logger.error(String.class.getName()+ ": failure" + e.getMessage());
		}
	}
}
