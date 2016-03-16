package il.ac.huji.todolist;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


        toDoList = (ListView) findViewById(R.id.todolist);
        stringsList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, stringsList) {

            @Override
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                // if a date is entered we want to check what color it would be
                Date date = null;
                try {
                    String dateFromText = null;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Pattern p = Pattern.compile(".*\\s(\\d{1,2}\\/\\d{1,2}\\/\\d{4})|\\d{1,2}\\/\\d{1,2}\\/\\d{4}");
                    Matcher m = p.matcher(textView.getText().toString());

                    if (m.find()){

                        dateFromText = m.group(1);
                    }
                    if(dateFromText != null){

                        date = dateFormat.parse(dateFromText);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(date != null){

                    Date now = new Date(System.currentTimeMillis());
                    int res = now.compareTo(date);


                    if(res < 0){

                        textView.setTextColor(Color.BLACK);
                    }else{
                        textView.setTextColor(Color.RED);
                    }
                }else {
                    textView.setTextColor(Color.BLACK);
                }

                return view;
            }
        };
        toDoList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(TodoListManagerActivity.this);
                alert.setCancelable(true);
                LayoutInflater factory = LayoutInflater.from(TodoListManagerActivity.this);
                View toShow = factory.inflate(R.layout.longclick, null);
                alert.setView(toShow);
                Button callBtn = (Button) toShow.findViewById(R.id.callbtn);
                final TextView txt = (TextView) view.findViewById(android.R.id.text1);

                //to check how to set the title to the item in the list
                alert.setTitle(toDoList.getItemAtPosition(position).toString());
                alert.setPositiveButton("Delete Item", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        adapter.remove(adapter.getItem(position));
                        adapter.notifyDataSetChanged();
                    }
                });
                callBtn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //checking if the number is a valid israeli number
//                        Pattern p = Pattern.compile("\\+9725\\d{8}|05\\d[-]?\\d{7}");
//                        Matcher m = p.matcher(txt.getText().toString());
                          String uri = txt.getText().toString();
//                        if (m.find()) {
//                            uri = m.group(0);
//                        }

                        if (uri.contains("Call")) {
                            String list[] = uri.split(",");
                            String toCall = list[0];
                            toCall = toCall.replaceAll("[^0-9|\\+]", "");
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", toCall, null));
                            startActivity(intent);
                        } else {
                            AlertDialog.Builder errAlert = new AlertDialog.Builder(TodoListManagerActivity.this);
                            errAlert.setMessage("Not a valid input for calling");
                            errAlert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            errAlert.create().show();
                        }

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
        final boolean[] dateChosen = {false};
        AlertDialog.Builder alert = new AlertDialog.Builder(TodoListManagerActivity.this);
        alert.setCancelable(true);
        alert.setTitle("Add New Item");
        LayoutInflater factory = LayoutInflater.from(this);
        final View a = factory.inflate(R.layout.dialoglay, null);
        alert.setView(a);
        final EditText text = (EditText) a.findViewById(R.id.userchoice);
        Button chooseDate = (Button) a.findViewById(R.id.dateButton);

        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener[] listener = {null};
        final int[] yearA = new int[2];
        final int[] monthA = new int[2];
        final int[] dayA = new int[2];
        yearA[0] = calendar.get(Calendar.YEAR);
        monthA[0] = calendar.get(Calendar.MONTH);
        dayA[0] = calendar.get(Calendar.DAY_OF_MONTH);

        chooseDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dateChosen[0] = true;
                listener[0] = new DatePickerDialog.OnDateSetListener() {


                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        yearA[0] = year;
                        monthA[0] = monthOfYear + 1;
                        dayA[0] = dayOfMonth;

                    }
                };
                new DatePickerDialog(TodoListManagerActivity.this, listener[0], yearA[0], monthA[0], dayA[0]).show();


            }
        });


        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // if the user chose to set a date
                if(dateChosen[0]){

                        adapter.add(text.getText().toString() + ", Due Date: " + dayA[0] + "/" + monthA[0] + "/" + yearA[0]);
                }else {
                    if(!text.getText().toString().isEmpty()){

                        adapter.add(text.getText().toString());
                    }

                }
                adapter.notifyDataSetChanged();

            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.create().show();

    }


}
