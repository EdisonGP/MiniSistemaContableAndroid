package Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crud_app.R;

import java.util.ArrayList;
import java.util.List;

import Entities.User;
import Manager.ManagerUser;

public class ListClientesAdapter extends RecyclerView.Adapter<ListClientesAdapter.ViewHolder> {
    private List<User> clientes;
    private LayoutInflater mInflater;
    private Context contex;
    final ListClientesAdapter.OnItemClickListener listener;
    ManagerUser mCliente;

    public interface OnItemClickListener{
        void OnItemClick(User cli);
    }

    public ListClientesAdapter(List<User> clientes, Context contex, ManagerUser mCli,ListClientesAdapter.OnItemClickListener listener) {
        this.clientes = new ArrayList<>(clientes); // Convertir a ArrayList para que sea modificable
        this.mInflater = LayoutInflater.from(contex);
        this.contex = contex;
        this.listener=listener;
        mCliente=mCli;
    }
    public void setFilterList(List<User> filterList){
        this.clientes=filterList;
        notifyDataSetChanged();
    }

    @Override
    public ListClientesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.from(parent.getContext()).inflate(R.layout.card_cliente, parent, false); //Relacion con la vista de lista
        return new ListClientesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ListClientesAdapter.ViewHolder holder, int position) {
        holder.cv.setAnimation(AnimationUtils.loadAnimation(contex,R.anim.fade_transition));
        holder.bindData(clientes.get(position));
    }

    @Override
    public int getItemCount() {
        return clientes.size();
    }

    public void setItems(List<User>items){clientes=items;}

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView client,correo;
        Button btn;
        CardView cv;

        ViewHolder(View itemView){
            super(itemView);
            client=itemView.findViewById(R.id.cliente);
            correo=itemView.findViewById(R.id.correo);
            cv=itemView.findViewById(R.id.cvCli);
            btn=itemView.findViewById(R.id.Btn);

        }

        void bindData(final User items){
            client.setText(items.getApellido()+" "+items.getNombre());
            correo.setText(items.getCorreo());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnItemClick(items);
                }
            });

            // Evento cuando se presiona el botón
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mCliente.deleteUser(items.getIdUsuario()); // Eliminar de la base de datos
                        clientes.remove(position); // Eliminar de la lista en el Adapter
                        notifyItemRemoved(position); // Notificar eliminación
                        notifyItemRangeChanged(position, clientes.size()); // Actualizar la vista
                        Toast.makeText(itemView.getContext(), "Cliente eliminado correctamente", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}

