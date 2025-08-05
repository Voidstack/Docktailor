// Copyright © 2019-2025 Andy Goryachev <andy@goryachev.com>
package com.enosi.docktailor.docktailor.fx;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Alias for SimpleLongProperty.
 */
public class FxDouble
	extends SimpleDoubleProperty
{
	public FxDouble(double initialValue)
	{
		super(initialValue);
	}
	
	
	public FxDouble()
	{
	}
}
