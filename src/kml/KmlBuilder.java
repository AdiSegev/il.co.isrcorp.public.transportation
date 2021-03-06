
package kml;

import java.io.File;
import java.util.Map.Entry;

import android.util.Log;

import com.example.il.co.isrcorp.spmcommunicationcore.Utils;
import com.example.publictransport.isr.routes.RouteStop;
import com.example.publictransport.isr.routes.CurrentRoutesInfo;
import com.example.publitransportationintegration.MyUtils;

public class KmlBuilder {
	CurrentRoutesInfo currentRouteInfo;
	
	public KmlBuilder(){
		currentRouteInfo = CurrentRoutesInfo.getInstance();
	}

	/**
	 * This method is responsible on building the KML file for IGO Navigation use.
	 * 
	 */
	public void buildKML() {
		System.out.println("build kml start ");
		String fullKmlContent = addHeader(currentRouteInfo.getRouteName());
	
		fullKmlContent += addRoutesCoords(currentRouteInfo.currentRouteCoordsReceived);

		
		
		for (Entry<Integer,RouteStop> entry : currentRouteInfo.routeStopsList.entrySet()) {
			
			fullKmlContent += addStopPlaceMark(entry.getValue());
		}
		
		fullKmlContent+= addEndKMLFile();
		
		MyUtils.saveKmlFile(currentRouteInfo.currentRouteFileName, fullKmlContent);
		
		System.out.println("build kml end ");
		
	}
	public String addHeader(String rteName){
		String result = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"+"\r\n"+
						"<kml xmlns=\"http://www.opengis.net/kml/2.2\">"+"\r\n"+
						"<Document>"+"\r\n"+
						"<Style id=\"bangormarina\">"+"\r\n"+
						"<LineStyle>"+"\r\n"+
							"<color>fff000ff</color>"+"\r\n"+
							"<width>3</width>"+"\r\n"+
						"</LineStyle>"+"\r\n"+
						"</Style>"+"\r\n"+
						"<Folder>"+"\r\n"+"<Placemark>"+"\r\n"+
						"<name>"+rteName+"</name>"+"\r\n"+
						"<description>"+rteName+"</description>"+"\r\n"+
						"<styleUrl>#bangormarina</styleUrl>"+"\r\n"+
						"<MultiGeometry>"+"\r\n";
							
		return result;
	}
	
	public String addRoutesCoords(String coordinates){
		String result = 
				"<LineString>"+"\r\n"+
					"<coordinates>"+"\r\n"+coordinates+"</coordinates>"+"\r\n"+
				"</LineString>"+"\r\n"+
				"</MultiGeometry>"+"\r\n"+
			"</Placemark>"+"\r\n";
		
		return result;
	}
	public String addStopPlaceMark(RouteStop stop){
		String result = "<Placemark>"+"\r\n"+"<name>"+stop.getStopName()+"</name>" +"\r\n"+
				"<description>"+stop.getStopName()+"</description>" +"\r\n"+
				"<Point>"+"\r\n"+
              "<coordinates>"+stop.getLon()+","+stop.getLat()+"</coordinates>"+"\r\n"+
          "</Point>"+"\r\n"+"</Placemark>"+"\r\n";
		
		return result;
		
	}
	
	public String addEndKMLFile(){
		String result = "</Folder></Document></kml>";
		return result;
	}
	
}
