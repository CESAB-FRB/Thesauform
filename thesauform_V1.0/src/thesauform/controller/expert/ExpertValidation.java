package thesauform.controller.expert;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;

import java.io.IOException;

import thesauform.beans.AnnotationConcept;
import thesauform.beans.Person;
import thesauform.beans.TraitConceptVote;
import thesauform.model.Format;
import thesauform.model.SkosTraitModel;
import thesauform.model.ThesauformConfiguration;
import thesauform.model.vocabularies.ChangeVoc;
import thesauform.model.vocabularies.SkosVoc;
import thesauform.model.vocabularies.SkosXLVoc;
import thesauform.model.vocabularies.TraitVocTemp;

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


/*
 * TODO definition selection should be done with reference
 * @Patch1: definition with reference would be on form def__ref for annotation : check if ref associated -> for selection, for the view, for printing
 */


/**
 * Servlet implementation class servletExpertValidation
 */
@WebServlet("/expert/validation")
public class ExpertValidation extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4569393576368232892L;

	public static final String VUE_SUCCESS = "/WEB-INF/scripts/expertValidation.jsp";
	private static final String COMMENT_NAME = "Comment";
	private static final String GET_PARAMETER = "trait";
	private static final String ERROR_PARAMETER = "parameter";
	private static final String ERROR_MESSAGE_PARAMETER = "parameter " + GET_PARAMETER + " empty";
	private static final String ERROR_CONCEPT = "concept";
	private static final String ERROR_MESSAGE_CONCEPT = "Cannot find trait in model";
	private static final String ERROR_URI = "uri";
	private static final String EMPTY_COMMENT = "No comment";
	private static final String ERROR_SYNONYMS = "synonyms";
	private static final String EMPTY_SYNONYM = "No synonym";
	private static final String ERROR_RELATEDS = "relateds";
	private static final String EMPTY_RELATED = "No related concept";	
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// manage errors messages
		Map<String, String> errors = new HashMap<>();
		// trait model
		SkosTraitModel traitModel = null;
		// test if a session is initialized
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
					//// do treatment
					// bean for the view
					TraitConceptVote myTraitVote = new TraitConceptVote();
					// insert vote note
					Integer insertVote = null;
					// existence of delete
					List<String> deleteList = null;
					// delete vote note
					Integer deleteVote = null;
					Map<String, Map<String, Integer>> nameVoteMap = new HashMap<String, Map<String, Integer>>();
					Map<String, Map<String, Integer>> definitionVoteMap = new HashMap<String, Map<String, Integer>>();
					Map<String, Map<String, Integer>> referenceVoteMap = new HashMap<String, Map<String, Integer>>();
					Map<String, Map<String, Integer>> abbreviationVoteMap = new HashMap<String, Map<String, Integer>>();
					Map<String, Map<String, Integer>> categoryVoteMap = new HashMap<String, Map<String, Integer>>();
					Map<String, Map<String, Integer>> unitVoteMap = new HashMap<String, Map<String, Integer>>();
					Map<String, Map<String, Integer>> synonymVoteMap = new HashMap<String, Map<String, Integer>>();
					Map<String, Map<String, Integer>> relatedVoteMap = new HashMap<String, Map<String, Integer>>();
					List<AnnotationConcept> commentList = new ArrayList<AnnotationConcept>();
					// get parameter
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
						// set trait name
						try {
							myTraitVote.setUri(traitName);
						} catch (Exception e) {
							errors.put(ERROR_URI, e.getMessage() + " for " + traitName);
						}
						//// get insert annotation if exists
						// get all insert members
						Map<String, List<String>> insertMap = traitModel.getAnnotation(Format.formatName(traitName),
								"insert");
						Iterator<Entry<String, List<String>>> insertIt = insertMap.entrySet().iterator();
						try {
							// test if the concept is inserted
							if (insertIt.hasNext()) {
								myTraitVote.setIsInserted(true);
								// get vote note by user
								insertVote = traitModel.countVote(concept, ChangeVoc.insert, user.getName(), "insert");
								myTraitVote.setNbInsertVote(insertVote);
							} else {
								myTraitVote.setIsInserted(false);
							}
						} catch (Exception e) {

						}
						//// get delete annotation with people if any
						// get all delete members
						deleteList = traitModel.getAllDelete(traitName);
						Iterator<String> deleteIt = deleteList.iterator();
						try {
							// test if the concept is deleted
							if (deleteIt.hasNext()) {
								// set the list of contributors
								myTraitVote.setDeleteList(deleteList);
								// get vote note by user
								deleteVote = traitModel.countVote(concept, ChangeVoc.delete, user.getName(), "delete");
								myTraitVote.setNbDeleteVote(deleteVote);
							} else {
							}
						} catch (Exception e) {

						}
						//// get update annotation
						// get all update properties vote
						Map<String, List<String>> updateMap = traitModel.getAnnotation(Format.formatName(traitName),
								"update");
						Iterator<Entry<String, List<String>>> updateIt = updateMap.entrySet().iterator();
						// set current name
						try {
							String name = traitModel.getLabelLiteralForm(traitModel.getPrefLabel(concept));
							// test if not empty
							if (name != null && !name.isEmpty()) {
								Integer propertyVote = traitModel.countVote(concept, SkosXLVoc.prefLabel, user.getName(), name);
								Map<String, Integer> voteMapTmp = new HashMap<String, Integer>();
								voteMapTmp.put(name, propertyVote);
								nameVoteMap.put("current", voteMapTmp);
							}
						} catch (Exception e) {
						}
						// get current definition
						try {
							String definition = traitModel.getValue(traitModel.getDefinition(concept));
							// test if not empty
							if (definition != null && !definition.isEmpty()) {
								Integer propertyVote = traitModel.countVote(concept, SkosVoc.definition, user.getName(), definition);
								Map<String, Integer> voteMapTmp = new HashMap<String, Integer>();
								voteMapTmp.put(definition, propertyVote);
								definitionVoteMap.put("current", voteMapTmp);
							}
						} catch (Exception e) {
						}
						// get current reference
						try {
							String reference = traitModel
									.getValue(traitModel.getReference(traitModel.getDefinition(concept)));
							// test if not empty
							if (reference != null && !reference.isEmpty()) {
								Integer propertyVote = traitModel.countVote(concept, TraitVocTemp.reference, user.getName(), reference);
								Map<String, Integer> voteMapTmp = new HashMap<String, Integer>();
								voteMapTmp.put(reference, propertyVote);
								referenceVoteMap.put("current", voteMapTmp);
							}
						} catch (Exception e) {
						}
						// get abbreviation
						try {
							String abbreviation = traitModel
									.getLabelLiteralForm(traitModel.getAbbreviation(traitModel.getPrefLabel(concept)));
							// test if not empty
							if (abbreviation != null && !abbreviation.isEmpty()) {
								Integer propertyVote = traitModel.countVote(concept, TraitVocTemp.abbreviation, user.getName(), 
										abbreviation);
								Map<String, Integer> voteMapTmp = new HashMap<String, Integer>();
								voteMapTmp.put(abbreviation, propertyVote);
								abbreviationVoteMap.put("current", voteMapTmp);
							}
						} catch (Exception e) {
						}
						// get unit
						try {
							String unit = traitModel.getValue(traitModel.getUnit(concept));
							// test if not empty
							if (unit != null && !unit.isEmpty()) {
								Integer propertyVote = traitModel.countVote(concept, TraitVocTemp.prefUnit, user.getName(), unit);
								Map<String, Integer> voteMapTmp = new HashMap<String, Integer>();
								voteMapTmp.put(unit, propertyVote);
								unitVoteMap.put("current", voteMapTmp);
							}
						} catch (Exception e) {
						}
						// get parent/category
						try {
							StmtIterator parentIt = traitModel.getAllParent(concept);
							if (parentIt.hasNext()) {
								Map<String, Integer> voteMapTmp = new HashMap<String, Integer>();
								while (parentIt.hasNext()) {
									Statement st = parentIt.next();
									Resource parent = st.getObject().as(Resource.class);
									String category = parent.getLocalName();
									// test if not empty
									if (category != null && !category.isEmpty()) {
										Integer propertyVote = traitModel.countVote(concept, SkosVoc.broaderTransitive, user.getName(), 
												category);
										voteMapTmp.put(category, propertyVote);
									}
								}
								categoryVoteMap.put("current", voteMapTmp);
							}
						} catch (Exception e) {
						}
						// get comments
						try {
							// get all comments in a list
							StmtIterator commentRawList = traitModel.getComment(concept);
							// test if there is at least one comment
							if (commentRawList.hasNext()) {
								Integer cpt = 0;
								// treat each comment
								while (commentRawList.hasNext()) {
									cpt++;
									// create AnnotationConcept object
									AnnotationConcept myAnnotationTmp = new AnnotationConcept();
									// get comment model object
									Resource commentRawObject = commentRawList.next().getObject().as(Resource.class);
									myAnnotationTmp.setProperty(COMMENT_NAME + cpt);
									myAnnotationTmp.setCreator(commentRawObject.listProperties(DC.creator).next()
											.getObject().as(Resource.class).listProperties(FOAF.name).next().getObject()
											.asNode().getLiteralLexicalForm());
									myAnnotationTmp.setValue(Format.printDef(commentRawObject.listProperties(RDF.value)
											.next().getObject().asNode().getLiteralLexicalForm()));
									commentList.add(myAnnotationTmp);
								}
								// set bean property
								myTraitVote.setCommentList(commentList);
							} else {
								throw new Exception(EMPTY_COMMENT);
							}
						} catch (Exception e) {
						}
						// get all synonyms
						try {
							StmtIterator synonymIt = traitModel.getAllAltLabel(concept);
							if (synonymIt.hasNext()) {
								Map<String, Integer> voteMapTmp = new HashMap<String, Integer>();
								while (synonymIt.hasNext()) {
									Statement st = synonymIt.next();
									Resource AltLabel = st.getObject().as(Resource.class);
									String synonym = traitModel.getLabelLiteralForm(AltLabel);
									Integer propertyVote = traitModel.countVote(concept, SkosXLVoc.altLabel, user.getName(), synonym);
									voteMapTmp.put(synonym, propertyVote);
								}
								synonymVoteMap.put("proposed", voteMapTmp);
								// set bean property
								myTraitVote.setSynonymList(synonymVoteMap);
							} else {
								throw new Exception(EMPTY_SYNONYM);
							}
						} catch (Exception e) {
							errors.put(ERROR_SYNONYMS, e.getMessage() + " for " + traitName);
						}
						// get all related
						try {
							StmtIterator RelatedIt = traitModel.getAllRelated(concept);
							if (RelatedIt.hasNext()) {
								Map<String, Integer> voteMapTmp = new HashMap<String, Integer>();
								while (RelatedIt.hasNext()) {
									Statement st = RelatedIt.next();
									Resource Related = st.getObject().as(Resource.class);
									String related = traitModel.getLabelLiteralForm(traitModel.getPrefLabel(Related));
									Integer propertyVote = traitModel.countVote(concept, SkosVoc.related, user.getName(), related);
									voteMapTmp.put(related, propertyVote);
								}
								// set bean property
								myTraitVote.setRelatedList(relatedVoteMap);
								relatedVoteMap.put("proposed", voteMapTmp);
							} else {
								throw new Exception(EMPTY_RELATED);
							}
						} catch (Exception e) {
							errors.put(ERROR_RELATEDS, e.getMessage() + " for " + traitName);
						}
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
								try {
									Map<String, Integer> voteMapTmp = new HashMap<String, Integer>();
									while (valueIt.hasNext()) {
										// get property value
										String value = valueIt.next();
										// get vote note by user for each value
										Integer propertyVote = 0;
										switch (property) {
										case "name":
											propertyVote = traitModel.countVote(concept, SkosXLVoc.prefLabel, user.getName(), value);
											break;
										case "unit":
											propertyVote = traitModel.countVote(concept, TraitVocTemp.prefUnit, user.getName(), value);
											break;
										case "reference":
											propertyVote = traitModel.countVote(concept, TraitVocTemp.reference, user.getName(), value);
											break;
										case "definition":
											propertyVote = traitModel.countVote(concept, SkosVoc.definition, user.getName(), value);
											break;
										case "abbreviation":
											propertyVote = traitModel.countVote(concept, TraitVocTemp.abbreviation, user.getName(), 
													value);
											break;
										case "category":
											propertyVote = traitModel.countVote(concept, SkosVoc.broaderTransitive, user.getName(), 
													value);
											break;
										default:
											propertyVoteMap.put("proposed", voteMapTmp);
											propertyVote = traitModel.countVote(concept, ChangeVoc.update, user.getName(), value);
											break;
										}
										voteMapTmp.put(value, propertyVote);
									}
									switch (property) {
									case "name":
										nameVoteMap.put("proposed", voteMapTmp);
										propertyVoteMap = nameVoteMap;
										break;
									case "unit":
										unitVoteMap.put("proposed", voteMapTmp);
										propertyVoteMap = unitVoteMap;
										break;
									case "reference":
										referenceVoteMap.put("proposed", voteMapTmp);
										propertyVoteMap = referenceVoteMap;
										break;
									case "definition":
										definitionVoteMap.put("proposed", voteMapTmp);
										propertyVoteMap = definitionVoteMap;
										break;
									case "abbreviation":
										abbreviationVoteMap.put("proposed", voteMapTmp);
										propertyVoteMap = abbreviationVoteMap;
										break;
									case "category":
										categoryVoteMap.put("proposed", voteMapTmp);
										propertyVoteMap = categoryVoteMap;
										break;
									default:
										propertyVoteMap.put("proposed", voteMapTmp);
										myTraitVote.setPropertyList(property, propertyVoteMap);
										break;
									}
								} catch (Exception e) {
									// @TODO manage exception
									System.out.println(e.getMessage());
								}
							}
						} else {

						}

						// set bean vote
						try {
							String property = "name";
							Map<String, Map<String, Integer>> propertyVoteMap = nameVoteMap;
							myTraitVote.setPropertyList(property, propertyVoteMap);
						} catch (Exception e) {
							System.out.println(e.getMessage());

						}
						try {
							String property = "unit";
							Map<String, Map<String, Integer>> propertyVoteMap = unitVoteMap;
							myTraitVote.setPropertyList(property, propertyVoteMap);
						} catch (Exception e) {
							System.out.println(e.getMessage());
						}
						try {
							String property = "reference";
							Map<String, Map<String, Integer>> propertyVoteMap = referenceVoteMap;
							myTraitVote.setPropertyList(property, propertyVoteMap);
						} catch (Exception e) {
							System.out.println(e.getMessage());
						}
						try {
							// TODO put reference in definition at the end (ref)
							String property = "definition";
							Map<String, Map<String, Integer>> propertyVoteMap = definitionVoteMap;
							myTraitVote.setPropertyList(property, propertyVoteMap);
						} catch (Exception e) {
							System.out.println(e.getMessage());
						}
						try {
							String property = "abbreviation";
							Map<String, Map<String, Integer>> propertyVoteMap = abbreviationVoteMap;
							myTraitVote.setPropertyList(property, propertyVoteMap);
						} catch (Exception e) {
							System.out.println(e.getMessage());
						}
						try {
							String property = "category";
							Map<String, Map<String, Integer>> propertyVoteMap = categoryVoteMap;
							myTraitVote.setPropertyList(property, propertyVoteMap);
						} catch (Exception e) {
							System.out.println(e.getMessage());

						}
					} else {
						errors.put(ERROR_CONCEPT, ERROR_MESSAGE_CONCEPT + ": " + traitName);
					}
					// set parameter for view
					request.setAttribute("myTraitVote", myTraitVote);
					request.setAttribute("user", user.getName());
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
