/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.joarchitectus.visor3d.jogl;

import java.util.ArrayList;
import java.util.List;

/**
 * A class representing a vertex in a 3D model.
 * @author 
 */
public class Vertex {

    /**
     * Counter for assigning IDs.
     */
    private static int counter = 0;

    /**
     * ID of the vertex.
     */
    public int id;

    /**
     * x value of the vertex.
     */
    public float x;

    /**
     * y value of the vertex.
     */
    public float y;

    /**
     * z value of the vertex.
     */
    public float z;

    /**
     * Confidence value of the vertex (holdover from model export).
     */
    public float confidence;

    /**
     * Intensity value of the vertex (holdover from model export).
     */
    public float intensity;

    /**
     * List containing all of the faces this vertex is a part of.
     */
    public List<Face> adj_face = new ArrayList<>();

    /**
     * The normal vector for this vertex.
     */
    public NormalVector normal;

    /**
     * Constructor for the class.
     *
     * @author Curtis White
     * @since 1.0
     * @param x0 x value of the vertex.
     * @param y0 y value of the vertex.
     * @param z0 z value of the vertex.
     * @param conf0 Confidence value of the vertex (holdover from model export)
     * @param int0 Intensity value of the vertex (holdover from model export)
     */
    public Vertex(float x0, float y0, float z0, float conf0, float int0) {
        // Assign values from construction
        id = counter;
        counter++;
        x = x0;
        y = y0;
        z = z0;
        confidence = conf0;
        intensity = int0;
    }

    /**
     * Calculates the normal vector for the vertex.
     * <p>
     * This uses the list of adjacent faces to create a normal vector. It does this by averaging the normals of the
     * adjacent faces and then normalizing the length of the resultant vector.
     *
     * @author Curtis White
     * @since 1.0
     * @return a NormalVector object representing the normal vector of the vertex
     */
    public NormalVector calcNormal() {
        float sumx = 0;
        float sumy = 0;
        float sumz = 0;

        for (int i = 0; i < adj_face.size(); i++) {
            sumx += adj_face.get(i).normal.x;
            sumy += adj_face.get(i).normal.y;
            sumz += adj_face.get(i).normal.z;
        }

        NormalVector out = new NormalVector(sumx/adj_face.size(),
                sumy/adj_face.size(),
                sumz/adj_face.size());

        return out;
    }
}