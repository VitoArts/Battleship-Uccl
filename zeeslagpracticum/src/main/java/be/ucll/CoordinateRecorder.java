package be.ucll;

import be.ucll.guicore.OngeldigeCoordinaatException;
import be.ucll.zeeslaggui.Coordinaat;
import be.ucll.zeeslaggui.GuiGebied;
import java.util.ArrayList;
import java.util.Random;

public class CoordinateRecorder {
    ArrayList<Coordinaat> CoordinateArraylist = new ArrayList<>();

    public void CreateCoordinate(int xWaarde, int yWaarde, GuiGebied gebied)
    {
        try {
            CoordinateArraylist.add(new Coordinaat(xWaarde, yWaarde, gebied));
        }catch (OngeldigeCoordinaatException e){
            System.out.println("Dit zou niet mogen voorkomen maar toch:");
            e.printStackTrace();
        }
    }

    public void CreateCoordinate(Coordinaat coordinaat)
    {
        try {
            CoordinateArraylist.add(new Coordinaat(coordinaat.getX(), coordinaat.getY(), coordinaat.getGebied()));
        }catch (OngeldigeCoordinaatException e){
            System.out.println("Dit zou niet mogen voorkomen maar toch:");
            e.printStackTrace();
        }
    }

    public void DeleteList(){CoordinateArraylist.clear();}

}




















/*package be.ucll;

import be.ucll.guicore.OngeldigeCoordinaatException;
import be.ucll.zeeslaggui.Coordinaat;
import be.ucll.zeeslaggui.GuiGebied;

import java.util.ArrayList;
import java.util.Random;

public class CoordinateRecorder {

    ArrayList<Coordinaat> coordinateArrayList = new ArrayList<>();

    private int xWaarde;
    private int yWaarde;
    private GuiGebied veld;
    //private Coordinaat c = null;
    private int schipNumber;

    public void createCoordinate(int xWaarde, int yWaarde, GuiGebied veld) {
        this.xWaarde = xWaarde;
        this.yWaarde = yWaarde;
        this.veld = veld;
        try {
            Coordinaat c = new Coordinaat(xWaarde, yWaarde, veld);
            coordinateArrayList.add(c);
        } catch (OngeldigeCoordinaatException e) {
            System.out.println("Ongeldige coordinaten");
            e.printStackTrace();
        }
    }

    public void createShipCoordinate(Coordinaat c, int schipNumber) {
        this.veld = veld;
        this.schipNumber = schipNumber;
        coordinateArrayList.add(c);
    }

    public void DeleteList() {
        coordinateArrayList.clear();
    }

    public void createRandomStartLocations(GuiGebied veld) {
        for (int i = 0; i < 10; i++) {
            createCoordinate(randInt(1, 10), randInt(1, 10), veld);
        }
    }

    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        //System.out.println(randomNum);
        return randomNum;
    }

    public int getyWaarde() {
        return yWaarde;
    }

    public int getxWaarde() {
        return xWaarde;
    }

    public int getSchipNumber() {
        return schipNumber;
    }
}*/