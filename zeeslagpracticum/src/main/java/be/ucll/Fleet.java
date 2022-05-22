package be.ucll;

import be.ucll.guicore.OngeldigeCoordinaatException;
import be.ucll.zeeslaggui.Coordinaat;

import java.util.ArrayList;

public class Fleet {
    ArrayList<Ship> ShipArraylist = new ArrayList<>();

    public void CreateShip(String richting, int lengte, Coordinaat begincoordinaat){
        CoordinateRecorder boatRecorder = new CoordinateRecorder();
        try {
            Coordinaat eindCoordinaat = begincoordinaat;
            for (int i = 0; i < lengte; i++) {
                if (richting.equals("horizontaal")) {
                    eindCoordinaat = new Coordinaat(begincoordinaat.getX() + i, begincoordinaat.getY(), begincoordinaat.getGebied());
                }
                if (richting.equals("vertikaal")) {
                    eindCoordinaat = new Coordinaat(begincoordinaat.getX(), begincoordinaat.getY() + i, begincoordinaat.getGebied());
                }
                boatRecorder.CoordinateArraylist.add(eindCoordinaat);
            }
        }catch(OngeldigeCoordinaatException e){
            System.out.println("Dit zou niet mogen voorkomen maar toch:");
            e.printStackTrace();
        }
        ShipArraylist.add(new Ship(richting,lengte,boatRecorder));
    }

    public void DeleteList(){ShipArraylist.clear();}

}























/*package be.ucll;

import java.util.ArrayList;

public class Fleet {

    ArrayList<Ship> ShipArraylist = new ArrayList<>();

    private Ship[] schepen;

    /*public Fleet(Ship[] schepen){
        this.schepen = schepen;
    }

    public void CreateShip(String richting, int xWaarde, int yWaarde, int lengte)
    {
        ShipArraylist.add(new Ship(richting,xWaarde,yWaarde,lengte));
    }
    public void CreateShip(Ship ship)
    {
        int lengte = ship.getLengte();
        String richting = ship.getRichting();
        int xWaarde = ship.getxWaarde();
        int yWaarde = ship.getyWaarde();
        ShipArraylist.add(new Ship(richting,xWaarde,yWaarde,lengte));
    }

    public void DeleteList(){ShipArraylist.clear();}

    /*public Coordinaten[] geefCoordinaten(){
        Coordinaten c = new Coordinaten();
        return null;
    }

    /*
    Create randomFleet()

}
*/