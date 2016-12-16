package thesauform.model.vocabularies;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import thesauform.model.ThesauformConfiguration;

public class SkosXLVoc extends Voc {


	// resources
	public static Resource Label = null;

	// properties
	public static Property hiddenLabel = null;
	public static Property altLabel = null;
	public static Property labelRelation = null;
	public static Property literalForm = null;
	public static Property prefLabel = null;

	static {
		try {
			// URI
			setUri(ThesauformConfiguration.skosXL);
			// Properties
			hiddenLabel = getCProperty("hiddenLabel");
			prefLabel = getCProperty("prefLabel");
			altLabel = getCProperty("altLabel");
			labelRelation = getCProperty("labelRelation");
			literalForm = getCProperty("literalForm");
			// resource
			Label = getCResource("Label");
		} catch (Exception e) {
			ThesauformConfiguration.thesauform_logger.error(String.class.getName()+ ": failure" + e.getMessage());
		}
	}

}
