package thesauform.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;

import thesauform.beans.AnnotationConcept;
import thesauform.beans.TraitConcept;
import thesauform.model.Format;
import thesauform.model.SkosTraitModel;
import thesauform.model.ThesauformConfiguration;

/**
 * Servlet implementation class search
 */
@WebServlet("/searchApi")
public class SearchApi extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3615271529291595778L;

	public static final String GET_VIZ = "viz";
	public static final String GET_CONCEPT = "concept";
	public static final String GET_TYPE = "type";
	public static final String GET_FORMAT = "format";
	public static final String ERROR_CONCEPT = "concept";
	public static final String ERROR_MESSAGE_CONCEPT = "Wrong argument concept";
	public static final String ERROR_TYPE = "type";
	public static final String ERROR_MESSAGE_TYPE = "Wrong argument type";
	public static final String ERROR_EMPTY_LIST = "empty";
	public static final String ERROR_MESSAGE_EMPTY_LIST = "No concept found";
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
	public static final String COMMENT_NAME = "Comment";
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SearchApi() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init(config);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// manage errors messages
		Map<String, String> errors = new HashMap<>();
		// set visualization request
		request.setAttribute(GET_VIZ, "1");

		PrintWriter pw = response.getWriter();
		// trait model
		SkosTraitModel traitModel = null;
		// get param in order to know what to print
		String paramConcept = request.getParameter("concept");
		String paramType = request.getParameter("type");
		//TODO different format (json, skos, ...)
		String paramFormat = request.getParameter("format");

		//return parameter
		List<TraitConcept> conceptList = new ArrayList<TraitConcept>();
		
		// set public file
		if(ThesauformConfiguration.database) {
			traitModel = new SkosTraitModel(ThesauformConfiguration.public_data_file);
		}
		else {
			traitModel = new SkosTraitModel(getServletContext().getRealPath(ThesauformConfiguration.public_data_file));
		}
		if (paramType != null) {
			try {
				if(paramType.equals("list")) {
					//get all concept
					if(paramConcept.equals("All")) {
						List<String> conceptNameList =  traitModel.getAllConcept();
						//TODO more properties
						for (String conceptName : conceptNameList) {
							TraitConcept myConcept = new TraitConcept();
							Resource concept = traitModel.getResource(Format.formatName(conceptName));
							if (concept != null) {
								// get trait real name
								try {
									myConcept.setName(traitModel.getLabelLiteralForm(traitModel.getPrefLabel(concept)));
								} catch (Exception e) {
									errors.put(ERROR_REAL_NAME, e.getMessage() + " for " + conceptName);
								}
								// get trait URI
								try {
									myConcept.setUri(concept.toString());
								} catch (Exception e) {
									errors.put(ERROR_URI, e.getMessage() + " for " + conceptName);
								}
								// get definition
								try {
									myConcept.setDefinition(traitModel.getValue(traitModel.getDefinition(concept)));
									// get definition reference
									myConcept.setReference(
											traitModel.getValue(traitModel.getReference(traitModel.getDefinition(concept))));
								} catch (Exception e) {
									errors.put(ERROR_DEFINITION, e.getMessage() + " for " + conceptName);
								}
								// get abbreviation
								try {
									myConcept.setAbbreviation(traitModel
											.getLabelLiteralForm(traitModel.getAbbreviation(traitModel.getPrefLabel(concept))));
								} catch (Exception e) {
									errors.put(ERROR_ABBREVIATION, e.getMessage() + " for " + conceptName);
								}
								// get unit
								try {
									myConcept.setUnit(traitModel.getValue(traitModel.getUnit(concept)));
								} catch (Exception e) {
									errors.put(ERROR_UNIT, e.getMessage() + " for " + conceptName);
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
										myConcept.setParent(myParentList);
									} else {
										throw new Exception(EMPTY_CATEGORY);
									}
								} catch (Exception e) {
									errors.put(ERROR_CATEGORIES, e.getMessage() + " for " + conceptName);
								}
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
										myConcept.setCommentsList(myCommentList);
									} else {
										throw new Exception(EMPTY_COMMENT);
									}
								} catch (Exception e) {
									errors.put(ERROR_COMMENTS, e.getMessage() + " for " + conceptName);
								}
								// get annotation in a list of annotation
								try {
									// get all annotation from the model
									Map<String, Map<String, String>> mapAnnotationsRaw = traitModel
											.getAnnotation(Format.formatName((myConcept.getName())));
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
												if(value!=null && !value.isEmpty()) {
													myAnnotationTmp.setValue(value);
													myAnnotationList.add(myAnnotationTmp);
												}
											}
											myAnnotationMap.put(cpt, myAnnotationList);
										}
										myConcept.setAnnotationsList(myAnnotationMap);
									} else {
										throw new Exception(EMPTY_ANNOTATION);
									}
								} catch (Exception e) {
									errors.put(ERROR_ANNOTATIONS, e.getMessage() + " for " + conceptName);
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
										myConcept.setCategoriesList(myCategoryList);
									} else {
										throw new Exception(EMPTY_CATEGORY);
									}
								} catch (Exception e) {
									errors.put(ERROR_CATEGORIES, e.getMessage() + " for " + conceptName);
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
										myConcept.setSynonymsList(mySynonymsList);
									} else {
										throw new Exception(EMPTY_SYNONYM);
									}
								} catch (Exception e) {
									errors.put(ERROR_SYNONYMS, e.getMessage() + " for " + conceptName);
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
										myConcept.setRelatedsList(myRelatedsList);
									} else {
										throw new Exception(EMPTY_RELATED);
									}
								} catch (Exception e) {
									errors.put(ERROR_RELATEDS, e.getMessage() + " for " + conceptName);
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
										myConcept.setSonsList(mySonsList);
									} else {
										throw new Exception(EMPTY_SON);
									}
								} catch (Exception e) {
									errors.put(ERROR_SONS, e.getMessage() + " for " + conceptName);
								}
							} else {
								errors.put(ERROR_CONCEPT, ERROR_MESSAGE_CONCEPT + ": " + conceptName);
							}
							conceptList.add(myConcept);
						}
					}
					else {
						//TODO get a part of the tree
					}
				}
				else {
					if(paramType.equals("concept")) {
						if(paramConcept != null) {
							TraitConcept myConcept = new TraitConcept();
							//get a concept
							Resource concept = traitModel.getResource(Format.formatName(paramConcept));
							String conceptName = request.getParameter(paramConcept);
							if (concept != null) {
								// get trait real name
								try {
									myConcept.setName(traitModel.getLabelLiteralForm(traitModel.getPrefLabel(concept)));
								} catch (Exception e) {
									errors.put(ERROR_REAL_NAME, e.getMessage() + " for " + conceptName);
								}
								// get trait URI
								try {
									myConcept.setUri(concept.toString());
								} catch (Exception e) {
									errors.put(ERROR_URI, e.getMessage() + " for " + conceptName);
								}
								// get definition
								try {
									myConcept.setDefinition(traitModel.getValue(traitModel.getDefinition(concept)));
									// get definition reference
									myConcept.setReference(
											traitModel.getValue(traitModel.getReference(traitModel.getDefinition(concept))));
								} catch (Exception e) {
									errors.put(ERROR_DEFINITION, e.getMessage() + " for " + conceptName);
								}
								// get abbreviation
								try {
									myConcept.setAbbreviation(traitModel
											.getLabelLiteralForm(traitModel.getAbbreviation(traitModel.getPrefLabel(concept))));
								} catch (Exception e) {
									errors.put(ERROR_ABBREVIATION, e.getMessage() + " for " + conceptName);
								}
								// get unit
								try {
									myConcept.setUnit(traitModel.getValue(traitModel.getUnit(concept)));
								} catch (Exception e) {
									errors.put(ERROR_UNIT, e.getMessage() + " for " + conceptName);
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
										myConcept.setParent(myParentList);
									} else {
										throw new Exception(EMPTY_CATEGORY);
									}
								} catch (Exception e) {
									errors.put(ERROR_CATEGORIES, e.getMessage() + " for " + conceptName);
								}
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
										myConcept.setCommentsList(myCommentList);
									} else {
										throw new Exception(EMPTY_COMMENT);
									}
								} catch (Exception e) {
									errors.put(ERROR_COMMENTS, e.getMessage() + " for " + conceptName);
								}
								// get annotation in a list of annotation
								try {
									// get all annotation from the model
									Map<String, Map<String, String>> mapAnnotationsRaw = traitModel
											.getAnnotation(Format.formatName((myConcept.getName())));
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
												if(value!=null && !value.isEmpty()) {
													myAnnotationTmp.setValue(value);
													myAnnotationList.add(myAnnotationTmp);
												}
											}
											myAnnotationMap.put(cpt, myAnnotationList);
										}
										myConcept.setAnnotationsList(myAnnotationMap);
									} else {
										throw new Exception(EMPTY_ANNOTATION);
									}
								} catch (Exception e) {
									errors.put(ERROR_ANNOTATIONS, e.getMessage() + " for " + conceptName);
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
										myConcept.setCategoriesList(myCategoryList);
									} else {
										throw new Exception(EMPTY_CATEGORY);
									}
								} catch (Exception e) {
									errors.put(ERROR_CATEGORIES, e.getMessage() + " for " + conceptName);
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
										myConcept.setSynonymsList(mySynonymsList);
									} else {
										throw new Exception(EMPTY_SYNONYM);
									}
								} catch (Exception e) {
									errors.put(ERROR_SYNONYMS, e.getMessage() + " for " + conceptName);
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
										myConcept.setRelatedsList(myRelatedsList);
									} else {
										throw new Exception(EMPTY_RELATED);
									}
								} catch (Exception e) {
									errors.put(ERROR_RELATEDS, e.getMessage() + " for " + conceptName);
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
										myConcept.setSonsList(mySonsList);
									} else {
										throw new Exception(EMPTY_SON);
									}
								} catch (Exception e) {
									errors.put(ERROR_SONS, e.getMessage() + " for " + conceptName);
								}
								conceptList.add(myConcept);
							} else {
								errors.put(ERROR_CONCEPT, ERROR_MESSAGE_CONCEPT + ": " + conceptName);
							}
						}
						else {
							errors.put(ERROR_CONCEPT, ERROR_MESSAGE_CONCEPT);
							throw new Exception( ERROR_MESSAGE_CONCEPT );						
						}
					}
					else {
						errors.put(ERROR_TYPE, ERROR_MESSAGE_TYPE);
						throw new Exception( ERROR_MESSAGE_TYPE + paramType );
					}
				}
				//No exception, print output
				switch (paramFormat) {
				case "json":
					JSONArray jsonArray = new JSONArray();
					for (TraitConcept myConceptTmp : conceptList) {
					    JSONObject json = new JSONObject();
	                    json.put("uri",myConceptTmp.getUri());
	                    json.put("name",myConceptTmp.getName());
	                    json.put("definition",myConceptTmp.getDefinition());
	                    json.put("unit",myConceptTmp.getUnit());
	                    jsonArray.add(json);
					}
					pw.print(jsonArray);
					break;
				default:
					break;
				}
			}
			catch (Exception e) {
				pw.print(e.getMessage());
			}
		}
	}
}