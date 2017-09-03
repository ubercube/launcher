package fr.veridiangames.launcher;

import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.io.OutputStream;

public class GraphicsOutputStream extends OutputStream
{
    private Text text;
    private ScrollPane scrollPane;

    public GraphicsOutputStream(Text text, ScrollPane scrollPane)
    {
        this.text = text;
        this.scrollPane = scrollPane;
    }

    @Override
    public void write(int b) throws IOException
    {
        Platform.runLater(() -> {
            this.text.setText(this.text.getText() + String.valueOf((char)b));
            this.scrollPane.setVvalue(1.0d);
        });
    }
}
