package cz.cvut.fit.geotrip.data;

import cz.cvut.fit.geotrip.data.entities.GeoPlace;
import cz.cvut.fit.geotrip.data.entities.GeoPoint;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 *
 * @author jan
 */
public class GpxExport {

    private final File file;

    public GpxExport(File file) {
        this.file = file;
    }

    public boolean export(List<GeoPlace> route) {
        Element gpx = new Element("gpx");
        Document doc = new Document(gpx);

        Element rte = new Element("rte");
        gpx.addContent(rte);

        for (GeoPlace point : route) {
            Element rtept = new Element("rtept");
            rtept.setAttribute("lat", Double.toString(point.getLat()));
            rtept.setAttribute("lon", Double.toString(point.getLon()));

            Element name = new Element("name");
            name.setText(point.getName());
            rtept.addContent(name);

            rte.addContent(rtept);
        }

        XMLOutputter xmlOutput = new XMLOutputter();
        xmlOutput.setFormat(Format.getPrettyFormat());
        
        try (FileOutputStream fos = new FileOutputStream(file)) {
            xmlOutput.output(doc, fos);
        } catch (IOException ex) {
            return false;
        }

        return true;
    }
}
