package thesauform.model.vocabularies;

import com.hp.hpl.jena.rdf.model.Resource;

import thesauform.model.ThesauformConfiguration;

public class UnitVoc extends Voc {
	public static Resource Unit = null;

	static {
		try {
			// URI
			setUri(ThesauformConfiguration.term_uri + ThesauformConfiguration.uriUnit);
			// Resource
			Unit = getCResource("Unit");
		} catch (Exception e) {
			ThesauformConfiguration.thesauform_logger.error(String.class.getName() + ": failure" + e.getMessage());
		}
	}
}
