
package com.enosi.docktailor.dock;
import com.enosi.docktailor.common.util.ASettingsStore;
import com.enosi.docktailor.docktailor.fxdock.FxDockPane;
import com.enosi.docktailor.docktailor.fxdock.FxDockSchema;
import javafx.stage.Stage;

/**
 * Demo Schema creates custom dock windows and dock panes.
 */
public class DemoDockSchema extends FxDockSchema
{
	public static final String BROWSER = "BROWSER";

	public DemoDockSchema(ASettingsStore store)
	{
		super(store);
	}

	/** creates custom pane using the type id */
	@Override
	public FxDockPane createPane(String id) {
		// Check si il s'agit d'une fenetre de base de l'application.
		if (id.equals(BROWSER)) {
			return new DemoBrowser();
		}

		// Check si il s'agit d'une fenetre Dockable.
		for (IControllerDockablePane<?> newInstance : ServiceDockableTab.getInstance().getNewInstances()) {
			if (id.equals(newInstance.getTabName())) {
				return newInstance.createDockPane();
			}
		}

		throw new IllegalArgumentException("Le fichier de configuration pour docktailor est corrompue");
	}
	

	@Override
	public Stage createWindow(String name)
	{
		return new DemoWindow();
	}

	@Override
	public Stage createDefaultWindow()
	{
		return new DemoWindow();
	}
}
