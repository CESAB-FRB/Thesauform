package thesauform.controller;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

import thesauform.model.SkosTraitModel;
import thesauform.model.ThesauformConfiguration;
import thesauform.model.vocabularies.RefVoc;

@WebServlet("/references")
public class ReferencesVisualization extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8707962351564987836L;
	
	private static final String VUE_SUCCESS = "/WEB-INF/scripts/referencesVisualization.jsp";
	public static final String GET_VIZ = "viz";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// set visualization request
		request.setAttribute(GET_VIZ, "1");
		// trait model
		SkosTraitModel traitModel = null;
		// set public file
		traitModel = new SkosTraitModel(getServletContext().getRealPath(ThesauformConfiguration.public_data_file));
		// Treatment
		if (traitModel != null) {
			try {
				StmtIterator it = traitModel.SimpleSelector(null, RDF.type, RefVoc.Reference);
				if (it.hasNext()) {
					Set<String> myReferenceList = new HashSet<String>();
					while (it.hasNext()) {
						Resource ref = it.next().getSubject();
						String refValue = ref.listProperties(RDF.value).next().getObject().asNode()
								.getLiteralLexicalForm();
						myReferenceList.add(refValue);
					}
					request.setAttribute("my_ref", myReferenceList);
					traitModel.close();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		this.getServletContext().getRequestDispatcher(VUE_SUCCESS).forward(request, response);
	}
}
