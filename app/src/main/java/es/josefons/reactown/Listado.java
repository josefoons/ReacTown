package es.josefons.reactown;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class Listado extends Fragment implements ItemClickListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    int[] iconList = {R.drawable.common_full_open_on_phone, R.drawable.common_google_signin_btn_icon_dark_focused};
    String[] nameList = {"nombre1", "nombnre2"};
    String[] autorList = {"autor1", "autor2"};
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
                //Toast.makeText(getView().getContext(),"OnBackpress Click", Toast.LENGTH_LONG).show();
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
        mAuth = FirebaseAuth.getInstance();
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
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_listado, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.menuLogOut: System.out.println("Menu 1"); break;
            case R.id.menuAddList: System.out.println("Menu 2"); break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view, int position) {
        String id = listadoList.get(position).getId();
        String name = listadoList.get(position).getName();

        Toast.makeText(view.getContext(), name, Toast.LENGTH_SHORT).show();
    }
}
