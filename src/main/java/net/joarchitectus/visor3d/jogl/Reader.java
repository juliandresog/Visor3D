/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.joarchitectus.visor3d.jogl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import org.jengineering.sjmply.PLY;
import org.jengineering.sjmply.PLYElementList;
import static org.jengineering.sjmply.PLYType.FLOAT32;
import static org.jengineering.sjmply.PLYType.INT32;
import static org.jengineering.sjmply.PLYType.LIST;
import org.smurn.jply.Element;
import org.smurn.jply.ElementReader;
import org.smurn.jply.ElementType;
import org.smurn.jply.PlyReader;

/**
 * Handler to read from a .ply file and create the vertices and faces stored
 * within the file.
 *
 * @author
 */
public class Reader {

    /**
     * String of the filename.
     */
    public String file;

    /**
     * List of all read in vertices.
     */
    public Vertex[] vertex_list;

    /**
     * List of all read in faces.
     */
    public Face[] face_list;

    /**
     * Number of vertices to be read in.
     */
    public int vertex_len;

    /**
     * Number of faces to be read in
     */
    public int face_len;
    
    public Reader(){
        face_len = 0;
        vertex_len = 0;
        vertex_list = new Vertex[0];
        face_list = new Face[0];
    }

    /**
     * Usando sjmPLy
     *
     * @param path
     * @throws IOException
     */
    public Reader(Path path) throws IOException {
        PLY objeto3D = PLY.load(path);

        PLYElementList vertex = objeto3D.elements("vertex");
        PLYElementList face;
        try {
            face = objeto3D.elements("face");
        } catch (NoSuchElementException ex) {
            face = new PLYElementList(0);
        }

        vertex_len = vertex.size;
        vertex_list = new Vertex[vertex_len];
        face_len = face.size;
        face_list = new Face[face_len];

        float[] x = vertex.property(FLOAT32, "x");
        float[] y = vertex.property(FLOAT32, "y");
        float[] z = vertex.property(FLOAT32, "z");

        for (int i = 0; i < vertex_len; i++) {
            // Create a vertex from the split line
            vertex_list[i] = new Vertex(
                    x[i],
                    y[i],
                    z[i],
                    0.0f,//new Float(element.getDouble("confidence")),
                    0.5f //new Float(element.getDouble("intensity"))
            );
        }

        // Small list for constructing faces
        Vertex[] vert_list = new Vertex[3];
        if (face.size > 0) {
            int[][] vertex_indices = face.property(LIST(INT32), "vertex_indices");
            for (int i = 0; i < face_len; i++) {
                // Create a face from the split line, finding the vertices needed for the face
                vert_list[0] = vertex_list[vertex_indices[i][0]];
                vert_list[1] = vertex_list[vertex_indices[i][1]];
                vert_list[2] = vertex_list[vertex_indices[i][2]];

                face_list[i] = new Face(3, vert_list);

                // Add the new face to each vertex's list of adjacent faces
                vert_list[0].adj_face.add(face_list[i]);
                vert_list[1].adj_face.add(face_list[i]);
                vert_list[2].adj_face.add(face_list[i]);
            }
        }

        for (int i = 0; i < vertex_list.length; i++) {
            // Loop over vertices and find their normal vector
            // This has to be done last so we have all the faces
            vertex_list[i].normal = vertex_list[i].calcNormal();
        }

    }

    /**
     *
     * @param ply
     * @throws IOException
     */
    @Deprecated
    public Reader(PlyReader ply) throws IOException {
        // Save the filename, could allow us to write back later if needed
        //file = ply.toString();

        // Flag lets us know when we aren't reading header
        boolean flag = false;

        // Counters for the loop
        int vertex_counter = 0;
        int face_counter = 0;

        // Small list for constructing faces
        Vertex[] vert_list = new Vertex[3];

        // Create the reader and loop over the lines
        ElementReader reader = ply.nextElementReader();
        while (reader != null) {
            ElementType type = reader.getElementType();

            // In PLY files vertices always have a type named "vertex".
            if (type.getName().equals("vertex")) {
                // The number of elements is known in advance.This is great for // allocating buffers and a like.
                // You can even get the element counts for each type before getting 
                // the corresponding reader via the PlyReader.getElementCount(..)
                // method.
                //System.out.println("There are " + reader.getCount() + " vertices:");
                // Extracts the number of vertices in the file
                vertex_len = reader.getCount();
                vertex_list = new Vertex[vertex_len];

                // Read the elements. They all share the same type.
                Element element = reader.readElement();
                while (element != null) {
                    //System.out.println(vertex_counter+" :> "+element.toString());
                    // Use the the 'get' methods to access the properties.
                    // jPly automatically converts the various data types supported
                    // by PLY for you.
                    // Create a vertex from the split line
                    vertex_list[vertex_counter] = new Vertex(
                            new Float(element.getDouble("x")),
                            new Float(element.getDouble("y")),
                            new Float(element.getDouble("z")),
                            new Float(element.getDouble("confidence")),
                            0.5f //new Float(element.getDouble("intensity"))
                    );
                    vertex_counter++;

                    element = reader.readElement();
                }
            } else if (type.getName().equals("face")) {
                face_len = reader.getCount();
                face_list = new Face[face_len];

                // Read the elements. They all share the same type.
                Element element = reader.readElement();
                while (element != null) {
                    //System.out.println(face_counter+" :> "+element.toString());
                    int[] indices = element.getIntList("vertex_indices");

                    // Create a face from the split line, finding the vertices needed for the face
                    vert_list[0] = vertex_list[indices[0]];
                    vert_list[1] = vertex_list[indices[1]];
                    vert_list[2] = vertex_list[indices[2]];

                    face_list[face_counter] = new Face(3, vert_list);

                    // Add the new face to each vertex's list of adjacent faces
                    vert_list[0].adj_face.add(face_list[face_counter]);
                    vert_list[1].adj_face.add(face_list[face_counter]);
                    vert_list[2].adj_face.add(face_list[face_counter]);
                    face_counter++;

                    element = reader.readElement();
                }

            }

            // Close the reader for the current type before getting the next one.
            reader.close();

            reader = ply.nextElementReader();
        }

        ply.close();

        for (int i = 0; i < vertex_list.length; i++) {
            // Loop over vertices and find their normal vector
            // This has to be done last so we have all the faces
            vertex_list[i].normal = vertex_list[i].calcNormal();
        }
    }

//    /**
//     * Constructor for the class.
//     *
//     * @author Curtis White
//     * @since 1.0
//     * @param filename The filename to be read from.
//     * @throws IOException Exception thrown with file errors
//     */
//    @Deprecated
//    public Reader(String filename) throws IOException {
//        // Save the filename, could allow us to write back later if needed
//        file = filename;
//
//        // Flag lets us know when we aren't reading header
//        boolean flag = false;
//
//        // Counters for the loop
//        int vertex_counter = 0;
//        int face_counter = 0;
//
//        // Small list for constructing faces
//        Vertex[] vert_list = new Vertex[3];
//
//        // Create the reader and loop over the lines
//        BufferedReader read = new BufferedReader(new FileReader(file));
//        for (String line = read.readLine(); line != null; line = read.readLine()) {
//            // Split the line on spaces
//            String[] splits = line.split(" ");
//            if (flag) {
//                if (vertex_counter < vertex_len) {
//                    // Create a vertex from the split line
//                    vertex_list[vertex_counter] = new Vertex(Float.parseFloat(splits[0]),
//                            Float.parseFloat(splits[1]),
//                            Float.parseFloat(splits[2]),
//                            Float.parseFloat(splits[3]),
//                            Float.parseFloat(splits[4]));
//                    vertex_counter++;
//                } else if (face_counter < face_len) {
//                    // Create a face from the split line, finding the vertices needed for the face
//                    vert_list[0] = vertex_list[Integer.parseInt(splits[1])];
//                    vert_list[1] = vertex_list[Integer.parseInt(splits[2])];
//                    vert_list[2] = vertex_list[Integer.parseInt(splits[3])];
//
//                    face_list[face_counter] = new Face(Integer.parseInt(splits[0]), vert_list);
//
//                    // Add the new face to each vertex's list of adjacent faces
//                    vert_list[0].adj_face.add(face_list[face_counter]);
//                    vert_list[1].adj_face.add(face_list[face_counter]);
//                    vert_list[2].adj_face.add(face_list[face_counter]);
//                    face_counter++;
//                }
//            } else if (line.contains("end_header")) {
//                // Find the end of the header and flag that we are entering real data
//                flag = true;
//            } else if (line.contains("element vertex")) {
//                // Extracts the number of vertices in the file
//                vertex_len = Integer.parseInt(splits[2]);
//                vertex_list = new Vertex[vertex_len];
//            } else if (line.contains("element face")) {
//                // Extracts the number of faces in the file
//                face_len = Integer.parseInt(splits[2]);
//                face_list = new Face[face_len];
//            }
//        }
//        for (int i = 0; i < vertex_list.length; i++) {
//            // Loop over vertices and find their normal vector
//            // This has to be done last so we have all the faces
//            vertex_list[i].normal = vertex_list[i].calcNormal();
//        }
//    }
}
