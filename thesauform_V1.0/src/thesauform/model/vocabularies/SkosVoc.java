package thesauform.model.vocabularies;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import thesauform.model.ThesauformConfiguration;

public class SkosVoc extends Voc {

	// Resources
	public static Resource Concept = null;
	public static Resource ConceptScheme = null;
	public static Resource Collection = null;
	public static Resource OrderedCollection = null;
	public static Resource Annotation = null;

	// Properties
	public static Property inScheme = null;
	public static Property hasTopConcept = null;
	public static Property topConceptOf = null;
	public static Property altLabel = null;
	public static Property hiddenLabel = null;
	public static Property prefLabel = null;
	public static Property notation = null;
	public static Property changeNote = null;
	public static Property definition = null;
	public static Property editorialNote = null;
	public static Property example = null;
	public static Property historyNote = null;
	public static Property note = null;
	public static Property scopeNote = null;
	public static Property broader = null;
	public static Property broaderTransitive = null;
	public static Property narrower = null;
	public static Property narrowerTransitive = null;
	public static Property related = null;
	public static Property semanticRelation = null;
	public static Property member = null;
	public static Property memberList = null;
	public static Property broadMatch = null;
	public static Property closeMatch = null;
	public static Property exactMatch = null;
	public static Property mappingRelation = null;
	public static Property narrowMatch = null;
	public static Property relatedMatch = null;
	public static Property status = null;
	public static Property hasValue = null;
	public static Property hasProperty = null;
	public static Property hasPropertyType = null;

	static {
		try {
			// URI
			setUri(ThesauformConfiguration.skos);
			// Properties
			inScheme = getCProperty("inScheme");
			hasTopConcept = getCProperty("hasTopConcept");
			topConceptOf = getCProperty("topConceptOf");
			altLabel = getCProperty("altLabel");
			hiddenLabel = getCProperty("hiddenLabel");
			prefLabel = getCProperty("prefLabel");
			notation = getCProperty("notation");
			changeNote = getCProperty("changeNote");
			definition = getCProperty("definition");
			editorialNote = getCProperty("editorialNote");
			example = getCProperty("example");
			historyNote = getCProperty("historyNote");
			note = getCProperty("note");
			scopeNote = getCProperty("scopeNote");
			broader = getCProperty("broader");
			broaderTransitive = getCProperty("broaderTransitive");
			narrower = getCProperty("narrower");
			narrowerTransitive = getCProperty("narrowerTransitive");
			related = getCProperty("related");
			semanticRelation = getCProperty("semanticRelation");
			member = getCProperty("member");
			memberList = getCProperty("memberList");
			broadMatch = getCProperty("broadMatch");
			closeMatch = getCProperty("closeMatch");
			exactMatch = getCProperty("exactMatch");
			mappingRelation = getCProperty("mappingRelation");
			narrowMatch = getCProperty("narrowMatch");
			relatedMatch = getCProperty("relatedMatch");
			// Resources
			Concept = getCResource("Concept");
			ConceptScheme = getCResource("ConceptScheme");
			Collection = getCResource("Collection");
			OrderedCollection = getCResource("OrderedCollection");
		} catch (Exception e) {
			ThesauformConfiguration.thesauform_logger.error(String.class.getName() + ": failure" + e.getMessage());
		}
	}
}
