package thesauform.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import thesauform.beans.Person;
import thesauform.model.Format;
import thesauform.model.SkosTraitModel;
import thesauform.model.ThesauformConfiguration;

import com.hp.hpl.jena.rdf.model.Resource;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author baptiste
 */
@WebServlet("/annotationFather")
public class FatherAnnotation extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9038555838019691705L;

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
		// test if a session is initialized else it is visualization
		HttpSession session = request.getSession(false);
		if (request.getParameter(ThesauformConfiguration.GET_VIZ) != null
				&& request.getParameter(ThesauformConfiguration.GET_VIZ).equals("1")) {
			// set public file if exists
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
									getServletContext().getRealPath(ThesauformConfiguration.data_file));
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
					PrintWriter pw = response.getWriter();
					Resource concept = traitModel.getResource(Format.formatName(traitName));
					if (traitModel.getConceptFromSyn(Format.formatName(traitName)) != null) {
						concept = traitModel.getConceptFromSyn(Format.formatName(traitName));
					}
					String var = "[";
					Resource pere = traitModel.getSuperclass(concept);
					var = var + "\"#" + pere.getLocalName() + "\",";
					while (pere != null) {
						pere = traitModel.getSuperclass(pere);
						if (pere != null) {
							var = var + "\"#" + pere.getLocalName() + "\",";
						}
					}
					var = var.substring(0, var.length() - 1);
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
			// redirect to logging page
			request.setAttribute("my_errors", errors);
			this.getServletContext().getRequestDispatcher(ThesauformConfiguration.VUE_FAILED).forward(request,
					response);
		}
	}
}