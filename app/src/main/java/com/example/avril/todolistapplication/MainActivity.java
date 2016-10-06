package com.example.avril.todolistapplication;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import android.widget.SimpleAdapter;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private EditText taskTitle;
    private EditText taskDescription;
    private Button addTask;
    private ListView taskList;
    private CheckBox checked;
    private List<HashMap<String, String>> list = new ArrayList<>();
    private SimpleAdapter adapter;

    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskTitle = (EditText) findViewById(R.id.tastTitle);
        taskDescription = (EditText) findViewById(R.id.taskDescription);
        addTask = (Button) findViewById(R.id.addTask);
        taskList = (ListView) findViewById(R.id.tastList);

        //adapter extends SimpleAdapter
        adapter = new SimpleAdapter(this, list,
                R.layout.mylistview, new String[]{"title", "descrip"},
                new int[]{R.id.listtext1, R.id.listtext2} ) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                checked = (CheckBox) view.findViewById(R.id.checked);
                checked.setChecked(false);
                checked.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //delete item in text file
                        Delete(position);

                        //delete in listview
                        list.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
                return view;
            }
        };

        taskList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //delete item in text file
                Delete(position);

                //delete in listview
                list.remove(position);
                adapter.notifyDataSetChanged();
                return false;
            }
        });


        ReadFromFile();
        Save();
    }

    private void ReadFromFile(){
        try {
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
            File file = new File(path, "/ToDoList.txt");
            FileInputStream fis = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fis);
            try {
                BufferedReader buffer = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = buffer.readLine()) != null){
                    String[] mydata = line.split("\t");
                    HashMap<String, String> map = new HashMap<>();
                    map.put("title", mydata[0]);
                    map.put("descrip", mydata[1]);
                    list.add(map);
                }
            }catch (IOException e) {e.printStackTrace();}
        }catch (Exception e) {e.printStackTrace();}

        taskList.setAdapter(adapter);
    }

    private void Delete(int pos){
        try {
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
            File file = new File(path, "/ToDoList.txt");
            File tempFile = new File(path, "/myTempFile.txt");
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tempFile));
                FileOutputStream fos = new FileOutputStream(file,true);
                String line;
                while ((line = bufferedReader.readLine()) != null){
                    if (pos-- == 0)
                        continue;
                    bufferedWriter.write(line + System.getProperty("line.separator"));
                }
                bufferedReader.close();
                bufferedWriter.close();

                tempFile.renameTo(file);
            }catch (IOException e) {e.printStackTrace();}
        }catch (Exception e) {e.printStackTrace();}
    }


    private void Save(){

        //click Add button
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Objects.equals(taskTitle.getText().toString(), ""))
                    Toast.makeText(MainActivity.this, "You have to add a task title!", Toast.LENGTH_SHORT).show();
                else if (Objects.equals(taskDescription.getText().toString(), ""))
                    Toast.makeText(MainActivity.this, "You have to add task description!", Toast.LENGTH_SHORT).show();
                else {
                    //save into the txt file
                    try {
                        File file = new File(path, "/ToDoList.txt");
                        FileOutputStream fos = new FileOutputStream(file,true);
                        fos.write(taskTitle.getText().toString().getBytes());
                        fos.write("\t".getBytes());
                        fos.write(taskDescription.getText().toString().getBytes());
                        fos.write("\n".getBytes());
                    }catch (IOException e) {e.printStackTrace();}

                    //save into list
                    HashMap<String, String> map = new HashMap<>();
                    map.put("title", taskTitle.getText().toString());
                    map.put("descrip", taskDescription.getText().toString());
                    list.add(map);

                    //set edit text to empty
                    taskTitle.setText("");
                    taskDescription.setText("");
                }
            }
        });

        //save into adapter and listview
        taskList.setAdapter(adapter);
    }
}
