/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.joarchitectus.visor3d.jogl;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

//import static com.jogamp.opengl.GL.*;  // GL constants
import static com.jogamp.opengl.GL2.*; // GL2 constants
import com.jogamp.opengl.GLCapabilities;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import net.joarchitectus.visor3d.icp.IterativeClosestPoint;
import net.joarchitectus.visor3d.icp.ListenerAvance;
import org.slf4j.LoggerFactory;
import org.smurn.jply.PlyReaderFile;

/**
 * Based on :http://www3.ntu.edu.sg/home/ehchua/programming/opengl/JOGL2.0.html
 * with minor modifications JOGL 2.0 Program Template (GLCanvas) This is a
 * "Component" which can be added into a top-level "Container". It also handles
 * the OpenGL events to render graphics.
 *
 * @author Curtis White
 * @version 1.0
 * @since 1.0
 */
@SuppressWarnings("serial")
public class Canvas extends GLCanvas implements GLEventListener {

    private static org.slf4j.Logger log = LoggerFactory.getLogger(Canvas.class);

    /**
     * The main GL Utility
     */
    private GLU glu;
    private String archivo;
    private boolean resultadoICPVisible=true;

    /**
     * Constructor to setup the GUI for this Component
     *
     * @param capabilities
     */
    public Canvas(GLCapabilities capabilities) {
        super(capabilities);
        this.addGLEventListener(this);
        valuesB = new ArrayList<>();
        resultadoICPClasico = new ArrayList<>();
    }

    /**
     * Constructor to setup the GUI for this Component
     *
     * @param capabilities
     * @param archivo
     */
    public Canvas(GLCapabilities capabilities, String archivo) {
        super(capabilities);
        this.addGLEventListener(this);
        this.archivo = archivo;
        valuesB = new ArrayList<>();
        resultadoICPClasico = new ArrayList<>();
    }

    /**
     * Constructor to setup the GUI for this Component
     */
    public Canvas() {
        this.addGLEventListener(this);
        valuesB = new ArrayList<>();
        resultadoICPClasico = new ArrayList<>();
    }

    /**
     * Reader to fetch values from the file
     */
    private Reader values;

    /**
     * Reader to fetch values from the file
     */
    private List<Reader> valuesB;

    /**
     * Resultado de ICP clasico
     */
    private List<Point3d> resultadoICPClasico;

    /**
     * Rotation value on the x axis
     */
    private float rt_x = 0;

    /**
     * Translation value on the x axis
     */
    private float tr_x = 0;

    /**
     * Rotation value on the y axis
     */
    private float rt_y = 0;

    /**
     * Translation value on the y axis
     */
    private float tr_y = -0.1f;

    /**
     * Rotation value on the z axis
     */
    private float rt_z = 0;

    /**
     * Translation value on the z axis
     */
    private float tr_z = -0.5f;

    /**
     * Stores whether we are rendering in wireframe or shading
     */
    private int render_mode = GL_FILL;

    /**
     * Stores the material being rendered
     */
    private float material = 0;

    /**
     * Stores the draw mode being used
     */
    private int draw_mode = 0;

    /**
     * Flag whether the lights are on or off
     */
    private boolean lights_on = false;

    /**
     * OpenGL
     */
    private GL2 gl;

    // ------ Implement methods declared in GLEventListener ------
    /**
     * Overridden method from the one declared in GLEventListener.
     * <p>
     * init() runs on the creation of the canvas, this is where we can declare
     * mouse and key listeners and other control methods.
     *
     * @author Curtis White
     * @since 1.0
     * @param drawable A GLAutoDrawable that calls the rendering functions
     */
    @Override
    public void init(GLAutoDrawable drawable) {

        /**
         * A mouse motion listener for the rendering client.
         *
         * @author Curtis White
         * @version 1.0
         * @since 1.0
         */
        addMouseMotionListener(new MouseMotionListener() {

            /**
             * Stores the mouse x value from the last frame.
             */
            private int old_x = 0;

            /**
             * Stores the mouse x value from the last frame.
             */
            private int old_y = 0;

            /**
             * Overridden method from the one declared in MouseMotionListener.
             * <p>
             * mouseDragged() runs when the mouse moves while a mouse button is
             * held
             *
             * @author Curtis White
             * @since 1.0
             * @param e A MouseEvent that details when, where and how the mouse
             * was moved/clicked
             */
            @Override
            public void mouseDragged(MouseEvent e) {

                // Check whether left or right mouse is held
                if (SwingUtilities.isLeftMouseButton(e)) {
                    // Get x and y
                    int x = e.getX();
                    int y = e.getY();

                    // Rotate for LMB
                    rt_y -= (old_x - x) / 2;
                    rt_x -= (old_y - y) / 2;

                    // Overwrite last frame data
                    old_x = x;
                    old_y = y;
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    // Get x and y
                    int x = e.getX();
                    int y = e.getY();

                    // Translate for RMB
                    tr_x -= ((float) old_x - (float) x) / 1000;
                    tr_y += ((float) old_y - (float) y) / 1000;

                    // Overwrite last frame data
                    old_x = x;
                    old_y = y;
                }
            }

            /**
             * Overridden method from the one declared in MouseMotionListener.
             * <p>
             * mouseMoved() runs when the mouse moves when a mouse button is not
             * held
             *
             * @author Curtis White
             * @since 1.0
             * @param e A MouseEvent that details when, where and how the mouse
             * was moved/clicked
             */
            @Override
            public void mouseMoved(MouseEvent e) {
                // Simply find x and y and then overwrite last frame data
                int x = e.getX();
                int y = e.getY();

                old_x = x;
                old_y = y;
            }
        });

        /**
         * A mouse wheel listener for the rendering client.
         *
         * @author Curtis White
         * @version 1.0
         * @since 1.0
         */
        addMouseWheelListener(new MouseWheelListener() {

            /**
             * Overridden method from the one declared in MouseWheelListener.
             * <p>
             * mouseMoved() runs when the mouse moves when a mouse button is not
             * held
             *
             * @author Curtis White
             * @since 1.0
             * @param e A MouseWheelEvent that details when, where and how the
             * mouse wheel moved
             */
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                // Check direction of rotation
                if (e.getWheelRotation() < 0) {
                    // Zoom in for up
                    tr_z += 0.01f;
                } else {
                    // Zoom out for down
                    tr_z -= 0.01f;
                }
            }
        });

        /**
         * A key listener for the rendering client.
         *
         * @author Curtis White
         * @version 1.0
         * @since 1.0
         */
        this.addKeyListener(new KeyListener() {

            /**
             * Overridden method from the one declared in KeyListener. (unused)
             *
             * @author Curtis White
             * @since 1.0
             * @param e A KeyEvent that details when, where and how key was
             * pressed/released/typed.
             */
            @Override
            public void keyTyped(KeyEvent e) {

            }

            /**
             * Overridden method from the one declared in KeyListener.
             * <p>
             * keyPressed() runs on key down of any key. The key pressed can be
             * found in e. This method is used over others for faster looping
             * and response time.
             *
             * @author Curtis White
             * @since 1.0
             * @param e A KeyEvent that details when, where and how key was
             * pressed/released/typed.
             */
            @Override
            public void keyPressed(KeyEvent e) {

                /**
                 * The code for the key that was pressed
                 */
                int key = e.getKeyCode();

                // Probably could be a case statement
                // Checking which key was pressed and acting accordingly
                if (key == KeyEvent.VK_W) {
                    rt_x += 1f; // Positive x rotation
                } else if (key == KeyEvent.VK_X) {
                    rt_x -= 1f; // Negative x rotation
                } else if (key == KeyEvent.VK_D) {
                    rt_y += 1f; // Positive y rotation
                } else if (key == KeyEvent.VK_A) {
                    rt_y -= 1f; // Negative y rotation
                } else if (key == KeyEvent.VK_E) {
                    rt_z += 1f; // Positive z rotation
                } else if (key == KeyEvent.VK_Z) {
                    rt_z -= 1f; // Negative z rotation
                } else if (key == KeyEvent.VK_P) {
                    tr_z += 0.01f; // Zooming in
                } else if (key == KeyEvent.VK_O) {
                    tr_z -= 0.01f; // Zooming out
                } else if (key == KeyEvent.VK_K) {
                    // This toggles the render mode
                    if (render_mode == GL_FILL) {
                        render_mode = GL_LINE;
                    } else {
                        render_mode = GL_FILL;
                    }
                } else if (key == KeyEvent.VK_L) {
                    // Toggle the lighting (doesn't disable ambient light)
                    if (lights_on) {
                        lights_on = false;
                    } else {
                        lights_on = true;
                    }
                } else if (key == KeyEvent.VK_M) {
                    // Cycles through the materials
                    material += 1;
                } else if (key == KeyEvent.VK_J) {
                    // Toggle perspective type
                    draw_mode += 1;
                }
            }

            /**
             * Overridden method from the one declared in KeyListener. (unused)
             *
             * @author Curtis White
             * @since 1.0
             * @param e A KeyEvent that details when, where and how key was
             * pressed/released/typed.
             */
            @Override
            public void keyReleased(KeyEvent e) {

            }

        });

        // Try/Catch to find and handle IOExceptions
        try {
            if (archivo == null || archivo.isEmpty()) {
                values = new Reader(
                        (getClass().getClassLoader().getResourceAsStream("bun_zipper_res3.ply")), null);
                //values = new Reader(Paths.get("/media/DATOS/Universidad/Proyectos/bunny/reconstruction/bun_zipper.ply"));
                //values = new Reader(Paths.get("/media/DATOS/Universidad/Proyectos/horse.ply"));
                //values = new Reader(Paths.get("/media/DATOS/Universidad/Proyectos/Armadillo_scans/ArmadilloStand_0.ply"));
            } else {
                values = new Reader(Paths.get(archivo), null);
            }
        } catch (IOException ie) {
            ie.printStackTrace();
        }

        // Get OpenGL context and GL Utils
        gl = drawable.getGL().getGL2();
        glu = new GLU();

        // Clear background colour and depth
        gl.glClearColor(0.1f, 0.1f, 0.2f, 0.0f);
        gl.glClearDepth(1.0f);

        // Depth testing, perspective and smooth shading
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        gl.glShadeModel(GL_SMOOTH);

    }

    /**
     * Overridden method from the one declared in GLEventListener.
     * <p>
     * reshape() runs on the resizing of the canvas, this is where we adjust
     * variables to be compatible with the new window size.
     *
     * @author Curtis White
     * @since 1.0
     * @param drawable A GLAutoDrawable that calls the rendering functions
     */
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        // Get OpenGL context
        gl = drawable.getGL().getGL2();

        // Prevent zero height to stop divide by zero errors
        if (height == 0) {
            height = 1;
        }
        float aspect = (float) width / height;

        // Set the view port (display area) to cover the entire window
        gl.glViewport(0, 0, width, height);

        // Setup perspective projection, with aspect ratio matches viewport
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0, aspect, 0.1, 100.0);

        // Enable the model-view transform
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    /**
     * Overridden method from the one declared in GLEventListener.
     * <p>
     * display() is called by the animator to render the image.
     *
     * @author Curtis White
     * @since 1.0
     * @param drawable A GLAutoDrawable that calls the rendering functions
     */
    @Override
    public void display(GLAutoDrawable drawable) {
        // Get the OpenGL context
        gl = drawable.getGL().getGL2();

        // Clear some GL buffers
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        // Translate by saved values and set render mode
        gl.glTranslatef(tr_x, tr_y, tr_z);
        gl.glPolygonMode(GL_FRONT_AND_BACK, render_mode);

        // Array of light position floats
        // Convert to byte buffer and then float buffer
        float[] light_pos = {1.0f, 1.0f, 1.0f, 1.0f};
        ByteBuffer lbb = ByteBuffer.allocateDirect(16);
        lbb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = lbb.asFloatBuffer();
        fb.put(light_pos);
        fb.position(0);

        // Set light on and send position if lights are meant to be on
        if (lights_on) {
            gl.glEnable(GL_LIGHT0);
            gl.glLightfv(GL_LIGHT0, GL_POSITION, fb);
            gl.glColorMaterial(GL_FRONT, GL_DIFFUSE);
            gl.glEnable(GL_COLOR_MATERIAL);
        } else {
            gl.glDisable(GL_LIGHT0);
        }

        // Create some more arrays for float buffers
        float[] ambient;
        float[] diffuse;
        float[] specular;

        // Use values based on selected material
        if (material % 3 == 0) {
            ambient = new float[]{0.96f, 0.96f, 0.96f, 1.0f};
            diffuse = new float[]{0.96f, 0.96f, 0.96f, 1.0f};
            specular = new float[]{0.96f, 0.96f, 0.96f, 1.0f};
        } else if (material % 3 == 1) {
            ambient = new float[]{1.0f, 0.843f, 0, 1.0f};
            diffuse = new float[]{1.0f, 0.843f, 0, 1.0f};
            specular = new float[]{1.0f, 0.843f, 0, 1.0f};
        } else {
            ambient = new float[]{0.804f, 0.5216f, 0.247f, 1.0f};
            diffuse = new float[]{0.804f, 0.5216f, 0.247f, 1.0f};
            specular = new float[]{0.804f, 0.5216f, 0.247f, 1.0f};
        }

        // Ambient settings for GL_LIGHT0
        fb.put(ambient);
        fb.position(0);
//        gl.glLightfv(GL_LIGHT0, GL_AMBIENT, fb);

        // Diffuse settings for GL_LIGHT0
        fb.put(diffuse);
        fb.position(0);
//        gl.glLightfv(GL_LIGHT0, GL_DIFFUSE, fb);

        // Specular settings for GL_LIGHT0
        fb.put(specular);
        fb.position(0);
//        gl.glLightfv(GL_LIGHT0, GL_SPECULAR, fb);

        // Settings for the ambient lighting
        fb.put(new float[]{0.3f, 0.3f, 0.3f, 1.0f});
        fb.position(0);
//        gl.glEnable(GL_LIGHTING);
//        gl.glLightModelfv(GL_LIGHT_MODEL_AMBIENT, fb);

        // Rotate based on saved values
        gl.glRotatef(rt_x, 1, 0, 0);
        gl.glRotatef(rt_y, 0, 1, 0);
        gl.glRotatef(rt_z, 0, 0, 1);

        //gl.glColor3f( 1f,0f,0f ); //applying red  
        // Begin triangle rendering
        gl.glBegin(GL_POINTS);

        if (values != null && values.isVisible()) {
            // Loop over faces, rendering each vertex for each face
            for (int i = 0; i < values.face_list.length; i++) {
                // Set the normal for a vertex and then make the vertex
                gl.glColor3f(0.5f, 0.0f, 1.0f); //applying purpura  
//            gl.glNormal3f(values.face_list[i].vertex_list[0].normal.x,
//                    values.face_list[i].vertex_list[0].normal.y,
//                    values.face_list[i].vertex_list[0].normal.z);
                gl.glVertex3f(values.face_list[i].vertex_list[0].x,
                        values.face_list[i].vertex_list[0].y,
                        values.face_list[i].vertex_list[0].z);

                // And again
                gl.glColor3f(0.5f, 0.0f, 1.0f); //applying red  
//            gl.glNormal3f(values.face_list[i].vertex_list[1].normal.x,
//                    values.face_list[i].vertex_list[1].normal.y,
//                    values.face_list[i].vertex_list[1].normal.z);
                gl.glVertex3f(values.face_list[i].vertex_list[1].x,
                        values.face_list[i].vertex_list[1].y,
                        values.face_list[i].vertex_list[1].z);

                // Third time's the charm
                gl.glColor3f(0.5f, 0.0f, 1.0f); //applying red  
//            gl.glNormal3f(values.face_list[i].vertex_list[2].normal.x,
//                    values.face_list[i].vertex_list[2].normal.y,
//                    values.face_list[i].vertex_list[2].normal.z);
                gl.glVertex3f(values.face_list[i].vertex_list[2].x,
                        values.face_list[i].vertex_list[2].y,
                        values.face_list[i].vertex_list[2].z);
            }

            if (values.face_list.length == 0) {
                for (int i = 0; i < values.vertex_list.length; i++) {
                    gl.glColor3f(1f, 0f, 0f); //applying red  
                    gl.glVertex3f(values.vertex_list[i].x,
                            values.vertex_list[i].y,
                            values.vertex_list[i].z);
                }
            }
        }

        float[] d1f = {0.2f, 0.5f, 0.8f, 1.0f};
        FloatBuffer d1 = FloatBuffer.wrap(d1f);

        if (valuesB != null && !valuesB.isEmpty()) {
            for (Reader figura : valuesB) {
                if (figura.isVisible()) {
                    FloatBuffer d3U = figura.getColor() == null ? d1 : figura.getColor();
                    for (int i = 0; i < figura.vertex_list.length; i++) {
                        gl.glColor3fv(d3U); //applying red  
//                    gl.glMaterialfv(GL_FRONT, GL_DIFFUSE, d2);
                        gl.glVertex3f(figura.vertex_list[i].x,
                                figura.vertex_list[i].y,
                                figura.vertex_list[i].z);
                    }
                }
            }
        }

        try{
            if (resultadoICPClasico != null && !resultadoICPClasico.isEmpty() && resultadoICPVisible) {

                for (Point3d point3d : resultadoICPClasico) {
                    gl.glColor3d(1.0f, 1.1f, 0.0f); //applying amarillo?  
    //                gl.glMaterialfv(GL_FRONT, GL_DIFFUSE, d1);
                    gl.glVertex3d(point3d.getX(), point3d.getY(), point3d.getZ());
                }
            }
        }catch(java.util.ConcurrentModificationException ex){
            log.warn("Solo si use archivos externos? "+ex.getMessage()); 
        }catch(Exception ex){
            log.warn("Otro? "+ex.getMessage()); 
        }

        // End rendering
        gl.glEnd();
        gl.glLoadIdentity();
    }

    /**
     * Overridden method from the one declared in GLEventListener. (unused)
     *
     * @author Curtis White
     * @since 1.0
     * @param drawable A GLAutoDrawable that calls the rendering functions
     */
    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    /**
     *
     * @param archivo
     */
    public Reader cargarArchivo(String archivo) {
        try {
            float[] d1f = {0.2f, 0.5f, 0.8f, 1.0f};
            float[] d2f = {0.3f, 0.5f, 0.6f, 1.0f};
            float[] d3f = {0.4f, 0.2f, 0.2f, 1.0f};
            FloatBuffer d1 = FloatBuffer.wrap(d1f);
            FloatBuffer d2 = FloatBuffer.wrap(d2f);
            FloatBuffer d3 = FloatBuffer.wrap(d3f);

            values = new Reader(Paths.get(archivo), d2);
        } catch (IOException ie) {
            ie.printStackTrace();
        }

        gl.glLoadIdentity();
        return values;
    }

    /**
     *
     * @param archivo
     */
    public Reader agregarArchivo(String archivo) {
        Reader nuevo = null;
        try {
            Random r = new Random();
            float[] d1f = {0.2f, 0.5f, 0.8f, 1.0f};
            float[] d2f = {0.3f, 0.5f, 0.6f, 1.0f};
            float[] d3f = {0.4f, 0.2f, r.nextFloat(), 1.0f};
            FloatBuffer d1 = FloatBuffer.wrap(d1f);
            FloatBuffer d2 = FloatBuffer.wrap(d2f);
            FloatBuffer d3 = FloatBuffer.wrap(d3f);

            nuevo = new Reader(Paths.get(archivo), d3);
            valuesB.add(nuevo);
        } catch (IOException ie) {
            ie.printStackTrace();
        }

        gl.glLoadIdentity();
        return nuevo;
    }
    
    /**
     *
     * @param archivo
     */
    public Reader agregarArchivo(InputStream inputStream) {
        Reader nuevo = null;
        try {
            Random r = new Random();
            float[] d1f = {0.2f, 0.5f, 0.8f, 1.0f};
            float[] d2f = {0.3f, 0.5f, 0.6f, 1.0f};
            float[] d3f = {r.nextFloat(), 0.2f, r.nextFloat(), 1.0f};
            FloatBuffer d1 = FloatBuffer.wrap(d1f);
            FloatBuffer d2 = FloatBuffer.wrap(d2f);
            FloatBuffer d3 = FloatBuffer.wrap(d3f);

            nuevo = new Reader(inputStream, d3);
            valuesB.add(nuevo);
        } catch (IOException ie) {
            ie.printStackTrace();
        }

        gl.glLoadIdentity();
        return nuevo;
    }

    /**
     *
     */
    public void limpiar(boolean refresh) {
        values = new Reader();
        valuesB.clear();
        resultadoICPClasico.clear();

        if (refresh) {
            gl.glLoadIdentity();
        }
    }

    /**
     *
     * @param swingWorker
     */
    public void ejecutarICP(final ListenerAvance listener, boolean kd_tree, boolean kd_tree_weka) throws Exception {
        if (valuesB != null && !valuesB.isEmpty() && valuesB.size() == 2) {
            double[][] pointSet1 = new double[valuesB.get(0).vertex_list.length][3];
            double[][] pointSet2 = new double[valuesB.get(1).vertex_list.length][3];

            for (int i = 0; i < valuesB.get(0).vertex_list.length; i++) {
                pointSet1[i][0] = valuesB.get(0).vertex_list[i].x;
                pointSet1[i][1] = valuesB.get(0).vertex_list[i].y;
                pointSet1[i][2] = valuesB.get(0).vertex_list[i].z;
            }

            for (int i = 0; i < valuesB.get(1).vertex_list.length; i++) {
                pointSet2[i][0] = valuesB.get(1).vertex_list[i].x;
                pointSet2[i][1] = valuesB.get(1).vertex_list[i].y;
                pointSet2[i][2] = valuesB.get(1).vertex_list[i].z;
            }

            //Ejecutamos ICP
            IterativeClosestPoint.setDebugLevel(1);
            IterativeClosestPoint.setWith_KD_Tree_java(kd_tree);
            IterativeClosestPoint.setWith_KD_Tree_weka(kd_tree_weka);
            Matrix4d matrix4d = IterativeClosestPoint.calcTransform(pointSet1, pointSet2, listener);

            //System.out.println("A: " + matrix4d.toString());
            if (resultadoICPClasico == null) {
                resultadoICPClasico = new ArrayList<>();
            } else {
                resultadoICPClasico.clear();
            }

            for (int i = 0; i < valuesB.get(1).vertex_list.length; i++) {
                resultadoICPClasico.add(transformPoint(matrix4d, new Point3d(
                        valuesB.get(1).vertex_list[i].x,
                        valuesB.get(1).vertex_list[i].y,
                        valuesB.get(1).vertex_list[i].z
                )));
            }

            log.warn("Termina algoritmo");
            gl.glLoadIdentity();
        }
    }

    /**
     * Returns the transformed point of the original point given a transform
     * matrix
     *
     * @param transformMat
     * @param originalPoint
     * @return
     */
    private static Point3d transformPoint(Matrix4d transformMat, Point3d originalPoint) {
        Point3d tempPoint = new Point3d();
        transformMat.transform(new Point3d(originalPoint.getX(), originalPoint.getY(), originalPoint.getZ()), tempPoint);
        return tempPoint;
    }

    /**
     * Refresca pantalla
     * @param raiz
     * @param bandera 
     */
    public void refrescar(Boolean raiz, boolean bandera, boolean isForICP) {
        if (!isForICP && raiz == null) {
            values.setVisible(bandera);
        }
        
        if(isForICP){
            resultadoICPVisible=bandera;
        }

        log.info("Refresca");
        gl.glLoadIdentity();
    }
}
