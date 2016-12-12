import javax.swing.*;
import java.io.*;

/**
 * @author Tomasz Pilarczyk
 */
class FileOperations {

    private static int paragraphCounter=0;
    private static String menuFilePath = getHomePath()+"/.fluxbox/menu";
    private enum ItemTypes {
        EXEC,SEPARATOR,SUBMENU,SUBMENU_END,NONE
    }

    private static String getHomePath(){
        return System.getProperty( "user.home" );
    }

    static void loadMenuFile() throws IOException {
        String line;

        try {
            FileReader fileReader = new FileReader(menuFilePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            ItemList.itemListModel.removeAllElements();

            while((line = bufferedReader.readLine()) != null) {
                recognizeAndAddItem(line);
            }

            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Can't find \"~/.fluxbox/menu\" file");
        }

    }

    private static void recognizeAndAddItem(String line){
        if(line.contains("[exec]")){
            addItemToList(ItemTypes.EXEC,line);
        }
        else if(line.contains("[separator]")) {
            addItemToList(ItemTypes.SEPARATOR,line);
        }
        else if(line.contains("[submenu]")){
            addItemToList(ItemTypes.SUBMENU,line);
            paragraphCounter++;
        }
        else if(line.contains("[end]")){
            paragraphCounter--;
            addItemToList(ItemTypes.SUBMENU_END,line);
        }
        else{
            addItemToList(ItemTypes.NONE,line);
        }
    }

    private static void addItemToList(ItemTypes itemType, String line){
        String parsedItem = parseItem(itemType, line);
        ItemList.itemListModel.addElement(parsedItem);
    }

    private static String parseItem(ItemTypes itemType, String line) {
        String paragraph="";
        for(int i=0;i<3*paragraphCounter;i++)paragraph+=" ";
        if(itemType==ItemTypes.SEPARATOR) {
            line = line.replace("[separator]", "------------------------------------------------");
        }
        if(itemType==ItemTypes.EXEC){
            line=line.replace("[exec]","");
            if(paragraphCounter>0)paragraph+="->";
        }
        return paragraph+line;

    }

    static void saveMenuFile() throws IOException{
        String item;
        try{
            FileWriter filewriter = new FileWriter(menuFilePath);
            BufferedWriter bufferedwriter = new BufferedWriter(filewriter);
            int itemCounter=0;

            while(itemCounter < ItemList.itemListModel.size()){
                item= ItemList.itemListModel.get(itemCounter);
                item=parseItemToSave(item);
                bufferedwriter.write(item);
                bufferedwriter.newLine();
                itemCounter++;
            }

            JOptionPane.showMessageDialog(null, "Saved to \"~/.fluxbox/menu\" file");
            bufferedwriter.close();
        }catch(FileNotFoundException ex){
            JOptionPane.showMessageDialog(null, "Can't find \"~/.fluxbox/menu\" file");
        }
    }

    private static String parseItemToSave(String line){
        if(line.contains("----------"))line="[separator]";
        if(line.contains("->"))line=line.replace("->","");
        if(line.contains("{") && line.contains("}") && !line.contains("[submenu]")){
            line=line.trim();
            line="[exec]"+line;
        }
        return line.trim();
    }
}
