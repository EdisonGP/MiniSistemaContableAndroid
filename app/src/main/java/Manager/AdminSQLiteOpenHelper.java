package Manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    public AdminSQLiteOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        //context.deleteDatabase("compras"); //Eliminar bbdd sqlite
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE usuario(id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,cedula text(11), nombre text(20),apellido text(20),correo text(20),usuario text(20), clave text(20),categoria text (13), estado text(5))");
        sqLiteDatabase.execSQL("CREATE TABLE producto(id_producto INTEGER PRIMARY KEY AUTOINCREMENT, descripcion text(20),stock text(4) ,status text (5),estado text (5),price text(10))");
        sqLiteDatabase.execSQL("CREATE TABLE pedido_factura(id_pedido_factura INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_cliente int," + //Cliente que va a realizar la compra
                "id_usuario int,"+  //Usuario que genera la factura
                "total text,"+
                "iva text(10),"+
                "fecha text(10)," +
                "despachado text (2),"+ //Saber si el factura ya ha sido despachado o no
                "facturado text (2),"+ //Saber si el pedido ya ha sido facturado o no
                "categoria text (6),"+   //Saber si si es un pedido o es una factura generada por el vendedor (admin) //Factura o pedido
                "estado text(5)," +
                "foreign key(id_cliente) references usuario(id_usuario))");
        sqLiteDatabase.execSQL("CREATE TABLE pedido_factura_det(id_det INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_pedido_factura int," +
                "id_producto int,"+
                "cantidad int,"+
                "subtotal text(10),"+
                "estado text(5)," +
                "foreign key(id_pedido_factura) references pedido_factura(id_pedido_factura)," +
                "foreign key(id_producto) references producto(id_producto))");
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}
}

