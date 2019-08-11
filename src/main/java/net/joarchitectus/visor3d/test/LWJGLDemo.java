///*
//* To change this license header, choose License Headers in Project Properties.
//* To change this template file, choose Tools | Templates
//* and open the template in the editor.
// */
//package net.joarchitectus.visor3d.test;
//
////import com.sun.javafx.geom.RectBounds;
//import java.io.File;
//import org.smurn.jply.Element;
//import java.nio.FloatBuffer;
//import java.io.IOException;
//import java.nio.ByteBuffer;
//import java.nio.IntBuffer;
//import org.apache.commons.io.IOUtils;
//import org.lwjgl.BufferUtils;
//import org.lwjgl.Version;
//import org.smurn.jply.PlyReader;
////import org.lwjgl.LWJGLException;
//import org.lwjgl.opengl.ARBShaderObjects;
//import static org.lwjgl.opengl.ARBVertexBufferObject.*;
////import org.lwjgl.opengl.Display;
////import org.lwjgl.opengl.DisplayMode;
//import org.lwjgl.opengl.GL11;
////import org.lwjgl.opengl.Util;
//import org.smurn.jply.ElementReader;
//import org.smurn.jply.PlyReaderFile;
//import org.smurn.jply.util.NormalMode;
//import org.smurn.jply.util.NormalizingPlyReader;
//import org.smurn.jply.util.TesselationMode;
//import org.smurn.jply.util.TextureMode;
//import static org.lwjgl.opengl.GL11.*;
//import static org.lwjgl.opengl.ARBShaderObjects.*;
//import static org.lwjgl.opengl.ARBVertexShader.*;
//import static org.lwjgl.opengl.ARBFragmentShader.*;
////import org.lwjgl.input.Mouse;
//import static org.lwjgl.glfw.Callbacks.*;
//import static org.lwjgl.glfw.GLFW.*;
//import org.lwjgl.glfw.GLFWErrorCallback;
//import org.lwjgl.glfw.GLFWVidMode;
//import org.lwjgl.opengl.GL;
//import static org.lwjgl.opengl.GL11.*;
//import org.lwjgl.opengles.GLES;
//import org.lwjgl.system.Configuration;
//import org.lwjgl.system.MemoryStack;
//import static org.lwjgl.system.MemoryStack.*;
//import static org.lwjgl.system.MemoryUtil.*;
//
///**
// * jPLY Demo: jPLY viewer using JWJGL.
// *
// * This example is using state-of-the-art vertex-buffer-objects and GLSL shaders
// * instead of the immediate mode rendering often seen in examples. This makes
// * the code a bit more complicated but its the way this things are done in
// * real-world applications.
// *
// * The code doesn't perform proper error checking and will just fail if run on a
// * computer with an old graphics-card (or old drivers). This way the code
// * remains much more readable and simple.
// */
//public class LWJGLDemo {
//    
//    // The window handle
//    private long window;
//    
//    /**
//     * Main
//     * @param args
//     * @throws IOException 
//     */
//    public static void main(String[] args) throws IOException {
//        new LWJGLDemo().run();
//    }
//    
//    public void run() throws IOException {
//        System.out.println("Hello LWJGL " + Version.getVersion() + "!");
//
//        init();
//        //loop();
//
//        // Free the window callbacks and destroy the window
//        glfwFreeCallbacks(window);
//        glfwDestroyWindow(window);
//
//        // Terminate GLFW and free the error callback
//        glfwTerminate();
//        glfwSetErrorCallback(null).free();
//    }
//
//    /**
//     * init
//     */
//    private void init() throws IOException {
//
////          Open a openGL enabled window.
////        Display.setTitle("jPLY LWJGL Demo");
////        Display.setDisplayMode(new DisplayMode(600, 600));
////        Display.create();
//
//        // Setup an error callback. The default implementation
//        // will print the error message in System.err.
//        GLFWErrorCallback.createPrint(System.err).set();
//
//        // Initialize GLFW. Most GLFW functions will not work before doing this.
//        if (!glfwInit()) {
//            throw new IllegalStateException("Unable to initialize GLFW");
//        }
//
//        // Configure GLFW
//        glfwDefaultWindowHints(); // optional, the current window hints are already the default
//        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
//        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
//        
//        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
//        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);
//        glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_ES_API);
//        
//        // Create the window
//        window = glfwCreateWindow(600, 600, "jPLY LWJGL Demo", NULL, NULL);
//        if (window == NULL) {
//            throw new RuntimeException("Failed to create the GLFW window");
//        }
//        
//        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
//        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
//            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
//                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
//            }
//        });
//
//        // Get the thread stack and push a new frame
//        try (MemoryStack stack = stackPush()) {
//            IntBuffer pWidth = stack.mallocInt(1); // int*
//            IntBuffer pHeight = stack.mallocInt(1); // int*
//
//            // Get the window size passed to glfwCreateWindow
//            glfwGetWindowSize(window, pWidth, pHeight);
//
//            // Get the resolution of the primary monitor
//            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
//
//            // Center the window
//            glfwSetWindowPos(
//                    window,
//                    (vidmode.width() - pWidth.get(0)) / 2,
//                    (vidmode.height() - pHeight.get(0)) / 2
//            );
//        } // the stack frame is popped automatically
//
//        // Make the OpenGL context current
//        glfwMakeContextCurrent(window);
//        // This line is critical for LWJGL's inter-operation with GLFW's
//        // OpenGL context, or any context that is managed externally.
//        // LWJGL detects the context that is current in the current thread,
//        // creates the GLCapabilities instance and makes the OpenGL
//        // bindings available for use
//        GL.createCapabilities();
//        // Enable v-sync
//        glfwSwapInterval(1);
//
//        // Make the window visible
//        glfwShowWindow(window);
//        
//        // Bypasses the default create() method.
//        Configuration.OPENGLES_EXPLICIT_INIT.set(true);
//        GLES.create(GL.getFunctionProvider());
//        
//        
//        //*********************************************************************************
//
//        // Open the PLY file
//        PlyReader plyReader = new PlyReaderFile(new File("/media/DATOS/Universidad/Proyectos/horse.ply"));
////                ClassLoader.getSystemResourceAsStream("bunny.ply"));
//
//        // Normalize the data in the PLY file to ensure that we only get
//        // triangles and that the vertices have normal vectors assigned.
//        plyReader = new NormalizingPlyReader(plyReader,
//                TesselationMode.TRIANGLES,
//                NormalMode.ADD_NORMALS_CCW,
//                TextureMode.PASS_THROUGH);
//
//        // We can get the number of vertices and triangles before
//        // reading it. We will use this to allocate buffers of the right size.
//        int vertexCount = plyReader.getElementCount("vertex");
//        int triangleCount = plyReader.getElementCount("face");
//
//        // Number of bytes we need per vertex.
//        // Three position coordinates and three normal vector coordinates
//        // all stored as 32-bit float.
//        int vertexSize = 3 * 4 + 3 * 4;
//
//        // Number of bytes we need per triangle.
//        // Three indices to vertices stored as 32-bit integer.
//        int triangleSize = 3 * 4;
//
//        // A bit of opengl magic to allocate a vertex-buffer-object to
//        // store the vertices.
//        int vertexBufferId = glGenBuffersARB();
//        glBindBufferARB(GL_ARRAY_BUFFER_ARB, vertexBufferId);
//        glBufferDataARB(GL_ARRAY_BUFFER_ARB,
//                vertexCount * vertexSize, GL_STATIC_DRAW_ARB);
//        FloatBuffer vertexBuffer = glMapBufferARB(GL_ARRAY_BUFFER_ARB,
//                GL_WRITE_ONLY_ARB, null).asFloatBuffer();
//
//        // The same magic again for the index buffer storing the triangles.
//        int indexBufferId = glGenBuffersARB();
//        glBindBufferARB(GL_ELEMENT_ARRAY_BUFFER_ARB, indexBufferId);
//        glBufferDataARB(GL_ELEMENT_ARRAY_BUFFER_ARB,
//                triangleCount * triangleSize, GL_STATIC_DRAW_ARB);
//        IntBuffer indexBuffer = glMapBufferARB(GL_ELEMENT_ARRAY_BUFFER_ARB,
//                GL_WRITE_ONLY_ARB, null).asIntBuffer();
//
//        // Now we READ THE PLY DATA into the buffers.
//        // We also measure the size of the model so that we can later fit
//        // arbitrary models into the screen.
//        RectBounds bounds = fillBuffers(plyReader, vertexBuffer, indexBuffer);
//
//        // Tell openGL that we filled the buffers.
//        glUnmapBufferARB(GL_ARRAY_BUFFER_ARB);
//        glUnmapBufferARB(GL_ELEMENT_ARRAY_BUFFER_ARB);
//
//        // Create the vertex and fragment shader for nice phong shading.
//        int programId = createShaders();
//
//        // The shaders have a few parameters to configure the three lights.
//        // Each parameter has an identifier which we query here.
//        // Vector in the direction towards the light.
//        int[] lightDirection = new int[]{
//            glGetUniformLocationARB(programId, "lightDir[0]"),
//            glGetUniformLocationARB(programId, "lightDir[1]"),
//            glGetUniformLocationARB(programId, "lightDir[2]")
//        };
//        // Color of the diffuse part of the light.
//        int[] diffuseColor = new int[]{
//            glGetUniformLocationARB(programId, "diffuseColor[0]"),
//            glGetUniformLocationARB(programId, "diffuseColor[1]"),
//            glGetUniformLocationARB(programId, "diffuseColor[2]")
//        };
//        // Color of the specular (high-lights) part of the light.
//        int[] specularColor = new int[]{
//            glGetUniformLocationARB(programId, "specularColor[0]"),
//            glGetUniformLocationARB(programId, "specularColor[1]"),
//            glGetUniformLocationARB(programId, "specularColor[2]")
//        };
//        // Exponent controlling the size of the specular light.
//        int[] shininess = new int[]{
//            glGetUniformLocationARB(programId, "shininess[0]"),
//            glGetUniformLocationARB(programId, "shininess[1]"),
//            glGetUniformLocationARB(programId, "shininess[2]")
//        };
//
//        // Configure the three light sources.
//        glUseProgramObjectARB(programId);
//        glUniform3fARB(lightDirection[0], -0.9f, 0.9f, 1f);
//        glUniform4fARB(diffuseColor[0], 0.7f, 0.7f, 0.7f, 1.0f);
//        glUniform4fARB(specularColor[0], 1.0f, 1.0f, 1.0f, 1.0f);
//        glUniform1fARB(shininess[0], 50.0f);
//        glUniform3fARB(lightDirection[1], 0.6f, 0.1f, 1f);
//        glUniform4fARB(diffuseColor[1], 0.1f, 0.1f, 0.1f, 1.0f);
//        glUniform4fARB(specularColor[1], 0.0f, 0.0f, 0.0f, 1.0f);
//        glUniform1fARB(shininess[1], 50.0f);
//        glUniform3fARB(lightDirection[2], 0.0f, 0.8f, -1f);
//        glUniform4fARB(diffuseColor[2], 0.8f, 0.8f, 0.8f, 1.0f);
//        glUniform4fARB(specularColor[2], 0.0f, 0.0f, 0.0f, 1.0f);
//        glUniform1fARB(shininess[2], 50.0f);
//
//        // Some more openGL setup.
//        glFrontFace(GL_CCW);
//        glCullFace(GL_BACK);
//        glEnable(GL_CULL_FACE);
//        glEnable(GL_DEPTH_TEST);
//        //glMatrixMode(GL_PROJECTION);
//        //glLoadIdentity();
////        glOrtho(-1, 1, -1, 1, -100, 100);
////        glEnableClientState(GL_VERTEX_ARRAY);
////        glEnableClientState(GL_NORMAL_ARRAY);
////
////        // Tell openGL to use the buffers we filled with the data from
////        // the PLY file.
////        glBindBufferARB(GL_ELEMENT_ARRAY_BUFFER_ARB, indexBufferId);
////        glVertexPointer(3, GL11.GL_FLOAT, 24, 0);
////        glBindBufferARB(GL_ARRAY_BUFFER_ARB, vertexBufferId);
////        glNormalPointer(GL11.GL_FLOAT, 24, 12);
//
//        // Find out to where we need to move the object and how we need
//        // to scale it such that it fits into the window.
//        double scale = bounds.getScaleToUnityBox() * 1.5;
//        double[] center = bounds.getCenter();
//
//        // Some state variables to let the model rotate via mouse 
//        double yawStart = 0;
//        double pitchStart = 0;
//        boolean mouseMoves = false;
//        int mouseStartX = 0;
//        int mouseStartY = 0;
//
//        // See if we've run into a problem so far.
////        Util.checkGLError();
//
//        // This line is critical for LWJGL's interoperation with GLFW's
//        // OpenGL context, or any context that is managed externally.
//        // LWJGL detects the context that is current in the current thread,
//        // creates the GLCapabilities instance and makes the OpenGL
//        // bindings available for use.
//        GL.createCapabilities();
//
//        // Rendering loop until the window is clsoed.
//        while (!glfwWindowShouldClose(window)) {
//
//            // Empty the buffers
//            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//            
//            glfwSwapBuffers(window); // swap the color buffers
//
//            // Poll for window events. The key callback above will only be
//            // invoked during this call.
//            glfwPollEvents();
//
//            // Mouse handling for the model rotation
//            double yaw = yawStart;
//            double pitch = pitchStart;
////            if (mouseMoves) {
////                double deltaX = Mouse.getX() - mouseStartX;
////                double deltaY = Mouse.getY() - mouseStartY;
////                double deltaYaw = deltaX * 0.3;
////                double deltaPitch = -deltaY * 0.3;
////                yaw += deltaYaw;
////                pitch += deltaPitch;
////                if (!Mouse.isButtonDown(0)) {
////                    mouseMoves = false;
////                    yawStart += deltaYaw;
////                    pitchStart += deltaPitch;
////                }
////            } else if (!mouseMoves && Mouse.isButtonDown(0)) {
////                mouseStartX = Mouse.getX();
////                mouseStartY = Mouse.getY();
////                mouseMoves = true;
////            }
//
//            // Build the model matrix that fits the model into the
//            // screen and does the rotation.
//            glMatrixMode(GL_MODELVIEW);
//            glLoadIdentity();
//            glRotated(pitch, 1, 0, 0);
//            glRotated(yaw, 0, 1, 0);
//            glScaled(scale, scale, scale);
//            glTranslated(-center[0], -center[1], -center[2]);
//
//            // Finally we can draw the model!
//            glDrawElements(GL_TRIANGLES, triangleCount * 3, GL_UNSIGNED_INT, 0);
//
////            // See if we've run into a problem.
////            Util.checkGLError();
////
////            // Lets show what we just have drawn.
////            Display.update();
//        }
//
//        // Cleanup opengl.
////        Display.destroy();
//    }
//
//    /**
//     * Loads the data from the PLY file into the buffers.
//     */
//    private static RectBounds fillBuffers(
//            PlyReader plyReader,
//            FloatBuffer vertexBuffer,
//            IntBuffer indexBuffer) throws IOException {
//
//// Get all element readers and find the two providing 
//// the vertices and triangles.
//        ElementReader reader = plyReader.nextElementReader();
//        RectBounds bounds = null;
//        while (reader != null) {
//
//            if (reader.getElementType().getName().equals("vertex")) {
//                bounds = fillVertexBuffer(reader, vertexBuffer);
//            } else if (reader.getElementType().getName().equals("face")) {
//                fillIndexBuffer(reader, indexBuffer);
//            }
//            reader.close();
//            reader = plyReader.nextElementReader();
//        }
//        return bounds;
//    }
//
//    /**
//     * Fill the vertex buffer with the data from the PLY file.
//     */
//    private static RectBounds fillVertexBuffer(
//            ElementReader reader,
//            FloatBuffer vertexBuffer) throws IOException {
//
//// Just go though the vertices and store the coordinates in the buffer.
//        Element vertex = reader.readElement();
//        RectBounds bounds = new RectBounds();
//        while (vertex != null) {
//            double x = vertex.getDouble("x");
//            double y = vertex.getDouble("y");
//            double z = vertex.getDouble("z");
//            double nx = vertex.getDouble("nx");
//            double ny = vertex.getDouble("ny");
//            double nz = vertex.getDouble("nz");
//            vertexBuffer.put((float) x);
//            vertexBuffer.put((float) y);
//            vertexBuffer.put((float) z);
//            vertexBuffer.put((float) nx);
//            vertexBuffer.put((float) ny);
//            vertexBuffer.put((float) nz);
//            bounds.addPoint(x, y, z);
//            vertex = reader.readElement();
//        }
//        return bounds;
//    }
//
//    /**
//     * Fill the index buffer with the data from the PLY file.
//     */
//    private static void fillIndexBuffer(
//            ElementReader reader,
//            IntBuffer indexBuffer) throws IOException {
//
//// Just go though the triangles and store the indices in the buffer.
//        Element triangle = reader.readElement();
//        while (triangle != null) {
//
//            int[] indices = triangle.getIntList("vertex_index");
//            for (int index : indices) {
//                indexBuffer.put(index);
//            }
//
//            triangle = reader.readElement();
//        }
//    }
//
//    /**
//     * Loads the vertex and fragment shader.
//     */
//    private static int createShaders() throws IOException {
//
//// load and compile the vertex shader.
////        String vertexShaderCode = IOUtils.toString(
////                ClassLoader.getSystemResourceAsStream("vertexShader.glsl"));
////        System.out.println("shader: "+vertexShaderCode);
//        String vertexShaderCode = "/media/DATOS/Universidad/Proyectos/vertexShader.glsl";
//        int vertexShaderId = glCreateShaderObjectARB(GL_VERTEX_SHADER_ARB);
//        glShaderSourceARB(vertexShaderId, vertexShaderCode);
//        glCompileShaderARB(vertexShaderId);
//        printLogInfo(vertexShaderId);
//
//// load and compile the fragment shader.
////        String fragmentShaderCode = IOUtils.toString(
////                ClassLoader.getSystemResourceAsStream("fragmentShader.glsl"));
//        String fragmentShaderCode = "/media/DATOS/Universidad/Proyectos/fragmentShader.glsl";
//        int fragmentShaderId = glCreateShaderObjectARB(GL_FRAGMENT_SHADER_ARB);
//        glShaderSourceARB(fragmentShaderId, fragmentShaderCode);
//        glCompileShaderARB(fragmentShaderId);
//        printLogInfo(fragmentShaderId);
//
//// combine the two into a program.
//        int programId = ARBShaderObjects.glCreateProgramObjectARB();
//        glAttachObjectARB(programId, vertexShaderId);
//        glAttachObjectARB(programId, fragmentShaderId);
//        glValidateProgramARB(programId);
//        glLinkProgramARB(programId);
//        printLogInfo(programId);
//
//        return programId;
//    }
//
//    /**
//     * Helper to get the log messages from the shader compiler.
//     */
//    private static boolean printLogInfo(int obj) {
//        IntBuffer iVal = BufferUtils.createIntBuffer(1);
//        ARBShaderObjects.glGetObjectParameterivARB(obj,
//                ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB, iVal);
//
//        int length = iVal.get();
//        if (length > 1) {
//// We have some info we need to output.
//            ByteBuffer infoLog = BufferUtils.createByteBuffer(length);
//            iVal.flip();
//            ARBShaderObjects.glGetInfoLogARB(obj, iVal, infoLog);
//            byte[] infoBytes = new byte[length];
//            infoLog.get(infoBytes);
//            String out = new String(infoBytes);
//            System.out.println("Info log:\n" + out);
//        } else {
//            return true;
//        }
//        return false;
//    }
//}
