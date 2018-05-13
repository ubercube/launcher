package fr.veridiangames.launcher;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

public class Main extends Application {

    public final static String IP_SERVER = "51.15.20.92";
    public final static String PORT_SERVER = "4242";
    public final static String URL_SERVER = "http://" + IP_SERVER;
    public final static String URL_SERVER_UBERCUBE = URL_SERVER + "/ubercube";
    public final static String URL_NIGTHLY = URL_SERVER_UBERCUBE + "/build-nigthly";
    public final static String URL_NIGTHLY_VERSION = URL_SERVER_UBERCUBE + "/ubercube-dev/version.udf";
    public final static String URL_CHANGELOG = URL_SERVER_UBERCUBE + "/changelog.html";

    private String os;
    private int osId;

    private String macosFlags = "";
    private Stage primaryStage;
    private Console console;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        this.setup();

        this.primaryStage = primaryStage;

        primaryStage.getIcons().add(new Image(new FileInputStream(new File("icon.png"))));

        StackPane root = new StackPane();
        primaryStage.setTitle("Ubercube Launcher");
        primaryStage.setScene(new Scene(root, 800, 600));

        BorderPane border = new BorderPane();
        border.setBottom(getMenu());
        border.setCenter(getWeb());

        root.getChildren().add(border);

        primaryStage.show();
    }

    private void setup ()
    {
        this.osId = OsChecker.getOsId();
        this.os = OsChecker.getOsName();

        this.macosFlags = (this.osId == OsChecker.MACOS) ? "-XstartOnFirstThread " : "";

        String serverVersion;
        String localVersion;

        try
        {
            /* GET SERVER VERSION */
            URL url = new URL(URL_NIGTHLY_VERSION);
            URLConnection yc = url.openConnection();

            BufferedReader body = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            serverVersion = body.readLine();
            body.close();

            /* GET LOCAL VERSION */
            File localVersionFile = new File("version.udf");
            if(localVersionFile.exists())
            {
                body = new BufferedReader(new FileReader(localVersionFile));
                localVersion = body.readLine();
                body.close();
            }
            else
            {
                localVersionFile.createNewFile();
                PrintWriter p = new PrintWriter(new FileWriter(localVersionFile));
                p.println(serverVersion);
                p.close();
                localVersion = "0";
            }

            System.out.println(localVersion + " " + serverVersion);

            /* Compare Version */
            if(!serverVersion.equals(localVersion))
            {
                String fn = new File("").getAbsolutePath() + "/ubercube-" + os + ".zip";

                Utils.download(URL_NIGTHLY + "/ubercube-" + os + ".zip", fn);
                Utils.unzip( fn, new File("").getAbsolutePath());
                new File(new File("").getAbsolutePath() + "/ubercube-" + os + ".zip").delete();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private HBox getMenu()
    {
        HBox menu = new HBox();
        menu.setAlignment(Pos.CENTER);
        menu.setSpacing(20);
        menu.setPadding(new Insets( 5, 20, 5, 20));

        Insets padding = new Insets( 10, 30, 10, 30);

        Button publicButton = new Button("Public Server");
        publicButton.setPadding(padding);
        publicButton.setOnAction(event -> {
            Stage modal = createPublicConnectionModal(((Node) event.getSource()).getScene().getWindow());
            modal.show();
        });
        menu.getChildren().add(publicButton);

        Button clientButton = new Button("Launch Client");
        clientButton.setPadding(padding);
        clientButton.setOnAction(event -> {
            Stage modal = createClientModal(((Node) event.getSource()).getScene().getWindow());
            modal.show();
        });
        menu.getChildren().add(clientButton);

        Button serverButton = new Button("Launch Server");
        serverButton.setPadding(padding);
        serverButton.setOnAction(event -> {
            Stage modal = createServerModal(((Node) event.getSource()).getScene().getWindow());
            modal.show();
        });
        menu.getChildren().add(serverButton);

        return menu;
    }

    private WebView getWeb()
    {
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        webEngine.load(URL_CHANGELOG);

        return webView;
    }

    private Stage createClientModal(Window window)
    {
        Stage modal = new Stage();
        GridPane rootModal = new GridPane();
        rootModal.setPadding(new Insets(10));
        rootModal.setVgap(5);
        rootModal.setHgap(5);

        modal.setScene(new Scene(rootModal, 300, 200));
        modal.setTitle("Configuration :");
        modal.initModality(Modality.WINDOW_MODAL);
        modal.initOwner(window);
        modal.setX(window.getX() + window.getWidth() / 2 - 150);
        modal.setY(window.getY() + window.getHeight() / 2 - 100);

        Label usernameLabel = new Label("Username :");
        TextField usernameField = new TextField();
        usernameField.setText("Random_" + new Random().nextInt(999));

        Label hostLabel = new Label("Server IP :");
        TextField hostField = new TextField();
        hostField.setText("localhost:4242");

        Button btn = new Button("Launch");
        btn.setOnAction(event -> {
            Runtime runtime = Runtime.getRuntime();
            try {
                String cmd = "java " + this.macosFlags + "-cp ubercube.jar fr.veridiangames.client.MainComponent " + hostField.getText() + " " + usernameField.getText();

                System.out.println("Exec : " + cmd);

                runtime.exec(cmd,null, new File(new File("").getAbsolutePath()));
                this.stop();
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        GridPane.setConstraints(usernameLabel, 0, 0);
        rootModal.getChildren().add(usernameLabel);
        GridPane.setConstraints(usernameField, 1, 0);
        rootModal.getChildren().add(usernameField);

        GridPane.setConstraints(hostLabel, 0, 1);
        rootModal.getChildren().add(hostLabel);
        GridPane.setConstraints(hostField, 1, 1);
        rootModal.getChildren().add(hostField);

        GridPane.setConstraints(btn, 0, 2);
        rootModal.getChildren().add(btn);

        return modal;
    }

    private Stage createPublicConnectionModal(Window window)
    {
        Stage modal = new Stage();
        GridPane rootModal = new GridPane();
        rootModal.setPadding(new Insets(10));
        rootModal.setVgap(5);
        rootModal.setHgap(5);

        modal.setScene(new Scene(rootModal, 300, 200));
        modal.setTitle("Configuration :");
        modal.initModality(Modality.WINDOW_MODAL);
        modal.initOwner(window);
        modal.setX(window.getX() + window.getWidth() / 2 - 150);
        modal.setY(window.getY() + window.getHeight() / 2 - 100);

        Label usernameLabel = new Label("Username :");
        TextField usernameField = new TextField();
        usernameField.setText("Random_" + new Random().nextInt(999));

        Button btn = new Button("Launch");
        btn.setOnAction(event -> {
            Runtime runtime = Runtime.getRuntime();
            try {
                String cmd = "java " + this.macosFlags + "-cp ubercube.jar fr.veridiangames.client.MainComponent " + IP_SERVER + ":" + PORT_SERVER + " " + usernameField.getText();
                System.out.println("Exec : " + cmd);
                runtime.exec(cmd,null, new File(new File("").getAbsolutePath()));
                this.stop();
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        GridPane.setConstraints(usernameLabel, 0, 0);
        rootModal.getChildren().add(usernameLabel);
        GridPane.setConstraints(usernameField, 1, 0);
        rootModal.getChildren().add(usernameField);

        GridPane.setConstraints(btn, 0, 2);
        rootModal.getChildren().add(btn);

        return modal;
    }

    private Stage createServerModal(Window window)
    {
        Stage modal = new Stage();
        GridPane rootModal = new GridPane();
        rootModal.setPadding(new Insets(10));
        rootModal.setVgap(5);
        rootModal.setHgap(5);

        modal.setScene(new Scene(rootModal, 300, 200));
        modal.setTitle("Configuration :");
        modal.initModality(Modality.WINDOW_MODAL);
        modal.initOwner(window);
        modal.setX(window.getX() + window.getWidth() / 2 - 150);
        modal.setY(window.getY() + window.getHeight() / 2 - 100);

        Label portLabel = new Label("Port :");
        TextField portField = new TextField();
        portField.setText("4242");

        Button btn = new Button("Launch");
        btn.setOnAction(event -> {
            try {

                this.console = new Console(portField.getText());

                this.primaryStage.close();
                modal.close();
                console.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        GridPane.setConstraints(portLabel, 0, 0);
        rootModal.getChildren().add(portLabel);
        GridPane.setConstraints(portField, 1, 0);
        rootModal.getChildren().add(portField);

        GridPane.setConstraints(btn, 0, 1);
        rootModal.getChildren().add(btn);

        return modal;
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
