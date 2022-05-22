package be.ucll;

public class Ship {

    private String richting;
    private int lengte;
    private CoordinateRecorder coordinaten;

    public Ship(String richting, int lengte, CoordinateRecorder coordinaten){
        this.richting = richting;
        this.lengte = lengte;
        this.coordinaten = coordinaten;
    }

    public int getLengte() {
        return lengte;
    }

    public CoordinateRecorder getCoordinates()
    {
        return coordinaten;
    }

    public String getRichting() {
        return richting;
    }
}































/*package be.ucll;

import be.ucll.guicore.OngeldigeCoordinaatException;
import be.ucll.zeeslaggui.Coordinaat;
import be.ucll.zeeslaggui.GuiGebied;
import sun.security.util.Length;

public class Ship {

    private String richting;
    private int xWaarde;
    private int yWaarde;
    private int lengte;
    private String shipType;
    private Coordinaat coordinaat;

    public Ship(String richting, int xWaarde, int yWaarde, int lengte){
        this.richting = richting;
        this.xWaarde = xWaarde;
        this.yWaarde = yWaarde;
        this.lengte = lengte;
    }

    public int getLengte() {
        return lengte;
    }

    public int getxWaarde() {
        return xWaarde;
    }

    public int getyWaarde() {
        return yWaarde;
    }

    public String getRichting() {
        return richting;
    }

    public String getShipType(){
        switch (lengte){
            case 2: shipType = "patrouilleschip";
            break;
            case 3: shipType = "onderzeeer";
            break;
            case 4: shipType = "slagschip";
            break;
            case 6: shipType = "Vliegdekschip";
        }
        return shipType;
    }

    public Coordinaat getCoordinaat(GuiGebied gebied){
        try {
            Coordinaat coordinaat = new Coordinaat(xWaarde, yWaarde, gebied);
            return coordinaat;
        }catch (OngeldigeCoordinaatException e){
        }
        return coordinaat;
    }
}
*/