/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.joarchitectus.visor3d.icp;

import java.util.Arrays;
import java.util.Random;
import java.util.TreeMap;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import net.joarchitectus.visor3d.jogl.Canvas;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.random.*;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class IterativeClosestPoint {

    private static int debugLevel = 0;
    private static org.slf4j.Logger log = LoggerFactory.getLogger(IterativeClosestPoint.class);

    /**
     * Set level of debugging output. 0 = none. 1 = some. 2 = all.
     *
     * @param level
     */
    public static void setDebugLevel(int level) {
        debugLevel = level;
    }

    /**
     * Calculate the transform (rotation and translation) between two 3D point
     * sets. Starts with an initial identity transform. Resulting transform
     * moves pointSet2 to pointSet1.
     *
     * @param pointSet1
     * @param pointSet2
     * @return
     */
    public static Matrix4d calcTransform(double[][] pointSet1, double[][] pointSet2, ListenerAvance listenerAvance) {
        if (debugLevel > 0) {
            log.warn("[ICP::calcTransform] no init transform");
        }
        Matrix4d initTransform = new Matrix4d();
        initTransform.setIdentity();
        return calcTransform(pointSet1, pointSet2, initTransform, listenerAvance);
    }

    /**
     * Calculate the transform (rotation and translation) between two 3D point
     * sets with an initial transform guess. Resulting transform moves pointSet2
     * to pointSet1.
     *
     * @param pointSet1
     * @param pointSet2
     * @param initTransform
     * @return
     */
    public static Matrix4d calcTransform(double[][] pointSet1, double[][] pointSet2, Matrix4d initTransform, ListenerAvance listenerAvance) {
        if (debugLevel > 0) {
            log.warn("[ICP::calcTransform]");
        }

        //sample pointSet2
        boolean samplePoints = true;
        double[][] localPointSet2;
        if (samplePoints) {
            int sparsity = 10;
            localPointSet2 = new double[pointSet2.length / sparsity][];
            for (int i = 0; i < localPointSet2.length; ++i) {
                localPointSet2[i] = pointSet2[i * sparsity];
            }
        } else {
            localPointSet2 = pointSet2;
        }
        //results (and also initial estimate)
        TreeMap<Double, Matrix4d> results = new TreeMap<Double, Matrix4d>();
        Matrix4d transform = new Matrix4d(initTransform);
        Matrix4d tempTransform = new Matrix4d();
        //internal pointSets
        double[][] matchedPointSet1;
        double[][] transformedPointSet2 = new double[localPointSet2.length][localPointSet2[0].length];
        //termination criteria
        double errorImprovementThreshold = 0.000001;
        double errorImprovement = Double.MAX_VALUE; //error improvement
        double lastError = Double.MAX_VALUE;
        double currError = Double.MAX_VALUE;
        //random restart params
        int maxNumRandomRestarts = 0;
        int numRandomRestarts = 0;
        double errorRestartThresh = 0.01;

        //apply initial rotation and translation to localPointSet2
        transformPoints(transform, localPointSet2, transformedPointSet2);

        int count = 0;
        while (errorImprovement > errorImprovementThreshold) {
            matchedPointSet1 = getClosestPoints(pointSet1, transformedPointSet2);

            //should this use pointSet1 and pointSet2?
            double[] mu1 = getExpectedValue(matchedPointSet1);
            double[] mu2 = getExpectedValue(transformedPointSet2);

            double[][] covarianceMatrix = getCovarianceMatrix(matchedPointSet1, transformedPointSet2, mu1, mu2);
            double[][] q = getQ(covarianceMatrix);
            double[] eigenVector = getMaxEigenVector(q);
            double[][] rotationMatrix = getRotationMatrix(eigenVector);
            double[] translationalVector = getTranslationVector(mu1, mu2, rotationMatrix);

            //accumulate transform
            tempTransform.setRow(0, rotationMatrix[0][0], rotationMatrix[0][1], rotationMatrix[0][2], translationalVector[0]);
            tempTransform.setRow(1, rotationMatrix[1][0], rotationMatrix[1][1], rotationMatrix[1][2], translationalVector[1]);
            tempTransform.setRow(2, rotationMatrix[2][0], rotationMatrix[2][1], rotationMatrix[2][2], translationalVector[2]);
            tempTransform.setRow(3, 0, 0, 0, 1.0);
            transform.mul(tempTransform, transform);

            //apply rotation and translation to localPointSet2
            transformPoints(transform, localPointSet2, transformedPointSet2);

            //calculate error
            currError = calculateError(matchedPointSet1, transformedPointSet2);
            errorImprovement = lastError - currError;
            lastError = currError;

            //print iteration results
            if (debugLevel > 1) {
                log.warn("Rot Matrix: ");
                for (double[] d : rotationMatrix) {
                    log.warn(Arrays.toString(d));
                }
                log.warn("Trans Vector: ");
                log.warn(Arrays.toString(translationalVector));
            }
            if (debugLevel > 0) {
                log.warn("Error: " + currError);
                log.warn("Error improvement: " + errorImprovement);
                log.warn("Accum Error: " + currError * localPointSet2.length);
                log.warn("Number used points: " + localPointSet2.length);
                log.warn("Iteration: " + count++);

                if (listenerAvance != null) {
                    listenerAvance.avance("Iteration: "+count);
                    listenerAvance.avance(count);
                }
            }

            //EXPERIMENTAL -- to get out of local mins
            if (errorImprovement <= errorImprovementThreshold && currError > errorRestartThresh && numRandomRestarts < maxNumRandomRestarts) {
                if (debugLevel > 0) {
                    System.out.printf("\n\nRandom restart number %d of %d\n", numRandomRestarts, maxNumRandomRestarts);
                }
                //add current results to list
                results.put(currError, new Matrix4d(transform));

                numRandomRestarts++;

                //randomize transform from current location
                Random rn = new Random();
                Matrix4d randomTransform = new Matrix4d();
                double maxRot = Math.PI / 20;

                //random transalte
                double maxTrans = 10.0;
                randomTransform.setIdentity();
                randomTransform.m03 = rn.nextDouble() * 2 * maxTrans - maxTrans;
                randomTransform.m13 = rn.nextDouble() * 2 * maxTrans - maxTrans;
                randomTransform.m23 = rn.nextDouble() * 2 * maxTrans - maxTrans;
                transform.mul(randomTransform);

                //apply rotation and translation to localPointSet2
                transformPoints(transform, localPointSet2, transformedPointSet2);

                //calculate error
                currError = calculateError(matchedPointSet1, transformedPointSet2);
                errorImprovement = Double.MAX_VALUE;  //reset so we re-enter main while loop
                lastError = currError;
            }
            //EXPERIMENTAL

            if (listenerAvance != null) {
                listenerAvance.matrixAvance(transform, count);
            }
        }

        if (debugLevel > 0) {
            log.warn("ICP resulting costs: " + results.keySet());
        }

        if (!results.isEmpty()) {
            return results.firstEntry().getValue();
        } else {
            return transform;
        }
    }

    /**
     * Transform points and place results in transformedPoints. Assumes arrays
     * are pre-allocated.
     *
     * @param transformMat
     * @param points
     * @param transformedPoints
     */
    private static void transformPoints(Matrix4d transformMat, double[][] points, double[][] transformedPoints) {
        Point3d tempPoint = new Point3d();
        for (int i = 0; i < points.length; ++i) {
            transformMat.transform(new Point3d(points[i][0], points[i][1], points[i][2]), tempPoint);
            transformedPoints[i][0] = tempPoint.x;
            transformedPoints[i][1] = tempPoint.y;
            transformedPoints[i][2] = tempPoint.z;
        }
    }

    /**
     * Calculate the error between two point sets.
     *
     * @param p1
     * @param p2
     * @return
     */
    private static double calculateError(double[][] p1, double[][] p2) {
        double error = 0.0;
        for (int i = 0; i < p1.length; ++i) {
            error += distance(p1[i], p2[i]);
        }
        error /= p1.length;
        return error;
    }

    /**
     * For every point in pointSet2, find its closest point in pointSet1. As a
     * result, the returned pointSet will be of the same size as pointSet2.
     *
     * @return
     */
    private static double[][] getClosestPoints(double[][] pointSet1, double[][] pointSet2) {
        double[][] matches = new double[pointSet2.length][];

        int i = 0;
        for (double[] p2 : pointSet2) {
            double[] closest = null;
            double bestDist = Double.POSITIVE_INFINITY;

            for (double[] p1 : pointSet1) {
                double distance = distance(p1, p2);
                if (distance < bestDist) {
                    closest = p1;
                    bestDist = distance;
                }
            }
            matches[i++] = closest;
        }

        return matches;
    }

    private static double distance(double[] p1, double[] p2) {
        double dist = 0;
        for (int i = 0; i < p1.length; ++i) {
            dist += Math.pow(p1[i] - p2[i], 2);
        }
        return Math.sqrt(dist);
    }

    private static double[] getExpectedValue(double[][] points) {
        double[] expectedVal = {0, 0, 0};

        for (int i = 0; i < points.length; i++) {
            expectedVal[0] += points[i][0];
            expectedVal[1] += points[i][1];
            expectedVal[2] += points[i][2];
        }

        expectedVal[0] /= points.length;
        expectedVal[1] /= points.length;
        expectedVal[2] /= points.length;

        return expectedVal;
    }

    private static double[][] getCovarianceMatrix(double[][] pointSet1, double[][] pointSet2, double[] mu1, double[] mu2) {
        if (pointSet1.length != pointSet2.length) {
            return null;
        }

        double[][] cov = new double[3][3];
        for (int n = 0; n < pointSet1.length; n++) {
            for (int i = 0; i < pointSet1[0].length; ++i) {
                for (int j = 0; j < pointSet1[0].length; ++j) {
                    cov[i][j] += pointSet2[n][i] * pointSet1[n][j];
                }
            }
        }

        //normalize and subtract means
        for (int i = 0; i < pointSet1[0].length; ++i) {
            for (int j = 0; j < pointSet1[0].length; ++j) {
                //normalize
                cov[i][j] /= pointSet1.length;

                //subtract means
                cov[i][j] -= mu2[i] * mu1[j];
            }
        }

        return cov;
    }

    private static double getTrace(double[][] e) {
        double trace = 0;
        for (int i = 0; i < e.length; i++) {
            trace += e[i][i];
        }

        return trace;
    }

    private static double[][] getQ(double[][] e) {
        if (debugLevel > 1) {
            log.warn("E: ");
            for (double[] d : e) {
                log.warn(Arrays.toString(d));
            }
            log.warn("");
        }

        double[][] q = new double[4][4];

        q[0][0] = getTrace(e);

        q[1][0] = e[1][2] - e[2][1];
        q[2][0] = e[2][0] - e[0][2];
        q[3][0] = e[0][1] - e[1][0];

        q[0][1] = q[1][0];
        q[0][2] = q[2][0];
        q[0][3] = q[3][0];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                q[1 + i][1 + j] = e[i][j] + e[j][i] - (i == j ? q[0][0] : 0);
            }
        }

        return q;
    }

    private static double[] getMaxEigenVector(double[][] q) {
        if (debugLevel > 1) {
            log.warn("Q: ");
            for (double[] d : q) {
                log.warn(Arrays.toString(d));
            }
            log.warn("");
        }

        RealMatrix Qmat = new BlockRealMatrix(q);
        EigenDecomposition evd = new EigenDecomposition(Qmat, 0.0);
        return evd.getEigenvector(0).toArray();
    }

    private static double[][] getRotationMatrix(double[] rotationVector) {
        double[][] r = new double[3][3];
        double[] rv = rotationVector;

        r[0][0] = rv[0] * rv[0] + rv[1] * rv[1] - rv[2] * rv[2] - rv[3] * rv[3];
        r[1][1] = rv[0] * rv[0] + rv[2] * rv[2] - rv[1] * rv[1] - rv[3] * rv[3];
        r[2][2] = rv[0] * rv[0] + rv[3] * rv[3] - rv[1] * rv[1] - rv[2] * rv[2];

        r[0][1] = 2 * (rv[1] * rv[2] - rv[0] * rv[3]);
        r[0][2] = 2 * (rv[1] * rv[3] + rv[0] * rv[2]);

        r[1][0] = 2 * (rv[1] * rv[2] + rv[0] * rv[3]);
        r[1][2] = 2 * (rv[2] * rv[3] - rv[0] * rv[1]);

        r[2][0] = 2 * (rv[1] * rv[3] - rv[0] * rv[2]);
        r[2][1] = 2 * (rv[2] * rv[3] + rv[0] * rv[1]);

        return r;
    }

    private static double[] getTranslationVector(double[] mu1, double[] mu2, double[][] rot) {
        double[] t = new double[3];

        t[0] = mu1[0] - (rot[0][0] * mu2[0] + rot[0][1] * mu2[1] + rot[0][2] * mu2[2]);
        t[1] = mu1[1] - (rot[1][0] * mu2[0] + rot[1][1] * mu2[1] + rot[1][2] * mu2[2]);
        t[2] = mu1[2] - (rot[2][0] * mu2[0] + rot[2][1] * mu2[1] + rot[2][2] * mu2[2]);

        return t;
    }

    // Returns a transformation Matrix from given corresponding points such that
    // oldpoints are transformed to newpoints
    public static Matrix4d transformMatFromCorrPoints(double[][] oldpoints, double[][] newpoints) {
        Matrix4d tempTransform = new Matrix4d();
        double[] mu1 = getExpectedValue(newpoints);
        double[] mu2 = getExpectedValue(oldpoints);

        double[][] covarianceMatrix = getCovarianceMatrix(newpoints, oldpoints, mu1, mu2);
        double[][] q = getQ(covarianceMatrix);
        double[] eigenVector = getMaxEigenVector(q);
        double[][] rotationMatrix = getRotationMatrix(eigenVector);
        double[] translationalVector = getTranslationVector(mu1, mu2, rotationMatrix);

        //accumulate transform
        tempTransform.setRow(0, rotationMatrix[0][0], rotationMatrix[0][1], rotationMatrix[0][2], translationalVector[0]);
        tempTransform.setRow(1, rotationMatrix[1][0], rotationMatrix[1][1], rotationMatrix[1][2], translationalVector[1]);
        tempTransform.setRow(2, rotationMatrix[2][0], rotationMatrix[2][1], rotationMatrix[2][2], translationalVector[2]);
        tempTransform.setRow(3, 0, 0, 0, 1.0);
        return tempTransform;
    }
}
