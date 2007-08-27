package edu.berkeley.biogeomancer.webservice.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.berkeley.biogeomancer.webservice.client.services.controller.Controller;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Client implements EntryPoint {

	private final HTML batchResponseHtml = new HTML();

	/*
	 * private final FormPanel form = new FormPanel();
	 * 
	 * private final FormHandler formHandler = new FormHandler() { public void
	 * onSubmit(FormSubmitEvent event) { // Window.alert("Error " +
	 * event.toString()); // event.setCancelled(true); // return; }
	 * 
	 * public void onSubmitComplete(FormSubmitCompleteEvent event) { String
	 * response = event.getResults(); batchResponseHtml.setHTML(response); //
	 * form.removeFormHandler(this); } };
	 */

	private VerticalPanel vpBatch;

	private HorizontalPanel vpSingle;

	private void initializeBoxes() {

		vpSingle = new HorizontalPanel();
		final ListBox georefListBox = new ListBox();
		georefListBox.addItem("Interpreter");
		georefListBox.addItem("HigherGeography");
		georefListBox.addItem("Country");
		georefListBox.addItem("Locality");
		georefListBox.addItem("Stateprovince");
		georefListBox.addItem("County");
		georefListBox.addItem("Verbatimlatitude");
		georefListBox.addItem("Verbatimlongitude");
		georefListBox.addItem("Island");
		georefListBox.addItem("IslandGroup");
		georefListBox.addItem("Waterbody");
		georefListBox.addItem("Continent");

		vpSingle.setSpacing(8);
		vpSingle.add(georefListBox);

		Button addField = new Button("Add Field");
		final VerticalPanel singleGeorefField = new VerticalPanel();
		singleGeorefField.setSpacing(8);
		Button submit = new Button("Georeference");
		addField.addClickListener(new ClickListener() {

			public void onClick(Widget sender) {
				ListBox fieldBox = (ListBox) vpSingle.getWidget(0);
				int selectedIndex = fieldBox.getSelectedIndex();
				String addedFieldName = fieldBox.getItemText(selectedIndex);
				Label addedFieldLabel = new Label(addedFieldName);
				TextBox addedFieldText = new TextBox();
				singleGeorefField.add(addedFieldLabel);
				singleGeorefField.add(addedFieldText);
			}

		});
		// singleGeorefField.iterator();
		// there is an error here
		submit.addClickListener(new ClickListener() {

			public void onClick(Widget sender) {
				Controller.getInstance().singleGeoreference(singleGeorefField,
						new AsyncCallback() {
							public void onFailure(Throwable caught) {
								Window.alert(caught.getMessage());
							}

							public void onSuccess(Object result) {
								if (result != null) {
									HTML htmlResponse = new HTML();
									String data = (String) result;
									htmlResponse.setHTML(data);
									vpSingle.add(htmlResponse);
								}
							}
						});

			}

		});
		vpSingle.add(addField);
		vpSingle.add(submit);
		RootPanel.get("slot1").add(vpSingle);
		RootPanel.get("slot1").add(singleGeorefField);
	}

	/**
	 * This is the entry point method. cal singleGeoref which just do single
	 * georeference base on the fields enter form user
	 */
	public void onModuleLoad() {
		singleGeoref();
		batchGeoref();
	}

	/**
	 * make 3 textBoxes: Locality, HigherGeography, and Interpreter make
	 * Georeference button for submit. Output single georeference in HTML format
	 * base input from the 3 textBoxes
	 */
	private void singleGeoref() {
		initializeBoxes();
		/*
		 * final VerticalPanel vp = new VerticalPanel(); final Label lLabel =
		 * new Label("Locality:"); final TextBox lBox = new TextBox(); final
		 * Label hgLabel = new Label("HigherGeography:"); final TextBox hgBox =
		 * new TextBox(); final Label iLabel = new Label("Interpreter:"); final
		 * TextBox iBox = new TextBox();
		 * 
		 * vp.setSpacing(8); vp.add(lLabel); vp.add(lBox); vp.add(hgLabel);
		 * vp.add(hgBox); vp.add(iLabel); vp.add(iBox); Button submit = new
		 * Button("Georeference"); submit.addClickListener(new ClickListener() {
		 * public void onClick(Widget sender) {
		 * Controller.getInstance().georeference(lBox.getText(),
		 * hgBox.getText(), iBox.getText(), new AsyncCallback() { public void
		 * onFailure(Throwable caught) { Window.alert(caught.getMessage()); }
		 * 
		 * public void onSuccess(Object result) { if (result != null) { HTML
		 * htmlResponse = new HTML(); String data = (String) result;
		 * htmlResponse.setHTML(data); vp.add(htmlResponse); } } }); } });
		 * vp.add(submit); RootPanel.get("slot1").add(vp);
		 */
	}

	/**
	 * make XML Georeferences request textBox to for XML request input make
	 * Batch georeference submit button to submit XML request
	 */
	private void batchGeoref() {
		// initForm();
		vpBatch = new VerticalPanel();
		final Label taLabel = new Label("XML Georeferences request:");
		final TextArea ta = new TextArea();
		ta.setName("xmlRequest");
		ta.setCharacterWidth(60);
		ta.setVisibleLines(25);
		Button submitBatch = new Button("Batch georeference");
		vpBatch.setSpacing(8);
		vpBatch.add(taLabel);
		vpBatch.add(ta);
		vpBatch.add(submitBatch);
		vpBatch.add(batchResponseHtml);
		submitBatch.addClickListener(new ClickListener() {

			public void onClick(Widget sender) {
				String xmlRequest = ta.getText();
				Controller.getInstance().batchGeoreference(xmlRequest,
						new AsyncCallback() {
							public void onFailure(Throwable caught) {
								Window.alert(caught.getMessage());
							}

							public void onSuccess(Object result) {
								if (result != null) {
									HTML htmlResponse = new HTML();
									String data = (String) result;
									htmlResponse.setHTML(data);
									vpBatch.add(htmlResponse);

								}
							}
						});
			}
		});
		RootPanel.get("slot2").add(vpBatch);
	}
}
