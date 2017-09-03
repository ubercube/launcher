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

    public final static String DEFAULT_URL = "http://91.134.107.165";
    public final static String GAME_FOLDER = "game/";

    private String jarPath;
    private String os;

    private Console console;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        this.setup();

        primaryStage.getIcons().add(new Image(new FileInputStream(new File("icon.png"))));

        StackPane root = new StackPane();
        primaryStage.setTitle("Ubercube Pre Alpha 1.1");
        primaryStage.setScene(new Scene(root, 800, 600));

        BorderPane border = new BorderPane();
        border.setBottom(getMenu());
        border.setCenter(getWeb());

        root.getChildren().add(border);

        primaryStage.show();
    }

    private void setup ()
    {
        os = OsChecker.getOsName();

        String review = "";
        String currentReview = "";
        String dlUrl = "";
        String gamePath = "";

        try
        {
            /* Check URL */
            URL url = new URL(DEFAULT_URL + "/info.udf");
            URLConnection yc = url.openConnection();

            BufferedReader body = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            review = body.readLine();
            String inputLine = body.readLine();
            dlUrl = inputLine.split(" ")[0].replace("{os}", os);
            gamePath = inputLine.split(" ")[1].replace("{os}", os);
            body.close();

            /* Check Version */
            File versionFile = new File(GAME_FOLDER + os + "/.version");
            if(versionFile.exists())
            {
                BufferedReader versionReader = new BufferedReader(new FileReader(versionFile));
                currentReview = versionReader.readLine();
                versionReader.close();
            }

            if(!review.equalsIgnoreCase(currentReview))
            {
                Utils.download(DEFAULT_URL + "/" + dlUrl, dlUrl);

                File gameDir = new File(GAME_FOLDER);

                if (gameDir.exists())
                    Utils.deleteFolder(gameDir);
                gameDir.mkdir();

                Utils.unzip(dlUrl, dlUrl, GAME_FOLDER);
            }

            jarPath = GAME_FOLDER + gamePath;
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
        webEngine.load("https://marccspro.itch.io/ubercube");

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
                runtime.exec("java -cp ubercube.jar fr.veridiangames.client.MainComponent " +
                                hostField.getText() + " " + usernameField.getText(),null, new File(GAME_FOLDER + os));
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
                String term = "";

                this.console = new Console(portField.getText());

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

    @Override
    public void stop() throws Exception
    {
        super.stop();
        if(console != null && console.getProcess() != null && console.getProcess().isAlive())
            console.getProcess().destroy();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
