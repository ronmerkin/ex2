package il.ac.huji.todolist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import static android.app.PendingIntent.getActivity;

public class TodoListManagerActivity extends AppCompatActivity {
    private ArrayList<String> stringsList;
    private EditText toAdd;
    private ListView toDoList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toAdd = (EditText) findViewById(R.id.toAdd);
        toDoList = (ListView) findViewById(R.id.todolist);
        stringsList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, stringsList) {
            private int[] colors = new int[]{Color.BLUE, Color.RED};

            @Override
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView textView = (TextView) view.findViewById(android.R.id.text1);

                // making sure that the text between two lines differ
                int colorPos = position % colors.length;
                textView.setTextColor(colors[colorPos]);

                return view;
            }
        };
        toDoList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(TodoListManagerActivity.this);
                alert.setCancelable(true);

                //to check how to set the title to the item in the list
                alert.setTitle(toDoList.getItemAtPosition(position).toString());
                alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        adapter.remove(adapter.getItem(position));
                        adapter.notifyDataSetChanged();
                    }
                });
                alert.create().show();

                return true;
            }
        });
        toDoList.setAdapter(adapter);


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menues, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.appmenuadd:
                add();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    //adding an item to the listview
    public void add() {
        if (!toAdd.getText().toString().isEmpty()) {
            stringsList.add(toAdd.getText().toString());
            adapter.notifyDataSetChanged();
        }else{
            toAdd.setError("Can't add empty input");
        }
    }


}
