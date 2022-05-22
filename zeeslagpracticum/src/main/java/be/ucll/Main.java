package be.ucll;

import be.ucll.guicore.OngeldigeCoordinaatException;
import be.ucll.zeeslaggui.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

public class Main {
    ZeeslagGui gui = ZeeslagGui.getZeeslagGui();

    //Fleets is een verzameling van alle schepen die de speler en vijand hebben
    Fleet enemyFleet = new Fleet();
    Fleet playerFleet = new Fleet();

    //CoordinateRecorder gebruikt de lengte, richting en coordinaten om alle coordinaten die de schepen innemen te onthouden voor gebruik in controles en spelverloop (Hitdetectie)
    CoordinateRecorder testCoordinates = new CoordinateRecorder();
    CoordinateRecorder playerHitRemember = new CoordinateRecorder();
    CoordinateRecorder enemyHitRemember = new CoordinateRecorder();
    CoordinateRecorder enemyBombAttacks = new CoordinateRecorder();
    CoordinateRecorder playerBombAttacks = new CoordinateRecorder();

    //direction has 2 values: 0 or 1, which can be changed with a Toolbar button; 0 = horizontal, 1 = vertical
    int direction = 0;
    //shipSelect has 4 values: 0, 1, 2 & 3, which are 'vliegdekschip', 'slagschip', 'onderzeeer' & 'Patrouilleschip'.
    int shipSelect = 0;
    //Strategy is een optie in het menu om aan te zetten en zal beslissen hoe de computer zijn move zal maken; 0 = random decisions, 1 = pre-programmed decisions
    int Strategy = 0;
    //
    int restart = 0;

    public static void main(String[] args) {
        Main game = new Main();
        game.start();
    }

    private void start() {
        gui.addToolbarButton(this::changeDirection, "Direction");
        gui.addToolbarButton(this::shipSelection, "Select Ship");
        gui.addToolbarButton(this::changeStrategy, "Verander de AI strategy");
        gui.addMenuItem(this::handleMenuItem, "Bestand", "Laad Bestand");

        //Gameverloop:
  /*
  0,5. er wordt beslist hoe de speler de enemy speler zijn schepen wilt laten zetten: Random of pre-Arranged
  1. Player zet zijn schepen: Random of Pre-Arranged
  2. De speler krijgt het bericht dat alle schepen geplaatst zijn en dat het spel nu kan starten (Er is ook de mogelijkheid om alle schepen terug te verwijderen en een nieuwe setup te plaatsen)
  3. De speler kiest een coordinaat om aan te vallen met zijn muis (Er wordt gecheckt of op die locatie op het vijandelijke veld een boot is)
  4. de enemy kiest een coordinaat om aan te vallen
  ...
  5. het spel eindigt wanneer 1 persoon zijn boten op 0 staan.
  */

        //Placement phase
        while (restart == 0){
            gui.wisBord();
            enemyFleet.DeleteList();
            playerFleet.DeleteList();
            int counter = 0;
            while (enemyFleet.ShipArraylist.size() < 10) {
                createValidRandomShip();
                gui.setStatusTekst("Enemy Ships: " + enemyFleet.ShipArraylist.size() + "     Player Ships: " + playerFleet.ShipArraylist.size());
                counter++;
                if (counter > 500)
                {
                    restart = 0;
                    break;
                }
            }
            while (playerFleet.ShipArraylist.size() < 10) {
                if (counter > 500)
                {
                    restart = 0;
                    break;
                }
                shipPlacement(gui.getCoordinaat());
                gui.setStatusTekst("Enemy Ships: " + enemyFleet.ShipArraylist.size() + "     Player Ships: " + playerFleet.ShipArraylist.size());
            }

            //Conditions to start the game
            if (playerFleet.ShipArraylist.size() == 10 && enemyFleet.ShipArraylist.size() == 10)
            {
                restart = 1;
            }
        }


        //Game Phase (Turn Based)
        gui.setStatusTekst("Schepen zijn gezet! Het spel begint. Selecteer een locatie om te bombarderen");
        while (enemyFleet.ShipArraylist.size() != 0 || playerFleet.ShipArraylist.size() != 0) {
            System.out.println("Spelers beurt");
            Coordinaat coordinaat = gui.getCoordinaat();
            while (coordinaat.getGebied().equals(GuiGebied.EIGEN)) {
                gui.showMessage("fout", "Gelieve een coordinaat in het vijandelijk terrein te selecteren");
            }
            if (playerBombAttacks.CoordinateArraylist.contains(coordinaat)) {
                bombAttack(coordinaat, GuiGebied.VIJAND);
                playerBombAttacks.CreateCoordinate(coordinaat);
                while (playerBombAttacks.CoordinateArraylist.contains(coordinaat)) {
                    gui.showMessage("invalid", "U hebt hier al eens gebombardeerd, kies opnieuw");
                    coordinaat = gui.getCoordinaat();
                }
            }
            bombAttack(coordinaat, GuiGebied.VIJAND);
            playerBombAttacks.CreateCoordinate(coordinaat);

            gui.setStatusTekst("Enemy Ships: " + enemyFleet.ShipArraylist.size() + "     Player Ships: " + playerFleet.ShipArraylist.size());
            for (int i = 0; i < enemyFleet.ShipArraylist.size(); i++) {
                if (sunkenShipDetection(enemyFleet.ShipArraylist.get(i))) {
                    gui.showMessage("You sunk my battleship", "You sunk my battleship");
                    gui.setStatusTekst("Enemy Ships: " + enemyFleet.ShipArraylist.size() + "\n" + "     Player Ships: " + playerFleet.ShipArraylist.size());
                }
            }

            System.out.println("Computer's Beurt");
            int k = 0;
            if (Strategy == 0) {
                System.out.println("Random hit locaties");
                {
                    try {
                        while (k == 0) {
                            Coordinaat randomCoordinate = new Coordinaat(randInt(1, 10), randInt(1, 10), GuiGebied.EIGEN);
                            if (!enemyBombAttacks.CoordinateArraylist.contains(randomCoordinate)) {
                                bombAttack(randomCoordinate, GuiGebied.EIGEN);
                                enemyBombAttacks.CreateCoordinate(randomCoordinate);
                                k = 1;
                            }
                        }
                    } catch (OngeldigeCoordinaatException e) {
                        System.out.println("Dit zou niet mogen voorkomen maar toch:");
                        e.printStackTrace();
                    }
                }
            }
            if (Strategy == 1) {
                System.out.println("De pre-planned strategy");
                for (int x = 1; x < 11; x++) {
                    for (int y = 1; y < 11; y++) {
                        try {
                            Coordinaat strategicCoordinate = new Coordinaat(x, y, GuiGebied.EIGEN);
                            if (!enemyBombAttacks.CoordinateArraylist.contains(strategicCoordinate)) {
                                bombAttack(strategicCoordinate, GuiGebied.EIGEN);
                                enemyBombAttacks.CreateCoordinate(strategicCoordinate);
                                k = 1;
                                break;
                            }
                        } catch (OngeldigeCoordinaatException e) {
                            System.out.println("Dit zou niet mogen voorkomen maar toch:");
                            e.printStackTrace();
                        }
                    }
                    if (k == 1) {
                        break;
                    }
                }
            }
            for (int i = 0; i < playerFleet.ShipArraylist.size(); i++) {
                if (sunkenShipDetection(playerFleet.ShipArraylist.get(i))) {
                    gui.showMessage("He sunk your battleship", "He sunk your battleship");
                    gui.setStatusTekst("Enemy Ships: " + enemyFleet.ShipArraylist.size() + "\n" + "     Player Ships: " + playerFleet.ShipArraylist.size());
                }
            }
        }
        if (enemyFleet.ShipArraylist.isEmpty()) {
            gui.showMessage("winnaar", "u hebt gewonnen");
        }
        else if (playerFleet.ShipArraylist.isEmpty()) {
            gui.showMessage("spijtig", "u hebt verloren");
        }
    }

    //Handles the menu selection for a premade text document containing ship positions.
    public void handleMenuItem() {
        if (!enemyFleet.ShipArraylist.isEmpty()) {
            enemyFleet.ShipArraylist.clear();
            gui.wisBord();
        }
        String Filename = gui.getFileName();
        try {
            BestandsLezer bestandsLezer = new BestandsLezer(Filename);
            try {
                String[][] values = bestandsLezer.getValues();
                for (int i = 0; i < values.length; i++) {
                    String[] rij = values[i];
                    String richting = rij[1];
                    int xCoordinaat = Integer.parseInt(rij[2]);
                    int yCoordinaat = Integer.parseInt(rij[3]);
                    int lengte = Integer.parseInt(rij[4]);

                    try {
                        Coordinaat coordinaat = new Coordinaat(xCoordinaat, yCoordinaat, GuiGebied.VIJAND);
                        if (validityChecker(richting,lengte,coordinaat)){
                            enemyFleet.CreateShip(richting, lengte, coordinaat);
                            handleDrawing(enemyFleet.ShipArraylist.get(i), GuiGebied.VIJAND);
                        }
                        else {
                            gui.showMessage("ongeldig", "Het gekozen document heeft geen geldige schip positities.");
                            enemyFleet.DeleteList();
                            gui.wisBord();
                        }
                    } catch (OngeldigeCoordinaatException e) {
                        System.out.println("Dit zou niet mogen voorkomen maar toch:");
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                System.out.println("Dit zou niet mogen voorkomen maar toch:");
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            System.out.println("Dit zou niet mogen voorkomen maar toch:");
            e.printStackTrace();
        }
    }

    //Handles the drawing of a ship based on coordinates, length, direction & the field they are on
    public void handleDrawing(Ship ship, GuiGebied gebied) {
        int lengte = ship.getLengte();
        String richting = ship.getRichting();

        if (playerFleet.ShipArraylist.size() < 11) {
            if (richting.equals("horizontaal")) {
                for (int i = 0; i < ship.getCoordinates().CoordinateArraylist.size(); i++) {
                    Coordinaat c = ship.getCoordinates().CoordinateArraylist.get(i);
                    if (i == 0) {
                        gui.tekenSchipEinde(c, CellKant.RECHTS);
                    }
                    if (i == lengte - 1) {
                        gui.tekenSchipEinde(c, CellKant.LINKS);
                    } else if (i > 0 && i < lengte) {
                        gui.tekenHorizontaalSchipDeel(c);
                    }
                }
            }
            if (richting.equals("vertikaal")) {
                for (int i = 0; i < ship.getCoordinates().CoordinateArraylist.size(); i++) {
                    Coordinaat c = ship.getCoordinates().CoordinateArraylist.get(i);
                    if (i == 0) {
                        gui.tekenSchipEinde(c, CellKant.ONDER);
                    }
                    if (i == lengte - 1) {
                        gui.tekenSchipEinde(c, CellKant.BOVEN);
                    } else if (i > 0 && i < lengte) {
                        gui.tekenVertikaalSchipDeel(c);
                    }
                }
            }
        }
    }


    public void changeStrategy(){
        Strategy = gui.showOption("Opties", "Welke strategie wilt u dat de computer heeft?", new String[]{"Random", "Pre-programmed"});
    }

    //Kleine option om de richting van het schip te veranderen tijdens de plaats fase
    public void changeDirection() {
        direction = gui.showOption("Opties", "In which direction do you want to position your ship?", new String[]{"Horizontal", "Vertical"});
        //System.out.println(direction);
    }

    //kleine optie om een ander schip eerst te plaatsen tijdens de plaats fase
    public void shipSelection() {
        shipSelect = gui.showOption("Opties", "Which ship do you want to place?", new String[]{"Vliegdekschip [lengte: 6]", "Slagschip [lengte: 4]", "Onderzeeer [lengte: 3]", "Patrouilleschip [Lengte: 2]"});
        //System.out.println(shipSelect);
    }

    //Function to place ships on the field. At the moment this works only for the player and during the selection phase. To expand it we need to interweave a method for non-players to use it so it is a universal function.
    public void shipPlacement(Coordinaat coordinaat) {
        int xWaarde = coordinaat.getX();
        int yWaarde = coordinaat.getY();
        int lengte = 0;
        String richting = "horizontaal";

        switch (shipSelect) {
            case 0:
                lengte = 6;
                break;
            case 1:
                lengte = 4;
                break;
            case 2:
                lengte = 3;
                break;
            case 3:
                lengte = 2;
                break;
        }

        switch (direction) {
            case 0:
                richting = "horizontaal";
                break;
            case 1:
                richting = "vertikaal";
                break;
        }

        if (validityChecker(richting, lengte, coordinaat)) {
            if (coordinaat.getGebied().equals(GuiGebied.EIGEN)) {
                if (shipIdentifier(playerFleet, lengte)) {
                    playerFleet.CreateShip(richting, lengte, coordinaat);
                    handleDrawing(playerFleet.ShipArraylist.get(playerFleet.ShipArraylist.size() - 1), coordinaat.getGebied());
                }
            } else {
                if (shipIdentifier(enemyFleet, lengte)) {
                    enemyFleet.CreateShip(richting, lengte, coordinaat);
                    handleDrawing(enemyFleet.ShipArraylist.get(enemyFleet.ShipArraylist.size() - 1), coordinaat.getGebied());
                }
            }
        }
    }

    //This function counts the amount of ships of a type already present within your fleet and thus limits the number/types of ships that can be placed.
    public boolean shipIdentifier(Fleet fleet, int testBoot) {
        int patrouilleCounter = 0;
        int onderzeeCounter = 0;
        int slagCounter = 0;

        for (int i = 0; i < fleet.ShipArraylist.size(); i++) {
            if (testBoot == fleet.ShipArraylist.get(i).getLengte()) {
                switch (testBoot) {
                    case 2:
                        if (patrouilleCounter == 3) {
                            return false;
                        }
                        ++patrouilleCounter;
                        break;
                    case 3:
                        if (onderzeeCounter == 2) {
                            return false;
                        }
                        ++onderzeeCounter;
                        break;
                    case 4:
                        if (slagCounter == 1) {
                            return false;
                        }
                        ++slagCounter;
                        break;
                    case 6:
                        return false;
                }
            }
        }
        return true;
    }

    //This function is meant to stop the placement by saying if the chosen location is possible location to place a boat. We could possibly use this with the randomization to check in between randoms?
    public boolean validityChecker(String richting, int lengte, Coordinaat coordinaat) {
        testCoordinates.DeleteList();
        if (richting.equals("horizontaal")) {
            if (coordinaat.getX() + lengte > 11) {
                gui.showMessage("invalid", "U hebt een ongeldige schippositie ingegeven (Buiten het speelveld), kies een andere locatie.");
                return false;
            }

            //overlapping (Array of coordinaten aanmaken in classe Coordinaten)
            //Gebruik hitdetectie om ervoor te zorgen dat we weten of we een schip gaan raken met onze coordinaten
            //We itereren dus door onze testcoordinaten en zenden deze naar de hitdetectie, wanneer er 1 true terugkomt weten we dat de boot overlapt
            //for (int k = -1; k < 1; k++){
            for (int i = 0; i < lengte; i++) {
                testCoordinates.CreateCoordinate(coordinaat.getX() + i, coordinaat.getY(), coordinaat.getGebied());
                if (zoneSeeker(testCoordinates.CoordinateArraylist.get(i), coordinaat.getGebied())) {
                    return false;
                }
                if (hitDetection(testCoordinates.CoordinateArraylist.get(i), coordinaat.getGebied())) {
                    return false;
                }
            }
        }
        if (richting.equals("vertikaal")) {
            if (coordinaat.getY() + lengte > 11) {
                gui.showMessage("invalid", "U hebt een ongeldige schippositie ingegeven (Buiten het speelveld), kies een andere locatie.");
                return false;
            }
            for (int i = 0; i < lengte; i++) {
                testCoordinates.CreateCoordinate(coordinaat.getX(), coordinaat.getY() + i, coordinaat.getGebied());
                if (zoneSeeker(testCoordinates.CoordinateArraylist.get(i), coordinaat.getGebied())) {
                    return false;
                }
                if (hitDetection(testCoordinates.CoordinateArraylist.get(i), coordinaat.getGebied())) {
                    return false;
                }
            }
        }
        return true;
    }


    public void bombAttack(Coordinaat coordinaat, GuiGebied gebied) {
        gui.wait(200);
        if (hitDetection(coordinaat, gebied)) {
            gui.tekenIcoon(coordinaat, ZeeslagIcon.HIT);
            if (gebied.equals(GuiGebied.EIGEN)) {
                enemyHitRemember.CreateCoordinate(coordinaat);
            } else {
                playerHitRemember.CreateCoordinate(coordinaat);
            }
            System.out.println("hit");
        } else {
            gui.tekenIcoon(coordinaat, ZeeslagIcon.MISS);
            System.out.println("mis");
        }
    }

    //Op basis van een Coordinaat kunnen we zien of we een schip raken of niet. Dit kan mogelijk handig zijn voor de bootzinker functie?
    public Boolean hitDetection(Coordinaat coordinaat, GuiGebied gebied) {
        if (gebied.equals(GuiGebied.VIJAND)) {
            for (int i = 0; i < enemyFleet.ShipArraylist.size(); i++) {
                try {
                    //We have to iterate through all the coordinates of a certain ship to check if a ship is hit
                    for (int k = 0; k < enemyFleet.ShipArraylist.get(i).getCoordinates().CoordinateArraylist.size(); k++) {
                        Coordinaat c = new Coordinaat(enemyFleet.ShipArraylist.get(i).getCoordinates().CoordinateArraylist.get(k).getX(), enemyFleet.ShipArraylist.get(i).getCoordinates().CoordinateArraylist.get(k).getY(), enemyFleet.ShipArraylist.get(i).getCoordinates().CoordinateArraylist.get(k).getGebied());
                        if (coordinaat.equals(c)) {
                            return true;
                        }
                    }
                } catch (OngeldigeCoordinaatException e) {
                    System.out.println("Dit zou niet mogen voorkomen maar toch:");
                    e.printStackTrace();
                }
            }
        }
        if (gebied.equals(GuiGebied.EIGEN)) {
            //We iterate through all the ships
            for (int i = 0; i < playerFleet.ShipArraylist.size(); i++) {
                try {
                    //We have to iterate through all the coordinates of a certain ship to check if a ship is hit
                    for (int k = 0; k < playerFleet.ShipArraylist.get(i).getCoordinates().CoordinateArraylist.size(); k++) {
                        Coordinaat c = new Coordinaat(playerFleet.ShipArraylist.get(i).getCoordinates().CoordinateArraylist.get(k).getX(), playerFleet.ShipArraylist.get(i).getCoordinates().CoordinateArraylist.get(k).getY(), playerFleet.ShipArraylist.get(i).getCoordinates().CoordinateArraylist.get(k).getGebied());
                        if (coordinaat.equals(c)) {
                            //System.out.println("We should get here");
                            return true;
                        }
                    }
                } catch (OngeldigeCoordinaatException e) {
                    System.out.println("Dit zou niet mogen voorkomen maar toch:");
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    //Boolean that has to calculate if all coordinates of a given ship are hit and if so has to return true;
    public Boolean sunkenShipDetection(Ship ship) {
        int hitCounter = 0;
        for (int i = 0; i < ship.getCoordinates().CoordinateArraylist.size(); i++) {
            if (ship.getCoordinates().CoordinateArraylist.get(i).getGebied().equals(GuiGebied.VIJAND)) {
                if (playerHitRemember.CoordinateArraylist.contains(ship.getCoordinates().CoordinateArraylist.get(i))) {
                    hitCounter++;
                }
            } else {
                if (enemyHitRemember.CoordinateArraylist.contains(ship.getCoordinates().CoordinateArraylist.get(i))) {
                    hitCounter++;
                }
            }
        }
        if (hitCounter == ship.getLengte()) {
            for (int i = 0; i < ship.getCoordinates().CoordinateArraylist.size(); i++) {
                if (ship.getCoordinates().CoordinateArraylist.get(i).getGebied().equals(GuiGebied.VIJAND)) {
                    enemyFleet.ShipArraylist.remove(ship);
                } else {
                    playerFleet.ShipArraylist.remove(ship);
                }
            }
            return true;
        }
        return false;
    }

    public void createValidRandomShip() {
        int counter = 1;
        GuiGebied gebied = GuiGebied.VIJAND;
        try {

            int xWaarde = randInt(1, 10);
            int yWaarde = randInt(1, 10);
            int length;

            String[] richting = new String[]{
                    "vertikaal", "horizontaal"
            };
            String direction = richting[randInt(0, richting.length - 1)];
            int[] lengte = new int[]{
                    2, 3, 4, 6
            };
            length = lengte[randInt(0, lengte.length - 1)];

            switch (direction) {
                case "vertikaal":
                    yWaarde = randInt(1, 10 - length);
                    break;
                case "horizontaal":
                    xWaarde = randInt(1, 10 - length);
                    break;
            }

            Coordinaat coordinaat = new Coordinaat(xWaarde, yWaarde, gebied);

            for (int i = 0; i < length; i++) {
                if (validityChecker(direction, length, coordinaat)) {
                            if (shipIdentifier(enemyFleet, length)) {
                                if (counter == length) {
                                    System.out.println("we here");
                                    enemyFleet.CreateShip(direction, length, coordinaat);
                                    //handleDrawing(enemyFleet.ShipArraylist.get(enemyFleet.ShipArraylist.size() - 1), gebied);
                                    //System.out.println(enemyFleet.ShipArraylist.size());
                                }
                            }
                    counter++;}
                }
        } catch (OngeldigeCoordinaatException e) {
            System.out.println("Dit zou niet mogen voorkomen maar toch:");
            e.printStackTrace();
        }
    }

    public static int randInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    public boolean zoneSeeker(Coordinaat testCoordinaat, GuiGebied gebied){

        for (int xDisplacement = -1; xDisplacement < 2; xDisplacement++){
            for (int yDisplacement = -1; yDisplacement < 2; yDisplacement++){
                int xCoordinaat;
                int yCoordinaat;
                if (testCoordinaat.getX()+xDisplacement < 1 || testCoordinaat.getX()+xDisplacement > 10){
                    xCoordinaat = testCoordinaat.getX();
                }
                else{
                    xCoordinaat = testCoordinaat.getX()+xDisplacement;
                }
                if (testCoordinaat.getY()+yDisplacement < 1 || testCoordinaat.getY()+yDisplacement>10){
                    yCoordinaat = testCoordinaat.getY();
                }
                else{
                    yCoordinaat = testCoordinaat.getY() + yDisplacement;
                }
                try{
                Coordinaat coordinaat = new Coordinaat(xCoordinaat,yCoordinaat,gebied);
                if (hitDetection(coordinaat,gebied)){
                    return true;
                }
                }catch(OngeldigeCoordinaatException e){
                    System.out.println("Dit zou niet mogen voorkomen maar toch:");
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}