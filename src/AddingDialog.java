import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Tomasz Pilarczyk
 */
class AddingDialog extends Frame {

    private static JTextField nameTextField;
    private static JTextField commandTextField;
    private static JButton okButton;
    private static JLabel nameLabel;
    private static JLabel commandLabel;
    private static FluxboxManager.Request requestType;



    AddingDialog() {
        setupComponents();
        
        nameTextFieldListener();
        commandTextFieldListener();
        okButtonListener();
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false);
            }
        });
        
        windowSettings();
        addComponents();

        
    }

    private void setupComponents(){
        nameLabel = new JLabel("Name");
        nameLabel.setBounds(165,25,40,20);

        commandLabel = new JLabel("Command");
        commandLabel.setBounds(155,78,80,20);

        nameTextField = new JTextField();
        nameTextField.setBounds(50,50,300,20);
        nameTextField.setFocusTraversalKeysEnabled(false);

        commandTextField = new JTextField();
        commandTextField.setBounds(50,100,300,40);

        okButton = new JButton("OK");
        okButton.setBounds(155,150,80,30);
    }

    private void nameTextFieldListener(){
        nameTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {}

            @Override
            public void keyPressed(KeyEvent keyEvent) {}

            @Override
            public void keyReleased(KeyEvent keyEvent) {
                if(keyEvent.getKeyCode()==keyEvent.VK_TAB) commandTextField.requestFocus();
                if(requestType== FluxboxManager.Request.ADD_SUBMENU && keyEvent.getKeyCode()==keyEvent.VK_ENTER){
                    addSubMenu();
                    setVisible(false);
                }

            }
        });
    }

    private void commandTextFieldListener(){
        commandTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {}

            @Override
            public void keyPressed(KeyEvent keyEvent) {}

            @Override
            public void keyReleased(KeyEvent keyEvent) {
                if(keyEvent.getKeyCode()==keyEvent.VK_ENTER){
                    addItem();
                }
            }
        });
    }

    private void okButtonListener(){
        okButton.addActionListener(actionEvent -> addItem());
    }

    private void addItem(){
        if(requestType== FluxboxManager.Request.ADD_EXEC)addExec();
        if(requestType== FluxboxManager.Request.ADD_SUBMENU)addSubMenu();
        if(requestType== FluxboxManager.Request.EDIT_EXEC)editExec();
        if(requestType== FluxboxManager.Request.EDIT_SUBMENU)editSubmenu();
        setVisible(false);
    }

    private void windowSettings(){
        setTitle("AddingDialog box");
        setSize(400, 200);
        setLayout(null);
        setVisible(false);
    }
    
    private void addComponents(){
        add(nameTextField);
        add(nameLabel);
        add(commandTextField);
        add(commandLabel);
        add(okButton);
    }

    void openBox(FluxboxManager.Request request)
    {
        requestType=request;
        prepareFields(request);
        Point parentLocation= FluxboxManager.frame.getLocation();
        setLocation(parentLocation.x+75, parentLocation.y+150);
        setVisible(true);
        nameTextField.requestFocus(true);

    }

    private static void prepareFields(FluxboxManager.Request request) throws ArrayIndexOutOfBoundsException{
        String itemString;
        String nameString;

        nameTextField.setText("");
        commandTextField.setText("");
        switch(request){
            case ADD_SUBMENU:
                commandTextField.setEditable(false);
                break;
            case ADD_EXEC:
                commandTextField.setEditable(true);
                break;
            case EDIT_SUBMENU:
                itemString=ItemList.itemListModel.getElementAt(ItemList.jList.getSelectedIndex());
                nameString=itemString.substring(itemString.indexOf("(")+1,itemString.indexOf(")"));
                nameTextField.setText(nameString);
                commandTextField.setText("");
                commandTextField.setEditable(false);
                break;
            case EDIT_EXEC:
                itemString=ItemList.itemListModel.getElementAt(ItemList.jList.getSelectedIndex());
                nameString=itemString.substring(itemString.indexOf("(")+1,itemString.indexOf(")"));
                String commandString = itemString.substring(itemString.indexOf("{") + 1, itemString.indexOf("}"));
                nameTextField.setText(nameString);
                commandTextField.setText(commandString);
                commandTextField.setEditable(true);
                break;
            default:
                commandTextField.setEditable(false);
                nameTextField.setEditable(false);
                break;
        }

    }

    private static void addExec(){
        String text="";
        text+= addParagraph();
        text+="(";
        text+= nameTextField.getText();
        text+=") {";
        text+= commandTextField.getText();
        text+="}";

        ItemList.itemListModel.add(ItemList.jList.getSelectedIndex()+1,text);
    }

    private  static String addParagraph(){
        String spaces="";
        String selectedItem="";
        int index = ItemList.jList.getSelectedIndex();
        if(ItemList.itemListModel.size()>0) {
            selectedItem=ItemList.itemListModel.get(index);
        }
        if(belongsToSubmenu(selectedItem)){
            for(int i=0;i<countSpaces(selectedItem);i++)spaces+=" ";
            if(selectedItem.contains("[submenu]") && requestType != FluxboxManager.Request.EDIT_SUBMENU)spaces+="   ";
            if(requestType == FluxboxManager.Request.ADD_EXEC || requestType == FluxboxManager.Request.EDIT_EXEC){
                spaces+="->";
            }
        }

        return spaces;
    }

    private static boolean belongsToSubmenu(String selected){
        return selected.contains("->") || selected.contains("[submenu]");
    }

    private static int countSpaces(String text){
        int count=0;
        for(int i=0;i<text.length();i++){
            if(Character.isWhitespace(text.charAt(i)))count++;
            else break;
        }
        return count;
    }

    private static void addSubMenu(){
        String text="";
        text+= addParagraph();
        text+="[submenu] (";
        text+= nameTextField.getText();
        text+=")";
        ItemList.itemListModel.add(ItemList.jList.getSelectedIndex()+1,text);
        text=addParagraph();
        ItemList.itemListModel.add(ItemList.jList.getSelectedIndex()+2,text+"[end]");


    }

    private static void editSubmenu(){
        String text="";
        text+= addParagraph();
        text+="[submenu] (";
        text+= nameTextField.getText();
        text+=")";
        int index=ItemList.jList.getSelectedIndex();
        ItemList.itemListModel.remove(index);
        ItemList.itemListModel.add(index,text);

    }

    private static void editExec() {
        String text="";
        text+= addParagraph();
        text+="(";
        text+= nameTextField.getText();
        text+=") {";
        text+= commandTextField.getText();
        text+="}";

        int index=ItemList.jList.getSelectedIndex();
        ItemList.itemListModel.remove(index);
        ItemList.itemListModel.add(index,text);
    }

}
