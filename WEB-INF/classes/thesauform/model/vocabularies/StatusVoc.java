package thesauform.model.vocabularies;

import com.hp.hpl.jena.rdf.model.Property;

import thesauform.model.ThesauformConfiguration;

public class StatusVoc extends Voc {

	// property
	// status possible : unstable, testing, stable, archaic
	public static Property term_status = null;

	static {
		try {
			// URI
			setUri(ThesauformConfiguration.vs);

			// Properties

			term_status = getCProperty("term_status");

		} catch (Exception e) {
			ThesauformConfiguration.thesauform_logger.error(String.class.getName() + ": failure" + e.getMessage());
		}
	}

}
