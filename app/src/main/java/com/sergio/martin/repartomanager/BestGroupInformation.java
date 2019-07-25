package com.sergio.martin.repartomanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.sergio.martin.repartomanager.aux.ConstantesDB;
import com.sergio.martin.repartomanager.aux.HelperDB;

import java.util.ArrayList;

public class BestGroupInformation extends AppCompatActivity implements View.OnClickListener {

    private HelperDB db;
    SimpleCursorAdapter dataAdapter;
    private boolean isDeleted = false;
    private boolean empty = false;
    private int[] IDs = new int[]{R.id.buttonEliminarGrupo, R.id.buttonVolverGrupo};
    private static final int REQUEST_CODE_MEJOR = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_mejor_grupo);

        TextView codPost = findViewById(R.id.textViewCodPostal);
        codPost.setText(R.string.mejor_grup);

        db = new HelperDB(this);

        for (int id : IDs) {
            Button button = findViewById(id);
            button.setOnClickListener(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mostrarPedidos();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonEliminarGrupo:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        HelperDB db = new HelperDB(getBaseContext());
                        db.openEscritura();
                        db.eliminarGrupo();
                        db.closeDB();
                        isDeleted = true;
                        finish();
                    }
                });
                builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                builder.setTitle("Order group will be deleted");
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            case R.id.buttonVolverGrupo:
                finish();
                break;
        }
    }

    public void mostrarPedidos() {
        String[] campos = new String[] {
                ConstantesDB.CLI_NOMBRE,
                ConstantesDB.CLI_TEL,
                ConstantesDB.CLI_EMAIL,
        };
        int[] to = new int[] {
                R.id.nombreListView,
                R.id.telefonoListView,
                R.id.emailListView,
        };
        ListView listaPedidos = findViewById(R.id.listViewPedidosGrupo);
        dataAdapter = new SimpleCursorAdapter(this, R.layout.info_grupo,
                db.buscarMejorGrupo(), campos, to, 0);

        try {
            Log.e("cp", db.codigoPostalMejorGrupo());
        } catch (NullPointerException e) {
            empty = true;
            finish();
        }

        listaPedidos.setAdapter(dataAdapter);

        listaPedidos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayList<String> infoPedido = new ArrayList<>();
                Intent intent = new Intent(getApplicationContext(), BestOrderInformation.class);
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);

                infoPedido.add(cursor.getString(0));
                infoPedido.add(cursor.getString(1));
                infoPedido.add(cursor.getString(2));
                infoPedido.add(cursor.getString(3));
                intent.putExtra("datosContacto", infoPedido);
                startActivityForResult(intent, REQUEST_CODE_MEJOR);
            }
        });
    }

    @Override
    public void finish() {
        Intent intentEliminado = new Intent();
        if (isDeleted) {
            intentEliminado.putExtra("deleted", "Group deleted");
            setResult(RESULT_OK, intentEliminado);
        } else if (empty) {
            intentEliminado.putExtra("empty", "No entries");
            setResult(RESULT_OK, intentEliminado);
        }
        super.finish();
    }
}
