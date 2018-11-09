/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;


import battleship.data.Cell;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.KeyEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.animation.Timeline;

import battleship.data.Coord;
import battleship.data.Player;
import battleship.data.Ship;
import battleship.data.FireResult;

import battleship.exceptions.*;
import battleship.gui.BShipButton;
import java.util.Optional;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import static javafx.scene.input.KeyCode.R;
import javafx.util.Duration;


/**
Programmer: Steven Siddall
 @author Steven Siddall

 This is the main driver class for the Battleship game with GUI.

 Certain GUI elements are private instance variables of the whole class in order to avoid enormous parameter lists.


 */

public class BattleshipGUI extends Application
{
    public static final int MODE_CLASSIC = 0;
    public static final int MODE_SALVO = 1;
    
    public static final int ORIENTATION_UP = 0;
    public static final int ORIENTATION_RIGHT = 1;
    
    private Ship shipToMove = null; //used for moving ships during placement
    private int currentOrientation = 0;
    private BShipButton currentButton = null;
    
    private Label playerShipsLabel;
    private Label playerCarrierLabel;
    private Label playerBattleshipLabel;
    private Label playerCruiserLabel;
    private Label playerSubmarineLabel;
    private Label playerPatrolBoatLabel;

    private Label cpuShipsLabel;
    private Label cpuCarrierLabel;
    private Label cpuBattleshipLabel;
    private Label cpuCruiserLabel;
    private Label cpuSubmarineLabel;
    private Label cpuPatrolBoatLabel;

    private Label eventLabel;
    
    private GridPane playerGrid;
    private GridPane cpuGrid;
    
    private Button humanBoardBtn;
    private Button cpuBoardBtn;
    
    @Override
    public void start(Stage primaryStage)
    {
        primaryStage.setTitle("Battleship");
        primaryStage.setResizable(false);
        loadMainMenu(primaryStage);
    }
    
    public void loadMainMenu(Stage primaryStage)
    {
        //***** Main Menu Components *****//
        Button classicModeBtn = new Button();
        Button salvoModeBtn = new Button();
        Button testModeBtn = new Button();
        Button exitBtn = new Button();
        
        classicModeBtn.setOnAction((ActionEvent event) ->
        {
            loadShipPlacementScreen(primaryStage, "Player", MODE_CLASSIC);
        });
        
        salvoModeBtn.setOnAction((ActionEvent event) ->
        {
            loadShipPlacementScreen(primaryStage, "Player", MODE_SALVO);
        });
        
        testModeBtn.setOnAction((ActionEvent event) ->
        {
            System.out.println("Test mode selected");
        });
        
        exitBtn.setOnAction((ActionEvent event) ->
        {
            System.exit(0);
        });
        
        GridPane mainMenuGrid = new GridPane();
        mainMenuGrid.setAlignment(Pos.CENTER_LEFT);
        mainMenuGrid.setVgap(40);
        mainMenuGrid.setHgap(10);
        mainMenuGrid.setPadding(new Insets(20,20,150,20));
        
        BackgroundImage myBI= new BackgroundImage(new Image("/imgs/menu_image.jpg",1250,750,false,true),
        BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
          BackgroundSize.DEFAULT);
        mainMenuGrid.setBackground(new Background(myBI));
        
        Text mainMenuTitle = new Text("Battleship");
        mainMenuTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 50));
        
        classicModeBtn.setText("Classic Mode");
        salvoModeBtn.setText("Salvo Mode");
        testModeBtn.setText("Test Mode");
        exitBtn.setText("Exit");
        
        classicModeBtn.setTooltip(new Tooltip("Players take turns firing at each other"));
        salvoModeBtn.setTooltip(new Tooltip("Each player gets as many shots as they have ships afloat"));
        testModeBtn.setTooltip(new Tooltip("Developer only mode. Used for testing targeting algorithm"));
        exitBtn.setTooltip(new Tooltip("Exit to desktop"));
        
        classicModeBtn.setFont(Font.font(classicModeBtn.getFont().getName(), FontWeight.NORMAL, 16));
        salvoModeBtn.setFont(Font.font(classicModeBtn.getFont().getName(), FontWeight.NORMAL, 16));
        testModeBtn.setFont(Font.font(classicModeBtn.getFont().getName(), FontWeight.NORMAL, 16));
        exitBtn.setFont(Font.font(classicModeBtn.getFont().getName(), FontWeight.NORMAL, 16));
        
        mainMenuGrid.add(mainMenuTitle, 1, 0);
        mainMenuGrid.add(classicModeBtn, 1, 5);
        mainMenuGrid.add(salvoModeBtn, 1, 6);
        //mainMenuGrid.add(testModeBtn, 1, 7);
        mainMenuGrid.add(exitBtn, 1, 7);
        
        StackPane mainMenuRoot = new StackPane();
        mainMenuRoot.getChildren().add(mainMenuGrid);
        
        
        Scene scene = new Scene(mainMenuRoot, 1200, 700);
        
        primaryStage.setScene(scene);
        primaryStage.show();
        
    }
    
    public void loadShipPlacementScreen(Stage primaryStage, String defaultName, int gameMode)
    {
        Player human = new Player(defaultName);
        
        
        //***** Classic Mode Components *****//
        BorderPane shipPlacementBorder = new BorderPane();
        GridPane shipPlacementGrid = new GridPane();
        HBox bottomButtons = new HBox();
        HBox topPanel = new HBox();
        VBox tipPanel = new VBox();
        
        shipPlacementGrid.setPadding(new Insets(20,10,20,280));
        shipPlacementGrid.setAlignment(Pos.CENTER);
        
        Button returnToMenuBtn = new Button();
        Button refreshBtn = new Button();
        Button beginGameBtn = new Button();
        Button[][] buttonGrid = new Button[10][10];
        
        Label titleLabel = new Label("Place Your Ships");
        titleLabel.setFont(Font.font(titleLabel.getFont().getName(), FontWeight.NORMAL, 40));
        titleLabel.setAlignment(Pos.CENTER);
        
        Label tipPanelLabel = new Label("Placement Tips:");
        Label tip1 = new Label("1. Click 'Randomize' as many times as\nyou want to randomly place your ships.\n");
        Label tip2 = new Label("2. Click on a ship to move it.\n");
        Label tip3 = new Label("3. Press the 'r' key while you are\nmoving a ship to rotate it.");
        
        tipPanelLabel.setFont(Font.font(tipPanelLabel.getFont().getName(), FontWeight.BOLD, 24));
        tip1.setFont(Font.font(tip1.getFont().getName(), FontWeight.BOLD, 16));
        tip2.setFont(tip1.getFont());
        tip3.setFont(tip1.getFont());
        
        tipPanel.setPadding(new Insets(50,20,0,0));
        tipPanel.setSpacing(15);
        tipPanel.setAlignment(Pos.TOP_LEFT);
        
        returnToMenuBtn.setText("Main Menu");
        refreshBtn.setText("Randomize");
        beginGameBtn.setText("Begin Game");
        
        returnToMenuBtn.setFont(Font.font(returnToMenuBtn.getFont().getName(), FontWeight.NORMAL, 16));
        refreshBtn.setFont(Font.font(refreshBtn.getFont().getName(), FontWeight.NORMAL, 20));
        beginGameBtn.setFont(Font.font(beginGameBtn.getFont().getName(), FontWeight.NORMAL, 20));
        
        returnToMenuBtn.setAlignment(Pos.TOP_LEFT);
        
        beginGameBtn.setDisable(true);
        
        //label axes of grid
        for(int i = 0; i < 10; i++)
        {
            char rowChar = (char) (i+65);
            Text yLabel = new Text(Character.toString(rowChar));
            yLabel.setFont(Font.font(yLabel.getFont().getName(), FontWeight.NORMAL, 20));
            Text xLabel = new Text(Integer.toString(i+1));
            xLabel.setFont(Font.font(xLabel.getFont().getName(), FontWeight.NORMAL, 20));
            
            shipPlacementGrid.add(yLabel, 0, i+1);
            shipPlacementGrid.add(xLabel, i+1, 0);
            
            shipPlacementGrid.setMargin(yLabel,new Insets(0,5,0,0));
            if(i != 9)
            {
                shipPlacementGrid.setMargin(xLabel,new Insets(0,0,0,18));
            }
            else
            {
                shipPlacementGrid.setMargin(xLabel,new Insets(0,0,0,12));
            }
        }
        
        //place buttons
        for(int y = 0; y < 10; y++)
        {
            for(int x = 0; x < 10; x++)
            {
                Coord thisCoord = null;
            
                try
                {
                    thisCoord = new Coord(x,y);
                }
                catch(BattleshipException e)
                {
                    System.out.println("Error generating board for ship placement. Exiting");
                    System.exit(1);
                }
                
                BShipButton thisBtn = new BShipButton(thisCoord);
                thisBtn.setMinSize(50, 50);
                thisBtn.setMaxSize(50, 50);
                shipPlacementGrid.add(thisBtn,x+1,y+1);
                buttonGrid[x][y] = thisBtn;
                thisBtn.setOnMouseEntered(e ->
                {
                    currentButton = thisBtn;
                    redrawShips(buttonGrid, human);
                    
                    //paint hypothetical placement here
                    redrawHypothetical(buttonGrid);
                });
                
                //when button is clicked
                thisBtn.setOnAction(e ->
                {
                    if(shipToMove == null) //trying to pick up a ship to move it
                    {
                        //will be null if there is no ship there
                        shipToMove = human.getShipAt(thisBtn.getCoords());
                        if(shipToMove != null)
                        {
                            beginGameBtn.setDisable(true);
                            Cell[] cells = shipToMove.getCells();
                            //remove current ship placement
                            for(int i = 0; i < shipToMove.getSize(); i++)
                            {
                                buttonGrid[cells[i].getCoords().getX()]
                                          [cells[i].getCoords().getY()].
                                          setStyle(new Button().getStyle());
                            }
                            redrawHypothetical(buttonGrid);
                            
                            try
                            {
                                 switch(shipToMove.getName())
                                {
                                    case "Carrier":
                                        human.initializeCarrier(null);
                                        break;
                                    case "Battleship":
                                        human.initializeBattleship(null);
                                        break;
                                    case "Cruiser":
                                        human.initializeCruiser(null);
                                        break;
                                    case "Submarine":
                                        human.initializeSubmarine(null);
                                        break;
                                    case "Patrol Boat":
                                        human.initializePatrolBoat(null);
                                        break;
                                }
                            }
                            catch(BattleshipException ex)
                            {
                                System.out.println("Error while moving ship");
                                System.exit(1);
                            }
                           
                        }
                        
                    }
                    else //trying to drop a ship somewhere
                    {
                        if(human.getShipAt(thisBtn.getCoords()) == null) //if there is no ship already there
                        {
                            Cell[] newShipCells = new Cell[shipToMove.getSize()];
                            //check if the ship will fit in the grid
                            //if it will then we make our new cell array
                            if(currentOrientation == ORIENTATION_UP)
                            {
                                if(thisBtn.getCoords().getY() - shipToMove.getSize() < -1)
                                {
                                    System.out.println("Placement failed: out of bounds");
                                    return; //we are out of bounds
                                }
                                
                                for(int i = 0; i < shipToMove.getSize(); i++)
                                {
                                    try
                                    {
                                        newShipCells[i] = human.getBoard().getCell(
                                                    thisBtn.getCoords().getX(), 
                                                    thisBtn.getCoords().getY() - i);
                                    }
                                    catch(InvalidCoordinateException ex)
                                    {
                                        System.out.println("Error generating new cell array");
                                        System.out.println(ex.getMessage());
                                        System.exit(1);
                                    }
                                }
                            }
                            else if(currentOrientation == ORIENTATION_RIGHT)
                            {
                                if(thisBtn.getCoords().getX() + shipToMove.getSize() > 10)
                                {
                                    System.out.println("Placement failed: out of bounds");
                                    return; //we are out of bounds
                                }
                                
                                for(int i = 0; i < shipToMove.getSize(); i++)
                                {
                                    try
                                    {
                                        newShipCells[i] = human.getBoard().getCell(
                                                    thisBtn.getCoords().getX() + i, 
                                                    thisBtn.getCoords().getY());
                                    }
                                    catch(InvalidCoordinateException ex)
                                    {
                                        System.out.println("Error generating new cell array");
                                        System.out.println(ex.getMessage());
                                        System.exit(1);
                                    }
                                }
                            }
                            
                            try
                            {
                                switch(shipToMove.getName())
                                {
                                    case "Carrier":
                                        human.initializeCarrier(newShipCells);
                                        break;
                                    case "Battleship":
                                        human.initializeBattleship(newShipCells);
                                        break;
                                    case "Cruiser":
                                        human.initializeCruiser(newShipCells);
                                        break;
                                    case "Submarine":
                                        human.initializeSubmarine(newShipCells);
                                        break;
                                    case "Patrol Boat":
                                        human.initializePatrolBoat(newShipCells);
                                        break;
                                }
                            }
                            catch(InvalidShipSizeException ex)
                            {
                                System.out.println("Invlaid ship size while placing new ship.");
                                System.out.println(ex.getMessage());
                                System.exit(1);
                            }
                            catch(InvalidShipPlacementException ex)
                            {
                                System.out.println("Invlaid ship placement.");
                                return;
                            }
                            
                            //update visuals
                            Ship[] ships = human.getAllShips();
                            for(int i = 0; i < ships.length; i++)
                            {
                                for(int j = 0; j < ships[i].getSize(); j++)
                                {
                                    buttonGrid[ships[i].getCells()[j].getXCoord()]
                                              [ships[i].getCells()[j].getYCoord()].
                                              setStyle("-fx-background-color: #00bf09");
                                }
                            }
                            shipToMove = null;
                            beginGameBtn.setDisable(false);
                        }
                    }
                });
            }
        }
        
        //event handlers
        
        returnToMenuBtn.setOnAction((ActionEvent event) ->
        {
            loadMainMenu(primaryStage);
        });
        
        refreshBtn.setOnAction((ActionEvent event) ->
        {
            beginGameBtn.setDisable(false);
            human.generateRandomShipPlacement();
            Ship[] ships = human.getAllShips();
            
            //first reset all buttons to default color
            ObservableList<Node> children = shipPlacementGrid.getChildren();
            for(Node thisNode : children)
            {
                if(GridPane.getRowIndex(thisNode) > 0 && GridPane.getColumnIndex(thisNode)  > 0)
                {
                    BShipButton button = (BShipButton) thisNode;
                    button.setStyle(new Button().getStyle());
                }
            }
            
            //color buttons with ships placed
            for(int i = 0; i < ships.length; i++)
            {
                for(int j = 0; j < ships[i].getSize(); j++)
                {
                    int x = ships[i].getCell(j).getXCoord() + 1;
                    int y = ships[i].getCell(j).getYCoord() + 1;
                    children = shipPlacementGrid.getChildren();
                    for(Node thisNode : children)
                    {
                        if(GridPane.getRowIndex(thisNode)  == y && GridPane.getColumnIndex(thisNode) == x)
                        {
                            BShipButton button = (BShipButton) thisNode;
                            button.setStyle("-fx-background-color: #00bf09");
                        }
                    }
                }
            }
        });
        
        beginGameBtn.setOnAction((ActionEvent event) ->
        {
            loadGame(primaryStage, human, gameMode);
        });
        
        
        bottomButtons.getChildren().addAll(refreshBtn, beginGameBtn);
        bottomButtons.setSpacing(20);
        bottomButtons.setPadding(new Insets(0,0,50,0));
        bottomButtons.setAlignment(Pos.CENTER);
        
        topPanel.getChildren().addAll(returnToMenuBtn, titleLabel);
        topPanel.setPadding(new Insets(50,0,0,0));
        topPanel.setSpacing(360);
        
        tipPanel.getChildren().addAll(tipPanelLabel,tip1,tip2,tip3);
        
        shipPlacementBorder.setCenter(shipPlacementGrid);
        shipPlacementBorder.setTop(topPanel);
        shipPlacementBorder.setBottom(bottomButtons);
        shipPlacementBorder.setRight(tipPanel);
        
        BackgroundImage myBI= new BackgroundImage(new Image("/imgs/ocean1.jpg",1250,750,false,true),
        BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
          BackgroundSize.DEFAULT);
        shipPlacementBorder.setBackground(new Background(myBI));
        
        
        StackPane placementRoot = new StackPane();
        placementRoot.getChildren().add(shipPlacementBorder);
        
        Scene scene = new Scene(placementRoot, 1200, 700);
        
        //if they press 'r' key we rotate the ship
        scene.setOnKeyPressed((KeyEvent e) ->
        {
            if(e.getCode() == R)
            {
                if(currentOrientation == ORIENTATION_UP)
                {
                    currentOrientation = ORIENTATION_RIGHT;
                    redrawShips(buttonGrid, human);
                    redrawHypothetical(buttonGrid);
                }
                else if(currentOrientation == ORIENTATION_RIGHT)
                {
                    currentOrientation = ORIENTATION_UP;
                    redrawShips(buttonGrid, human);
                    redrawHypothetical(buttonGrid);
                }
            }
        });
        
        primaryStage.setScene(scene);
        primaryStage.show();
        
        //display dialog for player to enter their name
        TextInputDialog nameInput = new TextInputDialog(defaultName);
        nameInput.setTitle("Enter Your Name");
        nameInput.setHeaderText(null);
        nameInput.setContentText("Enter your name:\n(20 characters max)");
        
        Optional<String> input = nameInput.showAndWait();
        if(input.isPresent())
        {
            if(input.get().length() == 0)
            {
                human.setName("Player");
            }   
            else if(input.get().length() > 20)
            {
                human.setName(input.get().substring(0,20));
            }
            else
            {
                human.setName(input.get());
            }
        }
    }
    
    public void loadGame(Stage primaryStage, Player human, int gameMode)
    {
        Player cpu = new Player("Computer");
        cpu.generateRandomShipPlacement();
        cpu.replenishShots(gameMode);
        
        human.replenishShots(gameMode);
        
        Coord targetCoords = new Coord();
        BShipButton previousTargetStyle = new BShipButton();
        BShipButton[][] playerButtons = new BShipButton[10][10];
        BShipButton[][] cpuButtons = new BShipButton[10][10];
        
        //***** Classic Game UI Componenets *****//
        
        //layouts
        BorderPane mainBorder = new BorderPane();
        StackPane boardStack = new StackPane(); //goes in center
        playerGrid = new GridPane(); //goes in stack
        cpuGrid = new GridPane(); //goes in stack
        VBox playerShipBox = new VBox(); //goes on left side
        VBox cpuShipBox = new VBox(); //goes on right side
        HBox firePanel = new HBox(); //goes on bottom
        StackPane topPanel = new StackPane(); //goes on top
        VBox navBox = new VBox(); //goes in top panel
        
        //buttons
        Button returnToMenuBtn = new Button();
        Button restartBtn = new Button();
        Button fireBtn = new Button();
        humanBoardBtn = new Button();
        cpuBoardBtn = new Button();
        
        //labels
        playerShipsLabel = new Label(human.getName() + "'s ships");
        playerCarrierLabel = new Label();
        playerBattleshipLabel = new Label();
        playerCruiserLabel = new Label();
        playerSubmarineLabel = new Label();
        playerPatrolBoatLabel = new Label();
        
        cpuShipsLabel = new Label("Computer's ships");
        cpuCarrierLabel = new Label();
        cpuBattleshipLabel = new Label();
        cpuCruiserLabel = new Label();
        cpuSubmarineLabel = new Label();
        cpuPatrolBoatLabel = new Label();
        
        //need a stack pane for each label for alignment purposes
        StackPane playerShipsStack = new StackPane();
        StackPane playerCarrierStack = new StackPane();
        StackPane playerBattleshipStack = new StackPane();
        StackPane playerCruiserStack = new StackPane();
        StackPane playerSubmarineStack = new StackPane();
        StackPane playerPatrolBoatStack = new StackPane();
        
        StackPane cpuShipsStack = new StackPane();
        StackPane cpuCarrierStack = new StackPane();
        StackPane cpuBattleshipStack = new StackPane();
        StackPane cpuCruiserStack = new StackPane();
        StackPane cpuSubmarineStack = new StackPane();
        StackPane cpuPatrolBoatStack = new StackPane();
        
        
        eventLabel = new Label(human.getName() + "'s turn!");
        
        //images of ships for labels
        ImageView playerCarrierImg = new ImageView(new Image(getClass().getResourceAsStream("/imgs/icon_carrier_right_green.png" ), 250, 50, true, true));
        playerCarrierLabel.setGraphic(playerCarrierImg);
        ImageView playerBattleshipImg = new ImageView(new Image(getClass().getResourceAsStream("/imgs/icon_battleship_right_green.png" ), 250, 50, true, true));
        playerBattleshipLabel.setGraphic(playerBattleshipImg);
        ImageView playerCruiserImg = new ImageView(new Image(getClass().getResourceAsStream("/imgs/icon_cruiser_right_green.png" ), 250, 50, true, true));
        playerCruiserLabel.setGraphic(playerCruiserImg);
        ImageView playerSubmarineImg = new ImageView(new Image(getClass().getResourceAsStream("/imgs/icon_submarine_right_green.png" ), 250, 50, true, true));
        playerSubmarineLabel.setGraphic(playerSubmarineImg);
        ImageView playerPatrolBoatImg = new ImageView(new Image(getClass().getResourceAsStream("/imgs/icon_patrolBoat_right_green.png" ), 250, 50, true, true));
        playerPatrolBoatLabel.setGraphic(playerPatrolBoatImg);
        
        ImageView cpuCarrierImg = new ImageView(new Image(getClass().getResourceAsStream("/imgs/icon_carrier_right_green.png" ), 250, 50, true, true));
        cpuCarrierImg.setScaleX(-1);
        cpuCarrierLabel.setGraphic(cpuCarrierImg);
        ImageView cpuBattleshipImg = new ImageView(new Image(getClass().getResourceAsStream("/imgs/icon_battleship_right_green.png" ), 250, 50, true, true));
        cpuBattleshipImg.setScaleX(-1);
        cpuBattleshipLabel.setGraphic(cpuBattleshipImg);
        ImageView cpuCruiserImg = new ImageView(new Image(getClass().getResourceAsStream("/imgs/icon_cruiser_right_green.png" ), 250, 50, true, true));
        cpuCruiserImg.setScaleX(-1);
        cpuCruiserLabel.setGraphic(cpuCruiserImg);
        ImageView cpuSubmarineImg = new ImageView(new Image(getClass().getResourceAsStream("/imgs/icon_submarine_right_green.png" ), 250, 50, true, true));
        cpuSubmarineImg.setScaleX(-1);
        cpuSubmarineLabel.setGraphic(cpuSubmarineImg);
        ImageView cpuPatrolBoatImg = new ImageView(new Image(getClass().getResourceAsStream("/imgs/icon_patrolBoat_right_green.png" ), 250, 50, true, true));
        cpuPatrolBoatImg.setScaleX(-1);
        cpuPatrolBoatLabel.setGraphic(cpuPatrolBoatImg);
        
        playerShipsStack.getChildren().addAll(playerShipsLabel);
        playerCarrierStack.getChildren().addAll(playerCarrierLabel);
        playerBattleshipStack.getChildren().addAll(playerBattleshipLabel);
        playerCruiserStack.getChildren().addAll(playerCruiserLabel);
        playerSubmarineStack.getChildren().addAll(playerSubmarineLabel);
        playerPatrolBoatStack.getChildren().addAll(playerPatrolBoatLabel);
        
        cpuShipsStack.getChildren().addAll(cpuShipsLabel);
        cpuCarrierStack.getChildren().addAll(cpuCarrierLabel);
        cpuBattleshipStack.getChildren().addAll(cpuBattleshipLabel);
        cpuCruiserStack.getChildren().addAll(cpuCruiserLabel);
        cpuSubmarineStack.getChildren().addAll(cpuSubmarineLabel);
        cpuPatrolBoatStack.getChildren().addAll(cpuPatrolBoatLabel);
        
        StackPane.setAlignment(playerCarrierImg, Pos.CENTER_LEFT);
        StackPane.setAlignment(playerBattleshipImg, Pos.CENTER_LEFT);
        StackPane.setAlignment(playerCruiserImg, Pos.CENTER_LEFT);
        StackPane.setAlignment(playerSubmarineImg, Pos.CENTER_LEFT);
        StackPane.setAlignment(playerPatrolBoatImg, Pos.CENTER_LEFT);
        
        StackPane.setAlignment(cpuCarrierImg, Pos.CENTER_RIGHT);
        StackPane.setAlignment(cpuBattleshipImg, Pos.CENTER_RIGHT);
        StackPane.setAlignment(cpuCruiserImg, Pos.CENTER_RIGHT);
        StackPane.setAlignment(cpuSubmarineImg, Pos.CENTER_RIGHT);
        StackPane.setAlignment(cpuPatrolBoatImg, Pos.CENTER_RIGHT);
        
        
        //***** Build GUI *****//
        
        returnToMenuBtn.setText("Main Menu");
        restartBtn.setText("New Game");
        fireBtn.setText("Fire");
        humanBoardBtn.setText("My Board");
        cpuBoardBtn.setText("Enemy Board");
        
        cpuBoardBtn.setFont(Font.font(cpuBoardBtn.getFont().getName(), FontWeight.NORMAL, 20));
        fireBtn.setFont(Font.font(fireBtn.getFont().getName(), FontWeight.BOLD, 20));
        humanBoardBtn.setFont(Font.font(humanBoardBtn.getFont().getName(), FontWeight.NORMAL, 20));
        
        fireBtn.setMinWidth(125);
        fireBtn.setStyle("-fx-background-color: #ff0019");
        fireBtn.setDisable(true);
        
        firePanel.setAlignment(Pos.CENTER);
        firePanel.setPadding(new Insets(20,20,20,20));
        firePanel.getChildren().addAll(humanBoardBtn, fireBtn, cpuBoardBtn);
        firePanel.setSpacing(100);
        
        navBox.getChildren().addAll(returnToMenuBtn, restartBtn);
        navBox.setAlignment(Pos.TOP_LEFT);
        navBox.setSpacing(10);
        
        eventLabel.setStyle("-fx-font-size: 30px;");
        eventLabel.setAlignment(Pos.CENTER);
        eventLabel.setPadding(new Insets(20,20,20,20));
        topPanel.getChildren().addAll(navBox, eventLabel);
        
        playerShipsLabel.setStyle("-fx-font-size: 20px;-fx-font-weight: bold");
        StackPane.setAlignment(playerShipsLabel, Pos.TOP_CENTER);
        cpuShipsLabel.setStyle("-fx-font-size: 20px;-fx-font-weight: bold");
        StackPane.setAlignment(cpuShipsLabel, Pos.TOP_CENTER);
        
        
        
        
        playerShipBox.setSpacing(15);
        cpuShipBox.setSpacing(15);
        
        playerShipBox.getChildren().addAll(playerShipsStack,
                                           playerCarrierStack,
                                           playerBattleshipStack,
                                           playerCruiserStack,
                                           playerSubmarineStack,
                                           playerPatrolBoatStack);
        
        cpuShipBox.getChildren().addAll(cpuShipsStack,
                                        cpuCarrierStack,
                                        cpuBattleshipStack,
                                        cpuCruiserStack,
                                        cpuSubmarineStack,
                                        cpuPatrolBoatStack);
        
        
        //label axes of grids
        for(int i = 0; i < 10; i++)
        {
            char rowChar = (char) (i+65);
            Text yLabel = new Text(Character.toString(rowChar));
            yLabel.setFont(Font.font(yLabel.getFont().getName(), FontWeight.NORMAL, 20));
            Text xLabel = new Text(Integer.toString(i+1));
            xLabel.setFont(Font.font(xLabel.getFont().getName(), FontWeight.NORMAL, 20));
            
            //have to make copies of labels so they show up on both boards
            Text xLabelCopy = new Text(xLabel.getText());
            Text yLabelCopy = new Text(yLabel.getText());
            xLabelCopy.setFont(xLabel.getFont());
            yLabelCopy.setFont(yLabel.getFont());
            
            playerGrid.add(yLabel, 0, i+1);
            playerGrid.add(xLabel, i+1, 0);
            cpuGrid.add(yLabelCopy, 0, i+1);
            cpuGrid.add(xLabelCopy, i+1, 0);
            
            playerGrid.setMargin(yLabel,new Insets(0,5,0,0));
            cpuGrid.setMargin(yLabelCopy,new Insets(0,5,0,0));
            if(i != 9)
            {
                playerGrid.setMargin(xLabel,new Insets(0,0,0,18));
                cpuGrid.setMargin(xLabelCopy,new Insets(0,0,0,18));
            }
            else
            {
                playerGrid.setMargin(xLabel,new Insets(0,0,0,12));
                cpuGrid.setMargin(xLabelCopy,new Insets(0,0,0,12));
            }
        }
        
        //place buttons on both grids
        for(int y = 0; y < 10; y++)
        {
            for(int x = 0; x < 10; x++)
            {
                Coord thisCoord = null;
            
                try
                {
                    thisCoord = new Coord(x,y);
                }
                catch(BattleshipException e)
                {
                    System.out.println("Error generating board for classic game. Exiting");
                    System.exit(1);
                }
                
                BShipButton humanBtn = new BShipButton(thisCoord);
                humanBtn.setMinSize(50, 50);
                humanBtn.setMaxSize(50, 50);
                humanBtn.setDisable(true);
                playerGrid.add(humanBtn,x+1,y+1);
                playerButtons[x][y] = humanBtn;
                
                BShipButton cpuBtn = new BShipButton(thisCoord);
                cpuBtn.setMinSize(50, 50);
                cpuBtn.setMaxSize(50, 50);
                cpuGrid.add(cpuBtn,x+1,y+1);
                cpuButtons[x][y] = cpuBtn;
                
                cpuBtn.setOnAction((ActionEvent event) ->
                {
                    try
                    {
                        fireBtn.setDisable(false);
                        //reset style of current target
                        cpuButtons[targetCoords.getX()][
                                targetCoords.getY()].setStyle(
                                        previousTargetStyle.getStyle());
                        
                        //store selcted coords
                        targetCoords.setX(cpuBtn.getCoords().getX());
                        targetCoords.setY(cpuBtn.getCoords().getY());
                        
                        previousTargetStyle.setStyle(cpuBtn.getStyle());
                        //set color of new target
                        cpuBtn.setStyle("-fx-background-color: #ffff00");
                        
                    }
                    catch(BattleshipException e)
                    {
                        System.out.println("Error setting target coordinates");
                        System.exit(1);
                    }
                });
            }
        }
        
        Ship[] ships = human.getAllShips();
        ObservableList<Node> children = playerGrid.getChildren();
        //color buttons with ships placed
        for(int i = 0; i < ships.length; i++)
        {
            for(int j = 0; j < ships[i].getSize(); j++)
            {
                int x = ships[i].getCell(j).getXCoord() + 1;
                int y = ships[i].getCell(j).getYCoord() + 1;
                children = playerGrid.getChildren();
                for(Node thisNode : children)
                {
                    if(GridPane.getRowIndex(thisNode)  == y && GridPane.getColumnIndex(thisNode) == x)
                    {
                        BShipButton button = (BShipButton) thisNode;
                        button.setStyle("-fx-background-color: #00bf09");
                    }
                }
            }
        }
        
        //TODO: ask user if they're sure
        returnToMenuBtn.setOnAction((ActionEvent event) ->
        {
            loadMainMenu(primaryStage);
        });
        
        //TODO: ask user if they're sure
        restartBtn.setOnAction((ActionEvent event) ->
        {
            loadShipPlacementScreen(primaryStage, human.getName(), gameMode);
        });
        
        humanBoardBtn.setOnAction((ActionEvent event) ->
        {
            playerGrid.setVisible(true);
            cpuGrid.setVisible(false);
            
            humanBoardBtn.setDisable(true);
            cpuBoardBtn.setDisable(false);
        });
        
        cpuBoardBtn.setOnAction((ActionEvent event) ->
        {
            cpuGrid.setVisible(true);
            playerGrid.setVisible(false);
            
            cpuBoardBtn.setDisable(true);
            humanBoardBtn.setDisable(false);
        });
        
        fireBtn.setOnAction((ActionEvent event) ->
        {
            //begin human's turn
            
            fireBtn.setDisable(true);
            //fire at cpu and generate fire result
            FireResult result = null;
            try
            {
                result = cpu.fireAt(targetCoords, human);
            }
            catch(BattleshipException e)
            {
                System.out.println("Error while generating fire result.");
                System.exit(1);
            }
            BShipButton humanTargetBtn = cpuButtons[targetCoords.getX()][targetCoords.getY()];
            String newStyle = "";
            //report results and update UI
            switch (result.getResult())
            {
                case FireResult.MISS:
                    eventLabel.setText(human.getName() + " fired at " +
                            targetCoords.bShipCoords() + " and missed!");
                    newStyle = "-fx-background-image: url(\"/imgs/icon_miss.jpg\")";
                    previousTargetStyle.setStyle("-fx-background-image: url(\"/imgs/icon_miss.jpg\")");
                    break;
                case FireResult.HIT:
                    eventLabel.setText(human.getName() + " fired at " + 
                            targetCoords.bShipCoords() + " and hit something!");
                    newStyle = "-fx-background-image: url(\"/imgs/icon_hit.jpg\")";
                    previousTargetStyle.setStyle("-fx-background-image: url(\"/imgs/icon_hit.jpg\")");
                    break;
                case FireResult.SINK:
                    eventLabel.setText(human.getName() + " fired at " +
                            targetCoords.bShipCoords() + " and sunk " +
                            cpu.getName() + "'s " + 
                            result.getSunkShip().getName() + "!");
                    previousTargetStyle.setStyle("-fx-background-color: #000000;-fx-background-image: url(\"/imgs/icon_sink.jpg\")");
                    newStyle = "-fx-background-color: #000000;-fx-background-image: url(\"/imgs/icon_sink.jpg\")";
                    Ship ship = result.getSunkShip();
                    switch (ship.getName())
                    {
                        case "Carrier":
                            ImageView playerCarrierImg1 = new ImageView(new Image(getClass().getResourceAsStream("/imgs/icon_carrier_right_red.png" ), 250, 50, true, true));
                            playerCarrierImg1.setScaleX(-1);
                            cpuCarrierLabel.setGraphic(playerCarrierImg1);
                            break;
                        case "Battleship":
                            ImageView playerBattleshipImg1 = new ImageView(new Image(getClass().getResourceAsStream("/imgs/icon_battleship_right_red.png" ), 250, 50, true, true));
                            playerBattleshipImg1.setScaleX(-1);
                            cpuBattleshipLabel.setGraphic(playerBattleshipImg1);
                            break;
                        case "Cruiser":
                            ImageView playerCruiserImg1 = new ImageView(new Image(getClass().getResourceAsStream("/imgs/icon_cruiser_right_red.png" ), 250, 50, true, true));
                            playerCruiserImg1.setScaleX(-1);
                            cpuCruiserLabel.setGraphic(playerCruiserImg1);
                            break;
                        case "Submarine":
                            ImageView playerSubmarineImg1 = new ImageView(new Image(getClass().getResourceAsStream("/imgs/icon_submarine_right_red.png" ), 250, 50, true, true));
                            playerSubmarineImg1.setScaleX(-1);
                            cpuSubmarineLabel.setGraphic(playerSubmarineImg1);
                            break;
                        case "Patrol Boat":
                            ImageView playerPatrolBoatImg1 = new ImageView(new Image(getClass().getResourceAsStream("/imgs/icon_patrolBoat_right_red.png" ), 250, 50, true, true));
                            playerPatrolBoatImg1.setScaleX(-1);
                            cpuPatrolBoatLabel.setGraphic(playerPatrolBoatImg1);
                            break;
                    }
                    break;
                default:
                    System.out.println("Error in human fire result");
                    System.exit(1);
            }
            cpuButtons[targetCoords.getX()][targetCoords.getY()].setDisable(true);
            //animate shot location
            if(result.getResult() == FireResult.SINK)
            {
                //store coordinates of shunken ship's cells
                Coord[] coords = new Coord[result.getSunkShip().getSize()];
                for(int i = 0; i < result.getSunkShip().getSize(); i++)
                {
                    coords[i] = result.getSunkShip().getCell(i).getCoords();
                }

                //prepare array of timelines for each cell's animation
                Timeline[] timelines = new Timeline[coords.length];
                for(int i = 0; i < coords.length; i++)
                {
                    timelines[i] = new Timeline();
                    
                    if(i == coords.length - 1 && human.getNumShots() == 0 && !checkIfHumanWon(primaryStage, cpu, human, cpuButtons, playerButtons))
                    {
                        timelines[i].setOnFinished(e -> processCPUTurn(primaryStage, cpu, human, cpuButtons, playerButtons, gameMode));
                    }
                    
                    BShipButton sunkShipBtn = cpuButtons[coords[i].getX()][coords[i].getY()];
                    
                    KeyValue keyVal1 = new KeyValue(sunkShipBtn.styleProperty(), newStyle);
                    KeyFrame keyFrame1 = new KeyFrame(Duration.millis(0), keyVal1);
                    
                    KeyValue keyVal2 = new KeyValue(sunkShipBtn.styleProperty(), "-fx-background-color: #ffff00");
                    KeyFrame keyFrame2 = new KeyFrame(Duration.millis(500), keyVal2);
                    
                    KeyValue keyVal3 = new KeyValue(sunkShipBtn.styleProperty(), newStyle);
                    KeyFrame keyFrame3 = new KeyFrame(Duration.millis(1000), keyVal3);
                    
                    KeyValue keyVal4 = new KeyValue(sunkShipBtn.styleProperty(), "-fx-background-color: #ffff00");
                    KeyFrame keyFrame4 = new KeyFrame(Duration.millis(1500), keyVal4);
                    
                    KeyValue keyVal5 = new KeyValue(sunkShipBtn.styleProperty(), newStyle);
                    KeyFrame keyFrame5 = new KeyFrame(Duration.millis(2000), keyVal5);
                    
                    KeyValue keyVal6 = new KeyValue(sunkShipBtn.styleProperty(), newStyle);
                    KeyFrame keyFrame6 = new KeyFrame(Duration.millis(2500), keyVal6);
                    
                    timelines[i].getKeyFrames().addAll(keyFrame1, keyFrame2, keyFrame3, keyFrame4, keyFrame5, keyFrame6);
                    
                }
                
                for(int i = 0; i < timelines.length; i++)
                {
                    timelines[i].play();
                }
                checkIfHumanWon(primaryStage, cpu, human, cpuButtons, playerButtons);
            }
            else
            {
                Timeline timeline = new Timeline();
                
                if(human.getNumShots() == 0)
                {
                    timeline.setOnFinished(e -> processCPUTurn(primaryStage, cpu, human, cpuButtons, playerButtons, gameMode));
                }
                
                KeyValue keyVal1 = new KeyValue(humanTargetBtn.styleProperty(), newStyle);
                KeyFrame keyFrame1 = new KeyFrame(Duration.millis(0), keyVal1);
                
                KeyValue keyVal2 = new KeyValue(humanTargetBtn.styleProperty(), "-fx-background-color: #ffff00");
                KeyFrame keyFrame2 = new KeyFrame(Duration.millis(500), keyVal2);
                
                KeyValue keyVal3 = new KeyValue(humanTargetBtn.styleProperty(), newStyle);
                KeyFrame keyFrame3 = new KeyFrame(Duration.millis(1000), keyVal3);
                
                KeyValue keyVal4 = new KeyValue(humanTargetBtn.styleProperty(), "-fx-background-color: #ffff00");
                KeyFrame keyFrame4 = new KeyFrame(Duration.millis(1500), keyVal4);
                
                KeyValue keyVal5 = new KeyValue(humanTargetBtn.styleProperty(), newStyle);
                KeyFrame keyFrame5 = new KeyFrame(Duration.millis(2000), keyVal5);
                
                KeyValue keyVal6 = new KeyValue(humanTargetBtn.styleProperty(), newStyle);
                KeyFrame keyFrame6 = new KeyFrame(Duration.millis(2500), keyVal6);
                
                timeline.getKeyFrames().addAll(keyFrame1, keyFrame2, keyFrame3, keyFrame4, keyFrame5, keyFrame6);
                timeline.play();
            }
            if(human.getNumShots() <= 0)
            {
                human.replenishShots(gameMode);
                cpu.replenishShots(gameMode);
                
                //prevent human from playing before cpu has their turn
                disableButtons(cpuButtons);
            }
        });
        
        
        BackgroundImage myBI= new BackgroundImage(new Image("/imgs/ocean1.jpg",1250,750,false,true),
        BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
          BackgroundSize.DEFAULT);
        mainBorder.setBackground(new Background(myBI));
        
        
        playerGrid.setVisible(false);
        cpuBoardBtn.setDisable(true);
        playerGrid.setAlignment(Pos.CENTER);
        cpuGrid.setAlignment(Pos.CENTER);
        
        playerShipBox.setPadding(new Insets(50,0,0,0));
        cpuShipBox.setPadding(new Insets(50,0,0,0));
        
        boardStack.getChildren().add(playerGrid);
        boardStack.getChildren().add(cpuGrid);
        boardStack.setAlignment(Pos.CENTER);
        
        mainBorder.setTop(topPanel);
        mainBorder.setBottom(firePanel);
        mainBorder.setLeft(playerShipBox);
        mainBorder.setRight(cpuShipBox);
        mainBorder.setCenter(boardStack);
        
        
        StackPane placementRoot = new StackPane();
        placementRoot.getChildren().add(mainBorder);
        
        Scene scene = new Scene(placementRoot, 1200, 700);
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    //we need to process the cpu turn separately because of javafx timeline animations
    public void processCPUTurn(Stage primaryStage, Player cpu, Player human, BShipButton[][] cpuButtons, BShipButton[][] playerButtons, int gameMode)
    {
        System.out.println("CPU:\n");
        System.out.println(cpu.boardToStringStates());
        System.out.println("\n\nHUMAN:\n");
        System.out.println(human.boardToStringStates());
        
        
        //process computer's turn and report results in UI
        cpu.updateRecommendation();
        Coord cpuTarget = cpu.getRecommendation();
        FireResult result = null;
        
        //fire at cpu recommendation
        try
        {
            result = human.fireAt(cpuTarget, cpu);
        }
        catch(BattleshipException e)
        {
            System.out.println("Error generating computer fire result");
            System.exit(1);
        }

        //show player's board
        cpuGrid.setVisible(false);
        playerGrid.setVisible(true);
        humanBoardBtn.setDisable(true);
        cpuBoardBtn.setDisable(false);

        BShipButton targetBtn = playerButtons[cpuTarget.getX()][cpuTarget.getY()];
        String newStyle = "";
        
        //report results
        switch(result.getResult())
        {
            case FireResult.MISS:
                eventLabel.setText(cpu.getName() + " fired at " + 
                    cpuTarget.bShipCoords() + " and missed!");


                newStyle = "-fx-background-image: url(\"/imgs/icon_miss.jpg\")";
                break;

            case FireResult.HIT:
                eventLabel.setText(cpu.getName() + " fired at " +
                    cpuTarget.bShipCoords() + " and hit something!");


                newStyle = "-fx-background-image: url(\"/imgs/icon_hit.jpg\")";
                break;

            case FireResult.SINK:
                eventLabel.setText(cpu.getName() + " fired at " + 
                    cpuTarget.bShipCoords() + " and sunk " + 
                    human.getName() + "'s " + 
                    result.getSunkShip().getName() + "!");
                newStyle = "-fx-background-color: #000000;-fx-background-image: url(\"/imgs/icon_sink.jpg\")";
                Ship ship = result.getSunkShip();

                //color UI ship name based on ship that was sunk
                switch(ship.getName())
                {
                    case "Carrier":
                        ImageView cpuCarrierImg = new ImageView(new Image(getClass().getResourceAsStream("/imgs/icon_carrier_right_red.png" ), 250, 50, true, true));
                        playerCarrierLabel.setGraphic(cpuCarrierImg);
                        break;

                    case "Battleship":
                        ImageView cpuBattleshipImg = new ImageView(new Image(getClass().getResourceAsStream("/imgs/icon_battleship_right_red.png" ), 250, 50, true, true));
                        playerBattleshipLabel.setGraphic(cpuBattleshipImg);
                        break;

                    case "Cruiser":
                        ImageView cpuCruiserImg = new ImageView(new Image(getClass().getResourceAsStream("/imgs/icon_cruiser_right_red.png" ), 250, 50, true, true));
                        playerCruiserLabel.setGraphic(cpuCruiserImg);
                        break;

                    case "Submarine":
                        ImageView cpuSubmarineImg = new ImageView(new Image(getClass().getResourceAsStream("/imgs/icon_submarine_right_red.png" ), 250, 50, true, true));
                        playerSubmarineLabel.setGraphic(cpuSubmarineImg);
                        break;

                    case "Patrol Boat":
                        ImageView cpuPatrolBoatImg = new ImageView(new Image(getClass().getResourceAsStream("/imgs/icon_patrolBoat_right_red.png" ), 250, 50, true, true));
                        playerPatrolBoatLabel.setGraphic(cpuPatrolBoatImg);
                        break;
                }

                break;
        }

        
        
        //animate shot location
        if(result.getResult() == FireResult.SINK)
        {
            //store coordinates of shunken ship's cells
            Coord[] coords = new Coord[result.getSunkShip().getSize()];
            for(int i = 0; i < result.getSunkShip().getSize(); i++)
            {
                coords[i] = result.getSunkShip().getCell(i).getCoords();
            }

            //prepare array of timelines for each cell's animation
            Timeline[] timelines = new Timeline[coords.length];
            for(int i = 0; i < coords.length; i++)
            {
                timelines[i] = new Timeline();
                if(i == coords.length - 1 && cpu.getNumShots() > 0 && !checkIfCPUWon(primaryStage, cpu, human, cpuButtons, playerButtons))
                {
                    timelines[i].setOnFinished(e -> processCPUTurn(primaryStage, cpu, human, cpuButtons, playerButtons, gameMode));
                }
                else if(i == coords.length - 1)
                {
                    timelines[i].setOnFinished(e -> checkIfCPUWon(primaryStage, cpu, human, cpuButtons, playerButtons));
                }
                

                BShipButton sunkShipBtn = playerButtons[coords[i].getX()][coords[i].getY()];

                KeyValue keyVal1 = new KeyValue(sunkShipBtn.styleProperty(), newStyle);
                KeyFrame keyFrame1 = new KeyFrame(Duration.millis(0), keyVal1);

                KeyValue keyVal2 = new KeyValue(sunkShipBtn.styleProperty(), "-fx-background-color: #ffff00");
                KeyFrame keyFrame2 = new KeyFrame(Duration.millis(500), keyVal2);

                KeyValue keyVal3 = new KeyValue(sunkShipBtn.styleProperty(), newStyle);
                KeyFrame keyFrame3 = new KeyFrame(Duration.millis(1000), keyVal3);

                KeyValue keyVal4 = new KeyValue(sunkShipBtn.styleProperty(), "-fx-background-color: #ffff00");
                KeyFrame keyFrame4 = new KeyFrame(Duration.millis(1500), keyVal4);

                KeyValue keyVal5 = new KeyValue(sunkShipBtn.styleProperty(), newStyle);
                KeyFrame keyFrame5 = new KeyFrame(Duration.millis(2000), keyVal5);

                KeyValue keyVal6 = new KeyValue(sunkShipBtn.styleProperty(), newStyle);
                KeyFrame keyFrame6 = new KeyFrame(Duration.millis(2500), keyVal6);

                timelines[i].getKeyFrames().addAll(keyFrame1, keyFrame2, keyFrame3, keyFrame4, keyFrame5, keyFrame6);

            }

            for(int i = 0; i < timelines.length; i++)
            {
                timelines[i].play();
            }
            
        }
        else
        {
            Timeline timeline = new Timeline();
            
            if(cpu.getNumShots() > 0)
            {
                timeline.setOnFinished(e -> processCPUTurn(primaryStage, cpu, human, cpuButtons, playerButtons, gameMode));
            }
            
            
            KeyValue keyVal1 = new KeyValue(targetBtn.styleProperty(), newStyle);
            KeyFrame keyFrame1 = new KeyFrame(Duration.millis(0), keyVal1);

            KeyValue keyVal2 = new KeyValue(targetBtn.styleProperty(), "-fx-background-color: #ffff00");
            KeyFrame keyFrame2 = new KeyFrame(Duration.millis(500), keyVal2);

            KeyValue keyVal3 = new KeyValue(targetBtn.styleProperty(), newStyle);
            KeyFrame keyFrame3 = new KeyFrame(Duration.millis(1000), keyVal3);

            KeyValue keyVal4 = new KeyValue(targetBtn.styleProperty(), "-fx-background-color: #ffff00");
            KeyFrame keyFrame4 = new KeyFrame(Duration.millis(1500), keyVal4);

            KeyValue keyVal5 = new KeyValue(targetBtn.styleProperty(), newStyle);
            KeyFrame keyFrame5 = new KeyFrame(Duration.millis(2000), keyVal5);

            KeyValue keyVal6 = new KeyValue(targetBtn.styleProperty(), newStyle);
            KeyFrame keyFrame6 = new KeyFrame(Duration.millis(2500), keyVal6);

            timeline.getKeyFrames().addAll(keyFrame1, keyFrame2, keyFrame3, keyFrame4, keyFrame5, keyFrame6);
            timeline.play();
        }
        
        if(cpu.getNumShots() <= 0)
        {
            cpu.replenishShots(gameMode);
            human.replenishShots(gameMode);
            
            //allow human to select buttons again
            enableUntouchedButtons(cpuButtons, human);
        }
    }
    
    public boolean checkIfHumanWon(Stage primaryStage, Player cpu, Player human, Button[][] cpuButtons, Button[][] humanButtons)
    {
        //check if human just won
        if(cpu.allShipsSunk())
        {
            eventLabel.setText("Game Over: " + human.getName() + " wins!!!");
            disableButtons(cpuButtons);
            disableButtons(humanButtons);
            return true;
        }
        return false;
    }
    
    public boolean checkIfCPUWon(Stage primaryStage, Player cpu, Player human, Button[][] cpuButtons, Button[][] humanButtons)
    {
        //check if cpu just won
        if(human.allShipsSunk())
        {
            eventLabel.setText("Game Over: " + cpu.getName() + " wins!");
            disableButtons(cpuButtons);
            disableButtons(humanButtons);
            
            //show all cpu ships that weren't sunk
            Ship[] ships = cpu.getAllShips();
            for(int i = 0; i < ships.length; i++)
            {
                if(ships[i].isAfloat())
                {
                    for(int j = 0; j < ships[i].getSize(); j++)
                    {
                        Coord coords = ships[i].getCell(j).getCoords();
                        int x = coords.getX();
                        int y = coords.getY();
                        
                        
                        try
                        {
                            //System.out.println("i=" + i);
                            //System.out.println("j=" + j);
                            //System.out.println("cell via ships:" + ships[i].getCell(j));
                            //System.out.println("cell via board:" + human.getBoard().getCell(x, y));
                            //System.out.println(cpu.getBoard().getCell(x,y).getState());
                        
                            if(human.getBoard().getCell(x,y).getState() == Cell.UNTOUCHED)
                            {
                                cpuButtons[x][y].setStyle("-fx-background-color: #ff0019");
                            }
                        }
                        catch(BattleshipException e)
                        {
                            System.out.println("Error revealing cpu ships");
                            System.exit(1);
                        }
                        
                    }
                }
            }
            return true;
        }
        return false;
    }
    
    private void disableButtons(Button[][] buttons)
    {
        for(int i = 0; i < 10; i++)
        {
            for(int j = 0; j < 10; j++)
            {
                buttons[i][j].setDisable(true);
            }
        }
    }
    
    private void enableUntouchedButtons(Button[][] buttons, Player player)
    {
        for(int y = 0; y < 10; y++)
        {
            for(int x = 0; x < 10; x++)
            {
                try
                {
                    if(player.getBoard().getCell(x, y).getState() == Cell.UNTOUCHED)
                    {
                        buttons[x][y].setDisable(false);
                    }
                }
                catch(BattleshipException e)
                {
                    System.out.println("Error re-enabling player buttons");
                }
                
            }
        }
    }
    
    private void redrawShips(Button[][] buttonGrid, Player human)
    {
        if(shipToMove != null)
        {
            //remove previous hypothetical ship placement
            for(int j = 0; j < 10; j++)
            {
                for(int i = 0; i < 10; i++)
                {
                    buttonGrid[i][j].setStyle(new Button().getStyle());
                }
            }
            Ship[] ships = human.getAllShips();
            for(int i = 0; i < ships.length; i++)
            {
                for(int j = 0; j < ships[i].getSize(); j++)
                {
                    buttonGrid[ships[i].getCells()[j].getXCoord()]
                              [ships[i].getCells()[j].getYCoord()].
                              setStyle("-fx-background-color: #00bf09");
                }
            }
        }
    }
    
    private void redrawHypothetical(Button[][] buttonGrid)
    {
        if(shipToMove != null && currentButton != null)
        {
            if(currentOrientation == ORIENTATION_UP)
            {
                for(int i = 0; i < shipToMove.getSize(); i++)
                {
                    if(currentButton.getCoords().getY() - i > -1)
                    {
                        buttonGrid[currentButton.getCoords().getX()]
                              [currentButton.getCoords().getY() - i].
                              setStyle("-fx-background-color: #0015ff");
                    }
                }
            }
            else if(currentOrientation == ORIENTATION_RIGHT)
            {
                for(int i = 0; i < shipToMove.getSize(); i++)
                {
                    if(currentButton.getCoords().getX() + i < 10)
                    {
                        buttonGrid[currentButton.getCoords().getX() + i]
                              [currentButton.getCoords().getY()].
                              setStyle("-fx-background-color: #0015ff");
                    }
                }
            }
        }
    }

    /**
     @param args the command line arguments
     */
    public static void main(String[] args)
    {
        launch(args);
    }
}