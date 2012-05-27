//
// anyRemote java client
// a bluetooth remote for your PC.
//
// Copyright (C) 2006-2012 Mikhail Fedotov <anyremote@mail.ru>
// 
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA. 
//
 
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;

interface ICanvasConsumer {

        void showScreen();
	void hideScreen();
	
	void keyPressed (int keyCode);
	void keyReleased(int keyCode);
	void pointerPressed (int x, int y);
	void pointerReleased(int x, int y);
	void pointerDragged (int x, int y);
	
	void commandAction(Command cmd, Displayable d);
	
	public void fullscreenBkgr();
	public void drawScreen();
	public void updateMenu();
        
	static final int FULL     = 0;
	static final int FIRST    = 1;
	static final int INTERMED = 2;
	static final int LAST     = 3;
        
	public void setData(Vector chunk, int stage);
}

abstract class CanvasConsumer implements ICanvasConsumer {
	public boolean	CLASSIC_DRAG	= false;
	public int	DRAG_TIMEOUT	= 350;
	public int 	PRECISION	= 5;
}
