package thesauform.model.vocabularies;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import thesauform.model.ThesauformConfiguration;

public class TraitVoc extends Voc {

	// Properties
	public static Property prefUnit = null;
	public static Property annotation = null;
	public static Property origine = null;
	public static Property altUnit = null;
	public static Property abbreviation = null;
	public static Property creationDate = null;
	public static Property lastModifDate = null;
	public static Property literalForm = null;
	public static Property delete = null;
	public static Property reference = null;
	public static Property hasValue = null;
	public static Property hasProperty = null;
	public static Property hasPropertyType = null;
	// Resources
	// Exemplification PropertyValuePair of DCAM
	public static Resource Annotation = null;
	public static Resource Unit = null;
	public static Resource Label = null;
	public static Resource Trait = null;
	public static Resource Reference = null;

	static {
		try {
			// URI
			setUri(ThesauformConfiguration.term_uri + ThesauformConfiguration.uriTrait);
			// Properties
			abbreviation = getCProperty("abbreviation");
			// status = getCProperty("status");
			prefUnit = getCProperty("prefUnit");
			annotation = getCProperty("annotation");
			origine = getCProperty("origine");
			altUnit = getCProperty("altUnit");
			creationDate = getCProperty("creationDate");
			lastModifDate = getCProperty("lastModifDate");
			literalForm = getCProperty("literalForm");
			delete = getCProperty("delete");
			reference = getCProperty("reference");
			// Exemplification PropertyValuePair of DCAM
			hasValue = getCProperty("hasValue");
			hasProperty = getCProperty("hasProperty");
			hasPropertyType = getCProperty("hasPropertyType");
			// Resources
			Annotation = getCResource("Annotation");
			Trait = getCResource("Trait");
			Unit = getCResource("Unit");
			Label = getCResource("Label");
			Reference = getCResource("Reference");
		} catch (Exception e) {
			ThesauformConfiguration.thesauform_logger.error(String.class.getName() + ": failure" + e.getMessage());
		}
	}

}
