package thesauform.beans;

import java.util.List;
import java.util.Map;

public class TraitConceptVote {
	private String uri;
	// new trait or not
	private boolean isInserted;
	private Integer nbInsertVote;
	// list of persons who have proposed the delete
	private List<String> deleteList;
	private Integer nbDeleteVote;
	// update property should have vote associated by proposition + be
	// diffentiated by valide/to vote
	private Map<String, Map<String, TraitVoteValue>> nameList;
	private Map<String, Map<String, TraitVoteValue>> unitList;
	private Map<String, Map<String, TraitVoteValue>> definitionList;
	private Map<String, Map<String, TraitVoteValue>> referenceList;
	private Map<String, Map<String, TraitVoteValue>> abbreviationList;
	private Map<String, Map<String, TraitVoteValue>> categoryList;
	private List<AnnotationConcept> commentList; // No vote associated
	private Map<String, Map<String, TraitVoteValue>> synonymList;
	private Map<String, Map<String, TraitVoteValue>> relatedList;

	private static final String MISSING_URI = "URI is empty";
	//private static final String MISSING_IS_INSERTED = "boolean insertion is empty";
	private static final String MISSING_INSERT_VOTE = "Insert vote number is empty";
	private static final String MISSING_DELETE_VOTE = "delete vote number is empty";
	private static final String MISSING_DELETE = "delete is empty";
	private static final String MISSING_NAME = "Name is empty";
	private static final String MISSING_REFERENCE = "Reference is empty";
	private static final String MISSING_UNIT = "Unit is empty";
	private static final String MISSING_ABBREVIATION = "Abbreviation is empty";
	private static final String MISSING_DEFINITION = "Definition is empty";
	private static final String MISSING_SYNONYM = "Synonyms list is empty";
	private static final String MISSING_RELATED = "Relateds list is empty";
	private static final String MISSING_COMMENT = "Comments list is empty";
	private static final String MISSING_CATEGORY = "Category list is empty";

	// @TODO: filter on Unit
	// private static List<String> UNITS_LIST =
	// Arrays.asList("cm","m","mm","L"); //to complete
	// private static String WRONG_UNIT = "Unit is not managed";

	public String toString() {
		return ("(uri: " + getUri() + " name: " + getNameList() + " unit: " + getUnitList() + " definition: "
				+ getDefinitionList() + " reference: " + getReferenceList() + " synonym: " + getSynonymList() 
				+ " abbreviation: " + getAbbreviationList() + " category :" + getCategoryList() + ")");
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

	public boolean valideNbInsertVote(Integer nbInsertVote) throws Exception {
		boolean returnVal = false;
		if (uri != null && !uri.isEmpty()) {
			returnVal = true;
		} else {
			throw new Exception(MISSING_INSERT_VOTE);
		}
		return (returnVal);
	}

	public boolean valideNbDeleteVote(Integer nbDeleteVote) throws Exception {
		boolean returnVal = false;
		if (uri != null && !uri.isEmpty()) {
			returnVal = true;
		} else {
			throw new Exception(MISSING_DELETE_VOTE);
		}
		return (returnVal);
	}

	public boolean valideIsInserted(boolean isInserted) throws Exception {
		boolean returnVal = false;
		returnVal = true;
		return (returnVal);
	}

	public boolean valideDeleteList(List<String> deleteList) throws Exception {
		boolean returnVal = false;
		if (deleteList != null && !deleteList.isEmpty()) {
			returnVal = true;
		} else {
			throw new Exception(MISSING_DELETE);
		}
		return (returnVal);
	}

	public boolean valideNameList(Map<String, Map<String, TraitVoteValue>> nameList) throws Exception {
		boolean returnVal = false;
		if (nameList != null && !nameList.isEmpty()) {
			returnVal = true;
		} else {
			throw new Exception(MISSING_NAME);
		}
		return (returnVal);
	}

	public boolean valideUnitList(Map<String, Map<String, TraitVoteValue>> unitList) throws Exception {
		boolean returnVal = false;
		if (unitList != null && !unitList.isEmpty()) {
			returnVal = true;
		} else {
			throw new Exception(MISSING_UNIT);
		}
		return (returnVal);
	}

	public boolean valideDefinitionList(Map<String, Map<String, TraitVoteValue>> definitionList) throws Exception {
		boolean returnVal = false;
		if (definitionList != null && !definitionList.isEmpty()) {
			returnVal = true;
		} else {
			throw new Exception(MISSING_DEFINITION);
		}
		return (returnVal);
	}

	public boolean valideReferenceList(Map<String, Map<String, TraitVoteValue>> referenceList) throws Exception {
		boolean returnVal = false;
		if (referenceList != null && !referenceList.isEmpty()) {
			returnVal = true;
		} else {
			throw new Exception(MISSING_REFERENCE);
		}
		return (returnVal);
	}

	public boolean valideAbbreviationList(Map<String, Map<String, TraitVoteValue>> abbreviationList) throws Exception {
		boolean returnVal = false;
		if (abbreviationList != null && !abbreviationList.isEmpty()) {
			returnVal = true;
		} else {
			throw new Exception(MISSING_ABBREVIATION);
		}
		return (returnVal);
	}

	public boolean valideCommentList(List<AnnotationConcept> commentList) throws Exception {
		boolean returnVal = false;
		if (commentList != null && !commentList.isEmpty()) {
			returnVal = true;
		} else {
			throw new Exception(MISSING_COMMENT);
		}
		return (returnVal);
	}

	public boolean valideCategoryList(Map<String, Map<String, TraitVoteValue>> categoryList) throws Exception {
		boolean returnVal = false;
		if (categoryList != null && !categoryList.isEmpty()) {
			returnVal = true;
		} else {
			throw new Exception(MISSING_CATEGORY);
		}
		return (returnVal);
	}

	public boolean valideSynonymList(Map<String, Map<String, TraitVoteValue>> synonymList) throws Exception {
		boolean returnVal = false;
		if (synonymList != null && !synonymList.isEmpty()) {
			returnVal = true;
		} else {
			throw new Exception(MISSING_SYNONYM);
		}
		return (returnVal);
	}

	public boolean valideRelatedList(Map<String, Map<String, TraitVoteValue>> relatedList) throws Exception {
		boolean returnVal = false;
		if (relatedList != null && !relatedList.isEmpty()) {
			returnVal = true;
		} else {
			throw new Exception(MISSING_RELATED);
		}
		return (returnVal);
	}

	public String getUri() {
		return (this.uri);
	}

	public boolean getIsInserted() {
		return (this.isInserted);
	}

	public Integer getNbDeleteVote() {
		return (this.nbDeleteVote);
	}

	public Integer getNbInsertVote() {
		return (this.nbInsertVote);
	}

	public List<String> getDeleteList() {
		return (this.deleteList);
	}

	public Map<String, Map<String, TraitVoteValue>> getNameList() {
		return (this.nameList);
	}

	public Map<String, Map<String, TraitVoteValue>> getUnitList() {
		return (this.unitList);
	}

	public Map<String, Map<String, TraitVoteValue>> getDefinitionList() {
		return (this.definitionList);
	}

	public Map<String, Map<String, TraitVoteValue>> getReferenceList() {
		return (this.referenceList);
	}

	public Map<String, Map<String, TraitVoteValue>> getAbbreviationList() {
		return (this.abbreviationList);
	}

	public Map<String, Map<String, TraitVoteValue>> getCategoryList() {
		return (this.categoryList);
	}

	public List<AnnotationConcept> getCommentList() {
		return (this.commentList);
	}

	public Map<String, Map<String, TraitVoteValue>> getSynonymList() {
		return (this.synonymList);
	}

	public Map<String, Map<String, TraitVoteValue>> getRelatedList() {
		return (this.relatedList);
	}

	public void setUri(String uri) throws Exception {
		try {
			if (valideUri(uri)) {
				this.uri = uri;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setIsInserted(boolean isInserted) throws Exception {
		try {
			if (valideIsInserted(isInserted)) {
				this.isInserted = isInserted;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setNbDeleteVote(Integer nbDeleteVote) throws Exception {
		try {
			if (valideNbDeleteVote(nbDeleteVote)) {
				this.nbDeleteVote = nbDeleteVote;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setNbInsertVote(Integer nbInsertVote) throws Exception {
		try {
			if (valideNbInsertVote(nbInsertVote)) {
				this.nbInsertVote = nbInsertVote;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setDeleteList(List<String> deleteList) throws Exception {
		try {
			if (valideDeleteList(deleteList)) {
				this.deleteList = deleteList;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setNameList(Map<String, Map<String, TraitVoteValue>> nameList) throws Exception {
		try {
			if (valideNameList(nameList)) {
				this.nameList = nameList;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setUnitList(Map<String, Map<String, TraitVoteValue>> unitList) throws Exception {
		try {
			if (valideUnitList(unitList)) {
				this.unitList = unitList;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setDefinitionList(Map<String, Map<String, TraitVoteValue>> definitionList) throws Exception {
		try {
			if (valideDefinitionList(definitionList)) {
				this.definitionList = definitionList;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setReferenceList(Map<String, Map<String, TraitVoteValue>> referenceList) throws Exception {
		try {
			if (valideReferenceList(referenceList)) {
				this.referenceList = referenceList;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setAbbreviationList(Map<String, Map<String, TraitVoteValue>> abbreviationList) throws Exception {
		try {
			if (valideAbbreviationList(abbreviationList)) {
				this.abbreviationList = abbreviationList;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setCommentList(List<AnnotationConcept> commentList) throws Exception {
		try {
			if (valideCommentList(commentList)) {
				this.commentList = commentList;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setCategoryList(Map<String, Map<String, TraitVoteValue>> categoryList) throws Exception {
		try {
			if (valideCategoryList(categoryList)) {
				this.categoryList = categoryList;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setSynonymList(Map<String, Map<String, TraitVoteValue>> synonymList) throws Exception {
		try {
			if (valideSynonymList(synonymList)) {
				this.synonymList = synonymList;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setRelatedList(Map<String, Map<String, TraitVoteValue>> relatedList) throws Exception {
		try {
			if (valideRelatedList(relatedList)) {
				this.relatedList = relatedList;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setPropertyList(String property, Map<String, Map<String, TraitVoteValue>> propertyList) throws Exception {
		try {
			switch (property) {
			case "name":
				setNameList(propertyList);
				break;
			case "unit":
				setUnitList(propertyList);
				break;
			case "definition":
				setDefinitionList(propertyList);
				break;
			case "reference":
				setReferenceList(propertyList);
				break;
			case "abbreviation":
				setAbbreviationList(propertyList);
				break;
			case "category":
				setCategoryList(propertyList);
				break;
			case "synonym":
				setSynonymList(propertyList);
				break;
			case "related":
				setRelatedList(propertyList);
				break;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

}
