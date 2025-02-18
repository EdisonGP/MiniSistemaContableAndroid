package Manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.List;

import Entities.DetallePedidoFactura;

public class ManagerDetallePedidoFactura {
    private AdminSQLiteOpenHelper adsql;
    private SQLiteDatabase db;

    public ManagerDetallePedidoFactura(Context context, String dbname, int version) {
        adsql = new AdminSQLiteOpenHelper(context, dbname, null, version);
    }

    public boolean insertPedidoFacturaDetalle(List<DetallePedidoFactura> det, long idPedidoFactura) {
        db = adsql.getWritableDatabase();
        for (int i = 0; i < det.size(); i++) {
            ContentValues data = new ContentValues();
            data.put("id_pedido_factura", idPedidoFactura);
            data.put("id_producto", det.get(i).getIdProducto());
            data.put("cantidad", det.get(i).getCantidad());
            data.put("subtotal", det.get(i).getSubtotal());
            data.put("estado", "true");
            db.insert("pedido_factura_det", null, data);
        }
        db.close();
        return true;
    }

    public DetallePedidoFactura[] getPedidoFacturaDetalle(int id) {
        db = adsql.getReadableDatabase();
        String[] param = new String[1];
        param[0]= String.valueOf(id);
        Cursor cursor = db.rawQuery("SELECT fac.id_pedido_factura,prod.descripcion, det.cantidad,det.subtotal FROM pedido_factura_det det INNER JOIN producto prod ON det.id_producto=prod.id_producto INNER JOIN pedido_factura fac ON fac.id_pedido_factura=det.id_pedido_factura  WHERE fac.id_pedido_factura=?", param);
        DetallePedidoFactura[] ps;
        DetallePedidoFactura p;
        int i=0;
        if (cursor.getCount() <= 0) {
            return null;
        } else {
            ps=new DetallePedidoFactura[cursor.getCount()];
            while (cursor.moveToNext()) {
                p = new DetallePedidoFactura();
                p.setId_pedido(cursor.getInt(0));
                p.setDescripcionProducto(cursor.getString(1));
                p.setCantidad(cursor.getInt(2));
                p.setSubtotal(cursor.getString(3));
                ps[i++] = p;
            }
        }
        return ps;
    }

    public int countDetalle (){
        db=adsql.getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM pedido_factura_det",null);
        int count=cursor.getCount();
        db.close();
        return  count;
    }
}

