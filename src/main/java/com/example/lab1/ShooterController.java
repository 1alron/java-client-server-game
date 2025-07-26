package com.example.lab1;

import com.example.lab1.model.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Optional;

public class ShooterController implements IObserver {
    @FXML
    private Button connectButton;
    @FXML
    private Button readyButton;
    @FXML
    private Button shootButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button resumeButton;
    @FXML
    private Button leaderboardButton;

    private boolean isAlertDialogShowing = false;

    @FXML
    private AnchorPane shootersArea;
    @FXML
    private AnchorPane gameArea;
    @FXML
    private VBox infoBoxesArea;

    private Model model = BuildModel.build();

    private PlayerGameInfo playerGameInfo;

    private ClientConnection clientConnection = null;
    private Socket socket = null;

    private final int port = 3124;

    @FXML
    public void initialize() {
        model.addObserver(this);
    }

    @FXML
    protected void onConnectButtonClick() {
        TextInputDialog dialog = new TextInputDialog("Player");
        dialog.setTitle("Enter the name");
        dialog.setHeaderText("Enter the user name to connect the server");
        dialog.setContentText("Name:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String username = result.get();
            try {
                if (socket == null) {
                    socket = new Socket(InetAddress.getLocalHost(), port);
                    clientConnection = new ClientConnection(socket, false);
                }
                playerGameInfo = new PlayerGameInfo(username, 0, 0);
                ClientRequest msg = new ClientRequest(ClientActionType.ADD_PLAYER, playerGameInfo);
                clientConnection.sendClientRequest(msg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        readyButton.setDisable(false);
        pauseButton.setDisable(false);
        resumeButton.setDisable(false);
        shootButton.setDisable(false);
        leaderboardButton.setDisable(false);
    }

    @FXML
    protected void onReadyButtonClick() {
        connectButton.setDisable(true);
        ClientRequest msg = new ClientRequest(ClientActionType.READY, playerGameInfo);
        clientConnection.sendClientRequest(msg);
    }

    @FXML
    protected void onPauseButtonClick() {
        ClientRequest msg = new ClientRequest(ClientActionType.PAUSE, playerGameInfo);
        clientConnection.sendClientRequest(msg);
        shootButton.setDisable(true);
    }

    @FXML
    protected void onResumeButtonClick() {
        ClientRequest msg = new ClientRequest(ClientActionType.RESUME, playerGameInfo);
        clientConnection.sendClientRequest(msg);
        shootButton.setDisable(false);
    }

    @FXML
    protected void onShootButtonClick() {
        ClientRequest msg = new ClientRequest(ClientActionType.SHOOT, playerGameInfo);
        clientConnection.sendClientRequest(msg);
    }

    @FXML
    protected void onLeaderboardButtonClick() {
        ClientRequest msg = new ClientRequest(ClientActionType.GET_LEADERBOARD, playerGameInfo);
        clientConnection.sendClientRequest(msg);
    }

    @Override
    public void refreshUI() {
        if (model.getGameState().getWinner() != null) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Winner!");
                alert.setHeaderText("One of the players scored the required number of points!");
                alert.setContentText("The winner is " + model.getGameState().getWinner());
                showWinnerAlert(alert);
            });
        }

        Platform.runLater(() -> {
            // refresh shooters area
            shootersArea.getChildren().clear();
            for (Shooter shooter : model.getShooters()) {
                shootersArea.getChildren().add(createShooter(shooter.getX(), shooter.getY(), shooter.getColor()));
            }

            // refresh game area
            gameArea.getChildren().clear();
            gameArea.getChildren().add(createSeparator(242.0, -1.0));
            gameArea.getChildren().add(createSeparator(317.0, -1.0));
            for (Arrow arrow : model.getArrows()) {
                gameArea.getChildren().add(createArrow(arrow.getX(), arrow.getY()));
            }
            for (Target target : model.getTargets()) {
                gameArea.getChildren().add(createTarget(target.getX(), target.getY(), target.getR()));
            }

            // refresh info boxes area
            infoBoxesArea.getChildren().clear();
            if (model.getPlayersGameInfo() == null) return;
            for (PlayerGameInfo playerGameInfo : model.getPlayersGameInfo()) {
                if (playerGameInfo == null) continue;
                VBox playerData = new VBox();
                playerData.getChildren().add(
                        createInfoHBox(playerGameInfo.getName() + " score:",
                                String.valueOf(playerGameInfo.getScore()))
                );
                playerData.getChildren().add(
                        createInfoHBox(playerGameInfo.getName() + " shots:",
                                String.valueOf(playerGameInfo.getShots()))
                );
                infoBoxesArea.getChildren().add(playerData);
            }
        });

    }

    private Polygon createShooter(double x, double y, String color) {
        Polygon polygon = new Polygon(-35.0, -44.0, 21.0, -21.0, -35.0, -1.0);
        polygon.setLayoutX(x);
        polygon.setLayoutY(y);
        polygon.setStrokeWidth(1);
        polygon.setStroke(Color.BLACK);
        if (color.equalsIgnoreCase("blue")) {
            polygon.setFill(Color.DODGERBLUE);
        } else if (color.equalsIgnoreCase("green")) {
            polygon.setFill(Color.web("#14b449"));
        } else if (color.equalsIgnoreCase("pink")) {
            polygon.setFill(Color.web("ff1ffa"));
        } else if (color.equalsIgnoreCase("orange")) {
            polygon.setFill(Color.web("ffab1f"));
        }
        return polygon;
    }

    private HBox createInfoHBox(String title, String value) {
        HBox box = new HBox(5.0);
        box.setPadding(new Insets(5.0));
        box.getChildren().addAll(new Label(title), new Label(value));
        return box;
    }

    private Separator createSeparator(double x, double y) {
        Separator sep = new Separator(Orientation.VERTICAL);
        sep.setLayoutX(x);
        sep.setLayoutY(y);
        sep.setPrefHeight(338.0);
        return sep;
    }

    private Group createArrow(double x, double y) {
        Group group = new Group();
        group.setLayoutX(x);
        group.setLayoutY(y);
        group.getChildren().add(createArrowLine(-75.0, 0.0));
        group.getChildren().add(createArrowLine(-50.0, -5.0));
        group.getChildren().add(createArrowLine(-50.0, 5.0));
        return group;
    }

    private Line createArrowLine(double startX, double startY) {
        Line line = new Line();
        line.setStartX(startX);
        line.setEndX(-40.0);
        line.setStartY(startY);
        line.setEndY(0.0);
        return line;
    }

    private Circle createTarget(double x, double y, double r) {
        Circle circle = new Circle();
        circle.setLayoutX(x);
        circle.setLayoutY(y);
        circle.setRadius(r);
        circle.setFill(Color.web("ff1f3c"));
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(1.0);
        return circle;
    }

    private void showWinnerAlert(Alert alert) {
        if (!isAlertDialogShowing) {
            isAlertDialogShowing = true;
            alert.setOnHidden(dialogEvent -> isAlertDialogShowing = false);
            alert.showAndWait();
        }
    }
}