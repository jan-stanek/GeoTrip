package cz.cvut.fit.geotrip.data;

import cz.cvut.fit.geotrip.data.entities.GeoPlace;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class GpxExport {

    private final File file;

    public GpxExport(File file) {
        this.file = file;
    }

    /**
     * Exports current trip to gpx.
     * 
     * @param route list of points
     * @return true if export was successful
     */
    public boolean export(List<GeoPlace> route) {
        Document doc = new Document();

        Namespace ns = Namespace.getNamespace("http://www.topografix.com/GPX/1/0");
        Element gpx = new Element("gpx", ns);
        gpx.setAttribute("version", "1.0");
        gpx.setAttribute("creator", "GeoTrip");
        doc.setRootElement(gpx);

        Element time = new Element("time", ns);
        time.addContent(getCurrentTimestamp());
        gpx.addContent(time);

        Element rte = new Element("rte", ns);
        gpx.addContent(rte);

        for (GeoPlace point : route) {
            Element rtept = new Element("rtept", ns);
            rtept.setAttribute("lat", Double.toString(point.getLat()));
            rtept.setAttribute("lon", Double.toString(point.getLon()));

            Element name = new Element("name", ns);
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

    private String getCurrentTimestamp() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(tz);
        return df.format(new Date());
    }
}
