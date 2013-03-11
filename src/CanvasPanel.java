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
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;

public class CanvasPanel {

	static final int TEXT    = 1;
	static final int LIST    = 3;
	static final int FMGR    = 4;

	static final int START   = 1;
	static final int END     = 2;
	static final int PGUP    = 3;
	static final int PGDN    = 4;
        
	static final int ICON_SIZE = 16;
	
        int X, Y, W, H;
	int[]  bg;
	int[]  fg;
	int[]  sl;
	int    idxSelect;
	int    idxStart;
	int    idxEnd;
	int    xShift;
        int    useMode;
        Font   cpFont;
        Vector data;
        Vector icons;
        Vector selIdxes;
        
        boolean showCursor;
        boolean useTicker;
        boolean useIcons;
        
        // autorepeat stuff
        Timer   repeatTimer;
	boolean stillPressed;
	int 	lastKey;
        int 	autoCount;

        Controller controller;
        
	public CanvasPanel(Controller ctl,
                           int fgR, int fgG, int fgB, 
                           int bgR, int bgG, int bgB, 
                           int slR, int slG, int slB,
                           int fsz, int mode) {
                
                Font fn = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, fsz);  
                init(ctl, fgR, fgG, fgB, bgR, bgG, bgB, slR, slG, slB, fn, mode);        
	}
        
	public CanvasPanel(Controller ctl,
                           int fgR, int fgG, int fgB, 
                           int bgR, int bgG, int bgB, 
                           int slR, int slG, int slB,
                           Font fn, int mode) {
 
 		init(ctl, fgR, fgG, fgB, bgR, bgG, bgB, slR, slG, slB, fn, mode);
 	}
        
 	public void init(Controller ctl,
                    int fgR, int fgG, int fgB, 
                    int bgR, int bgG, int bgB, 
                    int slR, int slG, int slB,
                    Font fn, int mode) {

                controller = ctl;
 		fg = new int[3];
		bg = new int[3];
 		sl = new int[3];
               
                fg[0] = fgR; bg[0] = bgR; sl[0] = slR;
                fg[1] = fgG; bg[1] = bgG; sl[1] = slG;
                fg[2] = fgB; bg[2] = bgB; sl[2] = slB;

                cpFont 	   = fn;
                useMode    = mode;
                data       = new Vector();
                icons      = new Vector();
                selIdxes   = new Vector();
                useTicker  = true;
                showCursor = (useMode == LIST);
	}
        
        public void showCursor(boolean show) {
        	if (useMode != TEXT) { 
                        showCursor = show;
        	}
        }

        public void setSize(int x, int w, int y, int h) {
        	//System.out.println("setSize() "+y+" "+h);
		X = x;
                Y = y;
                W = w;
                H = h;
        }

        public void setColor(int what, Vector vR) {
	        if (vR.size() < 5) {
			return;
		}
		
		int[] RGB = controller.cScreen.parseColor((String) vR.elementAt(2),
		                                          (String) vR.elementAt(3),
							  (String) vR.elementAt(4));
                
                if (RGB[0] == -1) {  // Error
                	//System.out.println("setColor() - fails to parse");
                	return;
                }
                
                if (what == CanvasScreen.BG) {
			bg[0] = RGB[0];
			bg[1] = RGB[1];
			bg[2] = RGB[2];
                } else if (what == CanvasScreen.FG){	
			fg[0] = RGB[0];
			fg[1] = RGB[1];
			fg[2] = RGB[2];
          	} else if (what == CanvasScreen.SL_FG){
			sl[0] = RGB[0];
			sl[1] = RGB[1];
			sl[2] = RGB[2];
         	}
        }

        public void setFont(int start, Vector defs) {
                cpFont = controller.cScreen.getFontBySpec(defs, start);
        }

        public void removeAll() {
		
		synchronized (controller.cScreen.drawMutex) {

        		data.removeAllElements();
        		icons.removeAllElements();
        		selIdxes.removeAllElements();

                	if (useMode != TEXT) {		// stop ticker
                		controller.cScreen.stopTicker();
                	}
                }
                useIcons  = false;
                idxSelect = 0;
	        idxStart  = 0;
	        idxEnd    = 0;
                xShift    = 0;
		
	}

        public void add(String content) {
		synchronized (controller.cScreen.drawMutex) {
        		data.addElement(content.replace('\r', ','));
		}
        }

        public void addWithIcon(String content) {
        
        	String icon = "";
                String text = "";
                
        	int idx = content.indexOf(":");
                if (idx <= 0) {
                	add(content);
                } else {
                	icon = content.substring(0,idx).trim();
                	text = content.substring(idx+1).trim().replace('\r', ',');
                }
        
        	int dd = icons.size() - data.size(); // this could be if Set(list,add ...) then Set(iconlist,add...)
                
		synchronized (controller.cScreen.drawMutex) {
        		if (dd > 0) {
				while (dd > 0) {
                        		data.addElement("");
                        		dd--;
                        	}
                	} else if (dd < 0) {
				while (dd < 0) {
                        		icons.addElement(""); //null);
                        		dd++;
                        	}
                	}
                	//System.out.println("ADD " + icon + " " + text);
         		data.addElement (text);
        		icons.addElement(icon); 
		}
                useIcons = true;			
        }

        public void switchUseTicker() {
                if (useTicker) {
                	controller.cScreen.stopTicker();	
                }
        	useTicker = (! useTicker);
                        
        }

	public void goTo(int where) {
		//System.out.println("panel.goTo " + where+ " total="+data.size());
		
		if (where == START) {
			idxStart  = 0;
		} else if (where == END) {
			int FH = cpFont.getHeight()+1;
                	if (useIcons && FH < ICON_SIZE) {
				FH = ICON_SIZE;
                	}
			idxStart = data.size() - H/FH + 2;
		} else if (where == PGDN) {
                	idxStart = idxEnd;
		} else if (where == PGUP) {
                	if (idxStart == idxEnd) {
                        	idxStart--;
                        } else {
                		idxStart = idxStart*2 - idxEnd;
                        }
		}
                
                
                if (idxStart < 0) {
                	idxStart = 0;
                }
                if (idxStart >= data.size()) {
                	idxStart = data.size()-1;
                }
        	if (useMode != TEXT) { 
                        idxSelect = idxStart;
        	}
                //System.out.println("goTo S=" + idxStart + " sel=" + idxSelect + " E="+ idxEnd);
        }

	public void goToPointer(int y) {

                int idx = idxStart;
                int cap = data.size();
                int drawY = Y;
                int FH = controller.cScreen.gr.getFont().getHeight();
                if (useIcons && FH < ICON_SIZE) {
			FH = ICON_SIZE;
                }
 
             	while (cap > idx) {

               		if (drawY < y && y < drawY+FH+1) {
                        	idxSelect = idx;
                                return;	
                        }  
               
                 	drawY += FH+1;
                	if (drawY > Y + H) {
                		break;
                	}
                	idx++;
		}
	}

	public boolean dragPointer(int y) {
        	if (controller.cScreen.py != -1) {
                	int dy = controller.cScreen.py - y;
                        if (dy > 2) {		// scroll down
                		return stepUp(false);
                         } else if (dy < -2) {	// scroll up
                        	return stepDown(false);
                        }
                }
                return false;
	}
                       
	public boolean stepUp(boolean repeatable) {
        	 boolean ret = true;
                 
                 if (useMode == TEXT || idxSelect <= idxStart) {	// Shift all list 
                	if (idxStart <= 0) {
				ret = false;
			} else {
				idxSelect--;
                		idxStart --;
                        }
                 } else {						// Shift only selected item
                 	if (idxSelect <= 0) {
				ret = false;
			} else {
				idxSelect--;
                        }
                }
                 if (ret && repeatable) {
                	startRepeatTimer(Canvas.UP);
                }
                return ret;
        }        

	public boolean stepDown(boolean repeatable) {
        	boolean ret = true;
                
                if (useMode == TEXT || idxSelect >= idxEnd) {	// Shift all list 
                	if (idxEnd >= (data.size() - 1)) {
                		ret = false;
                	} else {
                        	idxStart++;
				idxSelect++;
                        }
              	} else {					// Shift only selected item
                	if ((data.size() - 1) > idxSelect) {
                		idxSelect++;
                	} else {
                		ret = false;
                        }
                }
                
                if (ret && repeatable) {
                	startRepeatTimer(Canvas.DOWN);
                }
                return ret;
        }

        private void check() {
                
                autoCount++;
                if (autoCount<10) { 	// wait a second before autorepeating started
                	return;
                }
        	if (stillPressed && autoCount<100) {
                        boolean redraw = false;
                	if (lastKey == Canvas.UP) {
                        	redraw = stepUp(false);
                        } else if (lastKey == Canvas.DOWN) {
                		redraw = stepDown(false);
                        } else {
                        	return;
                        }
                        
                        if (redraw) {
                        	draw();
                                flush();
                        }
                        return;
                } 
                repeatTimer.cancel();
                repeatTimer = null;
        }
        
	private void startRepeatTimer(int direction) {                
        	if (repeatTimer == null) {
                	lastKey = direction;
                	stillPressed = true;
                        autoCount = 0;
                        
                	TimerTask autorepeat = new TimerTask() {
				public void run() {
					CanvasPanel.this.check();	
				}
			};
                
                	repeatTimer = new Timer();
                        repeatTimer.scheduleAtFixedRate(autorepeat, 0, 100);
                }
        }

	public void keyReleased() {
        	stillPressed = false;
	}
        
        private int searchInSelIdxes(int idx) {
		for (int n=0; n<selIdxes.size(); ++n) {
 			if (((Integer)selIdxes.elementAt(n)).intValue() == idx) {
				return n;			
			}
		}
        	return -1;
        }
        
	public void selectItem() {
        	int i = searchInSelIdxes(idxSelect);
        	if (i < 0) {
        		selIdxes.addElement(new Integer(idxSelect));
                        //System.out.println("CanvasPanel add to selected "+idxSelect);
                } else {
        		selIdxes.removeElementAt(i);
                }
        }

	public boolean moveHor(int shift) {
        	//System.out.println("CanvasPanel.moveHor "+shift);
       		if (useMode != TEXT || (X + xShift> 0 && shift > 0)) {
        		return false;
       		}
            	xShift += shift;
             
                return true;
        }

        // need to call this in syncronyzed wrapper !!!
        public void draw() {
        	//System.out.println("CanvasPanel.draw showCursor="+showCursor+" "+Y+","+H+");
                
                controller.cScreen.gr.setClip(X, Y, W, H);
                controller.cScreen.gr.setFont(cpFont);
                
		int FH = controller.cScreen.gr.getFont().getHeight();
                if (useIcons && FH < ICON_SIZE) {
			FH = ICON_SIZE;
                }

        	controller.cScreen.gr.setColor(bg[0], bg[1], bg[2]);
		controller.cScreen.gr.fillRect(X, Y, W, H);
                
                // Draw lines, one by one
                int idxE  = -1; 
                int drawY = Y;
                boolean isCursor = false;
                int idx = idxStart;
                int cap = data.size();
                
                int delta = -1;
                if (useIcons) {
                	delta = (ICON_SIZE - FH)/2;
                }
                int xStart = (useIcons ? X+ICON_SIZE+1 : X);
                int xWidth = (useIcons ? W-ICON_SIZE-1 : W);
                       
             	while (cap > idx) {
			isCursor = (idx == idxSelect && showCursor) ;
                	int isSel = searchInSelIdxes(idx);
                        //System.out.println("CanvasPanel.draw SELECTED="+idx+"->"+isSel);
                          
                        int yStart = (delta > 0 ? drawY+delta : drawY);

                        if (useIcons) {
                        	//System.out.println("CanvasPanel.draw drawImage");
                                try {
					Image ic = controller.cScreen.loadCachedImage((String) icons.elementAt(idx), 16, true);
					if (ic != null) {
                        			controller.cScreen.gr.drawImage(ic,X, drawY, Graphics.LEFT | Graphics.TOP);
					}
                                } catch (Exception e) { }
                        }
                        
			// draw one line
                	if (isCursor && useTicker) {
                                //System.out.println("CanvasPanel.draw isCursor && useTicker");
                        	if (useMode == FMGR) {
                                	if (isSel == -1) {
                        			controller.cScreen.setTVisuals(cpFont, 80, bg[0], bg[1], bg[2], fg[0], fg[1], fg[2], Graphics.LEFT, false); //true);
                        		} else {
                                		controller.cScreen.setTVisuals(cpFont, 80, bg[0], bg[1], bg[2], sl[0], sl[1], sl[2], Graphics.LEFT, false); //true);
                                	}
                                }
                                
  				// will run ticker if string is long enough
				controller.cScreen.setTParams(controller.cScreen.removeSpecials((String) data.elementAt(idx)), xStart, yStart, xWidth);
                	} else {
                                //System.out.println("CanvasPanel.draw ....");
                        	if (isSel == -1) {
					controller.cScreen.gr.setColor(fg[0], fg[1], fg[2]);
                                } else {
                                	controller.cScreen.gr.setColor(sl[0], sl[1], sl[2]);
                                }
                                if (isCursor) {
                                	controller.cScreen.gr.fillRect(xStart, drawY, xWidth, FH);
                                        controller.cScreen.gr.setColor(bg[0], bg[1], bg[2]);
                                }
				
				//System.out.println("CanvasPanel.draw Y="+yStart);
				controller.cScreen.gr.drawString(controller.cScreen.removeSpecials((String) data.elementAt(idx)), 
                                                 xStart + xShift, yStart, Graphics.TOP|Graphics.LEFT);
                	}
                	                                 
                	idx++;

                	drawY += FH+1;
                	if (drawY > (Y + H - FH*2) && idxE == -1) { 
                        	idxE = idx;
                	}
                	if (drawY > Y + H) {
                		break;
                	}
                }

                if (idxE != -1) {
                	idx = idxE;
                }
                idxEnd = (controller.cScreen.isFullscreen ? idx : idx-1);
        }
                
        public void flush() {
		if (controller.fullRepaint) {
			controller.cScreen.flushGraphics();
			return;
		}
		int p3 = (controller.seFix ? X+W: W);
		int p4 = (controller.seFix ? Y+H: H);
		controller.cScreen.flushGraphics(X, Y, p3, p4);
        }
}
