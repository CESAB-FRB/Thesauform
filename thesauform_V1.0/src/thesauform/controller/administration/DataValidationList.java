package thesauform.controller.administration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.hp.hpl.jena.rdf.model.Resource;

import thesauform.beans.Person;
import thesauform.model.Format;
import thesauform.model.SkosTraitModel;
import thesauform.model.ThesauformConfiguration;

/**
 * Servlet implementation class DataValidationList
 */
@WebServlet("/administration/validateDataList")
public class DataValidationList extends HttpServlet {

	private static final long serialVersionUID = -942389947664754440L;
	
	public static final String VUE_SUCCESS = "/WEB-INF/scripts/dataValidationList.jsp";

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		////print all concept with vote, and all concept without vote
		//vote number by concept
		Map<String,Integer> voteMap =  new HashMap<String,Integer>();
		// test if a session is initialized
		SkosTraitModel traitModel = null;
		HttpSession session = request.getSession(false);
		if (session != null) {
			if (session.getAttribute(ThesauformConfiguration.USR_SESSION) instanceof Person) {
				Person user = (Person) session.getAttribute(ThesauformConfiguration.USR_SESSION);
				boolean authentificationStatus = user.getAuthenticated();
				if (authentificationStatus) {
					if(ThesauformConfiguration.database) {
						traitModel = new SkosTraitModel(ThesauformConfiguration.data_file);
					}
					else {
						traitModel = new SkosTraitModel(getServletContext().getRealPath(ThesauformConfiguration.data_file));
					}
					// do treatment
					//get all concept
					List<String> conceptList = traitModel.getAllConcept();
					String conceptName = "";
					for (Iterator<String> itConceptList = conceptList.iterator(); itConceptList.hasNext();) {
						conceptName = (String) itConceptList.next();
						Resource concept = traitModel.getResource(Format.formatName(conceptName));
						if (concept != null) {
							Integer countVote = traitModel.countVote(concept);
							voteMap.put(conceptName, countVote);

						}
					}
					request.setAttribute("vote", voteMap);
					this.getServletContext().getRequestDispatcher(VUE_SUCCESS).forward(request, response);
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
