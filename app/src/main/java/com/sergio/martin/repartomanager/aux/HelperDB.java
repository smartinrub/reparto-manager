package com.sergio.martin.repartomanager.aux;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Sergio Martin Rubio on 7/24/16.
 */
public class HelperDB {

    private SQLiteDatabase db;
    private BaseDatosHelper BDHelper;

    public HelperDB(Context context) {
        BDHelper = new BaseDatosHelper(context);
    }

    private void openLectura() {
        db = BDHelper.getReadableDatabase();
    }

    public void openEscritura() {
        db = BDHelper.getWritableDatabase();
    }

    public void closeDB() {
        if (db != null) {
            db.close();
        }
    }

    private static class BaseDatosHelper extends SQLiteOpenHelper {

        public BaseDatosHelper(Context context) {
            super(context, ConstantesDB.NOMBRE_BD, null, ConstantesDB.VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(ConstantesDB.TABLA_PEDIDOS_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }

    private ContentValues getContentValues(Order order) {
        ContentValues cv = new ContentValues();
        cv.put(ConstantesDB.CLI_NOMBRE, order.getName());
        cv.put(ConstantesDB.CLI_COD_POSTAL, order.getPostCode());
        cv.put(ConstantesDB.CLI_DIST, order.getDistance());
        cv.put(ConstantesDB.CLI_PRECIO, order.getPrice());
        cv.put(ConstantesDB.CLI_TEL, order.getPhoneNumber());
        cv.put(ConstantesDB.CLI_EMAIL, order.getEmail());

        return cv;
    }

    public long insertarPedido(Order order) {
        this.openEscritura();
        long rowID = db.insert(ConstantesDB.TABLA_PEDIDOS, null, getContentValues(order));
        this.closeDB();
        return rowID;
    }

    public void eliminarPedido(String id) {
        this.openEscritura();
        db.execSQL("DELETE FROM pedidos where _id=" + id);
        this.closeDB();
    }

    public void eliminarGrupo() {
        this.openEscritura();
        db.delete(ConstantesDB.TABLA_PEDIDOS, ConstantesDB.CLI_COD_POSTAL
                + "= ?", new String[]{codigoPostalMejorGrupo()});
        this.closeDB();
    }

    public Cursor todosPedidos() {
        String[] p = new String[]{ConstantesDB.CLI_CODIGO, ConstantesDB.CLI_NOMBRE, ConstantesDB.CLI_COD_POSTAL,
                ConstantesDB.CLI_DIST, ConstantesDB.CLI_PRECIO};
        this.openLectura();
        Cursor cursor = db.query(ConstantesDB.TABLA_PEDIDOS, p, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        this.closeDB();
        return cursor;
    }

    public ArrayList buscarMejorPedido() {
        ArrayList<String> infoPedido = new ArrayList<>();
        this.openLectura();
        Cursor cursor = db.rawQuery("SELECT _id, nombre, telefono, email FROM pedidos WHERE _id = (SELECT MIN(_id) " +
                "FROM pedidos WHERE precio IN (SELECT MAX(precio) FROM pedidos WHERE " +
                "distancia IN (SELECT MIN(distancia) FROM pedidos)))", null);

        if (cursor.moveToFirst()) {
            infoPedido.add(cursor.getString(0));
            infoPedido.add(cursor.getString(1));
            infoPedido.add(cursor.getString(2));
            infoPedido.add(cursor.getString(3));
            Log.i("order info", cursor.getString(0) + " - " + cursor.getString(1) + " - " +
                    cursor.getString(2) + " - " + cursor.getString(3));
        }
        this.closeDB();
        cursor.close();
        return infoPedido;
    }

    public String codigoPostalMejorGrupo() {
        this.openLectura();

        Cursor cursor = db.rawQuery("select codigo_postal, max(derivada.total) " +
                "from (select codigo_postal, sum(precio) " +
                "as total from pedidos group by codigo_postal)derivada", null);

        cursor.moveToFirst();

        String postCode = cursor.getString(0);
        cursor.close();

        return postCode;
    }

    public Cursor buscarMejorGrupo() {
        this.openLectura();
        // Creamos una tabla temporal - ESTA CONSULTA NO FUNCIONA EN ANDROID!!!
        /*Cursor cursor = db.rawQuery("select _id, nombre, codigo_postal, distancia, " +
                "precio from pedidos where codigo_postal = (select derivada2.codigo_postal " +
                "from (select codigo_postal, max(derivada.total) from (select codigo_postal, " +
                "sum(precio) as total from pedidos group by codigo_postal)derivada)derivada2)", null);

        cursor.moveToFirst();
        */

        Cursor cursor = db.rawQuery("select _id, nombre, telefono, email" +
                " from pedidos where codigo_postal=?", new String[]{codigoPostalMejorGrupo()});
        cursor.moveToFirst();

        this.closeDB();
        return cursor;
    }

    public ArrayList<String> getCodigosPostales() {
        ArrayList<String> codigosPostales = new ArrayList<>();
        this.openLectura();
        Cursor cursor = db.rawQuery("SELECT DISTINCT codigo_postal FROM pedidos", null);
        if (cursor.moveToFirst()) {
            do {
                codigosPostales.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        this.closeDB();
        cursor.close();
        return codigosPostales;
    }

    public String cantPedidosPorCP(String cp) {
        this.openLectura();
        String count = "";
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM pedidos WHERE codigo_postal=?", new String[]{cp});
        if (cursor.moveToFirst()) {
            count = cursor.getString(0);
        }
        cursor.close();
        return count;
    }
}
