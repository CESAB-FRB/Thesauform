package thesauform.controller.expert;

import java.io.IOException;
import java.io.PrintWriter;

import thesauform.beans.Person;
import thesauform.model.SkosTraitModel;
import thesauform.model.ThesauformConfiguration;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/*
 * TODO definition selection should be done with reference
 * @Patch1: definition with reference would be on form def__ref for annotation : check if ref associated -> for selection, for the view, for printing
 */


/**
 * Servlet implementation class CountVote
 */
@WebServlet("/expert/count")
public class CountVote extends HttpServlet {


	/**
	 * count vote by concept
	 */
	private static final long serialVersionUID = 4546437920726310685L;
	private static final String GET_PARAMETER = "concept";
	private static final String ERROR_PARAMETER = "parameter";
	private static final String ERROR_MESSAGE_PARAMETER = "parameter " + GET_PARAMETER + " empty";
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SkosTraitModel traitModel = null;
		// manage errors messages
		Map<String, String> errors = new HashMap<>();
		//count total vote number per person for the concept
		Integer countNb = 0;
		// test if a session is initialized
		HttpSession session = request.getSession(false);
		if (session != null) {
			if (session.getAttribute(ThesauformConfiguration.USR_SESSION) instanceof Person) {
				Person user = (Person) session.getAttribute(ThesauformConfiguration.USR_SESSION);
				boolean authentificationStatus = user.getAuthenticated();
				if (authentificationStatus) {
					// do treatment
					try {
						// test parameters
						String concept = request.getParameter(GET_PARAMETER);
						if (concept == null || concept.isEmpty()) {
							errors.put(ERROR_PARAMETER, ERROR_MESSAGE_PARAMETER);
							throw new Exception(ERROR_MESSAGE_PARAMETER);
						}
						if(ThesauformConfiguration.database) {
							traitModel = new SkosTraitModel(ThesauformConfiguration.data_file);
						}
						else {
							traitModel = new SkosTraitModel(getServletContext().getRealPath(ThesauformConfiguration.data_file));
						}
						// return vote number
						countNb = traitModel.countVotePerson(traitModel.getResource(concept),user.getName());
						response.setContentType("application/json");
						PrintWriter writer = response.getWriter();
						writer.write("{\"nb\":\"" + countNb + "\"}");
						writer.close();
						// clean memory
						traitModel.close();
					} catch (Exception ex) {
						response.setContentType("application/json");
						PrintWriter writer = response.getWriter();
						ex.printStackTrace();
						writer.write("{\"error\":\"1\", \"message\":\"" + ex.toString() + "\"}");
						writer.close();
					}
				} else {
					// re-authenticate
					this.getServletContext().getRequestDispatcher(ThesauformConfiguration.VUE_FAILED).forward(request,
							response);
				}
			} else {
				// re-authenticate
				this.getServletContext().getRequestDispatcher(ThesauformConfiguration.VUE_FAILED).forward(request,
						response);
			}
		} else {
			// re-authenticate
			this.getServletContext().getRequestDispatcher(ThesauformConfiguration.VUE_FAILED).forward(request,
					response);
		}
	}

}
