/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.joarchitectus.visor3d.icp;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.vecmath.Point3d;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.DistanceFunction;
import weka.core.EuclideanDistance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.neighboursearch.KDTree;
import weka.core.neighboursearch.LinearNNSearch;

/**
 *
 * @author josorio
 */
public class KNNDemo {

    /**
     * This method is to load the data set.
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public static Instances getDataSet(String fileName) throws IOException {
        /**
         * we can set the file i.e., loader.setFile("finename") to load the data
         */
        int classIdx = 1;
        /**
         * the arffloader to load the arff file
         */
        ArffLoader loader = new ArffLoader();
        /**
         * load the traing data
         */
        loader.setSource(KNNDemo.class.getResourceAsStream("/" + fileName));
        /**
         * we can also set the file like loader3.setFile(new
         * File("test-confused.arff"));
         */
        //loader.setFile(new File(fileName));
        Instances dataSet = loader.getDataSet();
        /**
         * set the index based on the data given in the arff files
         */
        dataSet.setClassIndex(classIdx);
        return dataSet;
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void process() throws Exception {

        Instances data = getDataSet("data.arff");
        data.setClassIndex(data.numAttributes() - 1);
        //k - the number of nearest neighbors to use for prediction
        Classifier ibk = new IBk(1);
        ibk.buildClassifier(data);

        System.out.println(ibk);

        Evaluation eval = new Evaluation(data);
        eval.evaluateModel(ibk, data);
        /**
         * Print the algorithm summary
         */
        System.out.println("** KNN Demo  **");
        System.out.println(eval.toSummaryString());
        System.out.println(eval.toClassDetailsString());
        System.out.println(eval.toMatrixString());

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
    
    public void runKNN(List<Point3d> points) {
        //
        // Create the weka datastructure for 3D Points
        //
        Instances wekaPoints1 = insertIntoWeka(points, "wekaPoints1");
        
        weka.core.neighboursearch.LinearNNSearch knn = new LinearNNSearch(
            wekaPoints1);
        
        // let's search for the nearest and second nearest neighbor
        Instance nn1, nn2;
        final Instance p = createInstance(new Point3d(0, 0, 0), wekaPoints1);

        try {
            EuclideanDistance df = new EuclideanDistance(wekaPoints1);
            df.setDontNormalize(true);

            knn.setDistanceFunction(df);
            
            Instances neighbors = knn.kNearestNeighbours(p, 2);
            nn1 = neighbors.instance(0);
            nn2 = neighbors.instance(1);
        } catch (Exception e) {
            nn1 = nn2 = null;
        }

        System.out.println(nn1 + " is the nearest neigbor for " + p);
        System.out.println(nn2 + " is the second nearest neigbor for " + p);

        // Now we can also easily compute the distances as the KDTree does it
        DistanceFunction df = knn.getDistanceFunction();
        System.out.println("The distance between" + nn1 + " and " + p + " is " + df.distance(nn1, p));
        System.out.println("The distance between" + nn2 + " and " + p + " is " + df.distance(nn2, p));
        
    }

    public void runKDTree(List<Point3d> points) {
        //
        // Create the weka datastructure for 3D Points
        //

        Instances wekaPoints1 = insertIntoWeka(points, "wekaPoints1");

        // 
        // Set up the KDTree
        //
        KDTree tree = new KDTree();

        try {
            tree.setInstances(wekaPoints1);

            EuclideanDistance df = new EuclideanDistance(wekaPoints1);
            df.setDontNormalize(true);

            tree.setDistanceFunction(df);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // let's search for the nearest and second nearest neighbor
        Instance nn1, nn2;
        final Instance p = createInstance(new Point3d(0, 0, 0), wekaPoints1);

        try {
            Instances neighbors = tree.kNearestNeighbours(p, 2);
            nn1 = neighbors.instance(0);
            nn2 = neighbors.instance(1);
        } catch (Exception e) {
            nn1 = nn2 = null;
        }

        System.out.println(nn1 + " is the nearest neigbor for " + p);
        System.out.println(nn2 + " is the second nearest neigbor for " + p);
        
        System.out.println(nn1.value(0));

        // Now we can also easily compute the distances as the KDTree does it
        DistanceFunction df = tree.getDistanceFunction();
        System.out.println("The distance between" + nn1 + " and " + p + " is " + df.distance(nn1, p));
        System.out.println("The distance between" + nn2 + " and " + p + " is " + df.distance(nn2, p));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        RandomGenerator randomGenerator = new JDKRandomGenerator();
        randomGenerator.setSeed(100);
        //Random r=new Random();

        double[][] pointSet1 = new double[10][3];
        double[][] pointSet2 = new double[10][3];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 3; j++) {
                pointSet1[i][j] = randomGenerator.nextDouble() * 10;
            }
        }
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 3; j++) {
                pointSet2[i][j] = randomGenerator.nextDouble() * 10;
            }
        }

        List<Point3d> points = new ArrayList<>();
        for (double[] p1 : pointSet1) {
            Point3d point = new Point3d(p1[0], p1[1], p1[2]);
            points.add(point);
        }
        
        new KNNDemo().runKDTree(points);
        System.out.println("OTRO:");
        new KNNDemo().runKNN(points);

    }

}
