package es.josefons.reactown;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Listado extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseDatabase database;
    private TextView tvInformacionUsuarioListado;
    private Usuario usuario;

    RecyclerView recyclerView;
    ItemListadoAdapter itemListadoAdapter;
    List<ItemListado> listadoList;

    public Listado() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AlertDialog alertbox = new AlertDialog.Builder(getView().getContext())
                        .setMessage("¿Quieres salir de la cuenta?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                mAuth.signOut();
                                Navigation.findNavController(getView()).navigate(R.id.logout);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                //
                            }
                        })
                        .show();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_listado, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        tvInformacionUsuarioListado = view.findViewById(R.id.tvInformacionUsuarioListado);
        getUserInfo();

        /* Recycler */
        listadoList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);
        itemListadoAdapter = new ItemListadoAdapter(listadoList, getContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(itemListadoAdapter);

        mDatabase.child("itemListado").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listadoList.removeAll(listadoList);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ItemListado aux = new ItemListado();
                    aux.setId(snapshot.getKey());
                    aux.setIcon(snapshot.child("propuestaImagen").getValue().toString());
                    aux.setName(snapshot.child("propuestaNombre").getValue().toString());
                    listadoList.add(aux);
                }
                itemListadoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        itemListadoAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle datos = new Bundle();
                String aux = listadoList.get(recyclerView.getChildAdapterPosition(v)).getId();
                datos.putString("id", aux);
                Navigation.findNavController(getView()).navigate(R.id.infoRecycler, datos);
            }
        });

        /* Recycler */
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.menu_listado, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuLogOut: mAuth.signOut();  Navigation.findNavController(getView()).navigate(R.id.logout); break;
            case R.id.menuAddList:  Navigation.findNavController(getView()).navigate(R.id.anyadirItem);; break;
            case R.id.menuPanelUsuario: Navigation.findNavController(getView()).navigate(R.id.irPanelUsuario); break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getUserInfo(){
        String UserId = mAuth.getCurrentUser().getUid();
        usuario = new Usuario(UserId, "None", "None", 0);
        usuario.setId(UserId);
        mDatabase.child("Users").child(UserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if (dataSnapshot.exists()) {
                     String name = dataSnapshot.child("name").getValue().toString();
                     String correo = dataSnapshot.child("correo").getValue().toString();
                     int perm = Integer.parseInt(dataSnapshot.child("perm").getValue().toString());

                     tvInformacionUsuarioListado.setText(name);
                     usuario.setName(name);
                     usuario.setCorreo(correo);
                     usuario.setPermiso(perm);
                 }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
