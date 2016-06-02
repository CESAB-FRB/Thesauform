package thesauform.model;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;


public interface AnnotationModel {

	public Resource createInsert(Resource concept);
	public Resource createDelete(Resource concept);
	public Resource createComment(Resource concept);
	public Resource createUpdate(Resource concept);
	public void sethasProperty(Resource note, Property p);
	public void sethasValue(Resource note, String value);
	public void sethasValue(Resource note, Resource value);
	
	
}
