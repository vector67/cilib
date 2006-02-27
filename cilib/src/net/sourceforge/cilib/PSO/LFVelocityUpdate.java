/*
 * LFVelocityUpdate.java
 *
 * Created on January 31, 2004, 2:02 PM
 * 
 * Copyright (C) 2004 - CIRG@UP 
 * Computational Intelligence Research Group (CIRG@UP)
 * Department of Computer Science 
 * University of Pretoria
 * South Africa
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA 
 */

package net.sourceforge.cilib.PSO;

import net.sourceforge.cilib.Problem.GradientOptimisationProblem;

/**
 *
 * @author  barlad
 */
public class LFVelocityUpdate implements VelocityUpdate {
    
    /** Creates a new instance of LFVelocityUpdate */
    public LFVelocityUpdate() {
        standard = new StandardVelocityUpdate();
     }
    
    public void setStandardVelocityUpdate(StandardVelocityUpdate standard) {
        this.standard = standard;
    }
    
    public StandardVelocityUpdate getStandardVelocityUpdate() {
        return standard;
    }

    public void updateVelocity(Particle particle) {
        LFDecorator lfParticle = LFDecorator.extract(particle);
        if (particle.getNeighbourhoodBest().getId() == particle.getId()) {            
            double p = lfParticle.getP();
            double delta = lfParticle.getDelta();
            double delta1 = lfParticle.getDelta1();
            double deltaT = lfParticle.getDeltaT();
            double epsilon = lfParticle.getEpsilon();
            int s = lfParticle.getS();
            int m = lfParticle.getM();
            int i = lfParticle.getI();
            int j = lfParticle.getJ();            
            
            double[] velocity = particle.getVelocity();
            double[] position = particle.getPosition();
            double[] previousPosition = lfParticle.getPreviousPosition();
            double[] previousVelocity = lfParticle.getPreviousVelocity();
            double[] nextGradient = lfParticle.getNextGradient();  

            double[] gradient = lfParticle.getGradient();
            
            // If the particle was not the neighbourhood best in the previous
            // epoch, reinitialise the leapfrog variables.
            if (! lfParticle.getWasNeighbourhoodBest()) {
                deltaT = lfParticle.getDefaultDeltaT();
                                
                // Reset state variables
                i = 0;
                j = 2;
                s = 0;
                p = 1;                
                
                // LeapFrog Algorithm Step 2
                // Calculate initial gradient
                double [] functionGradient = getGradient(position);
                for (int l = 0; l < particle.getDimension(); ++l) {
                    gradient[l] = -functionGradient[l];
                }
                
                // Calculate initial velocity
                for (int l = 0; l < particle.getDimension(); ++l) {
                    
                    velocity[l] = 0.5 * gradient[l] * deltaT;
                }

                lfParticle.setWasNeighbourhoodBest(true);
            }   
          
            // LeapFrog Algorithm Step 3
            double lenDeltaX = calculateEuclidianLength(velocity) * deltaT;
            
            // LeapFrog Algorithm Step 4
            if (lenDeltaX < delta) {
                // LeapFrog Algorithm Step 5a: Update p and deltaT
                p = p + delta1;
                deltaT = deltaT * p;
            }
            else {                
                for (int l = 0; l < particle.getDimension(); ++l) {
                    velocity[l] = delta * velocity[l] / (lenDeltaX);
                }
                
                // LeapFrog Algorithm Step 5b
                if (s < m) {
                    deltaT = deltaT / 2.0;
                    double tmp;
                    for (int l = 0; l < particle.getDimension(); ++l) {
                        tmp = position[l] + previousPosition[l];
                        previousPosition[l] = position[l];
                        position[l] = tmp / 2.0;
                        
                        tmp = velocity[l] + previousVelocity[l];
                        previousVelocity[l] = velocity[l];
                        velocity[l] = tmp / 2.0;
                        s = 0;
                    }                    
                }
            }
            
            // Step 5: Update position;
            for (int l = 0; l < particle.getDimension(); ++l) {
                previousPosition[l] = position[l];
                position[l] += velocity[l] * deltaT;                
            }

            boolean repeatEndLoop = true;
            
            while (repeatEndLoop)
            {
                // LeapFrog Algorithm Step 6
                double [] functionGradient = getGradient(position);
                for (int l = 0; l < particle.getDimension(); ++l) {
                    nextGradient[l] = -functionGradient[l];
                }           
                
                for (int l = 0; l < particle.getDimension(); ++l) {
                    previousVelocity[l] = velocity[l];
                    velocity[l] += nextGradient[l] * deltaT;
                }
                
                // LeapFrog Algorithm Step 7a
                if (calculateDotProduct(nextGradient, gradient) > 0) {
                    s = 0;                
                }
                else {
                    ++s;
                    p = 1;                
                }

                for (int l = 0; l < particle.getDimension(); ++l) {
                    gradient[l] = nextGradient[l];
                }

                // LeapFrog Algorithm Step 7
                if (calculateEuclidianLength(nextGradient) > epsilon) {
                    // LeapFrog Algorithm Step 8
                    if (calculateEuclidianLength(velocity) > calculateEuclidianLength(previousVelocity)) {
                        i = 0;
                        repeatEndLoop = false;
                        }
                    else {
                        double tmp;
                        for (int l = 0; l < particle.getDimension(); ++l) {
                            tmp = position[l] + previousPosition[l];
                            previousPosition[l] = position[l];
                            position[l] = tmp / 2.0;
                        }
                        ++i;

                        // LeapFrog Algorithm  Step 9
                        if (i <= j) {
                            for (int l = 0; l < particle.getDimension(); ++l) {
                                tmp = velocity[l] + previousVelocity[l];
                                previousVelocity[l] = velocity[l];
                                velocity[l] = tmp / 4.0;                            
                            }
                        }
                        else {
                            for (int l = 0; l < particle.getDimension(); ++l) {
                                previousVelocity[l] = velocity[l];
                                velocity[l] = 0;
                                j = 1;
                            }
                        }
                    }
                } 
                else {
                    repeatEndLoop = false;                    
                }
            }
               
            // Set the state values in the particle
            lfParticle.setP(p);
            lfParticle.setDeltaT(deltaT);
            lfParticle.setS(s);
            lfParticle.setI(i);
            lfParticle.setJ(j);
        }
        else {
            lfParticle.setWasNeighbourhoodBest(false);
            standard.updateVelocity(particle);
        }
    }
    
    /**
     *  Returns the euclidian length of a vector x
     */
    private double calculateEuclidianLength(double x []) {
        double l = 0;
        for (int i = 0; i < x.length; i++) {
            l += x[i] * x[i];
        }        
        return (Math.sqrt(l));
    }

    /**
     *  Returns the dot product of two vectors.
     */
    private double calculateDotProduct(double x [], double y []) {
        if (x.length != y.length) {
            throw new RuntimeException("Cannot calculate dot product because vectors are of different sizes.");
        }
        else {            
            double t = 0;
            for (int i = 0; i < x.length; i++) {
            t += x[i] * y[i];
            }
            return t;
        }
    }
    
    public void setGradientOptimisationProblem(GradientOptimisationProblem problem) {
        this.problem = problem;         
    }    
    
    /**
     * Returns the gradient of the problem function at the given position
     */
    public double [] getGradient(double [] position) {
        return problem.getGradient(position);
    }    
    
    private StandardVelocityUpdate standard;    
    private GradientOptimisationProblem problem;  
}