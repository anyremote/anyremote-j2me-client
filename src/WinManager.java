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
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Displayable;

public class WinManager extends CanvasConsumer {

	Controller 	controller;
	Command 	back;
	Image 		screen;
	int 		imW, imH, curX, curY, dX, dY, curSize;
	boolean 	useCursor;
        boolean 	useDynCursor;
        
        Timer		repeatTimer;
	boolean 	stillPressed;
	int 		lastKey;
        int 		autoCount;

	public WinManager(Controller ctl) {
		controller  = ctl;

		back = new Command("Back", Command.SCREEN, 1);
		
                //redundant init
		//screen       = null;
                //repeatTimer  = null;
                //useDynCursor = false;
                useCursor    = true;
                
                curX = controller.cScreen.CW/2;
                curY = controller.cScreen.CH/2;
                curSize = controller.cScreen.CH/40;
                
	}

	public void updateMenu() {
                controller.menuCommands.addElement(back);
                
		if (controller.cScreen.nokiaPushFix) {
                	controller.menuCommands.addElement(controller.cScreen.nokiaPush);
		}
        }
        
	public void sendCursorPos() {
        	controller.protocol.queueCommand("PosX("+(curX-dX)+",)");
                controller.protocol.queueCommand("PosY("+(curY-dY)+",)");
	}
        
	public void setData(Vector vR, int stage) {
		//System.out.println("WinManager.setData");
        	try {
			String action = controller.protocol.getWord(true);
			//System.out.println("WinManager >"+action+"<");
			//controller.showAlert("WinManager >"+action+"<");
			
			if (action.equals("window") ||
                            action.equals("cover")  ||
                            action.equals("icon")) {
                           
 				//controller.showAlert("got window/icon");
                            	boolean justStore = false;
                                String iName = "";
                            	if (action.equals("icon") || action.equals("cover")) {
                                	justStore = true;
                                        iName = controller.protocol.getWord(true);
                                }
                                 
                                screen = controller.cScreen.receiveImage();
				
				imW = screen.getWidth();
				imH = screen.getHeight();
                                dX = (controller.cScreen.CW - imW)/2;
                                dY = (controller.cScreen.CH - imH)/2;

				if (justStore) {	// Just store it, not show
					if (action.equals("icon")) {
                                        	if (imW == imH && (imW == 16 || imW == 32 || imW == 48 || imW == 64 || imW == 128)) {

							String name_sz = iName + String.valueOf(imW);
							int found = controller.cScreen.iconNameCache.indexOf(name_sz);
							if (found < 0) {
								controller.cScreen.addToIconCache(name_sz,screen); 
							}

							// Redraw if this image was queued for upload from ControlForm
							controller.cScreen.cf.handleIfNeeded(iName,imW,screen);

                                                	int argb[] = new int[imW*imH];
                                        		screen.getRGB(argb,0,imW,0,0,imW,imH);
                        				controller.rmsHandle(true,argb,imW,imH,iName,true);
                                                	argb = null;
							
							found = controller.cScreen.iconNameCache.indexOf(name_sz);
                        			} else {
                					controller.showAlert("Icon does not fit ("+imW+","+imH+")");
                                        	}
					} else { 	// cover
						int found = controller.cScreen.coverNameCache.indexOf(iName);
						if (found < 0) {
							controller.cScreen.addToCoverCache(iName,screen); 
                                                
							int argb[] = new int[imW*imH];
                                        		screen.getRGB(argb,0,imW,0,0,imW,imH);
                        				controller.rmsHandle(true,argb,imW,imH,iName,false);
                                                	argb = null;
						}

						// Redraw if this image was queued for upload from ControlForm
						controller.repaintCanvas();
 					}
                        		return;
                        	}
			} else if (action.equals("set_cursor")) {
                        	int x = Integer.parseInt(controller.protocol.getWord(true));
                        	int y = Integer.parseInt(controller.protocol.getWord(true));
                                curX = x+dX;
                                curY = y+dY;
				//controller.showAlert("Set cursor to("+curX+","+curY+")");
			} else if (action.equals("close")) {
                        	controller.showScr(Controller.CONTROL_FORM);
                                return;
			} else if (action.equals("cursor")) {		// have to repaint (draw or hide cursor cross)
                        	useCursor    = true;
                        	useDynCursor = false;
				if (controller.cScreen.scr != CanvasScreen.WMAN_SCREEN) {
					return;
				}
			} else if (action.equals("dynamic_cursor")) {
                        	useCursor    = true;
                                useDynCursor = true;
				if (controller.cScreen.scr != CanvasScreen.WMAN_SCREEN) {
					return;
				}
			} else if (action.equals("nocursor")) {
                        	useCursor    = false;
                                useDynCursor = false;
				//controller.showAlert("got nocursor");
				if (controller.cScreen.scr != CanvasScreen.WMAN_SCREEN) {
					return;
				}
			} else if (action.equals("remove_all")) {  // deprecatad
				controller.rmsClean(true,true);
				return;
			} else if (action.equals("remove")) {
			        String what = controller.protocol.getWord(true);
				boolean i = (what.equals("all") || what.equals("icons"));
				boolean c = (what.equals("all") || what.equals("covers"));
				controller.rmsClean(i,c);
				return;
			} else if (action.equals("clear_cache")) {
				controller.cScreen.iconNameCache.removeAllElements();
				controller.cScreen.iconCache.removeAllElements();
				return;
			} else if (!action.equals("show")) {
				//System.out.println("WinManager NOT SHOW ???");
				controller.showAlert("WM:unknown cmd");
				return;
			}
			
                } catch(Exception e1) {
                	//System.out.println("Exception at WinManager.setData() " + e1.getClass().getName() + ": " + e1.getMessage());
                	controller.showAlert("Exception/WM.setData(): " + e1.getClass().getName() + ": " + e1.getMessage());
			
			screen = null;
                        
			return;
        	}
		//System.out.println("WinManager SHOW");
		controller.showScr(Controller.WMAN_FORM);
	}

	public void drawScreen() {
		//System.out.println("WinManager.drawScreen W/H"+controller.cScreen.CW+" "+controller.cScreen.CH);
		//System.out.println("WinManager.drawScreen im w/h"+imW+" "+imH);
		
		try {
                	synchronized (controller.cScreen.drawMutex) {
                        
				if (controller.cScreen.popupText.length() > 0) {
					controller.cScreen.drawPopup(0xFFFFFF, 0x000000);
					return;
				}
				
				// draw all in black
				controller.cScreen.gr.setClip(0, 0, controller.cScreen.CW, controller.cScreen.CH);
        			controller.cScreen.gr.setColor(0x000000);
				controller.cScreen.gr.fillRect(0, 0, controller.cScreen.CW, controller.cScreen.CH);
                                
				// draw screen image in center
				if (screen != null) {
					controller.cScreen.gr.drawImage(screen, (controller.cScreen.CW - imW)/2, (controller.cScreen.CH - imH)/2, Graphics.LEFT | Graphics.TOP);
				}
                                
                                if (useCursor) {	// draw cursor
 					controller.cScreen.gr.setColor(0xFFFFFF);
                                	controller.cScreen.gr.drawLine (curX-curSize, curY-1, curX+curSize, curY-1);
                                	controller.cScreen.gr.drawLine (curX-curSize, curY+1, curX+curSize, curY+1);
                                	controller.cScreen.gr.drawLine (curX-1, curY-curSize, curX-1, curY+curSize);
                                	controller.cScreen.gr.drawLine (curX+1, curY-curSize, curX+1, curY+curSize);
					controller.cScreen.gr.setColor(0x000000);
                                	controller.cScreen.gr.drawLine (curX-curSize, curY, curX+curSize, curY);
                                	controller.cScreen.gr.drawLine (curX, curY-curSize, curX, curY+curSize);
				}
                                
				controller.cScreen.flushGraphics();
			} // synchronized
                        
       		} catch (Exception e) {
                	controller.showAlert("Exception/WM.drawScreen() "+e.getMessage());
		}
	}

        private void check() {
        	//System.out.println("WinManager.check "+stillPressed);
                
                autoCount++;
                if (autoCount<10) { 	// wait a second before autorepeating started
                	return;
                }
        	if (stillPressed) {
                	keyPressed(lastKey);
                } else {
                	repeatTimer.cancel();
                        repeatTimer = null;
        	}
        }
        
        private void jPressed() {
        	if (useCursor) {
                	controller.protocol.queueCommand("PressedX("+(curX-dX)+",)");
                	controller.protocol.queueCommand("PressedY("+(curY-dY)+",)");
                }
        }
                                
	public void keyPressed(int keyCode) {
		//System.out.println("WinManager.keyPressed "+keyCode);
                
                boolean needPaint = true;
                int gAction = 0;
                try {
                       gAction = controller.cScreen.getGameAction(keyCode);
		} catch (Exception e) {
                       //controller.showAlert("Exception in WinManager.keyPressed() " + e.getMessage());
                       //needPaint = false;
                       return;
               	}
                
                boolean isRepeatable = false;
 		switch (gAction) {
			case Canvas.UP:
                		if (curY > dY) curY--;
                                isRepeatable = true;
				break;
                	case Canvas.DOWN:
                        	if (curY < (controller.cScreen.CH - dY)) curY++;
                                isRepeatable = true;
 				break;
                	case Canvas.LEFT:
                 		if (curX > dX) curX--;
                                isRepeatable = true;
				break;
                	case Canvas.RIGHT:
                         	if (curX < (controller.cScreen.CW - dX)) curX++;
                                isRepeatable = true;
				break;
                	case Canvas.FIRE:
                        	jPressed();
				break;
			default:
				needPaint = false;
                }
                if (isRepeatable && useDynCursor) {
                	sendCursorPos();
                }
                
                if (isRepeatable && repeatTimer == null) {
                	lastKey = keyCode;
                	stillPressed = true;
                        autoCount = 0;
                        
                	TimerTask autorepeat = new TimerTask() {
				public void run() {
					WinManager.this.check();	
				}
			};
                
                	repeatTimer = new Timer();
                        repeatTimer.scheduleAtFixedRate(autorepeat, 0, 100);
                }
                
                if (needPaint) {
                	drawScreen();
                }
                return;
 	}
	
	public void keyReleased(int keyCode) {
		//System.out.println("WinManager keyReleased"+keyCode);
        	stillPressed = false;
	}

	public void pointerPressed(int x, int y) {

        	if (y < dY) 
                	y = dY;
                else if (y > (controller.cScreen.CH - dY)) 
                	y = controller.cScreen.CH - dY;
        	if (x < dX) 
                	x = dX;
                else if (x > (controller.cScreen.CW - dX)) 
                	x = controller.cScreen.CW - dX;
                        
                curY = y;
                curX = x;
        
                jPressed();
	}
        
	public void pointerReleased(int x, int y) { }
	public void pointerDragged (int x, int y) { }

	public void commandAction(Command cmd, Displayable d) {
		//System.out.println("WinManager.commandAction "+cmd.getLabel());
                
		if (cmd == controller.cScreen.nokiaPush) { // emulate joystick push by menu item on Nokia Series 40
			jPressed();
		} else {
                        controller.protocol.queueCommand(cmd.getLabel());
                }
	}
        
	public void fullscreenBkgr() {
        	controller.cScreen.flushFullScreen(0x000000);
	}
                
	public void showScreen() {
		//System.out.println("WinManager.showScreen");
		drawScreen();
	}

	public void hideScreen() {}
}
