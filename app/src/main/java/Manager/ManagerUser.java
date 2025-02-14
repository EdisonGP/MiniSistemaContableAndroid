package Manager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.content.ContentValues;

import Entities.User;

public class ManagerUser {
    private AdminSQLiteOpenHelper adsql;
    private SQLiteDatabase db;

    public ManagerUser(Context context, String dbname, int version){
        adsql =new AdminSQLiteOpenHelper(context,dbname,null,version);
    }

    public User[] allUser(String categoria){
        db=adsql.getReadableDatabase();
        String[] param = new String[2];
        param[0]=categoria;
        param[1]="true";
        Cursor cursor=db.rawQuery("SELECT * FROM usuario WHERE categoria=? AND estado=?",param);
        User [] ps;
        User p;
        int i=0;
        if(cursor.getCount()<=0) {
            return null;
        }else{
            ps=new User[cursor.getCount()];
            while (cursor.moveToNext()){
                p=new User();
                p.setIdUsuario(cursor.getInt(0));
                p.setCedula(cursor.getString(1));
                p.setNombre(cursor.getString(2));
                p.setApellido(cursor.getString(3));
                p.setCorreo(cursor.getString(4));
                p.setUsuario(cursor.getString(5));
                p.setClave(cursor.getString(6));
                p.setCategoria(cursor.getString(7));
                p.setEstado(cursor.getString(8));
                ps[i++]=p;
            }
            return ps;
        }
    }

    public boolean insertUser(String cedula,String nombre, String apellido, String correo,String usuario,String clave, String categoria){
        db=adsql.getWritableDatabase();

        try {
            ContentValues data=new ContentValues();
            data.put("cedula",cedula);
            data.put("nombre",nombre);
            data.put("apellido",apellido);
            data.put("correo",correo);
            data.put("usuario",usuario);
            data.put("clave",clave);
            data.put("categoria",categoria);
            data.put("estado","true");
            db.insert("usuario",null,data);
            db.close();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public boolean updateUser(int id, String nombre, String apellido, String correo,String usuario,String clave){
        db=adsql.getWritableDatabase();
        ContentValues data=new ContentValues();
        data.put("id_usuario",id);
        data.put("nombre",nombre);
        data.put("apellido",apellido);
        data.put("correo",correo);
        data.put("usuario",usuario);
        data.put("clave",clave);
        db.update("usuario",data,"id_usuario="+id,null);
        db.close();
        return true;
    }

    public boolean deleteUser(int id){
        db=adsql.getWritableDatabase();
        String[] param = new String[2];
        param[0]="false";
        param[1]=String.valueOf(id);
        db.execSQL("UPDATE usuario SET estado=? WHERE id_usuario=?",param);
        db.close();
        return true;
    }

    public User getUser(int id){
        db=adsql.getReadableDatabase();
        String[] param = new String[2];
        param[0]= String.valueOf(id);
        param[1]="true";
        Cursor cursor=db.rawQuery("SELECT * FROM usuario WHERE id_usuario=? AND estado=?",param);
        User p;
        if(cursor.getCount()<=0) {
            return null;
        }else{
                p=new User();
                cursor.moveToNext();
                p.setIdUsuario(cursor.getInt(0));
                p.setCedula(cursor.getString(1));
                p.setNombre(cursor.getString(2));
                p.setApellido(cursor.getString(3));
                p.setCorreo(cursor.getString(4));
                p.setUsuario(cursor.getString(5));
                p.setClave(cursor.getString(6));
                p.setCategoria(cursor.getString(7));
        }
        return p;
    }

    public User login(String usuario,String clave,String categoria){
        db=adsql.getReadableDatabase();
        String[] param = new String[4];
        param[0]= usuario;
        param[1]= clave;
        param[2]= categoria;
        param[3]= "true";
        Cursor cursor=db.rawQuery("SELECT * FROM usuario cli WHERE cli.usuario=? AND cli.clave=? AND cli.categoria=? AND cli.estado=?",param);

        if(cursor.getCount()<=0) {
            return null;
        }else{
            User  p=new User ();
            cursor.moveToFirst();
            p.setIdUsuario(cursor.getInt(0));
            p.setCedula(cursor.getString(1));
            p.setNombre(cursor.getString(2));
            p.setApellido(cursor.getString(3));
            p.setCorreo(cursor.getString(4));
            p.setUsuario(cursor.getString(5));
            p.setClave(cursor.getString(6));
            p.setCategoria(cursor.getString(7));
            return p;
        }
    }
    public int countUser (){
        db=adsql.getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM usuario",null);
        int count=cursor.getCount();
        db.close();
        return  count;
    }
}
