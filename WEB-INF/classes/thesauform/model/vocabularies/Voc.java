package thesauform.model.vocabularies;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;

public class Voc {
	private static String uri = null;

	public static String getUri() {
		return uri;
	}

	public static void setUri(String uriSpe) {
		uri = uriSpe;
	}

	public static Property getCProperty(String pName) {
		return new PropertyImpl(getUri(), pName);
	}

	public static Resource getCResource(String pName) {
		return new ResourceImpl(getUri(), pName);
	}
}
