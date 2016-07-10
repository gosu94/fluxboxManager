/**
 * Created by gosu on 21.06.16.
 */
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import javax.swing.*;

public class menuList extends JPanel{

    public  DefaultListModel listModel;
    public  JList jlist;

    public menuList() {
        listModel = new DefaultListModel();
        jlist = new JList(listModel);
        jlist.setSelectedIndex(0);
        jlist.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {

            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {

            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
                if(keyEvent.getKeyCode()==27) System.exit(0);
                if(keyEvent.getKeyChar()=='a')listModel.add(jlist.getSelectedIndex()+1,"another one");
                if(keyEvent.getKeyChar()=='g') System.out.println(jlist.getSelectedIndex());
                System.out.println(keyEvent.getKeyCode());
            }
        });

        JScrollPane pane = new JScrollPane(jlist);
        JPanel p = new JPanel();

        pane.setPreferredSize(new Dimension(400,430));

        setLocation(20,30);
        setSize(400,450);
        add(pane);
        add(p);

    }


     void loadFile(){
        String fileName = "/home/gosu/.fluxbox/menu";
        String line = null;
         int belongsToSubmenu=0;


        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader =
                    new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);
            listModel.removeAllElements();
            while((line = bufferedReader.readLine()) != null) {

                if(line.contains("[exec]")){
                    addLine(belongsToSubmenu,true,line);
                    continue;
                }
                if(line.contains("[separator]")) {
                    addLine(belongsToSubmenu,false,line);
                    continue;

                }
                if(line.contains("[submenu]")){
                    addLine(belongsToSubmenu,false,line);
                    belongsToSubmenu++;
                    continue;

                }
                if(line.contains("[end]")){
                    belongsToSubmenu--;
                    addLine(belongsToSubmenu,false,line);
                    continue;
                }
                addLine(belongsToSubmenu,false,line);


            }

            // Always close files.
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Can't find \"~/.fluxbox/menu\" file");
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    void addLine(int belongsToSubmenu,boolean isExec,String line) {
        String space="";
        for(int i=0;i<3*belongsToSubmenu;i++)space+=" ";
        if(line.contains("[separator]"))
            line = line.replace("[separator]", "------------------------------------------------");
        if(isExec){
            line=line.replace("[exec]","");
            if(belongsToSubmenu>0)space+="->";
        }

        listModel.addElement(space+line);
    }

    void saveFile(){
        String fileName = "/home/gosu/.fluxbox/menu";
        String line = null;
        String space="";
        try{
            FileWriter filewriter = new FileWriter(fileName);
            BufferedWriter bufferedwriter = new BufferedWriter(filewriter);
            int i=0;
            while(i < listModel.size()){
                line=(String)listModel.get(i);
                if(line.contains("----------"))line="[separator]";
                if(line.contains("->"))line=line.replace("->","");
                if(line.contains("{") && line.contains("}") && !line.contains("[submenu]")){
                    line=line.trim();
                    String exec="[exec]";
                    exec+=line;
                    line=exec;
                }
                line=line.trim();
                bufferedwriter.write(line);
                bufferedwriter.newLine();
                i++;
            }
            JOptionPane.showMessageDialog(null, "Saved to \"~/.fluxbox/menu\" file");
            bufferedwriter.close();

        }catch(FileNotFoundException ex){
            JOptionPane.showMessageDialog(null, "Can't find \"~/.fluxbox/menu\" file");
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
    }
}
