/**
 *    shows how to read from xls files with multiple pages.
 *
 *    http://bezier.de/processing/libs/xls/
 *    fjenett 20081129
 */

import de.bezier.data.*;  // import library, see menu "Sketch -> Import Library -> ..."

XlsReader reader;         // the xls reader

void setup ()
{
    reader = new XlsReader( this, "Arbeitsmappe1.xls" );  // open xls file for reading
    
    reader.firstRow();              // set reader on first row, first cell
    println( reader.getString() );  // read data from that cell on page 1 (default, is at address 0)
    
    reader.openSheet(1);            // go to page 2 (at address 1)
    reader.firstRow();              // set reader on first row, first cell
    println( reader.getString() );  // read data from that cell on page 2
}
