package es.josefons.reactown;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ItemListadoAdapter extends RecyclerView.Adapter<ItemListadoAdapter.ItemListadoViewHolder> {

    private List<ItemListado> listado;
    private Context contexto;
    private ItemClickListener itemClickListener;

    public ItemListadoAdapter(List<ItemListado> listado, Context contexto) {
        this.listado = listado;
        this.contexto = contexto;
    }

    @Override
    public ItemListadoAdapter.ItemListadoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(contexto).inflate(R.layout.layout_item_listado, parent, false);
        ItemListadoViewHolder vh = new ItemListadoViewHolder(itemView);
        return vh;
    }


    @Override
    public void onBindViewHolder(ItemListadoViewHolder holder, int position) {
        //holder.icono.setImageResource(listado.get(position).getIcon());   <--------- DESCOMENTAR
        holder.nombre.setText(listado.get(position).getName());
        holder.autor.setText(listado.get(position).getAutor());
        Picasso.get()
                .load(listado.get(position).getIcon())
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.icono);
    }

    @Override
    public int getItemCount() {
        return listado.size();
    }

    public class ItemListadoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView icono;
        TextView nombre, autor;

        public ItemListadoViewHolder (View view) {
            super(view);
            icono = view.findViewById(R.id.listadoIcon);
            nombre = view.findViewById(R.id.listadoName);
            autor = view.findViewById(R.id.listadoAutor);
        }

        @Override
        public void onClick(View view) {
        }
    }
}
