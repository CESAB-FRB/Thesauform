package thesauform.model.vocabularies;

import com.hp.hpl.jena.rdf.model.Property;

import thesauform.model.ThesauformConfiguration;

public class PersonVoc extends Voc {

	// Properties
	public static Property password = null;
	public static Property right = null;

	static {
		try {
			// URI
			setUri(ThesauformConfiguration.person_uri);
			// Properties
			password = getCProperty("password");
			right = getCProperty("right");
		} catch (Exception e) {
			ThesauformConfiguration.thesauform_logger.error(String.class.getName() + ": failure" + e.getMessage());
		}
	}

}
