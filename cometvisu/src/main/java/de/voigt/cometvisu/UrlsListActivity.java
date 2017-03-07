package de.voigt.cometvisu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
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
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UrlsListActivity extends BaseActivity {

    private static final String TAG = "UrlsListActivity";

    static final String VISU_ORIENTATION_KEY = "VISU_ORIENTATION";
    private static final String VISU_URL_KEY = "VISU_URLS";
    static final String VISU_SELECTED_URL_KEY = "VISU_SELECTED_URL";
    public static final String URL = "url";
    public static final String CHECKED = "checked";

    private final Activity activity = this;

    private final List<Map<String, Object>> urlsMap = new ArrayList();

    private SimpleAdapter urlAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.urls_view);

        final ListView lv = (ListView) findViewById(R.id.urlListView);
        final Spinner orientationSpinner = (Spinner) findViewById(R.id.orientationSpinner);
        final Button addButton = (Button) findViewById(R.id.addButton);
        final Button backButton = (Button) findViewById(R.id.backButton);
        final TextView pushNotificationTokenTextView = (TextView) findViewById(R.id.textPushNotificationToken);
        final Button copy2ClipboardButton = (Button) findViewById(R.id.copy2clipboardButton);

        urlAdapter = new SimpleAdapter(activity,
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

        final String token = FirebaseInstanceId.getInstance().getToken();
        pushNotificationTokenTextView.setText(token != null ? token.substring(0, 20) + "..." : "Failed to get token");
        CopyToClipboardClickListener copyToClipboardClickListener = new CopyToClipboardClickListener(token);
        pushNotificationTokenTextView.setOnClickListener(copyToClipboardClickListener);
        copy2ClipboardButton.setOnClickListener(copyToClipboardClickListener);

        String[] items = Orientation.names();
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, items);
        orientationSpinner.setAdapter(adapter);
        orientationSpinner.setSelection(loadSelectedOrientationFromSharedPreferences());
        orientationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // save the selected value
                SharedPreferences sharedPref = getSharedPreferences();
                SharedPreferences.Editor editor = sharedPref.edit();

                String selectedOrientation = (String)parent.getItemAtPosition(position);
                Log.d(TAG, "selectedOrientation: " + selectedOrientation);

                int selectedOrientationId = Orientation.valueOf(selectedOrientation).getValue();
                Log.d(TAG, "selectedOrientationId: " + selectedOrientationId);

                editor.putLong(VISU_ORIENTATION_KEY, selectedOrientationId);
                boolean committed = editor.commit();
                Log.d(TAG, "commit successful? " + committed);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //nothing to do
                Log.d("orientationSpinner", "nothing selected!");
            }
        });

        urlAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if (data == null) { //if 2nd line text is null, its textview should be hidden
                    view.setVisibility(View.GONE);
                    return true;
                }
                view.setVisibility(View.VISIBLE);
                return false;
            }

        });


        lv.setAdapter(urlAdapter);
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
                    urlAdapter.notifyDataSetChanged();
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
        Log.d(TAG, "deleted key: " + deleted);

        Set<String> urlsEmpty = sharedPref.getStringSet(VISU_URL_KEY, new HashSet<String>());
        Log.d(TAG, "urls empty? " + urlsEmpty);

        editor.putStringSet(VISU_URL_KEY, urls);
        Log.d(TAG, "urls to be saved? " + urls);
        boolean committed = editor.commit();
        Log.d(TAG, "commit successful? " + committed);

        //set local map
        updateLocalUrlMapAndNotifyAdapter(urls);
    }

    private void updateLocalUrlMapAndNotifyAdapter(Set<String> urls) {
        urlsMap.clear();
        String selectedUrl = getSharedPreferences().getString(VISU_SELECTED_URL_KEY, "");
        for (String url : urls){
            boolean checked = (selectedUrl!=null && selectedUrl.equals(url) ? true : false);

            Map<String, Object> urlMap = new HashMap();
            urlMap.put(URL, url);
            urlMap.put(CHECKED, checked);
            urlsMap.add(urlMap);
        }
        urlAdapter.notifyDataSetChanged();
    }

    private Set<String> loadUrlStringsFromSharedPreferences() {
        SharedPreferences sharedPref = getSharedPreferences();
        Set<String> urls = new HashSet();
        try {
            urls = sharedPref.getStringSet(VISU_URL_KEY, new HashSet<String>());
        } catch (Exception e){
            // might happen if we mis-used the KEY by accident
        }
        return urls;
    }

    private int loadSelectedOrientationFromSharedPreferences() {
        SharedPreferences sharedPref = getSharedPreferences();
        int selectedOrientation = Orientation.Landscape.getValue();
        try {
            selectedOrientation= (int)sharedPref.getLong(VISU_ORIENTATION_KEY, Orientation.Landscape.getValue());
        } catch (Exception e){
            // might happen if we mis-used the KEY by accident
        }
        return selectedOrientation;
    }

    private SharedPreferences getSharedPreferences() {
        return getApplicationContext().getSharedPreferences(UrlsListActivity.class.getName(), Context.MODE_PRIVATE);
    }

    private class CopyToClipboardClickListener implements View.OnClickListener {
        private final String token;

        public CopyToClipboardClickListener(String token) {
            this.token = token;
        }

        @Override
        public void onClick(View v) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE); 
            ClipData clip = ClipData.newPlainText("Push Token", token);
            clipboard.setPrimaryClip(clip);
            Log.v(TAG, "Push Token: " + token);
            Toast.makeText(getApplicationContext(), "Push Token in die Zwischenablage kopiert", Toast.LENGTH_SHORT).show();
        }
    }
}