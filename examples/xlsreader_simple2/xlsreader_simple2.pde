/**
 *    read different values from a xls file.
 *
 *    make sure to look at the data.png to understand how data is layed out in the file.
 *
 *    this example show how to move around in the xls file.
 *
 *    fjenett 20080724
 */

import de.bezier.data.*;

XlsReader reader;


void setup ()
{
    reader = new XlsReader( this, "workbook.xls" );    // assumes file to be in the data folder
    
    reader.firstRow();               // automatically jumps to the first row with content,
                                     // and selects the first cell
    println( reader.getString() );
    
    reader.nextRow();                // next row and cell with content
    println( reader.getInt() );
    
    reader.nextRow();
    println( reader.getFloat() );
}
