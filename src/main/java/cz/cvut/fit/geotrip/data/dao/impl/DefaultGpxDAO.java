package cz.cvut.fit.geotrip.data.dao.impl;

import cz.cvut.fit.geotrip.data.dao.GpxDAO;
import cz.cvut.fit.geotrip.data.entities.GeoCache;
import cz.cvut.fit.geotrip.data.entities.CacheContainer;
import cz.cvut.fit.geotrip.data.entities.GeoPlace;
import cz.cvut.fit.geotrip.data.entities.GeoPoint;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

/**
 *
 * @author jan
 */
public class DefaultGpxDAO implements GpxDAO {

    public static GpxDAO instance = new DefaultGpxDAO();

    private Document document;

    private DefaultGpxDAO() {
        File inputFile = new File("data\\caches.gpx");
        SAXBuilder saxBuilder = new SAXBuilder();
        try {
            document = saxBuilder.build(inputFile);
        } catch (JDOMException | IOException ex) {
            Logger.getLogger(DefaultGpxDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public GeoPlace getRef() {
        Namespace ns = document.getRootElement().getNamespace();
        Element ref = document.getRootElement().getChild("extensions", ns).getChild("ref", ns);

        double lat = parseCacheLat(ref);
        double lon = parseCacheLon(ref);

        String name = ref.getChildText("name", ns);

        return new GeoPlace(new GeoPoint(lat, lon), name);
    }

    @Override
    public Collection<GeoCache> getAllCaches() {
        List<GeoCache> data = new ArrayList<>();

        Namespace rootNs = document.getRootElement().getNamespace();
        List<Element> waypoints = document.getRootElement().getChildren("wpt", rootNs);

        for (Element waypoint : waypoints) {
            GeoCache gc = parseCache(rootNs, waypoint);
            if (gc == null)
                continue;
            data.add(gc);
        }

        return data;
    }

    private GeoCache parseCache(Namespace rootNs, Element waypoint) {
        GeoPoint coordinates = parseCacheCoordinates(waypoint);

        String id = parseCacheId(rootNs, waypoint);

        boolean found = parseCacheFound(rootNs, waypoint);

        String link = parseCacheLink(rootNs, waypoint);

        Namespace ns = Namespace.getNamespace("groundspeak", "http://www.groundspeak.com/cache/1/0");
        Element e = waypoint.getChild("extensions", rootNs).getChild("cache", ns);

        String name = parseCacheName(ns, e);

        CacheContainer container = parseCacheContainer(ns, e);
        if (container == null)
            return null;

        int difficulty = parseCacheDifficulty(ns, e);
        int terrain = parseCacheTerrain(ns, e);

        ns = Namespace.getNamespace("gpxg", "http://geoget.ararat.cz/GpxExtensions/v2");
        e = waypoint.getChild("extensions", rootNs).getChild("GeogetExtension", ns).getChild("Tags", ns);

        int favorites = parseCacheFavorites(e);

        return new GeoCache(coordinates, name, container, difficulty, terrain, favorites, found, id, link);
    }

    private GeoPoint parseCacheCoordinates(Element e) {
        double lat = parseCacheLat(e);
        double lon = parseCacheLon(e);
        return new GeoPoint(lat, lon);
    }

    private double parseCacheLat(Element e) {
        return Double.parseDouble(e.getAttributeValue("lat"));
    }

    private double parseCacheLon(Element e) {
        return Double.parseDouble(e.getAttributeValue("lon"));
    }

    private String parseCacheId(Namespace ns, Element e) {
        return e.getChildText("name", ns);
    }

    private String parseCacheLink(Namespace ns, Element e) {
        return e.getChild("link", ns).getAttributeValue("href");
    }

    private boolean parseCacheFound(Namespace ns, Element e) {
        String foundString = e.getChildText("sym", ns);
        return foundString.equals("Geocache Found");
    }

    private String parseCacheName(Namespace ns, Element e) {
        return e.getChildText("name", ns);
    }

    private CacheContainer parseCacheContainer(Namespace ns, Element e) {
        String size = e.getChildText("container", ns);
        CacheContainer container;

        switch (size) {
            case "Micro":
                container = CacheContainer.MICRO;
                break;
            case "Small":
                container = CacheContainer.SMALL;
                break;
            case "Regular":
                container = CacheContainer.REGULAR;
                break;
            case "Large":
                container = CacheContainer.LARGE;
                break;
            case "Other":
                container = CacheContainer.OTHER;
                break;
            default:
                container = null;
        }

        return container;
    }

    private int parseCacheDifficulty(Namespace ns, Element e) {
        return Math.round(Float.parseFloat(e.getChildText("difficulty", ns)) * 2) - 1;
    }

    private int parseCacheTerrain(Namespace ns, Element e) {
        return Math.round(Float.parseFloat(e.getChildText("terrain", ns)) * 2) - 1;
    }

    private int parseCacheFavorites(Element e) {
        List<Element> tags = e.getChildren();

        for (Element tag : tags) {
            if (tag.getAttributeValue("Category").equals("favorites")) {
                return Integer.parseInt(tag.getValue());
            }
        }

        return 0;
    }
}
