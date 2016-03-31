package il.ac.huji.todolist;

/**
 * Created by ron on 30/03/16.
 */
public class ListItem {

    String item;
    String key;

    public ListItem(String item, String key){
        this.item = item;
        this.key = key;
    }
    public String getItem() {
        return item;
    }

    public String getKey() {
        return key;
    }

}
