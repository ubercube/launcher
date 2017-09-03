package fr.veridiangames.launcher;

import javafx.application.Platform;
import javafx.event.EventHandler;
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
import javafx.stage.WindowEvent;

import java.awt.*;
import java.io.*;

public class Console extends Stage implements Runnable
{
    public static final int MAX_CONSOLE_SIZE = 500;

    private String port;
    private Process process;

    private Text area;
    private TextField field;
    private ScrollPane scrollPane;

    private Thread thread;

    private BufferedWriter writer;
    private BufferedReader reader;

    private boolean processAlive;

    public Console(String port)
    {
        this.port = port;

        try
        {
            this.process = Runtime.getRuntime().exec("java -cp ubercube.jar fr.veridiangames.server.ServerMain " +
                    port,null, new File(Main.GAME_FOLDER + OsChecker.getOsName()));

            this.processAlive = true;

            this.writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            this.reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        }
        catch (IOException e)
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
                String text = this.area.getText();

                System.out.println("> " + command);

                if(this.writer != null)
                {
                    this.writer.write(command + "\n");
                    this.writer.flush();
                }

                this.field.setText("");

                if(command.equalsIgnoreCase("stop"))
                    this.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });

        System.setOut(new PrintStream(new GraphicsOutputStream(this.area, this.scrollPane)));

        this.thread = new Thread(this);
        this.thread.start();

        setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                close();
            }
        });
    }

    @Override
    public void close()
    {
        super.close();
        if(this.process != null)
            this.process.destroy();
        this.processAlive = false;
    }

    @Override
    public void run()
    {
        while (processAlive)
            try
            {
                if(this.reader != null)
                    System.out.println(this.reader.readLine());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
    }

    public Process getProcess()
    {
        return process;
    }

    public Thread getThread()
    {
        return thread;
    }
}
