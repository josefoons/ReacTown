package es.josefons.reactown.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.josefons.reactown.R;
import es.josefons.reactown.objetos.Usuario;

public class ListaUsuariosAdapter extends RecyclerView.Adapter<ListaUsuariosAdapter.ListaUsuarioViewHolder> implements View.OnClickListener{

    private List<Usuario> listado;
    private Context contexto;
    private View.OnClickListener listener;

    public ListaUsuariosAdapter(List<Usuario> listado, Context contexto) {
        this.listado = listado;
        this.contexto = contexto;
    }

    @Override
    public ListaUsuariosAdapter.ListaUsuarioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(contexto).inflate(R.layout.layout_usuario_listado, parent, false);
        ListaUsuarioViewHolder vh = new ListaUsuarioViewHolder(itemView);
        itemView.setOnClickListener(this);
        return vh;
    }


    @Override
    public void onBindViewHolder(ListaUsuarioViewHolder holder, int position) {
        holder.nombre.setText(listado.get(position).getCorreo());
        if(listado.get(position).getPermiso() == 1) {
            holder.permiso.setVisibility(View.VISIBLE);
            holder.permiso.setImageResource(R.drawable.ic_rank);
        } else {
            holder.permiso.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return listado.size();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if(listener != null) {
            listener.onClick(v);
        }
    }

    public class ListaUsuarioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView permiso;
        TextView nombre;

        public ListaUsuarioViewHolder (View view) {
            super(view);
            permiso = view.findViewById(R.id.listaUsuario_Perm);
            nombre = view.findViewById(R.id.listaUsuario_Nombre);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
