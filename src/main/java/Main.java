import java.util.*;

import com.google.gson.Gson;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Main {

    static final String FILENAME = "todo.json";

    static Input userInput = new Input();
    static ToDoCollection toDo = new ToDoCollection();

    public static ToDoCollection load(String filename) throws IOException {
        Gson gson = new Gson();
        FileReader reader = new FileReader(filename);
        try {
            return gson.fromJson(reader, ToDoCollection.class);
        }
        finally {
            reader.close();
        }
    }


    public static void save(String filename, ToDoCollection data) throws IOException {
        Gson gson = new Gson();
        FileWriter writer = new FileWriter(filename);
        try {
            gson.toJson(data, writer);
        }
        finally {
            writer.close();
        }
    }

    public static void main(String[] args) {
        try {
            toDo = load(FILENAME);
            int choice = 1;
            while (choice != 0) {
                displayChoices();
                choice = userInput.promptInt("Enter an option: ");
                switch (choice) {
                    case 1:
                        addItem();
                        break;
                    case 2:
                        removeItem();
                        break;
                    case 3:
                        editItem();
                        break;
                    case 4:
                        listItems();
                        break;
                    case 0:
                        save(FILENAME, toDo);
                        break;
                    default:
                        System.out.println(choice + " is not an option.");
                }

            }

        }
        catch (InputMismatchException e) {
            System.out.println("Error: You have entered the wrong data type. Priorities & options can only be integers (whole numbers).");
        }
        catch (Exception e) {
            System.out.println("Error: Well, ya did something wrong.");
        }
    }

    static void displayChoices() {
        System.out.println("(1) Add a to-do item.");
        System.out.println("(2) Remove a to-do item.");
        System.out.println("(3) Edit a to-do item.");
        System.out.println("(4) List items.");
        System.out.println("(0) Exit.");
    }

    static void addItem() {
        String fixBug = userInput.promptString("");
        String title = userInput.promptString("To-do item title: ");
        String description = userInput.promptString("Describe the to-do item '" + title + "': ");
        int priority = userInput.promptInt("What is this item's priority? (1 highest to 5 lowest): ");
        while (priority>5 || priority<1) {
            priority = userInput.promptInt("The priority must be an integer from 1 to 5. What is this item's priority?: ");
        }

        toDo.add(new Item(title, description, priority));
    }

    static void removeItem() {
        String fixBug = userInput.promptString("");
        String itemToRemove = userInput.promptString("What is the title of the to-do item you want to remove?: ");
        boolean foundItem = false;
        for (Item item:toDo) {
            if (itemToRemove.toUpperCase().equals(item.getTitle().toUpperCase())) {
                System.out.println("The item titled '" + item.getTitle() + "' has been removed.");
                toDo.remove(item);

                foundItem = true;
                break;
            }
        }

        if (!foundItem) {
            System.out.println("No item in the to-do list has the title '" + itemToRemove + "'.");
        }
    }

    static void editItem() {
        String fixBug = userInput.promptString("");
        String itemToChange = userInput.promptString("What is the title of the to-do item you want to edit?: ");
        boolean foundItem = false;
        for (Item item:toDo) {
            if (itemToChange.toUpperCase().equals(item.getTitle().toUpperCase())){
                String newTitle = userInput.promptString("What do you want to make this item's title?: ");

                System.out.println("This item's current description is: '" + item.getDescription() + "'");
                String newDesc = userInput.promptString("What do you want to make this item's description?: ");

                System.out.println("This item's current priority is: '" + item.getPriority() + "'");
                int newPriority = userInput.promptInt("What do you want to make this item's priority?: ");

                while (newPriority<1 || newPriority>5){
                    newPriority = userInput.promptInt("Priorities must be from 1 to 5. What do you want to make this item's priority?: ");
                }

                item.setTitle(newTitle);
                item.setDescription(newDesc);
                item.setPriority(newPriority);

                foundItem = true;
                break;
            }
        }

        if (!foundItem) {
            System.out.println("No item in the to-do list has the title '" + itemToChange + "'.");
        }
    }

    static void listItems() {
        int priority = userInput.promptInt("What item priority (1-5) do you want to list (0 for all): ");
        int itemNum = 1;
        if (priority == 0){
            for (Item item : toDo) {
                System.out.println(itemNum + ". " + item.getTitle());
                System.out.println("   Description: " + item.getDescription());
                System.out.println("   Priority: " + item.getPriority());

                itemNum++;
            }
        }
        else if (priority>0 && priority<6){
            for (Item item : toDo) {
                if (item.getPriority() == priority) {
                    System.out.println(itemNum + ". " + item.getTitle());
                    System.out.println("   Description: " + item.getDescription());
                    System.out.println("   Priority: " + item.getPriority());

                    itemNum++;
                }
            }
        }
        else{
            System.out.println(priority + " is not a valid priority.");
        }
    }

}

class Input {
    Scanner scanner = new Scanner(System.in);

    public String promptString(String prompt) {
        System.out.print(prompt);
        String userString = scanner.nextLine();

        return userString;
    }

    public int promptInt(String prompt) {
        System.out.print(prompt);
        int userInt = scanner.nextInt();

        return userInt;
    }
}

class Item implements Comparable<Item> {
    private String title;
    private String description;
    private int priority;

    public Item(String title, String description, int priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

    @Override
    public int compareTo(Item o) {
        if (priority != o.priority) {
            return Integer.toString(priority).compareTo(Integer.toString(o.priority));
        }
        else {
            return title.compareTo(o.title);
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}

class ToDoCollection implements Iterable<Item>, Iterator<Item> {
    private List<Item> toDoList;
    private int currentItem;

    public ToDoCollection() {
        this.toDoList = new ArrayList<Item>();
    }

    @Override
    public Iterator<Item> iterator() {
        setCurrentItem(0);
        return this;
    }

    @Override
    public boolean hasNext() {
        return currentItem < toDoList.size();
    }

    @Override
    public Item next() {
        if (hasNext()) {
            int index = getCurrentItem();
            currentItem++;
            return toDoList.get(index);
        } else {
            return null;
        }
    }

    public void add(Item item) {
        toDoList.add(item);
        Collections.sort(toDoList);
    }

    public void remove(Item item) {
        toDoList.remove(item);
    }

    public int getCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(int currentItem) {
        this.currentItem = currentItem;
    }
}