package thesauform.controller.administration;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
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

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;

import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;

import thesauform.beans.AnnotationConcept;
import thesauform.beans.TraitConcept;
import thesauform.controller.InfoAnnotation;
import thesauform.model.Format;
import thesauform.model.SkosTraitModel;
import thesauform.model.ThesauformConfiguration;
import thesauform.model.vocabularies.TraitVocTemp;

/**
 * Servlet implementation class Test
 */
@WebServlet("/administration/exportFileXls")
public class ExportFileXls extends HttpServlet {
       
	private static final String REQUEST_PARAMETER = "root";
	private static final String ANNOTATION_NAME = "pref name";
	private static final String ANNOTATION_UNIT = "prefUnit";
	private static final String ANNOTATION_DEF = "definition";
	private static final String ANNOTATION_ABB = "abbreviation";
	private static final String ANNOTATION_PARENT = "term category";
	private static final String XLS_TITLE_NAME = "Name";
	private static final String XLS_TITLE_PROP_NAME = "Proposed name";
	private static final String XLS_TITLE_PARENT = "Category";
	private static final String XLS_TITLE_PROP_PARENT = "Proposed category";
	private static final String XLS_TITLE_DEF = "Definition";
	private static final String XLS_TITLE_PROP_DEF = "Proposed definition";
	private static final String XLS_TITLE_UNIT = "Unit";
	private static final String XLS_TITLE_PROP_UNIT = "Proposed Unit";
	private static final String XLS_TITLE_ABB = "Abbreviation";
	private static final String XLS_TITLE_PROP_ABB = "Proposed abbreviation";
	private static final String XLS_TITLE_SYN = "Synonym";
	private static final String XLS_TITLE_REL = "Related";
	private static final String XLS_TITLE_COMMENT = "Comment";
	
	private static final long serialVersionUID = 4683006054332790382L;
	
	private static Integer cptLine = 0;
	private static HashMap<Integer,HashMap<Integer,String>> csvPatch = new HashMap<Integer,HashMap<Integer,String>>();
	
	//POI only support row by row insertion should transform before passing POI
	private void printHierarchy(String root, HashMap<String,List<String>> hierarchyMap, Integer cptCol) {
		HashMap<Integer,String> cellPatch = new HashMap<Integer,String>();
		if(csvPatch.containsKey(cptLine)) {
			cellPatch = csvPatch.get(cptLine);
		}
		cellPatch.put(cptCol, root);
		csvPatch.put(cptLine, cellPatch);
		if(hierarchyMap.containsKey(root)) {
			List<String> sonList = hierarchyMap.get(root);
			cptCol++;
			for (String son : sonList) {
				printHierarchy(son, hierarchyMap, cptCol);
				cptLine++;
			}
			cptLine--;
		}
	}
	
	private List<TraitConcept> readTree (String rootName, List<TraitConcept> treeList, SkosTraitModel traitModel) {
		// create object trait
		TraitConcept myTrait = new TraitConcept();
		// set name
		try {
			myTrait.setName(rootName);
		} catch (Exception e) {
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
			}
			// get trait URI
			try {
				myTrait.setUri(concept.toString());
			} catch (Exception e) {
			}
			// get definition
			try {
				myTrait.setDefinition(traitModel.getValue(traitModel.getDefinition(concept)));
				// get definition reference
				myTrait.setReference(
						traitModel.getValue(traitModel.getReference(traitModel.getDefinition(concept))));
			} catch (Exception e) {
			}
			// get abbreviation
			try {
				myTrait.setAbbreviation(traitModel
						.getLabelLiteralForm(traitModel.getAbbreviation(traitModel.getPrefLabel(concept))));
			} catch (Exception e) {
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
				}
			} catch (Exception e) {
			}
			// get unit
			try {
				myTrait.setUnit(traitModel.getValue(traitModel.getUnit(concept)));
			} catch (Exception e) {
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
						myAnnotationTmp.setProperty(InfoAnnotation.COMMENT_NAME + cpt);
						myAnnotationTmp.setCreator(commentRawObject.listProperties(DC.creator).next().getObject().as(Resource.class).listProperties(FOAF.name).next().getObject().asNode().getLiteralLexicalForm());
						myAnnotationTmp.setValue(Format.printDef(commentRawObject.listProperties(RDF.value).next()
								.getObject().asNode().getLiteralLexicalForm()));
						myCommentList.add(myAnnotationTmp);
					}
					myTrait.setCommentsList(myCommentList);
				} else {
				}
			} catch (Exception e) {
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
				}
			} catch (Exception e) {
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
				}
			} catch (Exception e) {
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
				}
			} catch (Exception e) {
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
				}
			} catch (Exception e) {
			}
			//avoid infinite loop
			if(!treeList.contains(myTrait)) {
				treeList.add(myTrait);
				// get all sons concept in a list of TraitConcept
				try {
					NodeIterator SonIt = traitModel.getSubclass(concept);
					if (SonIt.hasNext()) {
						List<TraitConcept> mySonsList = new ArrayList<>();
						while (SonIt.hasNext()) {
							Resource son = SonIt.next().as(Resource.class);
							String sonName = traitModel.getLabelLiteralForm(traitModel.getPrefLabel(son));
							readTree(sonName, treeList, traitModel);
						}
						myTrait.setSonsList(mySonsList);
					} else {
					}
				} catch (Exception e) {
				}
			}
		}
		else {
		}
		return treeList;
	}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//get root from configuration file
		String rootName = ThesauformConfiguration.super_root;
		//test if parameter given
		if(request.getParameterMap().containsKey(REQUEST_PARAMETER) && !request.getParameter(REQUEST_PARAMETER).isEmpty()) {
			rootName = request.getParameter(REQUEST_PARAMETER);
		}
		//get all concept
		//get all info for each concept (InfoAnnotation.java)
		//write each concept on a line
		
		//get first concept
		//get all son recursively and store level
		//test no loop
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("data");
		HSSFSheet hierarchySheet = workbook.createSheet("hierarchy");

		//read model
		SkosTraitModel traitModel = null;
		if(ThesauformConfiguration.database) {
			traitModel = new SkosTraitModel(ThesauformConfiguration.data_file);
		}
		else {
			traitModel = new SkosTraitModel(getServletContext().getRealPath(ThesauformConfiguration.data_file));
		}
		if (traitModel != null) {
			//get all informations
			List<TraitConcept> informationList = new ArrayList<TraitConcept>();
			informationList = readTree(rootName, informationList, traitModel);
			//store hierarchy
			HashMap<String,List<String>> hierarchyMap = new HashMap<String,List<String>>();

			int rowNb = 0;
			Row row = sheet.createRow(rowNb++);
			Integer cptCell = 0;
			Cell cell1 = row.createCell(cptCell++);
			Cell cell1a = row.createCell(cptCell++);
			Cell cell8 = row.createCell(cptCell++);
			Cell cell8a = row.createCell(cptCell++);
			Cell cell2 = row.createCell(cptCell++);
			Cell cell2a = row.createCell(cptCell++);
			Cell cell3 = row.createCell(cptCell++);
			Cell cell3a = row.createCell(cptCell++);
			Cell cell4 = row.createCell(cptCell++);
			Cell cell4a = row.createCell(cptCell++);
			Cell cell5 = row.createCell(cptCell++);
			Cell cell6 = row.createCell(cptCell++);
			Cell cell7 = row.createCell(cptCell++);
			cell1.setCellValue(XLS_TITLE_NAME);
			cell1a.setCellValue(XLS_TITLE_PROP_NAME);
			cell8.setCellValue(XLS_TITLE_PARENT);
			cell8a.setCellValue(XLS_TITLE_PROP_PARENT);
			cell2.setCellValue(XLS_TITLE_DEF);
			cell2a.setCellValue(XLS_TITLE_PROP_DEF);
			cell3.setCellValue(XLS_TITLE_UNIT);
			cell3a.setCellValue(XLS_TITLE_PROP_UNIT);
			cell4.setCellValue(XLS_TITLE_ABB);
			cell4a.setCellValue(XLS_TITLE_PROP_ABB);
			cell5.setCellValue(XLS_TITLE_SYN);
			cell6.setCellValue(XLS_TITLE_REL);
			cell7.setCellValue(XLS_TITLE_COMMENT);
			for (TraitConcept trait : informationList) {
				row = sheet.createRow(rowNb++);
				cptCell = 0;
				cell1 = row.createCell(cptCell++);
				cell1a = row.createCell(cptCell++);
				cell8 = row.createCell(cptCell++);
				cell8a = row.createCell(cptCell++);
				cell2 = row.createCell(cptCell++);
				cell2a = row.createCell(cptCell++);
				cell3 = row.createCell(cptCell++);
				cell3a = row.createCell(cptCell++);
				cell4 = row.createCell(cptCell++);
				cell4a = row.createCell(cptCell++);
				cell5 = row.createCell(cptCell++);
				cell6 = row.createCell(cptCell++);
				cell7 = row.createCell(cptCell++);
				cell1.setCellValue(trait.getName());
				if(trait.getDefinition()!=null) {
					cell2.setCellValue(trait.getDefinition() + " (" + trait.getReference() + ")");
				}
				if(trait.getUnit()!=null) {
					cell3.setCellValue(trait.getUnit());
				}
				if(trait.getAbbreviation()!=null) {
					cell4.setCellValue(trait.getAbbreviation());
				}
				String synString = "";
				List<TraitConcept> synList = trait.getSynonymsList();
				if(synList!=null&&!synList.isEmpty()) {
					for (TraitConcept syn : synList) {
						synString = synString.concat(syn.getName()) + " / ";
					}
					synString = synString.substring(0, synString.length()-3);
				}
				cell5.setCellValue(synString);
				String relString = "";
				List<TraitConcept> relList = trait.getRelatedsList();
				if(relList!=null&&!relList.isEmpty()) {
					for (TraitConcept rel : relList) {
						relString = relString.concat(rel.getName()) + " / ";
					}
					relString = relString.substring(0, relString.length()-3);
				}
				cell6.setCellValue(relString);
				String commentString = "";
				List<AnnotationConcept> commentsList = trait.getCommentsList();
				if(commentsList!=null&&!commentsList.isEmpty()) { 
					for (AnnotationConcept comment : commentsList) {
						commentString = commentString.concat(comment.getValue() + " / ");
					}
					commentString = commentString.substring(0, commentString.length()-3);
				}
				cell7.setCellValue(commentString);
				String parentString = "";
				List<TraitConcept> parentsList = trait.getParent();
				if(parentsList!=null&&!parentsList.isEmpty()) { 
					for (TraitConcept parent : parentsList) {
						if(hierarchyMap.containsKey(parent.getName())) {
							//remove duplicate
							if(!hierarchyMap.get(parent.getName()).contains(trait.getName())) {
								hierarchyMap.get(parent.getName()).add(trait.getName());
							}
						}
						else {
							hierarchyMap.put(parent.getName(), new ArrayList<String>(Arrays.asList(trait.getName())));
						}
						parentString = parentString.concat(parent.getName()) + " / ";
					}
					parentString = parentString.substring(0, parentString.length()-3);
				}
				cell8.setCellValue(parentString);
				//read all annotations
				String proposedName = "";
				String proposedUnit = "";
				String proposedReference = "";
				String proposedDefinition = "";
				String proposedAbbreviation = "";
				String proposedCategory = "";
				Map<Integer, List<AnnotationConcept>> annotationsMap = trait.getAnnotationsList();
				if(annotationsMap!=null&&!annotationsMap.isEmpty()) {
					Iterator<Entry<Integer, List<AnnotationConcept>>> annotationsIt = annotationsMap.entrySet().iterator();
					if (annotationsIt.hasNext()) {
						// for each property
						while (annotationsIt.hasNext()) {
							Entry<Integer, List<AnnotationConcept>> annotationsPair = annotationsIt.next();
							List<AnnotationConcept> annotationsList = annotationsPair.getValue();
							if(annotationsList!=null&&!annotationsList.isEmpty()) {
								for (AnnotationConcept myAnnotationConcept : annotationsList) {
									switch (myAnnotationConcept.getProperty()) {
									case ANNOTATION_NAME:
										proposedName = proposedName.concat(myAnnotationConcept.getValue()) + " / ";
										break;
									case ANNOTATION_UNIT:
										proposedUnit = proposedUnit.concat(myAnnotationConcept.getValue()) + " / ";
										break;
									case ANNOTATION_DEF:
										proposedDefinition = proposedDefinition.concat(myAnnotationConcept.getValue()) + " / ";
										break;
									case ANNOTATION_ABB:
										proposedAbbreviation = proposedAbbreviation.concat(myAnnotationConcept.getValue()) + " / ";
										break;
									case ANNOTATION_PARENT:
										proposedCategory = proposedCategory.concat(myAnnotationConcept.getValue()) + " / ";
									default:
										break;
									}
								}
							}
						}
						if(proposedName.length()>2) {
							proposedName = proposedName.substring(0, proposedName.length()-3);
						}
						cell1a.setCellValue(proposedName);
						if(proposedCategory.length()>2) {
							proposedCategory = proposedCategory.substring(0, proposedCategory.length()-3);
						}
						cell8a.setCellValue(proposedCategory);
						if(proposedDefinition.length()>2) {
							proposedDefinition = proposedDefinition.substring(0, proposedDefinition.length()-3);
						}
						cell2a.setCellValue(proposedDefinition + " (" + proposedReference + ")");
						if(proposedUnit.length()>2) {
							proposedUnit = proposedUnit.substring(0, proposedUnit.length()-3);
						}
						cell3a.setCellValue(proposedUnit);
						if(proposedAbbreviation.length()>2) {
							proposedAbbreviation = proposedAbbreviation.substring(0, proposedAbbreviation.length()-3);
						}
						cell4a.setCellValue(proposedAbbreviation);
					}
				}
			}
			//print hierarchy
			//start from root
			//reinitialize variable
			cptLine = 1;
			csvPatch = new HashMap<Integer,HashMap<Integer,String>>();
			//@PATCH for POI recursive don't work directly
			printHierarchy(rootName, hierarchyMap, 0);
			Iterator<Entry<Integer, HashMap<Integer,String>>> csvPatchIt = csvPatch.entrySet().iterator();
			//create header
			Row headerRow = hierarchySheet.createRow(0);
			DataFormat format = workbook.createDataFormat();
			CellStyle style = workbook.createCellStyle();
			HSSFFont font = workbook.createFont();
			font.setBold(true);
			style.setDataFormat(format.getFormat("0.0"));
			style.setFont(font);
			if (csvPatchIt.hasNext()) {
				while (csvPatchIt.hasNext()) {
					Entry<Integer, HashMap<Integer,String>> csvPatchPair = csvPatchIt.next();
					Integer cptLine = csvPatchPair.getKey();
					Row hierarchyRow = hierarchySheet.createRow(cptLine);
					HashMap<Integer,String> cellPatch = csvPatchPair.getValue();
					Iterator<Entry<Integer, String>> cellIt = cellPatch.entrySet().iterator();
					if (cellIt.hasNext()) {
						while (cellIt.hasNext()) {
							Entry<Integer,String> cellPair = cellIt.next();
							Integer cptCol = cellPair.getKey();
							Cell headerCell = headerRow.createCell(cptCol);
							headerCell.setCellStyle(style);
							headerCell.setCellValue("Category " + (cptCol + 1));
							Cell hierarchyCell = hierarchyRow.createCell(cptCol);
							String cellValue = cellPair.getValue();
							hierarchyCell.setCellValue(cellValue);
						}
					}
				}
			}
			try {
				response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
				response.setHeader("Content-Disposition", "attachment;filename=\"data.xls\"");
				OutputStream responseOutputStream = response.getOutputStream();
				workbook.write(responseOutputStream);
				workbook.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
