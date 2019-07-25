package com.sergio.martin.repartomanager;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.sergio.martin.repartomanager.aux.ConstantesDB;
import com.sergio.martin.repartomanager.aux.HelperDB;

public class Main extends AppCompatActivity implements View.OnClickListener {

    private HelperDB db;
    private SimpleCursorAdapter dataAdapter;

    private static final int REQUEST_CODE_MEJOR = 1;
    private static final int REQUEST_CODE_GRUPO = 2;
    private static final int REQUEST_CODE_MAPA = 3;

    private int[] IDs = new int[]{R.id.buttonNuevoPedido, R.id.buttonMejorPedido,
            R.id.buttonMejorGrupo, R.id.buttonMapa};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        db = new HelperDB(this);

        for (int id: IDs) {
            Button button = (Button)findViewById(id);
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
            case R.id.buttonNuevoPedido:
                Intent intentPedido = new Intent(this, OrderFactory.class);
                startActivity(intentPedido);
                break;
            case R.id.buttonMejorPedido:
                Intent intentMejorPedido = new Intent(this, BestOrderInformation.class);
                intentMejorPedido.putExtra("datosContacto", db.buscarMejorPedido());
                startActivityForResult(intentMejorPedido, REQUEST_CODE_MEJOR);
                break;
            case R.id.buttonMejorGrupo:
                Intent intentMejorGrupo = new Intent(this, BestGroupInformation.class);
                startActivityForResult(intentMejorGrupo, REQUEST_CODE_GRUPO);
                break;
            case R.id.buttonMapa:
                Intent intentMapa = new Intent(this, Map.class);
                startActivityForResult(intentMapa, REQUEST_CODE_MAPA);
                break;
        }
    }

    public void mostrarPedidos() {
        String[] campos = new String[] {
                ConstantesDB.CLI_NOMBRE,
                ConstantesDB.CLI_COD_POSTAL,
                ConstantesDB.CLI_DIST,
                ConstantesDB.CLI_PRECIO,
        };
        int[] to = new int[] {
                R.id.nombreListView,
                R.id.telefonoListView,
                R.id.emailListView,
                R.id.precioListView
        };
        ListView listaPedidos = (ListView)findViewById(R.id.listViewPedidos);
        dataAdapter = new SimpleCursorAdapter(this, R.layout.info_pedido,
                db.todosPedidos(), campos, to, 0);

        listaPedidos.setAdapter(dataAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(resultCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_MEJOR) {
            if (data.hasExtra("eliminado")) {
                String c = data.getExtras().getString("eliminado");
                Toast.makeText(this, c, Toast.LENGTH_LONG).show();
            } else if (data.hasExtra("vacio")) {
                String c = data.getExtras().getString("vacio");
                Toast.makeText(this, c, Toast.LENGTH_LONG).show();
            }
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_GRUPO) {
            if (data.hasExtra("eliminado")) {
                String c = data.getExtras().getString("eliminado");
                Toast.makeText(this, c, Toast.LENGTH_LONG).show();
            } else if (data.hasExtra("vacio")) {
                String c = data.getExtras().getString("vacio");
                Toast.makeText(this, c, Toast.LENGTH_LONG).show();
            }
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_MAPA) {
            if (data.hasExtra("error")) {
                String c = data.getExtras().getString("error");
                Toast.makeText(this, c, Toast.LENGTH_LONG).show();
            }
        }
    }
}

