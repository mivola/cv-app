package de.voigt.cometvisu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UrlsListActivity extends Activity {

    private static final String VISU_KEY = "VISU_URLS";
    public static final String URL = "url";
    public static final String CHECKED = "checked";
//    public static final String DELETE_ENABLED = "deleteEnabled";
    private final Activity activity = this;

    final List<Map<String, Object>> urlsMap = new ArrayList<Map<String, Object>>();

    SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.urls_view);

        final ListView lv = (ListView) findViewById(R.id.urlListView);
        final Button addButton = (Button) findViewById(R.id.addButton);
        final Button backButton = (Button) findViewById(R.id.backButton);


        Set<String> urls = loadUrlStringsFromSharedPreferences();

        for (String url : urls){
            Map<String, Object> urlMap = new HashMap<String, Object>();
            urlMap.put(URL, url);
            urlMap.put(CHECKED, false);
//            urlMap.put(DELETE_ENABLED, true);
            urlsMap.add(urlMap);
        }

        adapter = new SimpleAdapter(activity,
                urlsMap,
                R.layout.list_single_check,
                new String[] {URL, CHECKED/*, DELETE_ENABLED*/},
                new int[] {R.id.urlText, R.id.selectRadioButton/*, R.id.deleteButton*/});


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Set an EditText view to get user input
                final EditText input = new EditText(activity);
                input.setText("http://");

                new AlertDialog.Builder(activity)
                        .setTitle("Neue URL")
                        .setView(input)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                String newUrl = input.getText().toString();
                                addUrlToPreferences(newUrl);

                                //reload(lv);

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Do nothing.
                            }
                        }).show();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //reload(lv);

        adapter.setViewBinder(new SimpleAdapter.ViewBinder()
        {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation)
            {
                if (data == null) { //if 2nd line text is null, its textview should be hidden
                    view.setVisibility(View.GONE);
                    return true;
                }
                view.setVisibility(View.VISIBLE);
                return false;
            }

//            final ImageButton deleteButton = (ImageButton) findViewById(R.id.deleteButton);

        });


        // Bind to our new adapter.
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
                RadioButton rb = (RadioButton) v.findViewById(R.id.selectRadioButton);
                if (!rb.isChecked()) { //OFF->ON

                    for (Map<String, Object> m :urlsMap) {//clean previous selected
                        m.put(CHECKED, false);
                        //TODO: enable delete button
                    }

                    urlsMap.get(arg2).put(CHECKED, true);
                    //TODO: save selection
                    //TODO: disable delete button
                    adapter.notifyDataSetChanged();
                }
            }
        });

        /*
        //show result
        ((Button)activity.findViewById(R.id.Button01)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int r = -1;
                for (int i = 0; i < urlsMap.size(); i++) //clean previous selected
                {
                    HashMap<String, Object> m = urlsMap.get(i);
                    Boolean x = (Boolean) m.get("checked");
                    if (x == true)
                    {
                        r = i;
                        break; //break, since it's a single choice list
                    }
                }
                new AlertDialog.Builder(m_this).setMessage("you selected:"+r).show();
            }
        });
*/
    }

    private void addUrlToPreferences(String newUrl) {
        Set<String> urls = loadUrlStringsFromSharedPreferences();
        urls.add(newUrl);

        SharedPreferences sharedPref = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(VISU_KEY);
        boolean deleted = editor.commit();
        Log.i("sharedprefs", "deleted key: " + deleted);

        Set<String> urlsEmpty = sharedPref.getStringSet(VISU_KEY, new HashSet<String>());   
        Log.i("sharedprefs", "urls empty? " + urlsEmpty);

        editor.putStringSet(VISU_KEY, urls);
        Log.i("sharedprefs", "urls to be saved? " + urls);
        boolean committed = editor.commit();
        Log.i("sharedprefs", "commit successful? " + committed);

    }

    private void reload(ListView listView) {

        Set<String> urls = loadUrlStringsFromSharedPreferences();

        List<String> list = Arrays.asList(urls.toArray(new String[urls.size()]));

        ListAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, list);

        listView.setAdapter(adapter);

    }

    private Set<String> loadUrlStringsFromSharedPreferences() {
        SharedPreferences sharedPref = getSharedPreferences();

        return sharedPref.getStringSet(VISU_KEY, new HashSet<String>());
    }

    private SharedPreferences getSharedPreferences() {
        return getApplicationContext().getSharedPreferences(UrlsListActivity.class.getName(), Context.MODE_PRIVATE);
    }

}
