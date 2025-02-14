package Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crud_app.R;

import java.util.List;

import Entities.Producto;

public class ListProductoAdapter extends RecyclerView.Adapter<ListProductoAdapter.ViewHolder> {
    private List<Producto> productos;
    private LayoutInflater mInflater;
    private Context contex;
    final ListProductoAdapter.OnItemClickListener listener;
    private boolean isBuying;

    public interface OnItemClickListener{
        void OnItemClick(Producto prod);
    }

    public ListProductoAdapter(List<Producto> productos, Context contex, boolean isBuying,ListProductoAdapter.OnItemClickListener listener) {
        this.productos = productos;
        this.mInflater = LayoutInflater.from(contex);
        this.contex = contex;
        this.listener=listener;
        this.isBuying=isBuying;
    }

    public void setFilterList(List<Producto> filterList){
        this.productos=filterList;
        notifyDataSetChanged();
    }

    @Override
    public ListProductoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.from(parent.getContext()).inflate(R.layout.card_producto, parent, false); //Relacion con la vista de lista
        return new ListProductoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ListProductoAdapter.ViewHolder holder, int position) {
        holder.cv2.setAnimation(AnimationUtils.loadAnimation(contex,R.anim.fade_transition));
        holder.bindData(productos.get(position));
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public void setItems(List<Producto>items){productos=items;}

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView iconImage;
        TextView description,status,price;
        CardView cv2;

        ViewHolder(View itemView){
            super(itemView);

            iconImage=itemView.findViewById(R.id.iconImageView);
            description=itemView.findViewById(R.id.nameTextView);
            price=itemView.findViewById(R.id.stockTextView);
            status=itemView.findViewById(R.id.statusTextView);
            cv2=itemView.findViewById(R.id.cv);
        }

        void bindData(final Producto items){
            iconImage.setColorFilter(Color.rgb(25,200,100), PorterDuff.Mode.SRC_IN);
            description.setText(items.getDescripcion());
            price.setText(isBuying ? "Precio: "+items.getPrice(): (items.getStatus().equals("true")) ? "disponible" : "no disponible");
            status.setText(isBuying ? (items.getStatus().equals("true")) ? "disponible" : "no disponible": "Stock: "+String.valueOf(items.getStock()) );
            iconImage.setImageResource(isBuying ? R.drawable.shopping_cart : R.drawable.swipe_up_fill0_wght400_grad0_opsz48);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnItemClick(items);
                }
            });
        }
    }
}

