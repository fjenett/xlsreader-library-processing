package de.bezier.data;

import processing.core.*;
import java.io.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.*;

/**
 *	XlsReader, a reader for .xls files (OpenOffice, Microsoft Excel) 
 *	based on the Apache POI library.
 *	<p>
 *	"<a href="http://poi.apache.org/index.html">
 *	Apache POI - Java API To Access Microsoft Format Files</a>"<br />
 *	<a href="http://poi.apache.org/hssf/quick-guide.html#ReadWriteWorkbook">Apache POI QuickRef</a>
 *	<a href="http://poi.apache.org/apidocs/org/apache/poi/hssf/usermodel/">Apache POI Documentation</a>
 *	</p>
 *	<hr />
 *	<small>
 *	<a rel="license" href="http://creativecommons.org/licenses/by-sa/2.0/de/">
 *		<img alt="Creative Commons License" width="40px" style="border-width:0" 
 *			 src="http://i.creativecommons.org/l/by-sa/2.0/de/88x31.png"/>
 *	</a>
 *	This work is licensed under a 
 *	<a rel="license" href="http://creativecommons.org/licenses/by-sa/2.0/de/">
 *		Creative Commons Attribution-Share Alike 2.0 Germany License
 *	</a>. "Excel" and "Office" are trademarks of Microsoft Corporation.
 *	</small>
 *	<hr />
 *
 *	@author		fjenett 20081129
 *	@version	0.0.6
 *
 *	<!-- todo -->
 */

public class XlsReader
{
	private PApplet papplet;
	
	java.util.Iterator rowIterator, cellIterator;
	
	String xlsFile;
	
	HSSFWorkbook workBook;
	
	int sheetNum = -1;
	HSSFSheet sheet;
	
	int rowNum;
	HSSFRow row;
	
	int cellNum;
	HSSFCell cell;
	
	private boolean showWarnings = true;
	
	/**
	*	XlsReader constructor.
	*
	*	@param	thePapplet	Normally just pass "this", which is your sketch
	*	@param	theXlsFile	Path to xls file
	*/
	
	public XlsReader ( PApplet thePapplet, String theXlsFile )
	{
		papplet = thePapplet;
		xlsFile = theXlsFile;
		
		// read xls file
		InputStream is = papplet.createInput(xlsFile);
		
		if ( is == null )
		{
			papplet.println( "ERR, XlsReader(): file not found or unable to read" );
			return;
		}
		
		POIFSFileSystem fs = null;
		
		try 
		{
			fs = new POIFSFileSystem( is );
			workBook = new HSSFWorkbook( fs );
			/** TODO:
			 *	switch to event-api?
			 *	http://poi.apache.org/spreadsheet/how-to.html#event_api
			 */
		} 
		catch ( IOException ioe )
		{
			ioe.printStackTrace();
			papplet.println( "ERR, XlsReader(): file not found or unable to read" );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			papplet.println( "ERR, XlsReader(): general error" );
		}
		
		openSheet( 0 );
	}
	
	
	/**
	 *	Enable or diable warnings. Consider fixing your code rather than just diabling the warnings.
	 *
	 *	@param	enable	Turn warnings on or off.
	 */
	
	public void showWarnings ( boolean enable )
	{
		showWarnings = enable;
	}
	
	
	/**
	*	Jump to a different page (sheet) inside of the xls file. The default page is page 1 (at address 0).
	*
	*	@param	page	Page / sheet number to jump to, first page is at 0
	*/
	
	public void openSheet ( int page )
	{
		if ( workBook == null ) return;
		
		if ( sheetNum != page )
		{
			sheetNum = page;
			sheet = workBook.getSheetAt(sheetNum);
		
			resetAll();
		}
	}
	
	/**
	*	Reset all page specific settings, when opening a new sheet for example.
	*/
	
	private void resetAll ()
	{
		row = null;
		cell = null;
		rowIterator = null;
		cellIterator = null;
		rowNum = -1;
		cellNum = -1;
	}
	
	/**
	*	Read the index of the first not empty row of the current sheet.
	*
	*	@return		int		The index of the first row of this sheet or -1
	*/
	
	public int getFirstRowNum ()
	{
		if ( sheet != null )
			return sheet.getFirstRowNum();
		else
			papplet.println( "ERR, getFirstRowNum(): sheet is null" );
			
		return -1;
	}
	
	/**
	*	Read the index of the current row of the current sheet.
	*
	*	@return		int		The index of the currently selected row of this sheet or -1
	*/
	
	public int getRowNum ()
	{
		if ( sheet != null )
			return rowNum;
		else
			papplet.println( "ERR, getRowNum (): sheet is null" );
		
		return -1;
	}
	
	/**
	*	Read the index of the last available row of the current sheet. Rows inbetween might be empty.
	*
	*	@return		int		The index of the last row of this sheet or -1
	*/
	
	public int getLastRowNum ()
	{
		if ( sheet != null )
			return sheet.getLastRowNum();
		else
			papplet.println( "ERR, getLastRowNum(): sheet is null" );
			
		return -1;
	}
	
	/**
	*	Select the no row (and therfore cell) of current sheet.
	*/
	
	private void resetRow ()
	{
		rowIterator = null;
		initRowIterator();
	}
	
	private void selectRow ( int num )
	{
		if ( sheet == null ) papplet.println( "ERR, selectRow(): sheet is null" );
		row = sheet.getRow( num );
		rowNum = num;
		
		if ( row == null ) papplet.println( "ERR, selectRow(): row returned null" );
	}
	
	private void initRowIterator ()
	{
		if ( sheet == null ) 
		{
			papplet.println( "ERR, initRowIterator(): sheet is null" );
		}
		if ( rowIterator == null )
		{
			rowIterator = sheet.rowIterator();
		}
		if ( rowIterator == null ) 
		{
			papplet.println( "ERR, initRowIterator(): iterator is null" );
		}
	}
	
	/**
	*	Jump to the first row of a sheet.
	*/
	
	public void firstRow ()
	{
		resetRow();
		
		nextRow();
	}
	
	
	/**
	*	Select the next (or first) row of current sheet. First cell of that row will be selected automatically.
	*/
	
	public void nextRow ()
	{
		initRowIterator();
		
		if ( rowIterator.hasNext() )
		{
			row = (HSSFRow)rowIterator.next();
			rowNum = row.getRowNum();
			firstCell();
		}
	}
	
	
	
	/**
	*	Check if the row iterator has more row available to be read.
	*
	*	<p>This can be used in while() loops</p>
	*	<pre>
	*	while ( reader.hasMoreRows() ) {
	*		// jump to the next row
	*		reader.nextRow();
	*
	*		// read cell, eventually loop cells with another while() loop.
	*	}</pre>
	*
	*	@return		boolean		True if more rows are available, false if last row is reached
	*/
	
	public boolean hasMoreRows()
	{
		if ( rowIterator == null )
		{
			resetRow();
		}
		
		return rowIterator.hasNext();
	}
	
	/**
	*	Check if the cell iterator has more cells available to be read for the current row.
	*
	*	<p>This can be used in while() loops</p>
	*	<pre>
	*	while ( reader.hasMoreCells() ) {
	*		// jump to the next cell
	*		reader.nextCell();
	*
	*		// read cell value
	*	}</pre>
	*
	*	@return		boolean		True if more cells are available, false if last cell is reached
	*/
	
	public boolean hasMoreCells()
	{
		if ( cellIterator == null )
		{
			resetCell();
		}
		
		return cellIterator.hasNext();
	}
	
	private int getFirstCellNum ()
	{
		if ( row != null )
			return row.getFirstCellNum();
		else
			papplet.println( "ERR, getFirstCellNum(): row is null" );
			
		return -1;
	}
	
	/**
	*	Returns the index of the currently selected cell.
	*
	*	@return		int		Index of the currently selected cell or -1
	*/
	
	public int getCellNum ()
	{
		if ( sheet != null )
			return cellNum;
		else
			papplet.println( "ERR, getCellNum (): sheet is null" );
		
		return -1;
	}
	
	
	private int getLastCellNum ()
	{
		if ( row != null )
			return row.getLastCellNum();
		else
			papplet.println( "ERR, getLastCellNum(): row is null" );
			
		return -1;
	}
	
	
	/**
	*	Select no cell of the selected row of current sheet. This is needed with firstCell() and nextCell().
	*/
	
	public void resetCell ()
	{
		cellIterator = null;
		cell = null;
		
		if ( row == null ) 
		{
			resetRow();
		}
	}
	
	
	private void initCellIterator ()
	{
		if ( row == null ) firstRow();
		if ( row == null ) 
		{
			papplet.println( "ERR, nextCell(): row is null" );
			return;
		}
		if ( cellIterator == null ) cellIterator = row.cellIterator();
		if ( cellIterator == null ) 
		{
			papplet.println( "ERR, nextCell(): iterator is null" );
		}
	}
	
	
	/**
	*	Jump to the first cell of a row.
	*/
	
	public void firstCell ()
	{
		resetCell();
		nextCell();
	}
	
	
	/**
	*	Select the next (or first) cell of the selected row of current sheet.
	*/
	
	public void nextCell ()
	{
		initCellIterator();
		
		if ( cellIterator.hasNext() )
		{
			cell = (HSSFCell)cellIterator.next();
			cellNum = cell.getCellNum();
		}
	}
	
	/**
	*	Return a String value from the selected cell of the selected row of current sheet.
	*
	*	@return	String The String value of that cell.
	*/
	
	public String getString ()
	{
		if ( cell != null )
		{
			String s = null;
			try {
				s = cell.getRichStringCellValue().toString();
			} catch ( NumberFormatException nfe ) {
				if ( showWarnings ) papplet.println( "ERR, getString(): you are reading a numeric cell as string." );
			}
			if ( s == null )
			{
				s = cell.getNumericCellValue()+"";
			}
			return s;
		}
		else {
			papplet.println( "ERR, no cell selected! call firstCell() first." );
		}
		return null;
	}
	
	/**
	*	Return an int value from the selected cell of the selected row of current sheet.
	*
	*	@return	int The int value of that cell.
	*/
	
	public int getInt ()
	{
		if ( cell != null )
		{
			int i;
			try {
				i = (int)(cell.getNumericCellValue());
				return i;
			} catch ( NumberFormatException nfe ) {
				if ( showWarnings ) papplet.println( "ERR, getInt(): you are reading a text cell as int." );
			}
			i = Integer.parseInt( cell.getRichStringCellValue().toString().trim() );
			return i;
		}
		else {
			papplet.println( "ERR, no cell selected! call firstCell() first." );
		}
		return -1;
	}
	
	/**
	*	Return a float value from the selected cell of the selected row of current sheet.
	*
	*	@return	float The float value of that cell.
	*/
	
	public float getFloat ()
	{
		if ( cell != null )
		{
			float i;
			try {
				i = (float)(cell.getNumericCellValue());
				return i;
			} catch ( NumberFormatException nfe ) {
				if ( showWarnings ) papplet.println( "ERR, getFloat(): you are reading a text cell as float." );
			}
			i = Float.parseFloat( cell.getRichStringCellValue().toString().trim() );
			return i;
		}
		else {
			papplet.println( "ERR, no cell selected! call firstCell() first." );
		}
		return -1;
	}
	
	/**
	*	Return an int value from a specific cell of the current sheet.
	*
	*	@param	rowNum	Row (vertically) to read from. First row is 0.
	*	@param	cellNum Cell (horizontal) in the row to read from. Starts at 0.
	*	@return	int The int value of that cell.
	*/
	
	public int getInt ( int rowNum, int cellNum )
	{
		cell = getCell(rowNum,cellNum);
		
		if ( cell == null ) papplet.println( "ERR, getInt(): cell is null" );
		
		if ( !isCellType( cell, HSSFCell.CELL_TYPE_NUMERIC )) papplet.println( "ERR, getInt(): wrong celltype" );
		
		int i;
		try {
			i = (int)(cell.getNumericCellValue());
			return i;
		} catch ( NumberFormatException nfe ) {
			if ( showWarnings ) papplet.println( "ERR, getInt("+rowNum+","+cellNum+"): you are reading a text cell as int." );
		}
		i = Integer.parseInt( cell.getRichStringCellValue().toString().trim() );
		return i;
	}
	
	/**
	*	Return an float value from a specific cell of the current sheet.
	*
	*	@param	rowNum	Row (vertically) to read from. First row is 0.
	*	@param	cellNum Cell (horizontal) in the row to read from. Starts at 0.
	*	@return	float The float value of that cell.
	*/
	
	public float getFloat ( int rowNum, int cellNum )
	{
		HSSFCell cell = getCell(rowNum,cellNum);
		
		if ( cell == null ) papplet.println( "ERR, getFloat(): cell is null" );
		
		if ( !isCellType( cell, HSSFCell.CELL_TYPE_NUMERIC )) papplet.println( "ERR, getFloat(): wrong celltype" );
		
		float i;
		try {
			i = (float)(cell.getNumericCellValue());
			return i;
		} catch ( NumberFormatException nfe ) {
			if ( showWarnings ) papplet.println( "ERR, getFloat("+rowNum+","+cellNum+"): you are reading a text cell as float." );
		}
		i = Float.parseFloat( cell.getRichStringCellValue().toString().trim() );
		return i;
	}
	
	/**
	*	Return a string value (text) from a specific cell of the current sheet.
	*
	*	@param	rowNum	Row (vertically) to read from. First row is 0.
	*	@param	cellNum Cell (horizontal) in the row to read from. Starts at 0.
	*	@return	String The string value of that cell.
	*/
	
	public String getString ( int rowNum, int cellNum )
	{
		cell = getCell(rowNum,cellNum);
		
		if ( cell == null ) papplet.println( "ERR, getString(): cell is null" );
		
		if ( !isCellType( cell, HSSFCell.CELL_TYPE_STRING )) papplet.println( "ERR, getString(): wrong celltype" );
		
		String s = null;
		try {
			s = cell.getRichStringCellValue().toString();
		} catch ( NumberFormatException nfe ) {
			if ( showWarnings ) papplet.println( "ERR, getString("+rowNum+","+cellNum+"): you are reading a numeric cell as string." );
		}
		if ( s == null )
		{
			s = cell.getNumericCellValue()+"";
		}
		return s;
	}
	
	/**
	*	Get a Cell object of the current sheet at a specific location.
	*
	*	@param	rowNum	Row (vertically) to read from. First row is 0.
	*	@param	cellNum Cell (horizontal) in the row to read from. Starts at 0.
	*	@return	HSSFCell The HSSFCell object.
	*	@see org.apache.poi.hssf.usermodel.HSSFCell
	*/
	
	private HSSFCell getCell (int _rowNum, int cellNum)
	{
		if ( workBook == null ) return null;
		
		if ( sheet == null ) return null;
		
		if ( rowNum != _rowNum || row == null )
		{
			row = sheet.getRow(_rowNum);
			rowNum = _rowNum;
		}
		
		if ( row == null ) return null;
		
		cell = row.getCell((short)cellNum);
		
		return cell;
	}
	
	
	private boolean isCellType (HSSFCell _cell, int type)
	{
		if ( _cell == null ) {
			papplet.println("ERR, isCellType(): given cell is null.");
			return false;
		}
		
		if ( _cell.getCellType() == type ) return true;
		
		switch ( _cell.getCellType() )
		{
			case HSSFCell.CELL_TYPE_STRING: 
				papplet.println( "HSSFCell.CELL_TYPE_STRING" );
				break;
			case HSSFCell.CELL_TYPE_NUMERIC: 
				papplet.println( "HSSFCell.CELL_TYPE_NUMERIC" );
				break;
			case HSSFCell.CELL_TYPE_BLANK: 
				papplet.println( "HSSFCell.CELL_TYPE_BLANK" );
				break;
		}
		return false;
	}
	
}