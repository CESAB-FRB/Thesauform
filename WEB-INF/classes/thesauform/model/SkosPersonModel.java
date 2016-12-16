package thesauform.model;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;

public class SkosPersonModel extends SkosModel {

	private static String URI_MISSING = "URI not defined";

	public SkosPersonModel() {
		super();
		try {
			if (ThesauformConfiguration.person_uri != null && !ThesauformConfiguration.person_uri.isEmpty()) {
				setUri(ThesauformConfiguration.person_uri);
				m.setNsPrefix(ThesauformConfiguration.PERSON_PFX, ThesauformConfiguration.person_uri);
			} else {
				throw new Exception(URI_MISSING);
			}
		} catch (Exception e) {
			ThesauformConfiguration.thesauform_logger.error(String.class.getName()+ ": failure" + e.getMessage());
		}
	}

	public SkosPersonModel(String file) {
		super(file);
		try {
			if (ThesauformConfiguration.person_uri != null && !ThesauformConfiguration.person_uri.isEmpty()) {
				setUri(ThesauformConfiguration.person_uri);
			} else {
				throw new Exception(URI_MISSING);
			}
		} catch (Exception e) {
			ThesauformConfiguration.thesauform_logger.error(String.class.getName()+ ": failure" + e.getMessage());
		}
	}

	public boolean deleteUser(String user_name) {
		boolean return_value = false;
		try {
			StmtIterator it = m.listStatements(new SimpleSelector(null, FOAF.name, (RDFNode) null));
			Resource person_res = null;
			while (it.hasNext()) {
				Statement st = it.next();
				if (st.getObject().asNode().getLiteralLexicalForm().equalsIgnoreCase(user_name)) {
					person_res = st.getSubject();
				}
			}
			person_res.removeAll(null);
			return_value = true;
		} catch (Exception e) {
			ThesauformConfiguration.thesauform_logger.error(String.class.getName()+ ": failure" + e.getMessage());
		}
		return (return_value);
	}

}
