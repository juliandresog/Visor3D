/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.joarchitectus.visor3d.jogl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

/**
 * Based on :http://www3.ntu.edu.sg/home/ehchua/programming/opengl/JOGL2.0.html with minor modifications
 * JOGL 2.0 Program Template (GLCanvas)
 * This is the top-level "Container", which allocates and add GLCanvas ("Component") and animator.
 *
 * @author Curtis White
 * @version 1.0
 * @since 1.0
 */
@SuppressWarnings("serial")
public class RendererMain extends JFrame {

    /**
     * Window title.
     */
    private static String TITLE = "Bunny Renderer";

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

    /**
     * Canvas for OpenGL to draw on.
     */
    GLCanvas canvas;

    /**
     * Constructor for the class.
     *
     * @author Curtis White
     * @since 1.0
     */
    public RendererMain() {
        // Create the OpenGL rendering canvas and configure it
        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

        // Create a animator that drives canvas' display() at the specified FPS.
        final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);

        // Create the top-level container frame
        this.getContentPane().add(canvas);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Use a dedicate thread to run the stop() to ensure that the
                // animator stops before program exits.
                new Thread() {
                    @Override
                    public void run() {
                        if (animator.isStarted()) animator.stop();
                        System.exit(0);
                    }
                }.start();
            }
        });

        this.setTitle(TITLE);
        this.pack();
        this.setVisible(true);

        // Start animation loop
        animator.start();
    }

    /**
     * main() since this is the main class of the project.
     *
     * @author Curtis White
     * @since 1.0
     */
    public static void main(String[] args) {
        // Run the GUI codes in the event-dispatching thread for thread safety
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Run the constructor
                new RendererMain();
            }
        });
    }
}
