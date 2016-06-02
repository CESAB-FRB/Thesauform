package thesauform.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

import thesauform.beans.TraitConcept;
import thesauform.model.SkosTraitModel;
import thesauform.model.ThesauformConfiguration;
import thesauform.model.vocabularies.SkosVoc;

@WebServlet("/index")
public class IndexVisualization extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8568042009126416402L;

	private static final String VUE_SUCCESS = "/WEB-INF/scripts/indexVisualization.jsp";
	public static final String GET_VIZ = "viz";
	public static final String ERROR_LIST = "list";
	public static final String EMPTY_LIST = "No list of concepts";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// manage errors messages
		Map<String, String> errors = new HashMap<>();
		// set visualization request
		request.setAttribute(GET_VIZ, "1");
		// trait model
		SkosTraitModel traitModel = null;
		// set public file
		traitModel = new SkosTraitModel(getServletContext().getRealPath(ThesauformConfiguration.public_data_file));
		// treatment
		if (traitModel != null) {
			// TODO create object trait in separating code into model
			TraitConcept myTrait = new TraitConcept();
			//// query model
			// get all trait concept
			try {
				StmtIterator it = traitModel.SimpleSelector(null, RDF.type, SkosVoc.Concept);
				if (it.hasNext()) {
					List<TraitConcept> myConceptList = new ArrayList<>();
					while (it.hasNext()) {
						TraitConcept myTraitTmp = new TraitConcept();
						Resource concept = it.next().getSubject();
						myTraitTmp.setName(traitModel.getLabelLiteralForm(traitModel.getPrefLabel(concept)));
						myConceptList.add(myTraitTmp);
					}
					myTrait.setSonsList(myConceptList);
				} else {
					throw new Exception(EMPTY_LIST);
				}
			} catch (Exception e) {
				errors.put(ERROR_LIST, e.getMessage() + " for Model");
			}
			request.setAttribute("my_trait", myTrait);
			request.setAttribute("my_errors", errors);
			traitModel.close();
		}
		this.getServletContext().getRequestDispatcher(VUE_SUCCESS).forward(request, response);
	}
}
