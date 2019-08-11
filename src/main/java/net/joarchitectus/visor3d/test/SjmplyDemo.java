/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.joarchitectus.visor3d.test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jengineering.sjmply.PLY;
import org.jengineering.sjmply.PLYElementList;
import static org.jengineering.sjmply.PLYType.FLOAT32;
import static org.jengineering.sjmply.PLYType.FLOAT64;
import static org.jengineering.sjmply.PLYType.INT32;
import static org.jengineering.sjmply.PLYType.LIST;
import static org.jengineering.sjmply.PLYType.UINT16;
import static org.jengineering.sjmply.PLYType.UINT8;
import org.jengineering.sjmply.PLY_Plotly;

/**
 * https://github.com/DirkToewe/sjmply
 *
 * @author josorio
 */
public class SjmplyDemo {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // TODO code application logic here
            //Path path = Paths.get( System.getProperty("user.home"), "Documents/3d_models/bunny.ply" );
            //Path path = Paths.get("/media/DATOS/Universidad/Proyectos/bunny/reconstruction/bun_zipper.ply");
            Path path = Paths.get("/media/DATOS/Universidad/Proyectos/horse.ply");
            //Path path = Paths.get("/media/DATOS/Universidad/Proyectos/Armadillo_scans/ArmadilloStand_0.ply");
            PLY bunny = PLY.load(path);
            //System.out.println(bunny);
            //PLY_Plotly.Mesh3d_show("Bunneh",bunny);
            //PLY_Plotly.Scatter3d_show("Bunneh",bunny);

            PLYElementList vertex = bunny.elements("vertex");
            PLYElementList face;
            try{
                face = bunny.elements("face");
            }catch(NoSuchElementException ex){
                face = new PLYElementList(0);
            }
            System.out.println("Vertices:"+ vertex);
            System.out.println("Caras: "+face);
            float[] x = vertex.property(FLOAT32, "x");
            if(face.size>0){
                int[][] vertex_indices = face.property(LIST(INT32), "vertex_indices");
                System.out.println("Indices vertices: "+Arrays.deepToString(vertex_indices));
                short[][] vertex_indices_short = face.propertyAs(LIST(UINT16), "vertex_indices");
                System.out.println("Indices vertex short"+Arrays.deepToString(vertex_indices_short));
                bunny.elements("face").convertProperty( "vertex_indices", LIST(UINT8,UINT16) );
                System.out.println("Convert face: "+ bunny.elements("face") );
            }
            
            System.out.println("Vertice X: "+Arrays.toString(x));
            
            double[] x_double = vertex.propertyAs(FLOAT64, "x");
            
            System.out.println("Vertice x double: "+Arrays.toString(x_double));          
            
            
        } catch (IOException ex) {
            Logger.getLogger(SjmplyDemo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
