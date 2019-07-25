package com.sergio.martin.repartomanager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sergio.martin.repartomanager.aux.HelperDB;

import java.util.ArrayList;

public class BestOrderInformation extends AppCompatActivity implements View.OnClickListener {

    private int[] IDs = new int[]{R.id.buttonLlamar, R.id.buttonEmail,
            R.id.buttonBorrar, R.id.buttonAtras};

    private String code;
    private String name;
    private String phoneNumber;
    private String email;
    private boolean isDeleted = false;
    private boolean empty = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_mejor_pedido);

        TextView name = findViewById(R.id.textViewnombreInfoMejorPedido);
        TextView phoneNumber = findViewById(R.id.textViewTelefonoInfoMejorPedido);
        TextView email = findViewById(R.id.TextViewEmailInfoMejorPedido);

        Bundle extras = getIntent().getExtras();
        ArrayList information = extras.getStringArrayList("datosContacto");

        try {
            if (information != null) {
                code = String.valueOf(information.get(0));
                this.name = String.valueOf(information.get(1));
                this.phoneNumber = String.valueOf(information.get(2));
                this.email = String.valueOf(information.get(3));
            }
        } catch (IndexOutOfBoundsException e) {
            empty = true;
            finish();
        }

        name.setText(this.name);
        phoneNumber.setText(this.phoneNumber);
        email.setText(this.email);

        for (int id : IDs) {
            Button button = findViewById(id);
            button.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonLlamar:
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", phoneNumber, null));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(callIntent);
                break;

            case R.id.buttonEmail:
                String cuerpoEmail = "Sr. " + name + ",\n\n" + "Hoy pasaremos por su domicilio " +
                        "para realizar la entrega del pedido que usted realizó. \n\n" +
                        "Un cordial saludo.";
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("message/rfc822");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Estamos en camino!");
                emailIntent.putExtra(Intent.EXTRA_TEXT, cuerpoEmail);
                startActivity(emailIntent);
                break;

            case R.id.buttonBorrar:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        HelperDB db = new HelperDB(getBaseContext());
                        db.openEscritura();
                        db.eliminarPedido(code);
                        db.closeDB();
                        isDeleted = true;
                        finish();
                    }
                });
                builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                builder.setTitle("Se eliminara el pedido");
                AlertDialog dialog = builder.create();
                dialog.show();
                break;

            case R.id.buttonAtras:
                finish();
                break;
        }
    }

    @Override
    public void finish() {
        Intent intentEliminado = new Intent();
        if (isDeleted) intentEliminado.putExtra("eliminado", "Order eliminado");
        if (empty) intentEliminado.putExtra("empty", "No existen registros");
        setResult(RESULT_OK, intentEliminado);
        super.finish();
    }
}
