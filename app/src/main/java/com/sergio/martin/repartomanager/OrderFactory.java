package com.sergio.martin.repartomanager;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sergio.martin.repartomanager.aux.HelperDB;
import com.sergio.martin.repartomanager.aux.Order;

public class OrderFactory extends AppCompatActivity {

    EditText name, postCode, distance, price, phoneNumber, email;
    public static final String EMAIL_PATTERN = "\\w+@\\w+\\.\\w{2,3}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_pedido);

        name = findViewById(R.id.editTextNombreCliente);
        postCode = findViewById(R.id.editTextCodigoPostal);
        distance = findViewById(R.id.editTextDistancia);
        price = findViewById(R.id.editTextPrecio);
        phoneNumber = findViewById(R.id.editTextTelefono);
        email = findViewById(R.id.editTextEmail);

        Button save = findViewById(R.id.buttonGuardar);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String emailText = email.getText().toString();
                    Order order = new Order(name.getText().toString(),
                            postCode.getText().toString(),
                            Double.parseDouble(distance.getText().toString()),
                            Double.parseDouble(price.getText().toString()),
                            Integer.parseInt(phoneNumber.getText().toString()),
                            emailText);
                    if (emailText.matches(EMAIL_PATTERN)) {
                        saveOrder(order);
                    } else {
                        Toast.makeText(getBaseContext(), "Wrong Email", Toast.LENGTH_LONG).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getBaseContext(), "Missing Information", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void saveOrder(Order order) {
        HelperDB bd = new HelperDB(this);
        bd.insertarPedido(order);
        Toast.makeText(this, "Order Saved", Toast.LENGTH_SHORT).show();
        name.setText("");
        postCode.setText("");
        distance.setText("");
        price.setText("");
        phoneNumber.setText("");
        email.setText("");
    }
}
