/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.geotrip;

import cz.cvut.fit.geotrip.geopoint.Cache;
import cz.cvut.fit.geotrip.geopoint.CacheContainer;
import cz.cvut.fit.geotrip.geopoint.GeoPoint;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.mapsforge.core.model.LatLong;

/**
 *
 * @author jan
 */
public class GpxReader {
    private Document document;

    public GpxReader() {
        File inputFile = new File("D:\\Stahování\\GeoTrip\\data.gpx");
        SAXBuilder saxBuilder = new SAXBuilder();
        try {
            document = saxBuilder.build(inputFile);
        } catch (JDOMException ex) {
            Logger.getLogger(GpxReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GpxReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public GeoPoint readRef() {
        Namespace ns = document.getRootElement().getNamespace();
        Element ref = document.getRootElement().getChild("extensions", ns).getChild("ref", ns);
        
        double lat = Double.parseDouble(ref.getAttribute("lat").getValue());
        double lon = Double.parseDouble(ref.getAttribute("lon").getValue());
        String name = ref.getChildText("name", ns);
    
        return new GeoPoint(new LatLong(lat, lon), name);
    }
    
    public List<Cache> readCaches() {
        List<Cache> caches = new ArrayList<>();
        
        Namespace rootNS = document.getRootElement().getNamespace();
        List<Element> waypoints = document.getRootElement().getChildren("wpt", rootNS);
        
        for (Element waypoint : waypoints) {
            double lat = Double.parseDouble(waypoint.getAttribute("lat").getValue());
            double lon = Double.parseDouble(waypoint.getAttribute("lon").getValue());
            
            boolean found = false;
            String foundString = waypoint.getChildText("sym", rootNS);
            if (foundString.equals("Geocache Found"))
                found = true;
        
            String name = waypoint.getChild("link", rootNS).getChildText("text", rootNS);
            
            Namespace n = Namespace.getNamespace("groundspeak", "http://www.groundspeak.com/cache/1/0");
            Element e = waypoint.getChild("extensions", rootNS).getChild("cache", n);
            
            String size = e.getChildText("container", n);
            CacheContainer cacheSize;
            switch(size) {
                case "Micro":
                    cacheSize = CacheContainer.MICRO;
                    break;
                case "Small":
                    cacheSize = CacheContainer.SMALL;
                    break;
                case "Regular":
                    cacheSize = CacheContainer.REGULAR;
                    break;
                case "Large":
                    cacheSize = CacheContainer.LARGE;
                    break;
                case "Other":
                    cacheSize = CacheContainer.OTHER;
                    break;
                default:
                    continue;
            }
            
            int difficulty = Math.round(Float.parseFloat(e.getChildText("difficulty", n)) * 2) - 1;
            
            int terrain = Math.round(Float.parseFloat(e.getChildText("terrain", n)) * 2) - 1;
    
            n = Namespace.getNamespace("gpxg", "http://geoget.ararat.cz/GpxExtensions/v2");
            e = waypoint.getChild("extensions", rootNS).getChild("GeogetExtension", n).getChild("Tags", n);
            
            List<Element> tags = e.getChildren();
            int favorites = 0;
            for (Element el : tags) {
                if (el.getAttributeValue("Category").equals("favorites")) {
                    favorites = Integer.parseInt(el.getValue());
                    break;
                }
            }
            
            caches.add(new Cache(new LatLong(lat, lon), name, CacheContainer.SMALL, difficulty, terrain, favorites, found));
        }
        
        return caches;
    }
}
