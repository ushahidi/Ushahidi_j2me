package com.ushahidi.j2me.forms;

import com.sun.lwuit.Button;
import com.sun.lwuit.ComboBox;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Form;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.list.DefaultListModel;
import java.io.IOException;

/**
 * Base Form
 * @author dalezak
 */
public abstract class Base extends Form {

    
    public Base(String title) {
        super(title);
        removeAll();
    }

    protected Button createButton(String text, ActionListener listener) {
        Button button = new Button(text);
        button.setAlignment(Component.CENTER);
        button.addActionListener(listener);
        return button;
    }

    protected Image createImage(String path) {
        try {
            return Image.createImage(path);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    protected Label createLabel(String text) {
        return createLabel(text, Component.CENTER);
    }

    protected Label createLabel(String text, int alignment) {
        Label label = new Label(text);
        label.setAlignment(alignment);
        return label;
    }

    protected Label createEmptyLabel() {
        return createLabel("    ");
    }

    protected Label createImageLabel(String path) {
        Image image = createImage(path);
        Label label = new Label(image);
        label.setAlignment(Component.CENTER);
        return label;
    }

    protected Label createImageLabel(Image image) {
        Label label = new Label(image);
        label.setAlignment(Component.CENTER);
        return label;
    }

    protected ComboBox createComboBox() {
        ComboBox comboBox = new ComboBox();

        return comboBox;
    }

    protected ComboBox createComboBox(String[] items) {
        ComboBox comboBox = new ComboBox(items);
        return comboBox;
    }

    protected TextField createTextField() {
        return new TextField();
    }

    protected TextField createTextField(String text) {
        return new TextField(text);
    }

    protected TextField createTextField(String text, int constraint) {
        TextField textField = new TextField(text);
        textField.setConstraint(constraint);
        return textField;
    }
    
    protected TextField createTextField(String text, boolean editable) {
        TextField textField = new TextField(text);
        textField.setEditable(editable);
        return textField;
    }

    protected TextArea createTextArea(String text) {
        return createTextArea(text, false);
    }

    protected TextArea createTextArea(String text, boolean editable) {
        TextArea textArea = new TextArea(text);
        textArea.setEditable(editable);
        return textArea;
    }

    protected Container createdBoxLayout() {
        return new Container(new BoxLayout(BoxLayout.Y_AXIS));
    }

    protected DefaultListModel createModel(String[] items) {
        return new DefaultListModel(items);
    }

    protected List createList(DefaultListModel model) {
        return new List(model);
    }

    protected List createList(String[] items) {
        DefaultListModel model = new DefaultListModel(items);
        return new List(model);
    }
    
}
