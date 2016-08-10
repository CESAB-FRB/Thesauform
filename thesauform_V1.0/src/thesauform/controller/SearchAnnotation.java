package thesauform.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

import thesauform.beans.Person;
import thesauform.model.SkosTraitModel;
import thesauform.model.ThesauformConfiguration;
import thesauform.model.vocabularies.SkosVoc;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class search
 */
@WebServlet("/annotationSearch")
public class SearchAnnotation extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5075477172307426542L;

	public static final String GET_PARAMETER = "trait";
	public static final String ERROR_PARAMETER = "parameter";
	public static final String ERROR_MESSAGE_PARAMETER = "parameter " + GET_PARAMETER + " empty";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// manage errors messages
		Map<String, String> errors = new HashMap<>();
		// trait model
		SkosTraitModel traitModel = null;
		// test if a session is initialized or it is visualization
		HttpSession session = request.getSession(false);
		if (request.getParameter(ThesauformConfiguration.GET_VIZ) != null
				&& request.getParameter(ThesauformConfiguration.GET_VIZ).equals("1")) {
			// set public file
			if(!ThesauformConfiguration.public_data_file.isEmpty()) {
				if(ThesauformConfiguration.database) {
					traitModel = new SkosTraitModel(ThesauformConfiguration.public_data_file);
				}
				else {
					traitModel = new SkosTraitModel(getServletContext().getRealPath(ThesauformConfiguration.public_data_file));
				}
			}
		} else {
			if (session != null) {
				if (session.getAttribute(ThesauformConfiguration.USR_SESSION) instanceof Person) {
					Person user = (Person) session.getAttribute(ThesauformConfiguration.USR_SESSION);
					boolean authentificationStatus = user.getAuthenticated();
					if (authentificationStatus) {
						// set protected file
						if(ThesauformConfiguration.database) {
							traitModel = new SkosTraitModel(ThesauformConfiguration.data_file);
						}
						else {
							traitModel = new SkosTraitModel(getServletContext().getRealPath(ThesauformConfiguration.data_file));
						}
					} else {
						if (request.getParameter(ThesauformConfiguration.GET_VIZ) != null
								&& request.getParameter(ThesauformConfiguration.GET_VIZ).equals("1")) {
							// set public file
							traitModel = new SkosTraitModel(
									getServletContext().getRealPath(ThesauformConfiguration.public_data_file));
						} else {
							// re-authenticate
							this.getServletContext().getRequestDispatcher(ThesauformConfiguration.VUE_FAILED)
									.forward(request, response);
						}
					}
				}
			}
		}
		// Treatment
		if (traitModel != null) {
			String traitName = request.getParameter(GET_PARAMETER);
			try {
				if (traitName != null && !traitName.isEmpty()) {
					//// interrogate model
					// create model
					PrintWriter pw = response.getWriter();
					StmtIterator itTrait = traitModel.SimpleSelector(null, RDF.type, SkosVoc.Concept);
					String var = "[";
					while (itTrait.hasNext()) {
						Resource trait = itTrait.next().getSubject();
						String traitLabel = traitModel.getLabelLiteralForm(traitModel.getPrefLabel(trait));
						if (traitLabel.toLowerCase().startsWith(traitName.toLowerCase())) {
							var = var + "\"" + traitLabel + "\" , ";
						}
					}
					var = var.substring(0, var.length() - 2);
					var = var + "]";
					traitModel.close();
					pw.println(var);
				} else {
					throw new Exception(ERROR_MESSAGE_PARAMETER);
				}
			} catch (Exception e) {
				errors.put(ERROR_PARAMETER, e.getMessage());
			}
		} else {
			request.setAttribute("my_errors", errors);
			// redirect to logging page
			this.getServletContext().getRequestDispatcher(ThesauformConfiguration.VUE_FAILED).forward(request,
					response);
		}
	}
}
