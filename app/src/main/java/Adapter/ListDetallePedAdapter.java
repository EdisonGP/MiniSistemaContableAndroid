package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crud_app.R;

import java.util.List;

import Entities.DetallePedidoFactura;

public class ListDetallePedAdapter extends RecyclerView.Adapter<ListDetallePedAdapter.ViewHolder> {
    private List<DetallePedidoFactura> pedidoDetalles;
    private LayoutInflater mInflater;
    private Context contex;
    int opcion;
    ListDetallePedAdapter.OnItemClickListener listener;

    public interface OnItemClickListener{
        void OnItemClick(DetallePedidoFactura cli, int position);
    }

    public ListDetallePedAdapter(int opcion, List<DetallePedidoFactura> PedidoDetalles, Context contex, ListDetallePedAdapter.OnItemClickListener listener) {
        this.pedidoDetalles = PedidoDetalles;
        this.mInflater = LayoutInflater.from(contex);
        this.contex = contex;
        this.listener=listener;
        this.opcion=opcion;
    }

    @Override
    public ListDetallePedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.from(parent.getContext()).inflate(R.layout.card_detalle_pedido_factura, parent, false); //Relacion con la vista de lista
        return new ListDetallePedAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ListDetallePedAdapter.ViewHolder holder, int position) {
        holder.cv.setAnimation(AnimationUtils.loadAnimation(contex,R.anim.fade_transition));
        holder.bindData(pedidoDetalles.get(position),position);
    }

    @Override
    public int getItemCount() {
        return pedidoDetalles.size();
    }

    public List<DetallePedidoFactura> getPedidoDetalles(){
        return pedidoDetalles;
    }


    public void setItems(List<DetallePedidoFactura>items){pedidoDetalles=items;}

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView descripcion,cantidad,subtotal,eliminar;
        CardView cv;

        ViewHolder(View itemView){
            super(itemView);
            descripcion=itemView.findViewById(R.id.descripcionDetalle);
            cantidad=itemView.findViewById(R.id.cantidadDetalle);
            subtotal=itemView.findViewById(R.id.subTotalDetalle);
            eliminar=itemView.findViewById(R.id.btnEliminarDetPedido);
            cv=itemView.findViewById(R.id.cvDet);
        }

        void bindData(final DetallePedidoFactura items, int position){
            eliminar.setVisibility((opcion==0) ? View.GONE : View.VISIBLE);
            descripcion.setText(items.getDescripcionProducto());
            cantidad.setText(items.getCantidad()+"");
            subtotal.setText(items.getSubtotal());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnItemClick(items,position);
                }
            });
        }
    }
}

