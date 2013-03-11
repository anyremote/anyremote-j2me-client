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

public class FileManager extends CanvasConsumer  {
	Controller  controller;
        
	Command     cd;
	Command     back;
	Command     cp;
	Command     mv;
	Command     mkdir;
	Command     rm;
	Command     cat;
	Command     exec;
	Command     finfo;

	final int LEFT  	= 0;
	final int RIGHT 	= 1;
          	
        int     curPanel;
        
        CanvasPanel[]      panels;
        int                FH;
        
        // used in streamed commands only
        int    panelStream;
        String bufferedItem;
 
	public FileManager(Controller ctl) {
		
		controller = ctl;

                back  = new Command("Back", 	 Command.SCREEN, 4);
                cd    = new Command("GoTo", 	 Command.SCREEN, 4);
                cp    = new Command("Copy", 	 Command.SCREEN, 4);
                mv    = new Command("Move", 	 Command.SCREEN, 4);
                mkdir = new Command("MkDir", 	 Command.SCREEN, 4);
                rm    = new Command("Delete", 	 Command.SCREEN, 4);
                cat   = new Command("View", 	 Command.SCREEN, 4);
                exec  = new Command("Execute",   Command.SCREEN, 4);
                finfo = new Command("File info", Command.SCREEN, 4);

                curPanel   = LEFT;                
                
                panels        = new CanvasPanel[2];
                panels[LEFT]  = new CanvasPanel(ctl, 0xFFFFFF, 0x0000FF, 0xFFFF00, Font.SIZE_SMALL, CanvasPanel.FMGR);
                panels[RIGHT] = new CanvasPanel(ctl, 0xFFFFFF, 0x0000FF, 0xFFFF00, Font.SIZE_SMALL, CanvasPanel.FMGR);
                
                panelStream   = LEFT;
                bufferedItem  = "";
	}
	
	public void changePanel(int Direction) {
                if (curPanel != Direction) {
                        panels[curPanel].showCursor(false);
                        curPanel = Direction;
                        panels[curPanel].showCursor(true);

                        drawScreen();
                }
        }

        public boolean setPanel(boolean replace, Vector vR, int start, int stage) {
        	//System.out.println("FileManager.setPanel ");
                
                int Panel;
                
                if (stage == CanvasConsumer.INTERMED || stage == CanvasConsumer.LAST) {
                	Panel = panelStream;
                } else {				// FIRST || FULL
                	String Pnl = (String) vR.elementAt(2);
                	if (Pnl.equals("left")) {
                		Panel = LEFT;
                	} else if (Pnl.equals("right")) {
                		Panel = RIGHT;
                	} else {
                		return true;
                	}
                        if (stage == CanvasConsumer.FIRST) {
                        	panelStream = Panel;
                        }
		}

                if (replace) {
                	panels[Panel].removeAll();
                        panels[Panel].add(".."); 
                }

		int end = vR.size();
                if (stage == CanvasConsumer.FIRST || stage == CanvasConsumer.INTERMED) {
                	end -= 1;
                }
                boolean addInvisible = (panels[Panel].data.size() >= 2 && panels[Panel].data.size() > panels[Panel].idxEnd+1);
		
		//System.out.println("FileManager.setPanel sz "+panels[Panel].data.size()+" "+start+"->"+end);
                for (int idx=start;idx<end;idx++) {
                	String file = (String) vR.elementAt(idx);
                        if (idx == start && stage == CanvasConsumer.INTERMED || stage == CanvasConsumer.LAST) {
                        	file = bufferedItem + file;
                                bufferedItem = "";
                        }
                        if (!file.equals("") && ! (file.length() == 1 && file.charAt(0) == '\n')) {
				//System.out.println("FileManager.setPanel add "+file);
                        	panels[Panel].add(file);         	
                        }
                }
                if (stage == CanvasConsumer.FIRST || stage == CanvasConsumer.INTERMED) {
                	bufferedItem = (String) vR.elementAt(end);
                }

                return addInvisible;
        }


        //
        // ??? need optimize - remove temporary vector and optimize addToList()
        //
        
        // store panel and last item during stream processing
        //
	public void setData(Vector vR, int stage) {
         	//System.out.println("FileManager.String " + stage);
                
        	if (stage == CanvasConsumer.FULL || stage == CanvasConsumer.FIRST) {
                
                	//System.out.println("FileManager.setData FULL or FIRST "+vR.size());
                        processData(vR, stage);

                } else  if (stage == CanvasConsumer.INTERMED || stage == CanvasConsumer.LAST) {
                
       			//System.out.println("FileManager.setData INTERMED or LAST");
                        
                	boolean addInvisible = setPanel(false, vR, 0, stage);
                	if (controller.cScreen.currentScreen == this && addInvisible){
                		return;		// no needs to repaint
                	}
                	controller.showScr(Controller.FMGR_FORM); // force to repaint both panels
                }        
        }
        
	public void processData(Vector vR, int stage) {   // message = add|replace|show|close[,left|right,_data_]
                
                String oper  = (String) vR.elementAt(1);
		//System.out.println("FileManager.set " + oper);
		
                boolean addInvisible = false;
        	if (oper.indexOf("close")>=0) {
                         controller.showScr(Controller.CONTROL_FORM);
	        	 return;
                } else if (oper.indexOf("select")>=0) {
                	int Panel;
                	int i = Integer.parseInt((String) vR.elementAt(3))-1;
                        String Pnl = (String) vR.elementAt(2);
                        if (Pnl.equals("left")) {
                        	Panel = LEFT;
                        } else if (Pnl.equals("right")) {
                        	Panel = RIGHT;
                        } else {
                        	return;
                        }
                        if (i<0 && i >=  panels[Panel].data.size()) {
                        	return;
                        }
                        panels[Panel].idxSelect = i;
                        panels[Panel].idxStart = (i>2) ? i-2 : i;
                        
                } else if (oper.indexOf("add")>=0) {
                	addInvisible = setPanel(false, vR, 3, stage);
                } else if (oper.indexOf("replace")>=0) {
	        	addInvisible = setPanel(true, vR, 3, stage);
                } else if (oper.indexOf("show")<0) {
                	return;		// Seems command was improperly formed
                }
		
                if (controller.cScreen.currentScreen == this && addInvisible){
                	return;		// no needs to repaint
                }
                controller.showScr(Controller.FMGR_FORM); // force to repaint both panels
	}
        

	public void drawScreen() {
		//System.out.println("FileManager.drawScreen ("+controller.cScreen.CW+","+controller.cScreen.CH+")");
		
		//controller.cScreen.pauseTicker();
		
		try {
			if (controller.cScreen.popupText.length() > 0) {
				controller.cScreen.drawPopup(0xFFFFFF, 0x0000FF);
				return;
			}
			
			int W2 = controller.cScreen.CW/2;
                        int h2 = controller.cScreen.CH-2;
                        int w2 = controller.cScreen.CW-2;
                        int f2 = 1;
			if (controller.cScreen.isFullscreen) {
				f2 = FH+2;
			}
                        synchronized (controller.cScreen.drawMutex) {

				// draw bg
        			controller.cScreen.gr.setClip (0, 0, controller.cScreen.CW, controller.cScreen.CH);
        			controller.cScreen.gr.setColor(0x0000FF);
				controller.cScreen.gr.fillRect(0, 0, controller.cScreen.CW, controller.cScreen.CH);
			
				// draw caption
				//String cap = "File Manager";
                        	controller.cScreen.gr.setColor(0xFFFFFF);
				if (controller.cScreen.isFullscreen) {
					controller.cScreen.gr.drawString("File Manager", W2, 1, Graphics.TOP|Graphics.HCENTER);
				}
				
				// draw borders
                		controller.cScreen.gr.drawLine(1,f2,w2,f2);
                		controller.cScreen.gr.drawLine(1,f2,1,h2);
                		controller.cScreen.gr.drawLine(1,h2,w2,h2);
                		controller.cScreen.gr.drawLine(w2,f2,w2,h2);
                
                		controller.cScreen.gr.drawLine(W2-1,f2,W2-1,h2);
                		controller.cScreen.gr.drawLine(W2+1,f2,W2+1,h2);

                        	panels[LEFT].draw();
                        	panels[RIGHT].draw();
				
				controller.cScreen.flushGraphics();
                	}        
                } catch (Exception e) {
                	controller.showAlert("Exception/FM.drawScreen() " + e.getMessage());
		}
                
		//controller.cScreen.resumeTicker();
	}

	public void redrawPanel(boolean redraw, int which) {
                if (redraw) {
			synchronized (controller.cScreen.drawMutex) {
				panels[which].draw();
                		panels[which].flush();
			}
                }
	}
        
	public void updateMenu() {
		if (controller.cScreen.nokiaPushFix) {
                	controller.menuCommands.addElement(controller.cScreen.nokiaPush);
		}
		controller.menuCommands.addElement(back);
 		controller.menuCommands.addElement(cd);
 		controller.menuCommands.addElement(cp);
		controller.menuCommands.addElement(mv);
		controller.menuCommands.addElement(mkdir);
		controller.menuCommands.addElement(rm);
		controller.menuCommands.addElement(cat);
		controller.menuCommands.addElement(exec);
 		controller.menuCommands.addElement(finfo);
	}
	
	public void keyPressed(int keyCode) {
        	//System.out.println("FileManager.keyPressed " + keyCode);
		int gAction;
                try {
 			gAction = controller.cScreen.getGameAction(keyCode);
  	    	} catch (Exception e) { 
			return;
           	}
 		boolean redraw = false;
                if (gAction == Canvas.UP) {
                    redraw = panels[curPanel].stepUp(true);
                } else if (gAction == Canvas.DOWN) {
                    redraw = panels[curPanel].stepDown(true);
                } else if (gAction == Canvas.LEFT) {
                    changePanel(LEFT);
                } else if (gAction == Canvas.RIGHT) {
                    changePanel(RIGHT);
                } else if (gAction == Canvas.FIRE || keyCode == CanvasScreen.KEY_SEND) {
                       	String pnl = (curPanel == LEFT ? "L" : "R");
                        
                	controller.protocol.queueCommand("GoTo" + pnl + "(" + String.valueOf(panels[curPanel].idxSelect+1) + "," + 
                                                          (String) panels[curPanel].data.elementAt(panels[curPanel].idxSelect) + ")");
                }
                
                if (keyCode == Canvas.KEY_NUM0) {  // force to stop ticker
                	panels[LEFT].switchUseTicker();
                	panels[RIGHT].switchUseTicker();
                        redraw = true;
                } else if (keyCode == Canvas.KEY_STAR) { 
                	panels[curPanel].selectItem();
                        redraw = true;
                } else if (keyCode == Canvas.KEY_NUM1) { 
                	panels[curPanel].goTo(CanvasPanel.START);
                        redraw = true;
                } else if (keyCode == Canvas.KEY_NUM7) { 
                	panels[curPanel].goTo(CanvasPanel.END);
                        redraw = true;
                } else if (keyCode == Canvas.KEY_NUM3) { 
                	panels[curPanel].goTo(CanvasPanel.PGUP);
                        redraw = true;
                } else if (keyCode == Canvas.KEY_NUM9) { 
                	panels[curPanel].goTo(CanvasPanel.PGDN);
                        redraw = true;
                }
                redrawPanel(redraw, curPanel);
  	}

	public void keyReleased(int keyCode) { 
        	panels[curPanel].keyReleased();
        }
	
	public void pointerPressed(int x, int y) {
        
        	int p = LEFT;
        	if (panels[RIGHT].X < x) {
                	p = RIGHT;
                }
                
                panels[p].goToPointer(y);
                
                if (p != curPanel) {
                	changePanel(p);
                } else {                
                	redrawPanel(true, p);
		}
        }
        
	public void pointerReleased(int x, int y) { }
        
	public void pointerDragged(int x, int y) { 
                int p = LEFT;
                if (controller.cScreen.px != -1) {
        		if (panels[RIGHT].X < controller.cScreen.px) {
                		p = RIGHT;
                	}
		} else {
                	return;
                }

                redrawPanel(panels[p].dragPointer(y), p);
        }

	public void commandAction(Command c, Displayable d) {
 		String  cmd  = c.getLabel();
                boolean mCmd = false;
                
                if (panels[curPanel].selIdxes.size() > 0 && 
                    (cmd.equals("Copy") ||
                     cmd.equals("Move") ||
                     cmd.equals("Delete"))) {
                	mCmd = true;
                }
                
             	if (curPanel == LEFT) {
            		cmd += "L";
              	} else {
            		cmd += "R";
             	}
                if (mCmd) {
                        //System.out.println("FileManager.keyPressed multicmd");
			int cap = panels[curPanel].selIdxes.size() - 1;
                        
                        String multi   = "M_";
                        for (int i=0;i<=cap;i++) {
  				// command-by-command variant
                                
                                if (i == cap) {	// last replay, send ordinary replay
                                	multi = "";
                                }
				controller.protocol.queueCommand(multi + cmd + "(" + 
                                                  String.valueOf(((Integer)panels[curPanel].selIdxes.elementAt(i)).intValue()+1) + "," + 
                                                  (String) panels[curPanel].data.elementAt(((Integer)panels[curPanel].selIdxes.elementAt(i)).intValue()) + ")");
               		}
                } else {
			controller.protocol.queueCommand(cmd + "(" + String.valueOf(panels[curPanel].idxSelect+1) + "," + 
                                                     (String) panels[curPanel].data.elementAt(panels[curPanel].idxSelect) + ")");
                }                                                    
	}
        
	public void fullscreenBkgr() {
        	controller.cScreen.flushFullScreen(0x0000FF);
	}

        public void showScreen() {
		//System.out.println("FileManager.showNotify");
		
		controller.cScreen.gr.setFont(panels[curPanel].cpFont);
                //FH = controller.cScreen.gr.getFont().getHeight(); no needs since font can't be changed ))

                int wPanel = controller.cScreen.CW/2 - 6;
                
		int yStart = 3;
		if (controller.cScreen.isFullscreen) {
			yStart = FH+3;
		}
                // Panel coordinates
                panels[LEFT].setSize (3,wPanel,yStart,controller.cScreen.CH-yStart-2);
                panels[RIGHT].setSize(controller.cScreen.CW/2 + 3,wPanel,yStart,controller.cScreen.CH-yStart-2);
                
		panels[curPanel].showCursor(true);
                
        	drawScreen();
	}
	
        public void hideScreen() {
       	        panels[LEFT].removeAll();   // will stop ticker
        	panels[RIGHT].removeAll();
	}
}
