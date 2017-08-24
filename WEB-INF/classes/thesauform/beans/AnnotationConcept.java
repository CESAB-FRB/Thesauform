package thesauform.beans;

/**
 ** Manage annotation concept (couple property value + meta-data)
 **/
public class AnnotationConcept {

	private String property;
	private String value;
	private String date;
	private String id;
	private String creator;
	private static String MISSING_DATE = "Date empty";
	private static String MISSING_ID = "Id empty";
	private static String MISSING_CREATOR = "Creator empty";
	private static String MISSING_PROPERTY = "Property empty";
	private static String MISSING_VALUE = "Value empty";

	public String toString() {
		return ("(property: " + getProperty() + " value: " + getValue() + " creator: " + getCreator() + " date: "
				+ getDate() + " id: " + getId() + ")");
	}

	private boolean valideDate(String date) throws Exception {
		boolean returnVal = false;
		if (date != null && !date.isEmpty()) {
			returnVal = true;
		} else {
			throw new Exception(MISSING_DATE);
		}
		return (returnVal);
	}

	private boolean valideId(String id) throws Exception {
		boolean returnVal = false;
		if (id != null && !id.isEmpty()) {
			returnVal = true;
		} else {
			throw new Exception(MISSING_ID);
		}
		return (returnVal);
	}

	private boolean valideCreator(String creator) throws Exception {
		boolean returnVal = false;
		if (creator != null && !creator.isEmpty()) {
			returnVal = true;
		} else {
			throw new Exception(MISSING_CREATOR);
		}
		return (returnVal);
	}

	private boolean valideProperty(String prop) throws Exception {
		boolean returnVal = false;
		if (prop != null && !prop.isEmpty()) {
			returnVal = true;
		} else {
			throw new Exception(MISSING_PROPERTY);
		}
		return (returnVal);
	}

	private boolean valideValue(String val) throws Exception {
		boolean returnVal = false;
		if (val != null && !val.isEmpty()) {
			returnVal = true;
		} else {
			throw new Exception(MISSING_VALUE);
		}
		return (returnVal);
	}

	public String getDate() {
		return (this.date);
	}

	public String getId() {
		return (this.id);
	}

	public String getCreator() {
		return (this.creator);
	}

	public String getProperty() {
		return (this.property);
	}

	public String getValue() {
		return (this.value);
	}

	public void setDate(String date) throws Exception {
		try {
			if (valideDate(date)) {
				this.date = date;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setId(String id) throws Exception {
		try {
			if (valideId(id)) {
				this.id = id;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setCreator(String creator) throws Exception {
		try {
			if (valideCreator(creator)) {
				this.creator = creator;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setProperty(String prop) throws Exception {
		try {
			if (valideProperty(prop)) {
				this.property = prop;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setValue(String val) throws Exception {
		try {
			if (valideValue(val)) {
				this.value = val;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
}
