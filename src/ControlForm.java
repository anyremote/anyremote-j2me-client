//
// anyRemote java client
// a bluetooth remote for your PC.
//
// Copyright (C) 2006-2013 Mikhail Fedotov <anyremote@mail.ru>
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
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Displayable;

public class ControlForm extends CanvasConsumer {

    Controller  controller;

    Command     exitCommand;
    Command     dummyCommand;
    Command     disconnectCommand;
    
    Image         clickIcon;
    Image  []     icons;
    Image         cover;
        
    String []     iconNames;
    boolean[]     keyPressed;
    
    String        namedCover;

    boolean     useJoystick;
    boolean     useKeypad;
    boolean     useTicker;
    boolean     useVolume;
    boolean     useCover;
    int         yCaption;

    static final int NUM_ICONS     = 12;
    static final int NUM_ICONS_7X1 = 7;
    static final int NUM_INFOLINES = 10;
              
    static final int SK_DEFAULT    = 0;
    static final int SK_7X1 = 1;
    int   skin;
    
    int   startTitleY;
    int   endTitleY;
    
    int   volume;
        
    int[] xOffset;
    int[] yOffset;
    int   icSize;        
    int   curIcon;
    int   realIcon;
    boolean isPressed = false;
    int split = 0;
    
    Font  cfFont;
    
    int   xCorner;
    int   yCorner;
    int   bg;
    int   fg;
    
    String[] infoItem;
    int[]     yInfo;
    int     nInfoItems;
    String   captionItem;
    String   statusItem;

    String   upEvent;
    String   downEvent;

    boolean exitButtonFix;
    boolean joystickFix;
    int     joystickUp;
    int     joystickDown;
    int     joystickLeft;
    int     joystickRight;
    int     joystickPush;

    public ControlForm(Controller ctl) {
        //System.out.println("ControlForm --------------------");
        CLASSIC_DRAG = true;
        DRAG_TIMEOUT = 500;
        controller  = ctl;

        useJoystick = true;
        useKeypad   = true;
        //redundant init
        //useTicker   = false;
        //useVolume   = false;
        //useCover    = false;
        //volume      = 0;
        
        fg = 0xFFFFFF;
        bg = 0x000000;
        
        icons      = new Image[NUM_ICONS];
        iconNames  = new String[NUM_ICONS];
        keyPressed = new boolean[NUM_ICONS];

        //for (int ic=0; ic<NUM_ICONS; ++ic) {
        //    keyPressed[ic] = false;        redundant init
        //}

        xOffset = new int[NUM_ICONS];
        yOffset = new int[NUM_ICONS];

        infoItem    = new String[NUM_INFOLINES];  // Not more than 10 lines of info
        yInfo       = new int   [NUM_INFOLINES];  
        //nInfoItems  = 0;    redundant init
        statusItem  = "";
        captionItem = "";
	namedCover  = "";
                
        upEvent   = "";
        downEvent = "";
                
        cfFont    = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL); // SMALL by default
        yCaption  = cfFont.getHeight() + 3;
        skin       = SK_DEFAULT;

        int sz = controller.cScreen.CH >= 192 ? 
                     (controller.cScreen.CH >= 288 ? 
                         (controller.cScreen.CH >= 320 ? 
                             (controller.cScreen.CH >= 480 ? 
                                 (controller.cScreen.CH >= 640 ? 128 : 96)
                             : 64)
                         : 48)
                     : 32)
                 : 16;

        resetIconSize(sz,false);
        
        curIcon = 4;    // button "5"
        realIcon = curIcon;
        
        //setIconLayout(controller.protocol.splitReplay("Set(icons,SAME,1,vol_down,2,mute,3,vol_up,4,rewind,5,play,6,forward,7,previous,8,stop,9,next,*,default,0,pause,#,default",9999,0),false);
        for(int i=0;i<NUM_ICONS;i++) {
            iconNames[i] = "default";
            icons[i]     = controller.cScreen.loadCachedImage("default",icSize,false);    // suppose "default" image present in *jar
        }
        
        int exitCmdType = Command.BACK;
        
        //cover   = null;    redundant init

        if (controller.mPlatform.indexOf("NokiaE61") >= 0) {
            exitButtonFix = true;
            exitCmdType = Command.SCREEN;
        //} else {
        //    exitButtonFix   = false;    redundant init
        }
                
        //joystickFix   = false;    redundant init
        joystickUp    = -99999;
        joystickDown  = -99999;
        joystickLeft  = -99999;
        joystickRight = -99999;
        joystickPush  = -99999;

        exitCommand  = new Command("Exit", exitCmdType, 2);
        dummyCommand = new Command("_", exitCmdType, 2);
        disconnectCommand = new Command("Disconnect", Command.SCREEN, 4);
    }

    public void updateMenu() {
        controller.menuCommands.addElement(exitCommand);
        
        if (exitButtonFix && controller.cScreen.isFullscreen) {
            controller.menuCommands.addElement(dummyCommand);
        }
        
        if (controller.cScreen.nokiaPushFix) {
            controller.menuCommands.addElement(controller.cScreen.nokiaPush);
        }
        controller.menuCommands.addElement(disconnectCommand);
    }

    public void setVolume(String vol) {
        //System.out.println("setVolume "+vol);
        if (useVolume) {
            int v2 = volume;                        
            try {
                v2 = (int) Integer.parseInt(vol);
            } catch (NumberFormatException e) {
                 //controller.showAlert("Incorrect data in Set(volume,...) command");
            }

            if (v2 < 0) {
                v2 = 0;
            }
            if (v2 > 100) {
                v2 = 100;
            }
            if (v2 != volume) {
                volume = v2;
            }
            controller.showScr(Controller.CONTROL_FORM);
        }
    }

    public void setFontCF(Vector vR) {
        cfFont = controller.cScreen.getFontBySpec(vR, 1);
        yCaption = cfFont.getHeight() + 3;
        if (controller.cScreen.currentScreen == this) {
            showScreen();
        }
      }
     
    public void setCaption(String c) {
        //System.out.println("setCaption "+c);
        captionItem = c;
        controller.cScreen.setTitle(captionItem);
        if (controller.cScreen.isFullscreen) {
            showScreen();
        }
    }
    
    public void setColor(int what, Vector cmdTokens) {
        
        int color;
        try {
            color = controller.cScreen.parseColor(1, cmdTokens);
        } catch (Exception e) {
            return;
        }
        System.out.println("setColor "+what+" " + color);
                
        if (what == CanvasScreen.BG) {
            bg = color;
        } else {    // FG
            fg = color;
        }
        if (controller.cScreen.currentScreen == this) {
            showScreen();
        }
    }

    public void resetIconSize(int sz, boolean reload) {
       int i = 0;
        while (i < 6) {     // 16,32,48,64,96,128
        
            clickIcon = controller.cScreen.loadImageResource("click_icon",sz);    // suppose any *jar contains this
            
            if (clickIcon != null) {
                break;
            }
            if (sz > 128) {      // try to find image of different size 128->96->64->48->32->16->128
                sz = 128;
            } else if (sz > 96) {
                sz = 96;
            } else if (sz > 64) {
                sz = 64;
            } else if (sz > 48) {
                sz = 48;
            } else if (sz > 32) {
                sz = 32;
            } else if (sz > 16) {
                sz = 16;
            } else {
                    sz = 128;
            }
            i++;
        }
        
        icSize = sz;
        PRECISION = icSize / 3;
        setIconXY();
        
        if (reload) {
            for(i=0;i<NUM_ICONS;i++) {
                icons[i] = controller.cScreen.loadCachedImage(iconNames[i],sz,false);
            }
        } 
    }
        
    public void setSkin(Vector vR) {
        //System.out.println("setSkin "+vR.size());
        String name   = (String) vR.elementAt(1);
        
        useJoystick = true;
        useKeypad   = true;
        useCover    = false;
        
        upEvent   = "";
        downEvent = "";
        
        boolean newVolume = false;
        boolean newTicker = useTicker;
        int     newSize   = icSize;
        int     newCur    = curIcon;
        
        boolean oneMore = false;
                
        for (int i=2;i<vR.size();) {
                
            String oneParam = (String) vR.elementAt(i);
            //System.out.println("setSkin >> "+oneParam);
                        
            if (oneMore) {
                try {
                    newCur = btn2int(oneParam);
                } catch (NumberFormatException e) {
                    //controller.showAlert("Set(skin,...,choose,...) command");
                }
                            
                oneMore = false;
            } else if (oneParam.equals("joystick_only")) {
                useJoystick = true;
                useKeypad   = false;
            } else if (oneParam.equals("keypad_only")) {
                useJoystick = false;
                useKeypad   = true;
            } else if (oneParam.equals("ticker")) {
                newTicker   = true;
            } else if (oneParam.equals("noticker")) {
                newTicker   = false;
            } else if (oneParam.equals("volume")) {
                newVolume   = true;
            } else if (oneParam.equals("size16")) {
                newSize = 16;
            } else if (oneParam.equals("size32")) {
                newSize = 32;
            } else if (oneParam.equals("size48")) {
                newSize = 48;
            } else if (oneParam.equals("size64")) {
                newSize = 64;
            } else if (oneParam.equals("size96")) {
                newSize = 96;
            } else if (oneParam.equals("size128")) {
                newSize = 128;
            } else if (oneParam.equals("choose")) {
                oneMore = true;
            } else if (oneParam.equals("up")) {
                i++;
                if (i<vR.size()) {
                    upEvent = (String) vR.elementAt(i);
                }
            } else if (oneParam.equals("down")) {
                i++;
                if (i<vR.size()) {
                    downEvent = (String) vR.elementAt(i);
                }
            } 
            i++;
        }

        int newSkin = skin;
        if (name.equals("default") || name.equals("3x4")) {
            newSkin = SK_DEFAULT;
        } else if (name.equals("7x1") || name.equals("bottomline")) {
	    newSkin = SK_7X1;
	    useCover = (cover != null || namedCover.length() > 0);
        }

        if (skin      != newSkin   || 
            useTicker != newTicker || 
            useVolume != newVolume || 
            icSize    != newSize   ||
            curIcon   != newCur) {
                      
            skin  = newSkin;

            if (useTicker != newTicker) {
                if (!newTicker) {

                    controller.cScreen.stopTicker();
                }
            }
            useTicker = newTicker;
            useVolume = newVolume;

            resetIconSize(newSize, (icSize != newSize));
            setPositions();
            setInfoData();
 
            while (newCur>=0 && xOffset[newCur]<0) newCur--; // cursor should be visible
            curIcon = newCur;

            controller.showScr(Controller.CONTROL_FORM);
        }
	//System.out.println("setSkin EXIT"); 
    }

    public void setStatus(String s) {
        //System.out.println("setStatus "+s);
        //controller.showAlert("setStatus "+s);
        
        int l = s.length();
        int w = controller.cScreen.CW - 4;
        
        while (w < controller.cScreen.gr.getFont().stringWidth(s)) {
            s = s.substring(0,l);
            l--;
        }
        statusItem = s;
        
        controller.showScr(Controller.CONTROL_FORM);
    }

    private void setInfoData(String s) {
        //System.out.println("setInfoData >"+s+"<");
        //controller.showAlert("setInfoData "+s);
        
        if (useTicker) {
            if (s.equals("")) {
                nInfoItems = 0;
            } else { 
                infoItem[0] = s;
                nInfoItems  = 1;
            }
            return;
        }

        //System.out.println("setInfoData not use ticker");
        nInfoItems = 0;
        try {
            Vector text = new Vector();

            controller.cScreen.splitString(text, s, controller.cScreen.CW - 4, cfFont/*fSize ??? */);
            
            //controller.showAlert("splitString to # "+ String.valueOf(nInfoItems) + " font size " + String.valueOf(fSize));
            
            for (int n=0; n<NUM_INFOLINES; ++n) {
                            
                if (n>=text.size()) break;
                //controller.showAlert("infoItem (" + String.valueOf(n) + ") "+ (String) text.elementAt(n));
                infoItem[n] = (String) text.elementAt(n);
                nInfoItems++;
            }
        } catch (IndexOutOfBoundsException e) {     // end of string is reached
            //controller.showAlert("END infoItem[n] "+ String.valueOf(nInfoItems));
            infoItem[nInfoItems] = s;
            nInfoItems++;
        }
    }

    private void setInfoData() {    // Need to reposition the text
        String allInfo = "";
        for (int n=0; n<nInfoItems; ++n) {
            allInfo += infoItem[n];
        }
        setInfoData(allInfo);
    }

    public void setInfo(String s) {
        setInfoData(s);
        controller.showScr(Controller.CONTROL_FORM);
    }
        
    public int btn2int(String btn) {
        int i = -2;
                
        if (btn.equals("*")) {
            i=9;
        } else if (btn.equals("#")) {
            i=11;
        } else {
            try {
                i = Integer.parseInt(btn) - 1;
                if (i == -1) {    // 0 was parsed
                    i = 10;
                }
            } catch (NumberFormatException e) {
                //controller.showAlert("Incorrect button identifier !");
            }
        }
        return i;
    }
        
    public void setIconLayout(Vector data, boolean needRepaint) {    // Input should be: title,n1,name,n2,name2....
        //System.out.println("setIconLayout "+needRepaint + " " + data.size() + " icSize="+icSize);

        if (!((String) data.elementAt(1)).equals("SAME")) {
            captionItem = (String) data.elementAt(1);
            controller.cScreen.setTitle(captionItem);
        }

        for (int idx=2;idx<data.size()-1;idx+=2) {
                        
            try {
                int i = btn2int((String) data.elementAt(idx));

                if (i>=0||i<NUM_ICONS) {
                    icons[i]     = controller.cScreen.loadCachedImage((String) data.elementAt(idx+1),icSize,false);
                    iconNames[i] = (String) data.elementAt(idx+1);
		    //System.out.println("setIconLayout "+i+" "+iconNames[i]);
                }  
            } catch (Exception e) { 
                //System.out.println("exception"+e.getMessage());
            }
        }
        
	if (skin==SK_7X1) {
	    setIconXY();
	}
	
        if (needRepaint) {
            controller.showScr(Controller.CONTROL_FORM);
        }
    }
    
    public void handleIfNeeded(String name, int size, Image img) {
    
        if (size > 0) {   // get updated icon
            for (int i=0;i<NUM_ICONS;i++) {
                if (icons[i] == null && 
                    iconNames[i].equals(name) && 
                    icSize == size) {
                    
		    icons[i] = img; 
                    controller.showScr(Controller.CONTROL_FORM);     // repaint 
                }
            }  
        } else {   // get updated cover
	    if (skin==SK_7X1) {
		controller.showScr(Controller.CONTROL_FORM);     // repaint
	    }
	}
    }
                
    public void redrawIcons() {
         //System.out.println("ControlForm.redrawIcons");

        int xShift = xCorner + split;
        int yShift = yCorner + split;
        
        int pressed = -1;
        for (int ic=0; ic<NUM_ICONS; ++ic) {
            if (xOffset[ic]<0) {    // Skip non-drawable icons at the end of icons array
                break;
            }
            if (icons[ic] != null) {
                controller.cScreen.gr.drawImage(icons[ic],  
                                xOffset[ic] + xShift, 
                                yOffset[ic] + yShift, 
                                Graphics.LEFT|Graphics.TOP);
            }
            if (keyPressed[ic] == true) {
                pressed = ic;
            }
        }
        // need to draw cursor _over_ icons
        //System.out.println("ControlForm.redrawIcons drawCursor "+curIcon);
        drawCursor(xOffset[curIcon] + xShift,yOffset[curIcon] + yShift);
        if (pressed >= 0 && pressed < NUM_ICONS && pressed != curIcon) { 
            //System.out.println("ControlForm.redrawIcons drawCursor "+pressed);
            drawCursor(xOffset[pressed] + xShift,yOffset[pressed] + yShift);
        }
    }
    
    public void drawScreen() {
        //System.out.println("ControlForm.drawScreen "+useCover);
        //controller.showAlert("ControlForm.drawScreen");
        
        setPositions(); // Recalculate positions
        
        try {
            synchronized (controller.cScreen.drawMutex) {
            
                if (controller.cScreen.popupText.length() > 0) {
                    controller.cScreen.drawPopup(fg, bg);
                    return;
                }
                        
                controller.cScreen.gr.setClip(0, 0, controller.cScreen.CW, controller.cScreen.CH);
                controller.cScreen.gr.setColor(bg);
                controller.cScreen.gr.fillRect(0, 0, controller.cScreen.CW, controller.cScreen.CH);
                                
                controller.cScreen.gr.setFont(cfFont);

                if (controller.cScreen.isFullscreen) {
                    controller.cScreen.gr.setColor(fg);
                    controller.cScreen.gr.drawString(captionItem, controller.cScreen.CW>>1, 1, Graphics.TOP|Graphics.HCENTER);
                    controller.cScreen.gr.drawLine(0,yCaption-1,controller.cScreen.CW,yCaption-1);
                } 
                                
                int fh_and_half = cfFont.getHeight();
                fh_and_half += (fh_and_half>>1);
                
                if (useCover) {
                
                    int cv = getCoverSize();
                    if (cv > 0) {
                    
                        Image im = (cover != null ? cover : controller.cScreen.loadCachedCover(namedCover));
                        if (im != null) {
                        
                            int ci = im.getHeight();
                            int shift = 0;
                            if (ci < cv) {
                                shift = ((cv - ci)>>1);
                            }

                            controller.cScreen.gr.drawImage(im, 
                                          controller.cScreen.CW>>1, 
                                          getStatusY() + fh_and_half + shift, 
                                          Graphics.TOP|Graphics.HCENTER);
                        }
                    }
                }
                redrawIcons();
                        
                if (useVolume) {
                    drawVolume();
                }
                                
                controller.cScreen.gr.setColor(fg);
                controller.cScreen.gr.drawString(statusItem, controller.cScreen.CW>>1, getStatusY(), Graphics.TOP|Graphics.HCENTER);
		//System.out.println("ControlForm.drawScreen() "+statusItem+" "+getStatusY());
                                
                if (useTicker) {
                    // will run ticker if string is long enough
                    //controller.showAlert("ControlForm.drawScreen->setTParams");
                                        
                    if (nInfoItems > 0) {
                        int tickerY = startTitleY;
                        if (useCover && (cover != null || namedCover.length() > 0)) {
                            tickerY = yCorner - fh_and_half;
                        
                            if (useVolume) {
                                tickerY -= 8;
                            }
                        }
                        controller.cScreen.setTParams(infoItem[0], 0, tickerY, controller.cScreen.CW);
                    }
                } else  {
                    int maxLines = (endTitleY - startTitleY)/cfFont.getHeight();
                    if (maxLines > nInfoItems) {
                        maxLines = nInfoItems;
                    }
                    if (maxLines < 0) {
                        maxLines = 0;
                    }
                        
                    int delta = 0;
                    if (!useTicker) {
                        delta  = (endTitleY - startTitleY - maxLines*cfFont.getHeight())/(maxLines + 1);
                    }
                        
                    controller.cScreen.gr.setColor(fg);

                    for (int n=0; n<nInfoItems; ++n) {
                        yInfo[n] = startTitleY + delta*(n+1) + cfFont.getHeight()*n;
                        
                        if (endTitleY < yInfo[n] + cfFont.getHeight()) {    // Do not draw outside allowed area
                            yInfo[n] = -1;
                            break;
                        } 
             
                        //System.out.println("drawInfoData " + yInfo[n] + " " + infoItem[n]);
                        controller.cScreen.gr.drawString(controller.cScreen.removeSpecials(infoItem[n]), 
                                                                   controller.cScreen.CW>>1, yInfo[n], Graphics.TOP|Graphics.HCENTER);
                    }
                                }
                controller.cScreen.flushGraphics();
            } // synchronized
                        
        } catch (Exception e) {
            //controller.showAlert("Exception in ControlForm.drawScreen() "+e.getClass().getName()+" "+e.getMessage());
            //System.out.println("Exception in ControlForm.drawScreen() "+e.getClass().getName()+" "+e.getMessage());
        }
	//System.out.println("ControlForm.drawScreen() DONE");
    }

    public void drawCursor(int x, int y) {
        if (controller.alphaBlending > 2) {
            controller.cScreen.gr.drawImage(clickIcon, x, y, Graphics.LEFT|Graphics.TOP);
        } else {
            controller.cScreen.gr.setColor(fg);
            controller.cScreen.gr.drawLine(x,         y,         x + icSize, y);
            controller.cScreen.gr.drawLine(x,         y,         x,         y + icSize);
            controller.cScreen.gr.drawLine(x + icSize, y,         x + icSize, y + icSize);
            controller.cScreen.gr.drawLine(x,         y + icSize, x + icSize, y + icSize);
        }

    }
    
    public void drawVolume() {
        //System.out.println("drawVolume ");

        int y1 = controller.cScreen.CH - 8;
        int xs = 10;

        if (skin == SK_7X1) {
            y1 -= (icSize + 2);
                xs = xCorner+xOffset[0];
        }
        int xl = controller.cScreen.CW-2*xs;
        
        controller.cScreen.gr.setColor(fg);
        controller.cScreen.gr.drawRect(xs,y1,xl,6);
        
        controller.cScreen.gr.setColor(bg);
        controller.cScreen.gr.fillRect(xs+2,y1+2,xl-4,3);
        
        controller.cScreen.gr.setColor(fg);
        controller.cScreen.gr.fillRect(xs+2,y1+2,((xl-4)*volume)/100,3);
    }

    private int key2num(int keycode) {
            
        switch (keycode) {
            case Canvas.KEY_NUM1: return 0;
            case Canvas.KEY_NUM2: return 1;
            case Canvas.KEY_NUM3: return 2;
            case Canvas.KEY_NUM4: return 3;
            case Canvas.KEY_NUM5: return 4;
            case Canvas.KEY_NUM6: return 5;
            case Canvas.KEY_NUM7: return 6;
            case Canvas.KEY_NUM8: return 7;
            case Canvas.KEY_NUM9: return 8;
            case Canvas.KEY_STAR: return 9;
            case Canvas.KEY_NUM0: return 10;
            case Canvas.KEY_POUND: return 11;
            default: return -1;
        }
    }

    private int num2key(int num) {
            
        switch (num) {
            case 0 : return Canvas.KEY_NUM1;
            case 1 : return Canvas.KEY_NUM2;
            case 2 : return Canvas.KEY_NUM3;
            case 3 : return Canvas.KEY_NUM4;
            case 4 : return Canvas.KEY_NUM5;
            case 5 : return Canvas.KEY_NUM6;
            case 6 : return Canvas.KEY_NUM7;
            case 7 : return Canvas.KEY_NUM8;
            case 8 : return Canvas.KEY_NUM9;
            case 9 : return Canvas.KEY_STAR;
            case 10: return Canvas.KEY_NUM0;
            case 11: return Canvas.KEY_POUND;
            default: return -1;
        }
    }
                       
    public boolean isRealJoystick(int keyCode) {
        //System.out.println("isRealJoystick "+keyCode);
        // Does not works on Nokia-E61 (JoystickPush)
        //boolean zz = (keyCode == controller.cScreen.getKeyCode(controller.cScreen.getGameAction(keyCode)));
        
        if (joystickFix &&
            (joystickUp    == keyCode ||
             joystickDown  == keyCode ||
             joystickLeft  == keyCode ||
             joystickRight == keyCode ||
             joystickPush  == keyCode)) {
            return true;
        } else {
            int gAction = -99;
            if (keyCode == CanvasScreen.KEY_SEND) {
                gAction = Canvas.FIRE;
            } else {
                try {
                    gAction = controller.cScreen.getGameAction(keyCode);
                } catch (Exception e) {
                        return false;
                }
            }
                        
            // Do not allow to mix alpha's with joystick keys
            if (//keyCode < 0 && Motorola V500 uses values 1,2,5,6,20    
                keyCode != Canvas.KEY_NUM2 &&  // Is it enough ? 
                keyCode != Canvas.KEY_NUM4 &&
                keyCode != Canvas.KEY_NUM5 &&
                keyCode != Canvas.KEY_NUM6 &&
                keyCode != Canvas.KEY_NUM8 &&
                keyCode != 97  &&  // Nokia qwerty devices (E61, E71 etc.)
                keyCode != 100 &&
                keyCode != 103 &&
                keyCode != 106 &&
                keyCode != 108 &&
                keyCode != 112 &&
                keyCode != 114 &&    
                keyCode != 118 &&  // Nokia qwerty devices (E61, E71 etc.)
                (gAction == Canvas.UP    || 
                  gAction == Canvas.DOWN  || 
                  gAction == Canvas.LEFT  || 
                  gAction == Canvas.RIGHT || 
                  gAction == Canvas.FIRE)) {
                
                return true;
            }
        }
            
        return false;
    }
                        
    private boolean handleJoystick(int keyCode) {
               
        // This is not 0-9,*,#. Is it joystick key ?
        
        int gAction = -99;
        if (keyCode == CanvasScreen.KEY_SEND) {
            gAction = Canvas.FIRE;
        } else {
            try {
                gAction = controller.cScreen.getGameAction(keyCode);
            } catch (Exception e) {
                //controller.showAlert("Exception in handleJoystick() " + e.getMessage());
                return false;
            }
        }  
        
        return handleJoystickAction(gAction);
    }
                           
    private boolean handleJoystickAction(int gAction) {
        //System.out.println("handleJoystickAction "+gAction);
        int ic = curIcon;
                
        if (skin==SK_7X1) {
            if (gAction == Canvas.LEFT) {
                ic = curIcon - 1;
                if (ic<0) {
                    ic = 11;
                    while (xOffset[ic] < 0) {
                        ic--;
                    }
                                }
            } else if (gAction == Canvas.RIGHT) {
                ic = (curIcon + 1)%12;
                if (xOffset[ic] < 0) {
                    ic = 0;
                } 
            } else if (gAction == Canvas.FIRE) {
                controller.protocol.queueCommand(num2key(ic), true);
            } else if (gAction == Canvas.UP && skin==SK_7X1 && (!upEvent.equals(""))) {
                controller.protocol.queueCommand(upEvent);
            } else if (gAction == Canvas.DOWN && skin==SK_7X1 && (!downEvent.equals(""))) {
                controller.protocol.queueCommand(downEvent);
            } else {
                return false;
            }
        } else {    // DEFAULT
            
            switch (gAction) {
                case Canvas.UP:
                    if (curIcon>2) ic = curIcon - 3;
                    break;
                case Canvas.DOWN:
                    if (curIcon<9) ic = curIcon + 3;
                    break;
                case Canvas.LEFT:
                    if (curIcon%3>0) ic = curIcon - 1;
                    break;
                case Canvas.RIGHT:
                    if (curIcon%3<2) ic = curIcon + 1;
                    break;
                case Canvas.FIRE:
                    controller.protocol.queueCommand(num2key(ic), true);
                    break;
                default:
                    return false;
             }
        } 
        synchronized (controller.cScreen.drawMutex) {
             curIcon = ic;
             drawScreen();
        }
        return true;
    }
        
    public void keyPressed(int keyCode) {
        //System.out.println("ControlForm.keyPressed "+keyCode);
        //controller.showAlertAsTitle("keyPressed " + keyCode);
        //controller.showAlert("keyPressed " + keyCode);
        if (useKeypad) {
            //System.out.println("keyPressed use keypad");
            //controller.showAlert("keyPressed use keypad");
            int ic = key2num(keyCode);
            if (ic>=0 && ic<NUM_ICONS) {
                
                keyPressed[ic] = true;
                drawScreen();
                                
                controller.protocol.queueCommand(keyCode, true);
                return;
            }
        }

        if (useJoystick) {
            //System.out.println("keyPressed use joystick");
            //controller.showAlert("keyPressed use joystick");
            
            // on qwerty keypads we have to separate joystick buttons and alpha-numeric
            if (isRealJoystick(keyCode) && handleJoystick(keyCode)) {
                // command should be queued inside handleJoystick()
                drawScreen();
                return;
            } 
            if (!useKeypad) return;  // skip anything else because we in 
                                      // joystrick_only mode
        }
        // need to have this line at least for support of qwerty devices
        controller.protocol.queueCommand(keyCode, true);
    }
    
    public void keyReleased(int keyCode) {
        //System.out.println("ControlForm.keyReleased" + keyCode);
        //controller.showAlert("keyReleased " + keyCode);

        clearPressed();

        if (useKeypad) {
            int ic = key2num(keyCode);
            if (ic>=0 && ic<NUM_ICONS) {
                drawScreen();
                controller.protocol.queueCommand(keyCode, false);
                return;
            }
        }

        // on qwerty keypads we have to separate joystick buttons and alpha-numeric
        if (useJoystick) {
            if (isRealJoystick(keyCode)) {
                        
                int gAction = -99;
                try {
                    gAction = controller.cScreen.getGameAction(keyCode); // Do not proceed UP, DOWN, LEFT, RIGHT
                } catch (Exception e) { }
                        
                if (gAction == Canvas.FIRE || keyCode == CanvasScreen.KEY_SEND) {
                    drawScreen();
                    controller.protocol.queueCommand(num2key(curIcon), false);
                } else if (gAction == Canvas.LEFT  || 
                           gAction == Canvas.UP    || 
                           gAction == Canvas.RIGHT || 
                           gAction == Canvas.DOWN) {
                    // do nothing
                }
                return;
            }
            if (!useKeypad) return;  // skip anything else because we in 
                                      // joystrick_only mode
        }
        // need to have this line at least for support of qwerty devices
        controller.protocol.queueCommand(keyCode, false);
    }
        
    public void pointerPressed(int x, int y) { 
        isPressed = true;
    }
        
    public void pointerReleased(int x, int y) {
        //System.out.println("ControlForm.pointerReleased "+x+" "+y);
        int ic = getButton(x,y);
        if (ic >= 0) {
            int kcode = num2key(ic);

            if (!isPressed) {
                //change actual position only if icon has been dragged
                curIcon = ic;
            }
            drawScreen();

            if (kcode >= 0) {
                    
                // handle pointer action in any case
                boolean keyp = useKeypad;
                useKeypad = true;
                
                keyPressed (kcode);
                keyReleased(kcode);
                
                useKeypad = keyp;
            }
        }
        isPressed = false;
    }
        
     public void pointerDragged (int x, int y) { 
        
        int ic = getButton(x,y);
            
        if (ic >= 0 && realIcon != ic) {
            //do not actually change current icon, just draw
            int t = curIcon;
            realIcon = curIcon = ic;
            drawScreen();
            curIcon = t;
        }
    }
    
    public int getButton(int x, int y) {
                
        int iStep = icSize + (split<<1);

        for (int ic=0; ic<NUM_ICONS; ++ic) {
            if (xOffset[ic]<0) {    // Skip non-drawable icons at the end of icons array
                return -1;
            }
                        
            int xMin = xCorner + xOffset[ic];
            int yMin = yCorner + yOffset[ic];
            
            if (xMin < x && x < xMin + iStep &&
                yMin < y && y < yMin + iStep) {
 
                return ic;
            }
        }
        return -1;
    }
       
    public void commandAction(Command cmd, Displayable d) {
        //System.out.println("ControlForm.commandAction "+cmd.getLabel());
        if (cmd == exitCommand) {
            controller.exit();
        } else if (cmd == disconnectCommand) {
            controller.protocol.closeConnection();
        } else if (cmd == controller.cScreen.nokiaPush) {    // emulate joystick push by menu item on Nokia Series 40
            controller.protocol.queueCommand(num2key(curIcon), true);
            controller.protocol.queueCommand(num2key(curIcon), false);
        } else {
            //System.out.println("ControlForm.queueCommand getLabel");
            controller.protocol.queueCommand(cmd.getLabel());
        }
    }
        
    //
    //
    // Icon layouts support. Calculate coordinates of GUI elements depending on layout type
    //
    //
    private int setLastIcon() {
        int iconMax = controller.cScreen.CW/icSize;
        if (iconMax > NUM_ICONS) {
            iconMax = NUM_ICONS;
        }
        if (skin == SK_7X1 && iconMax > NUM_ICONS_7X1) {
                iconMax = NUM_ICONS_7X1;
        }
        for (;iconMax>=0; iconMax--) {
	    //System.out.println("ControlForm.setLastIcon "+iconMax+" "+iconNames[iconMax-1]);
            if (!iconNames[iconMax-1].equals("none")) {
                break;
            }
        }
	//System.out.println("ControlForm.setLastIcon "+iconMax);
        return iconMax;
    }
        
    public void setIconXY() {
        //System.out.println("ControlForm.setIconXY");
        try {    
            int w = controller.cScreen.CW;
            int iconMax, fGap;
            
            int iSize = icSize + (split<<1);
             
            switch (skin) {
              case SK_7X1:
                iconMax = setLastIcon();
                                
                fGap = (w - iconMax*iSize)>>1;
                for (int n=0; n<iconMax; n++) {
                    xOffset[n] = fGap + n*iSize;
                    yOffset[n] = 0;
                }
                for (int n=iconMax; n<NUM_ICONS; n++) {
                    xOffset[n] = -1;
                }
                break;
              default:
                int r = 0;
                int c = 0;
                for (int n=0; n<NUM_ICONS; n++) {
                    xOffset[n] = r*iSize;
                    yOffset[n] = c*iSize;
                    if (r>=2) {
                        r=0;
                        c++;
                    } else {
                        r++;
                    }
                }
            }
            //for (int n=0; n<NUM_ICONS; n++) {
            //     System.out.println("setIconXY " + xOffset[n] + " " + yOffset[n]);
            //}
        } catch (Exception e) {
            //controller.showAlert("Exception in setIconXY() "+e.getMessage());
        }
    }
        
    private int getStatusY() {
        return (controller.cScreen.isFullscreen ? yCaption + 1 : 1); 
    }

    public void setPositions() {        // Recalculate position for button images, title and status
        //System.out.println("ControlForm.setPositions");
        
        int FH = cfFont.getHeight();
        int H  = controller.cScreen.CH;
        if (controller.siemensFix && ! controller.cScreen.isFullscreen) {
            H -= 18;    // fix Siemens-S65 screen size 
        }

        int iSize = icSize + (split<<1);
                
        try {
            int yShift  = 0;
            int yShift2 = 0;
            if (controller.cScreen.isFullscreen) {
                yShift = yCaption;
            }
            if (useVolume && (skin == SK_DEFAULT || skin == SK_7X1)) {
                yShift2 = 8;
            }
            int y1 = ((FH*3)>>1) + yShift;
                
            switch (skin) {
              case SK_7X1:
                xCorner = 0;
                yCorner = H - iSize - 2;
		//System.out.println("ControlForm.setPositions "+yCorner+" "+H);
                         
                if (useTicker) {
                    startTitleY = (H>>1) + (FH>>2) - icSize + (yShift>>1) - (yShift2>>1);
                    endTitleY   = startTitleY+FH;
                } else {
                    startTitleY = y1;
                    endTitleY   = H - (iSize<<1) - yShift2;
                }
                break;
              default:
                xCorner = ((controller.cScreen.CW  - iSize*3)>>1);
                yCorner = y1;
                                
                if (useTicker) {
                    startTitleY = (H>>1) + (FH>>2) + (iSize<<1) + (yShift>>1) - (yShift2>>1);
                    endTitleY   = startTitleY+FH;
                } else {
                    startTitleY = y1 + (iSize<<2);
                    endTitleY   = H - yShift2;
                }
            }
        } catch (Exception e) {
            //controller.showAlert("Exception in setPositions() "+e.getMessage());
        }
	//System.out.println("ControlForm.setPositions "+yCaption);
    }
    
    public void fullscreenBkgr() {
        controller.cScreen.flushFullScreen(bg);
    }
    
    public void setData(Vector dataIn, int stage) {
       //System.out.println("ControlForm.setData "+stage);
       //controller.showAlert("ControlForm.setData");
        
        if (stage == CanvasConsumer.FULL) {
            useCover   = false;
            cover      = null;
	    namedCover = "";
        } else {
        
            try {
                useCover   = false;
                cover      = null;
                namedCover = "";
                
                Vector data = new Vector();
                useCover = controller.cScreen.receiveCover(data);
		
                //System.out.println("ControlForm.setData use cover "+useCover+" "+((String) data.elementAt(0)));
                if (useCover) {
                    String op = (String) data.elementAt(0);
                    if (op.equals("noname")) {
                       
                       cover    = (Image) data.elementAt(1);
 
                    } else if (op.equals("by_name")) {
                       
                       namedCover = (String) data.elementAt(1);

                    //} else { // if (op.equals("clear") {
                    }
                }

            } catch(Exception e1) {
                // This could be normal if we want to clean up cover
                //controller.showAlert("Exception/setData " + e1.getClass().getName() + ": " + e1.getMessage());
                useCover   = false;
		namedCover = "";
                cover      = null;
            } catch (Error me) {
                //controller.showAlert("Error/setData "+me.getClass().getName() + ": " + me.getMessage());
                useCover   = false;
		namedCover = "";
                cover      = null;
            }
        }
        controller.showScr(Controller.CONTROL_FORM);
    }
    
    public int getCoverSize() {
        int ret = -1;
	if (skin == SK_7X1 && useTicker) {
            int yTop	= getStatusY() + (cfFont.getHeight()<<2);    // title + status size
            int yBottom = yCorner;
            if (useVolume) {
            	yBottom -= 8;
            }
            int sz = yBottom - yTop;
	    ret = controller.cScreen.CW > sz ? sz : controller.cScreen.CW;
        }
        return ret;
    }
               
    public void hideScreen() {
        controller.cScreen.stopTicker();
        clearPressed();
        isPressed = false;
    }
    
    public void clearPressed() {
        for (int ic=0; ic<NUM_ICONS; ++ic) {
            keyPressed[ic] = false;
        }
    }
    
    public void showScreen() {
        //System.out.println("ControlForm.showScreen");
        //controller.showAlert("ControlForm.showScreen "+controller.cScreen.CH);
                
        clearPressed();
        isPressed = false;
        
        if (controller.cScreen.gr.getFont().getSize() != cfFont.getSize()) {
                
            synchronized (controller.cScreen.drawMutex) {
                controller.cScreen.gr.setFont(cfFont);
            }
            setInfoData();
        }        
        if (useTicker) {
            controller.cScreen.setTVisuals(cfFont, 15, fg, bg, Graphics.HCENTER, true);
        }
        
        if (controller.nokiaFixRepaint2) {
            try { 
                Thread.sleep(100); 
                controller.display.callSerially(new Runnable() { 
                    public void run() { 
                        controller.cScreen.cf.drawScreen(); 
                    }});
            } catch(Exception z) { } // just to pause execution 
        } else {
            drawScreen();
        }
    }
}
