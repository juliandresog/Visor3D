/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.joarchitectus.visor3d.jogl;

/**
 * 
 * @author 
 */
public class Face {

    /**
     * Counter for assigning IDs.
     */
 
   private static int counter = 0;

    /**
     * ID of the face.
     */
    public int id;

    /**
     * Number of vertices in the face.
     */
    public int vertices;

    /**
     * A list containing references to each vertex in the face.
     */
    public Vertex[] vertex_list;

    /**
     * The normal vector of the face.
     */
    public NormalVector normal;

    /**
     * Constructor for the class.
     *
     * @author Curtis White
     * @since 1.0
     * @param v0 The number of vertices in the face
     * @param v_list0 A list of the vertices in the face
     */
    public Face(int v0, Vertex[] v_list0) {
        // Save values from construction
        id = counter;
        counter++;
        vertices = v0;
        vertex_list = new Vertex[vertices];

        // Copy the values of v_list0
        for (int i = 0; i < vertices; i++) {
            vertex_list[i] = v_list0[i];
        }

        // Calculate the normal for this face
        normal = this.calcNormal();
    }

    /**
     * Calculates the normal vector for the face.
     * <p>
     * This uses vector calculus. By using 3 vectors that are not in a straight line and finding (v1 - v2) ` (v2 - v3)
     * we find a normal vector. This is then normalized to unit length and used.
     * <p>
     * NOTE: ` is the cross product of the two resultant vectors of the vector subtraction
     *
     * @author Curtis White
     * @since 1.0
     * @return a NormalVector object representing the normal vector of the face
     */
    public NormalVector calcNormal() {
        // Use the vertices of the face as the 3 vectors.
        // Using them as vectors results in a vector pointing from the origin to the vertex
        // For our use, this is fine
        Vertex v1 = vertex_list[0];
        Vertex v2 = vertex_list[1];
        Vertex v3 = vertex_list[2];

        // Define the x, y and z's of the resultant vectors
        // u1 is the x of the first vector, u2 is the y and so forth
        float u1, u2, u3, w1, w2, w3, out_x, out_y, out_z;

        // Find the two subtracted vectors
        u1 = v1.x - v2.x; w1 = v2.x - v3.x;
        u2 = v1.y - v2.y; w2 = v2.y - v3.y;
        u3 = v1.z - v2.z; w3 = v2.z - v3.z;

        // Cross product the vectors
        out_x = u2 * w3 - w2 * u3;
        out_y = w1 * u3 - u1 * w3;
        out_z = u1 * w2 - u2 * w1;

        // Normalize the length to unit length
        double len = Math.sqrt(Math.pow(out_x, 2) + Math.pow(out_y, 2) + Math.pow(out_z, 2));
        out_x /= len;
        out_y /= len;
        out_z /= len;

        NormalVector out = new NormalVector(out_x, out_y, out_z);

        return out;
    }
}
