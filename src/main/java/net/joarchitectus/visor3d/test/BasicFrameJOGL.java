/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.joarchitectus.visor3d.test;

import javax.swing.JFrame;
import com.jogamp.opengl.*;  // new jogl - 3.0b7
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import net.joarchitectus.visor3d.jogl.Canvas;

/**
 *
 * @author josorio
 */
public class BasicFrameJOGL implements GLEventListener {

    /**
     * Default width of the rendered window.
     */
    private static final int CANVAS_WIDTH = 800;

    /**
     * Default height of the rendered window.
     */
    private static final int CANVAS_HEIGHT = 600;

    /**
     * Target FPS for the animator.
     */
    private static final int FPS = 60;

    @Override
    public void display(GLAutoDrawable drawable) {
        // method body
        final GL2 gl = drawable.getGL().getGL2();
        gl.glBegin(GL2.GL_POINTS);

        // Drawing Using Triangles 
        gl.glColor3f(1.0f, 0.0f, 0.0f);   // Red 
        gl.glVertex3f(0.5f, 0.7f, 0.0f);    // Top 

        gl.glColor3f(0.0f, 1.0f, 0.0f);     // green 
        gl.glVertex3f(-0.2f, -0.50f, 0.0f); // Bottom Left 

        gl.glColor3f(0.0f, 0.0f, 1.0f);     // blue 
        gl.glVertex3f(0.5f, -0.5f, 0.0f);   // Bottom Right 

        gl.glEnd();
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

//      // The canvas
      final GLCanvas glcanvas = new GLCanvas(capabilities);
      BasicFrameJOGL b = new BasicFrameJOGL();
      glcanvas.addGLEventListener(b);        
      glcanvas.setSize(400, 400);
      
        // Create the OpenGL rendering canvas and configure it
//        GLCanvas canvas = new Canvas();
//        canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        // Create a animator that drives canvas' display() at the specified FPS.
//        final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);
        //creating frame
        final JFrame frame = new JFrame(" Basic Frame");

        // Create the top-level container frame
        //this.getContentPane().add(canvas);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Use a dedicate thread to run the stop() to ensure that the
                // animator stops before program exits.
                new Thread() {
                    @Override
                    public void run() {
//                        if (animator.isStarted()) {
//                            animator.stop();
//                        }
                        System.exit(0);
                    }
                }.start();
            }
        });

        // Start animation loop
//        animator.start();
        javax.swing.JMenu jMenu1;
        javax.swing.JMenu jMenu2;
        javax.swing.JMenuBar jMenuBarMain;

        jMenuBarMain = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        //frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        frame.setTitle("Visor 3D PLY");
        frame.setPreferredSize(new java.awt.Dimension(800, 650));
        frame.setResizable(false);
        frame.getContentPane().setLayout(null);

        jMenu1.setText("File");
        jMenuBarMain.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBarMain.add(jMenu2);

        frame.setJMenuBar(jMenuBarMain);

        //adding canvas to frame
        //frame.add(canvas);
        //adding canvas to it
        frame.getContentPane().add(glcanvas);
        frame.setSize(840, 680);
        frame.setVisible(true);
    }

}
