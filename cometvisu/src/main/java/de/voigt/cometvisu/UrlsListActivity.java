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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UrlsListActivity extends Activity {

    private static final String VISU_URL_KEY = "VISU_URLS";
    static final String VISU_SELECTED_URL_KEY = "VISU_SELECTED_URL";
    public static final String URL = "url";
    public static final String CHECKED = "checked";

    private final Activity activity = this;

    private final List<Map<String, Object>> urlsMap = new ArrayList<Map<String, Object>>();

    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.urls_view);

        final ListView lv = (ListView) findViewById(R.id.urlListView);
        final Button addButton = (Button) findViewById(R.id.addButton);
        final Button backButton = (Button) findViewById(R.id.backButton);


        adapter = new SimpleAdapter(activity,
                urlsMap,
                R.layout.list_single_check,
                new String[] {URL, CHECKED},
                new int[] {R.id.urlText, R.id.selectRadioButton});

        Set<String> urls = loadUrlStringsFromSharedPreferences();
        updateLocalUrlMapAndNotifyAdapter(urls);

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

                            }
                        })
                        .setNegativeButton("Cancel", null).show();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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

        });


        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RadioButton rb = (RadioButton) view.findViewById(R.id.selectRadioButton);
                if (!rb.isChecked()) { //OFF->ON

                    for (Map<String, Object> m :urlsMap) {//clean previous selected
                        m.put(CHECKED, false);
                    }

                    SharedPreferences.Editor editor = getSharedPreferences().edit();
                    editor.putString(VISU_SELECTED_URL_KEY, (String) urlsMap.get(position).get(URL));
                    editor.commit();

                    urlsMap.get(position).put(CHECKED, true);
                    adapter.notifyDataSetChanged();
                }
            }

        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                Boolean checked = (Boolean) urlsMap.get(position).get(CHECKED);
                final String urlToBeEdited = (String) urlsMap.get(position).get(URL);
                if(checked){
                    new AlertDialog.Builder(activity)
                            .setTitle("Fehler!")
                            .setMessage("Ein selektierter Eintrag kann nicht gelöscht/geändert werden!")
                            .setNeutralButton("OK", null).show();

                }else{

                    // Set an EditText view to get user input
                    final EditText input = new EditText(activity);
                    input.setText(urlToBeEdited);

                    new AlertDialog.Builder(activity)
                            .setTitle("Edit/Delete")
                            .setView(input)
                            .setMessage("Eintrag '" + urlToBeEdited + "' löschen/ändern?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Set<String> urls = loadUrlStringsFromSharedPreferences();
                                    urls.remove(urlToBeEdited);
                                    addUrlToPreferences(input.getText().toString());
                                }
                            })
                            .setNeutralButton("Löschen", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    removeUrlFromPreferences(urlToBeEdited);
                                }
                            })
                            .setNegativeButton("Cancel", null).show();

                }

                return true;
            }
        });
    }

    private void addUrlToPreferences(String newUrl) {
        Set<String> urls = loadUrlStringsFromSharedPreferences();
        urls.add(newUrl);

        saveUrlsToPreferences(urls);
    }

    private void removeUrlFromPreferences(String urlToBeRemoved) {
        Set<String> urls = loadUrlStringsFromSharedPreferences();
        urls.remove(urlToBeRemoved);

        saveUrlsToPreferences(urls);
    }

    private void saveUrlsToPreferences(Set<String> urls) {
        SharedPreferences sharedPref = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPref.edit();
        //for some strange reasons, we need to remove the key first; otherwise it wont be saved (after restart of app)
        editor.remove(VISU_URL_KEY);
        boolean deleted = editor.commit();
        Log.d("sharedprefs", "deleted key: " + deleted);

        Set<String> urlsEmpty = sharedPref.getStringSet(VISU_URL_KEY, new HashSet<String>());
        Log.d("sharedprefs", "urls empty? " + urlsEmpty);

        editor.putStringSet(VISU_URL_KEY, urls);
        Log.d("sharedprefs", "urls to be saved? " + urls);
        boolean committed = editor.commit();
        Log.d("sharedprefs", "commit successful? " + committed);

        //set local map
        updateLocalUrlMapAndNotifyAdapter(urls);
    }

    private void updateLocalUrlMapAndNotifyAdapter(Set<String> urls) {
        urlsMap.clear();
        String selectedUrl = getSharedPreferences().getString(VISU_SELECTED_URL_KEY,"");
        for (String url : urls){
            boolean checked = (selectedUrl!=null && selectedUrl.equals(url) ? true : false);

            Map<String, Object> urlMap = new HashMap<String, Object>();
            urlMap.put(URL, url);
            urlMap.put(CHECKED, checked);
            urlsMap.add(urlMap);
        }
        adapter.notifyDataSetChanged();
    }

    private Set<String> loadUrlStringsFromSharedPreferences() {
        SharedPreferences sharedPref = getSharedPreferences();
        return sharedPref.getStringSet(VISU_URL_KEY, new HashSet<String>());
    }

    private SharedPreferences getSharedPreferences() {
        return getApplicationContext().getSharedPreferences(UrlsListActivity.class.getName(), Context.MODE_PRIVATE);
    }
}