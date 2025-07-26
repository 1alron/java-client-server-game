package com.example.lab1;

import com.example.lab1.database.HibernateActions;
import com.example.lab1.entities.Player;
import com.example.lab1.model.*;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientConnection {
    private Model model = BuildModel.build();
    boolean isServer = false;
    Gson gson = new Gson();

    private Socket socket;

    private InputStream inputStream;
    private OutputStream outputStream;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public ClientConnection(Socket socket, boolean isServer) {
        this.socket = socket;
        this.isServer = isServer;
        try {
            outputStream = socket.getOutputStream();
            dataOutputStream = new DataOutputStream(outputStream);
            new Thread(this::run).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        try {
            inputStream = socket.getInputStream();
            dataInputStream = new DataInputStream(inputStream);

            while (true) {
                if (isServer) {
                    ClientRequest msg = readClientRequest();
                    if (msg.getType() == ClientActionType.ADD_PLAYER) {
                        if (model.getPlayersGameInfo().size() < GameUtil.MAX_NUMBER_OF_PLAYERS) {
                            if (isPlayerNameExists(msg)) {
                                sendServerResponse(new ServerResponse(ServerActionType.REJECT_ADD_PLAYER_NAME_ALREADY_EXISTS, model.getGameState()));
                            } else {
                                model.addPlayerGameInfo(msg.getPlayerGameInfo());
                                if (model.getPlayersGameInfo().size() == 1) {
                                    model.addShooter(new Shooter(GameUtil.SHOOTER_START_Y_POSITION, "blue"));
                                    model.addArrow(new Arrow(GameUtil.ARROW_START_Y_POSITION, msg.getPlayerGameInfo().getName()));
                                } else if (model.getPlayersGameInfo().size() == 2) {
                                    model.addShooter(new Shooter(GameUtil.SHOOTER_START_Y_POSITION +
                                            GameUtil.GEN_Y_OFFSET, "green"));
                                    model.addArrow(new Arrow(GameUtil.ARROW_START_Y_POSITION + GameUtil.GEN_Y_OFFSET, msg.getPlayerGameInfo().getName()));
                                } else if (model.getPlayersGameInfo().size() == 3) {
                                    model.addShooter(new Shooter(GameUtil.SHOOTER_START_Y_POSITION +
                                            GameUtil.GEN_Y_OFFSET * 2, "pink"));
                                    model.addArrow(new Arrow(GameUtil.ARROW_START_Y_POSITION + GameUtil.GEN_Y_OFFSET * 2, msg.getPlayerGameInfo().getName()));
                                } else if (model.getPlayersGameInfo().size() == 4) {
                                    model.addShooter(new Shooter(GameUtil.SHOOTER_START_Y_POSITION +
                                            GameUtil.GEN_Y_OFFSET * 3, "orange"));
                                    model.addArrow(new Arrow(GameUtil.ARROW_START_Y_POSITION + GameUtil.GEN_Y_OFFSET * 3, msg.getPlayerGameInfo().getName()));
                                }
                            }
                        }
                    } else if (msg.getType() == ClientActionType.READY) {
                        for (PlayerGameInfo player : model.getPlayersGameInfo()) {
                            if (player.getName().equals(msg.getPlayerGameInfo().getName())) {
                                player.setReady(true);
                            }
                        }
                        if (everyoneIsReady() && model.getGameThread() == null) {
                            setStartGameData();
                            HibernateActions.addNotExistingUsersToDB(model.getPlayersGameInfo());
                            model.startGame();
                        }
                    } else if (msg.getType() == ClientActionType.PAUSE) {
                        model.setPaused(true);
                    } else if (msg.getType() == ClientActionType.RESUME) {
                        model.resume();
                    } else if (msg.getType() == ClientActionType.SHOOT) {
                        model.startShooting(msg.getPlayerGameInfo().getName());
                    } else if (msg.getType() == ClientActionType.GET_LEADERBOARD) {
                        List<Player> players = HibernateActions.getPlayers();
                        sendServerResponse(new ServerResponse(
                                ServerActionType.PROVIDE_LEADERBOARD, model.getGameState(), players));
                    }
                } else {
                    ServerResponse msg = readServerResponse();
                    if (msg.getType() == ServerActionType.REFRESH_UI) {
                        model.setGameState(msg.getGameState());
                    } else if (msg.getType() == ServerActionType.REJECT_ADD_PLAYER_AREA_IS_FULL) {
                        socket = null;
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error!");
                            alert.setHeaderText("Maximum number of players exceeded!");
                            alert.setContentText("The maximum number of players for the game is 4.");
                            alert.showAndWait();
                            Platform.exit();
                        });
                    } else if (msg.getType() == ServerActionType.REJECT_ADD_PLAYER_NAME_ALREADY_EXISTS) {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error!");
                            alert.setHeaderText("Adding a player with an existing name!");
                            alert.setContentText("A player with this name already exists.");
                            alert.showAndWait();
                        });
                    } else if (msg.getType() == ServerActionType.PROVIDE_LEADERBOARD) {
                        Platform.runLater(() -> {
                            Stage stage = new Stage();
                            List<Player> players = msg.getPlayers();
                            TableView<Player> tableView = new TableView<>();

                            TableColumn<Player, String> nameCol = new TableColumn<>("Name");
                            nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

                            TableColumn<Player, Integer> winsCol = new TableColumn<>("Wins");
                            winsCol.setCellValueFactory(new PropertyValueFactory<>("wins"));

                            tableView.getColumns().addAll(nameCol, winsCol);
                            ObservableList<Player> playersObservable = FXCollections.observableArrayList(players);
                            tableView.setItems(playersObservable);

                            VBox vbox = new VBox(tableView);
                            Scene scene = new Scene(vbox);
                            stage.setScene(scene);
                            stage.setTitle("Leaderboard");
                            stage.show();
                        });
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendClientRequest(ClientRequest msg) {
        String str = gson.toJson(msg);
        try {
            dataOutputStream.writeUTF(str);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ClientRequest readClientRequest() {
        try {
            String str = dataInputStream.readUTF();
            return gson.fromJson(str, ClientRequest.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendServerResponse(ServerResponse msg) {
        String str = gson.toJson(msg);
        try {
            dataOutputStream.writeUTF(str);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ServerResponse readServerResponse() {
        try {
            String str = dataInputStream.readUTF();
            return gson.fromJson(str, ServerResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isPlayerNameExists(ClientRequest msg) {
        for (PlayerGameInfo player : model.getPlayersGameInfo()) {
            if (msg.getPlayerGameInfo().getName().equals(player.getName())) {
                return true;
            }
        }
        return false;
    }

    private boolean everyoneIsReady() {
        boolean flag = true;
        for (PlayerGameInfo player : model.getPlayersGameInfo()) {
            if (!player.isReady()) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    private void setStartGameData() {
        GameState gameState = model.getGameState();

        for (PlayerGameInfo player : model.getPlayersGameInfo()) {
            player.setShots(0);
            player.setScore(0);
        }
        gameState.setWinner(null);
        model.setGameState(gameState);
    }
}