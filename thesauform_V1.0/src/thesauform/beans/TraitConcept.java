package thesauform.beans;

import java.util.List;
import java.util.Map;

public class TraitConcept {
	private String uri;
	private String name;
	private String unit;
	private String definition;
	private String reference;
	private String abbreviation;
	private String realName;
	private String category; //manage super class if multiple	
	private List<TraitConcept> parent; //parent trait  (vertical link)
	private List<AnnotationConcept> commentsList; //all comments link to the trait
	private Map<Integer,List<AnnotationConcept>> annotationsList; //all annotations link to the trait, could be comments
	private List<TraitConcept> synonymsList; //all synonyms trait link to the trait
	private List<TraitConcept> relatedsList; //all related trait link to the trait (transverse links)
	private List<TraitConcept> sonsList; //all sons trait link to the trait (vertical links)
	private List<TraitConcept> categoriesList;//store categories link to the trait if categorical

	private static final String MISSING_URI = "URI is empty";
	private static final String MISSING_NAME = "Name is empty";
	private static final String MISSING_REFERENCE = "Reference is empty";
	private static final String MISSING_UNIT = "Unit is empty";
	private static final String MISSING_ABBREVIATION = "Abbreviation is empty";
	private static final String MISSING_DEFINITION = "Definition is empty";
	private static final String MISSING_ANNOTATION = "Annotations list is empty";
	private static final String MISSING_SYNONYM = "Synonyms list is empty";
	private static final String MISSING_RELATED = "Relateds list is empty";
	private static final String MISSING_SONS = "Sons list is empty";
	private static final String MISSING_COMMENT = "Comments list is empty";
	private static final String MISSING_REAL_NAME = "Real name is empty";
	private static final String MISSING_CATEGORY = "Category is empty";
	private static final String MISSING_CATEGORIES = "Category list is empty";
	// TODO: filter on Unit
	// private static List<String> UNITS_LIST =
	// Arrays.asList("cm","m","mm","L"); //to complete
	// private static String WRONG_UNIT = "Unit is not managed";

	public String toString() {
		return ("(uri: " + getUri() + " name: " + getName() + " unit: " + getUnit() + " definition: " + getDefinition()
				+ " reference: " + getReference() + " abbreviation: " + getAbbreviation() + " category :"
				+ getCategory() + " parent: " + getParent() + ")");
	}

	public boolean valideUri(String uri) throws Exception {
		boolean returnVal = false;
		if (uri != null && !uri.isEmpty()) {
			returnVal = true;
		} else {
			throw new Exception(MISSING_URI);
		}
		return (returnVal);
	}

	public boolean valideName(String name) throws Exception {
		boolean returnVal = false;
		if (name != null && !name.isEmpty()) {
			returnVal = true;
		} else {
			throw new Exception(MISSING_NAME);
		}
		return (returnVal);
	}

	public boolean valideUnit(String unit) throws Exception {
		boolean returnVal = false;
		if (unit != null && !unit.isEmpty()) {
			// if(!UNITS_LIST.contains(unit)) {
			// throw new Exception( WRONG_UNIT );
			// }
			returnVal = true;
		} else {
			throw new Exception(MISSING_UNIT);
		}
		return (returnVal);
	}

	public boolean valideDefinition(String definition) throws Exception {
		boolean returnVal = false;
		if (definition != null && !definition.isEmpty()) {
			returnVal = true;
		} else {
			throw new Exception(MISSING_DEFINITION);
		}
		return (returnVal);
	}

	public boolean valideReference(String reference) throws Exception {
		boolean returnVal = false;
		if (reference != null && !reference.isEmpty()) {
			returnVal = true;
		} else {
			throw new Exception(MISSING_REFERENCE);
		}
		return (returnVal);
	}

	public boolean valideAbbreviation(String abbreviation) throws Exception {
		boolean returnVal = false;
		if (abbreviation != null && !abbreviation.isEmpty()) {
			returnVal = true;
		} else {
			throw new Exception(MISSING_ABBREVIATION);
		}
		return (returnVal);
	}

	public boolean valideRealName(String realName) throws Exception {
		boolean returnVal = false;
		if (realName != null && !realName.isEmpty()) {
			returnVal = true;
		} else {
			throw new Exception(MISSING_REAL_NAME);
		}
		return (returnVal);
	}

	public boolean valideCategory(String category) throws Exception {
		boolean returnVal = false;
		if (category != null && !category.isEmpty()) {
			returnVal = true;
		} else {
			throw new Exception(MISSING_CATEGORY);
		}
		return (returnVal);
	}

	public boolean valideParent(List<TraitConcept> parent) throws Exception {
		boolean returnVal = false;
		if (parent != null && !parent.isEmpty()) {
			returnVal = true;
		} else {
			throw new Exception(MISSING_CATEGORY);
		}
		return (returnVal);
	}

	public boolean valideComments(List<AnnotationConcept> comments) throws Exception {
		boolean returnVal = false;
		if (comments != null && !comments.isEmpty()) {
			returnVal = true;
		} else {
			throw new Exception(MISSING_COMMENT);
		}
		return (returnVal);
	}

	public boolean valideAnnotations(Map<Integer, List<AnnotationConcept>> annotations) throws Exception {
		boolean returnVal = false;
		if (annotations != null && !annotations.isEmpty()) {
			returnVal = true;
		} else {
			throw new Exception(MISSING_ANNOTATION);
		}
		return (returnVal);
	}

	public boolean valideSynonyms(List<TraitConcept> synonyms) throws Exception {
		boolean returnVal = false;
		if (synonyms != null && !synonyms.isEmpty()) {
			returnVal = true;
		} else {
			throw new Exception(MISSING_SYNONYM);
		}
		return (returnVal);
	}

	public boolean valideRelateds(List<TraitConcept> relateds) throws Exception {
		boolean returnVal = false;
		if (relateds != null && !relateds.isEmpty()) {
			returnVal = true;
		} else {
			throw new Exception(MISSING_RELATED);
		}
		return (returnVal);
	}

	public boolean valideSons(List<TraitConcept> sons) throws Exception {
		boolean returnVal = false;
		if (sons != null && !sons.isEmpty()) {
			returnVal = true;
		} else {
			throw new Exception(MISSING_SONS);
		}
		return (returnVal);
	}

	public boolean valideCategories(List<TraitConcept> categories) throws Exception {
		boolean returnVal = false;
		if (categories != null && !categories.isEmpty()) {
			returnVal = true;
		} else {
			throw new Exception(MISSING_CATEGORIES);
		}
		return (returnVal);
	}

	public String getUri() {
		return (this.uri);
	}

	public String getName() {
		return (this.name);
	}

	public String getUnit() {
		return (this.unit);
	}

	public String getDefinition() {
		return (this.definition);
	}

	public String getReference() {
		return (this.reference);
	}

	public String getAbbreviation() {
		return (this.abbreviation);
	}

	public String getRealName() {
		return (this.realName);
	}

	public String getCategory() {
		return (this.category);
	}

	public List<TraitConcept> getParent() {
		return (this.parent);
	}

	public List<AnnotationConcept> getCommentsList() {
		return (this.commentsList);
	}

	public Map<Integer, List<AnnotationConcept>> getAnnotationsList() {
		return (this.annotationsList);
	}

	public List<TraitConcept> getSynonymsList() {
		return (this.synonymsList);
	}

	public List<TraitConcept> getRelatedsList() {
		return (this.relatedsList);
	}

	public List<TraitConcept> getSonsList() {
		return (this.sonsList);
	}

	public List<TraitConcept> getCategoriesList() {
		return (this.categoriesList);
	}

	public void setUri(String uri) throws Exception {
		try {
			if (valideName(uri)) {
				this.uri = uri;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setName(String name) throws Exception {
		try {
			if (valideName(name)) {
				this.name = name;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setUnit(String unit) throws Exception {
		try {
			if (valideUnit(unit)) {
				this.unit = unit;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setDefinition(String definition) throws Exception {
		try {
			if (valideDefinition(definition)) {
				this.definition = definition;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setReference(String reference) throws Exception {
		try {
			if (valideReference(reference)) {
				this.reference = reference;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setAbbreviation(String abbreviation) throws Exception {
		try {
			if (valideAbbreviation(abbreviation)) {
				this.abbreviation = abbreviation;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setRealName(String realName) throws Exception {
		try {
			if (valideRealName(realName)) {
				this.realName = realName;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setCategory(String category) throws Exception {
		try {
			if (valideCategory(category)) {
				this.category = category;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setParent(List<TraitConcept> parent) throws Exception {
		try {
			if (valideParent(parent)) {
				this.parent = parent;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setCommentsList(List<AnnotationConcept> commentsList) throws Exception {
		try {
			if (valideComments(commentsList)) {
				this.commentsList = commentsList;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setAnnotationsList(Map<Integer, List<AnnotationConcept>> annotationsList) throws Exception {
		try {
			if (valideAnnotations(annotationsList)) {
				this.annotationsList = annotationsList;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setSynonymsList(List<TraitConcept> synonymsList) throws Exception {
		try {
			if (valideSynonyms(synonymsList)) {
				this.synonymsList = synonymsList;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setRelatedsList(List<TraitConcept> relatedsList) throws Exception {
		try {
			if (valideRelateds(relatedsList)) {
				this.relatedsList = relatedsList;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setSonsList(List<TraitConcept> sonsList) throws Exception {
		try {
			if (valideRelateds(sonsList)) {
				this.sonsList = sonsList;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setCategoriesList(List<TraitConcept> categoriesList) throws Exception {
		try {
			if (valideRelateds(categoriesList)) {
				this.categoriesList = categoriesList;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
}
