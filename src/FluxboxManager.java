import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.*;


/**
 * @author Tomasz Pilarczyk
 */
public class FluxboxManager {

    static Frame frame =new Frame("FluxboxManager");
    private static ItemList itemList = new ItemList();
    private static JButton execButton;
    private static JButton submenuButton;
    private static JButton separatorButton;
    private static JButton loadButton;
    private static JButton saveButton;
    private static JButton deleteButton;
    private static JButton editButton;
    private static AddingDialog addingDialog;

    enum Request {
        ADD_EXEC, ADD_SUBMENU,EDIT_SUBMENU,EDIT_EXEC
    }

    public static void main(String args[]) {
        addingDialog = new AddingDialog();
        setupComponents();

        execButtonListener();
        submenuButtonListener();
        separatorButtonListener();
        loadButtonListener();
        saveButtonListener();
        deleteButtonListener();
        editButtonListener();
        windowListener();

        addComponents();
        windowSettings();
    }

    private static void setupComponents(){
        execButton = new JButton("Exec");
        execButton.setBounds(420,35,120,30);
        submenuButton = new JButton("Submenu");
        submenuButton.setBounds(420,75,120,30);
        separatorButton = new JButton("Separator");
        separatorButton.setBounds(420,115,120,30);
        loadButton = new JButton("Load");
        loadButton.setBounds(420,390,120,30);
        saveButton = new JButton("Save");
        saveButton.setBounds(420,430,120,30);
        deleteButton = new JButton("Delete");
        deleteButton.setBounds(420,205,120,30);
        editButton = new JButton("Edit");
        editButton.setBounds(420,245,120,30);
    }

    private static void execButtonListener(){
        execButton.addActionListener(actionEvent ->
                addingDialog.openBox(Request.ADD_EXEC));
    }

    private static void submenuButtonListener(){
        submenuButton.addActionListener(actionEvent ->
                addingDialog.openBox(Request.ADD_SUBMENU));
    }

    private static void separatorButtonListener(){
        separatorButton.addActionListener(actionEvent ->
                ItemList.itemListModel.add(ItemList.jList.getSelectedIndex()+1,"------------------------------------------------"));
    }

    private static void loadButtonListener(){
        loadButton.addActionListener(actionEvent -> {
            try {
                FileOperations.loadMenuFile();
            }catch (IOException err){
                JOptionPane.showMessageDialog(null, "Can't loadButton file");
            }
        });
    }

    private static void saveButtonListener(){
        saveButton.addActionListener(actionEvent -> {
            try {
                FileOperations.saveMenuFile();
            }catch(IOException err){
                JOptionPane.showMessageDialog(null, "Can't saveButton file");
            }
        });
    }

    private static void deleteButtonListener(){
        deleteButton.addActionListener(actionEvent -> {
                try {
                    int selectedItemIndex = ItemList.jList.getSelectedIndex();
                    String itemName = ItemList.itemListModel.get(selectedItemIndex);

                    if (!itemName.contains("[submenu]")) {
                        deleteItem(selectedItemIndex);
                    } else {
                        deleteSubmenu(itemName, selectedItemIndex);
                    }

                    ItemList.jList.setSelectedIndex(selectedItemIndex);

                }catch(ArrayIndexOutOfBoundsException err){
                    JOptionPane.showMessageDialog(null, "No field is selected");
                }
        });
    }

    private static void deleteItem(int index){
        try {
            ItemList.itemListModel.remove(index);
        }catch(ArrayIndexOutOfBoundsException err){
            JOptionPane.showMessageDialog(null, "Nothing more to delete");
        }
    }

    private static void deleteSubmenu(String itemName,int itemIndex){
        while (!itemName.contains("[end]")) {
            deleteItem(itemIndex);
            itemName = ItemList.itemListModel.get(itemIndex);
        }
        deleteItem(itemIndex);
    }

    private static void editButtonListener(){
        editButton.addActionListener(actionEvent -> {
            try {
                int selectedItemIndex = ItemList.jList.getSelectedIndex();
                String itemName = ItemList.itemListModel.getElementAt(selectedItemIndex);

                if (itemName.contains("[submenu]"))
                    addingDialog.openBox(Request.EDIT_SUBMENU);
                else
                    addingDialog.openBox(Request.EDIT_EXEC);

            }catch(ArrayIndexOutOfBoundsException err){
                JOptionPane.showMessageDialog(null, "No field is selected");
            }
        });
    }

    private static void windowListener(){
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
    private static void addComponents() {
        frame.add(itemList);
        frame.add(execButton);
        frame.add(submenuButton);
        frame.add(separatorButton);
        frame.add(saveButton);
        frame.add(loadButton);
        frame.add(deleteButton);
        frame.add(editButton);
    }

    private static void windowSettings(){
        frame.setLayout(null);

        frame.setFocusable(true);
        frame.setSize(550,500);
        frame.setLocation(400,200);
        frame.requestFocus();
        frame.setVisible(true);
    }
}

