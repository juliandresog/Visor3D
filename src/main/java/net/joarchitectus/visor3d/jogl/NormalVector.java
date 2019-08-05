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
public class NormalVector {

    /**
     * x value of the vector.
     */
    public float x;

    /**
     * y value of the vector.
     */
    public float y;

    /**
     * z value of the vector.
     */
    public float z;

    /**
     * Constructor for the class.
     * <p>
     * NOTE: A length is not stored for these vector since they are all assumed to be of unit length, as this eases
     * the lighting calculation in the RendererMain and Canvas classes.
     *
     * @author Curtis White
     * @since 1.0
     * @param x0 x value of the vector.
     * @param y0 y value of the vector.
     * @param z0 z value of the vector.
     */
    public NormalVector(float x0, float y0, float z0) {
        x = x0;
        y = y0;
        z = z0;
    }
}
