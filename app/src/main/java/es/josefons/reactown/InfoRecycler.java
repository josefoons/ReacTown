package es.josefons.reactown;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class InfoRecycler extends Fragment {
    private String ID_TRAIDO = "";
    private ImageView imgTotal;
    private TextView titulo, autor, desc;
    private Button volver;
    private DatabaseReference mDatabase;
    FirebaseDatabase database;

    public InfoRecycler() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            ID_TRAIDO = bundle.getString("id", "");
        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                //
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info_recycler, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        database = FirebaseDatabase.getInstance();

        imgTotal = view.findViewById(R.id.infoRecyImagen);
        titulo = view.findViewById(R.id.infoRecyNombre);
        autor = view.findViewById(R.id.InfoRecyAutor);
        desc = view.findViewById(R.id.InfoRecyInformacion);
        volver = view.findViewById(R.id.btnInfoRecyVolver);

        cargarInfo();

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getView()).navigate(R.id.volverMainRecycler);
            }
        });
    }

    private void cargarInfo(){
        DatabaseReference ref = database.getReference("itemListado/" + ID_TRAIDO);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                titulo.setText(dataSnapshot.child("propuestaNombre").getValue().toString());
                autor.setText(dataSnapshot.child("propuestaUsuario").getValue().toString());
                desc.setText(dataSnapshot.child("propuestaDescripcion").getValue().toString());
                Picasso.get()
                        .load(dataSnapshot.child("propuestaImagen").getValue().toString())
                        .placeholder(R.drawable.placeholder_loading)
                        .centerCrop()
                        .fit()
                        .into(imgTotal);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }


}
