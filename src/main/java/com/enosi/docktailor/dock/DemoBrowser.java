
package com.enosi.docktailor.dock;

import com.enosi.docktailor.common.log.Log;
import com.enosi.docktailor.docktailor.fx.FxAction;
import com.enosi.docktailor.docktailor.fxdock.FxDockPane;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.web.WebErrorEvent;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;

/**
 * Demo Browser.
 */
public class DemoBrowser
	extends FxDockPane
{
	protected static final Log log = Log.get("DemoBrowser");
	public final FxAction reloadAction = new FxAction(this::reload);
	public final TextField addressField;
	public final WebView view;
	public final Label statusField;
	
	
	public DemoBrowser()
	{
		super(DemoDockSchema.BROWSER);
		setTitle("Browser / " + CSystem.getJavaVersion());
		
		addressField = new TextField();
		addressField.setOnAction((ev) -> 
		{
			String url = addressField.getText();
			setUrl(url);
		});
		LocalSettings.get(this).add("URL", addressField.textProperty());
		
		view = new WebView();
		view.getEngine().setOnError((ev) -> handleError(ev));
		view.getEngine().setOnStatusChanged((ev) -> handleStatusChange(ev));
		Worker<Void> w = view.getEngine().getLoadWorker();
		w.stateProperty().addListener(new ChangeListener<Worker.State>()
		{
			@Override
			public void changed(ObservableValue v, Worker.State old, Worker.State cur)
			{
				log.debug(cur);
				
				if(w.getException() != null && cur == State.FAILED)
				{
					log.error(w.getException());
				}
			}
		});

		statusField = new Label();
		
		CPane p = new CPane();
		p.setGaps(10, 5);
		p.setCenter(view);
		p.setBottom(statusField);
		setContent(p);
		
		FX.later(() -> reload());
	}
	
	
	@Override
	public Node createToolBar(boolean tabMode)
	{
		HPane t = new HPane(5);
		t.setMaxWidth(Double.MAX_VALUE);
		t.setPadding(new Insets(2, 2, 2, 2));

		if(!tabMode)
		{
			Button b = new Button("x");
			FxDockStyles.TOOLBAR_CLOSE_BUTTON.set(b);
			closeAction.attach(b);

			t.add(titleField);
			t.add(b);
		}
		t.fill(addressField);
		t.add(new FxButton("Reload", reloadAction));
		return t;
	}
	
	
	public void reload()
	{
		String url = getUrl();
		if(CKit.isNotBlank(url))
		{
			setUrl(url);
		}
	}
	

	protected void handleStatusChange(WebEvent<String> ev)
	{
		statusField.setText(ev.getData());
	}


	protected void handleError(WebErrorEvent ev)
	{
		log.error(ev);
	}


	public void setUrl(String url)
	{
		log.info(url);
		
		addressField.setText(url);
		view.getEngine().load(url);
	}
	
	
	public String getUrl()
	{
		return addressField.getText();
	}
}
