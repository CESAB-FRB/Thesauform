package thesauform.model.vocabularies;

import com.hp.hpl.jena.rdf.model.Resource;

import thesauform.model.ThesauformConfiguration;

public class RefVoc extends Voc {
	public static Resource Reference = null;

	static {
		try {
			// URI
			setUri(ThesauformConfiguration.term_uri + ThesauformConfiguration.uriRef);
			// Resource
			Reference = getCResource("Reference");

		} catch (Exception e) {
			ThesauformConfiguration.thesauform_logger.error(String.class.getName() + ": failure" + e.getMessage());
		}
	}
}
