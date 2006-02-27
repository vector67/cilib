/*
 * UrsemF3.java
 *
 * Created on June 4, 2003, 1:46 PM
 *
 * 
 * Copyright (C) 2003, 2004 - CIRG@UP 
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
 *   
 */
package net.sourceforge.cilib.Functions;

import net.sourceforge.cilib.Domain.Validator.DimensionValidator;


/**
 * <p>Title: CILib</p>
 * <p>Description: CILib (Computational Intelligence Library)</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Clive Naicker
 * @version 1.0
 */

public class UrsemF3 extends ContinuousFunction {

    public UrsemF3() {
        constraint.add(new DimensionValidator(2));
        setDomain("R(-2, 2)^2");
    }

    public Object getMinimum() {
        return new Double(2.5);
    }
    
    public double evaluate(double[] X) {
        double x = X[0];
        double y = X[1];

        double result = Math.sin(2.2*Math.PI*x + 0.5*Math.PI)*((2.0 - Math.abs(y))/2.0)*((3.0 - Math.abs(x))/2.0);
        result += Math.sin(0.5*Math.PI*y*y + 0.5*Math.PI)*((2.0 - Math.abs(y))/2.0)*((2.0 - Math.abs(x))/2.0);

        return result;
    }

}