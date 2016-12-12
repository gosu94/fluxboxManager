import java.awt.*;
import javax.swing.*;

/**
 * @author Tomasz Pilarczyk
 */
class ItemList extends JPanel{

    static  DefaultListModel<String> itemListModel;
    static  JList<String> jList;

    ItemList() {
        itemListModel = new DefaultListModel();
        jList = new JList(itemListModel);
        jList.setSelectedIndex(0);

        JScrollPane pane = new JScrollPane(jList);
        JPanel p = new JPanel();

        pane.setPreferredSize(new Dimension(400,430));

        setLocation(20,30);
        setSize(400,450);
        add(pane);
        add(p);

    }

}
