package com.matic.lugarenelbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.matic.lugarenelbar.adapters.BarAdapter;
import com.matic.lugarenelbar.models.Bar;
import com.matic.lugarenelbar.taskWebService.bares.TareaWSArrayList;

import java.util.ArrayList;
import java.util.List;

public class ListadoBaresActivity extends AppCompatActivity implements  View.OnClickListener ,SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener {

    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;

    private ArrayList<Bar> listaBares ;

    private ListView lstBar;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_bares);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        listaBares =new ArrayList<>();

        new TareaWSArrayList(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                pd = ProgressDialog.show(ListadoBaresActivity.this, "", getString(R.string.pd_message_listadoBares));

            }

            @Override
            protected void onPostExecute(ArrayList<Bar> result) {
                if (result!=null)
                {


                    if (pd.isShowing()) {
                        pd.dismiss();
                    }

                    listaBares=result;
                    Log.i("Lista Bares",""+result);
                    //barAdapter.setListaBares(result);

                    recycler = (RecyclerView) findViewById(R.id.reciclador);
                    recycler.setHasFixedSize(true);

                    // Usar un administrador para LinearLayout
                    lManager = new LinearLayoutManager(ListadoBaresActivity.this);
                    recycler.setLayoutManager(lManager);

                    // Crear un nuevo adaptador
                    adapter = new BarAdapter(ListadoBaresActivity.this,listaBares);
                    recycler.setAdapter(adapter);


                }else{
                    Toast.makeText(ListadoBaresActivity.this,getString(R.string.error_ws)+"\n"+result,Toast.LENGTH_LONG).show();
                }
            }
        }.execute();



Log.i("Lista Bares",""+listaBares);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        try {
            menu.clear();
            getMenuInflater().inflate(R.menu.menu_search, menu);

            RelativeLayout actionView = (RelativeLayout) menu.findItem(R.id.menu_search).getActionView();
            final EditText editText = (EditText) actionView.findViewById(R.id.etSearch);
            //final EditText editText = (EditText) menu.findItem(R.id.menu_search).getActionView();
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    ArrayList<Bar> temp= new ArrayList<>();
                    int txtLength = editText.getText().length();
                    temp.clear();
                    for(int i=0;i<listaBares.size();i++)
                    {
                        if(txtLength<=listaBares.get(i).toString().length())
                        {
                         if(editText.getText().toString().equalsIgnoreCase((String) listaBares.get(i).toString().subSequence(0,txtLength)))
                            {
                               temp.add(listaBares.get(i));
                            }
                        }
                    }

                    adapter = new BarAdapter(ListadoBaresActivity.this,temp);
                    recycler.setAdapter(adapter);
                    Log.i("Lista temp",""+temp);
                    //menuBuscador(s);

                }

                @Override
                public void afterTextChanged(Editable s) {

                }

            });

            MenuItem menuItem = menu.findItem(R.id.menu_search);
            MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    editText.requestFocus();

                    //((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    return true; // Return true to expand action view
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    // Do something when collapsed

                    // Borramos el texto que había escrito.
                    editText.setText("");

                    return true; // Return true to collapse action view
                }
            });
        } catch (Exception ex) {

        }

        return super.onCreateOptionsMenu(menu);

    }

    //metodos de busqueda
    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return false;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        //menuBuscador(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //menuBuscador(newText);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {

    }

//    public void menuBuscador(CharSequence s)
//    {
//
//    }

}
