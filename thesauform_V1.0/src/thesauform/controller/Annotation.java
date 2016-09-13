package thesauform.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import thesauform.beans.Person;
import thesauform.model.Format;
import thesauform.model.SkosModel;
import thesauform.model.SkosTraitModel;
import thesauform.model.ThesauformConfiguration;
import thesauform.model.vocabularies.SkosVoc;
import thesauform.model.vocabularies.SkosXLVoc;
import thesauform.model.vocabularies.TraitVoc;
import thesauform.model.vocabularies.TraitVocTemp;

public class Annotation {
	public static final String ERROR_MESSAGE_USER = "User empty";
	public static final String ERROR_REFERENCE = "Reference without definition";
	public static final String ERROR_MESSAGE_SESSION = "Session empty";

	synchronized public static void submitSynchronized(HttpSession session, HttpServletRequest request,
			boolean ismodified) throws Exception {
		if (ismodified) {
			setModif(session, request);
		} else {
			addNewConcept(request, session);
		}
	}

	private static void setModif(HttpSession session, HttpServletRequest request) throws Exception {
		String parameter = (String) request.getServletContext().getAttribute(ThesauformConfiguration.TRAIT_FILE);
		String file = "";
		if(request.getServletContext().getAttribute(ThesauformConfiguration.DATABASE)=="false") {
			file = (String) request.getServletContext().getRealPath(parameter);
		}
		else {
			file = (String) parameter;
		}
		Calendar date = Calendar.getInstance();
		// test if session is correct
		if (session.getAttribute(ThesauformConfiguration.USR_SESSION) instanceof Person) {
			Person user = (Person) session.getAttribute(ThesauformConfiguration.USR_SESSION);
			boolean authentificationStatus = user.getAuthenticated();
			if (authentificationStatus) {
				// Treatment if user is logged
				Map<String, String[]> param = request.getParameterMap();
				//remove line break during insertion
				for (Map.Entry<?, String[]> e : param.entrySet()) {
					String[] valArray = e.getValue();
					if (valArray != null && valArray.length>0) {
						for (int i = 0; i < valArray.length; i++) {
							valArray[i] = valArray[i].replaceAll("[\r\n\t]", " ");
						}
						e.setValue(valArray);
					}
				}
				String concept = param.get("inputAnn")[0];
				String del = "";
				if (param.get("del") != null) {
					del = param.get("del")[0];
				}
				SkosTraitModel m = new SkosTraitModel(file);

				Resource person = m.getPerson(user.getName(), user.getMail());
				Resource scheme = m.getScheme();
				Resource modifConcept = m.getResource(Format.formatName(concept));
				// pere and nameAnn must not be different
				String pere = param.get("nameAnn")[0];
				// PATCH: set pere name to delete if asked to be delete
				if (del.equalsIgnoreCase("1")) {
					pere = "delete";
				}
				if (!Format.formatName(pere.trim()).equalsIgnoreCase(Format.formatName(concept.trim()))) {
					if (pere.trim().equalsIgnoreCase("delete")) {
						Resource delete = m.createDelete(modifConcept);
						m.setResource(delete, DCTerms.created, date);
						m.setResource(delete, DC.creator, person);
						Resource deletelist = m.createCollection("Delete");
						m.addMember(deletelist, modifConcept);
					} else {
						Resource modif = m.createUpdate(modifConcept);
						m.sethasProperty(modif, SkosXLVoc.prefLabel);
						m.sethasValue(modif, pere);
						m.setResource(modif, DCTerms.created, date);
						m.setResource(modif, DC.creator, person);
					}
				}
				Resource updatelist = m.createCollection("Update");
				m.addMember(updatelist, modifConcept);
				// test if definition is not empty
				if (!param.get("def")[0].isEmpty() || !param.get("def")[0].trim().equalsIgnoreCase("")) {
					// test if definition already exists
					if (m.getDefinition(modifConcept) != null) {
						Resource def = m.getDefinition(modifConcept);
						String valueDef = m.getValue(def);
						// test if the posted definition is the same than
						// existing
						if (!valueDef.trim().equalsIgnoreCase(param.get("def")[0].trim())) {
							Resource modif = m.createUpdate(modifConcept);
							m.sethasProperty(modif, SkosVoc.definition);
							m.sethasValue(modif, param.get("def")[0].trim());
							m.setResource(modif, DCTerms.created, date);
							m.setResource(modif, DC.creator, person);
							// test if reference is not empty
							if (!param.get("ref")[0].isEmpty() || !param.get("ref")[0].trim().equalsIgnoreCase("")) {
								// associate reference annotation to
								// definition annotation
								m.setReference(modif, param.get("ref")[0].trim());
							}
						} else {
							// test if reference not empty
							if (m.getReference(m.getDefinition(modifConcept)) != null&&!m.getValue(m.getReference(m.getDefinition(modifConcept))).isEmpty()) {
								Resource ref = m.getReference(m.getDefinition(modifConcept));
								String valueRef = m.getValue(ref);
								// test if reference already exists for
								// existing definition
								if (!valueRef.trim().equalsIgnoreCase(param.get("ref")[0].trim())) {
									// duplicate definition as annotation too
									// add as annotation
									Resource modif = m.createUpdate(modifConcept);
									m.sethasProperty(modif, SkosVoc.definition);
									m.sethasValue(modif, param.get("def")[0].trim());
									m.setResource(modif, DCTerms.created, date);
									m.setResource(modif, DC.creator, person);
									// associate reference annotation to
									// definition annotation
									m.setReference(modif, param.get("ref")[0].trim());
								} else {
									// do nothing, avoid double
								}
							} else {
								// add reference directly to the concept
								Resource ref = m.setReference(m.getDefinition(modifConcept),
										param.get("ref")[0].trim());
								Resource note = m.createInsert(ref);
								m.setResource(note, DCTerms.created, date);
								m.setResource(note, DC.creator, person);
							}
						}
					} else {
						// definition not existing, add directly it to the
						// concept
						Resource def = m.setDefinition(modifConcept, param.get("def")[0].trim());
						Resource note = m.createInsert(def);
						m.setResource(note, DCTerms.created, date);
						m.setResource(note, DC.creator, person);
						// add reference too if exists
						if (!param.get("ref")[0].trim().isEmpty() || !param.get("ref")[0].trim().equalsIgnoreCase("")) {
							Resource ref = m.setReference(m.getDefinition(modifConcept), param.get("ref")[0].trim());
							Resource noteRef = m.createInsert(ref);
							m.setResource(noteRef, DCTerms.created, date);
							m.setResource(noteRef, DC.creator, person);
						}
					}
				} else {
					// no definition posted, check that no reference too
					if (!param.get("ref")[0].isEmpty() || !param.get("ref")[0].trim().equalsIgnoreCase("")) {
						throw new Exception(ERROR_REFERENCE);
					}
				}
				// abbreviation
				if (!param.get("abbr")[0].isEmpty() || !param.get("abbr")[0].trim().equalsIgnoreCase("")) {
					if (m.getAbbreviation(m.getPrefLabel(modifConcept)) != null) {
						Resource abbr = m.getAbbreviation(m.getPrefLabel(modifConcept));
						String valueAbbr = m.getLabelLiteralForm(abbr);
						if (!valueAbbr.trim().equalsIgnoreCase(param.get("abbr")[0].trim())) {
							Resource modif = m.createUpdate(modifConcept);
							m.sethasProperty(modif, TraitVocTemp.abbreviation);
							m.sethasValue(modif, param.get("abbr")[0].trim());
							m.setResource(modif, DCTerms.created, date);
							m.setResource(modif, DC.creator, person);
						}
					} else {
						Resource abbr = m.setAbbreviation(m.getPrefLabel(modifConcept), param.get("abbr")[0]);
						Resource note = m.createInsert(abbr);
						m.setResource(note, DCTerms.created, date);
						m.setResource(note, DC.creator, person);
					}
				} else {
				}
				// synonym
				if (!param.get("syn")[0].isEmpty() || !param.get("syn")[0].trim().equalsIgnoreCase("")) {
					StmtIterator Sti = m.getAllAltLabel(modifConcept);
					ArrayList<String> al = new ArrayList<String>();
					while (Sti.hasNext()) {
						Statement st = Sti.next();
						Resource rel = st.getObject().as(Resource.class);
						String label = m.getLabelLiteralForm(rel);
						al.add(Format.formatName(label));
					}
					for (int i = 0; i < param.get("syn").length; i++) {
						if (m.getAllAltLabel(modifConcept).toList().size() != 0) {
							if (!al.contains(Format.formatName(param.get("syn")[i].trim()))) {
								Resource modif = m.createUpdate(modifConcept);
								m.sethasProperty(modif, SkosXLVoc.altLabel);
								m.sethasValue(modif, param.get("syn")[i].trim());
								m.setResource(modif, DCTerms.created, date);
								m.setResource(modif, DC.creator, person);
							}
						} else {
							Resource father = m.getCategory(modifConcept);
							Resource synconcept = null;
							if (m.containsConcept(m.getResource(Format.formatName(param.get("syn")[i])))) {
								synconcept = m.getResource(Format.formatName(param.get("syn")[i]));
							} else {
								synconcept = m.setConcept(Format.formatName(param.get("syn")[i]), scheme, father,
										person, date);
								m.setDefinition(synconcept, "Synonym for the trait " + modifConcept.getLocalName());
								Resource insertlist = m.createCollection("Insert");
								m.addMember(insertlist, synconcept);
							}
							Resource modif = m.createUpdate(modifConcept);
							m.sethasProperty(modif, SkosXLVoc.altLabel);
							m.sethasValue(modif, param.get("syn")[i].trim());
							m.setResource(modif, DCTerms.created, date);
							m.setResource(modif, DC.creator, person);
						}
					}
				}
				// RELATED
				if (!param.get("related")[0].isEmpty() || !param.get("related")[0].trim().equalsIgnoreCase("")) {
					StmtIterator Sti = m.getAllRelated(modifConcept);
					ArrayList<String> al = new ArrayList<String>();
					while (Sti.hasNext()) {
						Statement st = Sti.next();
						Resource rel = st.getObject().as(Resource.class);
						try {
							String label = m.getLabelLiteralForm(m.getPrefLabel(rel));
							al.add(Format.formatName(label));
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
					for (int i = 0; i < param.get("related").length; i++) {
						if (m.getAllRelated(modifConcept).toList().size() != 0) {
							if (!al.contains(Format.formatName(param.get("related")[i].trim()))) {
								Resource modif = m.createUpdate(modifConcept);
								m.sethasProperty(modif, SkosVoc.related);
								m.sethasValue(modif, param.get("related")[i].trim());
								m.setResource(modif, DCTerms.created, date);
								m.setResource(modif, DC.creator, person);
							}
						} else {
							Resource father = m.getCategory(modifConcept);
							Resource relconcept = null;
							if (m.containsConcept(m.getResource(Format.formatName(param.get("related")[i])))) {
								relconcept = m.getResource(Format.formatName(param.get("related")[i]));
							} else {
								relconcept = m.setConcept(Format.formatName(param.get("related")[i]), scheme, father,
										person, date);
								m.setDefinition(relconcept, "related for the trait " + modifConcept.getLocalName());
								Resource insertlist = m.createCollection("Insert");
								m.addMember(insertlist, relconcept);
							}
							Resource modif = m.createUpdate(modifConcept);
							m.sethasProperty(modif, SkosVoc.related);
							m.sethasValue(modif, param.get("related")[i].trim());
							m.setResource(modif, DCTerms.created, date);
							m.setResource(modif, DC.creator, person);
						}
					}
				}
				// unit
				if (param.get("unit")!=null && (!param.get("unit")[0].trim().isEmpty() || !param.get("unit")[0].trim().equalsIgnoreCase(""))) {
					if (m.getUnit(modifConcept) != null) {
						Resource unit = m.getUnit(modifConcept);
						String valueUnit = m.getValue(unit);
						if (!valueUnit.trim().equalsIgnoreCase(param.get("unit")[0].trim())) {
							Resource modif = m.createUpdate(modifConcept);
							m.sethasProperty(modif, TraitVocTemp.prefUnit);
							m.sethasValue(modif, param.get("unit")[0].trim());
							m.setResource(modif, DCTerms.created, date);
							m.setResource(modif, DC.creator, person);
						}
					} else {
						Resource unit = m.setUnit(modifConcept, param.get("unit")[0].trim());
						Resource note = m.createInsert(unit);
						m.setResource(note, DCTerms.created, date);
						m.setResource(note, DC.creator, person);
					}
				}
				if (!param.get("cat")[0].isEmpty()) {
					if (modifConcept.listProperties(SkosVoc.broaderTransitive) != null
							&& modifConcept.listProperties(SkosVoc.broaderTransitive).hasNext()) {
						RDFNode def = modifConcept.listProperties(SkosVoc.broaderTransitive).next().getObject();
						if (def.isResource()) {
							Resource Def = def.as(Resource.class);
							if (!Def.getLocalName().trim()
									.equalsIgnoreCase(Format.formatName(param.get("cat")[0]).trim())) {
								Resource modif = m.createUpdate(modifConcept);
								m.sethasProperty(modif, SkosVoc.broaderTransitive);
								m.sethasValue(modif, param.get("cat")[0].trim());
								m.setResource(modif, DCTerms.created, date);
								m.setResource(modif, DC.creator, person);
							}
						}
					}
				}
				if (!param.get("comment")[0].trim().isEmpty() || !param.get("comment")[0].trim().equalsIgnoreCase("")) {
					Resource comment = m.createComment(modifConcept);
					m.setComment(comment, param.get("comment")[0].trim());
					m.setResource(comment, DCTerms.created, date);
					m.setResource(comment, DC.creator, person);
				}
				m.save(file, file + "save");
				m.close();
			} else {
				throw new Exception(ERROR_MESSAGE_USER);
			}
		} else {
			throw new Exception(ERROR_MESSAGE_USER);
		}
	}

	public static void addNewConcept(HttpServletRequest request, HttpSession session) throws Exception {
		String parameter = (String) request.getServletContext().getAttribute(ThesauformConfiguration.TRAIT_FILE);
		String file = "";
		if(request.getServletContext().getAttribute(ThesauformConfiguration.DATABASE)=="false") {
			file = (String) request.getServletContext().getRealPath(parameter);
		}
		else {
			file = (String) parameter;
		}
		Calendar date = Calendar.getInstance();
		// test if session is correct
		if (session.getAttribute(ThesauformConfiguration.USR_SESSION) instanceof Person) {
			Person user = (Person) session.getAttribute(ThesauformConfiguration.USR_SESSION);
			boolean authentificationStatus = user.getAuthenticated();
			if (authentificationStatus) {
				// Treatment if user is logged
				Map<String, String[]> param = request.getParameterMap();
				//remove line break during insertion
				for (Map.Entry<?, String[]> e : param.entrySet()) {
					String[] valArray = e.getValue();
					if (valArray != null && valArray.length>0) {
						for (int i = 0; i < valArray.length; i++) {
							valArray[i] = valArray[i].replaceAll("[\r\n\t]", " ");
						}
						e.setValue(valArray);
					}
				}
				String concept = param.get("nameAdd")[0];
				String pere = param.get("hpere")[0];
				SkosTraitModel m = new SkosTraitModel(file);
				Resource person = m.getPerson(user.getName(), user.getMail());
				Resource scheme = m.getScheme();
				Resource newConcept = m.setConcept(concept, scheme, m.getResource(Format.formatName(pere)), person,
						date);
				Resource insertlist = m.createCollection("Insert");
				m.addMember(insertlist, newConcept);
				if (!param.get("def")[0].trim().isEmpty() || !param.get("def")[0].trim().equalsIgnoreCase("")) {
					// definition
					Resource def = m.setDefinition(newConcept, param.get("def")[0].trim());
					Resource note = m.createInsert(def);
					m.setResource(note, DCTerms.created, date);
					m.setResource(note, DC.creator, person);
				}
				if (!param.get("ref")[0].trim().isEmpty() || !param.get("ref")[0].trim().equalsIgnoreCase("")) {
					Resource ref = m.setReference(m.getDefinition(newConcept), param.get("ref")[0].trim());
					Resource note = m.createInsert(ref);
					m.setResource(note, DCTerms.created, date);
					m.setResource(note, DC.creator, person);
				}
				if (!param.get("abbr")[0].trim().isEmpty() || !param.get("abbr")[0].trim().equalsIgnoreCase("")) {
					Resource Label = m.setAbbreviation(m.getPrefLabel(newConcept), param.get("abbr")[0].trim());
					Resource note = m.createInsert(Label);
					m.setResource(note, DCTerms.created, date);
					m.setResource(note, DC.creator, person);
				}
				if (!param.get("syn")[0].trim().isEmpty() || !param.get("syn")[0].trim().equalsIgnoreCase("")) {
					for (int i = 0; i < param.get("syn").length; i++) {
						Resource ConceptLabel = null;
						// XL Label
						if (!m.getResource((Format.formatName(param.get("syn")[i]).trim()))
								.hasProperty(SkosXLVoc.prefLabel)) {
							m.setConcept(param.get("syn")[i].trim(), scheme, m.getResource(Format.formatName(pere)),
									person, date);
							Resource synConcept = m.getResource(Format.formatName(param.get("syn")[i]));
							ConceptLabel = m.getPrefLabel(synConcept);
							Resource Label = m.getPrefLabel(newConcept);
							m.setResource(ConceptLabel, TraitVocTemp.synonym, Label);
							m.setResource(Label, TraitVocTemp.synonym, newConcept);
							m.setResource(newConcept, SkosXLVoc.altLabel, ConceptLabel);
							m.setDefinition(synConcept, "Synonym for the trait " + newConcept.getLocalName());
						} else {
							ConceptLabel = m
									.getPrefLabel(m.getResource((Format.formatName(param.get("syn")[i].trim()))));
							Resource Label = m.getPrefLabel(newConcept);
							m.setResource(ConceptLabel, TraitVocTemp.synonym, Label);
							m.setResource(Label, TraitVocTemp.synonym, newConcept);
							m.setResource(newConcept, SkosXLVoc.altLabel, ConceptLabel);
						}
					}
				}
				if (param.get("unit")!=null && (!param.get("unit")[0].trim().isEmpty() || !param.get("unit")[0].trim().equalsIgnoreCase(""))) {
					Resource unit = m.setUnit(newConcept, param.get("unit")[0].trim());
					Resource note = m.createInsert(unit);
					m.setResource(note, DCTerms.created, date);
					m.setResource(note, DC.creator, person);
				}
				if (!param.get("related")[0].trim().isEmpty() || !param.get("related")[0].trim().equalsIgnoreCase("")) {
					for (int i = 0; i < param.get("related").length; i++) {
						Resource ConceptLabel = null;
						if (!m.getResource((Format.formatName(param.get("related")[i]).trim()))
								.hasProperty(SkosXLVoc.prefLabel)) {
							m.setConcept(param.get("related")[i].trim(), scheme, m.getResource(Format.formatName(pere)),
									person, date);
							Resource relConcept = m.getResource((Format.formatName(param.get("related")[i]).trim()));
							m.setResource(relConcept, SkosVoc.related, newConcept);
							m.setResource(newConcept, SkosVoc.related, relConcept);
							m.setDefinition(relConcept, "Related to the trait " + newConcept.getLocalName());
						} else {
							ConceptLabel = m.getResource((Format.formatName(param.get("related")[i]).trim()));
							m.setResource(newConcept, SkosVoc.related, ConceptLabel);
							m.setResource(ConceptLabel, SkosVoc.related, newConcept);
						}
					}
				}
				// test everything's ok!
				StmtIterator it = m.getListStatement(Format.formatName(concept));
				if (it.hasNext()) {
				}
				m.save(file, file + "save");
				m.close();
			} else {
				throw new Exception(ERROR_MESSAGE_USER);
			}
		} else {
			throw new Exception(ERROR_MESSAGE_USER);
		}
	}

	public static Resource createAnnotation(SkosModel m, Property p, String v) {
		Resource changeNote = m.createResource();
		Resource annotation = m.createResource();
		m.setResource(annotation, RDF.type, TraitVoc.Annotation);
		m.setResource(annotation, TraitVoc.hasProperty, p);
		m.setResource(annotation, TraitVoc.hasValue, v);
		m.setResource(changeNote, TraitVoc.annotation, annotation);
		return changeNote;
	}

	public static Resource createAnnotation(SkosModel m, Property p, Resource v) {
		Resource changeNote = m.createResource();
		Resource annotation = m.createResource();
		m.setResource(annotation, RDF.type, TraitVoc.Annotation);
		m.setResource(annotation, TraitVoc.hasProperty, p);
		m.setResource(annotation, TraitVoc.hasValue, v);
		m.setResource(changeNote, TraitVoc.annotation, annotation);
		return changeNote;
	}

	public static void setPropertyAnnotation(SkosModel m, Resource modifConcept, String s, Property prop, Resource date,
			Resource person) {

	}
}