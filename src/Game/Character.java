package Game;
import Battle.Battle;
import java.util.HashMap;
import java.util.Scanner;

import static Game.MapCreation.roomList;

import Items.Consumable;
import Items.Equipment;
import Items.Item;
import Monsters.Monster;


// simple comment
public class Character {

    private int health = 10;
    private int currentLife = 10;
    private int armor = 1;
    private int dmg = 5;
    private int level = 1;
    private int xp = 0;
    private int xpToNextLvl = 30;
    private HashMap<String, Equipment> equipment = new HashMap<>();
    private HashMap<String, Item> inventory = new HashMap<>();


    /*
    *  Prints equipment
    */
    public void printEquipment() {
        String equiped = "";
        try {
            for (String key : equipment.keySet()) {
                if (!key.equals(""))
                    equiped += key + "(armor:" + equipment.get(key).getExtraArmor() +
                                     ",dmg:" + equipment.get(key).getExtraDmg() + ") ";
            }
            if (!equiped.equals("")) {
                equiped = equiped.replace("_"," ");
                System.out.println("Φοράς: " + equiped + ".");
            }
        } catch (Exception e) {
        }
    }

    /*
     * Removes equipment from player
     */
    public void unequipItem(String itemToUnequip){
        itemToUnequip = itemToUnequip.replaceAll("UNEQUIP ", "");
        if (!itemToUnequip.equals(""))
            itemToUnequip = findItemName(itemToUnequip, equipment.toString());

        Scanner myVar = new Scanner(System.in);
        if (!itemToUnequip.equals("")) {
            this.armor -= equipment.get(itemToUnequip).getExtraArmor();
            this.dmg -= equipment.get(itemToUnequip).getExtraDmg();

            System.out.println("You want to STORE it in your inventory or you want to drop it on the floor?");
            System.out.println("You can also DISCARD it.");
            String answer = myVar.nextLine().toUpperCase();
            // valto sto inventory
            if (answer.equals("STORE")) {
                inventory.put(itemToUnequip, equipment.get(itemToUnequip));
                equipment.remove(itemToUnequip);
                System.out.println("Unequiped and stored in inventory.");
            } else if (answer.equals("DISCARD")) {
                equipment.remove(itemToUnequip);
                System.out.println("Unequiped and destroyed.");
            }
            // allios tha to petakseis sto edafos
            else {
                roomList.get(Room.activeRoom).itemMap.put(itemToUnequip, equipment.get(itemToUnequip));
                equipment.remove(itemToUnequip);
                System.out.println("Unequiped and dropped on the floor.");
            }
        }
    }

    /*
     * Foraei opla kai panoplies pou uparxoun sto domatio i sto inventory
     */
    public void equip(String itemName) {
        itemName = itemName.toLowerCase();
        itemName = itemName.replace("equip ", "");
        String fullName = findItemName(itemName, roomList.get(Room.activeRoom).itemMap.toString());

        if (fullName.equals("")) {
            fullName = findItemName(itemName, inventory.toString());
            if(!fullName.equals(""))
                equipOperation(itemName, fullName, inventory);
            else
                System.out.println(itemName + " not found");
        }
        else
            equipOperation(itemName, fullName, roomList.get(Room.activeRoom).itemMap);
    }


    /*
     * gia na min kanei 2 fores elegxous kai idies diadikasies gia to inventory kai gia ta items pou
     * uparxoun sto domatio ekana autin tin methodo
     */
    public void equipOperation(String itemName, String fullName, HashMap<String, Item> eMap){
        try {
            // an to item einai tupou equipment
            if (eMap.get(fullName).getClass().getSimpleName().equals("Equipment")) {
                // an den foras idi eksoplismo idiou tupoy p.x. armor forese ton
                if (!equipment.keySet().toString().contains(itemName)) {
                    equipment.put(fullName, (Equipment) eMap.get(fullName));
                    this.armor += equipment.get(fullName).getExtraArmor();
                    this.dmg += equipment.get(fullName).getExtraDmg();
                    eMap.remove(fullName);
                    System.out.println(fullName + " equiped");
                }
                // an foras idi eksoplismo
                else {
                    System.out.println("You already wear " + itemName);
                    System.out.println("You want to replace it?");
                    Scanner myVar = new Scanner(System.in);
                    // an thes na to antikatastiseis
                    if (myVar.nextLine().toUpperCase().equals("YES")) {
                        unequipItem(itemName);
                        equipOperation(itemName, fullName, eMap);
                    }
                }
            } else
                System.out.println("Item can't be equiped");
        }
        catch (Exception e){}
    }


    /*
     * @param shortened String of Equipment and returns complete name of Equipment
     * vriskei to String "***_armor"
     */
    public String findItemName(String itemName, String mapString) {
        itemName = itemName.toLowerCase();
        mapString = mapString.toLowerCase();
        try {
            int a = mapString.indexOf(itemName);
            while (true) {
                a--;
                if (mapString.charAt(a) == ' ' || mapString.charAt(a) == '{')
                    break;
            }
            a++;
            mapString = mapString.substring(a, mapString.indexOf(itemName));
            mapString = mapString.concat(itemName);
            return mapString;
        }
        catch(Exception e){
            return "";
        }
    }


    /*
     * Drinks potion and restores health
     */
    public void consumeItem(String potionName){
        potionName = potionName.toLowerCase();
        potionName = potionName.replaceAll("consume ", "");
        if(inventory.containsKey(potionName)){
            if(inventory.get(potionName).getClass().getSimpleName().equals("Consumable")){
                int restoredHealth = ((Consumable) inventory.get(potionName)).getRestoreHealth();
                this.currentLife += restoredHealth;
                if(currentLife>health)
                    currentLife = health;
                inventory.remove(potionName);
                System.out.println("You restored " + restoredHealth + " health");
            }
        }
    }
    
    /*
     * Apothikeuei Items sto Inventory
     */
    public void storeItem(String itemName){
        itemName = itemName.toLowerCase();
        itemName = itemName.replace("pick ", "");
        if(roomList.get(Room.activeRoom).itemMap.containsKey(itemName)){
            inventory.put(itemName,roomList.get(Room.activeRoom).itemMap.get(itemName));            
            roomList.get(Room.activeRoom).itemMap.remove(itemName);
            System.out.println(itemName + " stored in inventory");
        }
        else
            System.out.println(itemName + " not found");
    }

    /*
     * Rixnei items ap to inventory
     */
    public void dropItem(String itemName){
        itemName = itemName.toLowerCase();
        itemName = itemName.replace("drop ", "");
        if(inventory.containsKey(itemName)){
            roomList.get(Room.activeRoom).itemMap.put(itemName, inventory.get(itemName));
            inventory.remove(itemName);
            System.out.println("You droped " + itemName);
        }
    }
    
    /*
     * Prints Items in Inventory
     */
    public void viewInventory(){
        String inventoryItems = "";
        for(String key : this.inventory.keySet())
               inventoryItems += key + " ";
        System.out.println("Έχεις στο σάκο σου: " + inventoryItems);
    }
    
           
    
    /*
     * Ksekleidonei tin porta an exeis to katallilo kleidi,
     * (H porta einai kleidomeni me kodiko)
     */
    public void unlockDoor(String orientation){
        orientation = orientation.replace("UNLOCK ", "");
        Integer index = roomList.get(Room.activeRoom).getNextDoorIndex(orientation);
        if (index == null) index = 0;
        for(String key : this.inventory.keySet()){
            if((key.contains("key")) && (this.inventory.get(key).getIndexToUnlock().equals(index))){
                    roomList.get(index).removeLock(index);
                    System.out.println("You unlocked the room!");
            }
        }
    }

    /*
     * Kanei epithesi se teras an uparxei sto domatio kai mpainei stin klasi battle
     * TODO: na epilegei poio teras tha epitethei
     */
    public void attack(Monster monster){
        Battle fight = new Battle();
        int result = fight.enterBattle(this, monster);
        if (currentLife <= 0)
            System.out.println("You died!");
        else{
            System.out.print("You killed the " + monster.getClass().getSimpleName());
            System.out.println(" and gained " + monster.getXp() + " xp");
            addXp(result);
        }
    }

    /*
     * Prosthetei xp ston paikti
     */
    public void addXp(int experience){
        xp += experience;
        if (xp >= xpToNextLvl){
            int exp = xp - xpToNextLvl;
            levelUp(exp);
        }
    }

    /*
     * Pairnei tin ananeomeni zoi tou paikti ap tin maxi pou egine
     */
    public void refreshHealth(int health){
        this.currentLife = health;        
    }

    /*
     * Pernaei sto epomeno epipedo kai allazei stats tou paikti
     */
    public void levelUp(int exp){
        level++;
        dmg++;
        health += 5;
        xp = exp;
        currentLife = health;
        xpToNextLvl += 15;
    }

   /*
    * Get methods
    */
    public int getCurrentLife(){
        return currentLife;
    }

    public int getDmg(){
        return dmg;
    }
    
    public int getArmor(){
        return armor;
    }

    /*
     * Ektiponei ta stats tou paikti
     */
    public void printStats(){
        System.out.println("Current Health: " + this.currentLife);
        System.out.println("Damage: " + this.dmg);
        System.out.println("Armor: " + this.armor);
        System.out.println("Current Level: " + this.level);
        System.out.println("Current experience: " + this.xp);
        System.out.println("Experience to next Level: " + (this.xpToNextLvl - this.xp));
    }


}
