/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.joarchitectus.visor3d;

import javax.swing.JFrame;
import com.jogamp.opengl.*;  // new jogl - 3.0b7
import com.jogamp.opengl.awt.GLCanvas;
import java.awt.Frame;

/**
 *
 * @author josorio
 */
public class BasicFrameJOGL implements GLEventListener {

   @Override
   public void display(GLAutoDrawable arg0) {
      // method body
   }
	
   @Override
   public void dispose(GLAutoDrawable arg0) {
      //method body
   }
	
   @Override
   public void init(GLAutoDrawable arg0) {
      // method body
   }
	
   @Override
   public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
      // method body
   }
	
   public static void main(String[] args) {
   
      //getting the capabilities object of GL2 profile
      final GLProfile profile = GLProfile.get(GLProfile.GL2);
      GLCapabilities capabilities = new GLCapabilities(profile);
        
      // The canvas
      final GLCanvas glcanvas = new GLCanvas(capabilities);
      BasicFrameJOGL b = new BasicFrameJOGL();
      glcanvas.addGLEventListener(b);        
      glcanvas.setSize(400, 400);
        
      //creating frame
      final Frame frame = new Frame (" Basic Frame");
        
      //adding canvas to frame
      frame.add(glcanvas);
      frame.setSize( 640, 480 );
      frame.setVisible(true);
   }
	
}