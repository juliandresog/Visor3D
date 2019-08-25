/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.joarchitectus.visor3d.icp;

import java.util.Iterator;
import java.util.List;
import javax.vecmath.Point3d;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.EuclideanDistance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.neighboursearch.KDTree;
import weka.core.neighboursearch.LinearNNSearch;
import weka.core.neighboursearch.NearestNeighbourSearch;

/**
 *
 * @author josorio
 */
public class WekaNearestNeighbourSearch {

    private List<Point3d> points;
    private Instances wekaPoints;
    private NearestNeighbourSearch search;

    /**
     * Puntos
     *
     * @param points
     */
    public WekaNearestNeighbourSearch(List<Point3d> points) throws Exception {
        this.points = points;

        //
        // Create the weka datastructure for 3D Points
        //
        wekaPoints = insertIntoWeka(points, "wekaPoints1");
        
        //search = new LinearNNSearch();   
        search = new KDTree();
        search.setInstances(wekaPoints);
        
        EuclideanDistance df = new EuclideanDistance(wekaPoints);
        df.setDontNormalize(true);

        search.setDistanceFunction(df);
    }

    /**
     * El punto mas cercano usando algorritmos de weka
     * @param punto
     * @return
     * @throws Exception 
     */
    public Point3d puntoMasCercano(Point3d punto) throws Exception {

        Instance p = createInstance(punto, wekaPoints);         

        Instances neighbors = search.kNearestNeighbours(p, 1);
        
        Instance nn1 = neighbors.instance(0);
        
        return new Point3d(nn1.value(0), nn1.value(1), nn1.value(2));
    }

    /**
     * Creates a Weka Datastructure out of a List of 3D Points
     *
     * @param points - List of 3D Points
     * @param name - Instance name
     * @return Instances containing all 3D points
     */
    public Instances insertIntoWeka(final List<Point3d> points, final String name) {
        // Create numeric attributes "x" and "y" and "z"
        Attribute x = new Attribute("x");
        Attribute y = new Attribute("y");
        Attribute z = new Attribute("z");

        // Create vector of the above attributes
        FastVector attributes = new FastVector(3);
        attributes.addElement(x);
        attributes.addElement(y);
        attributes.addElement(z);

        // Create the empty datasets "wekaPoints" with above attributes
        Instances wekaPoints = new Instances(name, attributes, 0);

        for (Iterator<Point3d> i = points.iterator(); i.hasNext();) {
            // Create empty instance with three attribute values
            Instance inst = new DenseInstance(3);

            // get the point3d
            Point3d p = i.next();

            // Set instance's values for the attributes "x", "y", and "z"
            inst.setValue(x, p.x);
            inst.setValue(y, p.y);
            inst.setValue(z, p.z);

            // Set instance's dataset to be the dataset "wekaPoints"
            inst.setDataset(wekaPoints);

            // Add the Instance to Instances
            wekaPoints.add(inst);
        }

        return wekaPoints;
    }

    /**
     * Create an Instance of the same type as the Instances object you are
     * searching in.
     *
     * @param p - a 3D point
     * @param dataset - the dataset you are searching in, which was used to
     * build the KDTree
     * @return an Instance that the nearest neighbor can be found for
     */
    public Instance createInstance(final Point3d p, final Instances dataset) {
        // Create numeric attributes "x" and "y" and "z"
        Attribute x = dataset.attribute(0);
        Attribute y = dataset.attribute(1);
        Attribute z = dataset.attribute(2);

//        // Create vector of the above attributes
//        FastVector attributes = new FastVector(3);
//        attributes.addElement(x);
//        attributes.addElement(y);
//        attributes.addElement(z);
        // Create empty instance with three attribute values
        Instance inst = new DenseInstance(3);

        // Set instance's values for the attributes "x", "y", and "z"
        inst.setValue(x, p.x);
        inst.setValue(y, p.y);
        inst.setValue(z, p.z);

        // Set instance's dataset to be the dataset "points1"
        inst.setDataset(dataset);

        return inst;
    }

}
