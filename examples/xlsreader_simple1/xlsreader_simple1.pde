/**
 *    read different values from a xls file.
 *
 *    fjenett 20071115
 */

import de.bezier.data.*;

XlsReader reader;


void setup ()
{
    reader = new XlsReader( this, "workbook.xls" );    // assumes file to be in the data folder
    
    println( reader.getString( 1, 0 ) );    // first value is row, second is cell. both are zero-based
    println( reader.getInt( 2, 0 ) );
    println( reader.getFloat( 3, 0 ) );
}
