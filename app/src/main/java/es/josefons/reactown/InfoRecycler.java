package es.josefons.reactown;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


public class InfoRecycler extends Fragment {
    private String ID_TRAIDO = "";
    private int PERM_TRAIDO = 0;
    private String nombreImagen = "";
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
            PERM_TRAIDO = bundle.getInt("perm", 0);
        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                //
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        setHasOptionsMenu(true);
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
                titulo.setText("");
                autor.setText("");
                desc.setText("");
                imgTotal.setImageURI(null);
                Navigation.findNavController(getView()).navigate(R.id.volverMainRecycler);
            }
        });
    }

    /**
     * Carga la informacion del apartado mediante el ID que se trae del main. Este comprueba que si existe
     * y si por lo contrario no existe, te devuelve al main.
     */
    private void cargarInfo(){
        DatabaseReference ref = database.getReference("itemListado/" + ID_TRAIDO);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    titulo.setText(dataSnapshot.child("propuestaNombre").getValue().toString());
                    autor.setText(dataSnapshot.child("propuestaUsuario").getValue().toString());
                    desc.setText(dataSnapshot.child("propuestaDescripcion").getValue().toString());
                    Picasso.get()
                            .load(dataSnapshot.child("propuestaImagen").getValue().toString())
                            .placeholder(R.drawable.placeholder_loading)
                            .centerCrop()
                            .fit()
                            .into(imgTotal);
                    nombreImagen = dataSnapshot.child("propuestaImagen").getValue().toString();
                } else {
                    Toast.makeText(getContext(), "Error al cargar la solicitud", Toast.LENGTH_SHORT).show();
                    //Navigation.findNavController(getView()).navigate(R.id.volverMainRecycler);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if(PERM_TRAIDO == 1){
            inflater.inflate(R.menu.admin_menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.mDeleteAdmin){
            borrarPost();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * El borrado de post solop tendran acceso los usuarios que tengan el rol de administrador dados
     * desde el control de usuario en la web de firebase.
     */
    private void borrarPost(){
        DatabaseReference ref = database.getReference().child("itemListado").child(ID_TRAIDO);
        ref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                borrarImagen();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println(e.getMessage());
            }
        });
        Navigation.findNavController(getView()).navigate(R.id.volverMainRecycler);
    }

    /**
     * Funcion unicamente dedicada al borrado de la imagen cuando un administrador borra el post.
     * Busca la ID de esta y la borra del almacenamiento
     */
    private void borrarImagen(){
        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(nombreImagen);
        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("Imagen borrada");
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Error al borrar.");
            }
        });
    }
}
