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

import java.io.IOException;
import java.io.InputStream;
import java.io.EOFException;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.game.*;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Displayable;

public class CanvasScreen extends GameCanvas implements CommandListener {

	static final int CONTROL_SCREEN = 1;
	static final int FILEMGR_SCREEN = 2;
    	static final int TEXT_SCREEN    = 3;  
    	static final int LIST_SCREEN    = 4;  
    	static final int WMAN_SCREEN    = 5;  

	static final int FG     	= 1;
	static final int BG     	= 2;
	static final int SL_FG     	= 3;
        
	static final int KEY_SEND       = -10;
        static final int KEY_MOTO_SOFTL = -21;
        static final int KEY_MOTO_SOFTR = -22;

	static final int TICKER_CYCLE   = 50;

        Graphics    	gr;
        int         	scr;
        int 		CW;
        int 		CH;
	boolean     	isFullscreen;
        
        Vector		iconCache;
        Vector		iconNameCache;
        Vector		iconRequested;
	
        ControlForm 	cf;
        FileManager 	fm;
        TextScreen  	ts;
        ListForm    	lf;
        WinManager    	wm;
        Controller  	controller;
	
        int     px,dx;
        int     py,dy;
        long   pressTime = 0;

	CanvasConsumer currentScreen;
	 
	Integer iconCacheMutex;   
	    
        // ticker-related data 
 	Thread	ticker;
	int   	X;
	int   	Y;
	int   	W;        
        Font    tickFont;
	int   	align;
        int[] 	bg;
        int[] 	fg;
        String 	text;
 	int     xTickerStart;
	int     xTickerLen;
	int     tickerTmout;
	int     tickerStep;
 	int     tTickerCycle;
 	boolean bTickerSkip;
        int     FHTicker;
	Integer drawMutex;
        boolean tickerIsActive;     
        // ticker-related data 

	// popup-related data
        StringBuffer popupText;  // Popup shown if not empty
	
        boolean nokiaPushFix;
	Command nokiaPush;

	public CanvasScreen(Controller ctl) {
		super(false);

		setCommandListener(this);
                
       		controller = ctl;
                
                iconCache       = new Vector();
                iconNameCache   = new Vector();
		iconRequested   = new Vector();
		
		popupText       = new StringBuffer(16);  // enough for "Please wait !" :-)
		
		Image defaultIm = loadImage("file",16); // add default ocon
                iconNameCache.addElement("file16");
                iconCache.addElement(defaultIm); 
                
                px              = -1;
                py              = -1;
		                
                // ticker-related initialization 
                
                //xTickerStart 	= 0;	redundant init
		//xTickerLen    = 0;  redundant init
		//tickerTmout   = 50;
		//tickerStep    = 1;
                //ticker       	= null;	redundant init
                //fSize 		= Font.SIZE_SMALL;	
                align 		= Graphics.LEFT;	
                X		= -1;			// Show we not yet initialized or to show drawing is not needed
		fg 		= new int[3];
		bg 		= new int[3];
                text 		= "";
		drawMutex 	= new Integer(0);
                // ticker-related initialization 
                
		if (controller.mPlatform.indexOf("Nokia") >= 0 && 
		    controller.mPlatform.indexOf("NokiaE") < 0 &&  	// exclude Series 60 (E60, E61 )
		    controller.mPlatform.indexOf("NokiaN") < 0) {	// exclude Series 60 (N..)	
			nokiaPushFix = true;
		//} else {
		//	nokiaPushFix = false;	redundant init
		}	
		nokiaPush = new Command("Push", Command.ITEM, 1);
                
		iconCacheMutex 	= new Integer(0);
	}
        	
	public void init() {

                gr = getGraphics();
                
		//isFullscreen = false;		redundant init
                setFullScreenMode(isFullscreen);

                CW = getWidth();
                CH = getHeight();
               	
		/*InputStream is = Runtime.getRuntime().getClass().getResourceAsStream("/iconset.res");
		if (is != null) {
			try {
				int size = is.available();
				if (size > 2) {
					size = 2;
				}
				byte bt[] = new byte[size];
				is.read(bt);
				useIconSet = (int) Integer.parseInt(new String(bt));
			} catch (IOException e) { }
		}*/
	
		tickerTmout     = 80;	// 12 times per sec
		tickerStep      = (CW > 150 ? ( CW > 300 ? 8 : 4) : 2);

        	cf = new ControlForm(controller);
                // use lazy init for others
        	//fm = new FileManager(controller);
        	//ts = new TextScreen (controller);
        	//lf = new ListForm   (controller);
        	//wm = new WinManager (controller);
		
		currentScreen = (CanvasConsumer) cf;
                scr = -1;	// to make full initialization in show()
	}
	
	public void addToIconCache(String name_sz, Image im) {
        	iconNameCache.addElement(name_sz);
        	iconCache.addElement(im); 
	}
	
	public Image loadCachedImage(String name, int size, boolean useDefault) {
        	//System.out.println("loadCachedImage " + name + " cache size="+iconNameCache.size());
        	//controller.showAlert("loadCachedImage " + name + " cache size="+iconNameCache.size());
        	int sz = iconNameCache.size();
                
                String name_sz = name + String.valueOf(size);
 
 		int found = iconNameCache.indexOf(name_sz);
        	
		Image im = null;
                if (found >= 0) {
			im = (Image) iconCache.elementAt(found);
                } else {
	               	im = loadImage(name,size);
 			
			synchronized (iconCacheMutex) {
                         	if (im != null) {
					addToIconCache(name_sz, im); 
                		}
			}
                }
        	//controller.showAlert("loadCachedImage " + name + (im == null ? " NULL":" OK") +" cache size="+iconNameCache.size());
		return (im == null && useDefault ? (Image) iconCache.elementAt(0) : im);
        }

	public Image loadImage(String name, int size) {
        	//System.out.println("loadImage "+name+" "+size);
	
		try {
			return Image.createImage("/" + String.valueOf(size) + "/" + name+".png");
		} catch (IOException e) {
                        Image ri = controller.rmsHandle(false,null,size,size,name);	// try to search in RMS
			
			String name_sz = name + String.valueOf(size);
			
			if (ri == null && (!iconRequested.contains(name_sz))) {	// need to send request for upload
				controller.protocol.queueCommand("_GET_ICON_(" + String.valueOf(size) + ","+name+")");
				iconRequested.addElement(name_sz);
			}
			return ri;
		}
 	}

	public Image loadImageResource(String name, int size) {
 		try {
			return Image.createImage("/" + String.valueOf(size) + "/" + name+".png");
		} catch (IOException e) { } 
		
		return null;
	}
	
	public Image receiveImage() throws IOException {

                int sz = controller.protocol.iStream.readInt();
		controller.protocol.btoRead -= 4; 
	
	        //System.out.println("receiveImage: image size "+sz);
	        //controller.showAlert("receiveImage: image size "+sz);
		controller.protocol.doNextCommand();
	
                byte[] rgbArray = new byte[sz];
		byte[] bufArray = new byte[1];
		
                // now read all data
		// different devices crashed in different cases, so need to have all of this
		if (controller.readMethod == 0) {
			controller.protocol.iStream.readFully(rgbArray);
			controller.protocol.btoRead -= sz;
                } else  {
		        //int j = 0;
			for (int i=0;i<sz;i++) {	

                		try {
			        	
					if (controller.readMethod == 1) {
						// crash java on Nokia-6288
						int n = controller.protocol.iStream.read(bufArray,0,1);
						rgbArray[i] = bufArray[0];
						
					} else {
						// got EOFException on Samsung-SGH-G600
						rgbArray[i] = controller.protocol.iStream.readByte();
					}
				} catch(EOFException e) { 
					controller.showAlert("EOFException at "+i);
					controller.protocol.doNextCommand();
				
					controller.protocol.btoRead = 1;
				} 
				controller.protocol.btoRead --;
                	}
		
		}
		
	        //controller.showAlert("receiveImage: got "+sz + " bytes");
		//controller.protocol.doNextCommand();
	
                // get trailing ");"
                //byte b = controller.protocol.iStream.readByte();
                //b = controller.protocol.iStream.readByte();
                controller.protocol.iStream.read(bufArray,0,1);
                controller.protocol.iStream.read(bufArray,0,1);
	        controller.protocol.btoRead -= 2;
		
		Image im = null;
		try {
                	im = Image.createImage(rgbArray,0,sz);
		} catch (Exception e) {
			controller.showAlert("Exception/createImage "+e.getClass().getName() + ": " + e.getMessage());
		}
		controller.protocol.doNextCommand();
		
		rgbArray = null;
		return im;
	}

	public CanvasConsumer initIf(int s) {
                if (s == FILEMGR_SCREEN) {
                	if (fm == null) {
                        	fm = new FileManager(controller);
                        }
                        return (CanvasConsumer) fm;
                } else if (s == TEXT_SCREEN) {
                	if (ts == null) {
                        	ts = new TextScreen(controller);
                        }
                        return (CanvasConsumer) ts;
                } else if (s == LIST_SCREEN) {
                	if (lf == null) {
                        	lf = new ListForm(controller);
                        }
                        return (CanvasConsumer) lf;
                } else if (s == WMAN_SCREEN) {
                	if (wm == null) {
                        	wm = new WinManager(controller);
                        }
                	return (CanvasConsumer) wm;
                }
                
                // CONTROL_SCREEN must be initialized
                return (CanvasConsumer) cf;
        }
	
        public void setData(int s, Vector cmdTokens, int stage) {
        	CanvasConsumer disp = initIf(s);
                disp.setData(cmdTokens,stage);
        }
	
        public void popup(Vector v) {
       	        int end = v.size();
                if (end <= 1) return;

		popupText.delete(0, popupText.length());
		
		if (((String) v.elementAt(1)).equals("show")) {
			for (int idx=2;idx<end;idx++) {
				if (idx > 2) {
					popupText.append(", ");
				}
                                popupText.append((String) v.elementAt(idx));
        		}
		}
		currentScreen.drawScreen();
		//System.out.println("CanvasScreen.popup " + popupText.toString());
        }
	
	// draw popup message
        public void drawPopup(int rb, int gb, int bb, int rf, int gf, int bf) {
	
                gr.setClip(0, 0, getWidth(), getHeight());
        	gr.setColor(rb, gb, bb);
		gr.fillRect(0, 0, getWidth(), getHeight());
		
		Font fnt = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_LARGE);
		int FH = fnt.getHeight();
		
		gr.setColor(rf, gf, bf);
		gr.drawString(popupText.toString(), CW>>1, CH>>1, Graphics.TOP|Graphics.HCENTER);
		
		flushGraphics();
	}
        
	public void show(int screen) {
		//System.out.println("CanvasScreen.show (cur="+scr+") = "+screen);

        	if (scr != screen) {
                         currentScreen.hideScreen();
                         scr = screen;
                         
                         currentScreen = initIf(scr);
                }
                CH = getHeight();
       
                currentScreen.showScreen();
	}
	
	/*public void pausedPaint() {
		try { 
			Thread.sleep(100); 
			controller.display.callSerially(new Runnable() { 
				public void run() { 
					currentScreen.paint(); 
				}});
		} catch(Exception z) { } // just to pause execution 
	}*/
	
	public void splitString(Vector text, String newStr, int wMax, Font ff) {
		//System.out.println("splitString");
                if (wMax <= 0) {
                	return;
                }
                
                int idx, idx2;
		int max  = newStr.length();
		
                idx = 0;
                while (idx < max) {
			//System.out.println("splitString while (idx < max) "+idx+"<"+max);
                        idx2 = idx;
                        while (idx2 < max && newStr.charAt(idx2) != '\n' && wMax > ff.stringWidth(newStr.substring(idx,idx2))) {
                                idx2++;
                        }
			if (idx2 == idx && newStr.charAt(idx2) == '\n') {
				//System.out.println("splitString handle new-line");
				// just add new line; it will be lost on next splitString ??
				//text.addElement("");
				idx++;
				continue;
			}

                        if (idx2 == max) {
				//System.out.println("splitString (idx == max) addElement");
                                text.addElement(newStr.substring(idx));
                        } else {
				//System.out.println("splitString while still (idx < max)");
                                if (idx2 > 0 && newStr.charAt(idx2) != '\n') {
                                        idx2--;

                                	// try to split by space
                                        int s;
                                        for (s=0;s<20;s++) {
                                        	if (idx2-s == 0 || newStr.charAt(idx2-s) == ' ') {
                                                        break;
                                                }
                                        }
                                        if (idx2-s > 0 && s < 19) {  // space was found
                                        	idx2 = idx2-s;
                                        }
                                
                                }
                                if (newStr.charAt(idx2) == '\n' && idx2 < (max-1)) {
                                        idx2++;
                                }
				//System.out.println("splitString while (idx < max) addElement");
                                text.addElement(newStr.substring(idx,idx2));
                        }
                        idx = idx2;
                }
		//System.out.println("splitString EXIT");
                return;
	}
        
	public String removeSpecials(String in) {
		if (in == null) {
			return null;
		}
		int w = in.length();
		if (w == 0) {
			return in;
		}
        	String item = in;
                if (item.charAt(w-1) == '\n') {
                	item = item.substring(0,w-1);
                }
	        item = item.replace('\t',' ');
	        return item.replace('\r',' ');
        }

        public Font getFontBySpec(Vector defs, int start) {
        
		int size  = Font.SIZE_MEDIUM;
		int style = Font.STYLE_PLAIN;
                int face  = Font.FACE_PROPORTIONAL;
                
               	while(start<defs.size()) {
                	//System.out.println("getFontBySpec "+start+" "+((String) defs.elementAt(start)));
                        
                        String spec = (String) defs.elementAt(start);
                        if (spec.equals("plain")) {
                        	style = Font.STYLE_PLAIN;
                        } else if (spec.equals("bold")) {
                        	style = (style == Font.STYLE_PLAIN ? Font.STYLE_BOLD : style|Font.STYLE_BOLD);
                        } else if (spec.equals("italic")) {
                        	style = (style == Font.STYLE_PLAIN ? Font.STYLE_ITALIC : style|Font.STYLE_ITALIC);
                        } else if (spec.equals("underlined")) {
                        	style = (style == Font.STYLE_PLAIN ? Font.STYLE_UNDERLINED : style|Font.STYLE_UNDERLINED);
                        } else if (spec.equals("small")) {
                        	size = Font.SIZE_SMALL;
                        } else if (spec.equals("medium")) {
                        	size = Font.SIZE_MEDIUM;
                        } else if (spec.equals("large")) {
                        	size = Font.SIZE_LARGE;
                        } else if (spec.equals("monospace")) {
                        	face  = Font.FACE_MONOSPACE;
                        } else if (spec.equals("system")) {
                        	face  = Font.FACE_SYSTEM;
                        } else if (spec.equals("proportional")) {
                        	face  = Font.FACE_PROPORTIONAL;
                        //} else {
                        //	controller.showAlert("Incorrect font "+spec);
                        }
                	start++;
                }

                Font fn;
                try {
                	fn = Font.getFont(face, style, size);
                } catch (IllegalArgumentException e) {
                	fn = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, size);
                }
                return fn;
        }
        
	public int[] parseColor(String r, String g, String b) {
		int[] RGB = new int[3];
                try {
        		RGB[0] = Integer.parseInt(r);
        		RGB[1] = Integer.parseInt(g);
        		RGB[2] = Integer.parseInt(b);
                        
                        if (RGB[0]<0   || RGB[1]<0   || RGB[2]<0 ||
                            RGB[0]>255 || RGB[1]>255 || RGB[2]>255) {
                         	RGB[0] = -1;   
                        }
		} catch (Exception e) { 
			//controller.showAlert("Exception in parseColor() " + e.getMessage());
                        RGB[0] = -1;
		}
                return RGB;
	}
        
	public void flushFullScreen(int r, int g, int b) { 
        	// it should be in fullscreen mode currently
                gr.setClip(0, 0, getWidth(), getHeight());
        	gr.setColor(r, g, b);
		gr.fillRect(0, 0, getWidth(), getHeight());
		flushGraphics();
	}
        
	public void setFullScreen(String set_fs) {
		//System.out.println("setFullscreen "+set_fs);

		boolean fs;
		if (set_fs.startsWith("on")) {
			fs = true;
		} else if  (set_fs.startsWith("off")) {
			fs = false;
		} else if  (set_fs.startsWith("toggle")) {
			fs = (!isFullscreen);
		} else {
                	return;
                }

        	synchronized (drawMutex) {
                        //controller.showAlert("setFullScreen: Screen Height before "+CH);
                	if (!fs) { 	// ??? Canvas height on init < fullcsreen but (!!!) height on init > not fullscreen
                                // We have to clean-up with bg color of control form all screen
                                // to avoid some 'dead' region near the bottom (Motorola-L6, Sun WTK) 
				currentScreen.fullscreenBkgr();
		        }

		        setFullScreenMode(fs);
                        isFullscreen = fs;
			
                        CW = getWidth();
                        CH = getHeight();                                               
                        //controller.showAlert("setFullScreen: Screen Height after "+CH);
                }
		try { 
			Thread.sleep(500); 
		} catch(Exception z) { } // just to pause execution
		
		if (isFullscreen && cf.exitButtonFix && currentScreen == cf) {	// Add/remove dummmy menu item
			synchronized (drawMutex) {				// to show Exit menu item on Series60
				cf.updateMenu();
			}					
			controller.addToMenu(controller.currDisp);
		}
                currentScreen.showScreen();
	}

	public void updateMenu() {
        	currentScreen.updateMenu();
        }

	public void paint(Graphics g) {
		//System.out.println("CanvasScreen.paint");
                
                tickerIsActive = false;
                
		if (controller.siemensFix) {
			super.paint(g);
		}
		currentScreen.drawScreen();

                tickerIsActive = true;
	}

	protected void keyPressed(int keyCode) {
	        //System.out.println("CanvasScreen.keyPressed"+keyCode);
		if (controller.motoFixMenu &&
		    (keyCode == KEY_MOTO_SOFTL ||
		     keyCode == KEY_MOTO_SOFTR)) {
		     
			stopTicker();				// Motorola RIZR Z3 does not draw 
			controller.display.setCurrent(null);	// lcdui.Command over Canvas
		}
			
		currentScreen.keyPressed(keyCode);
 	}
	
	protected void keyReleased(int keyCode) {
	        //System.out.println("CanvasScreen.keyReleased"+keyCode);
		currentScreen.keyReleased(keyCode);
 	}

	protected void pointerPressed(int x, int y) {
        	dx = px = x;
                dy = py = y;
                pressTime = System.currentTimeMillis();
 	}

	protected void pointerReleased(int x, int y) {
		long now = System.currentTimeMillis();
		
		if (now - pressTime < currentScreen.DRAG_TIMEOUT
		 && Math.abs(py-y)<currentScreen.PRECISION && Math.abs(px-x)<currentScreen.PRECISION)
		    currentScreen.pointerPressed(px,py);
		
		currentScreen.pointerReleased(x,y);
                
                pressTime = 0;
                px = -1;
                py = -1;
 	}

	protected void pointerDragged(int x, int y) {
		if (currentScreen.CLASSIC_DRAG) {
		    px = x;
		    py = y;
		} else {
		    int Dx = dx - x;
		    int Dy = dy - y;
		    dx = x;
		    dy = y;
		    x = px - Dx;
		    y = py - Dy;
		}
		
		currentScreen.pointerDragged(x,y);
 	}

	public void commandAction(Command cmd, Displayable d) {
		currentScreen.commandAction(cmd,d);
	}
        
        // testing only
        /*public int getWidth() {
		return 176; //128;
	}
        public int getHeight() {
		return 128;
	}
	*/

        
        ///////////////////////////////////////////////////
        //
        // ticker part
        //
	
        public void setTVisuals(/*int fsz*/Font ff, int tm, int f0, int f1, int f2, int g0, int g1, int g2, int al, boolean resetStart) {
        	//System.out.println("setTVisuals "+f0+" "+f1+" "+f2+" "+g0+" "+g1+" "+g2+" "+al);
        	
                tickFont     = ff;
        	FHTicker     = ff.getHeight();
        	align        = al;
		//tickerTmout = tm;
		if (resetStart) {
        		xTickerStart = 0;
        	}
        	fg[0] = f0; bg[0] = g0;
        	fg[1] = f1; bg[1] = g1;
        	fg[2] = f2; bg[2] = g2;
        }

	public void setTParams(String s, int x, int y, int w) {
		//System.out.println("setTParams >"+s+"< "+x+" "+y+" "+w);

		if (s.length() == 0 || text.equals(s) && X==x && Y==y && W == w && ticker != null) {
			return;
		}
		
                text        = s;
		xTickerLen  = tickFont.stringWidth(text);
		
		X = x;
                Y = y;
                W = w;

		tTickerCycle = 0;
		xTickerStart = 0;
		bTickerSkip  = false;

		if (tickerStep > 0) {		// move left 
			tickerStep = -tickerStep;
		}
                
		drawTicker(true); // just draw the static text once

		if (xTickerLen > W) {
                	if (ticker == null) {		
				//tickerIsActive  = false;

				//controller.showAlert("setTParams start thread");
				Runnable runnable = new Runnable() {
					public void run() {
						CanvasScreen.this.run();	
					}
				};
				ticker = new Thread(runnable);
				ticker.start();
			}
        	} else {
                	//System.out.println("setTParams drawTicker(true)");
                	ticker = null;		// do not run drawing thread
                        //drawTicker(true); 	// just draw the static text once; do not run drawing thread
        	}
 	}
        
        private void drawTicker(boolean drawStatic) {
                
        	//System.out.println("drawTicker static="+drawStatic);
		
 		if (X < 0) {	// not yet inited or no needs to draw
                        return;
                }
               
                int xTxt = X;
                if (drawStatic && align == Graphics.HCENTER) {
                        xTxt += W/2;
                } else {
			align = Graphics.LEFT;
                        xTxt += xTickerStart;
                }
		
                //System.out.println(""+tTickerCycle+" "+bTickerSkip+" "+tickerStep);	
                //controller.showAlert(""+tTickerCycle+" "+bTickerSkip+" "+tickerStep);	
                if (tickerStep<0 && xTickerStart+xTickerLen < W || 
                    tickerStep>0 && xTickerStart>0) { 			// |<--->|
			if (tTickerCycle > TICKER_CYCLE)  { 		// suspend ticker if string is not so long (5 sec turn-around time)
                		tickerStep = -tickerStep;
			} else {
				//System.out.println("drawTicker <- bTickerSkip = true");	
				bTickerSkip = true;
			}
                
                }
			
                synchronized (drawMutex) {
			//System.out.println("drawTicker synchronized");
			
			if (!bTickerSkip || drawStatic) {	// drawStatic==true - we have to draw, but not to flush
				//System.out.println("drawTicker Y="+Y+" "+xTickerStart+" "+xTickerLen+" "+W+" "+(xTickerStart+xTickerLen));
 				
				gr.setFont(tickFont);
		
				int x = gr.getClipX();
				int y = gr.getClipY();
				int w = gr.getClipWidth();
				int h = gr.getClipHeight();
		
				gr.setClip(X, Y, W, FHTicker);

                		gr.setColor(bg[0], bg[1], bg[2]);    
                		gr.fillRect(X, Y, W, FHTicker); 		     // Erase previous
 				gr.setColor(fg[0], fg[1], fg[2]);    
                		gr.drawString(text, xTxt, Y, Graphics.TOP|align);    // Draw shifted
				
				gr.setClip(x, y, w, h);
                	        xTickerStart += tickerStep;
			}
		
                	if (!drawStatic) {			// drawStatic==true - we have to draw, but not to flush
                		if (!bTickerSkip) {
					//System.out.println("drawTicker flushGraphics X="+X+"Y="+Y+"W="+W+"H="+FHTicker);
					if (controller.fullRepaint) {
						flushGraphics();
					} else {
						int p3 = (controller.seFix ? X+W : W);
						int p4 = (controller.seFix ? Y+FHTicker : FHTicker*3); 	// Why *3 ????
						
						flushGraphics(X, Y, p3, p4);
					}
                                }
				tTickerCycle++;
			
                		if (tTickerCycle > TICKER_CYCLE && bTickerSkip) {
					tickerStep = -tickerStep;	
					bTickerSkip  = false;
					tTickerCycle = 0;
				}
                	}
                }
	}
        
	public void run() {       	
        	//System.out.println("run -> TickerThread");

		while (xTickerLen > W && ticker == Thread.currentThread()) {
			try {
				Thread.sleep(tickerTmout);
				if (ticker == Thread.currentThread() && tickerIsActive) {
                        		drawTicker(false);
				}
                                
			} catch (Exception e) { }
		}
                ticker = null;
	}
	
	public void stopTicker() {
		ticker = null; 
	} 	
		
	protected void sizeChanged(int w, int h) {
		//controller.showAlert("sizeChanged new="+h+" old="+CH);
		if (h != CH) {
			CH = h;
		}
	}
	
        ////////////////////////////////////////////////////////////
	// It is not good idea to use hideNotify/showNotify do detect incoming calls
	// since they also called whan screensaver goes on/off
	
	/*protected void hideNotify() {
                if (controller.fullRepaint) {
                	//currentScreen.paint();
			flushGraphics();
                }
        }*/
        
	protected void showNotify() {
		//controller.showAlert("showNotify");

		if (controller.nokiaFixRepaint || controller.motoFixMenu) {
                        
			try { 
				Thread.sleep(100); 
				controller.display.callSerially(new Runnable() { 
					public void run() { 
						controller.cScreen.flushGraphics(); 
					}});
			} catch(Exception z) { } // just to pause execution 
		}
	}
	
}

