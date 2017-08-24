package thesauform.controller.administration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import thesauform.model.vocabularies.ChangeVoc;
import thesauform.model.vocabularies.SkosVoc;
import thesauform.model.vocabularies.SkosXLVoc;
import thesauform.model.vocabularies.TraitVocTemp;

/**
 * Servlet implementation class ExportFile
 */
@WebServlet("/administration/validateData")
public class DataValidation extends HttpServlet {

	private static final long serialVersionUID = -942389947664754440L;
	
	public static final String VUE_SUCCESS = "/WEB-INF/scripts/dataValidation.jsp";
	private static final String GET_PARAMETER = "trait";
	private static final String ERROR_PARAMETER = "parameter";
	private static final String ERROR_MESSAGE_PARAMETER = "parameter " + GET_PARAMETER + " empty";

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//print all concept with vote, and all concept without vote
		// manage errors messages
		Map<String, String> errors = new HashMap<String,String>();
		//vote notation for definition
		Map<String,List<String>> voteMap =  new HashMap<String,List<String>>();
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
					String traitName = request.getParameter(GET_PARAMETER);
					try {
						if (traitName == null) {
							throw new Exception(ERROR_MESSAGE_PARAMETER);
						}
					} catch (Exception e) {
						errors.put(ERROR_PARAMETER, e.getMessage());
					}
					Resource concept = traitModel.getResource(Format.formatName(traitName));
					if (concept != null) {
						String definition = traitModel.getValue(traitModel.getDefinition(concept));
						// get current reference linked to definition
						String reference  = traitModel.getValue(traitModel.getReference(traitModel.getDefinition(concept)));
						// test if not empty
						if (definition != null && !definition.isEmpty()) {
							if(reference != null && !reference.isEmpty()) {
								voteMap.put(definition + "__" + reference, traitModel.getVote(concept, SkosVoc.definition, definition + "__" + reference));
							}
							else {
								voteMap.put(definition, traitModel.getVote(concept, SkosVoc.definition, definition));
							}
						}
						// get all update properties vote
						Map<String, List<String>> updateMap = traitModel.getAnnotation(Format.formatName(traitName), "update");
						Iterator<Entry<String, List<String>>> updateIt = updateMap.entrySet().iterator();
						// test if the concept is updated
						if (updateIt.hasNext()) {
							// for each property
							while (updateIt.hasNext()) {
								// update property value/list
								Map<String, Map<String, Integer>> propertyVoteMap = new HashMap<>();
								// get the properties lists
								Entry<String, List<String>> updatePair = updateIt.next();
								String property = updatePair.getKey();
								List<String> valueList = (List<String>) updatePair.getValue();
								Iterator<String> valueIt = valueList.iterator();
								while (valueIt.hasNext()) {
									// get property value
									String value = valueIt.next();
									if(property == "definition"){
										voteMap.put(value,traitModel.getVote(concept, SkosVoc.definition, value));
									}
								}
							}
						} else {

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
