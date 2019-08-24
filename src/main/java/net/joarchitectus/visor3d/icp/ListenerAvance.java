/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.joarchitectus.visor3d.icp;

import java.util.Map;
import javax.vecmath.Matrix4d;

/**
 *
 * @author josorio
 */
public interface ListenerAvance {
    
    public void avance(String text);
    
    public void avance(int iteracion);
    
    public void matrixAvance(Matrix4d matrix4d, int iteracion);

    public void addHistorico(Map registro);
}
