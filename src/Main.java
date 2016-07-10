import java.awt.*;
import java.awt.event.*;
import javax.swing.*;



public class Main {

    public static Frame f =new Frame("FluxboxManager");
    public static menuList cMenulist = new menuList();

    public enum Req {   //Type of field we want to add to a menu
        EXEC,SUBMENU,EDIT_SUBMENU,EDIT_EXEC
    }


    public static void main(String args[]) {


        Dialog cDialog = new Dialog();

        JButton exec = new JButton("Exec");
        exec.setBounds(420,35,120,30);
        exec.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                cDialog.openBox(Req.EXEC);

            }
        });

        JButton sub = new JButton("Submenu");
        sub.setBounds(420,75,120,30);
        sub.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                cDialog.openBox(Req.SUBMENU);
            }
        });

        JButton separator = new JButton("Separator");
        separator.setBounds(420,115,120,30);
        separator.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                cMenulist.listModel.add(cMenulist.jlist.getSelectedIndex()+1,"------------------------------------------------");
            }
        });

        JButton load = new JButton("Load");
        load.setBounds(420,390,120,30);
        load.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                cMenulist.loadFile();
            }
        });

        JButton save = new JButton("Save");
        save.setBounds(420,430,120,30);
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                cMenulist.saveFile();
            }
        });

        JButton delete = new JButton("Delete");
        delete.setBounds(420,205,120,30);
        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int i=cMenulist.jlist.getSelectedIndex();
                String s_name = (String)cMenulist.listModel.get(i);
                if(!s_name.contains("[submenu]"))
                    cMenulist.listModel.remove(i);
                else{
                    while(!s_name.contains("[end]")){  //removing all items till we get to the end of submenu
                        cMenulist.listModel.remove(i);
                        s_name=(String)cMenulist.listModel.get(i);
                    }
                    cMenulist.listModel.remove(i); //removing [end] itself
                }

                cMenulist.jlist.setSelectedIndex(i);
            }
        });

        JButton edit = new JButton("Edit");
        edit.setBounds(420,245,120,30);
        edit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String s_whole=(String)cMenulist.listModel.getElementAt(Main.cMenulist.jlist.getSelectedIndex());
                if(s_whole.contains("[submenu]"))
                    cDialog.openBox(Req.EDIT_SUBMENU);
                else
                    cDialog.openBox(Req.EDIT_EXEC);
            }
        });

        //adding stuff
        f.add(cMenulist);
        f.add(exec);
        f.add(sub);
        f.add(separator);
        f.add(save);
        f.add(load);
        f.add(delete);
        f.add(edit);

        //frame settings
        f.setLayout(null);
        f.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        f.setFocusable(true);
        f.setSize(550,500);
        f.setLocation(400,200);
        f.requestFocus();
        f.setVisible(true);



    }


}
//Dialog box responsible for adding elements to list
class Dialog extends Frame{
    JTextField name;
    JTextField command;
    JButton ok;
    static Main.Req requestType;


    Dialog() {


        //components
        name = new JTextField();
        name.setBounds(50,50,300,20);
        name.setFocusTraversalKeysEnabled(false);
        name.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {}
            @Override
            public void keyPressed(KeyEvent keyEvent) {}

            @Override
            public void keyReleased(KeyEvent keyEvent) {
                if(keyEvent.getKeyCode()==keyEvent.VK_TAB)command.requestFocus();
                if(requestType== Main.Req.SUBMENU && keyEvent.getKeyCode()==keyEvent.VK_ENTER){
                    addSubMenu();
                    setVisible(false);
                }

            }
        });

        command = new JTextField();
        command.setBounds(50,100,300,40);
        command.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {}

            @Override
            public void keyPressed(KeyEvent keyEvent) {}

            @Override
            public void keyReleased(KeyEvent keyEvent) {
                if(keyEvent.getKeyCode()==keyEvent.VK_ENTER){
                    if(requestType==Main.Req.EXEC)addExec();
                    if(requestType==Main.Req.SUBMENU)addSubMenu();
                    if(requestType==Main.Req.EDIT_EXEC)editExec();
                    if(requestType==Main.Req.EDIT_SUBMENU)editSubmenu();
                    setVisible(false);
                }
            }
        });

        JLabel l_name = new JLabel("Name");
        l_name.setBounds(165,25,40,20);

        JLabel l_command = new JLabel("Command");
        l_command.setBounds(155,78,80,20);

        ok = new JButton("OK");
        ok.setBounds(155,150,80,30);
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(requestType==Main.Req.EXEC)addExec();
                if(requestType==Main.Req.SUBMENU)addSubMenu();
                if(requestType==Main.Req.EDIT_EXEC)editExec();
                if(requestType==Main.Req.EDIT_SUBMENU)editSubmenu();

                setVisible(false);
            }
        });


        //frame settings
        setTitle("Dialog box");
        setSize(400, 200);
        setLayout(null);
        setVisible(false);

        //adding stuff
        add(name);
        add(l_name);
        add(command);
        add(l_command);
        add(ok);
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false);
            }
        });
    }
    public void openBox(Main.Req request)
    {
        requestType=request;    //while openning dialog box we're setting its type
        if(request==Main.Req.SUBMENU)command.setEditable(false);
        else command.setEditable(true);

        name.setText("");
        command.setText("");

        if(request==Main.Req.EDIT_SUBMENU){
            String s_whole=(String)Main.cMenulist.listModel.getElementAt(Main.cMenulist.jlist.getSelectedIndex());
            String s_name=s_whole.substring(s_whole.indexOf("(")+1,s_whole.indexOf(")"));
            String s_command="";

            command.setEditable(false);

            name.setText(s_name);
            command.setText(s_command);
        }
        if(request==Main.Req.EDIT_EXEC){
            String s_whole=(String)Main.cMenulist.listModel.getElementAt(Main.cMenulist.jlist.getSelectedIndex());
            String s_name=s_whole.substring(s_whole.indexOf("(")+1,s_whole.indexOf(")"));
            String s_command = s_whole.substring(s_whole.indexOf("{") + 1, s_whole.indexOf("}"));

            name.setText(s_name);
            command.setText(s_command);
        }

        Point parentLocation=Main.f.getLocation();
        setLocation(parentLocation.x+75, parentLocation.y+150);
        setVisible(true);
        name.requestFocus(true);

    }
    public void addExec(){
        String text="";
        String selected="";
        //getting element above to ensure it isn't in [submenu] section (only if there is one)
        if(Main.cMenulist.listModel.size()>0) {
            try{
                selected = (String) Main.cMenulist.jlist.getSelectedValue();
            }catch(NullPointerException e){
                //JOptionPane.showMessageDialog(null, "Exec has been added");
            }
        }

        if(selected.contains("->") || selected.contains("[submenu]")){
            for(int i=0;i<countSpaces(selected);i++)text+=" ";
            if(selected.contains("[submenu]"))text+="   ";
            text+="->";
        }
        text+="(";
        text+=name.getText();
        text+=") {";
        text+=command.getText();
        text+="}";

        Main.cMenulist.listModel.add(Main.cMenulist.jlist.getSelectedIndex()+1,text);
        //JOptionPane.showMessageDialog(null, "Exec has been added");
    }

    public void addSubMenu(){
        String text="";
        String selected="";
        //getting element above to ensure it isn't in [submenu] section (only if there is one)
        if(Main.cMenulist.listModel.size()>0)
            selected= (String) Main.cMenulist.jlist.getSelectedValue();

        if(selected.contains("->") || selected.contains("[submenu]")){
            for(int i=0;i<countSpaces(selected);i++)text+=" ";
            if(selected.contains("[submenu]"))text+="   ";

        }
        text+="[submenu] (";
        text+=name.getText();
        text+=")";
        Main.cMenulist.listModel.add(Main.cMenulist.jlist.getSelectedIndex()+1,text);
        text=text.substring(0,text.length()-1);  //removing ")"
        text=text.substring(0,text.length()-name.getText().length()); //removing name
        text=text.substring(0,text.length()-"[submenu] (".length());
        text+="[end]";
        Main.cMenulist.listModel.add(Main.cMenulist.jlist.getSelectedIndex()+2,text);


    }

    public void editSubmenu(){
        String text="";
        String selected="";
        //getting element above to ensure it isn't in [submenu] section (only if there is one)
        if(Main.cMenulist.listModel.size()>0)
            selected= (String) Main.cMenulist.jlist.getSelectedValue();

        if(selected.contains("->") || selected.contains("[submenu]")){
            for(int i=0;i<countSpaces(selected);i++)text+=" ";
            if(selected.contains("[submenu]"))text+="   ";
            text+="  ";
        }
        text+="[submenu] (";
        text+=name.getText();
        text+=")";
        Main.cMenulist.listModel.add(Main.cMenulist.jlist.getSelectedIndex(),text);

    }

    void editExec() {
        String text="";
        String selected="";
        //getting element above to ensure it isn't in [submenu] section (only if there is one)
        if(Main.cMenulist.listModel.size()>0)
            selected= (String) Main.cMenulist.jlist.getSelectedValue();

        if(selected.contains("->") || selected.contains("[submenu]")){
            for(int i=0;i<countSpaces(selected);i++)text+=" ";
            if(selected.contains("[submenu]"))text+="   ";
            text+="->";
        }
        text+="(";
        text+=name.getText();
        text+=") {";
        text+=command.getText();
        text+="}";

        int index=Main.cMenulist.jlist.getSelectedIndex();
        Main.cMenulist.listModel.remove(index);
        Main.cMenulist.listModel.add(index,text);
    }

    int countSpaces(String text){
        int count=0;
        for(int i=0;i<text.length();i++){
            if(Character.isWhitespace(text.charAt(i)))count++;
            else break;
        }
        return count;
    }
}
