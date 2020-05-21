package es.josefons.reactown;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import es.josefons.reactown.objetos.ItemListado;
import es.josefons.reactown.objetos.Usuario;


public class ListaUsuarios extends Fragment {

    private ArrayList<Usuario> listaUsuarios;
    private RecyclerView recyclerListado;
    private View vista;
    private ListaUsuariosAdapter listaUsuariosAdapter;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private int permUsuarioClick;
    private String idUsuarioClick;


    public ListaUsuarios() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vista = container;
        return inflater.inflate(R.layout.fragment_lista_usuarios, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        cargarListadoUsuarios();
    }

    /**
     * Funcion para cargar los datos en el RecyclerView y darle sus funciones
     */
    private void cargarListadoUsuarios(){
        listaUsuarios = new ArrayList<>();
        recyclerListado = vista.findViewById(R.id.listaUsuario_recycler);
        listaUsuariosAdapter = new ListaUsuariosAdapter(listaUsuarios, getContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(vista.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerListado.setLayoutManager(layoutManager);

        recyclerListado.setItemAnimator(new DefaultItemAnimator());
        recyclerListado.setAdapter(listaUsuariosAdapter);

        cargarRecycler();

        listaUsuariosAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemPosition = recyclerListado.getChildLayoutPosition(v);
                idUsuarioClick = listaUsuarios.get(itemPosition).getId();
                permUsuarioClick = listaUsuarios.get(itemPosition).getPermiso();

                AlertDialog alertbox = new AlertDialog.Builder(v.getContext())
                        .setMessage("¿Quieres cambiar el permiso?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                if(permUsuarioClick ==  0){
                                    mDatabase.child("Users").child(idUsuarioClick).child("perm").setValue(1);
                                } else {
                                    mDatabase.child("Users").child(idUsuarioClick).child("perm").setValue(0);
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                //
                            }
                        })
                        .show();
            }
        });
    }

    /**
     * Funcion para obtener informacion desde Firebase
     */
    private void cargarRecycler(){
        mDatabase.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaUsuarios.clear();
                if(dataSnapshot.exists()){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Evitamos poner al mismo usuario que lo consulta para que no la lie borrandose a si mismo o quitandose permisos
                        if(!snapshot.getKey().equals(mAuth.getCurrentUser().getUid())){
                            Usuario aux = new Usuario();
                            aux.setId(snapshot.getKey());
                            aux.setCorreo(snapshot.child("correo").getValue().toString());
                            aux.setPermiso(Integer.parseInt(snapshot.child("perm").getValue().toString()));
                            listaUsuarios.add(aux);
                        }
                    }
                }
                listaUsuariosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError.getMessage());
            }
        });
    }
}
