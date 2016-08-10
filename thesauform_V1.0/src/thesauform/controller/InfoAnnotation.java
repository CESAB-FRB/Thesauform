package thesauform.controller;

import com.hp.hpl.jena.rdf.model.NodeIterator;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Arrays;

import thesauform.beans.AnnotationConcept;
import thesauform.beans.Person;
import thesauform.beans.TraitConcept;
import thesauform.model.Format;
import thesauform.model.SkosTraitModel;
import thesauform.model.ThesauformConfiguration;
import thesauform.model.vocabularies.TraitVocTemp;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class infoAnnotation
 */
@WebServlet("/annotationInfo")
public class InfoAnnotation extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4100070118808402066L;
	
	public static final String VUE_SUCCESS = "/WEB-INF/scripts/infoAnnotation.jsp";
	public static final String VUE_SUCCESS_VIZ = "/WEB-INF/scripts/infoVisualization.jsp";
	public static final String COMMENT_NAME = "Comment";
	public static final String ANNOTATION_NAME = "Comment";
	public static final String ERROR_STATUS = "identification failed";
	public static final String GET_PARAMETER = "trait";
	public static final String ERROR_PARAMETER = "parameter";
	public static final String ERROR_MESSAGE_PARAMETER = "parameter " + GET_PARAMETER + " empty";
	public static final String ERROR_CONCEPT = "concept";
	public static final String ERROR_MESSAGE_CONCEPT = "Cannot find trait in model";
	public static final String ERROR_REAL_NAME = "real_name";
	public static final String ERROR_URI = "uri";
	public static final String ERROR_DEFINITION = "definition";
	public static final String ERROR_ABBREVIATION = "abbreviation";
	public static final String ERROR_UNIT = "unit";
	public static final String ERROR_PARENT = "parent";
	public static final String ERROR_COMMENTS = "comments";
	public static final String EMPTY_COMMENT = "No comment";
	public static final String ERROR_ANNOTATIONS = "annotations";
	public static final String EMPTY_ANNOTATION = "No annotation";
	public static final String ERROR_CATEGORIES = "categories";
	public static final String ERROR_SYNONYMS = "synonyms";
	public static final String EMPTY_SYNONYM = "No synonym";
	public static final String ERROR_RELATEDS = "relateds";
	public static final String EMPTY_RELATED = "No related concept";
	public static final String ERROR_SONS = "sons";
	public static final String EMPTY_SON = "No son concept";
	public static final String EMPTY_CATEGORY = "No category concept";

	protected ThesauformConfiguration conf = new ThesauformConfiguration();

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// manage errors messages
		Map<String, String> errors = new HashMap<>();
		// trait model
		SkosTraitModel traitModel = null;
		// test if a session is initialized else it is visualization
		HttpSession session = request.getSession(false);
		if (request.getParameter(ThesauformConfiguration.GET_VIZ) != null
				&& request.getParameter(ThesauformConfiguration.GET_VIZ).equals("1")) {
			// set public file
			if(ThesauformConfiguration.database) {
				traitModel = new SkosTraitModel(ThesauformConfiguration.public_data_file);
			}
			else {
				traitModel = new SkosTraitModel(getServletContext().getRealPath(ThesauformConfiguration.public_data_file));
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
						// re-authenticate
						this.getServletContext().getRequestDispatcher(ThesauformConfiguration.VUE_FAILED)
								.forward(request, response);
					}
				}
			}
		}
		// Treatment
		if (traitModel != null) {
			String traitName = request.getParameter(GET_PARAMETER);
			// create object trait
			TraitConcept myTrait = new TraitConcept();
			// set name
			try {
				myTrait.setName(traitName);
			} catch (Exception e) {
				errors.put(ERROR_PARAMETER, ERROR_MESSAGE_PARAMETER + e.getMessage());
			}
			//// interrogate model
			// get trait concept from form parameter
			Resource concept = traitModel.getResource(Format.formatName(myTrait.getName()));
			if (concept != null) {
				// get trait real name
				try {
					myTrait.setRealName(traitModel
							.getLabelLiteralForm(traitModel.SimpleSelector(concept, TraitVocTemp.formalName, null)
									.next().getObject().as(Resource.class)));
					myTrait.setName(traitModel.getLabelLiteralForm(traitModel.getPrefLabel(concept)));
				} catch (Exception e) {
					errors.put(ERROR_REAL_NAME, e.getMessage() + " for " + traitName);
				}
				// get trait URI
				try {
					myTrait.setUri(concept.toString());
				} catch (Exception e) {
					errors.put(ERROR_URI, e.getMessage() + " for " + traitName);
				}
				// get definition
				try {
					myTrait.setDefinition(traitModel.getValue(traitModel.getDefinition(concept)));
					// get definition reference
					myTrait.setReference(
							traitModel.getValue(traitModel.getReference(traitModel.getDefinition(concept))));
				} catch (Exception e) {
					errors.put(ERROR_DEFINITION, e.getMessage() + " for " + traitName);
				}
				// get abbreviation
				try {
					myTrait.setAbbreviation(traitModel
							.getLabelLiteralForm(traitModel.getAbbreviation(traitModel.getPrefLabel(concept))));
				} catch (Exception e) {
					errors.put(ERROR_ABBREVIATION, e.getMessage() + " for " + traitName);
				}
				// get unit
				try {
					myTrait.setUnit(traitModel.getValue(traitModel.getUnit(concept)));
				} catch (Exception e) {
					errors.put(ERROR_UNIT, e.getMessage() + " for " + traitName);
				}
				// get parent
				//@PATCH : multi parent
				try {
					StmtIterator parentIt = traitModel.getAllParent(concept);
					if (parentIt.hasNext()) {
						List<TraitConcept> myParentList = new ArrayList<>();
						while (parentIt.hasNext()) {
							TraitConcept myTraitTmp = new TraitConcept();
							Statement st = parentIt.next();
							Resource parent = st.getObject().as(Resource.class);
							myTraitTmp.setRealName(parent.getLocalName());
							myTraitTmp.setName(parent.getLocalName());
							myParentList.add(myTraitTmp);
						}
						myTrait.setParent(myParentList);
					} else {
						throw new Exception(EMPTY_CATEGORY);
					}
				} catch (Exception e) {
					errors.put(ERROR_CATEGORIES, e.getMessage() + " for " + traitName);
				}
				/*
				try {
					TraitConcept myTraitTmp = new TraitConcept();
					myTraitTmp.setName(
							traitModel.getLabelLiteralForm(traitModel.getPrefLabel(traitModel.getCategory(concept))));
					myTrait.setParent(myTraitTmp);
				} catch (Exception e) {
					errors.put(ERROR_PARENT, e.getMessage() + " for " + traitName);
					// if not a parent it could be a category
					StmtIterator getTraitOfCat = traitModel.SimpleSelector(null, TraitVocTemp.cat, concept);
					if (getTraitOfCat.hasNext()) {
						TraitConcept myTraitTmp = new TraitConcept();
						Resource parentCat = null;
						while (getTraitOfCat.hasNext()) {
							Statement st = getTraitOfCat.next();
							parentCat = st.getSubject();
						}
						try {
							myTraitTmp.setName(traitModel.getLabelLiteralForm(traitModel.getPrefLabel(parentCat)));
							myTrait.setParent(myTraitTmp);
						} catch (Exception e2) {
							errors.put(ERROR_PARENT, e2.getMessage() + " for category " + traitName);
						}
					} else {
						errors.put("category", "no cat");
					}
				}*/
				// get comments in a list
				try {
					// get all comments in a list
					StmtIterator commentRawList = traitModel.getComment(concept);
					// test if there is at least one comment
					if (commentRawList.hasNext()) {
						// create AnnotationConcept list for comment
						List<AnnotationConcept> myCommentList = new ArrayList<>();
						Integer cpt = 0;
						// treat each comment
						while (commentRawList.hasNext()) {
							cpt++;
							// create AnnotationConcept object
							AnnotationConcept myAnnotationTmp = new AnnotationConcept();
							// get comment model object
							Resource commentRawObject = commentRawList.next().getObject().as(Resource.class);
							myAnnotationTmp.setProperty(COMMENT_NAME + cpt);
							myAnnotationTmp.setCreator(commentRawObject.listProperties(DC.creator).next().getObject().as(Resource.class).listProperties(FOAF.name).next().getObject().asNode().getLiteralLexicalForm());
							myAnnotationTmp.setValue(Format.printDef(commentRawObject.listProperties(RDF.value).next()
									.getObject().asNode().getLiteralLexicalForm()));
							myCommentList.add(myAnnotationTmp);
						}
						myTrait.setCommentsList(myCommentList);
					} else {
						throw new Exception(EMPTY_COMMENT);
					}
				} catch (Exception e) {
					errors.put(ERROR_COMMENTS, e.getMessage() + " for " + traitName);
				}
				// get annotation in a list of annotation
				try {
					// get all annotation from the model
					Map<String, Map<String, String>> mapAnnotationsRaw = traitModel
							.getAnnotation(Format.formatName((myTrait.getName())));
					// test if there is annotation
					if (!mapAnnotationsRaw.isEmpty()) {
						// create AnnotationConcept list for annotation
						Map<Integer, List<AnnotationConcept>> myAnnotationMap = new HashMap<Integer, List<AnnotationConcept>>();
						Integer cpt = 0;
						// treat each annotation
						for (Iterator<String> i = mapAnnotationsRaw.keySet().iterator(); i.hasNext();) {
							List<AnnotationConcept> myAnnotationList = new ArrayList<AnnotationConcept>();
							cpt++;
							String key = i.next();
							Map<String, String> map2 = mapAnnotationsRaw.get(key);
							for (Iterator<String> j = map2.keySet().iterator(); j.hasNext();) {
								// create AnnotationConcept object
								AnnotationConcept myAnnotationTmp = new AnnotationConcept();
								String prop = j.next();
								myAnnotationTmp.setProperty(prop);
								String value = map2.get(prop);
								myAnnotationTmp.setValue(value);
								myAnnotationList.add(myAnnotationTmp);
							}
							myAnnotationMap.put(cpt, myAnnotationList);
						}
						myTrait.setAnnotationsList(myAnnotationMap);
					} else {
						throw new Exception(EMPTY_ANNOTATION);
					}
				} catch (Exception e) {
					errors.put(ERROR_ANNOTATIONS, e.getMessage() + " for " + traitName);
				}
				// get all categories in a list of TraitConcept
				try {
					StmtIterator categoriesIt = traitModel.getListStatement(concept.getLocalName());
					if (categoriesIt.hasNext()) {
						List<TraitConcept> myCategoryList = new ArrayList<>();
						while (categoriesIt.hasNext()) {
							TraitConcept myTraitTmp = new TraitConcept();
							Statement st = categoriesIt.next();
							if (st.getPredicate().getLocalName().equalsIgnoreCase("cat")) {
								Resource cat = st.getObject().as(Resource.class);
								myTraitTmp.setRealName(cat.getLocalName());
								myTraitTmp.setName(cat.getLocalName());
								myCategoryList.add(myTraitTmp);
							}
						}
						myTrait.setCategoriesList(myCategoryList);
					} else {
						throw new Exception(EMPTY_CATEGORY);
					}
				} catch (Exception e) {
					errors.put(ERROR_CATEGORIES, e.getMessage() + " for " + traitName);
				}
				// get all synonym in a list of TraitConcept
				try {
					StmtIterator synonymIt = traitModel.getAllAltLabel(concept);
					if (synonymIt.hasNext()) {
						List<TraitConcept> mySynonymsList = new ArrayList<>();
						while (synonymIt.hasNext()) {
							TraitConcept myTraitTmp = new TraitConcept();
							Statement st = synonymIt.next();
							Resource AltLabel = st.getObject().as(Resource.class);
							myTraitTmp.setRealName(traitModel.getLabelLiteralForm(AltLabel));
							myTraitTmp.setName(traitModel.getLabelLiteralForm(AltLabel));
							mySynonymsList.add(myTraitTmp);
						}
						myTrait.setSynonymsList(mySynonymsList);
					} else {
						throw new Exception(EMPTY_SYNONYM);
					}
				} catch (Exception e) {
					errors.put(ERROR_SYNONYMS, e.getMessage() + " for " + traitName);
				}
				// get all related concept in a list of TraitConcept
				try {
					StmtIterator RelatedIt = traitModel.getAllRelated(concept);
					if (RelatedIt.hasNext()) {
						List<TraitConcept> myRelatedsList = new ArrayList<>();
						while (RelatedIt.hasNext()) {
							TraitConcept myTraitTmp = new TraitConcept();
							Statement st = RelatedIt.next();
							Resource Related = st.getObject().as(Resource.class);
							myTraitTmp.setRealName(traitModel.getLabelLiteralForm(traitModel.getPrefLabel(Related)));
							myTraitTmp.setName(traitModel.getLabelLiteralForm(traitModel.getPrefLabel(Related)));
							myRelatedsList.add(myTraitTmp);
						}
						myTrait.setRelatedsList(myRelatedsList);
					} else {
						throw new Exception(EMPTY_RELATED);
					}
				} catch (Exception e) {
					errors.put(ERROR_RELATEDS, e.getMessage() + " for " + traitName);
				}
				// get all sons concept in a list of TraitConcept
				try {
					NodeIterator SonIt = traitModel.getSubclass(concept);
					if (SonIt.hasNext()) {
						List<TraitConcept> mySonsList = new ArrayList<>();
						while (SonIt.hasNext()) {
							TraitConcept myTraitTmp = new TraitConcept();
							Resource son = SonIt.next().as(Resource.class);
							myTraitTmp.setName(traitModel.getLabelLiteralForm(traitModel.getPrefLabel(son)));
							// test if the son have a son too
							Resource conceptSonTmp = traitModel.getResource(Format.formatName(myTraitTmp.getName()));
							NodeIterator SonSonTmpIt = traitModel.getSubclass(conceptSonTmp);
							if (SonSonTmpIt.hasNext()) {
								// Artifact the son is son of himself
								myTraitTmp.setSonsList(Arrays.asList(myTraitTmp));
							}
							// add son to the list
							mySonsList.add(myTraitTmp);
						}
						myTrait.setSonsList(mySonsList);
					} else {
						throw new Exception(EMPTY_SON);
					}
				} catch (Exception e) {
					errors.put(ERROR_SONS, e.getMessage() + " for " + traitName);
				}
			} else {
				errors.put(ERROR_CONCEPT, ERROR_MESSAGE_CONCEPT + ": " + traitName);
			}
			request.setAttribute("my_trait", myTrait);
			request.setAttribute("my_errors", errors);
			traitModel.close();
			if (request.getParameter(ThesauformConfiguration.GET_VIZ) != null
					&& request.getParameter(ThesauformConfiguration.GET_VIZ).equals("1")) {
				this.getServletContext().getRequestDispatcher(VUE_SUCCESS_VIZ).forward(request, response);
			} else {
				this.getServletContext().getRequestDispatcher(VUE_SUCCESS).forward(request, response);
			}
		} else {
			request.setAttribute("my_errors", errors);
			// redirect to logging page
			this.getServletContext().getRequestDispatcher(ThesauformConfiguration.VUE_FAILED).forward(request,
					response);
		}
	}
}
