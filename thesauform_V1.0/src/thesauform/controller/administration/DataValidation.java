package thesauform.controller.administration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import thesauform.beans.Person;
import thesauform.model.Format;
import thesauform.model.SkosTraitModel;
import thesauform.model.ThesauformConfiguration;
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
	private static final String GET_PARAMETER = "concept";
	private static final String ERROR_PARAMETER = "parameter";
	private static final String ERROR_PROPERTY = "property";
	private static final String ERROR_MESSAGE_PARAMETER = "parameter " + GET_PARAMETER + " empty";
	private static final String ERROR_MESSAGE_PROPERTY= "property not managed";

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//print a concept with vote and comment for each propertie
		//TODO create ajax call for validation
		//TODO draw line between category
		// manage errors messages
		Map<String, String> errors = new HashMap<String,String>();
		//vote notation for all properties managed
		Map<String, Map<String, List<String>>> voteMap =  new LinkedHashMap<String,Map<String, List<String>>>();
		//name
		Map<String, List<String>> nameVoteMap =  new LinkedHashMap<String, List<String>>();
		//definition
		Map<String, List<String>> definitionVoteMap =  new LinkedHashMap<String, List<String>>();
		//abbreviation
		Map<String, List<String>> abbreviationVoteMap =  new LinkedHashMap<String, List<String>>();
		//synonym
		Map<String, List<String>> synonymVoteMap =  new LinkedHashMap<String, List<String>>();
		//related
		Map<String, List<String>> relatedVoteMap =  new LinkedHashMap<String, List<String>>();
		//category
		Map<String, List<String>> categoryVoteMap =  new LinkedHashMap<String, List<String>>();
		//unit
		Map<String, List<String>> unitVoteMap =  new LinkedHashMap<String, List<String>>();
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
					//get concept name
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
						////get each property to retrieve vote and comment
						///name
						String name = traitModel.getLabelLiteralForm(traitModel.getPrefLabel(concept));
						nameVoteMap.put(name, traitModel.getVote(concept, SkosXLVoc.prefLabel, name));
						///definition
						String definition = traitModel.getValue(traitModel.getDefinition(concept));
						// get current reference linked to definition
						String reference  = traitModel.getValue(traitModel.getReference(traitModel.getDefinition(concept)));
						// test if not empty
						if (definition != null && !definition.isEmpty()) {
							if(reference != null && !reference.isEmpty()) {
								definitionVoteMap.put(definition + "__" + reference, traitModel.getVote(concept, SkosVoc.definition, definition + "__" + reference));
							}
							else {
								definitionVoteMap.put(definition, traitModel.getVote(concept, SkosVoc.definition, definition));
							}
						}
						///abbreviation
						String abbreviation = traitModel
								.getLabelLiteralForm(traitModel.getAbbreviation(traitModel.getPrefLabel(concept)));
						// test if not empty
						if (abbreviation != null && !abbreviation.isEmpty()) {
							abbreviationVoteMap.put(abbreviation, traitModel.getVote(concept, TraitVocTemp.abbreviation, abbreviation));
						}
						///synonym
						StmtIterator synonymIt = traitModel.getAllValidatedAltLabel(concept);
						// test if not empty
						if (synonymIt.hasNext()) {
							while (synonymIt.hasNext()) {
								Statement st = synonymIt.next();
								String synonym = st.getObject().asNode().getLiteralLexicalForm();
								synonymVoteMap.put(synonym, traitModel.getVote(concept, SkosXLVoc.altLabel, synonym));
							}
						}
						///category
						StmtIterator parentIt = traitModel.getAllParent(concept);
						if (parentIt.hasNext()) {
							while (parentIt.hasNext()) {
								Statement st = parentIt.next();
								Resource parent = st.getObject().as(Resource.class);
								String category = parent.getLocalName();
								categoryVoteMap.put(category, traitModel.getVote(concept, SkosVoc.altLabel, category));
							}
						}
						///unit
						String unit = traitModel.getValue(traitModel.getUnit(concept));
						// test if not empty
						if (unit != null && !unit.isEmpty()) {
							unitVoteMap.put(unit, traitModel.getVote(concept, TraitVocTemp.prefUnit, unit));
						}
						// get all related
						StmtIterator relatedIt = traitModel.getAllValidatedRelated(concept);
						if(relatedIt.hasNext()) {
							while (relatedIt.hasNext()) {
									Statement st = relatedIt.next();
									Resource Related = st.getObject().as(Resource.class);
									String related = traitModel.getLabelLiteralForm(traitModel.getPrefLabel(Related));
									relatedVoteMap.put(related, traitModel.getVote(concept, SkosVoc.related, related));
							}
						}
						// get all update properties vote
						try {
							Map<String, List<String>> updateMap = traitModel.getAnnotation(Format.formatName(traitName), "update");
							Iterator<Entry<String, List<String>>> updateIt = updateMap.entrySet().iterator();
							// test if the concept is updated
							// for each property
							while (updateIt.hasNext()) {
								// get the properties lists
								Entry<String, List<String>> updatePair = updateIt.next();
								String property = updatePair.getKey();
								List<String> valueList = (List<String>) updatePair.getValue();
								Iterator<String> valueIt = valueList.iterator();
								while (valueIt.hasNext()) {
									// get property value
									String value = valueIt.next();
									//add a tag to know validated from proposed
									String key_value = value.concat("@prop");
									if(property == "definition"){
										definitionVoteMap.put(key_value,traitModel.getVote(concept, SkosVoc.definition, value));
									}
									else if (property == "name") {
										nameVoteMap.put(key_value,traitModel.getVote(concept, SkosXLVoc.prefLabel, value));
									}
									else if (property == "abbreviation") {
										abbreviationVoteMap.put(key_value,traitModel.getVote(concept, TraitVocTemp.abbreviation, value));
									}
									else if (property == "synonym") {
										synonymVoteMap.put(key_value,traitModel.getVote(concept, SkosXLVoc.altLabel, value));
									}
									else if (property == "category") {
										categoryVoteMap.put(key_value,traitModel.getVote(concept, SkosVoc.broaderTransitive, value));
									}
									else if (property == "unit") {
										unitVoteMap.put(key_value,traitModel.getVote(concept, TraitVocTemp.prefUnit, value));
									}
									else if (property == "related") {
										relatedVoteMap.put(key_value,traitModel.getVote(concept, SkosVoc.related, value));
									}
									else {
										throw new Exception(ERROR_MESSAGE_PROPERTY + ":" + property);
									}
								}
							}
						} catch (Exception e) {
							System.out.println(e.getMessage());
							errors.put(ERROR_PROPERTY, e.getMessage());
						}
						voteMap.put("name",nameVoteMap);
						voteMap.put("definition",definitionVoteMap);
						voteMap.put("unit",unitVoteMap);
						voteMap.put("abbreviation",abbreviationVoteMap);
						voteMap.put("category",categoryVoteMap);
						voteMap.put("synonym",synonymVoteMap);
						voteMap.put("related",relatedVoteMap);
					}
					request.setAttribute("concept", traitName);
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
