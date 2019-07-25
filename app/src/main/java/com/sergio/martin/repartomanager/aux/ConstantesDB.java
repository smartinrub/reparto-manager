package com.sergio.martin.repartomanager.aux;

/**
 * Created by Sergio Martin Rubio on 7/24/16.
 */
public class ConstantesDB {

    static final String NOMBRE_BD = "pedidosBD.db";
    static final int VERSION = 1;

    static final String TABLA_PEDIDOS = "pedidos";

    static final String CLI_CODIGO = "_id";
    public static final String CLI_NOMBRE = "nombre";
    public static final String CLI_COD_POSTAL = "codigo_postal";
    public static final String CLI_DIST = "distancia";
    public static final String CLI_PRECIO = "precio";
    public static final String CLI_TEL = "telefono";
    public static final String CLI_EMAIL = "email";

    static String TABLA_PEDIDOS_SQL = "CREATE TABLE " + TABLA_PEDIDOS + "(" + CLI_CODIGO +
            " INTEGER PRIMARY KEY AUTOINCREMENT, " +  CLI_NOMBRE + " TEXT NOT NULL, "
            + CLI_COD_POSTAL + " INTEGER NOT NULL, " + CLI_DIST + " REAL NOT NULL, " + CLI_PRECIO +
            " REAL NOT NULL, " + CLI_TEL + " INTEGER NULL, " + CLI_EMAIL + " TEXT);";
}
