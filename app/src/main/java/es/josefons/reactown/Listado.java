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

public class Listado extends Fragment implements ItemClickListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView tvInformacionUsuarioListado;
    private Usuario usuario;

    /* AUXILIAR */
    int[] iconList = {R.drawable.common_full_open_on_phone, R.drawable.common_google_signin_btn_icon_dark_focused};
    String[] nameList = {"nombre1", "nombnre2"};
    String[] autorList = {"autor1", "autor2"};
     /* --------------- */
    RecyclerView recyclerView;
    ItemListadoAdapter itemListadoAdapter;
    List<ItemListado> listadoList = new ArrayList<>();

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
        recyclerView = view.findViewById(R.id.recyclerView);
        itemListadoAdapter = new ItemListadoAdapter(listadoList, getContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(itemListadoAdapter);
        itemListadoAdapter.setItemClickListener(this);
        itemListadoAdapter.notifyDataSetChanged();

        for (int i = 0; i < iconList.length; i++) {
            String name = nameList[i];
            String autor = autorList[i];
            int icon = iconList[i];

            ItemListado itemaux = new ItemListado(Integer.toString(i), icon, name, autor);
            listadoList.add(itemaux);
        }
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view, int position) {
        String id = listadoList.get(position).getId();
        String name = listadoList.get(position).getName();

        Toast.makeText(view.getContext(), name, Toast.LENGTH_SHORT).show();
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
