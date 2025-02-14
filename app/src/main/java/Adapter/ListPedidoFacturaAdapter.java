package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crud_app.R;

import java.util.List;

import Entities.PedidoFactura;
import Manager.ManagerPedidoFactura;

public class ListPedidoFacturaAdapter extends RecyclerView.Adapter<ListPedidoFacturaAdapter.ViewHolder> {
    private List<PedidoFactura> facturas;
    private LayoutInflater mInflater;
    private Context contex;
    String categoria;
    final ListPedidoFacturaAdapter.OnItemClickListener listener;
    ManagerPedidoFactura gestion;

    public interface OnItemClickListener{
        void OnItemClick(PedidoFactura prod);
    }

   public ListPedidoFacturaAdapter(List<PedidoFactura> facturas, String categoria, ManagerPedidoFactura pf,Context contex, ListPedidoFacturaAdapter.OnItemClickListener listener) {
        this.facturas = facturas;
        this.categoria=categoria;
        this.gestion=pf;
        this.mInflater = LayoutInflater.from(contex);
        this.contex = contex;
        this.listener=listener;
    }



    public void setFilterList(List<PedidoFactura> filterList){
        this.facturas=filterList;
        notifyDataSetChanged();
    }

    @Override
    public ListPedidoFacturaAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.from(parent.getContext()).inflate(R.layout.card_pedido_factura, parent, false); //Relacion con la vista de lista
        return new ListPedidoFacturaAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ListPedidoFacturaAdapter.ViewHolder holder, int position) {
        holder.cv.setAnimation(AnimationUtils.loadAnimation(contex,R.anim.fade_transition));
        holder.bindData(facturas.get(position));
    }

    @Override
    public int getItemCount() {
        return facturas.size();
    }

    public void setItems(List<PedidoFactura>items){facturas=items;}

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView client,estadoPF;
        Button btn;
        CardView cv;

        ViewHolder(View itemView){
            super(itemView);

            client=itemView.findViewById(R.id.cliente);
            estadoPF=itemView.findViewById(R.id.estadoPF);
            btn=itemView.findViewById(R.id.Btn);
            cv=itemView.findViewById(R.id.cvPed);
        }

        void bindData(final PedidoFactura items){
            client.setText(items.getApellidoCliente()+" "+items.getNombreCliente());

            if(categoria.equals("pedido")){
                estadoPF.setText((items.getFacturado().equals("si")) ? "facturado": "sin facturar");
                btn.setText("Facturar");
            }else{
                estadoPF.setText((items.getDespachado().equals("si")) ? "despachado": "sin despachar");
                btn.setText("Despachar");
            }

            // Evento cuando se presiona el botón
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(categoria.equals("pedido")){
                        gestion.updatePedidoFacturado(items.getId(),"si");   //Factura el pedido
                        items.setFacturado("si"); // Actualiza el estado en la lista
                        Toast.makeText(itemView.getContext(),"Pedido facturado correctmente", Toast.LENGTH_SHORT).show();
                    }else{
                        gestion.updateFacturaDespachado(items.getId(),"si"); //Despacha la factura
                        items.setDespachado("si"); // Actualiza el estado en la lista
                        Toast.makeText(itemView.getContext(),"Factura despachado correctmente", Toast.LENGTH_SHORT).show();
                    }
                    notifyItemChanged(getAdapterPosition()); // Refresca el item en la lista
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnItemClick(items);
                }
            });
        }
    }
}

