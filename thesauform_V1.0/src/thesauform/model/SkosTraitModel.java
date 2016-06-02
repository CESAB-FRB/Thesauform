package thesauform.model;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import thesauform.model.vocabularies.SkosVoc;
import thesauform.model.vocabularies.TraitVoc;

public class SkosTraitModel extends SkosModel {

	private static String URI_MISSING = "URI not defined";

	public SkosTraitModel() {
		super();
		try {
			if (ThesauformConfiguration.term_uri != null && !ThesauformConfiguration.term_uri.isEmpty()) {
				setUri(ThesauformConfiguration.term_uri);
				m.setNsPrefix(ThesauformConfiguration.TRAIT_PFX,
						ThesauformConfiguration.term_uri + ThesauformConfiguration.TRAIT_PFX);
				m.setNsPrefix(ThesauformConfiguration.CHANGE_PFX,
						ThesauformConfiguration.term_uri + ThesauformConfiguration.CHANGE_PFX);
				m.setNsPrefix(ThesauformConfiguration.REF_PFX,
						ThesauformConfiguration.term_uri + ThesauformConfiguration.REF_PFX);
				m.setNsPrefix(ThesauformConfiguration.UNIT_PFX,
						ThesauformConfiguration.term_uri + ThesauformConfiguration.UNIT_PFX);
			} else {
				throw new Exception(URI_MISSING);
			}
		} catch (Exception e) {
			ThesauformConfiguration.thesauform_logger.error(String.class.getName() + ": failure" + e.getMessage());
		}
	}

	public SkosTraitModel(String file) {
		super(file);
		try {
			if (ThesauformConfiguration.term_uri != null && !ThesauformConfiguration.term_uri.isEmpty()) {
				setUri(ThesauformConfiguration.term_uri);
			} else {
				throw new Exception(URI_MISSING);
			}
		} catch (Exception e) {
			ThesauformConfiguration.thesauform_logger.error(String.class.getName() + ": failure" + e.getMessage());
		}
	}

	public Resource createTrait(String name) {
		Resource trait = createResource(name);
		trait.addProperty(RDF.type, TraitVoc.Trait);
		trait.addProperty(RDF.type, SkosVoc.Concept);
		return trait;
	}

}
