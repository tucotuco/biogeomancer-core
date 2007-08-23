package edu.berkeley.biogeomancer.webservice.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
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

  private final FormPanel form = new FormPanel();

  private final FormHandler formHandler = new FormHandler() {
    public void onSubmit(FormSubmitEvent event) {
      // Window.alert("Error " + event.toString());
      // event.setCancelled(true);
      // return;
    }

    public void onSubmitComplete(FormSubmitCompleteEvent event) {
      String response = event.getResults();
      batchResponseHtml.setHTML(response);
      // form.removeFormHandler(this);
    }
  };

  private VerticalPanel vpBatch;

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    VerticalPanel vp = new VerticalPanel();
    final Label lLabel = new Label("Locality:");
    final TextBox lBox = new TextBox();
    final Label hgLabel = new Label("HigherGeography:");
    final TextBox hgBox = new TextBox();
    final Label iLabel = new Label("Interpreter:");
    final TextBox iBox = new TextBox();
    final HTML htmlResponse = new HTML();
    vp.setSpacing(8);
    vp.add(lLabel);
    vp.add(lBox);
    vp.add(hgLabel);
    vp.add(hgBox);
    vp.add(iLabel);
    vp.add(iBox);
    Button submit = new Button("Georeference");
    submit.addClickListener(new ClickListener() {
      public void onClick(Widget sender) {
        Controller.getInstance().georeference(lBox.getText(), hgBox.getText(),
            iBox.getText(), new AsyncCallback() {
              public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
              }

              public void onSuccess(Object result) {
                if (result != null) {
                  String data = (String) result;
                  htmlResponse.setHTML(data);
                }
              }
            });
      }
    });
    vp.add(submit);
    vp.add(htmlResponse);

    initForm();
    vpBatch = new VerticalPanel();
    TextArea ta = new TextArea();
    ta.setName("xmlRequest");
    ta.setCharacterWidth(80);
    ta.setVisibleLines(50);
    Button submitBatch = new Button("Batch georeference");
    vpBatch.setSpacing(8);
    vpBatch.add(ta);
    vpBatch.add(submitBatch);
    vpBatch.add(batchResponseHtml);
    submitBatch.addClickListener(new ClickListener() {

      public void onClick(Widget sender) {
        form.submit();
      }

    });

    RootPanel.get("slot1").add(vp);
    RootPanel.get("slot2").add(vpBatch);
  }

  private void initForm() {
    form.setMethod(FormPanel.METHOD_POST);
    form.setAction(GWT.getModuleBaseURL() + "ws/batch");
    form.setWidget(vpBatch);
    form.addFormHandler(formHandler);
  }
}
