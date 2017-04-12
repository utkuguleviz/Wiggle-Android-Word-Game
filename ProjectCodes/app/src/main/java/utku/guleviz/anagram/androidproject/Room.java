package utku.guleviz.anagram.androidproject;


public class Room
{
    private String name;
    private int numberOfRoom;

    public Room(String name, int numberOfRoom) {
        super();
        this.name = name;
        this.numberOfRoom = numberOfRoom;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberoOfRoom() {
        return numberOfRoom;
    }

    public void setNumberOfRoom(int numberOfRoom) {
        this.numberOfRoom = numberOfRoom;
    }
}