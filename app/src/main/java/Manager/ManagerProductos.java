package Manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.List;

import Entities.DetallePedidoFactura;
import Entities.Producto;

public class ManagerProductos {
    private AdminSQLiteOpenHelper adsql;
    private SQLiteDatabase db;

    public ManagerProductos(Context context, String dbname, int version){
        adsql =new AdminSQLiteOpenHelper(context,dbname,null,version);
    }

    public Producto[] allProductos(){
        db=adsql.getReadableDatabase();
        String[] param = new String[1];
        param[0]="true";
        Cursor cursor=db.rawQuery("SELECT * FROM producto WHERE estado=? ORDER BY descripcion",param);
        Producto [] ps;
        Producto p;
        int i=0;
        if(cursor.getCount()<=0) {
            return null;
        }else{
            ps=new Producto[cursor.getCount()];
            while (cursor.moveToNext()){
                p=new Producto();
                p.setId(cursor.getInt(0));
                p.setDescripcion(cursor.getString(1));
                p.setStock(cursor.getString(2));
                p.setStatus(cursor.getString(3));
                p.setPrice(cursor.getString(5));
                ps[i++]=p;
            }
            return ps;
        }
    }

    public boolean insertProducto(String descripcion,int stock,String disponible,String price){
        db=adsql.getWritableDatabase();
        ContentValues data=new ContentValues();
        data.put("descripcion",descripcion);
        data.put("stock",String.valueOf(stock));
        data.put("status",disponible);
        data.put("estado","true");
        data.put("price",price);
        Log.i("precio",""+price);
        db.insert("producto",null,data);
        db.close();
        return true;
    }

    public boolean updateProducto(Producto prod){
        db=adsql.getWritableDatabase();
        ContentValues data=new ContentValues();
        data.put("id_producto",prod.getId());
        data.put("descripcion",prod.getDescripcion());
        data.put("stock",prod.getStock());
        data.put("status",prod.getStatus());
        data.put("price",prod.getPrice());
        db.update("producto",data,"id_producto="+prod.getId(),null);
        db.close();
        return true;
    }

    public boolean deleteProducto(int id){
        db=adsql.getWritableDatabase();
        String[] param = new String[2];
        param[0]="false";
        param[1]=String.valueOf(id);
        db.execSQL("UPDATE producto SET estado=? WHERE id_producto=?",param);
        db.close();
        return true;
    }
    public boolean updateStockProducto(List<DetallePedidoFactura> det){
        db=adsql.getWritableDatabase();

        for (int i = 0; i < det.size(); i++) {
            int cantidad=det.get(i).getCantidad();
            Producto prod=getProducto(det.get(i).getIdProducto());
            int stockActual=Integer.parseInt(prod.getStock());
            String[] param = new String[2];
            param[0]=String.valueOf(stockActual-cantidad);
            param[1]=String.valueOf(det.get(i).getIdProducto());
            db.execSQL("UPDATE producto SET stock=? WHERE id_producto=?",param);
        }
        db.close();
        return true;
    }
    public Producto getProducto(int id){
        db=adsql.getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM producto WHERE id_producto="+id,null);

        if(cursor.getCount()<=0) {
            return null;
        }else{
            Producto p=new Producto();
            cursor.moveToFirst();
            p.setId(cursor.getInt(0));
            p.setDescripcion(cursor.getString(1));
            p.setStock(cursor.getString(2));
            p.setStatus(cursor.getString(3));
            p.setPrice(cursor.getString(4));
            return p;
        }
    }
}
