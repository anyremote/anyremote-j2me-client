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
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Font;

public class TextScreen extends CanvasConsumer {

	static final int ADD     = 1;
	static final int REPLACE = 2;
        
	Controller  controller;
	Command     back;
	Command     won;
	Command     woff;
	Command     jup;
	Command     jdow;
	
        String  caption;
        boolean wrap;

	CanvasPanel panel;

	public TextScreen(Controller ctl) {

		controller = ctl;

                back = new Command("Back",        Command.SCREEN, 4);
                won  = new Command("Wrap On",     Command.SCREEN, 4);
                woff = new Command("Wrap Off",    Command.SCREEN, 4);
                jup  = new Command("Jump Up",     Command.SCREEN, 4);
                jdow = new Command("Jump To End", Command.SCREEN, 4);

                wrap	= true;
                caption = "";
                
                panel   = new CanvasPanel(ctl, 0,0,0,255,255,255,0,0,0, Font.SIZE_SMALL, CanvasPanel.TEXT);

	}
	
	public void cleanUp() {
        	if (panel != null) {
        		panel.removeAll();
                }
	}

        public boolean setString(int mode, String newStr) {
		//System.out.println("setString " + mode + ">"+newStr);
                
		boolean addVisible = true;
        	if (mode == REPLACE) {
        		cleanUp();
                } else {
                 	addVisible = (panel.data.size() < 2 || panel.data.size() <= panel.idxEnd+1);
                        
                        try {
                        	newStr = (String) panel.data.elementAt(panel.data.size()-1) + newStr;
                                panel.data.removeElementAt(panel.data.size()-1);
                	} catch (Exception e) { }
                }

                int idx = 0;
                int max = newStr.length() - 1;
 		try {
                	if (wrap) {
                                controller.cScreen.splitString(panel.data, newStr, controller.cScreen.CW-2, panel.cpFont);
                 	} else {
                        	while (idx>=0) {
                                	int idx2 = newStr.indexOf('\n',idx);
                                        if (idx2 > 0 && idx2 < max) {
                                        	idx2++; 
                                        	panel.data.addElement(newStr.substring(idx,idx2));
                                        } else if (idx2 == idx) {
                                        	panel.data.addElement("\n");
                                                idx2++;
                                        } else {
                                        	panel.data.addElement(newStr.substring(idx));
                                        	idx2 = -99;
                                        }
                                 	idx = idx2;
                        	}
                        }
                } catch (Exception e) {
                	//System.out.println("Exception in setString() " + e.getMessage());
			//controller.showAlert("Exception in setString() " + e.getMessage());
		} catch ( OutOfMemoryError e ) {
                	try {
                		panel.data.removeElementAt(panel.data.size()-1);
                        	panel.data.addElement("... No more memory...");
                        } catch ( OutOfMemoryError e1 ) { }
		}
		
		return addVisible;
        }
        
        public void reparseString() {
		//System.out.println("reparseString");
        	StringBuffer txt = new StringBuffer();

                for (int idx=0;idx < panel.data.size();idx++) {
                	txt.append(panel.data.elementAt(idx));
                }
        
                setString(REPLACE,txt.toString());

		txt = null;
        }                

	public void setData(Vector vR, int stage) {
        	
        	if (stage == CanvasConsumer.FULL || stage == CanvasConsumer.FIRST) {
                
                	//System.out.println("TextScreen.setData FULL or FIRST");
                        processData(vR);

                } else if (stage == CanvasConsumer.INTERMED || stage == CanvasConsumer.LAST) {
               		
                        //System.out.println("TextScreen.setData INTERMED or LAST "+ CanvasConsumer.LAST+" "+(String) vR.elementAt(0));
                
 			boolean addVisible = setString(ADD, (String) vR.elementAt(0));
	        	if (controller.cScreen.currentScreen == this && !addVisible) {
                 		// Just draw caption with line number
                        	drawScreen(false);
                        	return;
	        	}
                	controller.showScr(Controller.TEXT_FORM);
                }
        }
	
	// Set(text,add,title,_text_)		3+text
	// Set(text,replace,title,_text_)	3+text
	// Set(text,fg|bg,r,g,b)		6
	// Set(text,font,small|medium|large)	3
	// Set(text,close[,clear])		2 or 3
	// Set(text,show)			2
	
	
	public void processData(Vector vR) {   // message = add|replace|show|clear,title,long_text
		//System.out.println("TextScreen.processData");
		
                String oper = (String) vR.elementAt(1);
		//System.out.println("set >" + oper + "< " + vR.size());
		
             	if (oper.equals("clear")) {
                
			cleanUp();
                        
                } else if (oper.equals("add") || 
		           oper.equals("replace")) {
                
                	int op = REPLACE;
                	if (oper.equals("add")) {
                        	op = ADD;
                        }

                	if (!((String) vR.elementAt(2)).equals("SAME")) {
				caption = (String) vR.elementAt(2);
				//controller.cScreen.setTitle(caption);
                	}

			boolean addVisible = setString(op, (String) vR.elementAt(3));
			if (controller.cScreen.currentScreen == this && !addVisible) {
                        	//System.out.println("text: draw caption only");
                        	// Just draw caption
                                drawScreen(false);
                                return;
			}
            	
                } else if (oper.equals("fg")) {
                
                	panel.setColor(CanvasScreen.FG,(String) vR.elementAt(2),(String) vR.elementAt(3),(String) vR.elementAt(4));
            	
                } else if (oper.equals("bg")) {
		    try {
                	panel.setColor(CanvasScreen.BG,(String) vR.elementAt(2),(String) vR.elementAt(3),(String) vR.elementAt(4));
                   } catch (Exception e) {
                	controller.showAlert("Exception/TS.processData() " + e.getClass().getName());
                   }            	
                } else if (oper.equals("font")) {
                
 			panel.setFont(2, vR);
                        reparseString();
                         
             	} else if (oper.equals("close")) {
                	//System.out.println("list close");
                        if (vR.size() > 2 && ((String) vR.elementAt(2)).equals("clear")) {
                		cleanUp();
                        }
			controller.showScr(Controller.CONTROL_FORM);
                        return;
                        
            	} else if (!oper.equals("show")) {
			return; // seems command improperly formed
                }
		
                //System.out.println("processData showScr");
                controller.showScr(Controller.TEXT_FORM);
	}

	public void setWrap(boolean w) {
                if (wrap != w) {
                 	wrap = w;
                        reparseString();
                }
        }

	public void drawScreen() {
        	drawScreen(true);
        }
        
	public void drawScreen(boolean full) {
        	//System.out.println("drawScreen " + full + " " + panel.data.size());
		try {
			synchronized (controller.cScreen.drawMutex) {
                        
				if (controller.cScreen.popupText.length() > 0) {
					controller.cScreen.drawPopup(panel.bg[0], panel.bg[1], panel.bg[2], panel.fg[0], panel.fg[1], panel.fg[2]);
					return;
				}
				
                        	int FH = panel.cpFont.getHeight();
                        
				controller.cScreen.gr.setClip(0, 0, controller.cScreen.CW, controller.cScreen.CH);
				controller.cScreen.gr.setFont(panel.cpFont);
                        	
                                int h = controller.cScreen.CH;
                                if (!full) {
                                	h = FH+2;
                                }
				
				if (controller.cScreen.isFullscreen) {
                                	String c = caption;
                                	if (panel.data.size() > 25) {	// do not draw number lines for small text
                                		c += "/" + String.valueOf(panel.data.size());
                                	}
					// draw bg
        				controller.cScreen.gr.setColor(panel.bg[0], panel.bg[1], panel.bg[2]);
					controller.cScreen.gr.fillRect(0, 0, controller.cScreen.CW, h);
                                
					// draw caption
					controller.cScreen.gr.setColor(panel.fg[0], panel.fg[1], panel.fg[2]);
					controller.cScreen.gr.drawString(c, controller.cScreen.CW>>1, 1, Graphics.TOP|Graphics.HCENTER);
                			controller.cScreen.gr.drawLine(0,FH+2,controller.cScreen.CW,FH+2);
				}

                                if (full) {
                        		panel.draw();
				}

				if (full || controller.cScreen.isFullscreen) {
					controller.cScreen.flushGraphics();
				}
			}
                } catch (Exception e) {
                	controller.showAlert("Exception/TS.drawScreen() " + e.getClass().getName());
                } catch (OutOfMemoryError e) {
                 	controller.showAlert("OutOfMemory/TS.drawScreen()");
		}
	}

	public void updateMenu() {
		controller.menuCommands.addElement(back);
 		controller.menuCommands.addElement(won);
		controller.menuCommands.addElement(woff);
		controller.menuCommands.addElement(jup);
		controller.menuCommands.addElement(jdow);
        }	

	public void keyPressed(int keyCode) {
        	//System.out.println("TextScreen.keyPressed "+keyCode);
 		int gAction = controller.cScreen.getGameAction(keyCode);
 
 		boolean redraw = false;
                if (gAction == Canvas.UP) {
			redraw = panel.stepUp(true);
                } else if (gAction == Canvas.DOWN) {
			redraw = panel.stepDown(true);
                } else if (gAction == Canvas.LEFT && !wrap) {
			redraw = panel.moveHor(-10);
                } else if (gAction == Canvas.RIGHT && !wrap) {
			redraw = panel.moveHor(10);
                }
                
                if (keyCode == Canvas.KEY_NUM1) {
                	panel.goTo(CanvasPanel.START);
                        redraw = true;
                } else if (keyCode == Canvas.KEY_NUM7) {
                	panel.goTo(CanvasPanel.END);
                        redraw = true;
                } else if (keyCode == Canvas.KEY_NUM3) {
                	panel.goTo(CanvasPanel.PGUP);
                        redraw = true;
                } else if (keyCode == Canvas.KEY_NUM9) {
                	panel.goTo(CanvasPanel.PGDN);
                        redraw = true;
		}
                
                if (redraw) {
			drawScreen(true);
                }
  	}
	
	public void keyReleased    (int keyCode)  { 
        	panel.keyReleased();
        }	
        
        public void pointerPressed (int x, int y) { }
	public void pointerReleased(int x, int y) { }
        
	public void pointerDragged(int x, int y) {
                if (panel.dragPointer(y)) {
			drawScreen(true);
                }
        }
        
	public void commandAction(Command cmd, Displayable d) {
		
		boolean needRepaint = true;
                if (cmd == won) {
                	setWrap(true);
			needRepaint = false;
		} else if (cmd == woff) {
                	setWrap(false);
			needRepaint = false;
		} else if (cmd == jup) {
                	panel.goTo(CanvasPanel.START);
		} else if (cmd == jdow) {
                	panel.goTo(CanvasPanel.END);
                } else {		// custom menu items and Back
                        controller.protocol.queueCommand(cmd.getLabel());
			//needRepaint = false;
                }
		
		if (needRepaint) {
         		drawScreen(true);
		}       
	}
        
	public void fullscreenBkgr() {
        	controller.cScreen.flushFullScreen(panel.bg[0], panel.bg[1], panel.bg[2]);
	}

        public void showScreen() {

		int xStart = 0;
		if (controller.cScreen.isFullscreen) {
			xStart = panel.cpFont.getHeight()+3;
		}
                
                panel.setSize(1,controller.cScreen.CW-2,xStart,controller.cScreen.CH - xStart);

		drawScreen(true);
	}
	
        public void hideScreen() {
	}
}
