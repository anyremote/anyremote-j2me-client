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

import javax.microedition.lcdui.*;

public class SplashScreen extends Canvas implements  Runnable {

	boolean isInitialized;
        boolean splashIsShown;
	Image   ar;
        Font    ssFont;
        int     CW, CH, IW, IH, load;
	Display display;
        Controller controller;
        
	public SplashScreen(Controller ctl) {
        
        	controller = ctl;
        	display =Display.getDisplay(ctl);
                ssFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
                
		if (getHeight() >= 300 && getWidth() >= 270) {
                	try {
        			ar = Image.createImage("/anyRemote256.png");
                   	} catch (Exception e) {}
		}
		
		if (ar == null) {
                	try {
        			ar = Image.createImage("/anyRemote64.png");
                	} catch (Exception e) {}
                }
		
                if (ar != null) {
        		IH = ar.getHeight();
        		IW = ar.getWidth();
                }
        }
        
        public void run(){
                display.setCurrent(this);
                
                repaint();
                serviceRepaints();
                
                while(!isInitialized){
                	try{
                        	Thread.yield();
                        } catch(Exception e){}
                }
        }

        protected void paint(Graphics g) {
        
                CW = getWidth();
                CH = getHeight();
                
                int Y = (CH-IH-16-ssFont.getHeight())>>1;

                g.setClip (0, 0, CW, CH);
                g.setColor(0, 0, 0);
                g.fillRect(0, 0, CW, CH);
                
                g.setColor(255, 255, 255);
                if (ar != null) {
                        g.drawImage(ar, (CW>>1), Y, Graphics.TOP|Graphics.HCENTER);
                }
                
                Y+=IH+5;
                g.setFont(ssFont);
                g.drawString("anyRemote", (CW>>1), Y, Graphics.TOP|Graphics.HCENTER);

                int xl = CW-40;
                Y+=5+ssFont.getHeight();
                
                g.setColor(255, 255, 255);
                g.drawRect(20,Y,xl,6);
        
                g.setColor(0, 0, 0);
                g.fillRect(22,Y+2,xl-4,3);
        
                g.setColor(255, 255, 255);
                g.fillRect(22,Y+2,((xl-4)*load)/100,3);

                splashIsShown=true;
        }
}
