/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/* To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wsntools.iris.modules.gui.worldmap.model;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.Path;
import java.awt.Color;

/**
 * Helperclass to determine a color for a set of nodes of a path
 * @author Marvin Baudewig
 */
public class PathColor implements Path.PositionColors{
private int amountOfNodes;
private Color color;
    
	/**
	 * 
	 * @param amountOfNodes The amount of nodes on a path
	 * @param color the basecolor for the latest node
	 */
    public PathColor(int amountOfNodes,Color color)
    {
        this.amountOfNodes = amountOfNodes;
        this.color = color;
    }
    
	/**
	 * Determines a fading color depending of the position of the node in the list of nodes
	 * @param pstn Position of the current node (not used in derived class)
	 * @param i number of node in the current path of nodes 
	 * @see gov.nasa.worldwind.render.Path.PositionColors#getColor(gov.nasa.worldwind.geom.Position, int)
	 */
    @Override
    public Color getColor(Position pstn, int i) {
        if(i>amountOfNodes)
            throw new IllegalArgumentException("Try to create a color for a node, that is out of range");
        float[] hsvcol = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsvcol);
        //return Color.getHSBColor(color, 0.25f+(i+1)/(float)amountOfNodes*0.75f, 1.0f);
        return Color.getHSBColor(hsvcol[0], 0.25f+(i+1)/(float)amountOfNodes*0.75f, 1.0f);
    }
    
}
