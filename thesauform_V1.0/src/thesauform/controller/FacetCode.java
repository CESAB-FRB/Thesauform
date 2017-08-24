package thesauform.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import thesauform.model.ThesauformConfiguration;

/**
 * Servlet implementation class facetCode
 */
@WebServlet("/facetCode")
public class FacetCode extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6487314769459745582L;

	protected static final ThesauformConfiguration conf = new ThesauformConfiguration();
	private static final String ERROR_EMPTY_FACET = "Empty facets list";

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter pw = response.getWriter();
		try {
			response.setContentType("text/javascript");
			String js_code = "";
			String[] facet_list_array = ThesauformConfiguration.facet_list.split(";");
			String facets_item = "";
			//test if facet exists
			if (facet_list_array.length != 0) {
				js_code += "var item_template = '<div class=\"item\">' + '<h4><%= obj.name %></h4>' + '<p class=\"tags\">' + ";
				for (String facet_property_list : facet_list_array) {
					String[] facet_name_array = facet_property_list.split(":");
					String facet_code = facet_name_array[0].split("/")[0];
					String facet_name = facet_name_array[0].split("/")[1];
					js_code += "'<% if (obj." + facet_code.replace("'", "") + ") {  %> <%= obj."
							+ facet_name_array[0].split("/")[0].replace("'", "") + " %><% } %>' +";
					facets_item += "'"+facet_code+"':'"+facet_name+"',";
				}
				facets_item = facets_item.substring(0, facets_item.length() - 1);
				js_code += "'</p>' + '</div>';";
			}
			else {
				throw new Exception( ERROR_EMPTY_FACET );
			}
			// create JS function
			js_code += "$(function my_facet(){";
			// create the facet JSON array from file
			//TODO not dummy data, function to select all facet element from data public file
			String json_facet = "{'name':'baba','Facet1':'qualitatif','Facet2':'sure','Facet3':'monday'},{'name':'bibi','Facet1':'quantitatif','Facet2':'not sure','Facet3':'tuesdat'},{'name':'bubu','Facet1':'quantitatif','Facet2':'notsure','Facet3':'tdat'}";
			js_code += ("var item =[" + json_facet + "];");
			//// create the plug-in settings
			js_code += "var settings = {items: item,facets: {";
			// read facet from configuration file
			js_code += facets_item;
			js_code += "}, resultSelector  : '#results', facetSelector   : '#facets', resultTemplate  : item_template, orderByOptions  : {";
			// set order here it is define in configuration file by apparition order
			js_code += ("'name': 'Name'," + facets_item);
			// call the facet plugin0
			js_code += "}};$.facetelize(settings);});";
			pw.println(js_code);
		}
		catch (Exception e) {
			pw.println(e.getMessage());
		}
	}
}
