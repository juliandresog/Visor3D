/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package icp;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import net.joarchitectus.visor3d.icp.IterativeClosestPoint;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.stat.ranking.NaNStrategy;
import org.apache.commons.math3.stat.ranking.NaturalRanking;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.LoggerFactory;

/**
 *
 * @author josorio
 */
public class ICPTest {
    
    private static org.slf4j.Logger log = LoggerFactory.getLogger(ICPTest.class);

    public ICPTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test
    public void ejecutarICP() {
        log.warn("Iniciado...");
        RandomGenerator randomGenerator = new JDKRandomGenerator();
        randomGenerator.setSeed(100);
        //Random r=new Random();

        double[][] pointSet1 = new double[10][3];
        double[][] pointSet2 = new double[10][3];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 3; j++) {
                pointSet1[i][j] = randomGenerator.nextDouble()*10;
            }
        }
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 3; j++) {
                pointSet2[i][j] = randomGenerator.nextDouble()*10;
            }
        }

        IterativeClosestPoint.setDebugLevel(1);
        Matrix4d matrix4d = IterativeClosestPoint.calcTransform(pointSet1, pointSet2, null);

        System.out.println("A: " + matrix4d.toString());

        Point3d originalPoint = new Point3d(1, 2, 3);
        System.out.println("B: " + transformPoint(matrix4d, originalPoint));
    }

    // Returns the transformed point of the original point given a transform matrix
    private static Point3d transformPoint(Matrix4d transformMat, Point3d originalPoint) {
        Point3d tempPoint = new Point3d();
        transformMat.transform(new Point3d(originalPoint.getX(), originalPoint.getY(), originalPoint.getZ()), tempPoint);
        return tempPoint;
    }
}
