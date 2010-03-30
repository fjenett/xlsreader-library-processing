/**
 *    An example showing how to read many values from a xls file.
 *    Note that many latitude / longitue values are wrong ... i used yahoo to grab them and it reports streets / villages in the us for some cities.
 *
 *    The original dataset (without lat/lon) originates from Eurostat:
 *    http://epp.eurostat.ec.europa.eu/tgm/table.do?tab=table&init=1&plugin=1&language=de&pcode=tgs00013
 *
 *    fjenett 20081111
 */

// import library

import de.bezier.data.*;


// declare variables

XlsReader reader;

String title;


// setup, all the work is done here

void setup ()
{
    size( 600, 400 );

    noStroke();
    fill( 0 );
    background( 255 );
    smooth();

    reader = new XlsReader( this, "Eurostat_Table_tgs00013NoFlagNoDesc.xls" );

    reader.firstRow();

    title = reader.getString();    // the title of the original dataset is in the first row, first cell

    reader.nextRow(); // legend

    while ( reader.hasMoreRows() )    // loop thru rows
    {
        // jump to first data row and its first cell
        // don't forget this or you'll be stuck in an endless loop
        reader.nextRow();
        
        String city = reader.getString();    // city name

        if ( city.indexOf(":") != -1 ) break;    // exit loop once we reach the end of the values at ":=Nicht verfügbar"
                                                 // .. look at the end of the xls file to see what i'm checking against

        reader.nextCell();    // lon
        float x = lonToX( reader.getFloat() );    // read lon value and convert to screen space

        reader.nextCell();    // lat
        float y = latToY( reader.getFloat() );    // read lat value and convert to screen space

        /*
        // many lat/lon values are wrong (USA streets?), you might be able to fix them like this:
        
        XMLElement xml = new XMLElement( this, "http://local.yahooapis.com/MapsService/V1/geocode?appid=YD-9G7bey8_JXxQP6rxl.fBFGgCdNjoDMACQA--&city="+city );

        println( city );
        println( xml.getChild("Result/Longitude").getContent().replace(".",",") +"\t" + 
                 xml.getChild("Result/Latitude").getContent().replace(".",",") );
        
        println();
        
        // now copy-paste the lat-lon value from the console over into the xls file
        */
        
        // draw ellipse where yahoo thinks this city is located at
        ellipse( x, y, 2, 2 );
    }
}

// convert a longitude value to screen space
float lonToX ( float lon )
{
    return map( lon, -180, 180, 0, width );
}

// convert a latitude value to screen space
float latToY ( float lat )
{
    return map( lat, 0, 180, 0, height );
}

