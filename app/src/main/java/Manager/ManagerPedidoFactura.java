package Manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.MessageFormat;

import Entities.PedidoFactura;

public class ManagerPedidoFactura {
    private AdminSQLiteOpenHelper adsql;
    private SQLiteDatabase db;

    public ManagerPedidoFactura(Context context, String dbname, int version){
        adsql =new AdminSQLiteOpenHelper(context,dbname,null,version);
    }

    public PedidoFactura[] allPedidoFacturas(String categoria){
        db=adsql.getReadableDatabase();
        String[] param = new String[1];
        param[0]= categoria;
        Cursor cursor;
        if (categoria.equals("pedido")) {
            cursor= db.rawQuery("SELECT pf.id_pedido_factura,cli.cedula,cli.nombre,cli.apellido,pf.total,pf.iva,pf.fecha FROM pedido_factura pf INNER JOIN usuario cli ON cli.id_usuario=pf.id_cliente WHERE pf.categoria=? AND pf.estado='true'",param);
        }else{
            cursor= db.rawQuery("SELECT pf.id_pedido_factura,cli.cedula,cli.nombre,cli.apellido,pf.total,pf.iva,pf.fecha, pf.despachado,pf.facturado FROM pedido_factura pf INNER JOIN usuario cli ON cli.id_usuario=pf.id_cliente WHERE pf.categoria=? AND pf.estado='true'", param);
        }

        PedidoFactura[] ps;
        PedidoFactura p;
        int i=0;
        if(cursor.getCount()<=0) {
            return null;
        }else{
            ps=new PedidoFactura[cursor.getCount()];
            while (cursor.moveToNext()){
                p=new PedidoFactura();
                p.setId(cursor.getInt(0));
                p.setCedulaCliente(cursor.getString(1));
                p.setNombreCliente(cursor.getString(2));
                p.setApellidoCliente(cursor.getString(3));
                p.setTotal(cursor.getString(4));
                p.setIva(cursor.getString(5));
                p.setFecha(cursor.getString(6));
                p.setCategoria(categoria); //Pedido-Factura
                p.setDespachado((categoria.equals("factura"))? cursor.getString(7) : "");
                p.setFacturado((categoria.equals("factura"))? cursor.getString(8) : "");
                ps[i++]=p;
            }
            return ps;
        }
    }

    public long insertPedidoFactura(String idCliente, String idUsuario, String total, String iva, String fecha,String categoria){
        db=adsql.getWritableDatabase();
        ContentValues data=new ContentValues();
        data.put("id_cliente",idCliente);
        data.put("id_usuario",idUsuario);
        data.put("total",total);
        data.put("iva",iva);
        data.put("fecha",fecha);
        data.put("despachado","no");
        data.put("facturado","no");
        data.put("categoria",categoria);
        data.put("estado","true");
        long idFactura =db.insert("pedido_factura",null,data);
        db.close();

        return idFactura; // Retorna el ID insertado
    }

    public PedidoFactura getPedidoFactura(int id,String categoria){
        db=adsql.getReadableDatabase();
        String[] param = new String[2];
        param[0]= categoria;
        param[1]= String.valueOf(id);
        Cursor cursor;
        if (categoria.equals("pedido")) {
            cursor= db.rawQuery("SELECT pf.id_pedido_factura,cli.cedula,cli.nombre,cli.apellido,pf.total,pf.iva,pf.fecha FROM pedido_factura pf INNER JOIN usuario cli ON cli.id_usuario=pf.id_cliente WHERE pf.categoria=? AND pf.id_pedido_factura=?",param);
        }else{
            cursor= db.rawQuery("SELECT pf.id_pedido_factura,cli.cedula,cli.nombre,cli.apellido,pf.total,pf.iva,pf.fecha, pf.despachado,pf.facturado FROM pedido_factura pf INNER JOIN usuario cli ON cli.id_usuario=pf.id_cliente WHERE pf.categoria=? AND pf.id_pedido_factura=?", param);
        }

        if(cursor.getCount()<=0) {
            return null;
        }else{
            PedidoFactura p=new PedidoFactura();
            cursor.moveToFirst();
            p.setId(cursor.getInt(0));
            p.setCedulaCliente(cursor.getString(1));
            p.setNombreCliente(cursor.getString(2));
            p.setApellidoCliente(cursor.getString(3));
            p.setTotal(cursor.getString(4));
            p.setIva(cursor.getString(5));
            p.setFecha(cursor.getString(6));
            p.setCategoria(categoria); //Pedido-Factura
            p.setDespachado((categoria.equals("factura"))? cursor.getString(7) : "");
            p.setFacturado((categoria.equals("factura"))? cursor.getString(8) : "");
            return p;
        }
    }

    public boolean updateFacturaDespachado(int id, String despachado){
        db=adsql.getWritableDatabase();
        String[] param = new String[2];
        param[0]= despachado;
        param[1]= String.valueOf(id);
        db.execSQL("UPDATE pedido_factura SET despachado=? WHERE id_pedido_factura=?",param);
        db.close();
        return true;
    }
    public boolean updatePedidoFacturado(int id, String facturado){
        db=adsql.getWritableDatabase();
        String[] param = new String[2];
        param[0]= facturado;
        param[1]= String.valueOf(id);
        Log.i("OB",+id+" "+facturado);
        db.execSQL("UPDATE pedido_factura SET facturado=? WHERE id_pedido_factura=?",param);
        db.close();
        return true;
    }
}
