// Copyright © 2018-2025 Andy Goryachev <andy@goryachev.com>
package org.esioc.docktailor.fx;
import javafx.beans.property.SimpleObjectProperty;


/**
 * Alias for ReadOnlyObjectWrapper.
 */
public class FxObject<T>
	extends SimpleObjectProperty<T>
{
	public FxObject(T initialValue)
	{
		super(initialValue);
	}
	
	
	public FxObject()
	{
	}
}
