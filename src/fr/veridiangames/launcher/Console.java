package fr.veridiangames.launcher;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;

public class Console extends Stage implements Runnable
{
    private String port;
    private Process process;

    private Text area;
    private TextField field;
    private ScrollPane scrollPane;

    private Thread thread;

    private BufferedWriter writer;
    private BufferedReader reader;

    public Console(String port)
    {
        this.port = port;

        try
        {
            this.process = Runtime.getRuntime().exec("java -cp ubercube.jar fr.veridiangames.server.ServerMain " +
                    port,null, new File("game/" + OsChecker.getOsName()));
            this.writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            this.reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        VBox root = new VBox();
        root.setAlignment(Pos.BOTTOM_CENTER);

        this.setScene(new Scene(root, 600, 400));
        this.setTitle("Ubercube server");

        this.area = new Text();

        this.scrollPane = new ScrollPane();
        this.scrollPane.setFitToHeight(true);
        this.scrollPane.setContent(this.area);
        this.scrollPane.setStyle(
                "-fx-border-width: 0;" +
                "-fx-border-image-width: 0;" +
                "-fx-background-image: null;" +
                "-fx-region-background: null;" +
                "-fx-border-insets: 0;" +
                "-fx-background-size:0;" +
                "-fx-border-image-insets:0;");

        this.field = new TextField();
        this.field.setAlignment(Pos.BOTTOM_LEFT);

        root.getChildren().add(this.scrollPane);
        root.getChildren().add(this.field);

        this.field.setOnAction(event -> {
            try
            {
                String command = this.field.getText();

                this.area.setText(this.area.getText() + "\n" + command);

                if(this.writer != null)
                {
                    this.writer.write(command + "\n");
                    this.writer.flush();
                }

                this.field.setText("");

                if(command.equalsIgnoreCase("stop"))
                {
                    this.thread.join();
                    this.close();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });

        this.thread = new Thread(this);
        this.thread.start();
    }

    public Process getProcess()
    {
        return process;
    }

    @Override
    public void run()
    {
        while (process.isAlive())
            try
            {
                String line = this.reader.readLine();
                this.area.setText(this.area.getText() + "\n" + line);
                this.scrollPane.setVvalue(1.0d);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
    }
}
